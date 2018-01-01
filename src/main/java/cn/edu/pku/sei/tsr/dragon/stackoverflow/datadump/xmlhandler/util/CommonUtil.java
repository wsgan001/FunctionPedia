package cn.edu.pku.sei.tsr.dragon.stackoverflow.datadump.xmlhandler.util;


public class CommonUtil {
	/*
	 * generate a record as the format : "<row key1=value1 .../>" 
	 * 	example:
		  	String keys[] = {
				"Id","PostTypeId","AcceptedAnswerId"	
			};
			String values[] = {
				"11","1","1248"
			};
			System.out.println(CommonUtil.generateRecord(keys,values));
	 */
	public static String generateRecord(String keys[],String values[])
	{
		if(keys == null || values == null)
		{
			return "";
		}
		else if(keys.length != values.length)
		{
			return "";
		}
	
		String record = null;
		StringBuilder formatterBuilder = new StringBuilder("<row ");
		
		for(String key:keys)
		{
			formatterBuilder.append(key);
			formatterBuilder.append("=\"%s\" ");
		}
		
		formatterBuilder.append("/>");
		
		record = String.format(formatterBuilder.toString(), (Object[])values);
		
		return record;
	}
}
