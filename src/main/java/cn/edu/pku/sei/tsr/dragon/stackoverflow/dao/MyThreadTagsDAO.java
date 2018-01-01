package cn.edu.pku.sei.tsr.dragon.stackoverflow.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.ThreadTagInfo;
import cn.edu.pku.sei.tsr.dragon.utils.database.CommonDAO;

public class MyThreadTagsDAO extends CommonDAO<ThreadTagInfo, Integer> {
	public static final String	FIELD_THREAD_ID	= "ThreadId";
	public static final String	FIELD_TAG_ID	= "TagId";

	public MyThreadTagsDAO(Connection connection) {
		super(ThreadTagInfo.class, ThreadTagInfo.TABLE_NAME, connection);
	}

	public static void main(String[] args) {
	}

	public int addThreadTag(int threadId, int tagId) {
		try {
			return insert(new String[] { FIELD_THREAD_ID, FIELD_TAG_ID },
					new Integer[] { threadId, tagId });
		}
		catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

	public ThreadTagInfo addThreadTag(ThreadTagInfo threadTagLink) {
		try {
			int resultCode = insert(new String[] { FIELD_THREAD_ID, FIELD_TAG_ID },
					new Integer[] { threadTagLink.getThreadId(), threadTagLink.getTagId() });
			if (resultCode <= 0)
				return null;
			return threadTagLink;
		}
		catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<ThreadTagInfo> getByTagId(int id) {
		return getBy(FIELD_TAG_ID, id);
	}

	public List<ThreadTagInfo> getByThreadId(int id) {
		return getBy(FIELD_THREAD_ID, id);
	}
}
