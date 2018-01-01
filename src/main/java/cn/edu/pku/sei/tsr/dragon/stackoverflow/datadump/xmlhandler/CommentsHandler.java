package cn.edu.pku.sei.tsr.dragon.stackoverflow.datadump.xmlhandler;

import cn.edu.pku.sei.tsr.dragon.stackoverflow.datadump.xmlhandler.util.FileUtil;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.datadump.xmlhandler.util.XMLUtil;

public class CommentsHandler extends TableHandler {
	public CommentsHandler(String tableFilePath) {
		super(tableFilePath);
	}
	
	@Override
	protected void handleRecord(String commentRecord) {
		String postId = XMLUtil.getPropertyValue(commentRecord, "PostId");

		if (SubjectData.postIdSet.contains(postId)) {
			// add comment record to commentsBuilder
			sqlStrBuilder.append(commentRecord + "\n");

			// get user's userId who adds the commentRecord,then add it to
			// userset
			String userId = XMLUtil.getPropertyValue(commentRecord, "UserId");
			if (userId != null && !userId.equals("-1")) {
				SubjectData.userIdSet.add(userId);
			}
		}
	}

	@Override
	public void writeToTableFile() {
		// write comments records into tableFilePath/comments.xml
		FileUtil.writeToFile(outputFolderPath, "comments.xml", sqlStrBuilder.toString());
	}

}
