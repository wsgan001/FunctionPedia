package cn.edu.pku.sei.tsr.dragon.document.parser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;

import cn.edu.pku.sei.tsr.dragon.stackoverflow.dao.MyCommentDAO;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.CommentInfo;
import cn.edu.pku.sei.tsr.dragon.utils.database.DBConnPool;

public class PlainTextParser {
	public static final Logger logger = Logger.getLogger(PlainTextParser.class);

	public static String parsePlainTextToParagraphedText(String text) {
		text = StringEscapeUtils.unescapeHtml4(text);
		text = text.replace("\r", DocumentParser.PARAGRAPH_END_MARK);
		text = text.replace("\n", DocumentParser.PARAGRAPH_END_MARK);
		return text;
	}

	public static void main(String[] args) throws IOException {
		// String text = "sdhjgfgw&#x3A;efgwi&#xA;dgyuwe&#xFd82";
		// System.out.println(text);
		// System.out.println(parsePlainTextToParagraphedText(text));
		// System.out.println(text);
		FileWriter fw = new FileWriter(new File("test.txt"));
		MyCommentDAO dao = new MyCommentDAO(DBConnPool.getConnection());
		List<CommentInfo> comments = dao.getAll();
		for (int i = 0; i < comments.size(); i++) {

			if (i % 30 != 8)
				continue;
			CommentInfo commentInfo = comments.get(i);

			fw.write("================" + commentInfo.getId() + "================\n");
			String str = commentInfo.getText();
			fw.write(str + "\n");
			fw.write("\n");
			String s = parsePlainTextToParagraphedText(str);
			fw.write(s + "\n");
			fw.write("\n");

			logger.info(commentInfo.getId());
		}
	}
}
