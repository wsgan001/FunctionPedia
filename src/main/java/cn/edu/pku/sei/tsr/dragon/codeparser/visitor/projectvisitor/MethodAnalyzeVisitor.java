package cn.edu.pku.sei.tsr.dragon.codeparser.visitor.projectvisitor;

import org.eclipse.jdt.core.dom.Block;

import cn.edu.pku.sei.tsr.dragon.codeparser.code.entity.JavaBaseInfo;
import cn.edu.pku.sei.tsr.dragon.codeparser.code.entity.JavaClassInfo;
import cn.edu.pku.sei.tsr.dragon.codeparser.code.entity.JavaInterfaceInfo;
import cn.edu.pku.sei.tsr.dragon.codeparser.code.entity.JavaMethodInfo;
import cn.edu.pku.sei.tsr.dragon.codeparser.code.entity.JavaPackageInfo;
import cn.edu.pku.sei.tsr.dragon.codeparser.code.entity.JavaProjectInfo;
import cn.edu.pku.sei.tsr.dragon.codeparser.code.entity.JavaVariableInfo;
import cn.edu.pku.sei.tsr.dragon.codeparser.visitor.astvisitor.FieldChangeVisitor;

public class MethodAnalyzeVisitor extends JavaProjectVisitor<Object, Object> {
	@Override
	public Object visit(JavaBaseInfo javaBaseInfo, Object arg) {
		return null;
	}

	@Override
	public Object visit(JavaProjectInfo javaProject, Object arg) {
		return null;
	}

	@Override
	public Object visit(JavaPackageInfo javaPackage, Object arg) {
		return null;
	}

	@Override
	public Object visit(JavaClassInfo javaClassInfo, Object arg) {
		return null;
	}

	@Override
	public Object visit(JavaInterfaceInfo javaInterface, Object arg) {
		return null;
	}

	@Override
	public Object visit(JavaMethodInfo javaMethod, Object arg) {
		Block body = javaMethod.getBody();
		if (body != null) body.accept(new FieldChangeVisitor(javaMethod));
		return null;
	}

	@Override
	public Object visit(JavaVariableInfo javaVariable, Object arg) {
		return null;
	}
}
