package cn.edu.pku.sei.tsr.dragon.nlp.parser;

import java.util.List;

import org.apache.log4j.Logger;

import cn.edu.pku.sei.tsr.dragon.nlp.entity.PhraseInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.entity.VerbalPhraseStructureInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.parser.PhraseExtractor;
import cn.edu.pku.sei.tsr.dragon.nlp.parser.PhraseFilter;
import cn.edu.pku.sei.tsr.dragon.nlp.parser.Proof;

public class SyntaxParser {
	public static final Logger logger = Logger.getLogger(SyntaxParser.class);

	public static PhraseInfo[] extractPhrases(String text) {

		List<ParagraphInfo> paraList = content.getParagraphList();
		if (paraList != null && paraList.size() > 0) {
			for (int i = 0; i < paraList.size(); i++) {
				ParagraphInfo paragraph = paraList.get(i);
				List<SentenceInfo> sentences = paragraph.getSentences();
				if (sentences == null)
					continue;
				for (SentenceInfo sentence : sentences) {
					ContentParser.replaceCodeLikeTerms(sentence);

					long t4 = System.currentTimeMillis();
					SentenceParser.parseGrammaticalTree(sentence);
					long t5 = System.currentTimeMillis();

					// 提取原始的短语（vp或np）
					PhraseExtractor.extractVerbPhrases(sentence);
					// PhraseExtractor.extractNounPhrases(sentence);
					long t6 = System.currentTimeMillis();

					for (int j = 0; j < sentence.getPhrases().size(); j++) {
						long t7 = System.currentTimeMillis();
						PhraseInfo phrase = sentence.getPhrases().get(j);

						// 对每个短语进行过滤，添加proof（evidence）
						PhraseFilter.filter(phrase, sentence);
						// phrase.getStemmedTree().pennPrint();
						// System.out.println(phrase.getProofString());

						// 删掉不靠谱的短语
						if (phrase.getProofTotalScore() < Proof.NEGA_5) {
							sentence.getPhrases().remove(phrase);
							j--;
							phrase = null;
							continue;
						}

						if (phrase instanceof VerbalPhraseInfo) {
							VerbalPhraseInfo verbalPhrase = (VerbalPhraseInfo) phrase;
							VerbalPhraseStructureInfo vpStructure = new VerbalPhraseStructureInfo(
									verbalPhrase);
							verbalPhrase.setStructure(vpStructure);
							// 通过过滤，总得分超过阈值，是候选短语
							if (phrase.getProofTotalScore() > Proof.MID) {
								//System.out.println("--------------");
								//System.out.println(vpStructure);
								System.out.println("VP\t"+verbalPhrase.getContent().replaceAll(" ", "\t"));

								PhraseExtractor.extractNounPhrasesFromVP(phrase);
							}
						}
						long t8 = System.currentTimeMillis();
					}
					long t9 = System.currentTimeMillis();
					// logger.info("\t[parse tree:" + (t5 - t4) + "ms][extract
					// phrase:" + (t6 - t5)
					// + "ms][filter:" + (t9 - t6) + "ms]");

				}
			}
		}
		long t_end = System.currentTimeMillis();
		// logger.info("[parse html: " + (t1 - t0) + "ms][others: " + (t2 - t1)
		// + "ms]");
		return content;
	}

	public static SentenceInfo extractPhrases(SentenceInfo sentence) {

		ContentParser.replaceCodeLikeTerms(sentence);

		long t4 = System.currentTimeMillis();
		SentenceParser.parseGrammaticalTree(sentence);
		long t5 = System.currentTimeMillis();

		// 提取原始的短语（vp或np）
		PhraseExtractor.extractVerbPhrases(sentence);
		// PhraseExtractor.extractNounPhrases(sentence);
		long t6 = System.currentTimeMillis();

		for (int j = 0; j < sentence.getPhrases().size(); j++) {
			long t7 = System.currentTimeMillis();
			PhraseInfo phrase = sentence.getPhrases().get(j);

			// 对每个短语进行过滤，添加proof（evidence）
			PhraseFilter.filter(phrase, sentence);
			// phrase.getStemmedTree().pennPrint();
			// System.out.println(phrase.getProofString());

			// 删掉不靠谱的短语
			if (phrase.getProofTotalScore() < Proof.MID) {
				sentence.getPhrases().remove(phrase);
				j--;
				phrase = null;
				continue;
			}

			if (phrase instanceof VerbalPhraseInfo) {
				VerbalPhraseInfo verbalPhrase = (VerbalPhraseInfo) phrase;
				VerbalPhraseStructureInfo vpStructure = new VerbalPhraseStructureInfo(verbalPhrase);
				verbalPhrase.setStructure(vpStructure);
				// 通过过滤，总得分超过阈值，是候选短语
				if (phrase.getProofTotalScore() > Proof.MID) {
					// logger.warn(vpStructure);
					// logger.warn(verbalPhrase.getContent());
				}
			}
			long t8 = System.currentTimeMillis();
		}
		long t9 = System.currentTimeMillis();
		// logger.info("\t[parse tree:" + (t5 - t4) + "ms][extract
		// phrase:" + (t6 - t5)
		// + "ms][filter:" + (t9 - t6) + "ms]");

		long t_end = System.currentTimeMillis();
		// logger.info("[parse html: " + (t1 - t0) + "ms][others: " + (t2 - t1)
		// + "ms]");
		return sentence;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
