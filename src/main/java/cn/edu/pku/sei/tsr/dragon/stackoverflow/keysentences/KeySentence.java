
package cn.edu.pku.sei.tsr.dragon.stackoverflow.keysentences;

import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.sei.tsr.dragon.nlp.parser.Rules;

/**
 * Created by maxkibble on 2015/11/10.
 */
public class KeySentence {
    public static ArrayList<String> featureWords = new ArrayList<String>() {{
        add("I'm");
        add("am");
        add("was");
        add("Am");
        add("Was");
        add("How");
        add("how");
    }};
    public static List<String> keyWords = new ArrayList<>();

    public static boolean judge(String sentence) {
        String[] sentenceContent = sentence.split(" ");
        for (int i = 0; i < sentenceContent.length; i++) {
            if (hasWords(sentenceContent[i])) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasWords(String s) {
        keyWords = Rules.qa_verbs;
        for (int i = 0; i < keyWords.size(); i++) {
            if (keyWords.get(i).equals(s)) {
                return true;
            }
        }
        for (int i = 0; i < featureWords.size(); i++) {
            if (featureWords.get(i).equals(s)) {
                return true;
            }
        }
        return false;
    }
}
