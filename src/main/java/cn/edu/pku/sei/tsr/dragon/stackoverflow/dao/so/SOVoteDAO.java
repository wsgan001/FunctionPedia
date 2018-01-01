package cn.edu.pku.sei.tsr.dragon.stackoverflow.dao.so;

import java.sql.Connection;
import java.util.List;

import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.so.SOVote;
import cn.edu.pku.sei.tsr.dragon.utils.database.CommonDAO;
import cn.edu.pku.sei.tsr.dragon.utils.database.DBConnPool;

public class SOVoteDAO extends CommonDAO<SOVote, Integer> {
	public static final String	FIELD_POST_ID		= "PostId";
	public static final String	FIELD_USER_ID		= "UserId";
	public static final String	FIELD_VOTE_TYPE_ID	= "VoteTypeID";

	public SOVoteDAO(Connection connection) {
		super(SOVote.class, SOVote.TABLE_NAME, connection);
	}

	public List<SOVote> getByPostId(int id) {
		return getBy(FIELD_POST_ID, id);
	}

	public List<SOVote> getByUserId(int id) {
		return getBy(FIELD_USER_ID, id);
	}

	public List<SOVote> getByVoteTypeId(int id) {
		return getBy(FIELD_VOTE_TYPE_ID, id);
	}

	public static void main(String[] args) {
		SOVoteDAO soVoteDAO = new SOVoteDAO(DBConnPool.getConnection());
		List<SOVote> q = soVoteDAO.getAll();
		for (SOVote soVote : q) {
			log.info("\nVote　" + soVote.getId());
			log.info(soVote.getPostId());
			log.info(soVote.getVoteTypeId());
			log.info(soVote.getCreationDate());
		}
	}

}
