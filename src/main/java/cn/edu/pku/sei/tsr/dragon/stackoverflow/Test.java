package cn.edu.pku.sei.tsr.dragon.stackoverflow;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

/**
 * Created by maxkibble on 2015/12/24.
 */
public class Test {
	public static TreeMap<java.lang.Double, String> wordList = new TreeMap<>(
			Collections.reverseOrder());

	public static void main(String args[]) throws FileNotFoundException {
		while (true) {
			wordList.clear();
			InputStream in = System.in;
			Scanner sc = new Scanner(in);
			Vector tWord = new Vector();
			String tWordContent = sc.nextLine();
			if (tWordContent.equals("EXIT"))
				break;
			tWord.setWord(tWordContent);
			String corpusDir = "D:\\Dragon Project\\workspace\\Dragon\\pathToWriteto.txt";
			in = new FileInputStream(corpusDir);
			sc = new Scanner(in);
			String line = "";
			String[] ele;
			boolean findTword = false;
			while (sc.hasNext()) {
				line = sc.nextLine();
				ele = line.split(" ");
				if (tWord.getWord().equals(ele[0])) {
					for (int i = 0; i < 100; i++)
						tWord.setVec(java.lang.Double.valueOf(ele[i + 1]), i);
					findTword = true;
					break;
				}
			}
			if (findTword == false) {
				System.out.println("no such word in corpus");
				continue;
			}
			in = new FileInputStream(corpusDir);
			sc = new Scanner(in);
			while (sc.hasNext()) {
				line = sc.nextLine();
				ele = line.split(" ");
				Vector sWord = new Vector();
				sWord.setWord(ele[0]);
				for (int i = 0; i < 100; i++) {
					sWord.setVec(java.lang.Double.valueOf(ele[i + 1]), i);
				}
				wordList.put(sWord.cosineSimilarity(tWord), sWord.getWord());
			}
			java.util.Iterator it = wordList.entrySet().iterator();
			for (int i = 0; i < 10; i++) {
				Map.Entry entry = (Map.Entry) it.next();
				Object key = entry.getKey();
				Object value = entry.getValue();
				System.out.println(value + " " + key);
			}
		}
	}
}
