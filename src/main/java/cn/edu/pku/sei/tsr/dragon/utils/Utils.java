package cn.edu.pku.sei.tsr.dragon.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;

public class Utils {

	public static String getLineSeparator() {
		return System.getProperty("line.separator");
	}
//
//	public static String wordsConjuction(List<String> words) {
//		StringBuilder conj = new StringBuilder();
//		for (int i = 0; i < words.size(); i++) {
//			String keyword = words.get(i);
//			if (i > 0)
//				conj.append("|");
//			conj.append(keyword);
//		}
//		return conj.toString();
//	}

	public static void main(String args[]) {
		System.out.println(getLineSeparator());
		System.out.println(File.separator);
		// Tree tree = StanfordParser
		// .parseTree("I've (call) <him> {cute} \"boy\", that cannot be terrible
		// whole/parts; the Smiths' invited us to go, wasn't [a] 'you'?!!!...");
		// tree.pennPrint();
		// System.out.println(TreeUtils.interpretTreeToString(tree));

		List<Integer> list = new ArrayList<>();
		list.add(1232);
		list.add(232);
		list.add(564);
		list = null;
		int[] arr = convertIntegerListToIntArray(list);
		System.err.println("begin");
		System.err.println(arr);
		for (int i = 0; i < arr.length; i++) {
			int j = arr[i];
			System.err.println(j);
		}
		System.err.println("end");
	}

	public static int[] convertIntegerListToIntArray(List<Integer> intList) {
		try {
			return ArrayUtils.toPrimitive(intList.toArray(new Integer[intList.size()]));
		}
		catch (NullPointerException e) {
			e.printStackTrace();
			return new int[] {};
		}
	}

	public static int[] convertIntegerSetToIntArray(Set<Integer> intList) {
		try {
			return ArrayUtils.toPrimitive(intList.toArray(new Integer[intList.size()]));
		}
		catch (NullPointerException e) {
			e.printStackTrace();
			return new int[] {};
		}
	}
}
