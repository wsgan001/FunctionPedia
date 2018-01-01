package cn.edu.pku.sei.tsr.dragon.search.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

import org.apache.log4j.Logger;


public class ObjectIO {
	private static final Logger	logger	= Logger.getLogger(ObjectIO.class);
	public static Object readObject(File file) {
		ObjectInputStream objIn;
		try {
			if (file == null || !file.exists())
				return null;

			objIn = new ObjectInputStream(new FileInputStream(file));
			Object object = objIn.readObject();
			objIn.close();
			objIn = null;
			return object;
		}
		catch (Exception e) {
			logger.error(file.getAbsolutePath());
			// e.printStackTrace();
			objIn = null;
			return null;
		}
	}
}
