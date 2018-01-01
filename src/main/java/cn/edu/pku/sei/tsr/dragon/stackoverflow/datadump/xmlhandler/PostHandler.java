package cn.edu.pku.sei.tsr.dragon.stackoverflow.datadump.xmlhandler;

import cn.edu.pku.sei.tsr.dragon.stackoverflow.datadump.SOSubjectTagUtils;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.datadump.xmlhandler.util.FileUtil;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.datadump.xmlhandler.util.XMLUtil;

public class PostHandler extends TableHandler {

	private static final String	POST_TYPE_QUESION	= "1";
	private static final String	POST_TYPE_ANSWER	= "2";
	
	public PostHandler(String tableFilePath) {
		super(tableFilePath);
	}

	@Override
	protected void handleRecord(String record) {
		// Get post type(Question or Answer)
		String postTypeId = XMLUtil.getPropertyValue(record, "PostTypeId");

		if (POST_TYPE_QUESION.equals(postTypeId))
			handleQuestionRecord(record);
		else if (POST_TYPE_ANSWER.equals(postTypeId))
			handleAnswerRecord(record);
	}

	private void handleQuestionRecord(String questionRecord) {
		/* the same with POSTs's attribute name */
		String tags = XMLUtil.getPropertyValue(questionRecord, "Tags");

		// handle the record which contains subject tags
		if (SOSubjectTagUtils.checkSubjectTags(tags)) {
			// System.out.println("Quesion:" + questionRecord);

			// add question record to questionsBuilder
			sqlStrBuilder.append(questionRecord + "\n");

			String questionId = XMLUtil.getPropertyValue(questionRecord, "Id");

			// add questionId to SubjectDataHandler.questionIdSet for finding its
			// answers
			SubjectData.questionIdSet.add(questionId);
			// add questionId to SubjectDataHandler.postIdSet for finding its comments
			SubjectData.postIdSet.add(questionId);

			/* "OwnerUserId" is same with POSTs' column name for a user's id */
			String ownerId = XMLUtil.getPropertyValue(questionRecord, "OwnerUserId");
			String lastEditorId = XMLUtil.getPropertyValue(questionRecord, "LastEditorUserId");

			// add userId to SubjectDataHandler.userIdSet
			if (ownerId != null && !ownerId.equals("-1"))
				SubjectData.userIdSet.add(ownerId);
			if (lastEditorId != null && !lastEditorId.equals("-1"))
				SubjectData.userIdSet.add(lastEditorId);
		}
	}

	private void handleAnswerRecord(String answerRecord) {
		/* the same with POSTs' property name */
		String questionId = XMLUtil.getPropertyValue(answerRecord, "ParentId");

		// judge whether the answerRecord is an answer of an question which
		// contains seachTag
		if (SubjectData.questionIdSet.contains(questionId)) {
			// System.out.println("Answer:" + answerRecord);

			// add the answer record to answersBuilder
			sqlStrBuilder.append(answerRecord + "\n");

			// get answerId then add it to SubjectDataHandler.postIdSet
			String answerId = XMLUtil.getPropertyValue(answerRecord, "Id");
			SubjectData.postIdSet.add(answerId);

			// get the id of the man whose add the answer,then add the userId to
			// SubjectDataHandler.userIdSet
			String ownerId = XMLUtil.getPropertyValue(answerRecord, "OwnerUserId");
			String lastEditorId = XMLUtil.getPropertyValue(answerRecord, "LastEditorUserId");

			// add userId to SubjectDataHandler.userIdSet -1 indicates an unknown user
			if (ownerId != null && !ownerId.equals("-1"))
				SubjectData.userIdSet.add(ownerId);
			if (lastEditorId != null && !lastEditorId.equals("-1"))
				SubjectData.userIdSet.add(lastEditorId);
		}
	}

	@Override
	public void writeToTableFile() {
		FileUtil.writeToFile(outputFolderPath, "posts.xml", sqlStrBuilder.toString());
		sqlStrBuilder = null;
	}
}
