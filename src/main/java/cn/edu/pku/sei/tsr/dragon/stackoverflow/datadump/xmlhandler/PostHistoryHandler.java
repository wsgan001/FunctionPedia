package cn.edu.pku.sei.tsr.dragon.stackoverflow.datadump.xmlhandler;

import cn.edu.pku.sei.tsr.dragon.stackoverflow.datadump.xmlhandler.util.FileUtil;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.datadump.xmlhandler.util.XMLUtil;

@Deprecated
public class PostHistoryHandler extends TableHandler {
	// Temporarily ignore this type.
	private static final int MAX_CONTENT_LENGTH = 10 * 1024 * 1024;// 10M

	public PostHistoryHandler(String tableFilePath) {
		super(tableFilePath);
	}

	@Override
	protected void handleRecord(String postHistoryRecord) {
		String postId = XMLUtil.getPropertyValue(postHistoryRecord, "PostId");

		if (SubjectData.postIdSet.contains(postId)) {
			// System.out.println("SOPostHistory:" + postHistoryRecord);

			sqlStrBuilder.append(postHistoryRecord + "\n");

			if (sqlStrBuilder.length() > MAX_CONTENT_LENGTH) {
				FileUtil.writeToFile(outputFolderPath, "postHistory.xml", sqlStrBuilder.toString());
				sqlStrBuilder.setLength(0);
			}

			String userId = XMLUtil.getPropertyValue(postHistoryRecord, "UserId");

			if (userId != null && !userId.equals("-1")) {// -1 indicates an
															// unknown user
				SubjectData.userIdSet.add(userId);
			}
		}
	}

	@Override
	public void writeToTableFile() {
		// write SOPostHistory records into tableFilePath/postHistory.xml
		FileUtil.writeToFile(outputFolderPath, "postHistory.xml", sqlStrBuilder.toString());
		sqlStrBuilder = null;
	}
}
