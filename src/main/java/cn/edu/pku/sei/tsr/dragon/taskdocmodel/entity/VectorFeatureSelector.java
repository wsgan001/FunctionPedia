package cn.edu.pku.sei.tsr.dragon.taskdocmodel.entity;

import java.io.Serializable;

public class VectorFeatureSelector implements Serializable, Cloneable{
	private static final long serialVersionUID = 11450528999664597L;
	
	public static final int	SCORE_RANGE_5						= 5;
	public static final int	SCORE_RANGE_3						= 3;
	public static final int	SCORE_RANGE_2						= 2;
	public static final int	SCORE_RANGE_1						= 1;
	public static final int	SCORE_RANGE_ANNOTATED				= SCORE_RANGE_3;

	public String 			name="";
	public int				range								= -1;
	public boolean			isNumericClass						= false;
	
	public VectorFeatureSelector() {
		super();
	}
	public VectorFeatureSelector(String name) {
		super();
		this.name=name;
	}

	public boolean			answerRelativeRankAvrg				= false;
	public boolean			answerRelativeRankBest				= false;
	public boolean			commentCountAvrg					= false;
	public boolean			commentCountMax						= false;
	public boolean			commentCountMin						= false;
	public boolean			containsCodeInContent				= false;
	public boolean			containsCodeInSentence				= false;
	public boolean			contentLengthAvrg					= false;
	public boolean			contentLengthMax					= false;
	public boolean			contentLengthMin					= false;
	public boolean			contentScoreAvrg					= false;
	public boolean			contentScoreMax						= false;
	public boolean			contextQAWordsImmediate				= false;
	public boolean			contextQAWordsNearBy				= false;
	public boolean			contextQAWordsPreceding				= false;
	// only in short vector
	public boolean			hasPP								= false;
	public boolean			isAfterCode							= false;
	public boolean			isBeforeCode						= false;
	public boolean			isEdited							= false;
	public boolean			isInAcceptedAnswer					= false;
	public boolean			isInAnswer							= false;
	public boolean			isInComment							= false;
	public boolean			isInFirstParagraph					= false;
	public boolean			isInFirstSentence					= false;
	public boolean			isInLastParagraph					= false;
	public boolean			isInLastSentence					= false;
	public boolean			isInQuestion						= false;
	public boolean			isInTitle							= false;
	// only in short vector
	public boolean			isNearCode							= false;
	public boolean			isRegisteredUser					= false;
	public boolean			isTheOnlyParagraphInContent			= false;
	public boolean			isTheOnlySentenceInParagraph		= false;
	public boolean			isVPNP								= false;
	public boolean			isVPNPPP							= false;
	public boolean			isVPPP								= false;
	public boolean			kernelNounTFInAcceptedAnswer		= false;
	public boolean			kernelNounTFInAnswers				= false;
	public boolean			kernelNounTFInParagraphAvrg			= false;
	public boolean			kernelNounTFInParagraphMax			= false;
	public boolean			kernelNounTFInPostAvrg				= false;
	public boolean			kernelNounTFInPostMax				= false;
	public boolean			kernelNounTFInQuestion				= false;
	public boolean			kernelNounTFInSentenceAvrg			= false;
	public boolean			kernelNounTFInSentenceMax			= false;
	public boolean			kernelNounTFInThread				= false;
	public boolean			kernelVerbTFInAcceptedAnswer		= false;
	public boolean			kernelVerbTFInAnswers				= false;
	public boolean			kernelVerbTFInParagraphAvrg			= false;
	public boolean			kernelVerbTFInParagraphMax			= false;
	public boolean			kernelVerbTFInPostAvrg				= false;
	public boolean			kernelVerbTFInPostMax				= false;
	public boolean			kernelVerbTFInQuestion				= false;
	public boolean			kernelVerbTFInSentenceAvrg			= false;
	public boolean			kernelVerbTFInSentenceMax			= false;
	public boolean			kernelVerbTFInThread				= false;
	public boolean			lastEditorAcceptedAnswerRateAvrg	= false;
	public boolean			lastEditorAcceptedAnswerRateMax		= false;
	public boolean			lastEditorAnswersCountAvrg			= false;
	public boolean			lastEditorAnswersCountMax			= false;
	public boolean			lastEditorAnswersCountSum			= false;
	public boolean			lastEditorBadges1stAvrg				= false;
	public boolean			lastEditorBadges1stMax				= false;
	public boolean			lastEditorBadges1stSum				= false;
	public boolean			lastEditorBadges2ndAvrg				= false;
	public boolean			lastEditorBadges2ndMax				= false;
	public boolean			lastEditorBadges2ndSum				= false;
	public boolean			lastEditorBadges3rdAvrg				= false;
	public boolean			lastEditorBadges3rdMax				= false;
	public boolean			lastEditorBadges3rdSum				= false;
	public boolean			lastEditorDownVotesAvrg				= false;
	public boolean			lastEditorDownVotesMax				= false;
	public boolean			lastEditorDownVotesSum				= false;
	public boolean			lastEditorQuestionsCountAvrg		= false;
	public boolean			lastEditorQuestionsCountMax			= false;
	public boolean			lastEditorQuestionsCountSum			= false;
	public boolean			lastEditorReputationAvrg			= false;
	public boolean			lastEditorReputationMax				= false;
	public boolean			lastEditorReputationSum				= false;
	public boolean			lastEditorUpVotesAvrg				= false;
	public boolean			lastEditorUpVotesMax				= false;
	public boolean			lastEditorUpVotesSum				= false;
	public boolean			length								= false;
	public boolean			nounCount							= false;
	public boolean			nounTFInAcceptedAnswer				= false;
	public boolean			nounTFInAnswers						= false;
	public boolean			nounTFInParagraphAvrg				= false;
	public boolean			nounTFInParagraphMax				= false;
	public boolean			nounTFInPostAvrg					= false;
	public boolean			nounTFInPostMax						= false;
	public boolean			nounTFInQuestion					= false;
	public boolean			nounTFInSentenceAvrg				= false;
	public boolean			nounTFInSentenceMax					= false;
	public boolean			nounTFInThread						= false;
	public boolean			npWordCount							= false;
	public boolean			ownerAcceptedAnswerRateAvrg			= false;
	public boolean			ownerAcceptedAnswerRateMax			= false;
	public boolean			ownerAnswersCountAvrg				= false;
	public boolean			ownerAnswersCountMax				= false;
	public boolean			ownerAnswersCountSum				= false;
	public boolean			ownerBadges1stAvrg					= false;
	public boolean			ownerBadges1stMax					= false;
	public boolean			ownerBadges1stSum					= false;
	public boolean			ownerBadges2ndAvrg					= false;
	public boolean			ownerBadges2ndMax					= false;
	public boolean			ownerBadges2ndSum					= false;
	public boolean			ownerBadges3rdAvrg					= false;
	public boolean			ownerBadges3rdMax					= false;
	public boolean			ownerBadges3rdSum					= false;
	public boolean			ownerDownVotesAvrg					= false;
	public boolean			ownerDownVotesMax					= false;
	public boolean			ownerDownVotesSum					= false;
	public boolean			ownerQuestionsCountAvrg				= false;
	public boolean			ownerQuestionsCountMax				= false;
	public boolean			ownerQuestionsCountSum				= false;
	public boolean			ownerReputationAvrg					= false;
	public boolean			ownerReputationMax					= false;
	public boolean			ownerReputationSum					= false;
	// only in short vector
	public boolean			ownerTotalBadges					= false;
	public boolean			ownerUpVotesAvrg					= false;
	public boolean			ownerUpVotesMax						= false;
	public boolean			ownerUpVotesSum						= false;
	public boolean			paragraphLengthAvrg					= false;
	public boolean			paragraphLengthMax					= false;
	public boolean			paragraphLengthMin					= false;
	public boolean			paragraphPositionAvrg				= false;
	public boolean			paragraphPositionFirst				= false;
	public boolean			paragraphPositionLast				= false;
	public boolean			paragraphRelativePositionAvrg		= false;
	public boolean			paragraphRelativePositionFirst		= false;
	public boolean			paragraphRelativePositionLast		= false;
	public boolean			ppWordCount							= false;
	public boolean			proofScoreAvrg						= false;
	public boolean			proofScoreMax						= false;
	public boolean			sentenceLengthAvrg					= false;
	public boolean			sentenceLengthMax					= false;
	public boolean			sentenceLengthMin					= false;
	public boolean			sentencePositionAvrg				= false;
	public boolean			sentencePositionFirst				= false;
	public boolean			sentencePositionLast				= false;
	public boolean			sentenceRelativePositionAvrg		= false;
	public boolean			sentenceRelativePositionFirst		= false;
	public boolean			sentenceRelativePositionLast		= false;
	public boolean			similarityWithTasksAvrg				= false;
	public boolean			similarityWithTasksMax				= false;
	public boolean			taskFrequency						= false;
	public boolean			tfInAcceptedAnswer					= false;
	public boolean			tfInAnswers							= false;
	public boolean			tfInParagraphAvrg					= false;
	public boolean			tfInParagraphMax					= false;
	public boolean			tfInPostAvrg						= false;
	public boolean			tfInPostMax							= false;
	public boolean			tfInQuestion						= false;
	public boolean			tfInSentenceAvrg					= false;
	public boolean			tfInSentenceMax						= false;
	public boolean			tfInThread							= false;
	public boolean			threadAnswersCount					= false;
	public boolean			threadFavorites						= false;
	public boolean			threadScores						= false;
	public boolean			threadViews							= false;
	public boolean			verbCount							= false;
	public boolean			verbTFInAcceptedAnswer				= false;
	public boolean			verbTFInAnswers						= false;
	public boolean			verbTFInParagraphAvrg				= false;
	public boolean			verbTFInParagraphMax				= false;
	public boolean			verbTFInPostAvrg					= false;
	public boolean			verbTFInPostMax						= false;
	public boolean			verbTFInQuestion					= false;
	public boolean			verbTFInSentenceAvrg				= false;
	public boolean			verbTFInSentenceMax					= false;
	public boolean			verbTFInThread						= false;
	public boolean			wordCount							= false;

	public String getArffHead() {
		StringBuilder sb = new StringBuilder(getArffHeadWithoutClassAttribute());
		// Attribute score is the "class" to classify.
		// We use it as the final score in our task scorer.
		if (isNumericClass)
			sb.append("@attribute SCORE numeric\n");
		else {
			if (range == VectorFeatureSelector.SCORE_RANGE_1)
				sb.append("@attribute SCORE {0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0}\n");
			else if (range == VectorFeatureSelector.SCORE_RANGE_5)
				sb.append("@attribute SCORE {-5.0, -4.0, -3.0, -2.0, -1.0, 0.0, 1.0, 2.0, 3.0, 4.0, 5.0}\n");
			else if (range == VectorFeatureSelector.SCORE_RANGE_3)
				sb.append("@attribute SCORE {0.0, 1.0, 2.0, 3.0}\n");
			else if (range == VectorFeatureSelector.SCORE_RANGE_2)
				sb.append("@attribute SCORE {0.0, 1.0}\n");
		}

		sb.append("\n@data\n");
		return sb.toString();
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

		if (destRange == VectorFeatureSelector.SCORE_RANGE_2) {
			return score >= scoreMap[srcRangeIdx][2] ? 1 : 0;
		}

		for (int scoreIdx = 0; scoreIdx < 4; scoreIdx++) {
			if (score == scoreMap[srcRangeIdx][scoreIdx])
				return scoreMap[destRangeIdx][scoreIdx];
		}

		int srcUpper, srcLower, destUpper, destLower;

		switch (srcRange) {
		case VectorFeatureSelector.SCORE_RANGE_5:
			srcUpper = 5;
			srcLower = -5;
			break;
		case VectorFeatureSelector.SCORE_RANGE_3:
			srcUpper = 3;
			srcLower = 0;
			break;
		case VectorFeatureSelector.SCORE_RANGE_1:
		default:
			srcUpper = 1;
			srcLower = 0;
			break;
		}

		switch (destRange) {
		case VectorFeatureSelector.SCORE_RANGE_5:
			destUpper = 5;
			destLower = -5;
			break;
		case VectorFeatureSelector.SCORE_RANGE_3:
			destUpper = 3;
			destLower = 0;
			break;
		case VectorFeatureSelector.SCORE_RANGE_1:
		default:
			destUpper = 1;
			destLower = 0;
			break;
		}

		float normalizedScore = (score - srcLower) * (destUpper - destLower) / (srcUpper - srcLower)
				+ destLower;
		return normalizedScore;
	}

	public String getArffHeadWithoutClassAttribute() {
		StringBuilder sb = new StringBuilder();
		sb.append("@relation task\n\n");
		if (taskFrequency)
			sb.append("@attribute taskFrequency numeric\n");
		if (tfInThread)
			sb.append("@attribute tfInThread numeric\n");
		if (kernelVerbTFInThread)
			sb.append("@attribute kernelVerbTFInThread numeric\n");
		if (kernelNounTFInThread)
			sb.append("@attribute kernelNounTFInThread numeric\n");
		if (verbTFInThread)
			sb.append("@attribute verbTFInThread numeric\n");
		if (nounTFInThread)
			sb.append("@attribute nounTFInThread numeric\n");
		if (similarityWithTasksMax)
			sb.append("@attribute similarityWithTasksMax numeric\n");
		if (similarityWithTasksAvrg)
			sb.append("@attribute similarityWithTasksAvrg  numeric\n");
		if (length)
			sb.append("@attribute length numeric\n");
		if (wordCount)
			sb.append("@attribute wordCount numeric\n");
		if (verbCount)
			sb.append("@attribute verbCount numeric\n");
		if (nounCount)
			sb.append("@attribute nounCount numeric\n");
		if (npWordCount)
			sb.append("@attribute npWordCount numeric\n");
		if (ppWordCount)
			sb.append("@attribute ppWordCount numeric\n");
		if (isVPNP)
			sb.append("@attribute isVPNP {true,false}\n");
		if (isVPNPPP)
			sb.append("@attribute isVPNPPP {true,false}\n");
		if (isVPPP)
			sb.append("@attribute isVPPP  {true,false}\n");
		if (hasPP)
			sb.append("@attribute hasPP {true,false}\n");
		if (proofScoreMax)
			sb.append("@attribute proofScoreMax  numeric\n");
		if (proofScoreAvrg)
			sb.append("@attribute proofScoreAvrg  numeric\n");
		if (isInQuestion)
			sb.append("@attribute isInQuestion {true,false}\n");
		if (isInTitle)
			sb.append("@attribute isInTitle {true,false}\n");
		if (isInAnswer)
			sb.append("@attribute isInAnswer {true,false}\n");
		if (isInAcceptedAnswer)
			sb.append("@attribute isInAcceptedAnswer {true,false}\n");
		if (isInComment)
			sb.append("@attribute isInComment  {true,false}\n");
		if (isInFirstParagraph)
			sb.append("@attribute isInFirstParagraph {true,false}\n");
		if (isInLastParagraph)
			sb.append("@attribute isInLastParagraph  {true,false}\n");
		if (isTheOnlyParagraphInContent)
			sb.append("@attribute isTheOnlyParagraphInContent  {true,false}\n");
		if (paragraphPositionFirst)
			sb.append("@attribute paragraphPositionFirst numeric\n");
		if (paragraphPositionLast)
			sb.append("@attribute paragraphPositionLast  numeric\n");
		if (paragraphPositionAvrg)
			sb.append("@attribute paragraphPositionAvrg  numeric\n");
		if (paragraphRelativePositionFirst)
			sb.append("@attribute paragraphRelativePositionFirst numeric\n");
		if (paragraphRelativePositionLast)
			sb.append("@attribute paragraphRelativePositionLast numeric\n");
		if (paragraphRelativePositionAvrg)
			sb.append("@attribute paragraphRelativePositionAvrg  numeric\n");
		if (isInFirstSentence)
			sb.append("@attribute isInFirstSentence {true,false}\n");
		if (isInLastSentence)
			sb.append("@attribute isInLastSentence  {true,false}\n");
		if (isTheOnlySentenceInParagraph)
			sb.append("@attribute isTheOnlySentenceInParagraph  {true,false}\n");
		if (sentencePositionFirst)
			sb.append("@attribute sentencePositionFirst numeric\n");
		if (sentencePositionLast)
			sb.append("@attribute sentencePositionLast  numeric\n");
		if (sentencePositionAvrg)
			sb.append("@attribute sentencePositionAvrg  numeric\n");
		if (sentenceRelativePositionFirst)
			sb.append("@attribute sentenceRelativePositionFirst numeric\n");
		if (sentenceRelativePositionLast)
			sb.append("@attribute sentenceRelativePositionLast numeric\n");
		if (sentenceRelativePositionAvrg)
			sb.append("@attribute sentenceRelativePositionAvrg  numeric\n");
		if (contextQAWordsImmediate)
			sb.append("@attribute contextQAWordsImmediate {true,false}\n");
		if (contextQAWordsNearBy)
			sb.append("@attribute contextQAWordsNearBy {true,false}\n");
		if (contextQAWordsPreceding)
			sb.append("@attribute contextQAWordsPreceding  {true,false}\n");
		if (isBeforeCode)
			sb.append("@attribute isBeforeCode {true,false}\n");
		if (isAfterCode)
			sb.append("@attribute isAfterCode {true,false}\n");
		if (isNearCode)
			sb.append("@attribute isNearCode  {true,false}\n");
		if (containsCodeInContent)
			sb.append("@attribute containsCodeInContent {true,false}\n");
		if (containsCodeInSentence)
			sb.append("@attribute containsCodeInSentence  {true,false}\n");
		if (contentLengthMax)
			sb.append("@attribute contentLengthMax numeric\n");
		if (contentLengthMin)
			sb.append("@attribute contentLengthMin numeric\n");
		if (contentLengthAvrg)
			sb.append("@attribute contentLengthAvrg  numeric\n");
		if (paragraphLengthMax)
			sb.append("@attribute paragraphLengthMax numeric\n");
		if (paragraphLengthMin)
			sb.append("@attribute paragraphLengthMin numeric\n");
		if (paragraphLengthAvrg)
			sb.append("@attribute paragraphLengthAvrg  numeric\n");
		if (sentenceLengthMax)
			sb.append("@attribute sentenceLengthMax numeric\n");
		if (sentenceLengthMin)
			sb.append("@attribute sentenceLengthMin numeric\n");
		if (sentenceLengthAvrg)
			sb.append("@attribute sentenceLengthAvrg  numeric\n");
		if (tfInQuestion)
			sb.append("@attribute tfInQuestion numeric\n");
		if (tfInAcceptedAnswer)
			sb.append("@attribute tfInAcceptedAnswer numeric\n");
		if (tfInAnswers)
			sb.append("@attribute tfInAnswers numeric\n");
		if (kernelVerbTFInQuestion)
			sb.append("@attribute kernelVerbTFInQuestion numeric\n");
		if (kernelVerbTFInAcceptedAnswer)
			sb.append("@attribute kernelVerbTFInAcceptedAnswer numeric\n");
		if (kernelVerbTFInAnswers)
			sb.append("@attribute kernelVerbTFInAnswers numeric\n");
		if (kernelNounTFInQuestion)
			sb.append("@attribute kernelNounTFInQuestion numeric\n");
		if (kernelNounTFInAcceptedAnswer)
			sb.append("@attribute kernelNounTFInAcceptedAnswer numeric\n");
		if (kernelNounTFInAnswers)
			sb.append("@attribute kernelNounTFInAnswers numeric\n");
		if (verbTFInQuestion)
			sb.append("@attribute verbTFInQuestion numeric\n");
		if (verbTFInAcceptedAnswer)
			sb.append("@attribute verbTFInAcceptedAnswer numeric\n");
		if (verbTFInAnswers)
			sb.append("@attribute verbTFInAnswers numeric\n");
		if (nounTFInQuestion)
			sb.append("@attribute nounTFInQuestion numeric\n");
		if (nounTFInAcceptedAnswer)
			sb.append("@attribute nounTFInAcceptedAnswer numeric\n");
		if (nounTFInAnswers)
			sb.append("@attribute nounTFInAnswers numeric\n");
		if (tfInPostMax)
			sb.append("@attribute tfInPostMax numeric\n");
		if (tfInParagraphMax)
			sb.append("@attribute tfInParagraphMax numeric\n");
		if (tfInSentenceMax)
			sb.append("@attribute tfInSentenceMax  numeric\n");
		if (kernelVerbTFInPostMax)
			sb.append("@attribute kernelVerbTFInPostMax numeric\n");
		if (kernelVerbTFInParagraphMax)
			sb.append("@attribute kernelVerbTFInParagraphMax numeric\n");
		if (kernelVerbTFInSentenceMax)
			sb.append("@attribute kernelVerbTFInSentenceMax  numeric\n");
		if (kernelNounTFInPostMax)
			sb.append("@attribute kernelNounTFInPostMax numeric\n");
		if (kernelNounTFInParagraphMax)
			sb.append("@attribute kernelNounTFInParagraphMax numeric\n");
		if (kernelNounTFInSentenceMax)
			sb.append("@attribute kernelNounTFInSentenceMax  numeric\n");
		if (verbTFInPostMax)
			sb.append("@attribute verbTFInPostMax numeric\n");
		if (verbTFInParagraphMax)
			sb.append("@attribute verbTFInParagraphMax numeric\n");
		if (verbTFInSentenceMax)
			sb.append("@attribute verbTFInSentenceMax  numeric\n");
		if (nounTFInPostMax)
			sb.append("@attribute nounTFInPostMax numeric\n");
		if (nounTFInParagraphMax)
			sb.append("@attribute nounTFInParagraphMax numeric\n");
		if (nounTFInSentenceMax)
			sb.append("@attribute nounTFInSentenceMax  numeric\n");
		if (tfInPostAvrg)
			sb.append("@attribute tfInPostAvrg numeric\n");
		if (tfInParagraphAvrg)
			sb.append("@attribute tfInParagraphAvrg numeric\n");
		if (tfInSentenceAvrg)
			sb.append("@attribute tfInSentenceAvrg  numeric\n");
		if (kernelVerbTFInPostAvrg)
			sb.append("@attribute kernelVerbTFInPostAvrg numeric\n");
		if (kernelVerbTFInParagraphAvrg)
			sb.append("@attribute kernelVerbTFInParagraphAvrg numeric\n");
		if (kernelVerbTFInSentenceAvrg)
			sb.append("@attribute kernelVerbTFInSentenceAvrg  numeric\n");
		if (kernelNounTFInPostAvrg)
			sb.append("@attribute kernelNounTFInPostAvrg numeric\n");
		if (kernelNounTFInParagraphAvrg)
			sb.append("@attribute kernelNounTFInParagraphAvrg numeric\n");
		if (kernelNounTFInSentenceAvrg)
			sb.append("@attribute kernelNounTFInSentenceAvrg  numeric\n");
		if (verbTFInPostAvrg)
			sb.append("@attribute verbTFInPostAvrg numeric\n");
		if (verbTFInParagraphAvrg)
			sb.append("@attribute verbTFInParagraphAvrg numeric\n");
		if (verbTFInSentenceAvrg)
			sb.append("@attribute verbTFInSentenceAvrg  numeric\n");
		if (nounTFInPostAvrg)
			sb.append("@attribute nounTFInPostAvrg numeric\n");
		if (nounTFInParagraphAvrg)
			sb.append("@attribute nounTFInParagraphAvrg numeric\n");
		if (nounTFInSentenceAvrg)
			sb.append("@attribute nounTFInSentenceAvrg  numeric\n");
		if (threadFavorites)
			sb.append("@attribute threadFavorites numeric\n");
		if (threadScores)
			sb.append("@attribute threadScores numeric\n");
		if (threadViews)
			sb.append("@attribute threadViews numeric\n");
		if (threadAnswersCount)
			sb.append("@attribute threadAnswersCount numeric\n");
		if (contentScoreMax)
			sb.append("@attribute contentScoreMax  numeric\n");
		if (contentScoreAvrg)
			sb.append("@attribute contentScoreAvrg  numeric\n");
		if (answerRelativeRankBest)
			sb.append("@attribute answerRelativeRankBest numeric\n");
		if (answerRelativeRankAvrg)
			sb.append("@attribute answerRelativeRankAvrg  numeric\n");
		if (commentCountMax)
			sb.append("@attribute commentCountMax numeric\n");
		if (commentCountMin)
			sb.append("@attribute commentCountMin numeric\n");
		if (commentCountAvrg)
			sb.append("@attribute commentCountAvrg  numeric\n");
		if (isRegisteredUser)
			sb.append("@attribute isRegisteredUser  {true,false}\n");
		if (isEdited)
			sb.append("@attribute isEdited  {true,false}\n");
		if (ownerAcceptedAnswerRateMax)
			sb.append("@attribute ownerAcceptedAnswerRateMax numeric\n");
		if (ownerAcceptedAnswerRateAvrg)
			sb.append("@attribute ownerAcceptedAnswerRateAvrg  numeric\n");
		if (ownerTotalBadges)
			sb.append("@attribute ownerTotalBadges numeric\n");
		if (ownerReputationMax)
			sb.append("@attribute ownerReputationMax numeric\n");
		if (ownerBadges1stMax)
			sb.append("@attribute ownerBadges1stMax numeric\n");
		if (ownerBadges2ndMax)
			sb.append("@attribute ownerBadges2ndMax numeric\n");
		if (ownerBadges3rdMax)
			sb.append("@attribute ownerBadges3rdMax numeric\n");
		if (ownerQuestionsCountMax)
			sb.append("@attribute ownerQuestionsCountMax numeric\n");
		if (ownerAnswersCountMax)
			sb.append("@attribute ownerAnswersCountMax numeric\n");
		if (ownerUpVotesMax)
			sb.append("@attribute ownerUpVotesMax numeric\n");
		if (ownerDownVotesMax)
			sb.append("@attribute ownerDownVotesMax  numeric\n");
		if (ownerReputationAvrg)
			sb.append("@attribute ownerReputationAvrg numeric\n");
		if (ownerBadges1stAvrg)
			sb.append("@attribute ownerBadges1stAvrg numeric\n");
		if (ownerBadges2ndAvrg)
			sb.append("@attribute ownerBadges2ndAvrg numeric\n");
		if (ownerBadges3rdAvrg)
			sb.append("@attribute ownerBadges3rdAvrg numeric\n");
		if (ownerQuestionsCountAvrg)
			sb.append("@attribute ownerQuestionsCountAvrg numeric\n");
		if (ownerAnswersCountAvrg)
			sb.append("@attribute ownerAnswersCountAvrg numeric\n");
		if (ownerUpVotesAvrg)
			sb.append("@attribute ownerUpVotesAvrg numeric\n");
		if (ownerDownVotesAvrg)
			sb.append("@attribute ownerDownVotesAvrg  numeric\n");
		if (ownerReputationSum)
			sb.append("@attribute ownerReputationSum numeric\n");
		if (ownerBadges1stSum)
			sb.append("@attribute ownerBadges1stSum numeric\n");
		if (ownerBadges2ndSum)
			sb.append("@attribute ownerBadges2ndSum numeric\n");
		if (ownerBadges3rdSum)
			sb.append("@attribute ownerBadges3rdSum numeric\n");
		if (ownerQuestionsCountSum)
			sb.append("@attribute ownerQuestionsCountSum numeric\n");
		if (ownerAnswersCountSum)
			sb.append("@attribute ownerAnswersCountSum numeric\n");
		if (ownerUpVotesSum)
			sb.append("@attribute ownerUpVotesSum numeric\n");
		if (ownerDownVotesSum)
			sb.append("@attribute ownerDownVotesSum  numeric\n");
		if (lastEditorReputationMax)
			sb.append("@attribute lastEditorReputationMax numeric\n");
		if (lastEditorBadges1stMax)
			sb.append("@attribute lastEditorBadges1stMax numeric\n");
		if (lastEditorBadges2ndMax)
			sb.append("@attribute lastEditorBadges2ndMax numeric\n");
		if (lastEditorBadges3rdMax)
			sb.append("@attribute lastEditorBadges3rdMax numeric\n");
		if (lastEditorQuestionsCountMax)
			sb.append("@attribute lastEditorQuestionsCountMax numeric\n");
		if (lastEditorAnswersCountMax)
			sb.append("@attribute lastEditorAnswersCountMax numeric\n");
		if (lastEditorUpVotesMax)
			sb.append("@attribute lastEditorUpVotesMax numeric\n");
		if (lastEditorDownVotesMax)
			sb.append("@attribute lastEditorDownVotesMax  numeric\n");
		if (lastEditorReputationAvrg)
			sb.append("@attribute lastEditorReputationAvrg numeric\n");
		if (lastEditorBadges1stAvrg)
			sb.append("@attribute lastEditorBadges1stAvrg numeric\n");
		if (lastEditorBadges2ndAvrg)
			sb.append("@attribute lastEditorBadges2ndAvrg numeric\n");
		if (lastEditorBadges3rdAvrg)
			sb.append("@attribute lastEditorBadges3rdAvrg numeric\n");
		if (lastEditorQuestionsCountAvrg)
			sb.append("@attribute lastEditorQuestionsCountAvrg numeric\n");
		if (lastEditorAnswersCountAvrg)
			sb.append("@attribute lastEditorAnswersCountAvrg numeric\n");
		if (lastEditorUpVotesAvrg)
			sb.append("@attribute lastEditorUpVotesAvrg numeric\n");
		if (lastEditorDownVotesAvrg)
			sb.append("@attribute lastEditorDownVotesAvrg  numeric\n");
		if (lastEditorReputationSum)
			sb.append("@attribute lastEditorReputationSum numeric\n");
		if (lastEditorBadges1stSum)
			sb.append("@attribute lastEditorBadges1stSum numeric\n");
		if (lastEditorBadges2ndSum)
			sb.append("@attribute lastEditorBadges2ndSum numeric\n");
		if (lastEditorBadges3rdSum)
			sb.append("@attribute lastEditorBadges3rdSum numeric\n");
		if (lastEditorQuestionsCountSum)
			sb.append("@attribute lastEditorQuestionsCountSum numeric\n");
		if (lastEditorAnswersCountSum)
			sb.append("@attribute lastEditorAnswersCountSum numeric\n");
		if (lastEditorUpVotesSum)
			sb.append("@attribute lastEditorUpVotesSum numeric\n");
		if (lastEditorDownVotesSum)
			sb.append("@attribute lastEditorDownVotesSum  numeric\n");
		if (lastEditorAcceptedAnswerRateMax)
			sb.append("@attribute lastEditorAcceptedAnswerRateMax numeric\n");
		if (lastEditorAcceptedAnswerRateAvrg)
			sb.append("@attribute lastEditorAcceptedAnswerRateAvrg  numeric\n");

		return sb.toString();
	}

	/**
	 * @param range
	 *            range = 1: {0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1}
	 *            range = 5: {-5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5} range = 3:
	 *            {0, 1, 2, 3} range=2:{1, 0}
	 * @return
	 */
	public String getInstanceData(TaskVector taskVector) {
		StringBuilder sb = new StringBuilder();
		if (taskFrequency)
			sb.append(taskVector.taskFrequency + ", ");
		if (tfInThread)
			sb.append(taskVector.tfInThread + ", ");
		if (kernelVerbTFInThread)
			sb.append(taskVector.kernelVerbTFInThread + ", ");
		if (kernelNounTFInThread)
			sb.append(taskVector.kernelNounTFInThread + ", ");
		if (verbTFInThread)
			sb.append(taskVector.verbTFInThread + ", ");
		if (nounTFInThread)
			sb.append(taskVector.nounTFInThread + ", ");
		if (similarityWithTasksMax)
			sb.append(taskVector.similarityWithTasksMax + ", ");
		if (similarityWithTasksAvrg)
			sb.append(taskVector.similarityWithTasksAvrg + ", ");
		if (length)
			sb.append(taskVector.length + ", ");
		if (wordCount)
			sb.append(taskVector.wordCount + ", ");
		if (verbCount)
			sb.append(taskVector.verbCount + ", ");
		if (nounCount)
			sb.append(taskVector.nounCount + ", ");
		if (npWordCount)
			sb.append(taskVector.npWordCount + ", ");
		if (ppWordCount)
			sb.append(taskVector.ppWordCount + ", ");
		if (isVPNP)
			sb.append(taskVector.isVPNP + ", ");
		if (isVPNPPP)
			sb.append(taskVector.isVPNPPP + ", ");
		if (isVPPP)
			sb.append(taskVector.isVPPP + ", ");
		if (hasPP)
			sb.append(taskVector.isHasPP() + ", ");
		if (proofScoreMax)
			sb.append(taskVector.proofScoreMax + ", ");
		if (proofScoreAvrg)
			sb.append(taskVector.proofScoreAvrg + ", ");
		if (isInQuestion)
			sb.append(taskVector.isInQuestion + ", ");
		if (isInTitle)
			sb.append(taskVector.isInTitle + ", ");
		if (isInAnswer)
			sb.append(taskVector.isInAnswer + ", ");
		if (isInAcceptedAnswer)
			sb.append(taskVector.isInAcceptedAnswer + ", ");
		if (isInComment)
			sb.append(taskVector.isInComment + ", ");
		if (isInFirstParagraph)
			sb.append(taskVector.isInFirstParagraph + ", ");
		if (isInLastParagraph)
			sb.append(taskVector.isInLastParagraph + ", ");
		if (isTheOnlyParagraphInContent)
			sb.append(taskVector.isTheOnlyParagraphInContent + ", ");
		if (paragraphPositionFirst)
			sb.append(taskVector.paragraphPositionFirst + ", ");
		if (paragraphPositionLast)
			sb.append(taskVector.paragraphPositionLast + ", ");
		if (paragraphPositionAvrg)
			sb.append(taskVector.paragraphPositionAvrg + ", ");
		if (paragraphRelativePositionFirst)
			sb.append(taskVector.paragraphRelativePositionFirst + ", ");
		if (paragraphRelativePositionLast)
			sb.append(taskVector.paragraphRelativePositionLast + ", ");
		if (paragraphRelativePositionAvrg)
			sb.append(taskVector.paragraphRelativePositionAvrg + ", ");
		if (isInFirstSentence)
			sb.append(taskVector.isInFirstSentence + ", ");
		if (isInLastSentence)
			sb.append(taskVector.isInLastSentence + ", ");
		if (isTheOnlySentenceInParagraph)
			sb.append(taskVector.isTheOnlySentenceInParagraph + ", ");
		if (sentencePositionFirst)
			sb.append(taskVector.sentencePositionFirst + ", ");
		if (sentencePositionLast)
			sb.append(taskVector.sentencePositionLast + ", ");
		if (sentencePositionAvrg)
			sb.append(taskVector.sentencePositionAvrg + ", ");
		if (sentenceRelativePositionFirst)
			sb.append(taskVector.sentenceRelativePositionFirst + ", ");
		if (sentenceRelativePositionLast)
			sb.append(taskVector.sentenceRelativePositionLast + ", ");
		if (sentenceRelativePositionAvrg)
			sb.append(taskVector.sentenceRelativePositionAvrg + ", ");
		if (contextQAWordsImmediate)
			sb.append(taskVector.contextQAWordsImmediate + ", ");
		if (contextQAWordsNearBy)
			sb.append(taskVector.contextQAWordsNearBy + ", ");
		if (contextQAWordsPreceding)
			sb.append(taskVector.contextQAWordsPreceding + ", ");
		if (isBeforeCode)
			sb.append(taskVector.isBeforeCode + ", ");
		if (isAfterCode)
			sb.append(taskVector.isAfterCode + ", ");
		if (isNearCode)
			sb.append(taskVector.isNearCode() + ", ");
		if (containsCodeInContent)
			sb.append(taskVector.containsCodeInContent + ", ");
		if (containsCodeInSentence)
			sb.append(taskVector.containsCodeInSentence + ", ");
		if (contentLengthMax)
			sb.append(taskVector.contentLengthMax + ", ");
		if (contentLengthMin)
			sb.append(taskVector.contentLengthMin + ", ");
		if (contentLengthAvrg)
			sb.append(taskVector.contentLengthAvrg + ", ");
		if (paragraphLengthMax)
			sb.append(taskVector.paragraphLengthMax + ", ");
		if (paragraphLengthMin)
			sb.append(taskVector.paragraphLengthMin + ", ");
		if (paragraphLengthAvrg)
			sb.append(taskVector.paragraphLengthAvrg + ", ");
		if (sentenceLengthMax)
			sb.append(taskVector.sentenceLengthMax + ", ");
		if (sentenceLengthMin)
			sb.append(taskVector.sentenceLengthMin + ", ");
		if (sentenceLengthAvrg)
			sb.append(taskVector.sentenceLengthAvrg + ", ");
		if (tfInQuestion)
			sb.append(taskVector.tfInQuestion + ", ");
		if (tfInAcceptedAnswer)
			sb.append(taskVector.tfInAcceptedAnswer + ", ");
		if (tfInAnswers)
			sb.append(taskVector.tfInAnswers + ", ");
		if (kernelVerbTFInQuestion)
			sb.append(taskVector.kernelVerbTFInQuestion + ", ");
		if (kernelVerbTFInAcceptedAnswer)
			sb.append(taskVector.kernelVerbTFInAcceptedAnswer + ", ");
		if (kernelVerbTFInAnswers)
			sb.append(taskVector.kernelVerbTFInAnswers + ", ");
		if (kernelNounTFInQuestion)
			sb.append(taskVector.kernelNounTFInQuestion + ", ");
		if (kernelNounTFInAcceptedAnswer)
			sb.append(taskVector.kernelNounTFInAcceptedAnswer + ", ");
		if (kernelNounTFInAnswers)
			sb.append(taskVector.kernelNounTFInAnswers + ", ");
		if (verbTFInQuestion)
			sb.append(taskVector.verbTFInQuestion + ", ");
		if (verbTFInAcceptedAnswer)
			sb.append(taskVector.verbTFInAcceptedAnswer + ", ");
		if (verbTFInAnswers)
			sb.append(taskVector.verbTFInAnswers + ", ");
		if (nounTFInQuestion)
			sb.append(taskVector.nounTFInQuestion + ", ");
		if (nounTFInAcceptedAnswer)
			sb.append(taskVector.nounTFInAcceptedAnswer + ", ");
		if (nounTFInAnswers)
			sb.append(taskVector.nounTFInAnswers + ", ");
		if (tfInPostMax)
			sb.append(taskVector.tfInPostMax + ", ");
		if (tfInParagraphMax)
			sb.append(taskVector.tfInParagraphMax + ", ");
		if (tfInSentenceMax)
			sb.append(taskVector.tfInSentenceMax + ", ");
		if (kernelVerbTFInPostMax)
			sb.append(taskVector.kernelVerbTFInPostMax + ", ");
		if (kernelVerbTFInParagraphMax)
			sb.append(taskVector.kernelVerbTFInParagraphMax + ", ");
		if (kernelVerbTFInSentenceMax)
			sb.append(taskVector.kernelVerbTFInSentenceMax + ", ");
		if (kernelNounTFInPostMax)
			sb.append(taskVector.kernelNounTFInPostMax + ", ");
		if (kernelNounTFInParagraphMax)
			sb.append(taskVector.kernelNounTFInParagraphMax + ", ");
		if (kernelNounTFInSentenceMax)
			sb.append(taskVector.kernelNounTFInSentenceMax + ", ");
		if (verbTFInPostMax)
			sb.append(taskVector.verbTFInPostMax + ", ");
		if (verbTFInParagraphMax)
			sb.append(taskVector.verbTFInParagraphMax + ", ");
		if (verbTFInSentenceMax)
			sb.append(taskVector.verbTFInSentenceMax + ", ");
		if (nounTFInPostMax)
			sb.append(taskVector.nounTFInPostMax + ", ");
		if (nounTFInParagraphMax)
			sb.append(taskVector.nounTFInParagraphMax + ", ");
		if (nounTFInSentenceMax)
			sb.append(taskVector.nounTFInSentenceMax + ", ");
		if (tfInPostAvrg)
			sb.append(taskVector.tfInPostAvrg + ", ");
		if (tfInParagraphAvrg)
			sb.append(taskVector.tfInParagraphAvrg + ", ");
		if (tfInSentenceAvrg)
			sb.append(taskVector.tfInSentenceAvrg + ", ");
		if (kernelVerbTFInPostAvrg)
			sb.append(taskVector.kernelVerbTFInPostAvrg + ", ");
		if (kernelVerbTFInParagraphAvrg)
			sb.append(taskVector.kernelVerbTFInParagraphAvrg + ", ");
		if (kernelVerbTFInSentenceAvrg)
			sb.append(taskVector.kernelVerbTFInSentenceAvrg + ", ");
		if (kernelNounTFInPostAvrg)
			sb.append(taskVector.kernelNounTFInPostAvrg + ", ");
		if (kernelNounTFInParagraphAvrg)
			sb.append(taskVector.kernelNounTFInParagraphAvrg + ", ");
		if (kernelNounTFInSentenceAvrg)
			sb.append(taskVector.kernelNounTFInSentenceAvrg + ", ");
		if (verbTFInPostAvrg)
			sb.append(taskVector.verbTFInPostAvrg + ", ");
		if (verbTFInParagraphAvrg)
			sb.append(taskVector.verbTFInParagraphAvrg + ", ");
		if (verbTFInSentenceAvrg)
			sb.append(taskVector.verbTFInSentenceAvrg + ", ");
		if (nounTFInPostAvrg)
			sb.append(taskVector.nounTFInPostAvrg + ", ");
		if (nounTFInParagraphAvrg)
			sb.append(taskVector.nounTFInParagraphAvrg + ", ");
		if (nounTFInSentenceAvrg)
			sb.append(taskVector.nounTFInSentenceAvrg + ", ");
		if (threadFavorites)
			sb.append(taskVector.threadFavorites + ", ");
		if (threadScores)
			sb.append(taskVector.threadScores + ", ");
		if (threadViews)
			sb.append(taskVector.threadViews + ", ");
		if (threadAnswersCount)
			sb.append(taskVector.threadAnswersCount + ", ");
		if (contentScoreMax)
			sb.append(taskVector.contentScoreMax + ", ");
		if (contentScoreAvrg)
			sb.append(taskVector.contentScoreAvrg + ", ");
		if (answerRelativeRankBest)
			sb.append(taskVector.answerRelativeRankBest + ", ");
		if (answerRelativeRankAvrg)
			sb.append(taskVector.answerRelativeRankAvrg + ", ");
		if (commentCountMax)
			sb.append(taskVector.commentCountMax + ", ");
		if (commentCountMin)
			sb.append(taskVector.commentCountMin + ", ");
		if (commentCountAvrg)
			sb.append(taskVector.commentCountAvrg + ", ");
		if (isRegisteredUser)
			sb.append(taskVector.isRegisteredUser + ", ");
		if (isEdited)
			sb.append(taskVector.isEdited + ", ");
		if (ownerAcceptedAnswerRateMax)
			sb.append(taskVector.ownerAcceptedAnswerRateMax + ", ");
		if (ownerAcceptedAnswerRateAvrg)
			sb.append(taskVector.ownerAcceptedAnswerRateAvrg + ", ");
		if (ownerTotalBadges)
			sb.append(taskVector.getOwnerTotalBadges() + ", ");
		if (ownerReputationMax)
			sb.append(taskVector.ownerReputationMax + ", ");
		if (ownerBadges1stMax)
			sb.append(taskVector.ownerBadges1stMax + ", ");
		if (ownerBadges2ndMax)
			sb.append(taskVector.ownerBadges2ndMax + ", ");
		if (ownerBadges3rdMax)
			sb.append(taskVector.ownerBadges3rdMax + ", ");
		if (ownerQuestionsCountMax)
			sb.append(taskVector.ownerQuestionsCountMax + ", ");
		if (ownerAnswersCountMax)
			sb.append(taskVector.ownerAnswersCountMax + ", ");
		if (ownerUpVotesMax)
			sb.append(taskVector.ownerUpVotesMax + ", ");
		if (ownerDownVotesMax)
			sb.append(taskVector.ownerDownVotesMax + ", ");
		if (ownerReputationAvrg)
			sb.append(taskVector.ownerReputationAvrg + ", ");
		if (ownerBadges1stAvrg)
			sb.append(taskVector.ownerBadges1stAvrg + ", ");
		if (ownerBadges2ndAvrg)
			sb.append(taskVector.ownerBadges2ndAvrg + ", ");
		if (ownerBadges3rdAvrg)
			sb.append(taskVector.ownerBadges3rdAvrg + ", ");
		if (ownerQuestionsCountAvrg)
			sb.append(taskVector.ownerQuestionsCountAvrg + ", ");
		if (ownerAnswersCountAvrg)
			sb.append(taskVector.ownerAnswersCountAvrg + ", ");
		if (ownerUpVotesAvrg)
			sb.append(taskVector.ownerUpVotesAvrg + ", ");
		if (ownerDownVotesAvrg)
			sb.append(taskVector.ownerDownVotesAvrg + ", ");
		if (ownerReputationSum)
			sb.append(taskVector.ownerReputationSum + ", ");
		if (ownerBadges1stSum)
			sb.append(taskVector.ownerBadges1stSum + ", ");
		if (ownerBadges2ndSum)
			sb.append(taskVector.ownerBadges2ndSum + ", ");
		if (ownerBadges3rdSum)
			sb.append(taskVector.ownerBadges3rdSum + ", ");
		if (ownerQuestionsCountSum)
			sb.append(taskVector.ownerQuestionsCountSum + ", ");
		if (ownerAnswersCountSum)
			sb.append(taskVector.ownerAnswersCountSum + ", ");
		if (ownerUpVotesSum)
			sb.append(taskVector.ownerUpVotesSum + ", ");
		if (ownerDownVotesSum)
			sb.append(taskVector.ownerDownVotesSum + ", ");
		if (lastEditorReputationMax)
			sb.append(taskVector.lastEditorReputationMax + ", ");
		if (lastEditorBadges1stMax)
			sb.append(taskVector.lastEditorBadges1stMax + ", ");
		if (lastEditorBadges2ndMax)
			sb.append(taskVector.lastEditorBadges2ndMax + ", ");
		if (lastEditorBadges3rdMax)
			sb.append(taskVector.lastEditorBadges3rdMax + ", ");
		if (lastEditorQuestionsCountMax)
			sb.append(taskVector.lastEditorQuestionsCountMax + ", ");
		if (lastEditorAnswersCountMax)
			sb.append(taskVector.lastEditorAnswersCountMax + ", ");
		if (lastEditorUpVotesMax)
			sb.append(taskVector.lastEditorUpVotesMax + ", ");
		if (lastEditorDownVotesMax)
			sb.append(taskVector.lastEditorDownVotesMax + ", ");
		if (lastEditorReputationAvrg)
			sb.append(taskVector.lastEditorReputationAvrg + ", ");
		if (lastEditorBadges1stAvrg)
			sb.append(taskVector.lastEditorBadges1stAvrg + ", ");
		if (lastEditorBadges2ndAvrg)
			sb.append(taskVector.lastEditorBadges2ndAvrg + ", ");
		if (lastEditorBadges3rdAvrg)
			sb.append(taskVector.lastEditorBadges3rdAvrg + ", ");
		if (lastEditorQuestionsCountAvrg)
			sb.append(taskVector.lastEditorQuestionsCountAvrg + ", ");
		if (lastEditorAnswersCountAvrg)
			sb.append(taskVector.lastEditorAnswersCountAvrg + ", ");
		if (lastEditorUpVotesAvrg)
			sb.append(taskVector.lastEditorUpVotesAvrg + ", ");
		if (lastEditorDownVotesAvrg)
			sb.append(taskVector.lastEditorDownVotesAvrg + ", ");
		if (lastEditorReputationSum)
			sb.append(taskVector.lastEditorReputationSum + ", ");
		if (lastEditorBadges1stSum)
			sb.append(taskVector.lastEditorBadges1stSum + ", ");
		if (lastEditorBadges2ndSum)
			sb.append(taskVector.lastEditorBadges2ndSum + ", ");
		if (lastEditorBadges3rdSum)
			sb.append(taskVector.lastEditorBadges3rdSum + ", ");
		if (lastEditorQuestionsCountSum)
			sb.append(taskVector.lastEditorQuestionsCountSum + ", ");
		if (lastEditorAnswersCountSum)
			sb.append(taskVector.lastEditorAnswersCountSum + ", ");
		if (lastEditorUpVotesSum)
			sb.append(taskVector.lastEditorUpVotesSum + ", ");
		if (lastEditorDownVotesSum)
			sb.append(taskVector.lastEditorDownVotesSum + ", ");
		if (lastEditorAcceptedAnswerRateMax)
			sb.append(taskVector.lastEditorAcceptedAnswerRateMax + ", ");
		if (lastEditorAcceptedAnswerRateAvrg)
			sb.append(taskVector.lastEditorAcceptedAnswerRateAvrg + ", ");

		float score = taskVector.getAnnotatedScore();
		if (range > 0) // -1 means no special range requirement
			score = normalizeAnnotatedScore(score, taskVector.currScoreRange, range);

		if (score <= Integer.MIN_VALUE)
			sb.append("?\n");
		else
			sb.append(score + "\n");
		return sb.toString();
	}
	
	public static VectorFeatureSelector getTermFrequencySelector()
	{
		VectorFeatureSelector selector = new VectorFeatureSelector("TermFrequencySelector");
		selector.taskFrequency = true;
		selector.tfInThread = true;
		selector.kernelVerbTFInThread = true;
		selector.kernelNounTFInThread = true;
		selector.verbTFInThread = true;
		selector.nounTFInThread = true;
		return selector;
	}
	public static VectorFeatureSelector getPageRankSelector()
	{
		VectorFeatureSelector selector = new VectorFeatureSelector("PageRankSelector");
		selector.similarityWithTasksMax = true;
		selector.similarityWithTasksAvrg = true;
		return selector;
	}
	public static VectorFeatureSelector getPhraseSyntaxSelector()
	{
		VectorFeatureSelector selector = new VectorFeatureSelector("PhraseSyntaxSelector");
		selector.wordCount = true;
		selector.verbCount = true;
		selector.nounCount = true;
		selector.npWordCount = true;
		selector.ppWordCount = true;
		selector.isVPNP = true;
		selector.isVPNPPP = true;
		selector.isVPPP = true;
		// selector.hasPP=true;
		selector.proofScoreMax = true;
		selector.proofScoreAvrg = true;
		return selector;
	}
	public static VectorFeatureSelector getPositionSelector()
	{
		VectorFeatureSelector selector = new VectorFeatureSelector("PositionSelector");
		selector.isInQuestion = true;
		selector.isInTitle = true;
		selector.isInAnswer = true;
		selector.isInAcceptedAnswer = true;
		selector.isInComment = true;
		selector.isInFirstParagraph = true;
		selector.isInLastParagraph = true;
		selector.isTheOnlyParagraphInContent = true;
		selector.paragraphPositionFirst = true;
		selector.paragraphPositionLast = true;
		selector.paragraphPositionAvrg = true;
		selector.paragraphRelativePositionFirst = true;
		selector.paragraphRelativePositionLast = true;
		selector.paragraphRelativePositionAvrg = true;
		selector.isInFirstSentence = true;
		selector.isInLastSentence = true;
		selector.isTheOnlySentenceInParagraph = true;
		selector.sentencePositionFirst = true;
		selector.sentencePositionLast = true;
		selector.sentencePositionAvrg = true;
		selector.sentenceRelativePositionFirst = true;
		selector.sentenceRelativePositionLast = true;
		selector.sentenceRelativePositionAvrg = true;
		return selector;
	}
	public static VectorFeatureSelector getContextSelector()
	{
		VectorFeatureSelector selector = new VectorFeatureSelector("ContextSelector");
		selector.contextQAWordsImmediate = true;
		selector.contextQAWordsNearBy = true;
		selector.contextQAWordsPreceding = true;
		selector.isBeforeCode = true;
		selector.isAfterCode = true;
		// selector.isNearCode =true;
		selector.containsCodeInContent = true;
		selector.containsCodeInSentence = true;
		return selector;
	}
	public static VectorFeatureSelector getContextTFSelector()
	{
		VectorFeatureSelector selector = new VectorFeatureSelector("ContextTFSelector");
		selector.tfInQuestion = true;
		selector.tfInAcceptedAnswer = true;
		selector.tfInAnswers = true;
		selector.kernelVerbTFInQuestion = true;
		selector.kernelVerbTFInAcceptedAnswer = true;
		selector.kernelVerbTFInAnswers = true;
		selector.kernelNounTFInQuestion = true;
		selector.kernelNounTFInAcceptedAnswer = true;
		selector.kernelNounTFInAnswers = true;
		selector.verbTFInQuestion = true;
		selector.verbTFInAcceptedAnswer = true;
		selector.verbTFInAnswers = true;
		selector.nounTFInQuestion = true;
		selector.nounTFInAcceptedAnswer = true;
		selector.nounTFInAnswers = true;
		selector.tfInPostMax = true;
		selector.tfInParagraphMax = true;
		selector.tfInSentenceMax = true;
		selector.kernelVerbTFInPostMax = true;
		selector.kernelVerbTFInParagraphMax = true;
		selector.kernelVerbTFInSentenceMax = true;
		selector.kernelNounTFInPostMax = true;
		selector.kernelNounTFInParagraphMax = true;
		selector.kernelNounTFInSentenceMax = true;
		selector.verbTFInPostMax = true;
		selector.verbTFInParagraphMax = true;
		selector.verbTFInSentenceMax = true;
		selector.nounTFInPostMax = true;
		selector.nounTFInParagraphMax = true;
		selector.nounTFInSentenceMax = true;
		selector.tfInPostAvrg = true;
		selector.tfInParagraphAvrg = true;
		selector.tfInSentenceAvrg = true;
		selector.kernelVerbTFInPostAvrg = true;
		selector.kernelVerbTFInParagraphAvrg = true;
		selector.kernelVerbTFInSentenceAvrg = true;
		selector.kernelNounTFInPostAvrg = true;
		selector.kernelNounTFInParagraphAvrg = true;
		selector.kernelNounTFInSentenceAvrg = true;
		selector.verbTFInPostAvrg = true;
		selector.verbTFInParagraphAvrg = true;
		selector.verbTFInSentenceAvrg = true;
		selector.nounTFInPostAvrg = true;
		selector.nounTFInParagraphAvrg = true;
		selector.nounTFInSentenceAvrg = true;
		return selector;
	}
	public static VectorFeatureSelector getStackOverflowMetaSelector()
	{
		VectorFeatureSelector selector = new VectorFeatureSelector("StackOverflowMetaSelector");
		selector.isRegisteredUser = true;
		selector.isEdited = true;
		selector.ownerAcceptedAnswerRateMax = true;
		selector.ownerAcceptedAnswerRateAvrg = true;
		// selector.ownerTotalBadges=true;
		selector.ownerReputationMax = true;
		selector.ownerBadges1stMax = true;
		selector.ownerBadges2ndMax = true;
		selector.ownerBadges3rdMax = true;
		selector.ownerQuestionsCountMax = true;
		selector.ownerAnswersCountMax = true;
		selector.ownerUpVotesMax = true;
		selector.ownerDownVotesMax = true;
		selector.ownerReputationAvrg = true;
		selector.ownerBadges1stAvrg = true;
		selector.ownerBadges2ndAvrg = true;
		selector.ownerBadges3rdAvrg = true;
		selector.ownerQuestionsCountAvrg = true;
		selector.ownerAnswersCountAvrg = true;
		selector.ownerUpVotesAvrg = true;
		selector.ownerDownVotesAvrg = true;
		selector.ownerReputationSum = true;
		selector.ownerBadges1stSum = true;
		selector.ownerBadges2ndSum = true;
		selector.ownerBadges3rdSum = true;
		selector.ownerQuestionsCountSum = true;
		selector.ownerAnswersCountSum = true;
		selector.ownerUpVotesSum = true;
		selector.ownerDownVotesSum = true;
		return selector;
	}
	
	public static VectorFeatureSelector getFullFeatureSelector() {
		VectorFeatureSelector selector = new VectorFeatureSelector("FullSelector");
		selector.taskFrequency = true;
		selector.tfInThread = true;
		selector.kernelVerbTFInThread = true;
		selector.kernelNounTFInThread = true;
		selector.verbTFInThread = true;
		selector.nounTFInThread = true;
		selector.similarityWithTasksMax = true;
		selector.similarityWithTasksAvrg = true;
		selector.length = true;
		selector.wordCount = true;
		selector.verbCount = true;
		selector.nounCount = true;
		selector.npWordCount = true;
		selector.ppWordCount = true;
		selector.isVPNP = true;
		selector.isVPNPPP = true;
		selector.isVPPP = true;
		// selector.hasPP=true;
		selector.proofScoreMax = true;
		selector.proofScoreAvrg = true;
		selector.isInQuestion = true;
		selector.isInTitle = true;
		selector.isInAnswer = true;
		selector.isInAcceptedAnswer = true;
		selector.isInComment = true;
		selector.isInFirstParagraph = true;
		selector.isInLastParagraph = true;
		selector.isTheOnlyParagraphInContent = true;
		selector.paragraphPositionFirst = true;
		selector.paragraphPositionLast = true;
		selector.paragraphPositionAvrg = true;
		selector.paragraphRelativePositionFirst = true;
		selector.paragraphRelativePositionLast = true;
		selector.paragraphRelativePositionAvrg = true;
		selector.isInFirstSentence = true;
		selector.isInLastSentence = true;
		selector.isTheOnlySentenceInParagraph = true;
		selector.sentencePositionFirst = true;
		selector.sentencePositionLast = true;
		selector.sentencePositionAvrg = true;
		selector.sentenceRelativePositionFirst = true;
		selector.sentenceRelativePositionLast = true;
		selector.sentenceRelativePositionAvrg = true;
		selector.contextQAWordsImmediate = true;
		selector.contextQAWordsNearBy = true;
		selector.contextQAWordsPreceding = true;
		selector.isBeforeCode = true;
		selector.isAfterCode = true;
		// selector.isNearCode =true;
		selector.containsCodeInContent = true;
		selector.containsCodeInSentence = true;
		selector.contentLengthMax = true;
		selector.contentLengthMin = true;
		selector.contentLengthAvrg = true;
		selector.paragraphLengthMax = true;
		selector.paragraphLengthMin = true;
		selector.paragraphLengthAvrg = true;
		selector.sentenceLengthMax = true;
		selector.sentenceLengthMin = true;
		selector.sentenceLengthAvrg = true;
		selector.tfInQuestion = true;
		selector.tfInAcceptedAnswer = true;
		selector.tfInAnswers = true;
		selector.kernelVerbTFInQuestion = true;
		selector.kernelVerbTFInAcceptedAnswer = true;
		selector.kernelVerbTFInAnswers = true;
		selector.kernelNounTFInQuestion = true;
		selector.kernelNounTFInAcceptedAnswer = true;
		selector.kernelNounTFInAnswers = true;
		selector.verbTFInQuestion = true;
		selector.verbTFInAcceptedAnswer = true;
		selector.verbTFInAnswers = true;
		selector.nounTFInQuestion = true;
		selector.nounTFInAcceptedAnswer = true;
		selector.nounTFInAnswers = true;
		selector.tfInPostMax = true;
		selector.tfInParagraphMax = true;
		selector.tfInSentenceMax = true;
		selector.kernelVerbTFInPostMax = true;
		selector.kernelVerbTFInParagraphMax = true;
		selector.kernelVerbTFInSentenceMax = true;
		selector.kernelNounTFInPostMax = true;
		selector.kernelNounTFInParagraphMax = true;
		selector.kernelNounTFInSentenceMax = true;
		selector.verbTFInPostMax = true;
		selector.verbTFInParagraphMax = true;
		selector.verbTFInSentenceMax = true;
		selector.nounTFInPostMax = true;
		selector.nounTFInParagraphMax = true;
		selector.nounTFInSentenceMax = true;
		selector.tfInPostAvrg = true;
		selector.tfInParagraphAvrg = true;
		selector.tfInSentenceAvrg = true;
		selector.kernelVerbTFInPostAvrg = true;
		selector.kernelVerbTFInParagraphAvrg = true;
		selector.kernelVerbTFInSentenceAvrg = true;
		selector.kernelNounTFInPostAvrg = true;
		selector.kernelNounTFInParagraphAvrg = true;
		selector.kernelNounTFInSentenceAvrg = true;
		selector.verbTFInPostAvrg = true;
		selector.verbTFInParagraphAvrg = true;
		selector.verbTFInSentenceAvrg = true;
		selector.nounTFInPostAvrg = true;
		selector.nounTFInParagraphAvrg = true;
		selector.nounTFInSentenceAvrg = true;
		selector.threadFavorites = true;
		selector.threadScores = true;
		selector.threadViews = true;
		selector.threadAnswersCount = true;
		selector.contentScoreMax = true;
		selector.contentScoreAvrg = true;
		selector.answerRelativeRankBest = true;
		selector.answerRelativeRankAvrg = true;
		selector.commentCountMax = true;
		selector.commentCountMin = true;
		selector.commentCountAvrg = true;
		selector.isRegisteredUser = true;
		selector.isEdited = true;
		selector.ownerAcceptedAnswerRateMax = true;
		selector.ownerAcceptedAnswerRateAvrg = true;
		// selector.ownerTotalBadges=true;
		selector.ownerReputationMax = true;
		selector.ownerBadges1stMax = true;
		selector.ownerBadges2ndMax = true;
		selector.ownerBadges3rdMax = true;
		selector.ownerQuestionsCountMax = true;
		selector.ownerAnswersCountMax = true;
		selector.ownerUpVotesMax = true;
		selector.ownerDownVotesMax = true;
		selector.ownerReputationAvrg = true;
		selector.ownerBadges1stAvrg = true;
		selector.ownerBadges2ndAvrg = true;
		selector.ownerBadges3rdAvrg = true;
		selector.ownerQuestionsCountAvrg = true;
		selector.ownerAnswersCountAvrg = true;
		selector.ownerUpVotesAvrg = true;
		selector.ownerDownVotesAvrg = true;
		selector.ownerReputationSum = true;
		selector.ownerBadges1stSum = true;
		selector.ownerBadges2ndSum = true;
		selector.ownerBadges3rdSum = true;
		selector.ownerQuestionsCountSum = true;
		selector.ownerAnswersCountSum = true;
		selector.ownerUpVotesSum = true;
		selector.ownerDownVotesSum = true;
		selector.lastEditorReputationMax = true;
		selector.lastEditorBadges1stMax = true;
		selector.lastEditorBadges2ndMax = true;
		selector.lastEditorBadges3rdMax = true;
		selector.lastEditorQuestionsCountMax = true;
		selector.lastEditorAnswersCountMax = true;
		selector.lastEditorUpVotesMax = true;
		selector.lastEditorDownVotesMax = true;
		selector.lastEditorReputationAvrg = true;
		selector.lastEditorBadges1stAvrg = true;
		selector.lastEditorBadges2ndAvrg = true;
		selector.lastEditorBadges3rdAvrg = true;
		selector.lastEditorQuestionsCountAvrg = true;
		selector.lastEditorAnswersCountAvrg = true;
		selector.lastEditorUpVotesAvrg = true;
		selector.lastEditorDownVotesAvrg = true;
		selector.lastEditorReputationSum = true;
		selector.lastEditorBadges1stSum = true;
		selector.lastEditorBadges2ndSum = true;
		selector.lastEditorBadges3rdSum = true;
		selector.lastEditorQuestionsCountSum = true;
		selector.lastEditorAnswersCountSum = true;
		selector.lastEditorUpVotesSum = true;
		selector.lastEditorDownVotesSum = true;
		selector.lastEditorAcceptedAnswerRateMax = true;
		selector.lastEditorAcceptedAnswerRateAvrg = true;
		return selector;
	}

	public static VectorFeatureSelector getShortestFeatureSelector() {
		VectorFeatureSelector selector = new VectorFeatureSelector("ShortSelector");
		selector.tfInThread = true;
		selector.similarityWithTasksAvrg = true;
		selector.hasPP=true;
		selector.proofScoreAvrg = true;
		selector.isInQuestion = true;
		selector.isInTitle = true;
		selector.isInAnswer = true;
		selector.isInAcceptedAnswer = true;
		selector.isInFirstParagraph = true;
		selector.isInLastParagraph = true;
		selector.isTheOnlyParagraphInContent = true;
		selector.isInFirstSentence = true;
		selector.isInLastSentence = true;
		selector.isTheOnlySentenceInParagraph = true;
		selector.contextQAWordsImmediate = true;
		selector.contextQAWordsNearBy = true;
		selector.contextQAWordsPreceding = true;
		selector.contentScoreMax = true;
		selector.answerRelativeRankBest = true;
		selector.ownerReputationMax = true;
		return selector;
	}
	
	public static VectorFeatureSelector getMediumFeatureSelector() {
		VectorFeatureSelector selector = new VectorFeatureSelector("MediumSelector");
		selector.taskFrequency = true;
		selector.tfInThread = true;
		selector.kernelVerbTFInThread = true;
		selector.kernelNounTFInThread = true;
		selector.similarityWithTasksMax = true;
		selector.similarityWithTasksAvrg = true;
		selector.wordCount = true;
		selector.hasPP=true;
		selector.proofScoreMax = true;
		selector.proofScoreAvrg = true;
		selector.isInQuestion = true;
		selector.isInTitle = true;
		selector.isInAnswer = true;
		selector.isInAcceptedAnswer = true;
		selector.isInFirstParagraph = true;
		selector.isInLastParagraph = true;
		selector.isTheOnlyParagraphInContent = true;
		selector.isInFirstSentence = true;
		selector.isInLastSentence = true;
		selector.isTheOnlySentenceInParagraph = true;
		selector.contextQAWordsImmediate = true;
		selector.contextQAWordsNearBy = true;
		selector.contextQAWordsPreceding = true;
		selector.isNearCode =true;
		selector.containsCodeInContent = true;
		selector.containsCodeInSentence = true;
		selector.tfInPostMax = true;
		selector.kernelVerbTFInPostMax = true;
		selector.kernelNounTFInPostMax = true;
		selector.contentScoreMax = true;
		selector.contentScoreAvrg = true;
		selector.answerRelativeRankBest = true;
		selector.answerRelativeRankAvrg = true;
		selector.ownerAcceptedAnswerRateMax = true;
		selector.ownerTotalBadges=true;
		selector.ownerReputationMax = true;
		return selector;
	}
	
	public static VectorFeatureSelector getLongFeatureSelector() {
		VectorFeatureSelector selector = new VectorFeatureSelector("LongSelector");
		selector.taskFrequency = true;
		selector.tfInThread = true;
		selector.kernelVerbTFInThread = true;
		selector.kernelNounTFInThread = true;
		selector.verbTFInThread = true;
		selector.nounTFInThread = true;
		selector.similarityWithTasksMax = true;
		selector.similarityWithTasksAvrg = true;
		selector.wordCount = true;
		selector.verbCount = true;
		selector.nounCount = true;
		selector.npWordCount = true;
		selector.ppWordCount = true;
		selector.isVPNP = true;
		selector.isVPNPPP = true;
		selector.isVPPP = true;
		selector.proofScoreMax = true;
		selector.proofScoreAvrg = true;
		selector.isInQuestion = true;
		selector.isInTitle = true;
		selector.isInAnswer = true;
		selector.isInAcceptedAnswer = true;
		selector.isInComment = true;
		selector.isInFirstParagraph = true;
		selector.isInLastParagraph = true;
		selector.isTheOnlyParagraphInContent = true;
		selector.paragraphPositionAvrg = true;
		selector.paragraphRelativePositionAvrg = true;
		selector.isInFirstSentence = true;
		selector.isInLastSentence = true;
		selector.isTheOnlySentenceInParagraph = true;
		selector.sentencePositionAvrg = true;
		selector.sentenceRelativePositionAvrg = true;
		selector.contextQAWordsImmediate = true;
		selector.contextQAWordsNearBy = true;
		selector.contextQAWordsPreceding = true;
		selector.isNearCode =true;
		selector.containsCodeInContent = true;
		selector.containsCodeInSentence = true;
		selector.tfInQuestion = true;
		selector.tfInAcceptedAnswer = true;
		selector.tfInAnswers = true;
		selector.kernelVerbTFInQuestion = true;
		selector.kernelVerbTFInAcceptedAnswer = true;
		selector.kernelVerbTFInAnswers = true;
		selector.kernelNounTFInQuestion = true;
		selector.kernelNounTFInAcceptedAnswer = true;
		selector.kernelNounTFInAnswers = true;
		selector.verbTFInQuestion = true;
		selector.verbTFInAcceptedAnswer = true;
		selector.verbTFInAnswers = true;
		selector.nounTFInQuestion = true;
		selector.nounTFInAcceptedAnswer = true;
		selector.nounTFInAnswers = true;
		selector.tfInPostMax = true;
		selector.kernelVerbTFInPostMax = true;
		selector.kernelNounTFInPostMax = true;
		selector.verbTFInPostMax = true;
		selector.nounTFInPostMax = true;
		selector.contentScoreMax = true;
		selector.contentScoreAvrg = true;
		selector.answerRelativeRankBest = true;
		selector.answerRelativeRankAvrg = true;
		selector.commentCountAvrg = true;
		selector.ownerAcceptedAnswerRateMax = true;
		selector.ownerAcceptedAnswerRateAvrg = true;
		selector.ownerTotalBadges=true;
		selector.ownerReputationMax = true;
		selector.ownerAnswersCountMax = true;
		selector.ownerReputationAvrg = true;
		return selector;
	}
	
	public static VectorFeatureSelector getTFBaselineFeatureSelector() {
		VectorFeatureSelector selector = new VectorFeatureSelector("TFBaselineSelector");
		selector.taskFrequency = true;
		selector.tfInThread = true;
		return selector;
	}
	
	public static VectorFeatureSelector combineSelector(VectorFeatureSelector s1, VectorFeatureSelector s2) {
		VectorFeatureSelector selector = new VectorFeatureSelector();
		selector.name=s1.name+"+"+s2.name;
		
		selector.range=s1.range;
		selector.isNumericClass=s1.isNumericClass && s2.isNumericClass;
		
		selector.taskFrequency = s1.taskFrequency|| s2.taskFrequency;
		selector.tfInThread = s1.tfInThread|| s2.tfInThread;
		selector.kernelVerbTFInThread = s1.kernelVerbTFInThread|| s2.kernelVerbTFInThread;
		selector.kernelNounTFInThread = s1.kernelNounTFInThread|| s2.kernelNounTFInThread;
		selector.verbTFInThread = s1.verbTFInThread|| s2.verbTFInThread;
		selector.nounTFInThread = s1.nounTFInThread|| s2.nounTFInThread;
		selector.similarityWithTasksMax = s1.similarityWithTasksMax|| s2.similarityWithTasksMax;
		selector.similarityWithTasksAvrg  = s1.similarityWithTasksAvrg || s2.similarityWithTasksAvrg ;
		selector.length = s1.length|| s2.length;
		selector.wordCount = s1.wordCount|| s2.wordCount;
		selector.verbCount = s1.verbCount|| s2.verbCount;
		selector.nounCount = s1.nounCount|| s2.nounCount;
		selector.npWordCount = s1.npWordCount|| s2.npWordCount;
		selector.ppWordCount = s1.ppWordCount|| s2.ppWordCount;
		selector.isVPNP = s1.isVPNP|| s2.isVPNP;
		selector.isVPNPPP = s1.isVPNPPP|| s2.isVPNPPP;
		selector.isVPPP  = s1.isVPPP || s2.isVPPP ;
		selector.hasPP = s1.hasPP|| s2.hasPP;
		selector.proofScoreMax  = s1.proofScoreMax || s2.proofScoreMax ;
		selector.proofScoreAvrg  = s1.proofScoreAvrg || s2.proofScoreAvrg ;
		selector.isInQuestion = s1.isInQuestion|| s2.isInQuestion;
		selector.isInTitle = s1.isInTitle|| s2.isInTitle;
		selector.isInAnswer = s1.isInAnswer|| s2.isInAnswer;
		selector.isInAcceptedAnswer = s1.isInAcceptedAnswer|| s2.isInAcceptedAnswer;
		selector.isInComment  = s1.isInComment || s2.isInComment ;
		selector.isInFirstParagraph = s1.isInFirstParagraph|| s2.isInFirstParagraph;
		selector.isInLastParagraph  = s1.isInLastParagraph || s2.isInLastParagraph ;
		selector.isTheOnlyParagraphInContent  = s1.isTheOnlyParagraphInContent || s2.isTheOnlyParagraphInContent ;
		selector.paragraphPositionFirst = s1.paragraphPositionFirst|| s2.paragraphPositionFirst;
		selector.paragraphPositionLast  = s1.paragraphPositionLast || s2.paragraphPositionLast ;
		selector.paragraphPositionAvrg  = s1.paragraphPositionAvrg || s2.paragraphPositionAvrg ;
		selector.paragraphRelativePositionFirst = s1.paragraphRelativePositionFirst|| s2.paragraphRelativePositionFirst;
		selector.paragraphRelativePositionLast = s1.paragraphRelativePositionLast|| s2.paragraphRelativePositionLast;
		selector.paragraphRelativePositionAvrg  = s1.paragraphRelativePositionAvrg || s2.paragraphRelativePositionAvrg ;
		selector.isInFirstSentence = s1.isInFirstSentence|| s2.isInFirstSentence;
		selector.isInLastSentence  = s1.isInLastSentence || s2.isInLastSentence ;
		selector.isTheOnlySentenceInParagraph  = s1.isTheOnlySentenceInParagraph || s2.isTheOnlySentenceInParagraph ;
		selector.sentencePositionFirst = s1.sentencePositionFirst|| s2.sentencePositionFirst;
		selector.sentencePositionLast  = s1.sentencePositionLast || s2.sentencePositionLast ;
		selector.sentencePositionAvrg  = s1.sentencePositionAvrg || s2.sentencePositionAvrg ;
		selector.sentenceRelativePositionFirst = s1.sentenceRelativePositionFirst|| s2.sentenceRelativePositionFirst;
		selector.sentenceRelativePositionLast = s1.sentenceRelativePositionLast|| s2.sentenceRelativePositionLast;
		selector.sentenceRelativePositionAvrg  = s1.sentenceRelativePositionAvrg || s2.sentenceRelativePositionAvrg ;
		selector.contextQAWordsImmediate = s1.contextQAWordsImmediate|| s2.contextQAWordsImmediate;
		selector.contextQAWordsNearBy = s1.contextQAWordsNearBy|| s2.contextQAWordsNearBy;
		selector.contextQAWordsPreceding  = s1.contextQAWordsPreceding || s2.contextQAWordsPreceding ;
		selector.isBeforeCode = s1.isBeforeCode|| s2.isBeforeCode;
		selector.isAfterCode = s1.isAfterCode|| s2.isAfterCode;
		selector.isNearCode  = s1.isNearCode || s2.isNearCode ;
		selector.containsCodeInContent = s1.containsCodeInContent|| s2.containsCodeInContent;
		selector.containsCodeInSentence  = s1.containsCodeInSentence || s2.containsCodeInSentence ;
		selector.contentLengthMax = s1.contentLengthMax|| s2.contentLengthMax;
		selector.contentLengthMin = s1.contentLengthMin|| s2.contentLengthMin;
		selector.contentLengthAvrg  = s1.contentLengthAvrg || s2.contentLengthAvrg ;
		selector.paragraphLengthMax = s1.paragraphLengthMax|| s2.paragraphLengthMax;
		selector.paragraphLengthMin = s1.paragraphLengthMin|| s2.paragraphLengthMin;
		selector.paragraphLengthAvrg  = s1.paragraphLengthAvrg || s2.paragraphLengthAvrg ;
		selector.sentenceLengthMax = s1.sentenceLengthMax|| s2.sentenceLengthMax;
		selector.sentenceLengthMin = s1.sentenceLengthMin|| s2.sentenceLengthMin;
		selector.sentenceLengthAvrg  = s1.sentenceLengthAvrg || s2.sentenceLengthAvrg ;
		selector.tfInQuestion = s1.tfInQuestion|| s2.tfInQuestion;
		selector.tfInAcceptedAnswer = s1.tfInAcceptedAnswer|| s2.tfInAcceptedAnswer;
		selector.tfInAnswers = s1.tfInAnswers|| s2.tfInAnswers;
		selector.kernelVerbTFInQuestion = s1.kernelVerbTFInQuestion|| s2.kernelVerbTFInQuestion;
		selector.kernelVerbTFInAcceptedAnswer = s1.kernelVerbTFInAcceptedAnswer|| s2.kernelVerbTFInAcceptedAnswer;
		selector.kernelVerbTFInAnswers = s1.kernelVerbTFInAnswers|| s2.kernelVerbTFInAnswers;
		selector.kernelNounTFInQuestion = s1.kernelNounTFInQuestion|| s2.kernelNounTFInQuestion;
		selector.kernelNounTFInAcceptedAnswer = s1.kernelNounTFInAcceptedAnswer|| s2.kernelNounTFInAcceptedAnswer;
		selector.kernelNounTFInAnswers = s1.kernelNounTFInAnswers|| s2.kernelNounTFInAnswers;
		selector.verbTFInQuestion = s1.verbTFInQuestion|| s2.verbTFInQuestion;
		selector.verbTFInAcceptedAnswer = s1.verbTFInAcceptedAnswer|| s2.verbTFInAcceptedAnswer;
		selector.verbTFInAnswers = s1.verbTFInAnswers|| s2.verbTFInAnswers;
		selector.nounTFInQuestion = s1.nounTFInQuestion|| s2.nounTFInQuestion;
		selector.nounTFInAcceptedAnswer = s1.nounTFInAcceptedAnswer|| s2.nounTFInAcceptedAnswer;
		selector.nounTFInAnswers = s1.nounTFInAnswers|| s2.nounTFInAnswers;
		selector.tfInPostMax = s1.tfInPostMax|| s2.tfInPostMax;
		selector.tfInParagraphMax = s1.tfInParagraphMax|| s2.tfInParagraphMax;
		selector.tfInSentenceMax  = s1.tfInSentenceMax || s2.tfInSentenceMax ;
		selector.kernelVerbTFInPostMax = s1.kernelVerbTFInPostMax|| s2.kernelVerbTFInPostMax;
		selector.kernelVerbTFInParagraphMax = s1.kernelVerbTFInParagraphMax|| s2.kernelVerbTFInParagraphMax;
		selector.kernelVerbTFInSentenceMax  = s1.kernelVerbTFInSentenceMax || s2.kernelVerbTFInSentenceMax ;
		selector.kernelNounTFInPostMax = s1.kernelNounTFInPostMax|| s2.kernelNounTFInPostMax;
		selector.kernelNounTFInParagraphMax = s1.kernelNounTFInParagraphMax|| s2.kernelNounTFInParagraphMax;
		selector.kernelNounTFInSentenceMax  = s1.kernelNounTFInSentenceMax || s2.kernelNounTFInSentenceMax ;
		selector.verbTFInPostMax = s1.verbTFInPostMax|| s2.verbTFInPostMax;
		selector.verbTFInParagraphMax = s1.verbTFInParagraphMax|| s2.verbTFInParagraphMax;
		selector.verbTFInSentenceMax  = s1.verbTFInSentenceMax || s2.verbTFInSentenceMax ;
		selector.nounTFInPostMax = s1.nounTFInPostMax|| s2.nounTFInPostMax;
		selector.nounTFInParagraphMax = s1.nounTFInParagraphMax|| s2.nounTFInParagraphMax;
		selector.nounTFInSentenceMax  = s1.nounTFInSentenceMax || s2.nounTFInSentenceMax ;
		selector.tfInPostAvrg = s1.tfInPostAvrg|| s2.tfInPostAvrg;
		selector.tfInParagraphAvrg = s1.tfInParagraphAvrg|| s2.tfInParagraphAvrg;
		selector.tfInSentenceAvrg  = s1.tfInSentenceAvrg || s2.tfInSentenceAvrg ;
		selector.kernelVerbTFInPostAvrg = s1.kernelVerbTFInPostAvrg|| s2.kernelVerbTFInPostAvrg;
		selector.kernelVerbTFInParagraphAvrg = s1.kernelVerbTFInParagraphAvrg|| s2.kernelVerbTFInParagraphAvrg;
		selector.kernelVerbTFInSentenceAvrg  = s1.kernelVerbTFInSentenceAvrg || s2.kernelVerbTFInSentenceAvrg ;
		selector.kernelNounTFInPostAvrg = s1.kernelNounTFInPostAvrg|| s2.kernelNounTFInPostAvrg;
		selector.kernelNounTFInParagraphAvrg = s1.kernelNounTFInParagraphAvrg|| s2.kernelNounTFInParagraphAvrg;
		selector.kernelNounTFInSentenceAvrg  = s1.kernelNounTFInSentenceAvrg || s2.kernelNounTFInSentenceAvrg ;
		selector.verbTFInPostAvrg = s1.verbTFInPostAvrg|| s2.verbTFInPostAvrg;
		selector.verbTFInParagraphAvrg = s1.verbTFInParagraphAvrg|| s2.verbTFInParagraphAvrg;
		selector.verbTFInSentenceAvrg  = s1.verbTFInSentenceAvrg || s2.verbTFInSentenceAvrg ;
		selector.nounTFInPostAvrg = s1.nounTFInPostAvrg|| s2.nounTFInPostAvrg;
		selector.nounTFInParagraphAvrg = s1.nounTFInParagraphAvrg|| s2.nounTFInParagraphAvrg;
		selector.nounTFInSentenceAvrg  = s1.nounTFInSentenceAvrg || s2.nounTFInSentenceAvrg ;
		selector.threadFavorites = s1.threadFavorites|| s2.threadFavorites;
		selector.threadScores = s1.threadScores|| s2.threadScores;
		selector.threadViews = s1.threadViews|| s2.threadViews;
		selector.threadAnswersCount = s1.threadAnswersCount|| s2.threadAnswersCount;
		selector.contentScoreMax  = s1.contentScoreMax || s2.contentScoreMax ;
		selector.contentScoreAvrg  = s1.contentScoreAvrg || s2.contentScoreAvrg ;
		selector.answerRelativeRankBest = s1.answerRelativeRankBest|| s2.answerRelativeRankBest;
		selector.answerRelativeRankAvrg  = s1.answerRelativeRankAvrg || s2.answerRelativeRankAvrg ;
		selector.commentCountMax = s1.commentCountMax|| s2.commentCountMax;
		selector.commentCountMin = s1.commentCountMin|| s2.commentCountMin;
		selector.commentCountAvrg  = s1.commentCountAvrg || s2.commentCountAvrg ;
		selector.isRegisteredUser  = s1.isRegisteredUser || s2.isRegisteredUser ;
		selector.isEdited  = s1.isEdited || s2.isEdited ;
		selector.ownerAcceptedAnswerRateMax = s1.ownerAcceptedAnswerRateMax|| s2.ownerAcceptedAnswerRateMax;
		selector.ownerAcceptedAnswerRateAvrg  = s1.ownerAcceptedAnswerRateAvrg || s2.ownerAcceptedAnswerRateAvrg ;
		selector.ownerTotalBadges = s1.ownerTotalBadges|| s2.ownerTotalBadges;
		selector.ownerReputationMax = s1.ownerReputationMax|| s2.ownerReputationMax;
		selector.ownerBadges1stMax = s1.ownerBadges1stMax|| s2.ownerBadges1stMax;
		selector.ownerBadges2ndMax = s1.ownerBadges2ndMax|| s2.ownerBadges2ndMax;
		selector.ownerBadges3rdMax = s1.ownerBadges3rdMax|| s2.ownerBadges3rdMax;
		selector.ownerQuestionsCountMax = s1.ownerQuestionsCountMax|| s2.ownerQuestionsCountMax;
		selector.ownerAnswersCountMax = s1.ownerAnswersCountMax|| s2.ownerAnswersCountMax;
		selector.ownerUpVotesMax = s1.ownerUpVotesMax|| s2.ownerUpVotesMax;
		selector.ownerDownVotesMax  = s1.ownerDownVotesMax || s2.ownerDownVotesMax ;
		selector.ownerReputationAvrg = s1.ownerReputationAvrg|| s2.ownerReputationAvrg;
		selector.ownerBadges1stAvrg = s1.ownerBadges1stAvrg|| s2.ownerBadges1stAvrg;
		selector.ownerBadges2ndAvrg = s1.ownerBadges2ndAvrg|| s2.ownerBadges2ndAvrg;
		selector.ownerBadges3rdAvrg = s1.ownerBadges3rdAvrg|| s2.ownerBadges3rdAvrg;
		selector.ownerQuestionsCountAvrg = s1.ownerQuestionsCountAvrg|| s2.ownerQuestionsCountAvrg;
		selector.ownerAnswersCountAvrg = s1.ownerAnswersCountAvrg|| s2.ownerAnswersCountAvrg;
		selector.ownerUpVotesAvrg = s1.ownerUpVotesAvrg|| s2.ownerUpVotesAvrg;
		selector.ownerDownVotesAvrg  = s1.ownerDownVotesAvrg || s2.ownerDownVotesAvrg ;
		selector.ownerReputationSum = s1.ownerReputationSum|| s2.ownerReputationSum;
		selector.ownerBadges1stSum = s1.ownerBadges1stSum|| s2.ownerBadges1stSum;
		selector.ownerBadges2ndSum = s1.ownerBadges2ndSum|| s2.ownerBadges2ndSum;
		selector.ownerBadges3rdSum = s1.ownerBadges3rdSum|| s2.ownerBadges3rdSum;
		selector.ownerQuestionsCountSum = s1.ownerQuestionsCountSum|| s2.ownerQuestionsCountSum;
		selector.ownerAnswersCountSum = s1.ownerAnswersCountSum|| s2.ownerAnswersCountSum;
		selector.ownerUpVotesSum = s1.ownerUpVotesSum|| s2.ownerUpVotesSum;
		selector.ownerDownVotesSum  = s1.ownerDownVotesSum || s2.ownerDownVotesSum ;
		selector.lastEditorReputationMax = s1.lastEditorReputationMax|| s2.lastEditorReputationMax;
		selector.lastEditorBadges1stMax = s1.lastEditorBadges1stMax|| s2.lastEditorBadges1stMax;
		selector.lastEditorBadges2ndMax = s1.lastEditorBadges2ndMax|| s2.lastEditorBadges2ndMax;
		selector.lastEditorBadges3rdMax = s1.lastEditorBadges3rdMax|| s2.lastEditorBadges3rdMax;
		selector.lastEditorQuestionsCountMax = s1.lastEditorQuestionsCountMax|| s2.lastEditorQuestionsCountMax;
		selector.lastEditorAnswersCountMax = s1.lastEditorAnswersCountMax|| s2.lastEditorAnswersCountMax;
		selector.lastEditorUpVotesMax = s1.lastEditorUpVotesMax|| s2.lastEditorUpVotesMax;
		selector.lastEditorDownVotesMax  = s1.lastEditorDownVotesMax || s2.lastEditorDownVotesMax ;
		selector.lastEditorReputationAvrg = s1.lastEditorReputationAvrg|| s2.lastEditorReputationAvrg;
		selector.lastEditorBadges1stAvrg = s1.lastEditorBadges1stAvrg|| s2.lastEditorBadges1stAvrg;
		selector.lastEditorBadges2ndAvrg = s1.lastEditorBadges2ndAvrg|| s2.lastEditorBadges2ndAvrg;
		selector.lastEditorBadges3rdAvrg = s1.lastEditorBadges3rdAvrg|| s2.lastEditorBadges3rdAvrg;
		selector.lastEditorQuestionsCountAvrg = s1.lastEditorQuestionsCountAvrg|| s2.lastEditorQuestionsCountAvrg;
		selector.lastEditorAnswersCountAvrg = s1.lastEditorAnswersCountAvrg|| s2.lastEditorAnswersCountAvrg;
		selector.lastEditorUpVotesAvrg = s1.lastEditorUpVotesAvrg|| s2.lastEditorUpVotesAvrg;
		selector.lastEditorDownVotesAvrg  = s1.lastEditorDownVotesAvrg || s2.lastEditorDownVotesAvrg ;
		selector.lastEditorReputationSum = s1.lastEditorReputationSum|| s2.lastEditorReputationSum;
		selector.lastEditorBadges1stSum = s1.lastEditorBadges1stSum|| s2.lastEditorBadges1stSum;
		selector.lastEditorBadges2ndSum = s1.lastEditorBadges2ndSum|| s2.lastEditorBadges2ndSum;
		selector.lastEditorBadges3rdSum = s1.lastEditorBadges3rdSum|| s2.lastEditorBadges3rdSum;
		selector.lastEditorQuestionsCountSum = s1.lastEditorQuestionsCountSum|| s2.lastEditorQuestionsCountSum;
		selector.lastEditorAnswersCountSum = s1.lastEditorAnswersCountSum|| s2.lastEditorAnswersCountSum;
		selector.lastEditorUpVotesSum = s1.lastEditorUpVotesSum|| s2.lastEditorUpVotesSum;
		selector.lastEditorDownVotesSum  = s1.lastEditorDownVotesSum || s2.lastEditorDownVotesSum ;
		selector.lastEditorAcceptedAnswerRateMax = s1.lastEditorAcceptedAnswerRateMax|| s2.lastEditorAcceptedAnswerRateMax;
		selector.lastEditorAcceptedAnswerRateAvrg  = s1.lastEditorAcceptedAnswerRateAvrg || s2.lastEditorAcceptedAnswerRateAvrg ;

		return selector;
	}
	
	@Override
	public VectorFeatureSelector clone() {
		VectorFeatureSelector selector=new VectorFeatureSelector(this.name);
		selector.range=this.range;
		selector.isNumericClass=this.isNumericClass;
		selector.taskFrequency = this.taskFrequency;
		selector.tfInThread = this.tfInThread;
		selector.kernelVerbTFInThread = this.kernelVerbTFInThread;
		selector.kernelNounTFInThread = this.kernelNounTFInThread;
		selector.verbTFInThread = this.verbTFInThread;
		selector.nounTFInThread = this.nounTFInThread;
		selector.similarityWithTasksMax = this.similarityWithTasksMax;
		selector.similarityWithTasksAvrg  = this.similarityWithTasksAvrg ;
		selector.length = this.length;
		selector.wordCount = this.wordCount;
		selector.verbCount = this.verbCount;
		selector.nounCount = this.nounCount;
		selector.npWordCount = this.npWordCount;
		selector.ppWordCount = this.ppWordCount;
		selector.isVPNP = this.isVPNP;
		selector.isVPNPPP = this.isVPNPPP;
		selector.isVPPP  = this.isVPPP ;
		selector.hasPP = this.hasPP;
		selector.proofScoreMax  = this.proofScoreMax ;
		selector.proofScoreAvrg  = this.proofScoreAvrg ;
		selector.isInQuestion = this.isInQuestion;
		selector.isInTitle = this.isInTitle;
		selector.isInAnswer = this.isInAnswer;
		selector.isInAcceptedAnswer = this.isInAcceptedAnswer;
		selector.isInComment  = this.isInComment ;
		selector.isInFirstParagraph = this.isInFirstParagraph;
		selector.isInLastParagraph  = this.isInLastParagraph ;
		selector.isTheOnlyParagraphInContent  = this.isTheOnlyParagraphInContent ;
		selector.paragraphPositionFirst = this.paragraphPositionFirst;
		selector.paragraphPositionLast  = this.paragraphPositionLast ;
		selector.paragraphPositionAvrg  = this.paragraphPositionAvrg ;
		selector.paragraphRelativePositionFirst = this.paragraphRelativePositionFirst;
		selector.paragraphRelativePositionLast = this.paragraphRelativePositionLast;
		selector.paragraphRelativePositionAvrg  = this.paragraphRelativePositionAvrg ;
		selector.isInFirstSentence = this.isInFirstSentence;
		selector.isInLastSentence  = this.isInLastSentence ;
		selector.isTheOnlySentenceInParagraph  = this.isTheOnlySentenceInParagraph ;
		selector.sentencePositionFirst = this.sentencePositionFirst;
		selector.sentencePositionLast  = this.sentencePositionLast ;
		selector.sentencePositionAvrg  = this.sentencePositionAvrg ;
		selector.sentenceRelativePositionFirst = this.sentenceRelativePositionFirst;
		selector.sentenceRelativePositionLast = this.sentenceRelativePositionLast;
		selector.sentenceRelativePositionAvrg  = this.sentenceRelativePositionAvrg ;
		selector.contextQAWordsImmediate = this.contextQAWordsImmediate;
		selector.contextQAWordsNearBy = this.contextQAWordsNearBy;
		selector.contextQAWordsPreceding  = this.contextQAWordsPreceding ;
		selector.isBeforeCode = this.isBeforeCode;
		selector.isAfterCode = this.isAfterCode;
		selector.isNearCode  = this.isNearCode ;
		selector.containsCodeInContent = this.containsCodeInContent;
		selector.containsCodeInSentence  = this.containsCodeInSentence ;
		selector.contentLengthMax = this.contentLengthMax;
		selector.contentLengthMin = this.contentLengthMin;
		selector.contentLengthAvrg  = this.contentLengthAvrg ;
		selector.paragraphLengthMax = this.paragraphLengthMax;
		selector.paragraphLengthMin = this.paragraphLengthMin;
		selector.paragraphLengthAvrg  = this.paragraphLengthAvrg ;
		selector.sentenceLengthMax = this.sentenceLengthMax;
		selector.sentenceLengthMin = this.sentenceLengthMin;
		selector.sentenceLengthAvrg  = this.sentenceLengthAvrg ;
		selector.tfInQuestion = this.tfInQuestion;
		selector.tfInAcceptedAnswer = this.tfInAcceptedAnswer;
		selector.tfInAnswers = this.tfInAnswers;
		selector.kernelVerbTFInQuestion = this.kernelVerbTFInQuestion;
		selector.kernelVerbTFInAcceptedAnswer = this.kernelVerbTFInAcceptedAnswer;
		selector.kernelVerbTFInAnswers = this.kernelVerbTFInAnswers;
		selector.kernelNounTFInQuestion = this.kernelNounTFInQuestion;
		selector.kernelNounTFInAcceptedAnswer = this.kernelNounTFInAcceptedAnswer;
		selector.kernelNounTFInAnswers = this.kernelNounTFInAnswers;
		selector.verbTFInQuestion = this.verbTFInQuestion;
		selector.verbTFInAcceptedAnswer = this.verbTFInAcceptedAnswer;
		selector.verbTFInAnswers = this.verbTFInAnswers;
		selector.nounTFInQuestion = this.nounTFInQuestion;
		selector.nounTFInAcceptedAnswer = this.nounTFInAcceptedAnswer;
		selector.nounTFInAnswers = this.nounTFInAnswers;
		selector.tfInPostMax = this.tfInPostMax;
		selector.tfInParagraphMax = this.tfInParagraphMax;
		selector.tfInSentenceMax  = this.tfInSentenceMax ;
		selector.kernelVerbTFInPostMax = this.kernelVerbTFInPostMax;
		selector.kernelVerbTFInParagraphMax = this.kernelVerbTFInParagraphMax;
		selector.kernelVerbTFInSentenceMax  = this.kernelVerbTFInSentenceMax ;
		selector.kernelNounTFInPostMax = this.kernelNounTFInPostMax;
		selector.kernelNounTFInParagraphMax = this.kernelNounTFInParagraphMax;
		selector.kernelNounTFInSentenceMax  = this.kernelNounTFInSentenceMax ;
		selector.verbTFInPostMax = this.verbTFInPostMax;
		selector.verbTFInParagraphMax = this.verbTFInParagraphMax;
		selector.verbTFInSentenceMax  = this.verbTFInSentenceMax ;
		selector.nounTFInPostMax = this.nounTFInPostMax;
		selector.nounTFInParagraphMax = this.nounTFInParagraphMax;
		selector.nounTFInSentenceMax  = this.nounTFInSentenceMax ;
		selector.tfInPostAvrg = this.tfInPostAvrg;
		selector.tfInParagraphAvrg = this.tfInParagraphAvrg;
		selector.tfInSentenceAvrg  = this.tfInSentenceAvrg ;
		selector.kernelVerbTFInPostAvrg = this.kernelVerbTFInPostAvrg;
		selector.kernelVerbTFInParagraphAvrg = this.kernelVerbTFInParagraphAvrg;
		selector.kernelVerbTFInSentenceAvrg  = this.kernelVerbTFInSentenceAvrg ;
		selector.kernelNounTFInPostAvrg = this.kernelNounTFInPostAvrg;
		selector.kernelNounTFInParagraphAvrg = this.kernelNounTFInParagraphAvrg;
		selector.kernelNounTFInSentenceAvrg  = this.kernelNounTFInSentenceAvrg ;
		selector.verbTFInPostAvrg = this.verbTFInPostAvrg;
		selector.verbTFInParagraphAvrg = this.verbTFInParagraphAvrg;
		selector.verbTFInSentenceAvrg  = this.verbTFInSentenceAvrg ;
		selector.nounTFInPostAvrg = this.nounTFInPostAvrg;
		selector.nounTFInParagraphAvrg = this.nounTFInParagraphAvrg;
		selector.nounTFInSentenceAvrg  = this.nounTFInSentenceAvrg ;
		selector.threadFavorites = this.threadFavorites;
		selector.threadScores = this.threadScores;
		selector.threadViews = this.threadViews;
		selector.threadAnswersCount = this.threadAnswersCount;
		selector.contentScoreMax  = this.contentScoreMax ;
		selector.contentScoreAvrg  = this.contentScoreAvrg ;
		selector.answerRelativeRankBest = this.answerRelativeRankBest;
		selector.answerRelativeRankAvrg  = this.answerRelativeRankAvrg ;
		selector.commentCountMax = this.commentCountMax;
		selector.commentCountMin = this.commentCountMin;
		selector.commentCountAvrg  = this.commentCountAvrg ;
		selector.isRegisteredUser  = this.isRegisteredUser ;
		selector.isEdited  = this.isEdited ;
		selector.ownerAcceptedAnswerRateMax = this.ownerAcceptedAnswerRateMax;
		selector.ownerAcceptedAnswerRateAvrg  = this.ownerAcceptedAnswerRateAvrg ;
		selector.ownerTotalBadges = this.ownerTotalBadges;
		selector.ownerReputationMax = this.ownerReputationMax;
		selector.ownerBadges1stMax = this.ownerBadges1stMax;
		selector.ownerBadges2ndMax = this.ownerBadges2ndMax;
		selector.ownerBadges3rdMax = this.ownerBadges3rdMax;
		selector.ownerQuestionsCountMax = this.ownerQuestionsCountMax;
		selector.ownerAnswersCountMax = this.ownerAnswersCountMax;
		selector.ownerUpVotesMax = this.ownerUpVotesMax;
		selector.ownerDownVotesMax  = this.ownerDownVotesMax ;
		selector.ownerReputationAvrg = this.ownerReputationAvrg;
		selector.ownerBadges1stAvrg = this.ownerBadges1stAvrg;
		selector.ownerBadges2ndAvrg = this.ownerBadges2ndAvrg;
		selector.ownerBadges3rdAvrg = this.ownerBadges3rdAvrg;
		selector.ownerQuestionsCountAvrg = this.ownerQuestionsCountAvrg;
		selector.ownerAnswersCountAvrg = this.ownerAnswersCountAvrg;
		selector.ownerUpVotesAvrg = this.ownerUpVotesAvrg;
		selector.ownerDownVotesAvrg  = this.ownerDownVotesAvrg ;
		selector.ownerReputationSum = this.ownerReputationSum;
		selector.ownerBadges1stSum = this.ownerBadges1stSum;
		selector.ownerBadges2ndSum = this.ownerBadges2ndSum;
		selector.ownerBadges3rdSum = this.ownerBadges3rdSum;
		selector.ownerQuestionsCountSum = this.ownerQuestionsCountSum;
		selector.ownerAnswersCountSum = this.ownerAnswersCountSum;
		selector.ownerUpVotesSum = this.ownerUpVotesSum;
		selector.ownerDownVotesSum  = this.ownerDownVotesSum ;
		selector.lastEditorReputationMax = this.lastEditorReputationMax;
		selector.lastEditorBadges1stMax = this.lastEditorBadges1stMax;
		selector.lastEditorBadges2ndMax = this.lastEditorBadges2ndMax;
		selector.lastEditorBadges3rdMax = this.lastEditorBadges3rdMax;
		selector.lastEditorQuestionsCountMax = this.lastEditorQuestionsCountMax;
		selector.lastEditorAnswersCountMax = this.lastEditorAnswersCountMax;
		selector.lastEditorUpVotesMax = this.lastEditorUpVotesMax;
		selector.lastEditorDownVotesMax  = this.lastEditorDownVotesMax ;
		selector.lastEditorReputationAvrg = this.lastEditorReputationAvrg;
		selector.lastEditorBadges1stAvrg = this.lastEditorBadges1stAvrg;
		selector.lastEditorBadges2ndAvrg = this.lastEditorBadges2ndAvrg;
		selector.lastEditorBadges3rdAvrg = this.lastEditorBadges3rdAvrg;
		selector.lastEditorQuestionsCountAvrg = this.lastEditorQuestionsCountAvrg;
		selector.lastEditorAnswersCountAvrg = this.lastEditorAnswersCountAvrg;
		selector.lastEditorUpVotesAvrg = this.lastEditorUpVotesAvrg;
		selector.lastEditorDownVotesAvrg  = this.lastEditorDownVotesAvrg ;
		selector.lastEditorReputationSum = this.lastEditorReputationSum;
		selector.lastEditorBadges1stSum = this.lastEditorBadges1stSum;
		selector.lastEditorBadges2ndSum = this.lastEditorBadges2ndSum;
		selector.lastEditorBadges3rdSum = this.lastEditorBadges3rdSum;
		selector.lastEditorQuestionsCountSum = this.lastEditorQuestionsCountSum;
		selector.lastEditorAnswersCountSum = this.lastEditorAnswersCountSum;
		selector.lastEditorUpVotesSum = this.lastEditorUpVotesSum;
		selector.lastEditorDownVotesSum  = this.lastEditorDownVotesSum ;
		selector.lastEditorAcceptedAnswerRateMax = this.lastEditorAcceptedAnswerRateMax;
		selector.lastEditorAcceptedAnswerRateAvrg  = this.lastEditorAcceptedAnswerRateAvrg ;

		return selector;
	}
}
