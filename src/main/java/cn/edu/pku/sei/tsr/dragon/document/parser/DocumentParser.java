package cn.edu.pku.sei.tsr.dragon.document.parser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.apache.poi.ss.formula.functions.Code;

import cn.edu.pku.sei.tsr.dragon.document.dao.ContentDAO;
import cn.edu.pku.sei.tsr.dragon.document.dao.ParagraphDAO;
import cn.edu.pku.sei.tsr.dragon.document.dao.SentenceDAO;
import cn.edu.pku.sei.tsr.dragon.document.entity.CodeTermInfo;
import cn.edu.pku.sei.tsr.dragon.document.entity.ContentInfo;
import cn.edu.pku.sei.tsr.dragon.document.entity.ParagraphInfo;
import cn.edu.pku.sei.tsr.dragon.document.entity.SentenceInfo;
import cn.edu.pku.sei.tsr.dragon.experiment.entity.SubjectDataInfo;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.dao.MyCommentDAO;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.dao.MyPostDAO;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.dao.MyThreadDAO;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.CommentInfo;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.PostInfo;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.ThreadInfo;
import cn.edu.pku.sei.tsr.dragon.utils.Config;
import cn.edu.pku.sei.tsr.dragon.utils.Utils;
import cn.edu.pku.sei.tsr.dragon.utils.database.DBConnPool;
import cn.edu.pku.sei.tsr.dragon.utils.nlp.StanfordCoreSplit;

public class DocumentParser {
	public static final Logger		logger						= Logger.getLogger(DocumentParser.class);

	public static final String		PARAGRAPH_CODE_PREFIX		= "<CODE>";
	public static final String		PARAGRAPH_END_MARK			= "\n<EOP>\n";
	public static final String		SENTENCE_CODE_MASK			= "CODEMASK@";
	public static final String[]	CODE_LINE_SUFFIXES			= new String[] { ";", "{", "}" };
	public static final String		CODE_TERM_SEPARATOR			= "<CTS>";
	public static final String		PATH_THREAD_TITLE			= "Thread";
	public static final String		PATH_POST_BODY				= "Post";
	public static final String		PATH_COMMENT_TEXT			= "Comment";
	public static final String		PATH_CONTENT				= "Content";
	public static final String		PATH_PARAGRAPH				= "Paragraph";
	public static final String		PATH_SENTENCE				= "Sentence";
	public static final String		PATH_PHRASE					= "Phrase";
	public static final String		PATH_ID_MARKER				= "@";
	public static final String		PATH_SEPARATOR				= "/";

	public static final String		DOCUMENT_PARSER_LOG_FILE	= "document_parser_data_log.txt";
	public static Connection		conn						= DBConnPool.getConnection();
	public static FileWriter		fw;

	static {
		try {
			fw = new FileWriter(
					new File(Config.getDataLogsDir() + File.separator + DOCUMENT_PARSER_LOG_FILE));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Connection initializeConnection() {
		if (conn == null)
			conn = DBConnPool.getConnection();
		return conn;
	}

	public static void finalizeConnection() {
		if (conn != null)
			DBConnPool.closeConnection(conn);
	}

	public static void main(String[] args) {
		SentenceDAO sentenceDAO = new SentenceDAO(conn);
		List<SentenceInfo> sentences = sentenceDAO.getAll();
		logger.info("get all sentences: " + sentences.size());

		long t0 = System.currentTimeMillis();
		for (int i = 0; i < sentences.size(); i++) {
			String str = sentences.get(i).getText();
			System.out.println(hasTooManyIllegalSymbols(str) + "\t" + str);
		}

		// String str = "The way I layed it out here is not very flexible (but
		// possibly faster for the task to achieve), but you can of course go
		// back to patterns to achieve more flexibility. Then you would replace
		// all <code>foundTerm.text().startsWith(\"https://\") ||
		// foundTerm.text().startsWith(\"http://\")</code> with
		// <code>yourPattern.matches(foundTerm.text())</code>.";
		// "{\"ID\":733\"FileName\":\"content://media/external/images/media/1883\",
		// Image\":[-1,-40,-1,-31,85,41,69,120,105,102,0,0,73,73,42,0,8,0,0,0,12,0,0,1,4,0,1,0,0,0,-64,12,0,0,1,1,4,0,1,0,0,0,-112,9,0,0,15,1,2,0,8,0,0,0,-98,0,0,0,16,1,2,0,9,0,0,0,-90,0,0,0,18,1,3,0,1,0,0,0,1,0,0,0,26,1,5,0,1,0,0,0,-48,0,0,0,27,1,5,0,1,0,0,0,-40,0,0,0,40,1,3,0,1,0,0,0,2,0,0,0,49,1,2,0,11,0,0,0,-80,0,0,0,50,1,2,0,20,0,0,0,-68,0,0,0,19,2,3,0,1,0,0,0,1,0,0,0,105,-121,4,0,1,0,0,0,-32,0,0,0,-38,2,0,0,83,65,77,83,85,78,71,0,71,84,45,78,55,48,48,48,0,0,78,55,48,48,48,88,88,76,82,73,0,0,50,48,49,50,58,49,49,58,48,50,32,50,50,58,50,49,58,51,52,0,72,0,0,0,1,0,0,0,72,0,0,0,1,0,0,0,25,0,-102,-126,5,0,1,0,0,0,18,2,0,0,-99,-126,5,0,1,0,0,0,26,2,0,0,34,-120,3,0,1,0,0,0,3,0,0,0,39,-120,3,0,1,0,0,0,-112,1,0,0,0,-112,7,0,4,0,0,0,48,50,50,48,3,-112,2,0,20,0,0,0,34,2,0,0,4,-112,2,0,20,0,0,0,54,2,0,0,1,-110,10,0,1,0,0,0,74,2,0,0,2,-110,5,0,1,0,0,0,82,2,0,0,3,-110,10,0,1,0,0,0,90,2,0,0,4,-110,10,0,1,0,0,0,98,2,0,0,5,-110,5,0,1,0,0,0,106,2,0,0,7,-110,3,0,1,0,0,0,2,0,0,0,9,-110,3,0,1,0,0,0,16,0,0,0,10,-110,5,0,1,0,0,0,114,2,0,0,124,-110,7,0,66,0,0,0,-104,2,0,0,-122,-110,7,0,21,0,0,0,122,2,0,0,0,-96,7,0,4,0,0,0,48,49,48,48,1,-96,3,0,1,0,0,0,1,0,0,0,2,-96,4,0,1,0,0,0,-64,12,0,0,3,-96,4,0,1,0,0,0,-112,9,0,0,2,-92,3,0,1,0,0,0,0,0,0,0,3,-92,3,0,1,0,0,0,0,0,0,0,6,-92,3,0,1,0,0,0,0,0,0,0,32,-92,2,0,7,0,0,0,-112,2,0,0,0,0,0,0,1,0,0,0,17,0,0,0,9,1,0,0,100,0,0,0,50,48,49,50,58,49,49,58,48,50,32,50,50,58,50,49,58,51,52,0,50,48,49,50,58,49,49,58,48,50,32,50,50,58,50,49,58,51,52,0,-107,1,0,0,100,0,0,0,25,1,0,0,100,0,0,0,-29,-1,-1,-1,100,0,0,0,0,0,0,0,100,0,0,0,25,1,0,0,100,0,0,0,-115,1,0,0,100,0,0,0,65,83,67,73,73,0,0,0,85,115,101,114,32,99,111,109,109,101,110,116,115,0,79,79,69,73,48,56,0,0,5,0,1,0,7,0,4,0,0,0,48,49,48,48,2,0,4,0,1,0,0,0,0,32,1,0,64,0,4,0,1,0,0,0,0,0,0,0,80,0,4,0,1,0,0,0,1,0,0,0,0,1,3,0,1,0,0,0,0,0,0,0,0,0,0,0,9,0,0,1,4,0,1,0,0,0,64,1,0,0,1,1,4,0,1,0,0,0,-16,0,0,0,3,1,3,0,1,0,0,0,6,0,0,0,18,1,3,0,1,0,0,0,1,0,0,0,26,1,5,0,1,0,0,0,76,3,0,0,27,1,5,0,1,0,0,0,84,3,0,0,40,1,3,0,1,0,0,0,2,0,0,0,1,2,4,0,1,0,0,0,92,3,0,0,2,2,4,0,1,0,0,0,-59,81,0,0,0,0,0,0,72,0,0,0,1,0,0,0,72,0,0,0,1,0,0,0,-1,-40,-1,-37,0,-124,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,2,1,1,1,1,1,2,1,1,1,2,2,2,2,2,2,2,2,2,3,3,4,3,3,3,3,3,2,2,3,4,3,3,4,4,4,4,4,2,3,5,5,4,4,5,4,4,4,4,1,1,1,1,1,1,1,2,1,1,2,4,3,2,3,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,-1,-35,0,4,0,4,-1,-64,0,17,8,0,-16,1,64,3,1,33,0,2,17,1,3,17,1,-1,-60,1,-94,0,0,1,5,1,1,1,1,1,1,0,0,0,0,0,0,0,0,1,2,3,4,5,6,7,8,9,10,11,16,0,2,1,3,3,2,4,3,5,5,4,4,0,0,1,125,1,2,3,0,4,17,5,18,33,49,65,6,19,81,97,7,34,113,20,50,-127,-111,-95,8,35,66,-79,-63,21,82,-47,-16,36,51,98,114,-126,9,10,22,23,24,25,26,37,38,39,40,41,42,52,53,54,55,56,57,58,67,68,69,70,71,72,73,74,83,84,85,86,87,88,89,90,99,100,101,102,103,104,105,106,115,116,117,118,119,120,121,122,-125,-124,-123,-122,-121,-120,-119,-118,-110,-109,-108,-107,-106,-105,-104,-103,-102,-94,-93,-92,-91,-90,-89,-88,-87,-86,-78,-77,-76,-75,-74,-73,-72,-71,-70,-62,-61,-60,-59,-58,-57,-56,-55,-54,-46,-45,-44,-43,-42,-41,-40,-39,-38,-31,-30,-29,-28,-27,-26,-25,-24,-23,-22,-15,-14,-13,-12,-11,-10,-9,-8,-7,-6,1,0,3,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,1,2,3,4,5,6,7,8,9,10,11,17,0,2,1,2,4,4,3,4,7,5,4,4,0,1,2,119,0,1,2,3,17,4,5,33,49,6,18,65,81,7,97,113,19,34,50,-127,8,20,66,-111,-95,-79,-63,9,35,51,82,-16,21,98,114,-47,10,22,36,52,-31,37,-15,23,24,25,26,38,39,40,41,42,53,54,55,56,57,58,67,68,69,70,71,72,73,74,83,84,85,86,87,88,89,90,99,100,101,102,103,104,105,106,115,116,117,118,119,120,121,122,-126,-125,-124,-123,-122,-121,-120,-119,-118,-110,-109,-108,-107,-106,-105,-104,-103,-102,-94,-93,-92,-91,-90,-89,-88,-87,-86,-78,-77,-76,-75,-74,-73,-72,-71,-70,-62,-61,-60,-59,-58,-57,-56,-55,-54,-46,-45,-44,-43,-42,-41,-40,-39,-38,-30,-29,-28,-27,-26,-25,-24,-23,-22,-14,-13,-12,-11,-10,-9,-8,-7,-6,-1,-38,0,12,3,1,0,2,17,3,17,0,63,0,-2,52,-31,-116,-20,43,-71,55,49,-3,-29,48,-36,-92,-29,104,27,79,-45,39,-45,20,-41,64,-82,-93,-53,-33,-14,-100,-4,-60,117,-32,115,-97,113,-11,-51,122,49,-115,-102,63,54,-65,118,85,-72,-28,103,57,25,28,-87,27,79,0,3,-40,-10,21,-103,52,100,-74,73,-14,-127,3,-27,45,-14,-12,7,-116,103,-97,-104,-110,56,-95,-108,-101,70,116,-95,118,62,-20,51,-87,63,47,-35,10,15,31,123,-13,-56,62,-75,66,70,-113,-53,-27,84,24,-44,71,-79,-122,6,50,71,94,-25,-81,94,-62,-111,-68,27,-35,25,-109,-20,-37,-100,19,-114,3,125,-26,81,-57,56,-19,-57,-46,-79,102,-40,-92,-78,-95,42,-57,36,18,50,112,7,62,-93,-102,14,-54,109,-97,-1,-48,-2,20,-90,28,-100,-100,-11,43,-37,104,-55,-58,7,-25,-7,-42,92,-116,-65,49,-64,-50,-1,0,-108,99,56,-29,-41,-23,93,-82,39,-51,83,108,-93,33,4,-74,-35,-89,3,-111,-76,103,31,79,78,69,83,112,-96,-28,40,13,-19,-58,43,54,-84,122,20,-39,93,-108,-18,32,1,-49,1,-70,-3,5,35,32,85,32,-32,18,-35,-77,-98,48,115,72,-21,-125,118,34,97,-23,-111,-49,80,61,42,55,-12,-54,-127,-4,62,-4,-48,111,7,-91,-103,-1,-47,-2,3,-56,7,29,120,59,115,-127,-109,-1,0,-22,-90,16,0,-64,-17,-49,114,120,-85,-110,1,-99,67,115,-114,-4,-98,-67,105,56,29,65,-57,-89,127,-83,64,9,-97,126,61,-5,-29,52,30,0,-56,-21,-58,123,30,-76,0,-36,109,61,-72,-4,-15,-114,-76,3,-23,-49,108,119,-6,-48,7,-1,-46,-2,1,15,78,9,56,60,113,-97,106,65,-58,61,50,7,94,62,-76,0,-103,63,-105,92,-102,50,15,36,-32,-48,2,14,-97,-54,-113,76,-25,29,64,-6,80,1,-33,3,-90,122,-11,-59,7,-41,-81,-1,0,-86,-128,63,-1,-45,-2,0,7,-42,-127,-41,3,-12,29,104,1,123,116,39,-7,81,-19,-111,-6,-30,-128,0,79,67,-8,3,-48,82,-25,25,30,-98,-1,0,-46,-128,12,-9,-57,65,-40,-46,-106,6,-128,63,-1,-44,-2,55,-4,-127,-9,-94,5,48,0,32,-1,0,-85,61,80,-12,35,-41,-11,-12,52,-109,70,119,-30,79,-66,-85,-76,76,-96,46,10,-98,-128,-114,-93,-96,-81,69,43,-69,31,-102,-93,42,72,-50,-14,10,-56,120,-7,-101,28,-25,-116,-13,-41,-23,-20,107,42,-30,53,27,85,-104,16,-91,-92,126,-69,-119,36,1,-110,79,92,-125,-6,126,40,-94,-125,-18,-36,-5,124,-80,-52,56,24,-55,92,-106,56,-21,-20,-65,90,-89,52,65,-14,84,41,102,-32,14,90,69,32,18,78,126,-89,-11,34,-125,120,-67,19,50,-35,30,72,-54,-18,36,41,-53,-88,56,45,-49,4,-81,-75,99,-49,6,-34,9,42,-39,-61,18,48,84,-98,-29,-7,80,118,83,122,-40,-1,-43,-2,21,-89,69,-40,113,-107,24,-17,-111,-116,-100,-125,-113,-16,-84,-87,-111,-58,87,-88,35,39,104,-58,113,-114,-125,21,-24,31,49,73,-108,37,24,-28,12,28,12,-100,100,12,14,-4,125,61,106,-84,-118,62,115,-14,-19,-50,-43,-63,-28,112,127,-89,-75,76,-94,-103,-35,77,-40,-124,-116,114,0,-55,29,-108,-12,-19,-11,-88,-118,16,-93,118,14,91,60,116,-29,-37,-14,-84,78,-56,59,59,17,-78,96,29,-69,-127,-57,67,-64,24,39,-4,105,-123,24,40,-32,3,-126,67,3,-13,118,-49,31,-91,6,-47,118,103,-1,-42,-2,4,8,63,-62,-71,-12,39,-81,126,122,127,-100,83,10,-112,51,-4,33,-79,-128,49,-113,127,-27,91,73,93,9,52,-10,26,84,-114,79,-54,51,-98,-108,-46,-69,121,-28,124,-71,-32,103,-75,98,48,-37,-128,71,-65,30,-99,-1,0,-62,-103,-73,57,-56,28,112,50,49,-102,0,8,29,-56,-11,-24,105,57,25,56,-4,113,-126,61,-65,74,0,-1,-41,-2,1,48,71,124,-32,118,20,-104,-50,120,-4,-69,-48,2,96,-114,56,-32,-32,117,-17,70,-34,79,110,125,48,63,-49,74,0,76,30,-57,-109,-23,-34,-128,56,-49,-89,-75,0,3,60,119,-12,20,-99,120,-28,-25,-16,-26,-128,63,-1,-48,-2,0,58,113,-8,122,82,-127,-22,56,-23,-23,64,7,35,-109,-41,56,-28,-30,-126,63,-97,-7,-1,0,62,-44,0,99,0,-100,31,67,-38,-108,-126,50,79,126,5,0,55,24,7,57,-56,56,-10,-91,-63,-49,63,-88,-29,-67,0,127,-1,-47,-2,57,-103,28,-82,15,-54,-48,-27,88,21,-37,-68,-6,-127,-8,47,60,125,42,-92,-92,108,42,9,102,-36,65,-28,49,64,9,-34,57,-4,-77,-37,3,-91,122,81,87,103,-26,-67,-67,76,-39,22,71,87,118,27,10,-97,43,-106,-50,79,76,1,-45,28,85,9,35,60,51,-96,-37,-115,-92,99,107,-98,-7,7,30,-96,-2,84,53,-94,101,25,-14,-96,33,-120,64,14,-30,76,-97,127,-27,28,0,71,-88,-57,79,122,-91,42,56,-7,73,80,119,0,-118,-83,-9,-120,-36,72,-29,-13,-29,-46,-86,17,-45,83,88,109,99,50,85,-36,-65,59,-126,-86,70,1,44,28,-13,-114,72,-6,123,-16,13,102,93,35,-124,104,-101,110,67,-25,-3,95,81,-126,50,51,-50,57,63,-25,-102,-52,-21,-90,-10,63,-1,-46,-2,23,110,55,12,2,24,29,-65,32,-58,-26,35,-95,-6,-29,-97,-54,-77,36,66,78,14,6,1,92,17,-125,-7,-12,24,-11,-6,-41,-96,124,-83,55,100,101,-55,30,73,61,-58,89,70,62,102,-64,-25,-100,125,42,-69,-122,-61,97,113,-50,-46,72,-58,126,-108,29,-48,122,-40,-82,87,-100,-14,57,28,5,-17,-19,76,101,28,1,-126,50,51,-111,-55,-11,-4,-70,-42,83,-115,-99,-50,-88,61,6,108,32,-111,-64,-49,-54,59,-28,113,81,58,-114,0,-58,73,-49,35,-88,-88,58,35,43,-93,-1,-45,-2,5,-118,116,-50,24,112,64,7,0,98,-94,40,114,112,-89,111,83,-107,3,56,-49,-1,0,90,-70,12,96,-20,-11,27,-77,25,110,62,81,-111,-45,20,-52,28,16,127,-117,-114,-71,35,-113,-13,-46,-77,-108,109,-86,54,16,-116,14,0,-56,-23,-51,38,-45,-113,-57,60,96,122,-42,96,52,-114,-104,3,111,78,71,-13,-90,-29,-73,-65,79,-81,90,0,-1,-44,-2,1,72,63,54,57,-49,79,80,7,52,-72,57,37,-121,-12,-1,0,61,-24,2,60,2,57,39,-81,-13,2,-105,105,-25,32,113,-57,3,-83,0,31,-106,123,30,-97,-25,-1,0,-81,73,-116,31,95,117,-29,52,0,115,-37,-41,-114,115,-118,76,117,-10,-12,28,80,7,-1,-43,-2,0,112,120,61,123,-46,1,-109,-57,30,-108,0,-71,-3,15,126,-76,30,-103,28,118,-23,-42,-128,15,-90,122,-14,64,-91,-58,57,96,64,-50,61,13,0,33,30,-99,-65,-50,104,57,-58,51,-45,-37,-23,64,31,-1,-42,-2,58,71,-104,50,-45,72,12,73,-113,-71,-2,-75,8,-49,32,19,-112,57,111,92,-28,-43,107,-103,3,-77,-85,2,-126,64,3,50,-96,7,-8,113,-114,61,127,-91,122,-80,90,31,-102,62,-98,-90,108,-88,124,-77,20,37,-118,-85,-28,-55,-128,114,-67,127,50,56,-50,56,-17,84,36,-28,-79,37,-97,115,96,-95,95,-97,-114,-8,-4,-7,-9,-51,93,-117,51,-40,-19,102,96,-40,32,-4,-2,88,44,8,61,1,-29,56,-32,-97,108,-42,100,-69,67,33,-61,-4,-93,-94,-73,76,-29,-98,-7,62,-36,-6,80,105,79,-79,71,110,14,-32,119,97,-74,-106,-37,-76,116,7,-27,-49,-4,11,-89,-21,89,87,65,-50,-14,114,78,11,22,120,-2,101,24,-32,99,62,-66,-98,-76,89,61,-50,-102,111,67,-1,-41,-2,26,39,15,-79,78,-29,-68,55,-54,25,-127,32,31,110,-65,65,89,114,100,-31,20,-27,-113,82,-54,121,-57,92,-28,-1,0,-6,-77,94,-93,-121,99...";

		// String[] sent = splitSentences(str);
		// System.err.println(Arrays.toString(sent));
		// Pair<String, CodeTermInfo[]> pair = maskCodeTerms(str);
		// System.out.println(pair.getLeft());
		// for (CodeTermInfo codeTermInfo : pair.getRight()) {
		// System.out.println();
		// System.out.println(codeTermInfo.getOriginal());
		// System.out.println(codeTermInfo.getMask());
		// System.out.println(codeTermInfo.getIndex());
		// }

	}

	public static ContentInfo parseContent(ContentInfo content) throws IOException {
		if (content == null)
			return null;

		switch (content.getSourceType()) {
		case ContentInfo.SOURCE_TYPE_THREAD_TITLE:
			MyThreadDAO myThreadDAO = new MyThreadDAO(conn);
			ThreadInfo t = myThreadDAO.getById(content.getSourceId());
			fw.write("===============" + t.getId() + "==============\n" + t.getTitle() + "\n\n");
			break;
		case ContentInfo.SOURCE_TYPE_POST_BODY:
			MyPostDAO myPostDAO = new MyPostDAO(conn);
			PostInfo p = myPostDAO.getById(content.getSourceId());
			fw.write("===============" + p.getId() + "==============\n" + p.getBody() + "\n\n");
			break;
		case ContentInfo.SOURCE_TYPE_COMMENT_TEXT:
			MyCommentDAO myCommentDAO = new MyCommentDAO(conn);
			CommentInfo c = myCommentDAO.getById(content.getSourceId());
			fw.write("===============" + c.getId() + "==============\n" + c.getText() + "\n\n");

		default:
			break;
		}

		try {
			ContentDAO contentDAO = new ContentDAO(conn);
			content = contentDAO.addContent(content); // get id
			fw.write("[content] " + content.getText() + "\n");

			if (content == null)
				return null;

			String[] paraTexts = content.getText().split(DocumentParser.PARAGRAPH_END_MARK);
			List<Integer> paraIds = new ArrayList<>();
			int paraIndex = 0;
			for (int i = 0; i < paraTexts.length; i++) {

				if (StringUtils.isBlank(paraTexts[i]))
					continue;

				ParagraphInfo paragraphInfo = new ParagraphInfo();

				if (paraTexts[i].startsWith(DocumentParser.PARAGRAPH_CODE_PREFIX)) {
					paragraphInfo.setParagraphType(ParagraphInfo.PARAGRAPH_TYPE_CODE);
					paragraphInfo
							.setText(paraTexts[i].substring(DocumentParser.PARAGRAPH_CODE_PREFIX.length()));
				}
				else {
					paragraphInfo.setParagraphType(ParagraphInfo.PARAGRAPH_TYPE_TEXT);
					paragraphInfo.setText(paraTexts[i]);
				}

				paragraphInfo.setParentId(content.getId());
				paragraphInfo.setIndexAsChild(paraIndex);

				ParagraphDAO paragraphDAO = new ParagraphDAO(conn);
				paragraphInfo = paragraphDAO.addParagraph(paragraphInfo);

				paraIds.add(paragraphInfo.getId());
				paraIndex++;

				if (paragraphInfo.getParagraphType() != ParagraphInfo.PARAGRAPH_TYPE_TEXT)
					continue;

				String str = paragraphInfo.getText();
				// System.err.println("[paragraph]" + i + "\t" + str);

				String[] sentList = splitSentences(str);

				List<Integer> sentIds = new ArrayList<>();
				int sentIndex = 0;
				for (int j = 0; j < sentList.length; j++) {
					String sentenceText = sentList[j];
					if (StringUtils.isBlank(sentenceText))
						continue;
					fw.write("[sentence]\t" + sentenceText + "\n");

					SentenceInfo sentenceInfo = new SentenceInfo();

					// 处理嵌入的code
					sentenceInfo.setText(sentenceText);

					sentenceInfo.setParentId(paragraphInfo.getId());
					sentenceInfo.setIndexAsChild(sentIndex);

					SentenceDAO sentenceDAO = new SentenceDAO(conn);
					sentenceInfo = sentenceDAO.addSentence(sentenceInfo);

					sentIds.add(sentenceInfo.getId());
					sentIndex++;
				}
				paragraphInfo.setSentencesId(Utils.convertIntegerListToIntArray(sentIds));
			}
			content.setParagraphsId(Utils.convertIntegerListToIntArray(paraIds));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		fw.flush();

		return content;
	}

	public static String[] splitSentences(String text) {
		if (StringUtils.isBlank(text))
			return new String[0];

		Pair<String, CodeTermInfo[]> maskedTextAndCodeTerms = maskCodeTerms(text);

		String maskedText = maskedTextAndCodeTerms.getLeft();
		CodeTermInfo[] codeTerms = maskedTextAndCodeTerms.getRight();

		List<String> rawSentences = StanfordCoreSplit.splitSentences(maskedText);
		String[] sentences = new String[rawSentences.size()];
		int currentMaskIndex = 0;
		String currMask = SENTENCE_CODE_MASK + currentMaskIndex;
		for (int i = 0; i < rawSentences.size(); i++) {
			String rawSent = rawSentences.get(i);
			System.out.println(rawSent);
			while (rawSent.contains(currMask)) {
				rawSent = rawSent.replace(currMask, codeTerms[currentMaskIndex].getOriginal());
				currentMaskIndex++;
				currMask = SENTENCE_CODE_MASK + currentMaskIndex;
			}
			sentences[i] = rawSent;
			System.out.println(rawSent);
		}

		return sentences;
	}

	public static boolean checkCodeLine(String line) {
		if (line == null)
			return false;

		line = line.trim();
		for (String suffix : CODE_LINE_SUFFIXES) {
			if (line.endsWith(suffix))
				return true;
		}

		int tot = 0; // 特殊（code-like）符号计数
		for (int i = 0; i < line.length(); i++) {
			if (line.charAt(i) == '.')
				tot++;
			if (line.charAt(i) == '}')
				tot++;
			if (line.charAt(i) == '{')
				tot++;
			if (line.charAt(i) == '(')
				tot++;
			if (line.charAt(i) == ')')
				tot++;
			if (line.charAt(i) == ';')
				tot++;
		}
		if (tot * 12 > line.length())
			return false; // 如果特殊符号占句中字符的比例超过1/12，则视作无效

		return true;
	}

	public static boolean hasTooManyIllegalSymbols(String text) {
		int alphaCount = 0; // 特殊（code-like）符号计数
		int numberCount = 0;
		for (int i = 0; i < text.length(); i++) {
			if (Character.isAlphabetic(text.charAt(i)))
				alphaCount++;
			else if (Character.isDigit(text.charAt(i)))
				numberCount++; // 不是字母或数字
		}
		// int l = text.length();
		// System.out.println(
		// alphaCount + "\t" + numberCount + "\t" + text.length() + "\t" +
		// (float) l / (l - alphaCount)
		// + "\t" + (float) l / (l - alphaCount - numberCount) + "\t" + text);

		return 1.5 * alphaCount <= text.length(); // 非字母比例 >= 1/3 才返回真
	}

	/**
	 * Replace code like terms in the sentence with masks, return the terms.
	 * 
	 * @param sentence
	 * @return a pair of which the left is the masked sentence and the right is
	 *         an array of the code like terms found in the sentence
	 */
	public static Pair<String, CodeTermInfo[]> maskCodeTerms(String sentence) {
		if (StringUtils.isBlank(sentence))
			return Pair.of(sentence, new CodeTermInfo[0]);

		List<CodeTermInfo> codeTerms = new ArrayList<>();

		/** find code terms by html tags. **/
		String codeTagBegin = "<code>";
		String codeTagEnd = "</code>";

		for (int codeMaskNum = 0; codeMaskNum < sentence.length(); codeMaskNum++) {
			int beginIndex = sentence.indexOf(codeTagBegin);
			if (beginIndex < 0)
				break;
			int endIndex = sentence.indexOf(codeTagEnd);

			String codeOriginal = sentence.substring(beginIndex, endIndex + codeTagEnd.length());
			String currMask = SENTENCE_CODE_MASK + codeMaskNum;

			// Must not use replaceFirst here. Will lead to unstoppable error
			// <code>foundTerm.text().startsWith("https://") ||
			// foundTerm.text().startsWith("http://")</code>
			// The code segment might contain odd chars (escape chars) to
			// confuse the regex match
			// text = text.replaceFirst(codeOriginal, currMask);
			sentence = StringUtils.replaceOnce(sentence, codeOriginal, currMask);

			CodeTermInfo codeTerm = new CodeTermInfo();
			codeTerm.setOriginal(codeOriginal);
			codeTerm.setMask(currMask);
			codeTerm.setIndex(codeMaskNum);

			codeTerms.add(codeTerm);

			sentence = StringUtils.replaceOnce(sentence, codeOriginal, currMask);
		}

		return Pair.of(sentence, codeTerms.toArray(new CodeTermInfo[codeTerms.size()]));
	}

	public static String concatenateCodeTerms(CodeTermInfo[] codeTerms) {
		if (codeTerms == null)
			return "";
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < codeTerms.length; i++) {
			if (i > 0)
				str.append(CODE_TERM_SEPARATOR);
			str.append(codeTerms[i].getOriginal());

		}
		return str.toString();
	}

	public static String getSentenceSourcePath(int sentenceId) {
		StringBuilder pathBuilder = new StringBuilder();

		// initializeConnection();
		Connection conn = DBConnPool.getConnection();
		try {
			SentenceDAO sentenceDAO = new SentenceDAO(conn);
			ParagraphDAO paragraphDAO = new ParagraphDAO(conn);
			ContentDAO contentDAO = new ContentDAO(conn);

			int paragraphId = sentenceDAO.getParentIdById(sentenceId);
			if (paragraphId <= 0)
				return null;
			int contentId = paragraphDAO.getParentIdById(paragraphId);

			if (contentId <= 0)
				return null;
			Pair<Integer, Integer> sourcePair = contentDAO.getSourceTypeAndIdById(contentId);
			if (sourcePair == null)
				return null;
			int sourceType = sourcePair.getLeft();
			int sourceId = sourcePair.getRight();
			if (sourceType <= 0 || sourceId <= 0)
				return null;

			/**
			 * all data have been correctly retrieved and then build the
			 * sourcePath string
			 **/
			switch (sourceType) {
			case ContentInfo.SOURCE_TYPE_THREAD_TITLE:
				pathBuilder.append(PATH_THREAD_TITLE);
				break;
			case ContentInfo.SOURCE_TYPE_POST_BODY:
				pathBuilder.append(PATH_POST_BODY);
				break;
			case ContentInfo.SOURCE_TYPE_COMMENT_TEXT:
				pathBuilder.append(PATH_COMMENT_TEXT);
				break;
			default:
				return null;
			}
			pathBuilder.append(PATH_ID_MARKER).append(sourceId);
			pathBuilder.append(PATH_SEPARATOR).append(PATH_CONTENT).append(PATH_ID_MARKER).append(contentId);
			pathBuilder.append(PATH_SEPARATOR).append(PATH_PARAGRAPH).append(PATH_ID_MARKER)
					.append(paragraphId);
			pathBuilder.append(PATH_SEPARATOR).append(PATH_SENTENCE).append(PATH_ID_MARKER)
					.append(sentenceId);

			return pathBuilder.toString();
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		finally {
			DBConnPool.closeConnection(conn);
		}
	}


}
