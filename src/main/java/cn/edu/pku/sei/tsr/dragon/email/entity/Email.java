package cn.edu.pku.sei.tsr.dragon.email.entity;

import java.io.Serializable;
import java.util.Date;

import cn.edu.pku.sei.tsr.dragon.content.entity.ContentInfo;

public class Email implements Serializable{

	private static final long serialVersionUID = -1774681023403218036L;
	private String	uuid;
	private int		projectID;
	/**
	 * @fieldName: fromName �ʼ��������ǳ�
	 * @fieldType: String
	 */
	private String	fromName;

	/**
	 * @fieldName: fromEmail �ʼ������ߵ��ʼ���ַ
	 * @fieldType: String
	 */
	private String	fromEmail;

	/**
	 * @fieldName: toName �ʼ������ߵ��ǳ�
	 * @fieldType: String
	 */
	private String	toName;

	/**
	 * @fieldName: toEmail �ʼ������ߵ��ʼ���ַ
	 * @fieldType: String
	 */

	private String	toEmail;

	/**
	 * @fieldName: date �ʼ�����ʱ�� ��ʱ��String ���洢 TODO �Ժ����޸�Ϊ���ڸ�ʽ
	 * @fieldType: String
	 */
	private Date	sendDate;

	/**
	 * @fieldName: messageID �ʼ���Ψһ��ʶ��������ʶ�ʼ�������session��Ҫ
	 * @fieldType: String
	 */
	private String	messageID;

	/**
	 * @fieldName: inReplyTo �÷��ʼ��ظ�����һ���ʼ�ID,���ڹ���session
	 * @fieldType: String
	 */
	private String	inReplyTo;

	/**
	 * @fieldName: subject �ʼ�����
	 * @fieldType: String
	 */
	private String	subject;

	/**
	 * @fieldName: keyWords �÷��ʼ��Ĺؼ�� �����Ƕ�� �ɣ��ָ� �� ��lucene��index"
	 * @fieldType: String
	 * @Description: TODO ��ʱδ�洢��������
	 */
	private String	keywords;

	/**
	 * @fieldName: content �ʼ�����
	 * @fieldType: String
	 */

	private String	content;
	private Content	emailContent;
	private ContentInfo contentInfo;

	public ContentInfo getContentInfo() {
		return contentInfo;
	}

	public void setContentInfo(ContentInfo contentInfo) {
		this.contentInfo = contentInfo;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getFromName() {
		return fromName;
	}

	public void setFromName(String fromName) {
		if(fromName==null)
		{
			fromName = "";
		}
		this.fromName = fromName;
	}

	public String getFromEmail() {
		return fromEmail;
	}

	public void setFromEmail(String fromEmail) {
		if(fromEmail==null)
		{
			fromEmail = "";
		}
		this.fromEmail = fromEmail;
	}

	public String getToName() {
		return toName;
	}

	public void setToName(String toName) {
		if(toName==null)
		{
			toName = "";
		}
		this.toName = toName;
	}

	public String getToEmail() {
		return toEmail;
	}

	public void setToEmail(String toEmail) {
		if(toEmail==null)
		{
			toEmail = "";
		}
		this.toEmail = toEmail;
	}

	public Date getSendDate() {
		return sendDate;
	}

	public void setSendDate(Date date) {
		this.sendDate = date;
	}

	public String getMessageID() {
		return messageID;
	}

	public void setMessageID(String messageID) {
		this.messageID = messageID;
	}

	public String getInReplyTo() {
		return inReplyTo;
	}

	public void setInReplyTo(String inReplyTo) {
		if(inReplyTo==null)
		{
			inReplyTo = "";
		}
		this.inReplyTo = inReplyTo;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getKeyWords() {
		return keywords;
	}

	public void setKeyWords(String keyWords) {
		this.keywords = keyWords;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public int getProjectID() {
		return projectID;
	}

	public void setProjectID(int projectID) {
		this.projectID = projectID;
	}

	public String toString() {
		String ret = "";
		ret += "------------------------------\n";
		ret += "messageID :" + this.messageID + "\n";
		ret += "projectID :" + this.projectID + "\n";
		ret += "subject :" + this.subject + "\n";
		ret += "sendDate :" + this.sendDate + "\n";
		ret += "fromName :" + this.fromName + "\n";
		ret += "fromEmail :" + this.fromEmail + "\n";
		ret += "toName :" + this.toName + "\n";
		ret += "toEmail :" + this.toEmail + "\n";
		ret += "inReplyTo :" + this.inReplyTo + "\n";
		// ret += "content :" + this.content + "\n";
		ret += "------------------------------\n";
		return ret;
	}

	public Content getEmailContent() {
		return emailContent;
	}

	public void setEmailContent(Content emailContent) {
		this.emailContent = emailContent;
	}
}
