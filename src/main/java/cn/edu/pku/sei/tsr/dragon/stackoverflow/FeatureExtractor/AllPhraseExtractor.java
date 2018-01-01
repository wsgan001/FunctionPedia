
package cn.edu.pku.sei.tsr.dragon.stackoverflow.FeatureExtractor;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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

/**
 * Created by maxkibble on 16/3/28.
 */
public class AllPhraseExtractor {
    public static ArrayList<SentenceInfo> sentenceList = new ArrayList<>();
    public static HashSet<String> phraseList = new HashSet<>();

    public static void contentExtractor(ContentInfo contentInfo) {
        ContentParser.parseContent(contentInfo);
        List<ParagraphInfo> paraList = contentInfo.getParagraphList();
        for(ParagraphInfo paragraphInfo : paraList) {
            if(paragraphInfo.getSentences() == null) continue;
            for(SentenceInfo sentenceInfo : paragraphInfo.getSentences()) {
                sentenceList.add(sentenceInfo);
            }
        }
    }


    public static String getVerbNounPhrase(PhraseInfo phraseInfo) {
        VerbalPhraseStructureInfo v = new VerbalPhraseStructureInfo(phraseInfo);
        if(v.getVerb() != null && v.getSubNP() != null && v.getSubNP().getKeyNoun() != null) {
            return v.getVerb() + " " + v.getSubNP().getKeyNoun();
        }
        return "";
    }

    public static boolean beManualMarked(String filename) {
        File threadDir = new File("/home/maxkibble/Document/new_data/manualOutput");
        filename += ".txt";
        if(threadDir.exists() && threadDir.listFiles() != null) {
            File[] subdirs = threadDir.listFiles();
            for(File file: subdirs) {
                if(file.isDirectory()) {
                    File[] libContents = file.listFiles();
                    for(File threadFile : libContents) {
                        if(threadFile.getName().equals(filename)) return true;
                    }
                }
            }
        }
        return false;
    }

    public static void main(String[] args) throws Exception {
        File threadDir = new File("/home/maxkibble/Document/brand_new_data/poi_input");
        if (threadDir.exists() && threadDir.listFiles() != null) {
            File[] subdirs = threadDir.listFiles();
            for (File file : subdirs) {
                if (file.isDirectory()) {
                    File[] libContents = file.listFiles();
                    for (File threadFile : libContents) {
                        //if(!beManualMarked(threadFile.getName())) continue;
                        //repeat
                        //PrintStream out = new PrintStream("/home/maxkibble/Document/new_data/repeatAllPhrases/poiData/" + threadFile.getName() + ".txt");
                        //no repeat
                        PrintStream out = new PrintStream("/home/maxkibble/Document/brand_new_data/allPhrases/poiData/" + threadFile.getName() + ".txt");
                        System.out.println(threadFile.getName());
                        OldThreadInfo thread = (OldThreadInfo) ObjectIO.readObject(threadFile);
                        String output = "";
                        sentenceList.clear();
                        phraseList.clear();

                        //title
                        SentenceInfo title = new SentenceInfo(thread.getTitle().getContent());
                        sentenceList.add(title);

                        //question
                        ContentInfo questionContent = thread.getQuestion().getContent();
                        contentExtractor(questionContent);
                        List<CommentInfo> commentInfoList = thread.getQuestion().getComments();
                        for(CommentInfo commentInfo : commentInfoList) {
                            ContentInfo commentContent = commentInfo.getContent();
                            contentExtractor(commentContent);
                        }

                        //answer
                        List<PostInfo> answerList = thread.getAnswers();
                        for(PostInfo answer : answerList) {
                            ContentInfo answerContent = answer.getContent();
                            contentExtractor(answerContent);
                            commentInfoList = answer.getComments();
                            for(CommentInfo commentInfo : commentInfoList) {
                                ContentInfo commentContent = commentInfo.getContent();
                                contentExtractor(commentContent);
                            }
                        }

                        for(SentenceInfo sentenceInfo : sentenceList) {
                            SyntaxParser.extractPhrases(sentenceInfo);
                            /*DocumentParser.replaceCodeLikeTerms(sentenceInfo);
                            SentenceParser.parseGrammaticalTree(sentenceInfo);
                            PhraseExtractor.extractVerbPhrases(sentenceInfo);*/
                            for(PhraseInfo phraseInfo : sentenceInfo.getPhrases()) {
                                String vnPhrase = getVerbNounPhrase(phraseInfo);
                                if(vnPhrase.equals("")) continue;
                                //repeat
                                //output += vnPhrase + "\n";
                                // no repeat

                                if(phraseList.contains(vnPhrase)) continue;
                                else {
                                    phraseList.add(vnPhrase);
                                    output += vnPhrase + "\n";
                                }
                            }
                        }
                        out.println(output);
                    }
                }
            }
        }
    }
}
