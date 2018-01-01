package cn.edu.pku.sei.tsr.dragon.search;

import java.io.File;
import java.io.IOException;

import cn.edu.pku.sei.tsr.dragon.content.entity.ContentInfo;
import cn.edu.pku.sei.tsr.dragon.search.util.Config;
import cn.edu.pku.sei.tsr.dragon.search.util.LuceneUtil;
import cn.edu.pku.sei.tsr.dragon.search.util.ObjectIO;
public class ContentExtractor {
	private static void parseContent(File file){
		ContentInfo content = (ContentInfo)ObjectIO.readObject(file);
		LuceneUtil.updateContentToLucene(content);
	}
	public static void extractContentToLucene(String path){
		File fileDir = new File(path);
		int cnt = 0;
		if (fileDir.isDirectory()) {
			File files[] = fileDir.listFiles();
			if (files != null && files.length > 0) {
				for (File file : files) {
					cnt ++;
					parseContent(file);
					if (cnt % 100 == 0) System.out.println(cnt);
				}
			}
		}
	}
	public static void main(String args[]){
		String path = Config.getContentDir();
		extractContentToLucene(path);
		try {
			LuceneUtil.directory.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
