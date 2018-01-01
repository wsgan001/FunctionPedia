package cn.edu.pku.sei.tsr.dragon.wordscluster;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Dictionary;
import java.util.Hashtable;

import cn.edu.pku.sei.tsr.dragon.utils.Config;

/// <summary>
/// 用于移除停止词
/// </summary>
public class StopWordsHandler {
	public static String[]		stopWordsList	= new String[] { "的", "我们", "要", "自己", "之", "将", "“", "”",
			"，", "（", "）", "后", "应", "到", "某", "后", "个", "是", "位", "新", "一", "两", "在", "中", "或", "有", "更",
			"好" };
	private static Hashtable	_stopwords		= null;

	public static Object AddElement(Dictionary collection, Object key, Object newValue) {
		Object element = collection.get(key);
		collection.put(key, newValue);
		return element;
	}

	public static boolean IsStopword(String str) {

		// int index=Array.BinarySearch(stopWordsList, str)
		return _stopwords.containsKey(str.toLowerCase());
	}

	static {
		if (_stopwords == null) {
			// _stopwords = new Hashtable();
			// double dummy = 0;
			// for(String word:stopWordsList){
			// _stopwords.put(word, dummy);
			// }

			_stopwords = new Hashtable();
			Double dummy = new Double(0);
			File txt = new File(Config.getKeywordsDictionaryDir() + "/stopwords_en.txt");
			InputStreamReader is;
			String sw = null;
			try {
				is = new InputStreamReader(new FileInputStream(txt), "UTF-8");
				BufferedReader br = new BufferedReader(is);
				while ((sw = br.readLine()) != null) {
					_stopwords.put(sw, dummy);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
