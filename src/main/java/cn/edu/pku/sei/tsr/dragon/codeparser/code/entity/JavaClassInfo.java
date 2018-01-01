package cn.edu.pku.sei.tsr.dragon.codeparser.code.entity;

import cn.edu.pku.sei.tsr.dragon.codeparser.visitor.projectvisitor.JavaProjectVisitor;

public class JavaClassInfo extends JavaTypeInfo {
	private static final long serialVersionUID = -7051789768936732402L;
	public static final JavaClassInfo objectClass = new JavaClassInfo("java.lang.Object", null, null);

	private JavaClassInfo superClass = objectClass;

	public JavaClassInfo(String name, JavaPackageInfo javaPackage, JavaTypeInfo outerType) {
		super(name, javaPackage, outerType);
	}

	@Override
	public String toString() {
		String result;
		result = String.format("[JavaClass] %s", getFullyQualifiedName());
		return result;
	}

	public void setSuperClass(JavaClassInfo superClass) {
		if (superClass == null) return;
		this.superClass = superClass;
	}

	public JavaClassInfo getSuperClass() {
		return superClass;
	}

	public <R, A> void accept(JavaProjectVisitor<R, A> visitor, A arg) {
		visitor.visit(this, arg);
		super.accept(visitor, arg);
	}

	@Override
	public JavaVariableInfo getField(String name) {
		JavaVariableInfo field = super.getField(name);
		if (field != null) return field;
		if (superClass != null) return superClass.getField(name);
		return null;
	}

}
