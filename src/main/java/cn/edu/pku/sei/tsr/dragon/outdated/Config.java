package cn.edu.pku.sei.tsr.dragon.outdated;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

public class Config {
	private static Document doc = null;

	static {
		try {
			SAXReader reader = new SAXReader();
			// You cannot use File inside a JAR file. You need to use InputStream to read the text
			// data.
			// InputStream in = Config.class.getClassLoader().getResourceAsStream("config.xml");
			InputStream in = new FileInputStream(new File(
					"src/cn/edu/pku/sei/tsr/dragon/stackoverflow/datadump/config.xml"));

			doc = reader.read(in);
		}
		catch (DocumentException | FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		System.out.println(getInputPath());
		System.out.println(getOutputPath());
		for (String str : getCaredTags()) {
			System.out.println(str);
		}
		System.out.println(Config.getNotificationMailAddress());
		System.out.println(Config.getDbUser());
		System.out.println(Config.getDbPass());
		System.out.println(Config.getHost());
		System.out.println(Config.getPort());
		System.out.println(Config.getDatabaseName());
	}

	public static String getDbUser() {
		return getSingleValue("/configuration/database-config/dbUser");
	}

	public static String getDbPass() {
		return getSingleValue("/configuration/database-config/dbPass");
	}

	public static String getHost() {
		return getSingleValue("/configuration/database-config/host");
	}

	public static String getPort() {
		return getSingleValue("/configuration/database-config/port");
	}

	public static String getDatabaseName() {
		return getSingleValue("/configuration/database-config/databaseName");
	}

	public static String getNotificationMailAddress() {
		return getSingleValue("/configuration/notification-mail");
	}

	public static String getInputPath() {
		return getSingleValue("/configuration/input-path");
	}

	public static String getOutputPath() {
		return getSingleValue("/configuration/output-path");
	}

	public static String[] getCaredTags() {
		String tagsAsString = getSingleValue("/configuration/cared-tags");
		if (tagsAsString != null) {
			String rawTags[] = tagsAsString.split(";");
			List<String> caredTags = new ArrayList<String>();
			for (String rawTag : rawTags) {
				rawTag = rawTag.trim();
				if (rawTag.isEmpty()) {
					continue;
				}
				caredTags.add(rawTag);
			}

			return caredTags.toArray(new String[0]);
		}
		else {
			return new String[] {};
		}
	}

	private static String getSingleValue(String key) {
		if (doc == null) {
			return null;
		}

		Node node = doc.selectSingleNode(key);

		if (node == null) {
			return null;
		}

		String value = node.getText();
		if (value != null) {
			value = value.trim();
		}

		return value;
	}
}