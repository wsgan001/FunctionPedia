package cn.edu.pku.sei.tsr.dragon.outdated;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;

import cn.edu.pku.sei.tsr.dragon.content.ContentParser;
import cn.edu.pku.sei.tsr.dragon.content.SODataParser;
import cn.edu.pku.sei.tsr.dragon.content.SentenceParser;
import cn.edu.pku.sei.tsr.dragon.utils.database.DBConnPool;
import cn.edu.pku.sei.tsr.dragon.outdated.QuestionDao;
import cn.edu.pku.sei.tsr.dragon.entity.contentdata.PhraseInfo;
import cn.edu.pku.sei.tsr.dragon.entity.contentdata.SentenceInfo;
import cn.edu.pku.sei.tsr.dragon.entity.rawdata.Question;
import cn.edu.pku.sei.tsr.dragon.entity.sodata.ThreadInfo;
import cn.edu.pku.sei.tsr.dragon.main.APILibrary;
import cn.edu.pku.sei.tsr.dragon.nlp.parser.PhraseFilter;
import cn.edu.pku.sei.tsr.dragon.nlp.parser.PhraseExtractor;
import cn.edu.pku.sei.tsr.dragon.nlp.parser.Proof;
import cn.edu.pku.sei.tsr.dragon.nlp.TrunkExtractor;
import cn.edu.pku.sei.tsr.dragon.utils.Config;
import cn.edu.pku.sei.tsr.dragon.utils.ObjectIO;

public class MainOnePass {
	public static final Logger	logger	= Logger.getLogger(MainOnePass.class);

	public static void main(String[] args) {
		QuestionDao questionDao = new QuestionDao(DBConnPool.getConnection());
		// 获取数据库中的所有问题，以此为入手点创建thread
		List<Question> rawQuestionList = questionDao.getAllQuestions();

		if (rawQuestionList == null)
			return;
		long t_start = System.currentTimeMillis();
		int count = 0;

		for (int i = 0; i < rawQuestionList.size(); i++) {
			long t0 = System.currentTimeMillis();

			Question rawQuestion = rawQuestionList.get(i);
			String projectName = null;

			if (rawQuestion.getTags().contains(APILibrary.POI))
				projectName = APILibrary.POI;
			else if (rawQuestion.getTags().contains(APILibrary.NEO4J))
				projectName = APILibrary.NEO4J;
			else if (rawQuestion.getTags().contains(APILibrary.LUCENE))
				projectName = APILibrary.LUCENE;
			else if (rawQuestion.getTags().contains(APILibrary.SWING))
				projectName = APILibrary.SWING;
			else
				continue;

			if (!projectName.equals(APILibrary.POI))
				continue;

			String filePath = projectName + File.separator + "[thread]" + rawQuestion.getId()
					+ ObjectIO.DAT_FILE_EXTENSION;

			// File dataobjdir = new File(Config.getDataIODir());
			// String[] fileNames = dataobjdir.list();
			// if (Arrays.asList(fileNames).contains("[thread]" +
			// rawQuestion.getId())) {
			// long t1 = System.currentTimeMillis();
			// // System.out.println(rawQuestion.getId() + "  " + (t1 - t0) +
			// // "ms");
			// continue;
			// }

			long t1 = System.currentTimeMillis();

			ThreadInfo thread = SODataParser.parseThread(rawQuestion);
			ObjectIO.writeObject(thread, ObjectIO.RAW_SODATA + File.separator + filePath);

			long t2 = System.currentTimeMillis();

			ThreadContentParser.parseHTMLContentInPostsOfThread(thread);
			long t3 = System.currentTimeMillis();

			SentenceParser.separateParagraphToSentencesInThread(thread);
			long t4 = System.currentTimeMillis();

			List<SentenceInfo> sentences = thread.getSentences();
			for (SentenceInfo sentenceInfo : sentences) {
				SentenceParser.parseGrammaticalTree(sentenceInfo);
			}

			ObjectIO.writeObject(thread, ObjectIO.AFTER_TREE_PARSE + File.separator + filePath);

			// System.out.println("=== [Thread] " + thread.getTitle() + " ===");

			// List<SentenceInfo> sentences = thread.getSentences();
			for (SentenceInfo sentenceInfo : sentences) {
				// System.out.println("=== [Sentence] ===");
				// System.out.println(sentenceInfo.getContent());

				PhraseExtractor.extractVerbPhrases(sentenceInfo);
				PhraseExtractor.extractNounPhrases(sentenceInfo);
				boolean goodSentence = false;

				for (PhraseInfo phrase : sentenceInfo.getPhrases()) {
					PhraseFilter.filter(phrase, sentenceInfo);

					if (phrase.getProofTotalScore() > Proof.MID) {
						// phrase.getTree().pennPrint();

						new TrunkExtractor(phrase).extract();
						// System.out.println(phrase.getProofString());

						goodSentence = true;
					}
				}
				if (goodSentence) {
					// System.out.println("===========sentence start===============");
					// System.out.println(sentenceInfo.getContent());
					// System.out.println("==========sentence finish===============");
				}
			}
			long t5 = System.currentTimeMillis();
			ObjectIO.writeObject(thread, ObjectIO.AFTER_EXTRACTION + File.separator + filePath);
			long t6 = System.currentTimeMillis();

			long t_end = System.currentTimeMillis();
			count++;
			long avrg_time = (t_end - t_start) / count;
			logger.info("[AVGT: " + avrg_time + "ms] [" + count + "] [i=" + i + "] [id:" + thread.getQuestion().getId()
					+ "] [time: " + (t_end - t0) + "ms] " + thread.getTitle());
			logger.info("[parseThread: " + (t2 - t1) + "ms] [parseHtml: " + (t3 - t2) + "ms] [parseParagraph: "
					+ (t4 - t3) + "ms] [extractPhrases: " + (t5 - t4) + "ms] [outputOBJ: " + (t6 - t5) + "ms]");

		}
	}
}
