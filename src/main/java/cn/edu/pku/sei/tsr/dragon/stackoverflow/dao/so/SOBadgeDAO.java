package cn.edu.pku.sei.tsr.dragon.stackoverflow.dao.so;

import java.sql.Connection;
import java.util.List;

import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.so.SOBadge;
import cn.edu.pku.sei.tsr.dragon.utils.database.CommonDAO;

public class SOBadgeDAO extends CommonDAO<SOBadge, Integer> {
	public static final String	FIELD_USER_ID	= "UserId";
	public static final String	FIELD_CLASS		= "Class";

	private String				countBadgeSql;

	public SOBadgeDAO(Connection conn) {
		super(SOBadge.class, SOBadge.TABLE_NAME, conn);
		countBadgeSql = "SELECT count(*) FROM " + tableName + " WHERE " + FIELD_USER_ID
				+ " = ? and " + FIELD_CLASS + " = ?";
	}

	public List<SOBadge> getByUserId(int id) {
		return getBy(FIELD_USER_ID, id);
	}

	public int countFirstClassBadgeByUserId(int id) {
		return super.count(countBadgeSql, id, 1);
	}

	public int countSecondClassBadgeByUserId(int id) {
		return super.count(countBadgeSql, id, 2);
	}

	public int countThirdClassBadgeByUserId(int id) {
		return super.count(countBadgeSql, id, 3);
	}
}
