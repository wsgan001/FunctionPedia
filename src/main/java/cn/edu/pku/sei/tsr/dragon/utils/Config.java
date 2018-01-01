package cn.edu.pku.sei.tsr.dragon.utils;

import java.util.ResourceBundle;

public class Config {
	private static String			CONFIG_FILE_NAME	= "config";

	private static ResourceBundle	bundle;

	// 静态私有方法，用于从属性文件中取得属性值
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
	
	public static String getSearchDir() {
		return getValue("searchdir");
	}

	public static String getScorerRootDir() {
		return getValue("scorerrootdir");
	}

	public static String getScorersDir() {
		return getValue("scorersdir");
	}

	public static String getTrainingSubjectTags() {
		return getValue("trainingsubjecttags");
	}

	public static String getScorerTrainingDir() {
		return getValue("scorertrainingdir");
	}

	public static String getScorerSubjectDataDir() {
		return getValue("scorersubjectdatadir");
	}

	public static String getCorpusDir() {
		return getValue("corpusdir");
	}

	public static String getTrainingCorpusDir() {
		return getValue("trainingcorpusdir");
	}

	public static String getMannuallyAnnotationDir() {
		return getValue("mannuallyannotationdir");
	}

	public static String getTrainingSetDir() {
		return getValue("trainingsetdir");
	}

	public static String getSubjectDataDir() {
		return getValue("subjectdatadir");
	}

	public static String getStackOverflowDataDumpDir() {
		return getValue("sodatadumpdir");
	}

	public static String getStackOverflowDataTempDir() {
		return getValue("sodatatempdir");
	}

	public static String getSubjectTags() {
		return getValue("subjecttags");
	}

	public static String getDataRootDir() {
		return getValue("datarootdir");
	}

	public static String getLocalDBUrl() {
		return getValue("localdburl");
	}

	public static String getLocalDBUserName() {
		return getValue("localdbusername");
	}

	public static String getLocalDBPassword() {
		return getValue("localdbpassword");
	}

	public static String getLocalDBJDBCDriverName() {
		return getValue("localdbjdbcdrivername");
	}

	public static String getDateFormat() {
		return getValue("dateformat");
	}

	public static String getKeywordsDictionaryDir() {
		return getValue("keywordsdictionarydir");
	}

	public static String getDataObjDir() {
		return getValue("dataobjdir");
	}

	public static String getDataCodeObjDir() {
		return getValue("datacodeobjdir");
	}

	public static String getDataDocDir() {
		return getValue("datadocdir");
	}

	public static String getDataLogsDir() {
		return getValue("datalogsdir");
	}

	public static String getLexicalModelFile() {
		return getValue("lexicalmodelfile");
	}

	public static String getNotificationMail() {
		return getValue("notificationmail");
	}

	public static void main(String[] args) {
		System.out.println(getStackOverflowDataDumpDir());
		System.out.println(getStackOverflowDataTempDir());
		System.out.println(getSubjectTags());
		System.out.println(getDataRootDir());
		System.out.println(getLocalDBJDBCDriverName());
		System.out.println(getLocalDBPassword());
		System.out.println(getLocalDBUrl());
		System.out.println(getLocalDBUserName());
		System.out.println(getDateFormat());
		System.out.println(getDataDocDir());
		System.out.println(getDataObjDir());
		System.out.println(getLexicalModelFile());
		System.out.println(getKeywordsDictionaryDir());
		System.out.println(getNotificationMail());
		System.out.println(getDataLogsDir());
		System.out.println(getSubjectDataDir());
		System.out.println(getTrainingSetDir());
		System.out.println(getMannuallyAnnotationDir());
		System.out.println(getCorpusDir());
		System.out.println(getTrainingCorpusDir());
	}
}
