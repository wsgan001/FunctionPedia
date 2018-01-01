package cn.edu.pku.sei.tsr.dragon.outdated;

import java.io.Serializable;

import cn.edu.pku.sei.tsr.dragon.utils.SizeOf;

public class SOPostSimplified implements Serializable {
	private static final long	serialVersionUID	= 2715197374245549415L;

	private int					id;
	private int					acceptedAnswerId;
	private String				tags;

	public SOPostSimplified() {
		super();
	}
	public SOPostSimplified(int _id, int _acceptedAnswerId) {
		this();
		id = _id;
		acceptedAnswerId = _acceptedAnswerId;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getAcceptedAnswerId() {
		return acceptedAnswerId;
	}
	public void setAcceptedAnswerId(int acceptedAnswerId) {
		this.acceptedAnswerId = acceptedAnswerId;
	}

	public static void main(String[] args) {
		System.out.println(SizeOf.getObjectSize(new SOPostSimplified(1321343243, 978217212)));
		System.out.println(SizeOf.getObjectSize(new Integer(978217212)));
	}
	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}

}
