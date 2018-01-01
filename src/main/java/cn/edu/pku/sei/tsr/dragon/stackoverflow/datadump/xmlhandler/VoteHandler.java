package cn.edu.pku.sei.tsr.dragon.stackoverflow.datadump.xmlhandler;

import cn.edu.pku.sei.tsr.dragon.stackoverflow.datadump.xmlhandler.util.FileUtil;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.datadump.xmlhandler.util.XMLUtil;

public class VoteHandler extends TableHandler {

	public VoteHandler(String tableFilePath) {
		super(tableFilePath);
	}

	@Override
	protected void handleRecord(String voteRecord) {
		String postId = XMLUtil.getPropertyValue(voteRecord, "PostId");

		if (SubjectData.postIdSet.contains(postId)) {
			sqlStrBuilder.append(voteRecord + "\n");

			String userId = XMLUtil.getPropertyValue(voteRecord, "UserId");
			if (userId != null && !userId.equals("-1")) {
				SubjectData.userIdSet.add(userId);
			}
		}
	}

	@Override
	public void writeToTableFile() {
		// write votes records into tableFilePath/votes.xml
		FileUtil.writeToFile(outputFolderPath, "votes.xml", sqlStrBuilder.toString());
		sqlStrBuilder = null;
	}
}
