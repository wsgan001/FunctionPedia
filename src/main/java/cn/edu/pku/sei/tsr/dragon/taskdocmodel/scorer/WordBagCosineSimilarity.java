package cn.edu.pku.sei.tsr.dragon.taskdocmodel.scorer;

import java.util.HashMap;

import cn.edu.pku.sei.tsr.dragon.utils.TextUtils;
import cn.edu.pku.sei.tsr.dragon.utils.nlp.Stemmer;

public class WordBagCosineSimilarity {

	public static float calculateCosineSimlarity(HashMap<String, Integer> wordsMap1,
			HashMap<String, Integer> wordsMap2) {
		if (wordsMap1 == null || wordsMap2 == null || wordsMap1.size() <= 0 || wordsMap2.size() <= 0)
			return 0;

		int productSum = 0;
		for (String word : wordsMap2.keySet()) {
			if (wordsMap1.containsKey(word)) {
				int m1Freq = wordsMap1.get(word);
				int m2Freq = wordsMap2.get(word);
				productSum += m1Freq * m2Freq;
			}
		}
		System.out.println(productSum);
		double length1 = lengthOfVector(wordsMap1);
		System.out.println(length1);
		double length2 = lengthOfVector(wordsMap2);
		System.out.println(length2);
		double sim = (double)productSum / (length1 * length2);
		return (float) sim;
	}

	public static double lengthOfVector(HashMap<String, Integer> wordsCountMap) {
		int squareSum = 0;
		for (Integer dim : wordsCountMap.values()) {
			squareSum += dim * dim;
		}
		return Math.sqrt(squareSum);
	}

	public static void main(String[] args) {
		HashMap<String, Integer> m1 = new HashMap<>();
		HashMap<String, Integer> m2 = new HashMap<>();

		m1.put("coref", 4);
		m1.put("configuration", 1);
		m1.put("use", 4);
		m1.put("emnlp", 1);
		m1.put("Raghunathan", 1);
		m1.put("run", 1);
		m1.put("construct", 1);
		m1.put("head", 1);
		m1.put("result", 1);
		m1.put("hit", 1);
		m1.put("problem", 1);
		m1.put("paper", 1);
		m1.put("experiment", 1);
		m1.put("pronoun", 1);
		m1.put("recall", 1);
		m1.put("property", 1);
		m1.put("ner", 3);
		m1.put("corenlp", 1);
		m1.put("tag", 1);
		m1.put("precise", 1);
		m1.put("degrade", 1);
		m1.put("annotation", 4);
		m1.put("package", 1);
		m1.put("determine", 1);
		m1.put("pass", 3);
		m1.put("relax", 1);
		m1.put("want", 3);
		m1.put("match", 1);
		m1.put("specify", 2);
		m1.put("downstream", 1);
		m1.put("require", 1);
		m1.put("label", 1);
		m1.put("quality", 1);
		m1.put("pipeline", 1);
		m1.put("Stanford", 1);
		m1.put("take", 1);
		m1.put("system", 2);
		m1.put("name", 1);
		m1.put("describe", 1);
		m1.put("coreference", 1);
		m1.put("entity", 1);

		m2.put("coref", 4);
		m2.put("antecedent", 1);
		m2.put("accurate", 1);
		m2.put("use", 4);
		m2.put("emnlp", 1);
		m2.put("recognition", 1);
		m2.put("Raghunathan", 1);
		m2.put("run", 1);
		m2.put("construct", 2);
		m2.put("serve", 1);
		m2.put("type", 1);
		m2.put("second", 1);
		m2.put("hit", 1);
		m2.put("problem", 1);
		m2.put("experiment", 1);
		m2.put("property", 1);
		m2.put("ner", 3);
		m2.put("corenlp", 1);
		m2.put("tag", 1);
		m2.put("package", 1);
		m2.put("pass", 3);
		m2.put("need", 1);
		m2.put("want", 3);
		m2.put("specify", 2);
		m2.put("require", 1);
		m2.put("classification", 1);
		m2.put("quality", 1);
		m2.put("important", 1);
		m2.put("Stanford", 1);
		m2.put("take", 1);
		m2.put("system", 3);
		m2.put("person", 1);
		m2.put("name", 2);
		m2.put("semantic", 1);
		m2.put("configuration", 1);
		m2.put("head", 1);
		m2.put("result", 1);
		m2.put("general", 1);
		m2.put("candidate", 1);
		m2.put("paper", 1);
		m2.put("pronoun", 2);
		m2.put("recall", 1);
		m2.put("precise", 1);
		m2.put("degrade", 1);
		m2.put("annotation", 4);
		m2.put("chain", 1);
		m2.put("determine", 1);
		m2.put("org", 1);
		m2.put("relax", 1);
		m2.put("match", 1);
		m2.put("downstream", 1);
		m2.put("perform", 1);
		m2.put("label", 1);
		m2.put("target", 1);
		m2.put("pipeline", 1);
		m2.put("refer", 1);
		m2.put("step", 1);
		m2.put("location", 1);
		m2.put("describe", 1);
		m2.put("coreference", 2);
		m2.put("category", 1);
		m2.put("entity", 4);
		System.out.println("m1,m2: "+calculateCosineSimlarity(m1, m2));

		HashMap<String, Integer> m3 = new HashMap<>();
		HashMap<String, Integer> m4 = new HashMap<>();

		m3.put("coref", 4);
		m3.put("configuration", 2);
		m3.put("use", 4);
		m4.put("coref", 4);
		m4.put("use", 1);
		System.out.println("m3,m4: "+calculateCosineSimlarity(m3, m4));
		float tf = m3.get("use");
		System.out.println(tf);

	}

	public static void addWordToCountMap(HashMap<String, Integer> wordsCountMap, String word) {
		if (!wordsCountMap.containsKey(word))
			wordsCountMap.put(word, 1);
		else
			wordsCountMap.put(word, wordsCountMap.get(word) + 1);
	}

	public static String getStemmedValidWord(String rawWord) {
		int letterCount = 0;
		if (!Character.isLetterOrDigit(rawWord.charAt(0)))
			return null;
		if (TextUtils.isStopword(rawWord))
			return null;

		for (int i = 0; i < rawWord.length(); i++) {
			char ch = rawWord.charAt(i);
			if (Character.isAlphabetic(ch))
				letterCount++;
			else if (!Character.isDigit(ch) && ch != '-')
				return null;
		}

		if (letterCount > 0) {
			String stemmedWord = Stemmer.stem(rawWord);
			if (TextUtils.isStopword(stemmedWord))
				return null;
			return stemmedWord;
		}
		return null;
	}
}
