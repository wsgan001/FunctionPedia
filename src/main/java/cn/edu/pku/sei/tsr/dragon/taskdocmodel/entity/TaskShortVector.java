package cn.edu.pku.sei.tsr.dragon.taskdocmodel.entity;

import java.io.Serializable;
import java.util.HashMap;

import cn.edu.pku.sei.tsr.dragon.task.entity.TaskInfo;

public class TaskShortVector implements Serializable {
	private static final long		serialVersionUID		= -4096169240058358121L;
	public static final int			SCORE_RANGE_1			= 1;
	public static final int			SCORE_RANGE_2			= 2;
	public static final int			SCORE_RANGE_3			= 3;
	public static final int			SCORE_RANGE_5			= 5;
	public static final int			SCORE_RANGE_ANNOTATED	= SCORE_RANGE_3;
	public static int				currScoreRange			= SCORE_RANGE_1;

	public TaskInfo					taskInfo;
	public float					score;
	public float					annotatedScore			= Integer.MIN_VALUE;

	public float					relevancy				= 0;					// classifier?
	public float					idf						= 0;					// global
																					// unique
	private float					tfidf					= Integer.MIN_VALUE;

	public HashMap<String, Float>	taskSimilarityCache		= new HashMap<>();

	public float getTFIDF() {
		if (tfidf <= Integer.MIN_VALUE) {
			tfidf = relevancy * idf;
		}
		return tfidf;
	}

	/** task info **/
	// public int length;
	public int		wordCount;

	/** page rank - task similarity **/
	public float	similarityWithTasksMax			= 0;
	// public float similarityWithTasksAvrg = 0;

	/** term frequency **/
	public int		taskFrequency;
	public float	tfInThread;
	public float	kernelVerbTFInThread;
	public float	kernelNounTFInThread;

	public float	tfInContentMax					= 0;
	public float	kernelVerbTFInContentMax		= 0;
	public float	kernelNounTFInContentMax		= 0;

	/** phrase info **/
	// ProofType, 3 forms, 3 kinds of context
	public boolean	hasPP							= false;
	public int		proofScoreMax					= 0;
	public float	proofScoreAvrg					= 0;
	public boolean	contextQAWordsImmediate			= false, contextQAWordsNearBy = false,
			contextQAWordsPreceding = false;

	/** source content **/
	public boolean	isInQuestion					= false, isInTitle = false, isInAnswer = false,
			isInAcceptedAnswer = false;
	public float	answerRelativeRankBest			= 1;
	public int		contentScoreMax					= 0;

	// post
	public boolean	containsCodeInContent			= false, containsCodeInSentence = false;

	/** paragraph info **/
	public boolean	isInFirstParagraph				= false, isInLastParagraph = false;
	public boolean	isTheOnlyParagraphInContent		= false;
	// code related
	public boolean	isNearCode						= false;

	/** sentence info **/
	public boolean	isInFirstSentence				= false, isInLastSentence = false;
	public boolean	isTheOnlySentenceInParagraph	= false;

	/** user info **/
	// post or comment
	public int		ownerReputationMax				= 0, ownerTotalBadges = 0;

	public TaskShortVector(TaskInfo task) {
		this.taskInfo = task;
	}

	public TaskShortVector(TaskVector longVector) {
		this.taskInfo = longVector.getTaskInfo();
		this.score = longVector.score;
		this.annotatedScore = longVector.annotatedScore;
		this.relevancy = longVector.relevancy;
		this.idf = longVector.idf;
		this.tfidf = longVector.getTFIDF();

		this.taskSimilarityCache = longVector.taskSimilarityCache;

		this.wordCount = longVector.wordCount;
		this.similarityWithTasksMax = longVector.similarityWithTasksMax;
		this.taskFrequency = longVector.taskFrequency;
		this.tfInThread = longVector.tfInThread;
		this.kernelVerbTFInThread = longVector.kernelVerbTFInThread;
		this.kernelNounTFInThread = longVector.kernelNounTFInThread;
		this.tfInContentMax = longVector.tfInPostMax;
		this.kernelVerbTFInContentMax = longVector.kernelVerbTFInPostMax;
		this.kernelNounTFInContentMax = longVector.kernelNounTFInPostMax;
		this.hasPP = longVector.isVPNPPP || longVector.isVPPP || !longVector.isVPNP; // new
		this.contextQAWordsImmediate = longVector.contextQAWordsImmediate;
		this.contextQAWordsNearBy = longVector.contextQAWordsNearBy;
		this.contextQAWordsPreceding = longVector.contextQAWordsPreceding;
		this.proofScoreMax = longVector.proofScoreMax;
		this.proofScoreAvrg = longVector.proofScoreAvrg;
		this.isInQuestion = longVector.isInQuestion;
		this.isInTitle = longVector.isInTitle;
		this.isInAnswer = longVector.isInAnswer;
		this.isInAcceptedAnswer = longVector.isInAcceptedAnswer;
		this.answerRelativeRankBest = longVector.answerRelativeRankBest;
		this.contentScoreMax = longVector.contentScoreMax;
		this.containsCodeInContent = longVector.containsCodeInContent;
		this.containsCodeInSentence = longVector.containsCodeInSentence;
		this.isInFirstParagraph = longVector.isInFirstParagraph;
		this.isInLastParagraph = longVector.isInLastParagraph;
		this.isTheOnlyParagraphInContent = longVector.isTheOnlyParagraphInContent;
		this.isNearCode = longVector.isAfterCode || longVector.isBeforeCode; // new
		this.isInFirstSentence = longVector.isInFirstSentence;
		this.isInLastSentence = longVector.isInLastSentence;
		this.isTheOnlySentenceInParagraph = longVector.isTheOnlySentenceInParagraph;
		this.ownerReputationMax = longVector.ownerReputationMax;
		this.ownerTotalBadges = longVector.ownerBadges1stMax + longVector.ownerBadges2ndMax
				+ longVector.ownerBadges3rdMax; // new
	}

	@Override
	public String toString() {
		String instance = getInstanceData(currScoreRange);
		if (instance.equals(""))
			return "Invalid annotated score:" + annotatedScore;
		else
			return instance;
	}

	/**
	 * @param range
	 *            range = 1: {0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1}
	 *            range = 5: {-5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5} range = 3:
	 *            {0, 1, 2, 3} range=2:{1, 0}
	 * @return
	 */
	public String getInstanceData(int range) {
		StringBuilder sb = new StringBuilder();
		sb.append(wordCount + ", ");
		sb.append(similarityWithTasksMax + ", ");
		sb.append(taskFrequency + ", ");
		sb.append(tfInThread + ", ");
		sb.append(kernelVerbTFInThread + ", ");
		sb.append(kernelNounTFInThread + ", ");
		sb.append(tfInContentMax + ", ");
		sb.append(kernelVerbTFInContentMax + ", ");
		sb.append(kernelNounTFInContentMax + ", ");
		sb.append(hasPP + ", ");
		sb.append(contextQAWordsImmediate + ", ");
		sb.append(contextQAWordsNearBy + ", ");
		sb.append(contextQAWordsPreceding + ", ");
		sb.append(proofScoreMax + ", ");
		sb.append(proofScoreAvrg + ", ");
		sb.append(isInQuestion + ", ");
		sb.append(isInTitle + ", ");
		sb.append(isInAnswer + ", ");
		sb.append(isInAcceptedAnswer + ", ");
		sb.append(answerRelativeRankBest + ", ");
		sb.append(contentScoreMax + ", ");
		sb.append(containsCodeInContent + ", ");
		sb.append(containsCodeInSentence + ", ");
		sb.append(isInFirstParagraph + ", ");
		sb.append(isInLastParagraph + ", ");
		sb.append(isTheOnlyParagraphInContent + ", ");
		sb.append(isNearCode + ", ");
		sb.append(isInFirstSentence + ", ");
		sb.append(isInLastSentence + ", ");
		sb.append(isTheOnlySentenceInParagraph + ", ");
		sb.append(ownerReputationMax + ", ");
		sb.append(ownerTotalBadges + ", ");
		float score = normalizeAnnotatedScore(annotatedScore, currScoreRange, range);
		if (score <= Integer.MIN_VALUE)
			sb.append("?\n");
		else
			sb.append(score + "\n");
		return sb.toString();
	}

	public float getScore() {
		return score;
	}

	public void setScore(float score) {
		this.score = score;
	}

	public float getAnnotatedScore() {
		return annotatedScore;
	}

	public void setAnnotatedScore(float annotatedScore) {
		this.annotatedScore = annotatedScore;
	}

	public TaskInfo getTaskInfo() {
		return taskInfo;
	}

	public void setTaskInfo(TaskInfo taskInfo) {
		this.taskInfo = taskInfo;
	}

	public float getRelevancy() {
		return relevancy;
	}

	public void setRelevancy(float relevancy) {
		this.relevancy = relevancy;
	}

	public float getIdf() {
		return idf;
	}

	public void setIdf(float idf) {
		this.idf = idf;
	}

	public void setTfidf(float tfidf) {
		this.tfidf = tfidf;
	}

	public HashMap<String, Float> getTaskSimilarityCache() {
		if (taskSimilarityCache == null)
			taskSimilarityCache = new HashMap<>();
		return taskSimilarityCache;
	}

	public void setTaskSimilarityCache(HashMap<String, Float> taskSimilarityCache) {
		this.taskSimilarityCache = taskSimilarityCache;
	}

	/**
	 * original format: 3, 2, 1, 0 (range 3) curr data format: 1, 0.8, 0.5, 0
	 * (range 1) range 5: 5, 4, ..., 0, ..., -4, -5(range 5)
	 * 
	 * @param range
	 * @return
	 */
	public static float normalizeAnnotatedScore(float score, int srcRange, int destRange) {

		float[][] scoreMap = new float[][] { { 1.0f, 0.8f, 0.5f, 0.0f }, { 3, 2, 1, 0 }, { 5, 3, 0, -5 } };

		int srcRangeIdx = (srcRange - 1) / 2;
		int destRangeIdx = (destRange - 1) / 2;

		if (destRange == TaskShortVector.SCORE_RANGE_2) {
			return score >= scoreMap[srcRangeIdx][2] ? 1 : 0;
		}

		for (int scoreIdx = 0; scoreIdx < 4; scoreIdx++) {
			if (score == scoreMap[srcRangeIdx][scoreIdx])
				return scoreMap[destRangeIdx][scoreIdx];
		}

		int srcUpper, srcLower, destUpper, destLower;

		switch (srcRange) {
		case SCORE_RANGE_5:
			srcUpper = 5;
			srcLower = -5;
			break;
		case SCORE_RANGE_3:
			srcUpper = 3;
			srcLower = 0;
			break;
		case SCORE_RANGE_1:
		default:
			srcUpper = 1;
			srcLower = 0;
			break;
		}

		switch (destRange) {
		case SCORE_RANGE_5:
			destUpper = 5;
			destLower = -5;
			break;
		case SCORE_RANGE_3:
			destUpper = 3;
			destLower = 0;
			break;
		case SCORE_RANGE_1:
		default:
			destUpper = 1;
			destLower = 0;
			break;
		}

		float normalizedScore = (score - srcLower) * (destUpper - destLower) / (srcUpper - srcLower)
				+ destLower;
		return normalizedScore;
	}

	public static String getArffHead(boolean isNumericClass, int range) {
		StringBuilder sb = new StringBuilder(getArffHeadWithoutClassAttribute());
		// Attribute score is the "class" to classify.
		// We use it as the final score in our task scorer.
		if (isNumericClass)
			sb.append("@attribute SCORE numeric\n");
		else {
			if (range == TaskShortVector.SCORE_RANGE_1)
				sb.append("@attribute SCORE {0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0}\n");
			else if (range == TaskShortVector.SCORE_RANGE_5)
				sb.append("@attribute SCORE {-5.0, -4.0, -3.0, -2.0, -1.0, 0.0, 1.0, 2.0, 3.0, 4.0, 5.0}\n");
			else if (range == TaskShortVector.SCORE_RANGE_3)
				sb.append("@attribute SCORE {0.0, 1.0, 2.0, 3.0}\n");
			else if (range == TaskShortVector.SCORE_RANGE_2)
				sb.append("@attribute SCORE {0.0, 1.0}\n");
		}

		sb.append("\n@data\n");
		return sb.toString();
	}

	public static String getArffHeadWithoutClassAttribute() {
		StringBuilder sb = new StringBuilder();
		sb.append("@relation task\n\n");
		sb.append("@attribute wordCount numeric\n");
		sb.append("@attribute similarityWithTasksMax numeric\n");
		sb.append("@attribute taskFrequency numeric\n");
		sb.append("@attribute tfInThread numeric\n");
		sb.append("@attribute kernelVerbTFInThread numeric\n");
		sb.append("@attribute kernelNounTFInThread numeric\n");
		sb.append("@attribute tfInContentMax numeric\n");
		sb.append("@attribute kernelVerbTFInContentMax numeric\n");
		sb.append("@attribute kernelNounTFInContentMax numeric\n");
		sb.append("@attribute hasPP {true,false}\n");
		sb.append("@attribute contextQAWordsImmediate {true,false}\n");
		sb.append("@attribute contextQAWordsNearBy {true,false}\n");
		sb.append("@attribute contextQAWordsPreceding {true,false}\n");
		sb.append("@attribute proofScoreMax numeric\n");
		sb.append("@attribute proofScoreAvrg numeric\n");
		sb.append("@attribute isInQuestion {true,false}\n");
		sb.append("@attribute isInTitle {true,false}\n");
		sb.append("@attribute isInAnswer {true,false}\n");
		sb.append("@attribute isInAcceptedAnswer {true,false}\n");
		sb.append("@attribute answerRelativeRankBest numeric\n");
		sb.append("@attribute contentScoreMax numeric\n");
		sb.append("@attribute containsCodeInContent {true,false}\n");
		sb.append("@attribute containsCodeInSentence {true,false}\n");
		sb.append("@attribute isInFirstParagraph {true,false}\n");
		sb.append("@attribute isInLastParagraph {true,false}\n");
		sb.append("@attribute isTheOnlyParagraphInContent {true,false}\n");
		sb.append("@attribute isNearCode {true,false}\n");
		sb.append("@attribute isInFirstSentence {true,false}\n");
		sb.append("@attribute isInLastSentence {true,false}\n");
		sb.append("@attribute isTheOnlySentenceInParagraph {true,false}\n");
		sb.append("@attribute ownerReputationMax numeric\n");
		sb.append("@attribute ownerTotalBadges numeric\n");

		return sb.toString();
	}

}
