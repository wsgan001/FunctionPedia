package cn.edu.pku.sei.tsr.dragon.nlp.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SegmentWords {
	public static List<String> segmentWords(String name) {

		List<String> splitWords = new ArrayList<>();

		String[] subStrings = Pattern.compile("_").split(name);
		for (String str : subStrings) {
			List<Integer> camelPos = new ArrayList<>();
			camelPos.add(0);
			for (int i = 1; i < str.length(); i++) {
				if (str.substring(i, i + 1).matches("[A-Z]")) {
					try {
						if (!str.substring(i - 1, i).matches("[A-Z]")
								|| !str.substring(i + 1, i + 2).matches("[A-Z]"))
							camelPos.add(i);
					}
					catch (StringIndexOutOfBoundsException e) {
						if (i + 1 < str.length())
							if (!str.substring(i + 1).matches("[A-Z]"))
								camelPos.add(i);
					}
				}
				else if (str.substring(i, i + 1).matches("[A-Z|a-z]")) {
					if (str.substring(i - 1, i).matches("[0-9]"))
						camelPos.add(i);
				}
			}

			for (int i = 0; i < camelPos.size() - 1; i++) {
				splitWords.add(str.substring(camelPos.get(i), camelPos.get(i + 1)).toLowerCase());
			}
			splitWords.add(str.substring(camelPos.get(camelPos.size() - 1)).toLowerCase());
		}

		return splitWords;
	}
}
