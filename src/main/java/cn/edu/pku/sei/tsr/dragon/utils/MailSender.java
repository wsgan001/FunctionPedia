package cn.edu.pku.sei.tsr.dragon.utils;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/*
 * A utility class to send email.
 */
public class MailSender {

	private static final String	HTML_NEWLINE	= "<br/>";
	private static boolean		isDebug			= false;	// debug state or
															// not, debug state
															// will not send
															// email, but just
															// display the
															// email's content
															// to console.

	/*
	 * Send an email, which has the title $title and the content $content, to
	 * $toMailAddress.
	 * 
	 * @param toMailAddress the email's receiver
	 * 
	 * @param title the email's title
	 * 
	 * @param content the email's content
	 * 
	 * @return a flag which indicates whether the email has been sent
	 * successfully or not, and true for success, false for failure.
	 */
	public static boolean sendMail(String toMailAddress, String title, String content) {
		if (toMailAddress == null || title == null || content == null) {
			return false;
		}

		if (isDebug) {
			displayEmailContent(toMailAddress, title, content);
			return true;
		}

		content = formatEmailContent(content);

		String to = toMailAddress;
		String from = "temp123@163.com";
		String host = "smtp.163.com";

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");// support TTLS for
														// security.
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", "25");

		Session session = Session.getInstance(props, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("temp123@163.com", "asdftemp123");
			}
		});

		String mailSenderName = "System Reporter";
		try {
			MimeMessage message = new MimeMessage(session);

			message.setFrom(new InternetAddress(from, mailSenderName));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
			message.setSubject(title);
			message.setText(content, "utf-8", "html");

			Transport.send(message);

			System.out.println("Sent message successfully....");
			return true;
		}
		catch (MessagingException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return false;
	}

	/*
	 * Format the given email content.
	 * 
	 * @param mailContent the email content which will be formatted
	 * 
	 * @return the formatted email content
	 */
	private static String formatEmailContent(String mailContent) {
		StringBuilder formattedContentBuilder = new StringBuilder();
		int contentLength = mailContent.length();
		for (int i = 0; i < contentLength; i++) {
			char ch = mailContent.charAt(i);

			if (ch == '\n') {
				formattedContentBuilder.append(HTML_NEWLINE);
			}
			else {
				formattedContentBuilder.append(ch);
			}
		}
		return formattedContentBuilder.toString();
	}

	/*
	 * Toggle debug state between DEBUG and Non-DEBUG.
	 * 
	 * @param isDebug a boolean flag indicates whether it is DEBUG state or not.
	 */
	public static void toggleDebug(boolean isDebug) {
		MailSender.isDebug = isDebug;
	}

	/*
	 * Display the email, including the receiver mail address, title and
	 * content.
	 * 
	 * @param toMail receiver mail address of the email
	 * 
	 * @param title title of the email
	 * 
	 * @param content content of the email
	 */
	private static void displayEmailContent(String toMail, String title, String content) {
		synchronized (System.out) {
			System.out.println("--------------------------------------------------");
			System.out.println("Mail Content:");
			System.out.println();
			System.out.printf("to:%s\n", toMail);
			System.out.printf("Title:%s\n", title);
			System.out.printf("Content:\n%s\n", content);
			System.out.println("--------------------------------------------------");
		}
	}

	public static void main(String[] args) {
		String msg = "This is a test email.";

		toggleDebug(false);
		sendMail("shutear@qq.com", "Mail Testing", msg);
	}
}
