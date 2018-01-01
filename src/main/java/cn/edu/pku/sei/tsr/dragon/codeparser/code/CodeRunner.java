package cn.edu.pku.sei.tsr.dragon.codeparser.code;

public class CodeRunner{
	private static long index = 0;
	private static long time_start = System.currentTimeMillis();
	private static long total_count = 0;

	public static void parseProject(String path, String name) {
//		JavaPackage javaPackage = MainParser.generatePackage(path);
//		javaPackage.writeToFile(name);
//		ContentVisitor visitor = new ContentVisitor();
//		visitor.setLibrary(name);
//		javaPackage.accept(visitor);

	}

	public static void main(String[] args) {
//		List<Pair<String, String>> projects = new ArrayList<>();
//		projects.add(new ImmutablePair<String, String>("weka-3-7-12\\weka-src\\src\\main\\java", APILibrary.WEKA));
//		projects.add(new ImmutablePair<String, String>("apache-jena-3.0.0\\lib-src\\jena-core-3.0.0-sources", APILibrary.JENA));
//		projects.add(new ImmutablePair<String, String>("apache-nutch-2.3\\src\\java", APILibrary.NUTCH));
//		projects.add(new ImmutablePair<String, String>("dom4j\\src\\main\\java", APILibrary.DOM4J));
//		projects.add(new ImmutablePair<String, String>("gson\\gson\\src\\main\\java", APILibrary.GSON));
//		projects.add(new ImmutablePair<String, String>("guava\\guava\\src", APILibrary.GUAVA));
//		projects.add(new ImmutablePair<String, String>("itext-5.5.6\\itextpdf-5.5.6-sources", APILibrary.ITEXTPDF));
//		projects.add(new ImmutablePair<String, String>("lucene-5.2.1\\core\\src\\java", APILibrary.LUCENE));
//		projects.add(new ImmutablePair<String, String>("poi-3.13\\src\\java", APILibrary.POI));
//		projects.add(new ImmutablePair<String, String>("struts-2.3.24\\src\\core\\src\\main\\java", APILibrary.STRUTS2));
//		projects.add(new ImmutablePair<String, String>("neo4j-2.3\\community\\server\\src\\main\\java", APILibrary.NEO4J));
//		projects.add(new ImmutablePair<String, String>("jfreechart-1.0.19\\source", APILibrary.JFREECHART));
//		projects.add(new ImmutablePair<String, String>("httpcomponents-client-4.5-src\\httpcomponents-client-4.5\\httpclient\\src\\main\\java", APILibrary.HTTPCLIENT));
//		projects.forEach(x -> {
//			logger.info(x.getRight() + " start");
//			long t1 = System.currentTimeMillis();
//			parseProject(Config.getDataRootDir()+ File.separator + ObjectIO.SUBJECTS + File.separator + x.getLeft(), x.getRight());
//			long t2 = System.currentTimeMillis();
//			logger.info(x.getRight() + " end in "+(t2-t1)+"ms");
//		});
	}

	public static long getTime_start() {
		return time_start;
	}

	public static void setTime_start(long time_start) {
		CodeRunner.time_start = time_start;
	}

}
