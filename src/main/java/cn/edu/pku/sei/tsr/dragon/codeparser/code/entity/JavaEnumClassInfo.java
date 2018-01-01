package cn.edu.pku.sei.tsr.dragon.codeparser.code.entity;


public class JavaEnumClassInfo extends JavaClassInfo {
	public JavaEnumClassInfo(String name, JavaPackageInfo javaPackage, JavaTypeInfo outerClass) {
		super(name, javaPackage, outerClass);
	}

	@Override
	public String toString() {
		return String.format(
			"[JavaEnumClass] %s.%s", getJavaPackage().getFullyQualifiedName(), getNameWithOuterType()
		);
	}

}
