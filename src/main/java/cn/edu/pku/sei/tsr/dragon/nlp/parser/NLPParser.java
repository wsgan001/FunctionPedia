package cn.edu.pku.sei.tsr.dragon.nlp.parser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;

import javax.print.Doc;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;

import cn.edu.pku.sei.tsr.dragon.document.dao.SentenceDAO;
import cn.edu.pku.sei.tsr.dragon.document.entity.CodeTermInfo;
import cn.edu.pku.sei.tsr.dragon.document.entity.SentenceInfo;
import cn.edu.pku.sei.tsr.dragon.document.parser.DocumentParser;
import cn.edu.pku.sei.tsr.dragon.nlp.dao.PhraseDAO;
import cn.edu.pku.sei.tsr.dragon.nlp.entity.PhraseInfo;
import cn.edu.pku.sei.tsr.dragon.utils.Config;
import cn.edu.pku.sei.tsr.dragon.utils.database.DBConnPool;
import cn.edu.pku.sei.tsr.dragon.utils.nlp.StanfordParser;
import edu.stanford.nlp.trees.Tree;

public class NLPParser {
	public static final Logger	logger				= Logger.getLogger(NLPParser.class);
	public static final String	NLP_PARSER_LOG_FILE	= "nlp_parser_data_log.txt";
	public static Connection	conn;

	public static Connection initializeConnection() {
		if (conn == null)
			conn = DBConnPool.getConnection();
		return conn;
	}

	public static void finalizeConnection() {
		if (conn != null)
			DBConnPool.closeConnection(conn);
	}

	public static void main(String[] args) {
		NLPParser.initializeConnection();
		parseAllSentences();
		NLPParser.finalizeConnection();
	}

	public static void parseAllSentences() {
		SentenceDAO sentenceDAO = new SentenceDAO(conn);
		List<SentenceInfo> sentences = sentenceDAO.getAll();
		logger.info("get all sentences: " + sentences.size());

		long t0 = System.currentTimeMillis();
		for (int i = 0; i < sentences.size(); i++) {
			parseSentence(sentences.get(i));

			if (i % 250 == 249) {
				long t1 = System.currentTimeMillis();
				// logger.info("[SentenceCount]" + i + "\t[avrg_time]" + (t1 -
				// t0) / (double) (i + 1) + "ms.");
			}
		}
	}

	public static void parseSentence(SentenceInfo sentence) {
		initializeConnection();
		parseSentence(sentence, NLPParser.conn);
	}

	public static void parseSentence(SentenceInfo sentence, Connection conn) {
		long t1, t2, t3, t4;
		String sentenceText = sentence.getText();
		// logger.info(sentenceText);

		if (StringUtils.isBlank(sentenceText))
			return;

		Pair<String, CodeTermInfo[]> textCodeMasks = DocumentParser.maskCodeTerms(sentenceText);
		String maskedText = textCodeMasks.getLeft();
		CodeTermInfo[] maskedCodeTerms = textCodeMasks.getRight();
		String codeTermString = DocumentParser.concatenateCodeTerms(maskedCodeTerms);
		// logger.info(maskedText);
		t1 = System.currentTimeMillis();
		Tree tree = parseGrammaticalTree(maskedText);
		if (tree == null)
			return; // Fail to build a syntax tree.

		String treeText = tree.pennString().trim();
		// logger.info(tree);
		t2 = System.currentTimeMillis();

		SentenceDAO sentenceDAO = new SentenceDAO(conn);
		if (StringUtils.isBlank(codeTermString))
			sentenceDAO.updateTreeById(sentence.getId(), treeText);
		else
			sentenceDAO.updateTreeAndCodeTermsById(sentence.getId(), treeText, codeTermString);

		String sentenceSourcePath = DocumentParser.getSentenceSourcePath(sentence.getId());
		t3 = System.currentTimeMillis();

		PhraseInfo[] phrases = PhraseExtractor.extractVerbPhrases(tree);
		t4 = System.currentTimeMillis();

		// logger.info(sentence.getId() + "-sentence: [nlp parse tree]" + (t2 -
		// t1) + "ms [db query]" + (t3 - t2)
		// + "ms [extract phrase]" + (t4 - t3) + "ms");
		for (int j = 0; j < phrases.length; j++) {
			PhraseInfo phrase = phrases[j];
			phrase.setParentId(sentence.getId());
			phrase.setSourcePath(sentenceSourcePath);

			t1 = System.currentTimeMillis();
			// 对每个短语进行过滤，添加proof（evidence）
			PhraseFilter.filter(phrase, maskedText);
			// phrase.getStemmedTree().pennPrint();
			// logger.info(phrase.getProofString());

			t2 = System.currentTimeMillis();
			PhraseDAO phraseDAO = new PhraseDAO(conn);
			phrase = phraseDAO.addPhrase(phrase); // set id
			t3 = System.currentTimeMillis();
			// logger.info("phrase [filter]" + (t2 - t1) + "ms [db insert] " +
			// (t3 - t2) + "ms.");

			// logger.info("*****[phrase]*****");
			// logger.info(phrase.getId());
			// logger.info(phrase.getText());
			// logger.info(phrase.getSyntaxTree());
			// logger.info(phrase.printProofs());
		}
	}

	// public static void parseSentenceText(String text) {
	// if (StringUtils.isBlank(text))
	// return;
	//
	// Pair<String, CodeTermInfo[]> textCodeMasks =
	// DocumentParser.maskCodeTerms(text);
	// String maskedText = textCodeMasks.getLeft();
	// CodeTermInfo[] maskedCodeTerms = textCodeMasks.getRight();
	// logger.info(maskedText);
	//
	// Tree tree = parseGrammaticalTree(maskedText);
	// logger.info(tree);
	//
	// PhraseInfo[] phrases = PhraseExtractor.extractVerbPhrases(tree);
	// for (int i = 0; i < phrases.length; i++) {
	// PhraseInfo phrase = phrases[i];
	//
	// // 对每个短语进行过滤，添加proof（evidence）
	// PhraseFilter.filter(phrase, maskedText);
	// // phrase.getStemmedTree().pennPrint();
	// // logger.info(phrase.getProofString());
	//
	// logger.info("*****[phrase]*****");
	// logger.info(phrase.getText());
	// logger.info(phrase.getSyntaxTree());
	// logger.info(phrase.getProofString());
	//
	// }
	//
	// }

	public static Tree parseGrammaticalTree(String sentence) {
		if (DocumentParser.hasTooManyIllegalSymbols(sentence))
			return null;

		// Add a period to the end of sentence, if there is none.
		int i;
		for (i = sentence.length() - 1; i >= 0; i--) {
			char ch = sentence.charAt(i);
			if (Character.isLetter(ch) || Character.isDigit(ch))
				break;
		}
		sentence = sentence.substring(0, i + 1) + ".";

		// logger.info(sentence);
		// long t1 = System.currentTimeMillis();
		Tree tree = StanfordParser.parseTree(sentence);
		// long t2 = System.currentTimeMillis();
		// System.err.println("parseTree "+(t2 - t1) + "ms");

		return tree;
	}

	// public static ContentInfo extractAndFilterPhrases(String sentence) {
	//
	// List<ParagraphInfo> paraList = content.getParagraphList();
	// if (paraList != null && paraList.size() > 0) {
	// for (int i = 0; i < paraList.size(); i++) {
	// ParagraphInfo paragraph = paraList.get(i);
	// List<SentenceInfo> sentences = paragraph.getSentences();
	// if (sentences == null)
	// continue;
	// for (SentenceInfo sentence : sentences) {
	// ContentParser.replaceCodeLikeTerms(sentence);
	//
	// long t4 = System.currentTimeMillis();
	// SentenceParser.parseGrammaticalTree(sentence);
	// long t5 = System.currentTimeMillis();
	//
	// // 提取原始的短语（vp或np）
	// PhraseExtractor.extractVerbPhrases(sentence);
	// // PhraseExtractor.extractNounPhrases(sentence);
	// long t6 = System.currentTimeMillis();
	//
	// for (int j = 0; j < sentence.getPhrases().size(); j++) {
	// long t7 = System.currentTimeMillis();
	// PhraseInfo phrase = sentence.getPhrases().get(j);
	//
	// // 对每个短语进行过滤，添加proof（evidence）
	// PhraseFilter.filter(phrase, sentence);
	// // phrase.getStemmedTree().pennPrint();
	// // logger.info(phrase.getProofString());
	//
	// // 删掉不靠谱的短语
	// if (phrase.getProofTotalScore() < Proof.NEGA_5) {
	// sentence.getPhrases().remove(phrase);
	// j--;
	// phrase = null;
	// continue;
	// }
	//
	// if (phrase instanceof VerbalPhraseInfo) {
	// VerbalPhraseInfo verbalPhrase = (VerbalPhraseInfo) phrase;
	// VerbalPhraseStructureInfo vpStructure = new VerbalPhraseStructureInfo(
	// verbalPhrase);
	// verbalPhrase.setStructure(vpStructure);
	// // 通过过滤，总得分超过阈值，是候选短语
	// if (phrase.getProofTotalScore() > Proof.MID) {
	// // logger.info("--------------");
	// // logger.info(vpStructure);
	// logger.info("VP\t" + verbalPhrase.getContent().replaceAll(" ",
	// "\t"));
	//
	// PhraseExtractor.extractNounPhrasesFromVP(phrase);
	// }
	// }
	// long t8 = System.currentTimeMillis();
	// }
	// long t9 = System.currentTimeMillis();
	// // logger.info("\t[parse tree:" + (t5 - t4) + "ms][extract
	// // phrase:" + (t6 - t5)
	// // + "ms][filter:" + (t9 - t6) + "ms]");
	//
	// }
	// }
	// }
	// long t_end = System.currentTimeMillis();
	// // logger.info("[parse html: " + (t1 - t0) + "ms][others: " + (t2 - t1)
	// // + "ms]");
	// return content;
	// }

}
