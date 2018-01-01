package cn.edu.pku.sei.tsr.dragon.stackoverflow.datadump;

import java.sql.Connection;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.log4j.Logger;

import cn.edu.pku.sei.tsr.dragon.stackoverflow.datadump.xmlhandler.BadgesHandler;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.datadump.xmlhandler.CommentsHandler;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.datadump.xmlhandler.PostHandler;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.datadump.xmlhandler.TableHandler;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.datadump.xmlhandler.UserHandler;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.datadump.xmlhandler.VoteHandler;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.so.SOBadge;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.so.SOComment;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.so.SOPost;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.so.SOPostHistory;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.so.SOPostLink;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.so.SOTag;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.so.SOUser;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.so.SOVote;
import cn.edu.pku.sei.tsr.dragon.utils.Config;
import cn.edu.pku.sei.tsr.dragon.utils.database.DBConnPool;;

public class XmlToDbLoader {
	public static final Logger logger = Logger.getLogger(XmlToDbLoader.class);

	public static void main(String[] args) throws Exception {

		boolean filterByTags = true;

		if (filterByTags)
			loadToDBFilteringTags();
		else
			loadXmlToDBFull();

		String message = "All the xml SO dump data have been loaded into database: "
				+ cn.edu.pku.sei.tsr.dragon.utils.Config.getLocalDBUrl();
		// MailSender.sendMail(Config.getNotificationMail(), "SO data loaded
		// into database", message);
		logger.info(message);

	}

	private static void loadToDBFilteringTags() throws Exception {
		// Subject tags are not set explicitly, they are stored in
		// config.properties,
		// fetched and checked in SubjectDataHandler.java
		String inputFolderPath = Config.getStackOverflowDataDumpDir();
		String tmpFolderPath = Config.getStackOverflowDataTempDir();

		if (inputFolderPath == null || inputFolderPath.isEmpty()) {
			logger.error("Please config input-path!");
			return;
		}
		else if (tmpFolderPath == null || tmpFolderPath.isEmpty()) {
			logger.error("Please config output-path!");
			return;
		}

		TableHandler.setOutputFolderPath(tmpFolderPath);

		logger.info("Posts are being processed...");
		PostHandler postHandler = new PostHandler(inputFolderPath + "/Posts.xml");
		postHandler.handle();
		postHandler.writeToTableFile();
		logger.info("Posts finished!");

		logger.info("Comments are being processed...");
		CommentsHandler commentsHandler = new CommentsHandler(inputFolderPath + "/Comments.xml");
		commentsHandler.handle();
		commentsHandler.writeToTableFile();
		logger.info("Comments finished!");

		logger.info("Votes are being processed...");
		VoteHandler voteHandler = new VoteHandler(inputFolderPath + "/Votes.xml");
		voteHandler.handle();
		voteHandler.writeToTableFile();
		logger.info("Votes finished!");

		logger.info("Users are being processed...");
		UserHandler userHandler = new UserHandler(inputFolderPath + "/Users.xml");
		userHandler.handle();
		userHandler.writeToTableFile();
		logger.info("Users finished!");

		logger.info("Badges are being processed...");
		BadgesHandler badgesHandler = new BadgesHandler(inputFolderPath + "/Badges.xml");
		badgesHandler.handle();
		badgesHandler.writeToTableFile();
		logger.info("Badges finished!");

		logger.info("Finish handling data!");

		// batch load records from local files to local tables
		Connection conn = DBConnPool.getConnection();
		QueryRunner queryRunner = new QueryRunner();

		String loadHandledSql = "load xml local infile '" + tmpFolderPath;
		String loadOriginalSql = "load xml local infile '" + inputFolderPath;

		try {
			logger.info("loading tags...");
			queryRunner.update(conn, loadOriginalSql + "/tags.xml' into table " + SOTag.TABLE_NAME);
			logger.info("loading users...");
			queryRunner.update(conn,
					loadHandledSql + "/users.xml' into table " + SOUser.TABLE_NAME);
			logger.info("loading badges...");
			queryRunner.update(conn,
					loadHandledSql + "/badges.xml' into table " + SOBadge.TABLE_NAME);
			logger.info("loading posts...");
			queryRunner.update(conn,
					loadHandledSql + "/posts.xml' into table " + SOPost.TABLE_NAME);
			logger.info("loading votes...");
			queryRunner.update(conn,
					loadHandledSql + "/votes.xml' into table " + SOVote.TABLE_NAME);
			logger.info("loading comments...");
			queryRunner.update(conn,
					loadHandledSql + "/comments.xml' into table " + SOComment.TABLE_NAME);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			DBConnPool.closeConnection(conn);
		}
	}
	

	private static void loadXmlToDBFull() throws Exception {
		String inputFolderPath = Config.getStackOverflowDataDumpDir();

		if (inputFolderPath == null || inputFolderPath.isEmpty()) {
			logger.error("Please config input-path!");
			return;
		}

		// batch load records from local files to local tables
		Connection conn = DBConnPool.getConnection();
		QueryRunner queryRunner = new QueryRunner();

		String loadSql = "load xml local infile '" + inputFolderPath;

		try {
			logger.info("loading tags...");
			queryRunner.update(conn, loadSql + "/tags.xml' into table " + SOTag.TABLE_NAME);
			logger.info("loading postlinks...");
			queryRunner.update(conn,
					loadSql + "/postlinks.xml' into table " + SOPostLink.TABLE_NAME);
			logger.info("loading users...");
			queryRunner.update(conn, loadSql + "/users.xml' into table " + SOUser.TABLE_NAME);
			logger.info("loading badges...");
			queryRunner.update(conn, loadSql + "/badges.xml' into table " + SOBadge.TABLE_NAME);
			logger.info("loading votes...");
			queryRunner.update(conn, loadSql + "/votes.xml' into table " + SOVote.TABLE_NAME);
			logger.info("loading comments...");
			queryRunner.update(conn, loadSql + "/comments.xml' into table " + SOComment.TABLE_NAME);
			logger.info("loading posts...");
			queryRunner.update(conn, loadSql + "/posts.xml' into table " + SOPost.TABLE_NAME);
			logger.info("loading post history...");
			queryRunner.update(conn,
					loadSql + "/postHistory.xml' into table " + SOPostHistory.TABLE_NAME);

		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			DBConnPool.closeConnection(conn);
		}

	}

}
