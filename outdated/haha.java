package cn.edu.pku.sei.tsr.dragon.outdated;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import cn.edu.pku.sei.tsr.dragon.utils.database.DBConnPool;
import cn.edu.pku.sei.tsr.dragon.outdated.QuestionDao;
import cn.edu.pku.sei.tsr.dragon.entity.contentdata.PhraseInfo;
import cn.edu.pku.sei.tsr.dragon.entity.contentdata.SentenceInfo;
import cn.edu.pku.sei.tsr.dragon.entity.contentdata.ParagraphInfo;
import cn.edu.pku.sei.tsr.dragon.entity.rawdata.Question;
import cn.edu.pku.sei.tsr.dragon.entity.sodata.CommentInfo;
import cn.edu.pku.sei.tsr.dragon.entity.sodata.PostInfo;
import cn.edu.pku.sei.tsr.dragon.entity.sodata.ThreadInfo;
import cn.edu.pku.sei.tsr.dragon.utils.ObjectIO;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreePrint;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import edu.stanford.nlp.trees.tregex.tsurgeon.Tsurgeon;
import edu.stanford.nlp.trees.tregex.tsurgeon.TsurgeonPattern;

public class haha {
	static List<String>	phrases	= new ArrayList<String>();

	public static void main(String[] args) throws FileNotFoundException {
		QuestionDao questionDao = new QuestionDao(DBConnPool.getConnection());
		List<Question> rawQuestionList = questionDao.getAllQuestions();
		int poi = 40;
		int luc = 30;
		int neo = 20;
		if (rawQuestionList != null) {
			for (Question rawQuestion : rawQuestionList) {
				if (poi < 0 && luc < 0 && neo < 0)
					break;
				phrases.clear();
				PrintWriter fos;
				ThreadInfo newThread;
				if (rawQuestion.getTags().contains("poi")) {
					poi--;
					if (poi < 0)
						continue;
					fos = new PrintWriter((new FileOutputStream(new File("testdata" + File.separatorChar + "poi" + poi
							+ ".txt"))));

				}
				else if (rawQuestion.getTags().contains("lucene")) {
					luc--;
					if (luc < 0)
						continue;
					fos = new PrintWriter((new FileOutputStream(new File("testdata" + File.separatorChar + "lucene"
							+ luc + ".txt"))));
				}
				else if (rawQuestion.getTags().contains("neo4j")) {
					neo--;
					if (neo < 0)
						continue;
					fos = new PrintWriter((new FileOutputStream(new File("testdata" + File.separatorChar + "neo4j"
							+ neo + ".txt"))));
				}
				else
					continue;
				newThread = SODataParser.parseThread(rawQuestion);
				new ThreadContentParser().parseHTMLContentInPostsOfThread(newThread);
				new SentenceParser().separateParagraphToSentencesInThread(newThread);
				fos.println("QUESTION:");
				display(newThread.getQuestion(), fos);
				fos.println();
				fos.println("ACCEPTED ANS:");
				display(newThread.getAcceptedAnswer(), fos);
				fos.println();
				fos.println("ANS:");
				for (PostInfo postInfo : newThread.getAnswerList()) {
					display(postInfo, fos);
				}
				fos.println();
				fos.println("Phrase:");
				for (String s : phrases) {
					fos.println(s);
				}
				fos.close();
			}
		}

	}

	public static void display(PostInfo postInfo, PrintWriter out) {
		if (postInfo == null)
			return;
		out.println("HTML:");
		out.println(postInfo.getHtmlContent());
		out.println("Comment:");
		for (ParagraphInfo para : postInfo.getContent().getParagraphs()) {
			if (para instanceof ParagraphInfo) {
				ParagraphInfo tpara = (ParagraphInfo) (para);
				for (SentenceInfo sent : tpara.getSentences()) {
					for (PhraseInfo phrase : sent.getPhrases()) {
						if (phrase.isVP())
							phrases.add(phrase.getContent());
					}
				}
			}
		}
		for (CommentInfo commentInfo : postInfo.getComments()) {
			out.println(commentInfo.getContent());
			ParagraphInfo tpara = commentInfo.getContent();
			for (SentenceInfo sent : tpara.getSentences()) {
				for (PhraseInfo phrase : sent.getPhrases()) {
					if (phrase.isVP())
						phrases.add(phrase.getContent());
				}
			}
		}

	}

	public static void demoAPI(LexicalizedParser lp) throws FileNotFoundException {
		Scanner sc = new Scanner(new File("testdata\\a.txt"));
		String sent = "";
		while (sc.hasNext()) {
			sent = sent + sc.nextLine();
		}
		TokenizerFactory<CoreLabel> tokenizerFactory = PTBTokenizer.factory(new CoreLabelTokenFactory(), "");
		List<CoreLabel> rawWords = tokenizerFactory.getTokenizer(new StringReader(sent)).tokenize();
		Tree parse = lp.apply(rawWords);
		parse.pennPrint();
		System.out.println(parse.label());
		List<Tree> cs = parse.postOrderNodeList();
		for (int i = 0; i < cs.size(); i++) {
			if (cs.get(i).label().toString().equalsIgnoreCase("VP")) {
				List<Tree> c = cs.get(i).getLeaves();
				for (int j = 0; j < c.size(); j++)
					System.out.print("" + c.get(j) + " ");
				System.out.println();
			}
		}

	}
}
