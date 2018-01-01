package cn.edu.pku.sei.tsr.dragon.codeparser.code.entity;


import cn.edu.pku.sei.tsr.dragon.codeparser.visitor.projectvisitor.JavaProjectVisitor;

public class JavaVariableInfo extends JavaBaseInfo {
	private static final long serialVersionUID = -6841462242714647874L;
	private JavaTypeInfo type;

	public JavaVariableInfo(String name, JavaTypeInfo type) {
		super(name);
		this.type = type;
	}

	@Override
	public String toString() {
		return String.format("[JavaVariable] %s: %s", getName(), type.getFullyQualifiedName());
	}

	public JavaTypeInfo getType() {
		return type;
	}

	public <R, A> void accept(JavaProjectVisitor<R, A> visitor, A arg) {
		visitor.visit(this, arg);
	}
}
