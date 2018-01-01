package cn.edu.pku.sei.tsr.dragon.content;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class JavaDocParser {

	private static String processLine(String line) {
		line = line.trim();
		line = line.replaceAll("^/\\*\\*", "");
		line = line.replaceAll("^\\* ", "");
		line = line.replaceAll("^\\*/", "");

		line = line.replaceAll("<(.*?)>", "");

		line = line.replaceAll("\\{@link(.*?)\\}", "$1");
		line = line.replaceAll("\\{@code(.*?)\\}", "$1");

		line = line.replaceAll("^@since.*$", "");
		line = line.replaceAll("^@version.*$", "");
		line = line.replaceAll("^@see.*$", "");
		line = line.replaceAll("^@param.*$", "");
		line = line.replaceAll("^@return.*$", "");
		line = line.replaceAll("^@author.*$", "");
		line = line.replaceAll("^@deprecated.*$", "");
		line = line.replaceAll("^@exception.*$", "");
		line = line.replaceAll("^@throws.*$", "");
		line = line.replaceAll("^@see.*$", "");
		return line;
	}

	public static String parseJavaDoc(String javaDoc) {
		//去掉无意义的空行，然后以空格连接各行
		List<String> lines = Arrays.asList(javaDoc.split("\n"));
		return lines.stream().map(JavaDocParser::processLine).filter(line -> !line.equals("")).collect(Collectors.joining(" "));
	}
}
