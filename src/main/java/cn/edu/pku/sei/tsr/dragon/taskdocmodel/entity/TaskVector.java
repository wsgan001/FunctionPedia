package cn.edu.pku.sei.tsr.dragon.taskdocmodel.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.PrimitiveIterator.OfDouble;

import org.apache.xpath.operations.And;

import cn.edu.pku.sei.tsr.dragon.task.entity.TaskInfo;

public class TaskVector implements Serializable {
	private static final long		serialVersionUID	= -4096169240058358121L;
	public int						currScoreRange		= VectorFeatureSelector.SCORE_RANGE_1;

	public TaskInfo					taskInfo;
	public float					score;
	public float					annotatedScore		= Integer.MIN_VALUE;

	public float					relevancy			= 0;									// classifier?
	public float					idf					= 0;									// global
																								// unique
	private float					tfidf				= Integer.MIN_VALUE;

	public HashMap<String, Float>	taskSimilarityCache	= new HashMap<>();

	private VectorFeatureSelector	selector			= new VectorFeatureSelector();

	public float getTFIDF() {
		if (tfidf <= Integer.MIN_VALUE) {
			tfidf = relevancy * idf;
		}
		return tfidf;
	}

	/** 1 term frequency **/
	public int		taskFrequency;

	/** 1 word term frequency **/
	public float	tfInThread;
	public float	kernelVerbTFInThread;
	public float	kernelNounTFInThread;
	public float	verbTFInThread;
	public float	nounTFInThread;

	/** 2 page rank - task similarity **/
	public float	similarityWithTasksMax			= 0, similarityWithTasksAvrg = 0;

	/** 3 task & phrase info **/
	@Deprecated
	public int		length;																						// 字母数量毫无意义
	public int		wordCount;
	public int		verbCount, nounCount;
	public int		npWordCount, ppWordCount;

	// ProofType, 3 forms, 3 kinds of context
	public boolean	isVPNP							= false, isVPNPPP = false, isVPPP = false;
	private boolean	hasPP							= false /* short vector */;
	public int		proofScoreMax					= 0;
	public float	proofScoreAvrg					= 0;

	/** 4 positional info **/
	public boolean	isInQuestion					= false, isInTitle = false, isInAnswer = false,
			isInAcceptedAnswer = false, isInComment = false;

	/** 4 paragraph positional info **/
	public boolean	isInFirstParagraph				= false, isInLastParagraph = false;
	public boolean	isTheOnlyParagraphInContent		= false;
	public int		paragraphPositionFirst			= Integer.MAX_VALUE, paragraphPositionLast = 0;
	public float	paragraphPositionAvrg			= 0;
	public float	paragraphRelativePositionFirst	= 1, paragraphRelativePositionLast = 0,
			paragraphRelativePositionAvrg = 0;

	/** 4 sentence positional info **/
	public boolean	isInFirstSentence				= false, isInLastSentence = false;
	public boolean	isTheOnlySentenceInParagraph	= false;
	public int		sentencePositionFirst			= Integer.MAX_VALUE, sentencePositionLast = 0;
	public float	sentencePositionAvrg			= 0;
	public float	sentenceRelativePositionFirst	= 1, sentenceRelativePositionLast = 0,
			sentenceRelativePositionAvrg = 0;

	/** 5 context info **/
	public boolean	contextQAWordsImmediate			= false, contextQAWordsNearBy = false,
			contextQAWordsPreceding = false;

	// code related
	public boolean	isBeforeCode					= false, isAfterCode = false;
	private boolean isNearCode = false; /*short vector*/
	public boolean	containsCodeInContent			= false, containsCodeInSentence = false;

	public int		contentLengthMax				= 0, contentLengthMin = Integer.MAX_VALUE;
	public float	contentLengthAvrg				= 0;
	public int		paragraphLengthMax				= 0, paragraphLengthMin = Integer.MAX_VALUE;
	public float	paragraphLengthAvrg				= 0;
	public int		sentenceLengthMax				= 0, sentenceLengthMin = Integer.MAX_VALUE;
	public float	sentenceLengthAvrg				= 0;

	/** 6 contextual term frequency **/
	public float	tfInQuestion, tfInAcceptedAnswer, tfInAnswers;
	public float	kernelVerbTFInQuestion, kernelVerbTFInAcceptedAnswer, kernelVerbTFInAnswers;
	public float	kernelNounTFInQuestion, kernelNounTFInAcceptedAnswer, kernelNounTFInAnswers;
	public float	verbTFInQuestion, verbTFInAcceptedAnswer, verbTFInAnswers;
	public float	nounTFInQuestion, nounTFInAcceptedAnswer, nounTFInAnswers;

	public float	tfInPostMax						= 0, tfInParagraphMax = 0, tfInSentenceMax = 0;
	public float	kernelVerbTFInPostMax			= 0, kernelVerbTFInParagraphMax = 0,
			kernelVerbTFInSentenceMax = 0;
	public float	kernelNounTFInPostMax			= 0, kernelNounTFInParagraphMax = 0,
			kernelNounTFInSentenceMax = 0;
	public float	verbTFInPostMax					= 0, verbTFInParagraphMax = 0, verbTFInSentenceMax = 0;
	public float	nounTFInPostMax					= 0, nounTFInParagraphMax = 0, nounTFInSentenceMax = 0;

	public float	tfInPostAvrg					= 0, tfInParagraphAvrg = 0, tfInSentenceAvrg = 0;
	public float	kernelVerbTFInPostAvrg			= 0, kernelVerbTFInParagraphAvrg = 0,
			kernelVerbTFInSentenceAvrg = 0;
	public float	kernelNounTFInPostAvrg			= 0, kernelNounTFInParagraphAvrg = 0,
			kernelNounTFInSentenceAvrg = 0;
	public float	verbTFInPostAvrg				= 0, verbTFInParagraphAvrg = 0, verbTFInSentenceAvrg = 0;
	public float	nounTFInPostAvrg				= 0, nounTFInParagraphAvrg = 0, nounTFInSentenceAvrg = 0;

	/** 7 stackoverflow meta info **/
	@Deprecated
	public int		threadFavorites, threadScores, threadViews, threadAnswersCount;

	public int		contentScoreMax					= 0;
	public float	contentScoreAvrg				= 0;
	public float	answerRelativeRankBest			= 1, answerRelativeRankAvrg = 0;

	public int		commentCountMax					= 0, commentCountMin = Integer.MAX_VALUE;
	public float	commentCountAvrg				= 0;

	/** 7 user meta info **/
	public boolean	isRegisteredUser				= false;
	// post only
	public boolean	isEdited						= false;
	// post or comment
	public float	ownerAcceptedAnswerRateMax		= 0, ownerAcceptedAnswerRateAvrg = 0;
	private float	ownerTotalBadges/* short vector */;
	public int		ownerReputationMax				= 0, ownerBadges1stMax = 0, ownerBadges2ndMax = 0,
			ownerBadges3rdMax = 0, ownerQuestionsCountMax = 0, ownerAnswersCountMax = 0, ownerUpVotesMax = 0,
			ownerDownVotesMax = 0;
	public float	ownerReputationAvrg				= 0, ownerBadges1stAvrg = 0, ownerBadges2ndAvrg = 0,
			ownerBadges3rdAvrg = 0, ownerQuestionsCountAvrg = 0, ownerAnswersCountAvrg = 0,
			ownerUpVotesAvrg = 0, ownerDownVotesAvrg = 0;
	public int		ownerReputationSum				= 0, ownerBadges1stSum = 0, ownerBadges2ndSum = 0,
			ownerBadges3rdSum = 0, ownerQuestionsCountSum = 0, ownerAnswersCountSum = 0, ownerUpVotesSum = 0,
			ownerDownVotesSum = 0;
	// post
	public int		lastEditorReputationMax			= 0, lastEditorBadges1stMax = 0,
			lastEditorBadges2ndMax = 0, lastEditorBadges3rdMax = 0, lastEditorQuestionsCountMax = 0,
			lastEditorAnswersCountMax = 0, lastEditorUpVotesMax = 0, lastEditorDownVotesMax = 0;
	public float	lastEditorReputationAvrg		= 0, lastEditorBadges1stAvrg = 0,
			lastEditorBadges2ndAvrg = 0, lastEditorBadges3rdAvrg = 0, lastEditorQuestionsCountAvrg = 0,
			lastEditorAnswersCountAvrg = 0, lastEditorUpVotesAvrg = 0, lastEditorDownVotesAvrg = 0;
	public int		lastEditorReputationSum			= 0, lastEditorBadges1stSum = 0,
			lastEditorBadges2ndSum = 0, lastEditorBadges3rdSum = 0, lastEditorQuestionsCountSum = 0,
			lastEditorAnswersCountSum = 0, lastEditorUpVotesSum = 0, lastEditorDownVotesSum = 0;
	public float	lastEditorAcceptedAnswerRateMax	= 0, lastEditorAcceptedAnswerRateAvrg = 0;

	public TaskVector(TaskInfo task) {
		this.taskInfo = task;
	}

	@Override
	public String toString() {
		String instance = getInstanceData();
		if (instance.equals(""))
			return "Invalid annotated score:" + annotatedScore;
		else
			return instance;
	}

	// use feature selector to manage features
	public String getInstanceData() {
		if(selector!=null)
			return selector.getInstanceData(this);
		else{
			VectorFeatureSelector fullSelector=VectorFeatureSelector.getFullFeatureSelector();
			return fullSelector.getInstanceData(this);
		}
	}
	
	public String getInstanceData(VectorFeatureSelector selector) {
		return selector.getInstanceData(this);
	}

	public String getArffHead() {
		if(selector!=null)
			return selector.getArffHead();
		else{
			VectorFeatureSelector fullSelector=VectorFeatureSelector.getFullFeatureSelector();
			return fullSelector.getArffHead();
		}
	}

	public String getArffHeadWithoutClassAttribute() {
		if(selector!=null)
			return selector.getArffHeadWithoutClassAttribute();
		else{
			VectorFeatureSelector fullSelector=VectorFeatureSelector.getFullFeatureSelector();
			return fullSelector.getArffHeadWithoutClassAttribute();
		}
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

	public VectorFeatureSelector getSelector() {
		return selector;
	}

	public void setSelector(VectorFeatureSelector selector) {
		this.selector = selector;
	}

	public boolean isNearCode() {
		if(isAfterCode || isBeforeCode) //if any of them are true, set true.
			isNearCode = isAfterCode || isBeforeCode; // new
		return isNearCode;
	}

	public void setNearCode(boolean isNearCode) {
		this.isNearCode = isNearCode;
	}

	public boolean isHasPP() {
		boolean hasPP = isVPNPPP || isVPPP || !isVPNP;
		if(hasPP)
			this.hasPP=hasPP;
		return this.hasPP;
	}

	public void setHasPP(boolean hasPP) {
		this.hasPP = hasPP;
	}

	public float getOwnerTotalBadges() {
		int ownerTotalBadges = ownerBadges1stMax + ownerBadges2ndMax
				+ ownerBadges3rdMax; // new
		if(this.ownerTotalBadges<=0 && ownerTotalBadges>0)
			this.ownerTotalBadges=ownerTotalBadges;
		return this.ownerTotalBadges;
	}

	public void setOwnerTotalBadges(float ownerTotalBadges) {		
		this.ownerTotalBadges = ownerTotalBadges;
	}

}
