package cn.edu.pku.sei.tsr.dragon.taskdocmodel.scorer;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import cn.edu.pku.sei.tsr.dragon.task.entity.TaskInfo;
import cn.edu.pku.sei.tsr.dragon.taskdocmodel.entity.Document;
import cn.edu.pku.sei.tsr.dragon.taskdocmodel.entity.TaskVector;
import cn.edu.pku.sei.tsr.dragon.taskdocmodel.entity.Word;
import cn.edu.pku.sei.tsr.dragon.utils.TextUtils;
import cn.edu.pku.sei.tsr.dragon.utils.nlp.Stemmer;

public class TaskSimilarity {

	/**
	 * try to fetch similarity in the task cache, if not existed, calculate a
	 * new one, and store into the cache.
	 * 
	 * @param task1
	 * @param task2
	 * @return
	 */
	public static float calculateSimilarity(TaskVector task1, TaskVector task2) {
		String task1Text = task1.getTaskInfo().getText();
		String task2Text = task2.getTaskInfo().getText();

		Float cachedSimilarity1_2 = task1.getTaskSimilarityCache().get(task2Text);
		Float cachedSimilarity2_1 = task2.getTaskSimilarityCache().get(task1Text);

		if (cachedSimilarity1_2 != null) {
			if (cachedSimilarity2_1 == null)
				task2.getTaskSimilarityCache().put(task1Text, cachedSimilarity1_2);
			return cachedSimilarity1_2;
		}
		else if (cachedSimilarity2_1 != null) {
			task1.getTaskSimilarityCache().put(task2Text, cachedSimilarity2_1);
			return cachedSimilarity2_1;
		}
		else { // both null
			float similarity = calculateSimilarity(task1Text, task2Text);
			task1.getTaskSimilarityCache().put(task2Text, similarity);
			task2.getTaskSimilarityCache().put(task1Text, similarity);
			return similarity;
		}

	}

	public static float calculateSimilarity(String query, TaskVector task) {
		Float cachedSimilarity = task.getTaskSimilarityCache().get(query);
		if (cachedSimilarity != null) {
			return cachedSimilarity;
		}
		else {
			float similarity = calculateSimilarity(query, task.getTaskInfo().getText());
			task.getTaskSimilarityCache().put(query, similarity);
			return similarity;
		}
	}

	/**
	 * assumed to be symmetric 
	 * 
	 * @param taskString1
	 * @param taskString2
	 * @return
	 */
	public static float calculateSimilarity(String taskString1, String taskString2) {
		List<Word> wordList1 = getWordsList(taskString1);
		List<Word> wordList2 = getWordsList(taskString2);
		int wordCount1 = wordList1.size();
		int wordCount2 = wordList2.size();
		float weightedLCS[][] = new float[wordCount1 + 1][wordCount2 + 1];
		for (int i = 0; i <= wordCount1; i++)
			weightedLCS[i][0] = 0;
		for (int i = 0; i <= wordCount2; i++)
			weightedLCS[0][i] = 0;
		for (int i = 1; i <= wordCount1; i++) {
			for (int j = 1; j <= wordCount2; j++) {
				Word word1 = wordList1.get(i - 1);
				Word word2 = wordList2.get(j - 1);
				if (word1.equals(word2))
					weightedLCS[i][j] = weightedLCS[i - 1][j - 1] + word1.weight + word2.weight;
				else {
					if (word1.word.equals(word2.word)) {
						weightedLCS[i][j] = weightedLCS[i - 1][j - 1] + Math.min(word1.weight, word2.weight);
					}
					else
						weightedLCS[i][j] = weightedLCS[i - 1][j - 1];
				}
				weightedLCS[i][j] = Math.max(weightedLCS[i][j],
						Math.max(weightedLCS[i - 1][j], weightedLCS[i][j - 1]));
			}
		}
		float sum = 0;
		for (int i = 0; i < wordCount1; i++)
			sum += wordList1.get(i).weight;
		for (int i = 0; i < wordCount2; i++)
			sum += wordList2.get(i).weight;
		return weightedLCS[wordCount1][wordCount2] / sum;
	}

	public static List<Word> getWordsList(TaskInfo task) {
		return getWordsList(task.getText());
	}

	public static List<Word> getWordsList(String taskString) {
		String[] words = taskString.split(" ");
		List<Word> wordsList = new ArrayList<>();

		boolean knnexist = false;
		boolean verbexist = false;
		for (String word : words) {
			if (!isValidWord(word))
				continue;

			// "< comment / >/vb eee/xxx"
			String[] wordParts = word.split(TaskInfo.POS_SEPARATOR);
			if (wordParts.length <= 0) {
				// System.out.println(word);
				// System.out.println(wordParts);
				continue;
			}
			String wordText = wordParts[0];
			String wordTag = wordParts.length >= 2 ? wordParts[1] : "";

			Word w = new Word(wordText, wordTag);
			if (wordTag.equals(TaskInfo.POS_VERB)) {
				if (verbexist)
					w.weight = 0.7f;
				else
					w.weight = 1;
				verbexist = true;
			}
			else if (wordTag.equals(TaskInfo.POS_KERNEL_NOUN)) {
				if (knnexist)
					w.weight = 0.7f;
				else
					w.weight = 1;
				knnexist = true;
			}
			else if (wordTag.equals(TaskInfo.POS_NOUN)) {
				// before knn appears, it's still in the first NP
				if (knnexist)
					w.weight = 0.5f;
				else
					w.weight = 0.7f;
			}
			else if (wordTag.equals(TaskInfo.POS_ADJ)) {
				// before knn appears, it's still in the first NP
				if (knnexist)
					w.weight = 0.5f;
				else
					w.weight = 0.7f;
			}
			else if (wordTag.equals(TaskInfo.POS_CONJ) || wordTag.equals(TaskInfo.POS_OTHER)) {
				w.weight = 0.3f;
			}
			else {
				w.weight = 0;
			}

			wordsList.add(w);

			// if (!map.containsKey(wordText))
			// map.put(wordText, 0);
			// map.put(wordText, map.get(wordText) + 1);
		}
		return wordsList;
	}

	public static boolean isValidWord(String word) {
		if (StringUtils.isBlank(word))
			return false;

		int letterCount = 0;
		if (!Character.isLetterOrDigit(word.charAt(0)))
			return false;

		for (int i = 0; i < word.length(); i++) {
			char ch = word.charAt(i);
			if (Character.isAlphabetic(ch))
				letterCount++;
			else if (!Character.isDigit(ch) && ch != '-' && ch != '/')
				return false;
		}

		if (letterCount > 0)
			return true;
		else
			return false;
	}

	public static void main(String[] args) {
		TaskInfo t1 = new TaskInfo();
		TaskInfo t2 = new TaskInfo();
		t1.setText("read/vb cell/nn formular/knn from/conj excel/nn file/knn");
		t2.setText("get/vb formular/nn cell/knn from/conj large/adj excel/nn file/knn");
		System.out.println(calculateSimilarity(t1.getText(), t2.getText()));
	}
}
