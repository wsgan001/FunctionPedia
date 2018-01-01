package cn.edu.pku.sei.tsr.dragon.stackoverflow.datadump.xmlhandler.util;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;

public class XMLUtil {

	private static final String XML_HADER = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<records>\n";
	private static final String XML_TAILER = "</records>";

	/*
	 * Get property value
	 */
	public static String getPropertyValue(String xmlData, String propertyName) {

		String propertyValue = null;
		try {
			Document doc = DocumentHelper.parseText(xmlData);
			//@propertyName --> select all attributes @propertyName
			Node node = doc.selectSingleNode("//@" + propertyName);
			if (node == null){
				return null;
			}
			propertyValue = node.getText();
		} catch (DocumentException e) {
			System.err.println(e.getMessage());
		}

		return propertyValue;
	}

	public static String getXMLFormatData(String records[]) {
		StringBuilder xmlDataBuilder = new StringBuilder(XML_HADER);

		for (String record : records) {
			xmlDataBuilder.append(record);
			xmlDataBuilder.append("\n");
		}

		xmlDataBuilder.append(XML_TAILER);

		return xmlDataBuilder.toString();
	}

	/*
	 * generate a record as the format : "<row key1=value1 .../>" example:
	 * String keys[] = { "Id","PostTypeId","AcceptedAnswerId" }; String values[]
	 * = { "11","1","1248" };
	 * System.out.println(CommonUtil.generateRecord(keys,values));
	 */
	public static String generateRecordAsXML(String keys[], String values[]) {
		if (keys == null || values == null) {
			return "";
		} else if (keys.length != values.length) {
			return "";
		}

		String record = null;
		StringBuilder formatterBuilder = new StringBuilder("<row ");

		for (String key : keys) {
			formatterBuilder.append(key);
			formatterBuilder.append("=\"%s\" ");
		}

		formatterBuilder.append("/>");

		record = String.format(formatterBuilder.toString(), (Object[]) values);

		return record;
	}

}
