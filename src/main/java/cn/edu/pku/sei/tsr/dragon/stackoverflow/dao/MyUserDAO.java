package cn.edu.pku.sei.tsr.dragon.stackoverflow.dao;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.UserInfo;
import cn.edu.pku.sei.tsr.dragon.utils.database.CommonDAO;

public class MyUserDAO extends CommonDAO<UserInfo, Integer> {
	public static final String	FIELD_ID						= "Id";
	public static final String	FIELD_DISPLAY_NAME				= "DisplayName";
	public static final String	FIELD_REPUTATION				= "Reputation";
	public static final String	FIELD_BADGES_COUNT_1ST_CLASS	= "BadgesCountFirstClass";
	public static final String	FIELD_BADGES_COUNT_2ND_CLASS	= "BadgesCountSecondClass";
	public static final String	FIELD_BADGES_COUNT_3RD_CLASS	= "BadgesCountThirdClass";
	public static final String	FIELD_ASKED_QUESTIONS_COUNT		= "AskedQuestionsCount";
	public static final String	FIELD_ANSWERS_COUNT				= "AnswersCount";
	public static final String	FIELD_ACCEPTED_ANSWERS_COUNT	= "AcceptedAnswersCount";
	public static final String	FIELD_VIEWS						= "Views";
	public static final String	FIELD_UP_VOTES					= "UpVotes";
	public static final String	FIELD_DOWN_VOTES				= "DownVotes";

	public MyUserDAO(Connection connection) {
		super(UserInfo.class, UserInfo.TABLE_NAME, connection);
	}

	public UserInfo addUser(UserInfo user) {
		try {
			List<String> keys = new ArrayList<>();
			List<Object> values = new ArrayList<>();

			keys.add(FIELD_ID);
			values.add(user.getId());

			keys.add(FIELD_DISPLAY_NAME);
			values.add(user.getDisplayName());

			keys.add(FIELD_REPUTATION);
			values.add(user.getReputation());

			keys.add(FIELD_BADGES_COUNT_1ST_CLASS);
			values.add(user.getBadgesCountFirstClass());

			keys.add(FIELD_BADGES_COUNT_2ND_CLASS);
			values.add(user.getBadgesCountSecondClass());

			keys.add(FIELD_BADGES_COUNT_3RD_CLASS);
			values.add(user.getBadgesCountThirdClass());

			keys.add(FIELD_ASKED_QUESTIONS_COUNT);
			values.add(user.getAskedQuestionsCount());

			keys.add(FIELD_ANSWERS_COUNT);
			values.add(user.getAnswersCount());
			
			keys.add(FIELD_ACCEPTED_ANSWERS_COUNT);
			values.add(user.getAcceptedAnswersCount());

			keys.add(FIELD_VIEWS);
			values.add(user.getViews());

			keys.add(FIELD_UP_VOTES);
			values.add(user.getUpVotes());

			keys.add(FIELD_DOWN_VOTES);
			values.add(user.getDownVotes());

			int resultCode = insert(keys.toArray(), values.toArray());
			if (resultCode <= 0)
				return null;
			return user;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
