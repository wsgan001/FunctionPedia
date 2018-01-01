package cn.edu.pku.sei.tsr.dragon.stackoverflow.FeatureExtractor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import cn.edu.pku.sei.tsr.dragon.content.ContentParser;
import cn.edu.pku.sei.tsr.dragon.content.SyntaxParser;
import cn.edu.pku.sei.tsr.dragon.content.entity.ContentInfo;
import cn.edu.pku.sei.tsr.dragon.content.entity.ParagraphInfo;
import cn.edu.pku.sei.tsr.dragon.content.entity.PhraseInfo;
import cn.edu.pku.sei.tsr.dragon.content.entity.SentenceInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.entity.VerbalPhraseStructureInfo;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.CommentInfo;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.PostInfo;
import cn.edu.pku.sei.tsr.dragon.utils.ObjectIO;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

/**
 * Created by maxkibble on 16/3/27.
 */
class SmallerPostInfo {
    public int commentNum;
    public int wordNum;
    public int score;
    public int authorRep;
}

public class Extractor {
    public static int phraseNum = 0;
    public static SmallerPostInfo currentPost = new SmallerPostInfo();
    public static HashMap<String,Integer> phraseList = new HashMap<>();
    public static ArrayList<PhraseFeature> featureList = new ArrayList<>();
    public static String path = "/home/maxkibble/Document/brand_new_data";

    public static String getArffHead() {
        String str = "@relation phrase\n\n";
        str += "@attribute phraseTotTimes numeric\n";
        str += "@attribute phraseQuesTimes numeric\n";
        str += "@attribute phraseCommentTimes numeric\n";
        str += "@attribute phraseAnswerTimes numeric\n";
        str += "@attribute phraseAccAnswerTimes numeric\n";
        str += "@attribute verbTotTimes numeric\n";
        str += "@attribute verbQuesTimes numeric\n";
        str += "@attribute verbCommentTimes numeric\n";
        str += "@attribute verbAnswerTimes numeric\n";
        str += "@attribute verbAccAnswerTimes numeric\n";
        str += "@attribute nounTotTimes numeric\n";
        str += "@attribute nounQuesTimes numeric\n";
        str += "@attribute nounCommentTimes numeric\n";
        str += "@attribute nounAnswerTimes numeric\n";
        str += "@attribute nounAccAnswerTimes numeric\n";
        str += "@attribute appearInTitle {TRUE,FALSE}\n";
        str += "@attribute firstSentenceId numeric\n";
        str += "@attribute LastSentenceId numeric\n";
        str += "@attribute beforeCode {TRUE,FALSE}\n";
        str += "@attribute afterCode {TRUE,FALSE}\n";
        str += "@attribute highPostRep numeric\n";
        str += "@attribute avgPostRep numeric\n";
        str += "@attribute sumPostRep numeric\n";
        str += "@attribute highPostScore numeric\n";
        str += "@attribute avgPostScore numeric\n";
        str += "@attribute sumPostScore numeric\n";
        str += "@attribute highPostWordNum numeric\n";
        str += "@attribute avgPostWordNum numeric\n";
        str += "@attribute highPostCommentNum numeric\n";
        str += "@attribute avgPostCommentNum numeric\n";
        str += "@attribute mentionedPostNum numeric\n";
        //str += "@attribute isKeyPhrase {yes,no}\n\n";
        str += "@attribute POINTS numeric\n\n";
        str += "@data\n";
        return str;
    }

    public static int appearTimes(String word,String content) {
        int times = 0;
        Properties props = new Properties();
        props.put("annotators", "tokenize,ssplit,pos, lemma");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        Annotation document = new Annotation(content);
        pipeline.annotate(document);
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        for(CoreMap sentence: sentences) {
            for (CoreLabel token: sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                String lema = token.get(CoreAnnotations.LemmaAnnotation.class);
                if(lema.equals(word)) times++;
            }
        }
        return times;
    }

    public static boolean beManualMarked(String filename) {
        File threadDir = new File(path + "/manualOutput");
        filename += ".txt";
        if (threadDir.exists() && threadDir.listFiles() != null) {
            File[] subdirs = threadDir.listFiles();
            for (File file : subdirs) {
                if (file.isDirectory()) {
                    File[] libContents = file.listFiles();
                    for (File threadFile : libContents) {
                        if (threadFile.getName().equals(filename)) return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean isKeyPhrase(String phraseContent, String filename) throws Exception {
        File threadDir = new File(path + "/manualOutput");
        filename += ".txt";
        if (threadDir.exists() && threadDir.listFiles() != null) {
            File[] subdirs = threadDir.listFiles();
            for (File file : subdirs) {
                if (file.isDirectory()) {
                    File[] libContents = file.listFiles();
                    for (File threadFile : libContents) {
                        if (threadFile.getName().equals(filename)) {
                            BufferedReader in = new BufferedReader(new FileReader(path + "/manualOutput/poiData/" + filename));
                            String manualPhrase = in.readLine();
                            if(manualPhrase.equals(phraseContent)) return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static String getVerbNounPhrase(PhraseInfo phraseInfo) {
        VerbalPhraseStructureInfo v = new VerbalPhraseStructureInfo(phraseInfo);
        if(v.getVerb() != null && v.getSubNP() != null && v.getSubNP().getKeyNoun() != null) {
            return v.getVerb() + " " + v.getSubNP().getKeyNoun();
        }
        return "";
    }

    public static void contentExtractor(ContentInfo content,String type) {
        ContentParser.parseContent(content);
        List<ParagraphInfo> paraList = content.getParagraphList();
        int paraSize = paraList.size();
        for(int i = 0; i < paraSize; i++) {
            if(paraList.get(i).getSentences() != null) {
                if(i < paraSize - 1 && paraList.get(i+1).isCodeFragment()) {
                    int sentenceNum = paraList.get(i).getSentences().size();
                    if(sentenceNum != 0) paraList.get(i).getSentences().get(sentenceNum-1).beforeCodeFragment = true;
                }
                else if(i > 0 && paraList.get(i-1).isCodeFragment()) {
                    if(paraList.get(i).getSentences().size() != 0)
                        paraList.get(i).getSentences().get(0).afterCodeFragment = true;
                }
                int senteceId = 0;
                for(SentenceInfo sentenceInfo : paraList.get(i).getSentences()) {
                    SyntaxParser.extractPhrases(sentenceInfo);
                    /*DocumentParser.replaceCodeLikeTerms(sentenceInfo);
                    SentenceParser.parseGrammaticalTree(sentenceInfo);
                    PhraseExtractor.extractVerbPhrases(sentenceInfo);*/
                    sentenceExtractor(sentenceInfo,type,senteceId++);
                }
            }
        }
    }

    public static void sentenceExtractor(SentenceInfo sentence,String type,int sentenceId) {
        if(sentence.getPhrases() != null) {
            for(PhraseInfo phraseInfo : sentence.getPhrases()) {
                String verbNounPhrase = getVerbNounPhrase(phraseInfo);
                if(verbNounPhrase.equals("")) continue;
                if(phraseList.containsKey(verbNounPhrase)) {
                    int featureIdx = phraseList.get(verbNounPhrase);
                    featureList.get(featureIdx).phraseTotTimes++;
                    featureList.get(featureIdx).lastSentenceId = sentenceId;
                    PhraseFeature p = featureList.get(featureIdx);
                    featureList.get(featureIdx).highPostCommNum = Math.max(currentPost.commentNum,p.highPostCommNum);
                    featureList.get(featureIdx).avgPostCommNum = (p.mentionedPostNum * p.avgPostCommNum + currentPost.commentNum) / (p.mentionedPostNum + 1);
                    featureList.get(featureIdx).highPostScore = Math.max(currentPost.score,p.highPostScore);
                    featureList.get(featureIdx).sumPostScore = p.sumPostScore + currentPost.score;
                    featureList.get(featureIdx).avgPostScore = featureList.get(featureIdx).sumPostScore / (p.mentionedPostNum + 1);
                    featureList.get(featureIdx).highPostRep = Math.max(p.highPostRep,currentPost.authorRep);
                    featureList.get(featureIdx).sumPostRep = p.sumPostRep + currentPost.authorRep;
                    featureList.get(featureIdx).avgPostRep = featureList.get(featureIdx).sumPostRep / (p.mentionedPostNum + 1);
                    featureList.get(featureIdx).highPostWordNum = Math.max(p.highPostWordNum,currentPost.wordNum);
                    featureList.get(featureIdx).avgPostWordNum = (p.mentionedPostNum * p.avgPostWordNum + currentPost.wordNum) / (p.mentionedPostNum + 1);
                    featureList.get(featureIdx).mentionedPostNum++;
                    if(sentence.beforeCodeFragment) {
                        featureList.get(featureIdx).beforeCode = true;
                    }
                    if(sentence.afterCodeFragment) {
                        featureList.get(featureIdx).afterCode = true;
                    }
                    if(type.equals("Question")) {
                        featureList.get(featureIdx).phraseQuesTimes++;
                    }
                    else if(type.equals("Answer")) {
                        featureList.get(featureIdx).phraseAnswerTimes++;
                    }
                    else if(type.equals("AccAnswer")) {
                        featureList.get(featureIdx).phraseAnswerTimes++;
                        featureList.get(featureIdx).phraseAccAnsTimes++;
                    }
                    else if(type.equals("SOComment")) {
                        featureList.get(featureIdx).phraseCommentTimes++;
                    }
                }
                else {
                    phraseList.put(verbNounPhrase,phraseNum);
                    PhraseFeature phraseFeature = new PhraseFeature(verbNounPhrase);
                    if(sentence.beforeCodeFragment) phraseFeature.beforeCode = true;
                    if(sentence.afterCodeFragment) phraseFeature.afterCode = true;
                    phraseFeature.mentionedPostNum = 1;
                    phraseFeature.phraseTotTimes = 1;
                    if(type.equals("Title")) {
                        phraseFeature.appearInTitle = true;
                    }
                    else if(type.equals("Question")) {
                        phraseFeature.phraseQuesTimes = 1;
                        phraseFeature.mentionedPostNum = 1;
                        phraseFeature.highPostCommNum = phraseFeature.avgPostCommNum = currentPost.commentNum;
                        phraseFeature.highPostScore = phraseFeature.avgPostScore = phraseFeature.sumPostScore = currentPost.score;
                        phraseFeature.highPostRep = phraseFeature.avgPostRep = phraseFeature.sumPostRep = currentPost.authorRep;
                        phraseFeature.highPostWordNum = phraseFeature.avgPostWordNum = currentPost.wordNum;
                    }
                    else if(type.equals("Answer")){
                        phraseFeature.phraseAnswerTimes = 1;
                        phraseFeature.mentionedPostNum = 1;
                        phraseFeature.highPostCommNum = phraseFeature.avgPostCommNum = currentPost.commentNum;
                        phraseFeature.highPostScore = phraseFeature.avgPostScore = phraseFeature.sumPostScore = currentPost.score;
                        phraseFeature.highPostRep = phraseFeature.avgPostRep = phraseFeature.sumPostRep = currentPost.authorRep;
                        phraseFeature.highPostWordNum = phraseFeature.avgPostWordNum = currentPost.wordNum;
                    }
                    else if(type.equals("AccAnswer")) {
                        phraseFeature.phraseAccAnsTimes = 1;
                        phraseFeature.mentionedPostNum = 1;
                        phraseFeature.highPostCommNum = phraseFeature.avgPostCommNum = currentPost.commentNum;
                        phraseFeature.highPostScore = phraseFeature.avgPostScore = phraseFeature.sumPostScore = currentPost.score;
                        phraseFeature.highPostRep = phraseFeature.avgPostRep = phraseFeature.sumPostRep = currentPost.authorRep;
                        phraseFeature.highPostWordNum = phraseFeature.avgPostWordNum = currentPost.wordNum;
                    }
                    else if(type.equals("SOComment")) {
                        phraseFeature.phraseCommentTimes = 1;
                    }
                    phraseFeature.firstSentenceId = sentenceId;
                    featureList.add(phraseFeature);
                    phraseNum++;
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        File threadDir = new File(path + "/poi_input");
        // for single testing file
        int cnt = 0;

        // for training set
        /*PrintStream out = new PrintStream("/home/maxkibble/Document/brand_new_data/train_regression.arff");
        String output = getArffHead();*/

        if (threadDir.exists() && threadDir.listFiles() != null) {
            File[] subdirs = threadDir.listFiles();
            for (File file : subdirs) {
                if (file.isDirectory()) {
                    File[] libContents = file.listFiles();
                    for (File threadFile : libContents) {
                        // for training set
                        /*if(!beManualMarked(threadFile.getName())) continue;*/

                        // for single testing file
                        if(cnt == 1) break;
                        cnt++;
                        threadFile = new File(path + "/poi_input/poiData/0b4ea256-9ee4-4eac-aa44-d69647332c67.dat");

                        // CHANGE THIS LINE !!!
                        OldThreadInfo thread = (OldThreadInfo) ObjectIO.readObject(threadFile);


                        phraseList.clear();
                        featureList.clear();
                        phraseNum = 0;

                        String  QuestionString = "",
                                AnswerString = "",
                                AccAnswerString = "",
                                CommentString = "";

                        // for single testing file
                        String output = getArffHead();
                        PrintStream out = new PrintStream(path + "/arffFile/poiData/0b4ea256-9ee4-4eac-aa44-d69647332c67.arff");

                        SentenceInfo title = new SentenceInfo(thread.getTitle().getContent());
                        sentenceExtractor(title, "Title", 0);

                        ContentInfo questionContent = thread.getQuestion().getContent();
                        QuestionString = questionContent.toString();
                        currentPost.commentNum = thread.getQuestion().getComments().size();
                        if(thread.getQuestion().getOwnerUser() != null)
                            currentPost.authorRep = thread.getQuestion().getOwnerUser().getReputation();
                        currentPost.score = thread.getQuestion().getScore();
                        currentPost.wordNum = thread.getQuestion().getContent().toString().split(" ").length;
                        contentExtractor(questionContent,"Question");
                        List<CommentInfo> commentInfoList = thread.getQuestion().getComments();
                        for(CommentInfo commentInfo : commentInfoList) {
                            ContentInfo commentContent = commentInfo.getContent();
                            CommentString += commentContent.toString();
                            contentExtractor(commentContent,"SOComment");
                        }

                        List<PostInfo> answerList = thread.getAnswers();
                        if(answerList != null) {
                            for(PostInfo answer : answerList) {
                                ContentInfo answerContent = answer.getContent();
                                AnswerString += answerContent.toString();
                                currentPost.commentNum = answer.getComments().size();
                                if(answer.getOwnerUser() != null)
                                    currentPost.authorRep = answer.getOwnerUser().getReputation();
                                currentPost.score = answer.getScore();
                                currentPost.wordNum = answer.getContent().toString().split(" ").length;
                                if(answer.isAcceptedAnswer()) {
                                    AccAnswerString += answerContent.toString();
                                    contentExtractor(answerContent,"AccAnswer");
                                }
                                else contentExtractor(answerContent,"Answer");

                                commentInfoList = answer.getComments();
                                if(commentInfoList != null) {
                                    for(CommentInfo commentInfo : commentInfoList) {
                                        ContentInfo commentContent = commentInfo.getContent();
                                        CommentString += commentContent.toString();
                                        contentExtractor(commentContent,"SOComment");
                                    }
                                }
                            }
                        }

                        for(PhraseFeature phraseFeature : featureList) {
                            String[] content = phraseFeature.phraseContent.split(" ");
                            String verb = content[0];
                            String noun = content[1];
                            phraseFeature.verbQuesTimes = appearTimes(verb,QuestionString);
                            phraseFeature.verbAnswerTimes = appearTimes(verb,AnswerString);
                            phraseFeature.verbAccAswTimes = appearTimes(verb,AccAnswerString);
                            phraseFeature.verbCommentTimes = appearTimes(verb,CommentString);
                            phraseFeature.verbTotTimes = phraseFeature.verbQuesTimes + phraseFeature.verbAnswerTimes + phraseFeature.verbCommentTimes;

                            phraseFeature.nounQuesTimes = appearTimes(noun,QuestionString);
                            phraseFeature.nounAnswerTimes = appearTimes(noun,AnswerString);
                            phraseFeature.nounAccAnsTimes = appearTimes(noun,AccAnswerString);
                            phraseFeature.nounCommentTimes = appearTimes(noun,CommentString);
                            phraseFeature.nounTotTimes = phraseFeature.nounQuesTimes + phraseFeature.nounAnswerTimes + phraseFeature.nounCommentTimes;
                        }

                        //System.out.println("phrase num:" + phraseList.size());
                        for(PhraseFeature phraseFeature : featureList) {
                            System.out.println(phraseFeature.phraseContent);
                            output += phraseFeature.display();

                            // for single testing file
                            output += "0\n";

                            // for training set
                            /*if(isKeyPhrase(phraseFeature.phraseContent, threadFile.getName())) output += "5\n";
                            else output += "-5\n";*/
                        }
                        // for testing set
                        out.println(output);

                        System.out.println("====");
                    }
                }
            }
        }
        // for training set
        /*out.println(output);*/
    }
}
