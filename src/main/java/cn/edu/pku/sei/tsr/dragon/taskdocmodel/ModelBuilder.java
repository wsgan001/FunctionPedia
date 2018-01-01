package cn.edu.pku.sei.tsr.dragon.taskdocmodel;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import cn.edu.pku.sei.tsr.dragon.experiment.SubjectDataHandler;
import cn.edu.pku.sei.tsr.dragon.experiment.TrainingSet;
import cn.edu.pku.sei.tsr.dragon.experiment.entity.SubjectDataInfo;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.ThreadInfo;
import cn.edu.pku.sei.tsr.dragon.taskdocmodel.entity.Corpus;
import cn.edu.pku.sei.tsr.dragon.taskdocmodel.entity.Document;
import cn.edu.pku.sei.tsr.dragon.taskdocmodel.entity.TaskVector;
import cn.edu.pku.sei.tsr.dragon.taskdocmodel.scorer.FeatureExtractor;

public class ModelBuilder {
	public static final Logger logger = Logger.getLogger(ModelBuilder.class);

	public static Corpus buildCorpusForSubject(SubjectDataInfo subject) {
		Corpus corpus = new Corpus(subject.getName());
		corpus.setType(Corpus.TYPE_FULL_SUBJECT);
		for (Integer threadId : subject.threadMap.keySet()) {
			Document threadDocument = FeatureExtractor.extractFeatures(threadId, subject);
			corpus.addDocument(threadDocument);
		}
		logger.info("Built corpus for subject: " + corpus.getSubjectName());
		return corpus;
	}

	public static Corpus extractTrainingCorpus(Corpus subjectCorpus, TrainingSet ts) {
		Corpus corpus = new Corpus(subjectCorpus.getSubjectName());
		corpus.setType(Corpus.TYPE_TRAINING_SET);
		for (ThreadInfo thread : ts.getSelectedThreads()) {
			Document threadDocument = subjectCorpus.getDocumentByThreadId(thread.getId());
			// = FeatureExtractor.extractFeatures(thread.getId(), subject);
			if (threadDocument == null) {
				System.err.println("[ERR]No existed document!" + thread.getId());
				continue;
			}

			corpus.addDocument(threadDocument);
		}
		logger.info("Built corpus for training set: " + corpus.getSubjectName());
		return corpus;
	}

	public static Corpus buildCorpusForTrainingSet(SubjectDataInfo subject, TrainingSet ts) {
		Corpus corpus = new Corpus(subject.getName());
		corpus.setType(Corpus.TYPE_TRAINING_SET);
		for (ThreadInfo thread : ts.getSelectedThreads()) {
			Document threadDocument = FeatureExtractor.extractFeatures(thread.getId(), subject);
			corpus.addDocument(threadDocument);
		}
		logger.info("Built corpus for training set: " + corpus.getSubjectName());
		return corpus;
	}

	public static Corpus combineCorpus(Corpus c1, Corpus c2) {
		if (!c1.getSubjectName().equals(c2.getSubjectName()) || c1.getType() != c2.getType())
			return null;

		Corpus corpus = new Corpus(c1.getSubjectName());
		corpus.setType(c1.getType());

		for (Document doc1 : c1.getDocuments()) {
			corpus.addDocument(doc1);
		}

		for (Document doc2 : c2.getDocuments()) {
			corpus.addDocumentWithReplacement(doc2);
		}
		return corpus;
	}

	public static void main(String[] args) {

	}
}
