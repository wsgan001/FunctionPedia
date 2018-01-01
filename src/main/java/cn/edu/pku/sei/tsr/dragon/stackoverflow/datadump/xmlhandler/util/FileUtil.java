package cn.edu.pku.sei.tsr.dragon.stackoverflow.datadump.xmlhandler.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;


public class FileUtil {
	
	public static boolean writeToFile(String directoryPath,String fileName,String data)
	{
		checkDirAndCreateIfNotExist(directoryPath);
		
		String filePath = directoryPath + "/" + fileName;
		
		boolean flag = false;
		BufferedWriter out = null;
		
		try {
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath)));
			out.write(data);
			data = null;
			
			flag = true;
		}
		catch (IOException e) {
			System.out.println(e.getMessage());
			flag = false;
		}finally{
			try {
				out.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return flag;
	}
	
	/*
	 * check the directoryPath is a directory or not.
	 */
	private static void checkDirAndCreateIfNotExist(String directoryPath)
	{
		File file = new File(directoryPath);
		if(!file.isDirectory())
		{
			file.mkdirs();
		}
	}
	
	public static void main(String[] args) {
		FileUtil.writeToFile("I:/data2", "data.xml", "Hello,This is a test!");
	}
}
