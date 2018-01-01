package cn.edu.pku.sei.tsr.dragon.taskdocmodel.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Corpus implements Serializable {
	private static final long serialVersionUID = -8593216314080330299L;

	public static final int	TYPE_DEFAULT						= 0;
	public static final int	TYPE_FULL_SUBJECT					= 1;
	public static final int	TYPE_TRAINING_SET					= 2;
	public static final int	TYPE_HALF_TRAINING_SET				= 3;
	public static final int	TYPE_ANNOTATED_TRAINING_SET			= 4;
	public static final int	TYPE_ANNOTATED_HALF_TRAINING_SET	= 5;

	private HashMap<Integer, Integer>	threadIdToDocumentIndexMap	= new HashMap<>();
	private List<Document>				documents					= new ArrayList<>();
	private String						subjectName;
	private int							type						= TYPE_DEFAULT;
	private HashMap<String, Float>		taskIDFCache				= new HashMap<>();

	public Corpus(String subjectName) {
		super();
		this.setSubjectName(subjectName);
	}

	public Document getDocumentByThreadId(int id) {
		try {
			int docIndex = threadIdToDocumentIndexMap.get(id);
			return documents.get(docIndex);
		}
		catch (Exception e) {
			return null;
		}
	}

	public int addDocumentWithReplacement(Document doc) {
		Document existDoc = getDocumentByThreadId(doc.getThreadId());
		if (existDoc != null) {
			// duplicate document, decide which to keep.
			boolean replaceFlag = false;
			if (!doc.isAnnotated())
				return -1;
			else if (!existDoc.isAnnotated())
				replaceFlag = true;
			else {
				// both annotated
				float existMax = Integer.MIN_VALUE, existMin = Integer.MAX_VALUE, existAvrg = 0;
				for (TaskVector vector : existDoc.getTaskVectors()) {
					float annotatedScore = vector.getAnnotatedScore();
					existMax = (existMax >= annotatedScore) ? existMax : annotatedScore;
					existMin = (existMin <= annotatedScore) ? existMin : annotatedScore;
					existAvrg += annotatedScore;
				}
				existAvrg /= existDoc.getTaskVectors().size();

				float docMax = Integer.MIN_VALUE, docMin = Integer.MAX_VALUE, docAvrg = 0;
				for (TaskVector vector : doc.getTaskVectors()) {
					float annotatedScore = vector.getAnnotatedScore();
					docMax = (docMax >= annotatedScore) ? docMax : annotatedScore;
					docMin = (docMin <= annotatedScore) ? docMin : annotatedScore;
					docAvrg += annotatedScore;
				}
				docAvrg /= doc.getTaskVectors().size();

				if (existAvrg < docAvrg)
					replaceFlag = true;
				else if (existAvrg == docAvrg) {
					if (existMin == Integer.MIN_VALUE && docMin > Integer.MIN_VALUE)
						replaceFlag = true;
					else if (existMax < docMax)
						replaceFlag = true;
				}
			}

			if (replaceFlag) {
				System.err.println("[ERR]Replace document!!\t" + doc.getThreadId());
				removeDocument(existDoc);
				return addDocument(doc);
			}
		}
		else {
			// not existed document
			return addDocument(doc);
		}
		return -1;
	}

	public int addDocument(Document doc) {
		Document existDoc = getDocumentByThreadId(doc.getThreadId());
		if (existDoc != null)
			return -1; // do not add if existed

		documents.add(doc);
		int index = documents.indexOf(doc);
		threadIdToDocumentIndexMap.put(doc.getThreadId(), index);
		return index;
	}

	public int removeDocument(Document doc) {
		int index = documents.indexOf(doc);
		documents.remove(doc);
		threadIdToDocumentIndexMap.remove(doc.getThreadId());
		return index;
	}

	public HashMap<Integer, Integer> getThreadIdToDocumentIndexMap() {
		return threadIdToDocumentIndexMap;
	}

	public void setThreadIdToDocumentIndexMap(
			HashMap<Integer, Integer> threadIdToDocumentIndexMap) {
		this.threadIdToDocumentIndexMap = threadIdToDocumentIndexMap;
	}

	public List<Document> getDocuments() {
		return documents;
	}

	public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public HashMap<String, Float> getTaskIDFCache() {
		if (taskIDFCache == null)
			taskIDFCache = new HashMap<>();
		return taskIDFCache;
	}

	public void setTaskIDFCache(HashMap<String, Float> taskIDFCache) {
		this.taskIDFCache = taskIDFCache;
	}
}
