package cn.edu.pku.sei.tsr.dragon.task.parser;

import java.sql.Connection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;

import cn.edu.pku.sei.tsr.dragon.nlp.dao.PhraseDAO;
import cn.edu.pku.sei.tsr.dragon.nlp.entity.PhraseInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.entity.VerbalPhraseStructureInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.parser.NLPParser;
import cn.edu.pku.sei.tsr.dragon.nlp.parser.PhraseExtractor;
import cn.edu.pku.sei.tsr.dragon.nlp.parser.PhraseFilter;
import cn.edu.pku.sei.tsr.dragon.task.dao.TaskDAO;
import cn.edu.pku.sei.tsr.dragon.task.entity.TaskInfo;
import cn.edu.pku.sei.tsr.dragon.utils.database.DBConnPool;
import cn.edu.pku.sei.tsr.dragon.utils.nlp.Stemmer;
import edu.stanford.nlp.trees.Tree;

public class TaskParser {
	public static final Logger				logger					= Logger.getLogger(TaskParser.class);
	public static final String				TASK_PARSER_LOG_FILE	= "task_parser_data_log.txt";
	public static final int					PHRASE_SCORE_THRESHOLD	= -5;

	public static Connection				conn;
	public static HashMap<String, TaskInfo>	taskMap					= new HashMap<>();

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
		initializeConnection();
		extractTasksFromAllPhrases();
		finalizeConnection();
	}

	public static void extractTasksFromAllPhrases() {
		PhraseDAO phraseDAO = new PhraseDAO(conn);
		TaskDAO taskDAO = new TaskDAO(conn);
		List<PhraseInfo> phrases = phraseDAO.getPhrasesByScoreThreshold(PHRASE_SCORE_THRESHOLD);

		logger.info("Retrieved " + phrases.size() + " valid phrases.");
		long t0 = System.currentTimeMillis(), t1;
		int phraseCount = phrases.size();

		logger.info("start to mine tasks...");
		for (int i = 0; i < phrases.size(); i++) {
			PhraseInfo phrase = phrases.get(i);
			VerbalPhraseStructureInfo vps = getPhraseStructure(phrase);
			if (vps != null) {
				String taskText = vps.toTaskText();
				System.err.println(taskText);
				TaskInfo task = taskMap.get(taskText);
				if (task == null) {
					task = new TaskInfo(vps);
					task = taskDAO.addTask(task);// set id
					if (task == null) //exception caught, insertion failed.
						continue;
					taskMap.put(taskText, task);
				}
				phraseDAO.updateTaskId(phrase.getId(), task.getId());
			}

			if (i % 5000 == 4999) {
				t1 = System.currentTimeMillis();
				float avrg = (t1 - t0) / (float) (i + 1);
				float remaining = (phraseCount - i - 1) * avrg / 1000;
				logger.info("taskMap size:" + taskMap.size() + " phrases handled:" + (i + 1) + " avrg_time:"
						+ avrg + "ms. remaining:" + remaining + "s.");
			}
		}
	}

	public static VerbalPhraseStructureInfo getPhraseStructure(PhraseInfo phrase) {
		if (phrase.getPhraseType() != PhraseInfo.PHRASE_TYPE_VP)
			return null;

		Tree stemmedPhraseTree // = Tree.valueOf(phrase.getSyntaxTree());
				= Stemmer.stemTree(Tree.valueOf(phrase.getSyntaxTree()));
		VerbalPhraseStructureInfo vps = new VerbalPhraseStructureInfo(stemmedPhraseTree);

//		System.out.println(phrase.getText());
//		System.out.println(stemmedPhraseTree);
//		System.out.println(vps);

		return vps;
	}

	public static void main2(String[] args) {
		String string = "I'm trying to develop a complex report, and I need to set up the print areas for the excel file.";
		// "I am working with a Swing program and having a little trouble.";
		// "How can I get it into the giant box?" ;
		// "He got a score of 3.7 in the exams.";
		// " support for some of the `` Java Look and Feel Graphics Repository
		// '' actions .";
		// "He wants to immediately give us an idea of how things are scored.";
		// "I could never be playing around with natural language parse trees,
		// he wants to handle empty cells in excel sheets.";
		// "He wants to put that into this box, I hope I can move these from
		// there to his room.";
		// ", he'll be a dancer, and he wouldn't said that his father might be a
		// farmer, his family
		// will never be Chinese.";

		Tree t = NLPParser.parseGrammaticalTree(string);
		t.pennPrint();
		PhraseInfo[] ps = PhraseExtractor.extractVerbPhrases(t);

		for (PhraseInfo phrase : ps) {
			System.out.println("==================");
			phrase.getSyntaxTree();
			PhraseFilter.filter(phrase, string);

			getPhraseStructure(phrase);
		}

	}

}
