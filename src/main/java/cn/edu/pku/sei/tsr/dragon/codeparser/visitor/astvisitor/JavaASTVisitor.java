package cn.edu.pku.sei.tsr.dragon.codeparser.visitor.astvisitor;

import org.eclipse.jdt.core.dom.ASTVisitor;

import cn.edu.pku.sei.tsr.dragon.codeparser.code.entity.JavaProjectInfo;

public abstract class JavaASTVisitor extends ASTVisitor {
	protected JavaProjectInfo project = null;

	public JavaASTVisitor(JavaProjectInfo project) {
		this.project = project;
	}

	public abstract void reset();
}
