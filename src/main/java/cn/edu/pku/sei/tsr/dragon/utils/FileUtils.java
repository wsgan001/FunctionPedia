package cn.edu.pku.sei.tsr.dragon.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileUtils {

	public static String getFileContent(File file) {
		InputStream is = null;
		try {
			is = new FileInputStream(file);
		} catch (FileNotFoundException e) {
		}
		byte[] bytes = new byte[(int) file.length() + 10];
		int offset = 0;
		int numRead;
		try {
			while ((offset < bytes.length) && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
				offset += numRead;
			}
			is.close();
		} catch (Exception e) {
		}
		FileInputStream fInputStream;
		InputStreamReader isr = null;
		try {
			fInputStream = new FileInputStream(file);
			isr = new InputStreamReader(fInputStream, "UTF-8");
		} catch (Exception e) {
		}
		StringBuffer str = new StringBuffer("");
		String tmp;
		BufferedReader in = new BufferedReader(isr);
		try {
			while ((tmp = in.readLine()) != null) {
				str.append(tmp + "\n");
			}
		} catch (Exception e) {
		}
		return str.toString();
	}
}
