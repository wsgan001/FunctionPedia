package cn.edu.pku.sei.tsr.dragon.codeparser.code.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.Type;

import cn.edu.pku.sei.tsr.dragon.codeparser.visitor.projectvisitor.JavaProjectVisitor;

public class JavaProjectInfo extends JavaBaseInfo {
	private static final long serialVersionUID = -5075278159910049878L;
	private JavaPackageInfo rootPackge = new JavaPackageInfo("", null);
	private List<JavaClassInfo> classes = new ArrayList<>();
	private List<JavaInterfaceInfo> interfaces = new ArrayList<>();
	public JavaProjectInfo(String name) {
		super(name);
	}

	public JavaPackageInfo getPackage(String path, boolean created) {
		return rootPackge.getPackage(new LinkedList<>(Arrays.asList(path.split("\\."))), created);
	}

	public void addClass(JavaClassInfo javaClassInfo) {
		classes.add(javaClassInfo);
	}

	public JavaClassInfo getClass(JavaPackageInfo javaPackage, JavaTypeInfo currentType, List<String> imports, Type type) {
		if (type == null) return null;
		String typeName = type.toString();

		// parameterized type
		if (type instanceof ParameterizedType) {
			typeName = typeName.replaceAll("<.*>", "");
		}

		// single import
		for (String importDec : imports) {
			LinkedList<String> domains = new LinkedList<>(Arrays.asList(importDec.split("\\.")));
			if (!domains.getLast().equals(typeName)) continue;
			domains.removeLast();
			JavaPackageInfo belongPackage = getPackage(String.join(".", domains), false);
			if (belongPackage == null)
				return new JavaClassInfo(String.join(".", domains) + "." + typeName, null, null);
			return belongPackage.getClass(typeName);
		}

		// wildcard import
		List<String> wildcardImport = imports.stream().filter(x -> x.charAt(x.length() - 1) == '*').collect(Collectors.toList());
		for (String importDec : wildcardImport) {
			LinkedList<String> domains = new LinkedList<>(Arrays.asList(importDec.split("\\.")));
			domains.removeLast();
			JavaPackageInfo belongPackage = getPackage(String.join(".", domains), false);
			if (belongPackage == null) continue;
			JavaClassInfo javaClassInfo = belongPackage.getClass(typeName);
			if (javaClassInfo != null) return javaClassInfo;
		}

		JavaClassInfo javaClassInfo = null;

		// same outer type
		if (currentType != null) javaClassInfo = currentType.getInnerClass(typeName);
		if (javaClassInfo != null) return javaClassInfo;

		// same package
		javaClassInfo = javaPackage.getClass(typeName);
		if (javaClassInfo != null) return javaClassInfo;
		return new JavaClassInfo(typeName, null, null);
	}

	public JavaTypeInfo getType(JavaPackageInfo javaPackage, List<String> imports, Type type, TypeKind expectedType) {
		if (type == null) return null;

		// primitive type
		if (type instanceof PrimitiveType) {
			return JavaPrimitiveTypeInfo.PRIMITIVE_TYPES.get(((PrimitiveType) type).getPrimitiveTypeCode());
		}

		String typeName = type.toString();

		// parameterized type
		if (type instanceof ParameterizedType) {
			typeName = typeName.replaceAll("<.*>", "");
		}

		// single import
		for (String importDec : imports) {
			LinkedList<String> domains = new LinkedList<>(Arrays.asList(importDec.split("\\.")));
			if (!domains.getLast().equals(typeName)) continue;
			domains.removeLast();
			JavaPackageInfo belongPackage = getPackage(String.join(".", domains), false);
			if (belongPackage == null) {
				switch (expectedType) {
					case CLASS:
						return new JavaClassInfo(String.join(".", domains) + "." + typeName, null, null);
					case INTERFACE:
						return new JavaInterfaceInfo(String.join(".", domains) + "." + typeName, null, null);
					case TYPE:
						return new JavaTypeInfo(String.join(".", domains) + "." + typeName, null, null);
				}
			}
			return belongPackage.getType(typeName);
		}

		// wildcard import
		List<String> wildcardImport = imports.stream().filter(x -> x.charAt(x.length() - 1) == '*').collect(Collectors.toList());
		for (String importDec : wildcardImport) {
			LinkedList<String> domains = new LinkedList<>(Arrays.asList(importDec.split("\\.")));
			domains.removeLast();
			JavaPackageInfo belongPackage = getPackage(String.join(".", domains), false);
			if (belongPackage == null) continue;
			JavaTypeInfo javaType = belongPackage.getType(typeName);
			if (javaType != null) return javaType;
		}

		// same package
		JavaTypeInfo javaType = javaPackage.getType(typeName);
		if (javaType != null) return javaType;

		// built-in
		switch (expectedType) {
			case CLASS:
				return new JavaClassInfo(typeName, null, null);
			case INTERFACE:
				return new JavaInterfaceInfo(typeName, null, null);
			case TYPE:
				return new JavaTypeInfo(typeName, null, null);
		}

		return null;
	}

	public void addInterface(JavaInterfaceInfo javaInterface) {
		interfaces.add(javaInterface);
	}

	public <R, A> void accept(JavaProjectVisitor<R, A> visitor, A arg) {
		visitor.visit(this, arg);
		classes.forEach(x -> x.accept(visitor, arg));
		interfaces.forEach(x -> x.accept(visitor, arg));
	}

	public enum TypeKind {
		CLASS, INTERFACE, TYPE
	}

	public JavaPackageInfo getRootPackge() {
		return rootPackge;
	}

	public List<JavaClassInfo> getClasses() {
		return classes;
	}

	public List<JavaInterfaceInfo> getInterfaces() {
		return interfaces;
	}

}
