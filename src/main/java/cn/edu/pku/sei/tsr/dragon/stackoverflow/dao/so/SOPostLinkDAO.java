package cn.edu.pku.sei.tsr.dragon.stackoverflow.dao.so;

import java.sql.Connection;
import java.util.List;

import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.so.SOPostLink;
import cn.edu.pku.sei.tsr.dragon.utils.database.CommonDAO;

public class SOPostLinkDAO extends CommonDAO<SOPostLink, Integer> {
	public static final String	FIELD_POST_ID			= "PostId";
	public static final String	FIELD_RELATED_POST_ID	= "RelatedPostId";

	public SOPostLinkDAO(Connection connection) {
		super(SOPostLink.class, SOPostLink.TABLE_NAME, connection);
	}

	public List<SOPostLink> getByPostId(int id) {
		return getBy(FIELD_POST_ID, id);
	}

	public List<SOPostLink> getByRelatedPostId(int id) {
		return getBy(FIELD_RELATED_POST_ID, id);
	}
}
