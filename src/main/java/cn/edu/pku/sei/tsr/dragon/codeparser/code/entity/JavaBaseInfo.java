package cn.edu.pku.sei.tsr.dragon.codeparser.code.entity;


import java.io.Serializable;
import java.util.UUID;

import org.eclipse.jdt.core.dom.Modifier;

import cn.edu.pku.sei.tsr.dragon.codeparser.visitor.projectvisitor.JavaProjectVisitor;
import cn.edu.pku.sei.tsr.dragon.utils.UUIDInterface;

public abstract class JavaBaseInfo implements UUIDInterface, Serializable {
	private static final long	serialVersionUID	= 6133626550171541857L;
	private Comment				comment;
	private String				name;
	private String				uuid				= UUID.randomUUID().toString();
	private int					modifierFlag;
	public JavaBaseInfo(String name) {
		this.name = name;
	}

	public void setComment(Comment comment) {
		this.comment = comment;
	}

	public Comment getComment() {
		return comment;
	}

	public String getName() {
		return name;
	}

	@Override
	public String getUuid() {
		return uuid;
	}
	
	public void setModifier(int modifierFlag){
		this.modifierFlag = modifierFlag;
	}
	
	public boolean ispublic(){
		return Modifier.isPublic(this.modifierFlag);
	}
	
	public boolean isprivate(){
		return Modifier.isPrivate(this.modifierFlag);
	}
	
	public boolean isprotected(){
		return Modifier.isProtected(this.modifierFlag);
	}
	
	public boolean isabstract(){
		return Modifier.isAbstract(this.modifierFlag);
	}
	
	public boolean isfinal(){
		return Modifier.isFinal(this.modifierFlag);
	}
	
	public boolean isstatic(){
		return Modifier.isStatic(this.modifierFlag);
	}
	
	public <R, A> void accept(JavaProjectVisitor<R, A> visitor, A arg) {
		visitor.visit(this, arg);
	}
}
