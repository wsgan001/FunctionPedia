package cn.edu.pku.sei.tsr.dragon.taskdocmodel.search;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

import cn.edu.pku.sei.tsr.dragon.email.mbox.crawler.ApacheMboxCrawler;
import cn.edu.pku.sei.tsr.dragon.experiment.APILibrary;
import cn.edu.pku.sei.tsr.dragon.experiment.SubjectDataHandler;
import cn.edu.pku.sei.tsr.dragon.experiment.entity.SubjectDataInfo;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.ThreadInfo;
import cn.edu.pku.sei.tsr.dragon.taskdocmodel.entity.Corpus;
import cn.edu.pku.sei.tsr.dragon.taskdocmodel.entity.Document;
import cn.edu.pku.sei.tsr.dragon.taskdocmodel.entity.Result;
import cn.edu.pku.sei.tsr.dragon.taskdocmodel.entity.TaskVector;
import cn.edu.pku.sei.tsr.dragon.taskdocmodel.scorer.TaskSimilarity;
import cn.edu.pku.sei.tsr.dragon.utils.Config;

public class APITasksSearcher {
	public static final Logger	logger		= Logger.getLogger(APITasksSearcher.class);
	public static final float	THRESHOLD	= 0.05f;

	public static void main(String[] args) {
//		String path = Config.getSearchDir() + File.separator + "corpusMap.dat";
//		SubjectDataHandler.readSubjectCorpusDataFromFile(path);
//		indexingAllSubjectCorpus();
//		SubjectDataHandler.writeSubjectCorpusDataToFile(path);

		SubjectDataHandler.readSubjectDataFromFile(APILibrary.APACHE_POI);
		String query = "";
		List<Result> results = search(query, APILibrary.APACHE_POI);
		for (int i = 0; i < results.size(); i++) {
			SubjectDataInfo subject = SubjectDataHandler.subjectDataMap.get(APILibrary.APACHE_POI);
			Result result = results.get(i);
			ThreadInfo thread = subject.threadMap.get(result.getThreadId());
			System.err.println(
					i + "\t" + thread.getId() + "\t" + thread.getQuestionId() + "\t" + thread.getTitle());
		}
	}

	public static List<Result> search(String query, String subjectName) {
		return search(query, SubjectDataHandler.subjectCorpusMap.get(subjectName));
	}

	public static List<Result> search(String query, Corpus corpus) {
		List<Result> results = new ArrayList<>();
		for (Document doc : corpus.getDocuments()) {
			float relevancy = calculateSearchRelevancyBetweenQueryAndDocument(query, doc);
			if (relevancy >= THRESHOLD) {
				Result result = new Result();
				result.setDocument(doc);
				result.setRankScore(relevancy);
			}
		}

		results.sort(new Comparator<Result>() {
			@Override
			public int compare(Result r1, Result r2) {
				float diff = r2.getRankScore() - r1.getRankScore();
				if (diff < 0)
					return -1;
				else if (diff == 0)
					return 0;
				else // (diff > 0)
					return 1;
			}
		});
		return results;
	}

	public static float calculateSearchRelevancyBetweenQueryAndDocument(String query, Document doc) {
		if (query == null || doc == null)
			return 0;
		float searchRelevancy = 0;
		for (TaskVector taskInDoc : doc.getTaskVectors()) {
			float taskSimilarity = TaskSimilarity.calculateSimilarity(query,
					taskInDoc.getTaskInfo().getText());
			float taskInDocScore = taskInDoc.getTFIDF();
			searchRelevancy += taskSimilarity * taskInDocScore;
		}
		return searchRelevancy;
	}
}
