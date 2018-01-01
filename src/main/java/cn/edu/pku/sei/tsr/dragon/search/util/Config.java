package cn.edu.pku.sei.tsr.dragon.search.util;

import java.util.ResourceBundle;

public class Config {

	private static String			CONFIG_FILE_NAME	= "cn.edu.pku.sei.tsr.dragon.search.config";
	private static ResourceBundle	bundle;

	static {
		try {
			bundle = ResourceBundle.getBundle(CONFIG_FILE_NAME);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String getValue(String key) {
		return bundle.getString(key);
	}

	public static String getIndexDir() {
		return getValue("lucene_index");
	}
	public static String getContentDir() {
		return getValue("content_object");
	}
	public static String getThreadDir(){
		return getValue("thread_object");
	}
	public static String getMailDir(){
		return getValue("email_file");
	}
	public static String getSessionDir(){
		return getValue("session_file");
	}
	public static void main(String args[]) {

	}
}
