package cn.edu.pku.sei.tsr.dragon.outdated;

import java.util.List;

import org.apache.log4j.Logger;

import cn.edu.pku.sei.tsr.dragon.content.ThreadContentParser;
import cn.edu.pku.sei.tsr.dragon.content.PreProcessor;
import cn.edu.pku.sei.tsr.dragon.content.SODataParser;
import cn.edu.pku.sei.tsr.dragon.content.SentenceParser;
import cn.edu.pku.sei.tsr.dragon.utils.database.DBConnPool;
import cn.edu.pku.sei.tsr.dragon.outdated.QuestionDao;
import cn.edu.pku.sei.tsr.dragon.entity.contentdata.PhraseInfo;
import cn.edu.pku.sei.tsr.dragon.entity.contentdata.SentenceInfo;
import cn.edu.pku.sei.tsr.dragon.entity.rawdata.Question;
import cn.edu.pku.sei.tsr.dragon.entity.sodata.ThreadInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.parser.PhraseFilter;
import cn.edu.pku.sei.tsr.dragon.nlp.parser.PhraseExtractor;
import cn.edu.pku.sei.tsr.dragon.nlp.parser.Proof;

public class MainTest {
	public static final Logger	logger	= Logger.getLogger(MainTest.class);

	public static void main(String[] args) {

		try {
			QuestionDao questionDao = new QuestionDao(DBConnPool.getConnection());

			for (int i = 0; i < 3; i++) {
				long t0 = System.currentTimeMillis();
				Question rawQuestion = questionDao.getQuestionById(i == 0 ? 387 : (i == 1 ? 1437721 : 1254759));
				long t1 = System.currentTimeMillis();
				System.out.println(t1 - t0 + "ms  questionDao.getQuestionById");

				ThreadInfo thread = SODataParser.parseThread(rawQuestion);
				long t2 = System.currentTimeMillis();
				System.out.println(t2 - t1 + "ms  SODataParser.parseThread");

				ThreadContentParser.parseHTMLContentInPostsOfThread(thread);

				long t3 = System.currentTimeMillis();
				System.out.println(t3 - t2 + "ms  parseHTML");

				SentenceParser.separateParagraphToSentencesInThread(thread);

				long t4 = System.currentTimeMillis();
				System.out.println(t4 - t3 + "ms  separateSentence");
				logger.info(thread.getTitle());

				List<SentenceInfo> sentences = thread.getSentences();
				long t40 = System.currentTimeMillis();
				System.out.println(t40 - t4 + "ms  get thread sentences");
				for (SentenceInfo sentenceInfo : sentences) {
					// System.out.println("=== [Sentence] ===");
					// System.out.println(sentenceInfo.getContent());
					t1 = System.currentTimeMillis();

					PhraseExtractor.extractVerbPhrases(sentenceInfo);
					PhraseExtractor.extractNounPhrases(sentenceInfo);

					for (PhraseInfo phraseInfo : sentenceInfo.getPhrases()) {
						PhraseFilter.filter(phraseInfo, sentenceInfo);
						if (phraseInfo.getProofTotalScore() > Proof.MID) {
							// System.out
							// .println("[Phrase] " + phraseInfo.getPhrase());
							// System.out.println(phraseInfo.printProofToString());
							// phraseInfo.getTree().pennPrint();
						}
					}
					t2 = System.currentTimeMillis();
					// logger.info("[Main] sentence finished, time: " + (t2 -
					// t1) +
					// "ms");
				}

				long t5 = System.currentTimeMillis();
				System.out.println(t5 - t4 + "ms  extract phrase");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			DBConnPool.shutdownPool();
		}

	}

}
