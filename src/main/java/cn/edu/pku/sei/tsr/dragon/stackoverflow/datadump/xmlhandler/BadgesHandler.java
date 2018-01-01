package cn.edu.pku.sei.tsr.dragon.stackoverflow.datadump.xmlhandler;

import cn.edu.pku.sei.tsr.dragon.stackoverflow.datadump.xmlhandler.util.FileUtil;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.datadump.xmlhandler.util.XMLUtil;

public class BadgesHandler extends TableHandler {
	public BadgesHandler(String tableFilePath) {
		super(tableFilePath);
	}

	@Override
	protected void handleRecord(String badgeRecord) {
		String userId = XMLUtil.getPropertyValue(badgeRecord, "UserId");

		if (SubjectData.userIdSet.contains(userId)) {
			// System.out.println("Badges:" + badgeRecord);
			sqlStrBuilder.append(badgeRecord + "\n");
		}
	}

	@Override
	public void writeToTableFile() {
		// write badges records into tableFilePath/badges.xml
		FileUtil.writeToFile(outputFolderPath, "badges.xml", sqlStrBuilder.toString());
		sqlStrBuilder = null;
	}
}
