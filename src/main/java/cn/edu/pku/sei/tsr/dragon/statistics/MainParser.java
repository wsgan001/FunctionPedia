package cn.edu.pku.sei.tsr.dragon.statistics;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONObject;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multiset;

import cn.edu.pku.sei.tsr.dragon.code.CodeParser;
import cn.edu.pku.sei.tsr.dragon.code.entity.JavaPackage;
import cn.edu.pku.sei.tsr.dragon.nlp.entity.NounKGramInfo;
import cn.edu.pku.sei.tsr.dragon.visitor.CountVisitor;
import cn.edu.pku.sei.tsr.dragon.visitor.VerbPhraseVisitor;

public class MainParser {
	private final static List<Pair<String, String>> projects = new ArrayList<>();

	static {
		final String PATH = "/home/woooking/java/projects/";
		projects.add(new ImmutablePair<>(PATH + "lucene-5.0.0/core/src/java", "lucene"));
		projects.add(new ImmutablePair<>(PATH + "apache-nutch-2.3/src/java", "nutch"));
		projects.add(new ImmutablePair<>(PATH + "httpcomponents-client-4.3.3/src", "httpclient"));
		projects.add(new ImmutablePair<>(PATH + "commons-lang3/src", "commons-lang"));
		projects.add(new ImmutablePair<>(PATH + "commons-math3/src", "commons-math"));
		projects.add(new ImmutablePair<>(PATH + "jfreechart/src", "jfreechart"));
		projects.add(new ImmutablePair<>(PATH + "apache-tomcat/java", "tomcat"));
		projects.add(new ImmutablePair<>(PATH + "poi/src", "poi"));

	}

	public static String generateJson(String path) {
		JavaPackage rootPackage = new JavaPackage("");
		CodeParser.parseDir(new File(path), rootPackage);
		return rootPackage.toJSONString();
	}

	public static JavaPackage generatePackage(String path) {
		JavaPackage rootPackage = new JavaPackage("");
		CodeParser.parseDir(new File(path), rootPackage);
		return rootPackage;
	}

	public static JavaPackage getPackageFromJson(String json) {
		JSONObject jsonObject = new JSONObject(json);
		return JavaPackage.createFromJson(jsonObject);
	}

	public static void generateVerbPhrase(JavaPackage javaPackage, String path) {
		CountVisitor countVisitor = new CountVisitor();
		javaPackage.accept(countVisitor);
		countVisitor.print();

		VerbPhraseVisitor phraseVisitor = new VerbPhraseVisitor();
		javaPackage.accept(phraseVisitor);
		phraseVisitor.serialize(path);
	}

	public static void generateAll() {
		projects.forEach(x -> {
			String path = x.getLeft();
			String projectName = x.getRight();
			System.out.println(projectName);
			String s = generateJson(path);
			try {
				PrintStream writer = new PrintStream("out/" + projectName + ".json");
				JSONObject object = new JSONObject(s);
				writer.println(object.toString(4));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		});
	}

	public static void main(String[] args) {
		VerbPhraseVisitor visitor = new VerbPhraseVisitor();
		visitor.restoreFromFile("out/poi.ser");
		List<VerbNounInfo> verbNounInfos = visitor.getPhraseList().stream()
			.map(x -> new VerbNounInfo(x))
			.collect(Collectors.toList());
		List<NounKGramInfo> nounKGramInfos = new ArrayList<>();
		verbNounInfos.forEach(verbNounInfo -> {
			nounKGramInfos.add(new NounKGramInfo(verbNounInfo.getNouns()));
		});
		ImmutableSet<Multiset.Entry<NounKGramInfo>> result = VerbNounStatistics.kgramCount(nounKGramInfos);
		int lastCount = -1;
		for (Multiset.Entry<NounKGramInfo> entry : result) {
			int count = entry.getCount();
			if (count != lastCount) {
				System.out.println(count);
				lastCount = count;
			}
			System.out.println(entry.getElement());
		}

//		// 动宾频数
//		System.out.println("========1.verb-noun phrase stat count==========");
//		ImmutableSet<Multiset.Entry<Pair<String, String>>> result1 = VerbNounStatistics.verbNounCount(verbNounInfos);
//		result1.forEach(entry -> {
//			System.out.println(entry.getElement());
//			System.out.println(entry.getCount());
//		});
//		// 动词在名词中分布
//		System.out.println("========2.verb distribution in noun-dominants==========");
//		Map<String, Multiset<String>> result2 = VerbNounStatistics.verbCountInNoun(verbNounInfos);
//		result2.forEach((noun, verbs) -> {
//			System.out.println("-------");
//			System.out.println(noun + " " + verbs.size());
//			System.out.println();
//			verbs.entrySet().forEach(entry -> {
//				System.out.println(entry.getElement() + " " + entry.getCount());
//			});
//		});
//		// 名词在动词中分布
//		System.out.println("========3.noun distribution in verb-dominants==========");
//		Map<String, Multiset<String>> result3 = VerbNounStatistics.nounCountInVerb(verbNounInfos);
//		result3.forEach((verb, nouns) -> {
//			System.out.println("-------");
//			System.out.println(verb + " " + nouns.size());
//			System.out.println();
//			nouns.entrySet().forEach(entry -> {
//				System.out.println(entry.getElement() + " " + entry.getCount());
//			});
//		});
	}
}
