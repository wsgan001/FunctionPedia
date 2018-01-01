package cn.edu.pku.sei.tsr.dragon.codeparser.code.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class JavaPackageInfo extends JavaBaseInfo {
	private static final long serialVersionUID = -8096561287494730414L;
	private JavaPackageInfo parentPackage;
	private List<JavaPackageInfo> packages = new ArrayList<>();
	private Map<String, JavaClassInfo> classes = new HashMap<>();
	private Map<String, JavaInterfaceInfo> interfaces = new HashMap<>();

	public JavaPackageInfo(String name, JavaPackageInfo parentPackage) {
		super(name);
		this.parentPackage = parentPackage;
	}

	@Override
	public String toString() {
		return String.format("[JavaPackage] %s", getFullyQualifiedName());
	}

	public String getFullyQualifiedName() {
		String result;
		String parentPackageName = "";
		if (parentPackage != null) parentPackageName = parentPackage.getFullyQualifiedName();
		if (parentPackageName.equals("")) {
			result = String.format("%s", getName());
		} else {
			result = String.format("%s.%s", parentPackageName, getName());
		}
		return result;
	}

	public JavaPackageInfo getPackage(LinkedList<String> path, boolean created) {
		if (path.isEmpty()) return this;

		String first = path.removeFirst();

		for (JavaPackageInfo javaPackage : packages) {
			if (first.equals(javaPackage.getName())) {
				return javaPackage.getPackage(path, created);
			}
		}

		if (!created) return null;

		JavaPackageInfo newPackage = new JavaPackageInfo(first, this);
		packages.add(newPackage);
		return newPackage.getPackage(path, true);
	}

	public void addClass(JavaClassInfo javaClassInfo) {
		classes.put(javaClassInfo.getName(), javaClassInfo);
	}

	public JavaClassInfo getClass(String name) {
		return classes.get(name);
	}

	public void addInterface(JavaInterfaceInfo javaInterface) {
		interfaces.put(javaInterface.getName(), javaInterface);
	}

	public JavaInterfaceInfo getInterface(String name) {
		return interfaces.get(name);
	}

	public JavaTypeInfo getType(String name) {
		JavaTypeInfo result = classes.get(name);
		if (result == null) result = interfaces.get(name);
		return result;
	}

	public JavaPackageInfo getParentPackage() {
		return parentPackage;
	}

	public List<JavaPackageInfo> getPackages() {
		return packages;
	}

	public Map<String, JavaClassInfo> getClasses() {
		return classes;
	}

	public Map<String, JavaInterfaceInfo> getInterfaces() {
		return interfaces;
	}
}
