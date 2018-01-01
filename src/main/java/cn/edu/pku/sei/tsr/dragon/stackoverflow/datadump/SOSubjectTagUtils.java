package cn.edu.pku.sei.tsr.dragon.stackoverflow.datadump;

import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.sei.tsr.dragon.utils.Config;

public class SOSubjectTagUtils {
	public static String[]		subjectTags;
	static {
		SOSubjectTagUtils.subjectTags = Config.getSubjectTags().split(";");
	}
	
	public static void main(String[] args) {
		
	}

	public static boolean checkSubjectTags(String questionTags) {
		boolean flag = false;
		for (String subjectTag : SOSubjectTagUtils.subjectTags) {
			if (questionTags.indexOf("<" + subjectTag+">") >= 0) {
				flag = true;
				break;
			}
		}
		return flag;
	}

	public static String[] extractSubjectTags(String questionTags) {
		List<String> tagList=new ArrayList<>();
		for (String subjectTag : SOSubjectTagUtils.subjectTags) {
			if (questionTags.indexOf("<" + subjectTag+">") >= 0) {
				tagList.add(subjectTag);
			}
		}
		return tagList.toArray(new String[tagList.size()]);
	}

	public static String[] splitTags(String tagstr) {
		String[] rawTags = tagstr.split("<|>");
		List<String> tags = new ArrayList<>();
		for (int i = 0; i < rawTags.length; i++) {
			if (!"".equals(rawTags[i]))
				tags.add(rawTags[i]);
		}
		return tags.toArray(new String[tags.size()]);
	}
}
