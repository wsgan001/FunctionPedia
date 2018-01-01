package cn.edu.pku.sei.tsr.dragon.search;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.UUID;

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

import cn.edu.pku.sei.tsr.dragon.email.entity.Email;
import cn.edu.pku.sei.tsr.dragon.email.entity.Session;
import cn.edu.pku.sei.tsr.dragon.email.entity.SessionContent;
import cn.edu.pku.sei.tsr.dragon.email.mbox.parser.SessionBuilder;
import cn.edu.pku.sei.tsr.dragon.email.utils.Checker;
import cn.edu.pku.sei.tsr.dragon.email.utils.MBoxSpliter;
import cn.edu.pku.sei.tsr.dragon.email.utils.StringUtils;
import cn.edu.pku.sei.tsr.dragon.search.util.Config;
import cn.edu.pku.sei.tsr.dragon.search.util.LuceneUtil;

public class SessionExtractor {
	private static MessageBuilder	builder	= new DefaultMessageBuilder();
	private static Message			message;
	private static StringBuffer		txtBody;
	private static StringBuffer		htmlBody;
	private static void parseMail(File file){
		try{
			ArrayList<String> emails = MBoxSpliter.splitMBox(file);
			ArrayList<Email> emailList = new ArrayList<Email>();
			for (String email : emails) {
				Email e = buildMessage(email);
				if (e == null)
					continue;
				e.setUuid(UUID.randomUUID().toString());
				e.setProjectID(1);
				e.setKeyWords(e.getSubject());
				if (Checker.isGoodMessage(e))
					emailList.add(e);
			}		
			ArrayList<Session> sessionList = new ArrayList<Session>();
			SessionBuilder sessionBuilder = new SessionBuilder(emailList);
			ArrayList<SessionContent> sessionContentList = new ArrayList<SessionContent>();
			sessionBuilder.buildSessionAndContent();
			sessionList = sessionBuilder.getSessionList();
			emailList = sessionBuilder.getEmailList();
			sessionContentList = sessionBuilder.getSessionContentList();
			String sb = "";
			for (SessionContent sc : sessionContentList){
				LuceneUtil.updateSessionToLucene(sc.getContent(), sc.getSessionID());
			}			
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public static Email buildMessage(String msg) {
		if (msg == null) {
			return null;
		}
		InputStream is = new ByteArrayInputStream(msg.getBytes());
		Email email = new Email();
		try {

			message = builder.parseMessage(is);
			txtBody = new StringBuffer();
			htmlBody = new StringBuffer();

			email.setSubject(StringUtils.trimString(message.getSubject()));
			email.setSendDate(message.getDate());
			email.setMessageID(StringUtils.trimString(message.getMessageId()));

			MailboxList from = message.getFrom();
			if (from != null && from.size() > 0) {
				email.setFromName(StringUtils.trimString(from.get(0).getName()));
				email.setFromEmail(StringUtils.trimString(from.get(0).getAddress()));
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
				}
			}

			Header header = message.getHeader();
			for (Field field : header.getFields()) {
				String f = field.getName();
				if (f.toLowerCase().contains("in-reply-to")) {
					String inReplyTo = field.getBody();
					email.setInReplyTo(StringUtils.trimString(inReplyTo));
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
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return email;
	}
	
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
	
	public static void extractSessionToLucene(String path){
		File fileDir = new File(path);
		int cnt = 0;
		if (fileDir.isDirectory()) {
			File files[] = fileDir.listFiles();
			if (files != null && files.length > 0) {
				for (File file : files) {
					cnt ++;
					parseMail(file);
					System.out.println(cnt);
				}
			}
		}
	}
	
	public static void main(String args[]){
		String path = Config.getSessionDir();
		extractSessionToLucene(path);
		try {
			LuceneUtil.directory.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
