package cn.edu.pku.sei.tsr.dragon.taskdocmodel.search;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

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
import cn.edu.pku.sei.tsr.dragon.utils.ObjectIO;
import weka.classifiers.Classifier;

/**
 * @author ZHUZixiao
 *
 */
public class APITasksIndexer implements Runnable {
	public static final Logger	logger						= Logger
			.getLogger(APITasksSearcher.class);
	public static final String	INDEXED_CORPUS_FILE_NAME	= "indexedCorpus.dat";

	public static int				THREAD_LIMIT	= 9;
	private static Corpus			corpus;
	private static List<Document>	documentPool	= new ArrayList<>();
	private static int				nextIndex		= 0;
	private static int				globalCount		= 0;
	private static long				t_init			= System.currentTimeMillis();

	private static boolean		isInitial	= true;
	private static Classifier	classifier	= null;

	private int localCount = 0;

	// get next document to handle, in multi-thread program
	private static Document getNextDocument() {
		if (documentPool == null)
			return null;
		Document doc = null;
		try {
			if (nextIndex >= documentPool.size())
				return null;

			doc = documentPool.get(nextIndex);
			nextIndex++;
		}
		catch (Exception e) {
			// if (!(e instanceof IndexOutOfBoundsException))
			e.printStackTrace();
		}
		return doc;
	}

	@Override
	public void run() {
		// 从sentence池的文件目录中读出内容
		Document doc;
		while ((doc = getNextDocument()) != null) {
			long t_start = System.currentTimeMillis();

			if (isInitial)
				indexingDocumentInCorpus(doc, corpus);
			else if (classifier != null)
				recalculateTaskRelevancyInDocument(doc, corpus, classifier);

			synchronized (this) {
				globalCount++;
			}

			localCount++;
			long t_mid = System.currentTimeMillis();
			float avrgTimeLocal = (t_mid - t_init) / (float) localCount;
			float avrgTimeGlobal = (t_mid - t_init) / (float) globalCount;
			logger.info("[corpus]" + corpus.getSubjectName() + " [avgt_g]" + avrgTimeGlobal
					+ "ms [remain]" + (documentPool.size() - globalCount) * avrgTimeGlobal / 1000
					+ "s [avgt_l]" + avrgTimeLocal + "ms [current]" + (t_mid - t_start)
					+ "ms [threadId]" + doc.getThreadId() + " [total]" + globalCount
					+ " [nextIndex]" + nextIndex + " [" + Thread.currentThread().getName() + "] "
					+ localCount);
		}
	}

	public static void startClock() {
		t_init = System.currentTimeMillis();
	}

	public static void main(String[] args) {

		String srcCorpusPath = Config.getCorpusDir() + File.separator + "subjectCorpusMap.dat";
		String indexedCorpusPath = Config.getSearchDir() + File.separator
				+ INDEXED_CORPUS_FILE_NAME;

		indexingCorpus(true, srcCorpusPath, indexedCorpusPath, null);
	}

	public static void indexingCorpus(boolean isInitial, String srcPath, String destPath,
			Classifier classifier) {
		SubjectDataHandler.indexedCorpusMap = SubjectDataHandler.readCorpusMapFromFile(srcPath);
		APITasksIndexer.setInitial(isInitial);
		APITasksIndexer.setClassifier(null);

		for (String subjectName : SubjectDataHandler.indexedCorpusMap.keySet()) {
			corpus = SubjectDataHandler.indexedCorpusMap.get(subjectName);
			documentPool.clear();
			documentPool.addAll(corpus.getDocuments());
			nextIndex = 0;
			globalCount = 0;
			t_init = System.currentTimeMillis();

			APITasksIndexer.startClock();
			for (int i = 0; i < THREAD_LIMIT; i++) {
				APITasksIndexer indexer = new APITasksIndexer();
				Thread thread = new Thread(indexer, "[API Indexer] " + subjectName + "-" + i);
				thread.start();
			}

			/** wait for the end of this subject **/
			while (nextIndex < documentPool.size()) {
				try {
					Thread.sleep(10000);
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			int oldGlobal = globalCount;
			long t0 = System.currentTimeMillis();
			while (globalCount < documentPool.size()) {
				try {
					long t1 = System.currentTimeMillis();
					if (t1 - t0 > 10 && globalCount == oldGlobal) {
						// if waiting too long and the process is interrupted,
						// stop the current thread
						t0 = t1;
						oldGlobal = globalCount;
						Thread.sleep(10000);
						globalCount++;
					}
					else {
						t0 = t1;
						oldGlobal = globalCount;
						Thread.sleep(1000);
					}
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			/** end of indexing subject **/
			String subjectCorpusOutputPath = Config.getSearchDir() + File.separator + subjectName
					+ "Corpus.dat";
			ObjectIO.writeObject(corpus, subjectCorpusOutputPath);

			logger.info("Corpus: " + subjectName + " is indexed. output data path: "
					+ subjectCorpusOutputPath);
		}

		SubjectDataHandler.writeCorpusMapToFile(SubjectDataHandler.indexedCorpusMap, destPath);

		// SubjectDataHandler.readSubjectDataFromFile(APILibrary.APACHE_POI);
		// String query = "set/vb cell/nn background/nn color/knn";
		// List<Result> results = APITasksSearcher.search(query,
		// APILibrary.APACHE_POI);
		// for (int i = 0; i < results.size(); i++) {
		// SubjectDataInfo subject =
		// SubjectDataHandler.subjectDataMap.get(APILibrary.APACHE_POI);
		// Result result = results.get(i);
		// ThreadInfo thread = subject.threadMap.get(result.getThreadId());
		// System.err.println(
		// i + "\t" + thread.getId() + "\t" + thread.getQuestionId() + "\t" +
		// thread.getTitle());
		// }
	}

	/**
	 * multi-thread needed, this version consume too much time.
	 */
	@Deprecated
	public static void indexingAllSubjectCorpus() {
		logger.info("start to indexing all subject corpus...");
		for (String subjectName : SubjectDataHandler.subjectCorpusMap.keySet()) {
			Corpus corpus = SubjectDataHandler.subjectCorpusMap.get(subjectName);
			for (Document doc : corpus.getDocuments()) {
				indexingDocumentInCorpus(doc, corpus);
			}
			logger.info("Corpus: " + corpus.getSubjectName() + " is indexed.");
		}
	}

	/**
	 * run at the first time to build up basic indexing data
	 * 
	 * @param doc
	 * @param corpus
	 */
	public static void indexingDocumentInCorpus(Document doc, Corpus corpus) {
		calculateRelevancyForTasksInDocument(doc);
		calculateIDFForTasksInDocument(doc, corpus);
	}

	/**
	 * after first time, only update task scores and relevancy with different classifiers.
	 * 
	 * @param doc
	 * @param corpus
	 */
	public static void recalculateTaskRelevancyInDocument(Document doc, Corpus corpus,
			Classifier classifier) {
		calculateRelevancyForTasksInDocument(doc);
	}

	public static void calculateIDFForTasksInDocument(Document doc, Corpus corpus) {
		for (TaskVector task : doc.getTaskVectors()) {
			float idf = calculateIDFInCorpus(task.getTaskInfo().getText(), corpus);
			task.setIdf(idf);
			// System.err.println(task.getTaskInfo().getText() + "\t" +
			// task.getRelevancy() + "\t"
			// + task.getIdf() + "\t" + task.getTFIDF());
		}
	}

	/**
	 * cache based
	 * 
	 * @param task
	 * @param corpus
	 * @return
	 */
	public static float calculateIDFInCorpus(String task, Corpus corpus) {
		Float cachedIDF = corpus.getTaskIDFCache().get(task);
		if (cachedIDF != null)
			return cachedIDF;

		float occuredDocCount = 0;
		for (Document doc : corpus.getDocuments()) {
			float maxTaskSimilarity = calculateTaskMaxSimilarityInDocument(task, doc);
			occuredDocCount += maxTaskSimilarity;
		}
		if (occuredDocCount <= 0)
			occuredDocCount = 1;
		float idf = (float) Math.log(corpus.getDocuments().size() / occuredDocCount);

		corpus.getTaskIDFCache().put(task, idf);
		return idf;
	}

	public static void calculateRelevancyForTasksInDocument(Document doc) {
		for (TaskVector task : doc.getTaskVectors()) {
			float relevancy = calculateRelevancyBetweenTaskAndDocument(task, doc);
			task.setRelevancy(relevancy);
		}
	}

	public static float calculateRelevancyBetweenTaskAndDocument(TaskVector task, Document doc) {
		if (task == null || doc == null)
			return 0;
		/**
		 * since we'll calculate the similarity between the task and every task in the doc, we try
		 * to calculate and cache the max similarity in the document at the same time, to save time
		 * consuming.
		 **/
		boolean calcMaxSimFlag = false;
		String taskText = task.getTaskInfo().getText();
		if (doc.getMaxSimilarityCache().get(taskText) == null)
			calcMaxSimFlag = true;

		float relevancySum = 0;
		float similarityMax = Integer.MIN_VALUE;
		for (TaskVector taskInDoc : doc.getTaskVectors()) {
			float taskSimilarity = TaskSimilarity.calculateSimilarity(task, taskInDoc);
			float taskInDocScore = taskInDoc.getScore();
			relevancySum += taskSimilarity * taskInDocScore;

			if (calcMaxSimFlag) {
				similarityMax = Math.max(similarityMax, taskSimilarity);
			}
		}
		if (calcMaxSimFlag)
			doc.getMaxSimilarityCache().put(task.getTaskInfo().getText(), similarityMax);

		return relevancySum;
	}

	public static float calculateTaskMaxSimilarityInDocument(String task, Document document) {
		// String taskText = task.getTaskInfo().getText();
		Float cachedMaxSim = document.getMaxSimilarityCache().get(task);
		if (cachedMaxSim != null)
			return cachedMaxSim;
		else {
			float maxSim = Integer.MIN_VALUE;
			for (TaskVector taskInDoc : document.getTaskVectors()) {
				float similarity = TaskSimilarity.calculateSimilarity(task, taskInDoc);
				maxSim = Math.max(maxSim, similarity);
			}
			document.getMaxSimilarityCache().put(task, cachedMaxSim);
			return maxSim;
		}
	}

	public static boolean isInitial() {
		return isInitial;
	}

	public static void setInitial(boolean isInitial) {
		APITasksIndexer.isInitial = isInitial;
	}

	public static Classifier getClassifier() {
		return classifier;
	}

	public static void setClassifier(Classifier classifier) {
		APITasksIndexer.classifier = classifier;
	}

}
