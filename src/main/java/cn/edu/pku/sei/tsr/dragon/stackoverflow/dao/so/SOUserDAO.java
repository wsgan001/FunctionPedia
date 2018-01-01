package cn.edu.pku.sei.tsr.dragon.stackoverflow.dao.so;

import java.sql.Connection;
import java.util.List;

import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.so.SOUser;
import cn.edu.pku.sei.tsr.dragon.utils.database.CommonDAO;
import cn.edu.pku.sei.tsr.dragon.utils.database.DBConnPool;

public class SOUserDAO extends CommonDAO<SOUser, Integer> {

	public SOUserDAO(Connection connection) {
		super(SOUser.class, SOUser.TABLE_NAME, connection);
	}

	public static void main(String[] args) {
		SOUserDAO soUserDAO = new SOUserDAO(DBConnPool.getConnection());
		List<SOUser> q = soUserDAO.getAll();
		for (SOUser u : q) {
			log.info("USER　" + u.getId());

			SOUser soUser = soUserDAO.getById(u.getId());
			log.info(soUser.getDisplayName());
			log.info(soUser.getLastAccessDate());
			log.info(soUser.getReputation());
			log.info(soUser.getLocation());
			log.info(soUser.getProfileImageUrl());
			log.info(soUser.getCreationDate());
		}
	}
}
