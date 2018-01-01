package cn.edu.pku.sei.tsr.dragon.stackoverflow.FeatureExtractor;


/**
 * Created by maxkibble on 16/3/28.
 */
public class PhraseFeature {
    public String phraseContent;
    public int phraseTotTimes,verbTotTimes,nounTotTimes;
    public int phraseQuesTimes,verbQuesTimes,nounQuesTimes;
    public int phraseCommentTimes,verbCommentTimes,nounCommentTimes;
    public int phraseAnswerTimes,verbAnswerTimes,nounAnswerTimes;
    public int phraseAccAnsTimes,verbAccAswTimes,nounAccAnsTimes;
    public boolean appearInTitle;
    public int firstSentenceId,lastSentenceId;
    public boolean beforeCode,afterCode;
    public int highPostRep,avgPostRep,sumPostRep;
    public int highPostScore,avgPostScore,sumPostScore;
    public int highPostWordNum,avgPostWordNum;
    public int highPostCommNum,avgPostCommNum;
    public int mentionedPostNum;

    public PhraseFeature(String phrase) { phraseContent = phrase; }

    public String display() {
        String str = phraseTotTimes + "," + phraseQuesTimes + "," + phraseCommentTimes + "," + phraseAnswerTimes + "," + phraseAccAnsTimes + ",";
        str += verbTotTimes + "," + verbQuesTimes + "," + verbCommentTimes + "," + verbAnswerTimes + "," + verbAccAswTimes + ",";
        str += nounTotTimes + "," + nounQuesTimes + "," + nounCommentTimes + "," + nounAnswerTimes + "," + nounAccAnsTimes + ",";
        if(appearInTitle) str += "TRUE,";
        else str += "FALSE,";
        str += firstSentenceId + "," + lastSentenceId + ",";
        if(beforeCode) str += "TRUE,";
        else str += "FALSE,";
        if(afterCode) str += "TRUE,";
        else str += "FALSE,";
        str += highPostRep + "," + avgPostRep + "," + sumPostRep + "," + highPostScore + "," + avgPostScore + "," + sumPostScore + ","
                + highPostWordNum + "," + avgPostWordNum + "," + highPostCommNum + "," + avgPostCommNum + "," + mentionedPostNum + ",";
        return str;
    }
}
