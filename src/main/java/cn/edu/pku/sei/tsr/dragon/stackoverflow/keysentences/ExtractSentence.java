package cn.edu.pku.sei.tsr.dragon.stackoverflow.keysentences;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import cn.edu.pku.sei.tsr.dragon.utils.Config;

/**
 * Created by maxkibble on 2015/11/10.
 */
public class ExtractSentence {
	private String				filename;
	private int					originalNum;
	private int					filteredNum;
	private String				title;
	private ArrayList<String>	featureWords	= new ArrayList<String>() {
													{
														add("I'm");
														add("am");
														add("was");
														add("Am");
														add("Was");
														add("How");
														add("how");
													}
												};
	private ArrayList<String>	keyWords		= new ArrayList<>();

	public ExtractSentence(String filename) {
		try {
			this.filename = filename;
			BufferedReader in = new BufferedReader(new FileReader("./fullquestion/" + filename));
			String line = in.readLine();
			title = line;
			line = in.readLine();
			originalNum = Integer.valueOf(line);
			filteredNum = 0;
			String[] titleWords = title.split(" ");
			for (int i = 1; i < titleWords.length; i++) {
				if (!isStopWords(titleWords[i])) {
					keyWords.add(titleWords[i]);
				}
			}
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isStopWords(String s) {
		try {
			BufferedReader in = new BufferedReader(
					new FileReader(Config.getKeywordsDictionaryDir() + "/stopwords_en.txt"));
			String line = null;
			while ((line = in.readLine()) != null) {
				if (line.equals(s))
					return true;
			}
			return false;
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("this sentence should not appear");
		return false;
	}

	public boolean findWords(String word, ArrayList<String> stringArrayList) {
		for (int i = 0; i < stringArrayList.size(); i++) {
			if (word.equals(stringArrayList.get(i))) {
				return true;
			}
		}
		return false;
	}

	public void extract() {
		try {
			BufferedReader in = new BufferedReader(new FileReader("./fullquestion/" + filename));
			PrintStream out = new PrintStream("./extractedquestion/" + filename);
			// PrintStream out = System.out;
			String line = in.readLine();
			line = in.readLine();
			boolean firstLine = true;
			while ((line = in.readLine()) != null) {
				if (firstLine) {
					filteredNum++;
					out.println(line);
					firstLine = false;
				}
				String[] sentenceContent = line.split(" ");
				for (int i = 0; i < sentenceContent.length; i++) {
					if (findWords(sentenceContent[i], featureWords)
							&& findWords(sentenceContent[i], keyWords)) {
						filteredNum++;
						out.println(line);
					}
				}
			}
			out.println(originalNum + " -> " + filteredNum);
			out.println(title);
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
