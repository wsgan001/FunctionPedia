package cn.edu.pku.sei.tsr.dragon.email.mbox.parser;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;

import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.james.mime4j.MimeException;
import org.apache.james.mime4j.dom.Body;
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
import org.apache.james.mime4j.message.MessageImpl;
import org.apache.james.mime4j.stream.Field;

import cn.edu.pku.sei.tsr.dragon.email.entity.Email;
import cn.edu.pku.sei.tsr.dragon.email.utils.StringUtils;

public class EmailParser {

	/**
	 * Wraps an Object and associates it with a text. All message parts
	 * (headers, bodies, multiparts, body parts) will be wrapped in
	 * ObjectWrapper instances before they are added to the JTree instance.
	 */
	private String	textContent	= "";
	private Email	email		= new Email();

	public Email getEmail() {
		return email;
	}

	public static class ObjectWrapper {

		private String	text	= "";
		private Object	object	= null;

		public ObjectWrapper(String text, Object object) {
			this.text = text;
			this.object = object;
		}

		@Override
		public String toString() {
			return text;
		}

		public Object getObject() {
			return object;
		}
	}

	public DefaultMutableTreeNode createNode(Entity entity) {

		/*
		 * Create the root node for the entity. It's either a Message or a Body
		 * part.
		 */
		String type = "Message";

		if (entity instanceof BodyPart) {
			type = "Body part";
		}
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(new ObjectWrapper(type, entity));

		/*
		 * Add the node encapsulating the entity Header.
		 */
		node.add(createNode(entity.getHeader()));

		Body body = entity.getBody();

		if (body instanceof Multipart) {
			/*
			 * The body of the entity is a Multipart.
			 */

			node.add(createNode((Multipart) body));
		}
		else if (body instanceof MessageImpl) {
			/*
			 * The body is another Message.
			 */

			node.add(createNode((MessageImpl) body));

		}
		else {
			/*
			 * Discrete Body (either of type TextBody or BinaryBody).
			 */
			type = "Text body";
			if (body instanceof TextBody) {
				TextBody tb = (TextBody) body;
				StringBuilder sb = new StringBuilder();
				try {
					Reader r = tb.getReader();
					int c;
					while ((c = r.read()) != -1) {
						sb.append((char) c);
					}
				}
				catch (IOException ex) {
					ex.printStackTrace();
				}
				setTextContent(getTextContent() + new String(sb.toString()));
			}
			node.add(new DefaultMutableTreeNode(new ObjectWrapper(type, body)));

		}

		return node;
	}

	/**
	 * Create a node given a Multipart body. Add the Preamble, all Body parts
	 * and the Epilogue to the node.
	 * 
	 * @param multipart
	 *            the Multipart.
	 * @return the root node of the tree.
	 */
	private DefaultMutableTreeNode createNode(Multipart multipart) {
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(new ObjectWrapper("Multipart",
				multipart));

		node.add(new DefaultMutableTreeNode(new ObjectWrapper("Preamble", multipart.getPreamble())));
		for (Entity part : multipart.getBodyParts()) {
			node.add(createNode(part));
		}
		node.add(new DefaultMutableTreeNode(new ObjectWrapper("Epilogue", multipart.getEpilogue())));

		return node;
	}

	/**
	 * Create a node given a Multipart body. Add the Preamble, all Body parts
	 * and the Epilogue to the node.
	 * 
	 * @param multipart
	 *            the Multipart.
	 * @return the root node of the tree.
	 */
	private DefaultMutableTreeNode createNode(Header header) {
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(
				new ObjectWrapper("Header", header));

		for (Field field : header.getFields()) {
			String name = field.getName();
			if (name.toLowerCase().contains("subject")) {
				email.setSubject(field.getBody().toString());
			}
			else if (name.toLowerCase().contains("message-id")) {
				email.setMessageID(field.getBody().toString());
			}
			else if (name.toLowerCase().contains("in-reply-to")) {
				String inReplyTo = field.getBody();
				email.setInReplyTo(StringUtils.trimString(inReplyTo));
				// System.out.println("in-reply-to : " +
				// email.getInReplyTo());
			}
			node.add(new DefaultMutableTreeNode(new ObjectWrapper(name, field)));
		}

		return node;
	}

	public String getTextContent() {
		return textContent;
	}

	public void setTextContent(String textContent) {
		this.textContent = textContent;
	}

	public Email parse(String msg) {
		email = new Email();
		final MessageBuilder builder = new DefaultMessageBuilder();
		try {
			final Message message = builder.parseMessage(new ByteArrayInputStream(msg.getBytes()));
			if (message.getContentTransferEncoding().toLowerCase().contains("7bit"))
				return null;
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

			createNode(message);
		}
		catch (MimeException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		email.setContent(textContent);
		return email;
	}

	public static void main(String args[]) throws ClassNotFoundException, SQLException {
		final MessageBuilder builder = new DefaultMessageBuilder();
		try {
			final Message message = builder.parseMessage(new FileInputStream(new File(
					"test_email.txt")));
			EmailParser ep = new EmailParser();
			ep.createNode(message);
			System.out.println(ep.getTextContent());
			Email e = ep.email;
			System.out.println(e.toString());
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (MimeException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

}
