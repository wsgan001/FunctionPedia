package cn.edu.pku.sei.tsr.dragon.stackoverflow.dao.so;

import java.sql.Connection;
import java.util.List;

import org.apache.log4j.Logger;

import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.so.SOTag;
import cn.edu.pku.sei.tsr.dragon.utils.database.CommonDAO;

public class SOTagDAO extends CommonDAO<SOTag, Integer> {
	public static final Logger	logger			= Logger.getLogger(SOTagDAO.class);

	public static final String	FIELD_TAG_NAME	= "TagName";

	public SOTagDAO(Connection connection) {
		super(SOTag.class, SOTag.TABLE_NAME, connection);
	}

	public List<SOTag> getTagListByTagName(String tagName) {
		return getBy(FIELD_TAG_NAME, tagName);
	}

	public SOTag getTagByTagName(String tagName) {
		List<SOTag> soTags = getTagListByTagName(tagName);
		if (soTags != null && soTags.size() > 0)
			return soTags.get(0);
		return null;
	}

	public int getTagIdByTagName(String tagName) {
		try {
			return getTagByTagName(tagName).getId();
		}
		catch (NullPointerException e) {
			logger.error(tagName + " not found this tag!");
			e.printStackTrace();
			return -1;
		}
	}

	public int[] getTagIdsByTagName(String[] tagNames) {
		int[] tags = new int[tagNames.length];
		for (int i = 0; i < tagNames.length; i++) {
			tags[i] = getTagIdByTagName(tagNames[i]);
			if (tags[i] == -1)
				i--;
		}
		return tags;
	}
}
