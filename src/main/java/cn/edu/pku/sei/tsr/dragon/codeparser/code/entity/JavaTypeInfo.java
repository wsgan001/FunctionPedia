package cn.edu.pku.sei.tsr.dragon.codeparser.code.entity;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.edu.pku.sei.tsr.dragon.codeparser.visitor.projectvisitor.JavaProjectVisitor;

public class JavaTypeInfo extends JavaBaseInfo {
	private static final long serialVersionUID = -4005096473911792186L;
	protected Map<String, JavaClassInfo> innerClasses = new HashMap<>();
	protected Map<String, JavaInterfaceInfo> innerInterfaces = new HashMap<>();
	private JavaPackageInfo javaPackage;
	private JavaTypeInfo outerType;
	private Map<String, JavaVariableInfo> fields = new HashMap<>();
	private List<JavaMethodInfo> methods = new ArrayList<>();
	private Map<String, JavaInterfaceInfo> superInterfaces = new HashMap<>();

	public JavaTypeInfo(String name, JavaPackageInfo javaPackage, JavaTypeInfo outerType) {
		super(name);
		this.javaPackage = javaPackage;
		this.outerType = outerType;
	}

	public JavaPackageInfo getJavaPackage() {
		return javaPackage;
	}

	public String getFullyQualifiedName() {
		if (javaPackage != null)
			return String.format("%s.%s", javaPackage.getFullyQualifiedName(), getNameWithOuterType());
		return String.format("%s (Lib Class)", getName());
	}

	public String getNameWithOuterType() {
		String result;
		if (outerType != null) {
			result = String.format("%s.%s", outerType.getNameWithOuterType(), getName());
		} else {
			result = String.format("%s", getName());
		}
		return result;
	}

	public JavaTypeInfo getOuterType() {
		return outerType;
	}

	public void addInnerClass(JavaClassInfo innerClass) {
		innerClasses.put(innerClass.getName(), innerClass);
	}

	public JavaClassInfo getInnerClass(String name) {
		return innerClasses.get(name);
	}

	public void addInnerInterface(JavaInterfaceInfo innerInterface) {
		innerInterfaces.put(innerInterface.getName(), innerInterface);
	}

	public JavaInterfaceInfo getInnerInterface(String name) {
		return innerInterfaces.get(name);
	}

	public void addMethod(JavaMethodInfo method) {
		methods.add(method);
	}

	public List<JavaMethodInfo> getMethods() {
		return methods;
	}

	public void addField(JavaVariableInfo field) {
		fields.put(field.getName(), field);
	}

	public JavaVariableInfo getField(String name) {
		return fields.get(name);
	}

	public JavaVariableInfo getFieldWithOuterType(String name) {
		JavaVariableInfo field = getField(name);
		if (field != null) return field;
		if (outerType != null) return outerType.getFieldWithOuterType(name);
		return null;
	}

	public void addSuperInterface(JavaInterfaceInfo superInterface) {
		superInterfaces.put(superInterface.getName(), superInterface);
	}

	public <R, A> void accept(JavaProjectVisitor<R, A> visitor, A arg) {
		fields.forEach((k, v) -> v.accept(visitor, arg));
		methods.forEach(x -> x.accept(visitor, arg));
	}

	public Map<String, JavaClassInfo> getInnerClasses() {
		return innerClasses;
	}

	public Map<String, JavaInterfaceInfo> getInnerInterfaces() {
		return innerInterfaces;
	}

	public Map<String, JavaVariableInfo> getFields() {
		return fields;
	}

	public Map<String, JavaInterfaceInfo> getSuperInterfaces() {
		return superInterfaces;
	}

}
