package cn.edu.pku.sei.tsr.dragon.stackoverflow.dao.so;

import java.sql.Connection;
import java.util.List;

import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.so.SOPostHistory;
import cn.edu.pku.sei.tsr.dragon.utils.database.CommonDAO;

public class SOPostHistoryDAO extends CommonDAO<SOPostHistory, Integer> {
	public static final String	FIELD_POST_ID	= "PostId";
	public static final String	FIELD_USER_ID	= "UserId";

	public SOPostHistoryDAO(Connection conn) {
		super(SOPostHistory.class, SOPostHistory.TABLE_NAME, conn);
	}

	public List<SOPostHistory> getByPostId(int id) {
		return getBy(FIELD_POST_ID, id);
	}

	public List<SOPostHistory> getByUserId(int id) {
		return getBy(FIELD_USER_ID, id);
	}
}
