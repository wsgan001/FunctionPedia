package cn.edu.pku.sei.tsr.dragon.stackoverflow.datadump.xmlhandler;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

public abstract class TableHandler {
	public static final Logger	logger			= Logger.getLogger(TableHandler.class);

	protected static String		outputFolderPath;
	protected String			tableFilePath	= null;
	protected StringBuilder		sqlStrBuilder	= new StringBuilder();

	public TableHandler(String tableFilePath) {
		super();
		this.tableFilePath = tableFilePath;
	}

	public void handle() throws Exception {
		// eg:"I:/data/stackoverflow.com/Posts.xml"
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(new FileInputStream(tableFilePath)));

		// xml file header
		reader.readLine();
		// root node
		reader.readLine();

		long handledRecordNum = 0;

		String record = null;
		while ((record = reader.readLine()) != null) {
			// end flag
			if (record.startsWith("</")) {
				break;
			}

			if (handledRecordNum % 100000 == 0) {
				logger.info("Handled " + handledRecordNum + " records...");
			}

			handleRecord(record);
			handledRecordNum++;

		}

		reader.close();
	}

	protected abstract void handleRecord(String record);

	public abstract void writeToTableFile();

	public static void setOutputFolderPath(String outputFolderPath) {
		TableHandler.outputFolderPath = outputFolderPath;
	}

	public static String getOutputFolderPath() {
		return outputFolderPath;
	}
}
