package cn.edu.pku.sei.tsr.dragon.wordscluster;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/*
 Tokenization
 Author: Thanh Ngoc Dao - Thanh.dao@gmx.net
 Copyright (c) 2005 by Thanh Ngoc Dao.
 */

/// <summary>
/// Summary description for Tokeniser.
/// Partition string into SUBwords
/// </summary>
public class Tokeniser implements ITokeniser {

	// / <summary>
	// / �Կհ��ַ����м򵥷ִʣ������Դ�Сд��
	// / ʵ������п������������ķִ��㷨
	// / </summary>
	// / <param name="input"></param>
	// / <returns></returns>

	public Tokeniser() {
	}

	@Override
	public List<String> partition(String input) {

		String r = "([ \\t{}():;. \n])";
		input = input.toLowerCase();
		Pattern p = Pattern.compile(r);

		String[] tokens = input.split(r);

		List<String> filter = new ArrayList<String>();

		for (int i = 0; i < tokens.length; i++) {
			//System.out.println(tokens[i]);
			//Matcher m = p.matcher(tokens[i]);
			//if (m.groupCount() == 0 && tokens[i].trim().length() > 0
			String token=tokens[i];
			//System.out.println(token);
			if (token.trim().length() > 0&& !StopWordsHandler.IsStopword(token)) {
				System.out.println(token);
				filter.add(token);
			}

			/*
			 * MatchCollection mc=r.matches(tokens[i]); if (mc.Count <= 0 &&
			 * tokens[i].Trim().Length > 0 && !StopWordsHandler.IsStopword
			 * (tokens[i]) ) filter.add(tokens[i]) ; }
			 */

		}
		return filter;
	}
}
