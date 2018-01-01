package cn.edu.pku.sei.tsr.dragon.email.mbox.parser;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.james.mime4j.dom.Entity;
import org.apache.james.mime4j.dom.Header;
import org.apache.james.mime4j.dom.Message;
import org.apache.james.mime4j.dom.MessageBuilder;
import org.apache.james.mime4j.dom.Multipart;
import org.apache.james.mime4j.dom.TextBody;
import org.apache.james.mime4j.dom.address.AddressList;
import org.apache.james.mime4j.dom.address.MailboxList;
import org.apache.james.mime4j.message.BodyPart;
import org.apache.james.mime4j.message.DefaultMessageBuilder;
import org.apache.james.mime4j.stream.Field;
import org.apache.log4j.Logger;

import cn.edu.pku.sei.tsr.dragon.codeparser.code.CodeParser;
import cn.edu.pku.sei.tsr.dragon.codeparser.code.entity.JavaProjectInfo;
import cn.edu.pku.sei.tsr.dragon.content.CodeTermsParser;
import cn.edu.pku.sei.tsr.dragon.content.ContentParser;
import cn.edu.pku.sei.tsr.dragon.content.entity.ContentInfo;
import cn.edu.pku.sei.tsr.dragon.content.entity.ParagraphInfo;
import cn.edu.pku.sei.tsr.dragon.content.entity.SentenceInfo;
import cn.edu.pku.sei.tsr.dragon.email.entity.Email;
import cn.edu.pku.sei.tsr.dragon.email.utils.Checker;
import cn.edu.pku.sei.tsr.dragon.email.utils.MBoxSpliter;
import cn.edu.pku.sei.tsr.dragon.email.utils.StringUtils;

public class MessageParser {

	private static MessageBuilder	builder	= new DefaultMessageBuilder();
	private static Message			message;
	private static StringBuffer		txtBody;
	private static StringBuffer		htmlBody;

	protected static final Logger	logger	= Logger.getLogger(MessageParser.class.getName());
	public static JavaProjectInfo project;
	static{
		project = CodeParser.parse("lucene", "D:\\Dragon Project\\subjects\\lucene-5.2.1");
	}
	public static Email buildMessage(String msg) {
		if (msg == null) {
			logger.info("the msg string is null");
			return null;
		}
		InputStream is = new ByteArrayInputStream(msg.getBytes());
		Email email = new Email();
		try {

			message = builder.parseMessage(is);
			txtBody = new StringBuffer();
			htmlBody = new StringBuffer();

			// if
			// (message.getContentTransferEncoding().toLowerCase().contains("7bit"))
			// {
			// logger.info("the msg charset is 7bit");
			// // return null;
			// }
			email.setSubject(StringUtils.trimString(message.getSubject()));
			// System.out.println("Subject :"+email.getSubject());

			email.setSendDate(message.getDate());
			// System.out.println("Date :" + email.getDate());

			email.setMessageID(StringUtils.trimString(message.getMessageId()));
			// System.out.println("MessageID :" + email.getMessageID());

			MailboxList from = message.getFrom();
			if (from != null && from.size() > 0) {
				email.setFromName(StringUtils.trimString(from.get(0).getName()));
				email.setFromEmail(StringUtils.trimString(from.get(0).getAddress()));
				// System.out.println(" From :" + email.getFromName()
				// +" address :" + email.getFromEmail());
			}

			AddressList to = message.getTo();
			if (to != null && to.size() > 0) {
				MailboxList ml = to.flatten();
				if (ml != null && ml.size() > 0) {
					String nameList = ml.get(0).getName();
					String emailList = ml.get(0).getAddress();
					for (int i = 1; i < ml.size(); i++) {
						nameList += "," + ml.get(i).getName();
						emailList += "," + ml.get(i).getAddress();
					}
					email.setToName(StringUtils.trimString(nameList));
					email.setToEmail(StringUtils.trimString(emailList));
					// System.out.println(" to : " + email.getToName() +
					// " address : " + email.getToEmail());
				}
			}

			Header header = message.getHeader();
			for (Field field : header.getFields()) {
				String f = field.getName();
				if (f.toLowerCase().contains("in-reply-to")) {
					String inReplyTo = field.getBody();
					email.setInReplyTo(StringUtils.trimString(inReplyTo));
					// System.out.println("in-reply-to : " +
					// email.getInReplyTo());
				}
			}

			if (message.isMultipart()) {
				Multipart multipart = (Multipart) message.getBody();
				parseBodyParts(multipart);
			}
			else {
				// If it's single part message, just get text body
				if (message.getBody() instanceof TextBody) {
					String text = getTextPart(message);
					txtBody.append(text);
				}
			}
			email.setContent(txtBody.toString());
			if (email.getContent().contains("I have generated Excel")){
				System.out.println();
			}
			email.setContentInfo(new ContentInfo(txtBody.toString()));
			ContentParser.parseContent(email.getContentInfo());
			for (ParagraphInfo paragraph : email.getContentInfo().getParagraphList()){
				if (paragraph.isCodeFragment()) continue;
				for (SentenceInfo sentence : paragraph.getSentences()){
					ContentParser.replaceCodeLikeTerms(sentence);
				}
			}

			CodeTermsParser parser = new CodeTermsParser(project);
			parser.parseRelativeCodeTerms(email.getContentInfo());
			//SyntaxParser.extractPhrases(email.getContentInfo());

		}
		catch (Exception e) {
			e.printStackTrace();
			logger.info(e.getMessage());
		}

		return email;
	}

	// private static TextBody getTextBody(Body body) {
	// if(body instanceof TextBody) {
	// TextBody b = (TextBody) body;
	// return b;
	// }
	// if(body instanceof MessageImpl) {
	// body = ((MessageImpl) body).getBody();
	// return getTextBody(body);
	// }
	// if(body instanceof Multipart) {
	// for(Entity part : ((Multipart)body).getBodyParts()) {
	// if(part instanceof BodyPart) {
	// if(getTextBody(part.getBody()) != null)
	// return getTextBody(part.getBody());
	// }
	// }
	// }
	// return null;
	// }
	//

	private static void parseBodyParts(Multipart multipart) {
		for (Entity part : multipart.getBodyParts()) {
			if (((BodyPart) part).isMimeType("text/plain")) {
				String txt = getTextPart(part);
				txtBody.append(txt);
			}
			else if (((BodyPart) part).isMimeType("text/html")) {
				String html = getTextPart(part);
				htmlBody.append(html);
			}
			else if (part.getDispositionType() != null && !part.getDispositionType().equals("")) {
				// If DispositionType is null or empty, it means that it's
				// multipart, not attached file
				// attachments.add(part);
			}

			// If current part contains other, parse it again by recursion
			if (part.isMultipart()) {
				parseBodyParts((Multipart) part.getBody());
			}
		}
	}

	private static String getTextPart(Entity part) {
		TextBody tb = (TextBody) part.getBody();
		String charset = tb.getMimeCharset().toLowerCase();
		if (charset.contains("unknown") || charset.equals("utf-7")
				|| charset.equals("unicode-1-1-utf-7") || charset.equals("iso 8859-15")) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		try {
			Reader r = tb.getReader();

			int c;
			while ((c = r.read()) != -1) {
				sb.append((char) c);
			}
		}
		catch (IOException ex) {
			System.out.println(tb.getMimeCharset());
			ex.printStackTrace();
		}
		return sb.toString();
	}

	public static void main(String args[]) throws ClassNotFoundException, SQLException {
		// String message = ReadFile.read_file("D:\\lab\\�ʼ��б����\\msg3.txt");
		//
		// Email e = MessageParser.buildMessage(message);
		// e.setUuid("@@@");
		// MessageDao md = new MessageDao();
		// md.insertMessage(e);
		ArrayList<String> msgList = new ArrayList<String>();
		String path = "test_email.txt";
		// String path =
		// "D:/lab/final/mbox_file/test/201310.mbox99bd8c9c-44d6-4451-931b-a194815d0be3.txt";
		// String path =
		// "D:/lab/final/mbox_file/test/201311.mbox408b93dd-541e-4eff-853a-85cc50f2a33b.txt";
		//msgList = MBoxSpliter.splitMBox(new File(path));
		// System.out.println(msgList.size()); //check if the number of message
		// is right;
		String dir = "D:/Codes/Dragon/mbox_file/poi";
		File dirFile = new File(dir);
		int count = 0;
		if (dirFile.isDirectory()) {
			File[] fileList = dirFile.listFiles();
			for (File f : fileList) {
				msgList = MBoxSpliter.splitMBox(f);
				ArrayList<Email> emailList = new ArrayList<Email>();
				for (String msg : msgList) {
					Email e = MessageParser.buildMessage(msg);
					if (Checker.isGoodMessage(e)) {
						emailList.add(e);
					}
					else
						count++;
					// logger.info(e.getSubject() + e.getMessageID() +
					// e.getSendDate());
				}
				// SessionBuilder sb = new SessionBuilder(emailList);
				// sb.buildSessionAndContent();
				// ArrayList<Session> sessionList = sb.getSessionList();
				// for(Session s : sessionList) {
				// logger.info(s.getSubject() + " msg num : " +
				// s.getMsgList().split(",").length);
				// }
				// logger.info(sessionList.size());

			}
		}
		logger.info(count);
		// ArrayList<Email> emailList = new ArrayList<Email>();
		// for(String msg : msgList) {
		// Email e = MessageParser.buildMessage(msg);
		// if(Checker.isGoodMessage(e))
		// emailList.add(e);
		// // logger.info(e.getSubject());
		// }
		// System.out.println(emailList.size());
		// SessionBuilder sb = new SessionBuilder(emailList);
		// sb.buildSessionAndContent();
		// ArrayList<Session> sessionList = sb.getSessionList();
		// for(Session s : sessionList) {
		// logger.info(s.getSubject() + " msg num : " +
		// s.getMsgList().split(",").length);
		// }
		// logger.info(sessionList.size());
	}
}
