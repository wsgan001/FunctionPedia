package cn.edu.pku.sei.tsr.dragon.taskdocmodel.search;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Task{
	double idf = 0;
	double score;
	String text;
	Doc doc;
	List<Word> words = new ArrayList<Word>();
	List<Double> relevances = new ArrayList<Double>();
	List<Double> tfidf = new ArrayList<Double>();
	List<Double> tfs = new ArrayList<Double>();
	Map<String,Integer> map = new HashMap<String,Integer>();
	public void settask(String line){
		this.text = line;
		line = line.replaceAll(" +", " ");
		String [] words = line.split(" ");
		boolean knnexist = false;
		boolean nnexist = false;
		boolean adjexist = false;
		boolean verbexist = false;
		for (String word : words){
			String key = word.split("/")[0];
			String value = word.split("/")[1];
			Word w = new Word(key,value);
			this.words.add(w);
			w.weight = 0.3;
			if (value.equals("verb")) {
				if (verbexist) w.weight = 0.7; else
				w.weight = 1;
				verbexist = true;
			}
			if (value.equals("knn")) {
				if (knnexist) w.weight = 0.7; else
				w.weight = 1;
				knnexist = true;
			}
			if (value.equals("nn")) {
				if (nnexist) w.weight = 0.5; else
				w.weight = 0.7;
				nnexist = true;
			}				
			if (value.equals("adj")) {
				if (adjexist) w.weight = 0.5; else
				w.weight = 0.7;
				adjexist = true;
			}			
			if (!map.containsKey(key)) map.put(key, 0);
			map.put(key, map.get(key)+1);
		}
	}
	
	public double getSim(Task task){
		int l1 = words.size();
		int l2 = task.words.size();
		double f[][] = new double [l1 + 1][l2 + 1];
		for (int i = 0; i <= l1; i++) f[i][0] = 0;
		for (int i = 0; i <= l2; i++) f[0][i] = 0;
		for (int i = 1; i <= l1; i++){
			for (int j = 1; j <= l2; j++){
				Word word1 = words.get(i-1);
				Word word2 = task.words.get(j-1); 
				if (word1.equals(word2)) f[i][j] = f[i-1][j-1] + word1.weight + word2.weight; else
				{
					if (word1.word.equals(word2.word)){
						f[i][j] = f[i-1][j-1] + Math.min(word1.weight, word2.weight);
					}else
					f[i][j] = f[i-1][j-1];
				}
				f[i][j] = Math.max(f[i][j],Math.max(f[i-1][j],f[i][j-1]));
			}
		}
		double sum = 0;
		for (int i = 0; i < l1; i++) sum += words.get(i).weight;
		for (int i = 0; i < l2; i++) sum += task.words.get(i).weight;
		return f[l1][l2]/sum;
	}
	
	public String toString(){
		String ret = "";
		for (Word word : words){
			ret = ret + word.word + " ";
		}
		return ret;
	} 
}