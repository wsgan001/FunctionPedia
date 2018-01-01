package cn.edu.pku.sei.tsr.dragon.experiment;

import java.util.HashMap;
import java.util.Map;

import cn.edu.pku.sei.tsr.dragon.feature.entity.LibraryInfo;
import cn.edu.pku.sei.tsr.dragon.utils.Config;

public class APILibrary {
	private static Map<String, LibraryInfo>	libraries;
	private static Map<String, LibraryInfo>	library_subjects;

	public static final String				STANFORD_NLP	= "stanford-nlp";
	public static final String				WEKA			= "weka";
	public static final String				JFREECHART		= "jfreechart";
	public static final String				JSOUP			= "jsoup";
	public static final String				GSON			= "gson";
	public static final String				APACHE_POI		= "apache-poi";
	public static final String				LUCENE			= "lucene";
	public static final String				NEO4J			= "neo4j";
	public static final String				JAVAFX			= "javafx";

	public static final String				SWING			= "swing";
	public static final String				NUTCH			= "nutch";
	public static final String				JENA			= "jena";
	public static final String				HTTPCLIENT		= "httpclient";
	public static final String				HTTPCLIENT_TAG2	= "apache-commons-httpclient";
	public static final String				HTTPCLIENT_TAG3	= "apache-httpclient-4.x";
	public static final String				GUAVA			= "guava";
	public static final String				ITEXTPDF		= "itext";
	public static final String				DOM4J			= "dom4j";

	public static final String				STRUTS2			= "struts2";

	public static final String				COMMONS			= "apache-commons";
	public static final String				DEFAULT			= "default";

	static {
		libraries = new HashMap<>();

		libraries.put(LUCENE, new LibraryInfo(LUCENE));
		libraries.put(APACHE_POI, new LibraryInfo(APACHE_POI));
		libraries.put(SWING, new LibraryInfo(SWING));
		libraries.put(NEO4J, new LibraryInfo(NEO4J));
		libraries.put(NUTCH, new LibraryInfo(NUTCH));
		libraries.put(WEKA, new LibraryInfo(WEKA));
		libraries.put(JENA, new LibraryInfo(JENA));
		libraries.put(JFREECHART, new LibraryInfo(JFREECHART));
		libraries.put(HTTPCLIENT, new LibraryInfo(HTTPCLIENT));
		libraries.put(GUAVA, new LibraryInfo(GUAVA));
		libraries.put(ITEXTPDF, new LibraryInfo(ITEXTPDF));
		libraries.put(DOM4J, new LibraryInfo(DOM4J));
		libraries.put(GSON, new LibraryInfo(GSON));
		libraries.put(STRUTS2, new LibraryInfo(STRUTS2));
		libraries.put(JSOUP, new LibraryInfo(JSOUP));
		libraries.put(COMMONS, new LibraryInfo(COMMONS));

		library_subjects = new HashMap<>();

		library_subjects.put(LUCENE, new LibraryInfo(LUCENE));
		library_subjects.put(APACHE_POI, new LibraryInfo(APACHE_POI));
		library_subjects.put(NEO4J, new LibraryInfo(NEO4J));
		library_subjects.put(NUTCH, new LibraryInfo(NUTCH));
		library_subjects.put(WEKA, new LibraryInfo(WEKA));
		library_subjects.put(JENA, new LibraryInfo(JENA));
		library_subjects.put(JFREECHART, new LibraryInfo(JFREECHART));
		library_subjects.put(HTTPCLIENT, new LibraryInfo(HTTPCLIENT));
	}

	public static String[] getSubjectNames() {
		return Config.getSubjectTags().split(";");
	}

	public static String[] getAllLibraryNames() {
		String[] libraryNames = { DOM4J, GSON, GUAVA, HTTPCLIENT, ITEXTPDF, JENA, JFREECHART, JSOUP, LUCENE,
				NEO4J, NUTCH, APACHE_POI, STRUTS2, WEKA, COMMONS, SWING };
		return libraryNames;
	}

	public static String[] getSubjectLibraryNames() {
		String[] libraryNames = { HTTPCLIENT, JENA, JFREECHART, LUCENE, NEO4J, NUTCH, APACHE_POI, WEKA };
		return libraryNames;
	}

	public static Map<String, LibraryInfo> getLibraries() {
		return libraries;
	}

	public static LibraryInfo getLibrary(String libraryTag) {
		String libTag = judgeProjectByTags(libraryTag);
		if (libTag == null)
			return null;
		else
			return libraries.get(libraryTag);
	}

	public static String judgeProjectByTags(String tagString) {
		if (tagString == null)
			return null;

		String projectName;
		if (tagString.contains(APILibrary.APACHE_POI))
			projectName = APILibrary.APACHE_POI;
		else if (tagString.contains(APILibrary.NEO4J))
			projectName = APILibrary.NEO4J;
		else if (tagString.contains(APILibrary.LUCENE))
			projectName = APILibrary.LUCENE;
		else if (tagString.contains(APILibrary.SWING))
			projectName = APILibrary.SWING;
		else if (tagString.contains(APILibrary.NUTCH))
			projectName = APILibrary.NUTCH;
		else if (tagString.contains(APILibrary.HTTPCLIENT) || tagString.contains(APILibrary.HTTPCLIENT_TAG2)
				|| tagString.contains(APILibrary.HTTPCLIENT_TAG3))
			projectName = APILibrary.HTTPCLIENT;
		else if (tagString.contains(APILibrary.WEKA))
			projectName = APILibrary.WEKA;
		else if (tagString.contains(APILibrary.JENA))
			projectName = APILibrary.JENA;
		else if (tagString.contains(APILibrary.STRUTS2))
			projectName = APILibrary.STRUTS2;
		else if (tagString.contains(APILibrary.GSON))
			projectName = APILibrary.GSON;
		else if (tagString.contains(APILibrary.DOM4J))
			projectName = APILibrary.DOM4J;
		else if (tagString.contains(APILibrary.ITEXTPDF))
			projectName = APILibrary.ITEXTPDF;
		else if (tagString.contains(APILibrary.GUAVA))
			projectName = APILibrary.GUAVA;
		else if (tagString.contains(APILibrary.JFREECHART))
			projectName = APILibrary.JFREECHART;
		else if (tagString.contains(APILibrary.JSOUP))
			projectName = APILibrary.JSOUP;
		else if (tagString.contains(APILibrary.COMMONS))
			projectName = APILibrary.COMMONS;
		else
			projectName = APILibrary.DEFAULT;
		return projectName;
	}

	public static void main(String[] args) {
		System.out.println(judgeProjectByTags(SWING));
		System.out.println(judgeProjectByTags("" + HTTPCLIENT_TAG2 + "," + GUAVA + NEO4J));
		System.out.println(getLibraries());
		String str = "dom4j project";
		System.out.println(getLibrary(str));
		System.out.println(getLibrary(judgeProjectByTags(str)));

		str = ITEXTPDF + "";
		System.out.println(getLibrary(str));
		System.out.println(getLibrary(judgeProjectByTags(str)));

		System.out.println();
		String[] strs = getAllLibraryNames();
		for (String string : strs) {
			System.out.println(string);
		}
		System.out.println();
	}

}
