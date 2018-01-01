package cn.edu.pku.sei.tsr.dragon.codeparser.visitor.projectvisitor;


import java.util.List;

import cn.edu.pku.sei.tsr.dragon.codeparser.code.entity.JavaBaseInfo;
import cn.edu.pku.sei.tsr.dragon.codeparser.code.entity.JavaClassInfo;
import cn.edu.pku.sei.tsr.dragon.codeparser.code.entity.JavaInterfaceInfo;
import cn.edu.pku.sei.tsr.dragon.codeparser.code.entity.JavaMethodInfo;
import cn.edu.pku.sei.tsr.dragon.codeparser.code.entity.JavaPackageInfo;
import cn.edu.pku.sei.tsr.dragon.codeparser.code.entity.JavaProjectInfo;
import cn.edu.pku.sei.tsr.dragon.codeparser.code.entity.JavaVariableInfo;

public class MethodCollectVisitor extends JavaProjectVisitor<Object, List<JavaMethodInfo>> {
	@Override
	public Object visit(JavaBaseInfo javaBaseInfo, List<JavaMethodInfo> arg) {
		return null;
	}

	@Override
	public Object visit(JavaProjectInfo javaProject, List<JavaMethodInfo> arg) {
		return null;
	}

	@Override
	public Object visit(JavaPackageInfo javaPackage, List<JavaMethodInfo> arg) {
		return null;
	}

	@Override
	public Object visit(JavaClassInfo javaClassInfo, List<JavaMethodInfo> arg) {
		arg.addAll(javaClassInfo.getMethods());
		return null;
	}

	@Override
	public Object visit(JavaInterfaceInfo javaInterface, List<JavaMethodInfo> arg) {
		arg.addAll(javaInterface.getMethods());
		return null;
	}

	@Override
	public Object visit(JavaMethodInfo javaMethod, List<JavaMethodInfo> arg) {
		return null;
	}

	@Override
	public Object visit(JavaVariableInfo javaVariable, List<JavaMethodInfo> arg) {
		return null;
	}
}
