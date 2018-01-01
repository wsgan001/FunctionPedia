package cn.edu.pku.sei.tsr.dragon.search;

import java.io.File;
import java.io.IOException;

import cn.edu.pku.sei.tsr.dragon.search.util.Config;
import cn.edu.pku.sei.tsr.dragon.search.util.LuceneUtil;
import cn.edu.pku.sei.tsr.dragon.search.util.ObjectIO;
public class ThreadExtractor {
	private static void parseThread(File file){
		OldThreadInfo thread = (OldThreadInfo)ObjectIO.readObject(file);
		LuceneUtil.updateThreadToLucene(thread);
	}
	public static void extractContentToLucene(String path){
		File fileDir = new File(path);
		int cnt = 0;
		if (fileDir.isDirectory()) {
			File files[] = fileDir.listFiles();
			if (files != null && files.length > 0) {
				for (File file : files) {
					cnt ++;
					parseThread(file);
					if (cnt % 100 == 0) System.out.println(cnt);
				}
			}
		}
	}
	public static void main(String args[]){
		String path = Config.getThreadDir();
		extractContentToLucene(path);
		try {
			LuceneUtil.directory.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
