package cn.edu.pku.sei.tsr.dragon.email.mbox.parser;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.UUID;

/*import cn.edu.pku.sei.tsr.dragon.email.dao.SessionContentDao;
import cn.edu.pku.sei.tsr.dragon.email.dao.SessionDao;*/
import cn.edu.pku.sei.tsr.dragon.email.entity.Email;
import cn.edu.pku.sei.tsr.dragon.email.entity.Session;
import cn.edu.pku.sei.tsr.dragon.email.entity.SessionContent;
import cn.edu.pku.sei.tsr.dragon.email.utils.MBoxSpliter;

public class SessionBuilder {

	public static final String			SPLIT_CONTENT		= "#####SPLIT_CONTENT#####";
	public static final String			QUESTION_MAIL		= "#####QUESTION_MAIL#####";
	public static final String			REPLY_MAIL			= "#####REPLY_MAIL#####";
	private ArrayList<Email>			emailList			= new ArrayList<Email>();
	private ArrayList<Session>			sessionList			= new ArrayList<Session>();
	private ArrayList<SessionContent>	sessionContentList	= new ArrayList<SessionContent>();

	public ArrayList<SessionContent> getSessionContentList() {
		return sessionContentList;
	}

	public void setSessionContentList(ArrayList<SessionContent> sessionContentList) {
		this.sessionContentList = sessionContentList;
	}

	public ArrayList<Session> getSessionList() {
		return sessionList;
	}

	public void setSessionList(ArrayList<Session> sessionList) {
		this.sessionList = sessionList;
	}

	public ArrayList<Email> getEmailList() {
		return emailList;
	}

	public void setEmailList(ArrayList<Email> emailList) {
		this.emailList = emailList;
	}

	private static String generateSessionID() {
		return UUID.randomUUID().toString();
	}

	public SessionBuilder(ArrayList<Email> list) {
		this.emailList = list;
	}

	public void buildSessionAndContent() {
		boolean isVisited[] = new boolean[emailList.size()];
		ArrayList<Email> newEmailList = new ArrayList<Email>();
		for (int i = 0; i < emailList.size(); i++) {
			if (isVisited[i])
				continue;
			Email email = emailList.get(i);
			if (email.getInReplyTo() == null || email.getInReplyTo().trim().length() == 0) {
				// in reply to is empty, this email maybe a start of a session
				String subject = email.getSubject();
				if (subject == null || subject.trim().length() == 0
						|| subject.toLowerCase().startsWith("re")
						|| subject.toLowerCase().startsWith("aw")) {
					// a email with no subject or its subject start with "re" is
					// not a session start
					continue;
				}

				newEmailList.add(email);
				isVisited[i] = true;
				Session oneSession = new Session();
				oneSession.setSessionID(generateSessionID());
				oneSession.setStartTime(email.getSendDate());
				oneSession.setPromoterName(email.getFromName());
				oneSession.setPromoterEmail(email.getFromEmail());
				oneSession.setSubject(email.getSubject());
				String participants = email.getFromEmail();
				if (email.getSendDate() == null)
					email.setSendDate(new Date());
				oneSession.setStartTime(email.getSendDate());

				Date endTime = email.getSendDate();
				if (endTime == null)
					endTime = new Date();
				HashSet<String> replySet = new HashSet<String>();
				replySet.add(email.getMessageID());

				SessionContent oneSessionContent = new SessionContent();
				oneSessionContent.setSessionID(oneSession.getSessionID());
				oneSessionContent.setSubject(subject);

				StringBuilder sessionContentSB = new StringBuilder();
				sessionContentSB.append(email.getSubject() + "\r\n");
				sessionContentSB.append(QUESTION_MAIL + "\r\n");
				sessionContentSB.append(email.getMessageID() + "\r\n");
				sessionContentSB.append("" + "\r\n");
				sessionContentSB.append(email.getContent() + "\r\n");
				sessionContentSB.append(SPLIT_CONTENT + "\r\n");

				for (int j = 0; j < emailList.size(); j++) {
					if (isVisited[j])
						continue;
					Email e = emailList.get(j);
					if (e.getSubject() != null
							&& e.getSubject().toLowerCase().contains(subject.toLowerCase())) {
						isVisited[j] = true;
						newEmailList.add(e);
						if (e.getInReplyTo() == null || e.getInReplyTo().trim().length() == 0) {
							e.setInReplyTo(email.getMessageID());
						}
						participants += "," + e.getFromEmail();
						if (!replySet.contains(e.getMessageID())) {
							replySet.add(e.getMessageID());
						}
						if (e.getSendDate() != null
								&& endTime.getTime() < e.getSendDate().getTime()) {
							endTime = e.getSendDate();
						}
						if (e.getFromEmail().equals(email.getFromEmail())) {
							sessionContentSB.append(QUESTION_MAIL + "\r\n");
						}
						else
							sessionContentSB.append(REPLY_MAIL + "\r\n");
						sessionContentSB.append(e.getMessageID() + "\r\n");
						sessionContentSB.append(e.getInReplyTo() + "\r\n");
						sessionContentSB.append(e.getContent() + "\r\n");
						sessionContentSB.append(SPLIT_CONTENT + "\r\n");
					}
					else if (e.getSubject() != null && e.getInReplyTo() != null
							&& replySet.contains(e.getInReplyTo())) {
						isVisited[j] = true;
						newEmailList.add(e);
						if (e.getInReplyTo() == null || e.getInReplyTo().trim().length() == 0) {
							e.setInReplyTo(email.getMessageID());
						}
						participants += "," + e.getFromEmail();
						if (!replySet.contains(e.getMessageID())) {
							replySet.add(e.getMessageID());
						}
						if (e.getSendDate() != null
								&& endTime.getTime() < e.getSendDate().getTime()) {
							endTime = e.getSendDate();
						}
						if (e.getFromEmail().equals(email.getFromEmail())) {
							sessionContentSB.append(QUESTION_MAIL + "\r\n");
						}
						else
							sessionContentSB.append(REPLY_MAIL + "\r\n");
						sessionContentSB.append(e.getMessageID() + "\r\n");
						sessionContentSB.append(e.getInReplyTo() + "\r\n");
						sessionContentSB.append(e.getContent() + "\r\n");
						sessionContentSB.append(SPLIT_CONTENT + "\r\n");
					}
				}

				oneSession.setEndTime(endTime);
				oneSession.setParticipants(participants);
				String msgList = "";
				for (String str : replySet) {
					if (msgList.length() == 0) {
						msgList = str;
					}
					else {
						msgList += "," + str;
					}
				}
				oneSession.setMsgList(msgList);
				oneSessionContent.setMsgList(msgList);
				oneSessionContent.setParticipants(participants);
				oneSessionContent.setContent(sessionContentSB.toString());
				sessionContentList.add(oneSessionContent);
				sessionList.add(oneSession);
			}
		}
		this.setEmailList(newEmailList);
	}

	public static void main(String args[]) throws ClassNotFoundException, SQLException {
		File mbox = new File("lab\\temp\\201309.mbox");
		ArrayList<String> msgList = MBoxSpliter.splitMBox(mbox);
		ArrayList<Email> emailList = new ArrayList<Email>();
		for (String msg : msgList) {
			Email e = MessageParser.buildMessage(msg);
			emailList.add(e);
		}
		SessionBuilder sessionBuilder = new SessionBuilder(emailList);
		sessionBuilder.buildSessionAndContent();
		ArrayList<Session> sessionList = sessionBuilder.getSessionList();
		System.out.println(sessionList.size());
/*		SessionDao sd = new SessionDao();
		for (int i = 0; i < sessionList.size(); i++) {
			System.out.println(i + " : " + sessionList.get(i).getMsgList());
			sd.insertSession(sessionList.get(i));
		}
		ArrayList<SessionContent> sessionContentList = sessionBuilder.getSessionContentList();
		SessionContentDao scd = new SessionContentDao();
		System.out.println(sessionContentList.size());
		for (SessionContent s : sessionContentList) {
			System.out.println(s.getSubject());
			scd.insertSessionContent(s);
		}*/
	}
}
