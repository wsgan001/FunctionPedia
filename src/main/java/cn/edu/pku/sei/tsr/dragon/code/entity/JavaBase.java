package cn.edu.pku.sei.tsr.dragon.code.entity;

import java.io.Serializable;
import java.util.UUID;

import org.json.JSONString;

import cn.edu.pku.sei.tsr.dragon.utils.UUIDInterface;
import cn.edu.pku.sei.tsr.dragon.visitor.JavaBaseVisitor;

public abstract class JavaBase implements JSONString, UUIDInterface, Serializable {
	private static final long	serialVersionUID	= 6133626550171541857L;
	private Comment				comment;
	private Name				name;
	private String				uuid				= UUID.randomUUID().toString();

	public abstract void writeToFile(String projectName);

	public JavaBase(Name name) {
		this.name = name;
		System.out.println(name);
	}

	public void setComment(Comment comment) {
		this.comment = comment;
	}

	public Comment getComment() {
		return comment;
	}

	public Name getName() {
		return name;
	}

	@SuppressWarnings("rawtypes")
	public abstract void accept(JavaBaseVisitor visitor);

	@Override
	public String getUuid() {
		return uuid;
	}
}
