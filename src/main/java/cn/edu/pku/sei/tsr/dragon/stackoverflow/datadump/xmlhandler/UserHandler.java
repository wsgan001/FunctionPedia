package cn.edu.pku.sei.tsr.dragon.stackoverflow.datadump.xmlhandler;

import cn.edu.pku.sei.tsr.dragon.stackoverflow.datadump.xmlhandler.util.FileUtil;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.datadump.xmlhandler.util.XMLUtil;

public class UserHandler extends TableHandler {
	public UserHandler(String tableFilePath) {
		super(tableFilePath);
	}

	@Override
	protected void handleRecord(String userRecord) {
		// get userId
		String userId = XMLUtil.getPropertyValue(userRecord, "Id");

		// add the record of the user who is in SubjectDataHandler.userIdSet to
		// sqlStrBuilder
		if (SubjectData.userIdSet != null && SubjectData.userIdSet.contains(userId)) {
			sqlStrBuilder.append(userRecord + "\n");
		}
	}

	@Override
	public void writeToTableFile() {
		// write users records into tableFilePath/users.xml
		FileUtil.writeToFile(outputFolderPath, "users.xml", sqlStrBuilder.toString());
	}

}
