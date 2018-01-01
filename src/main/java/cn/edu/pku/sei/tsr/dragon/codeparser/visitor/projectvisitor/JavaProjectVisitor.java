package cn.edu.pku.sei.tsr.dragon.codeparser.visitor.projectvisitor;


import cn.edu.pku.sei.tsr.dragon.codeparser.code.entity.JavaBaseInfo;
import cn.edu.pku.sei.tsr.dragon.codeparser.code.entity.JavaClassInfo;
import cn.edu.pku.sei.tsr.dragon.codeparser.code.entity.JavaInterfaceInfo;
import cn.edu.pku.sei.tsr.dragon.codeparser.code.entity.JavaMethodInfo;
import cn.edu.pku.sei.tsr.dragon.codeparser.code.entity.JavaPackageInfo;
import cn.edu.pku.sei.tsr.dragon.codeparser.code.entity.JavaProjectInfo;
import cn.edu.pku.sei.tsr.dragon.codeparser.code.entity.JavaVariableInfo;

public abstract class JavaProjectVisitor<R, A> {
	public abstract R visit(JavaBaseInfo javaBaseInfo, A arg);

	public abstract R visit(JavaProjectInfo javaProject, A arg);

	public abstract R visit(JavaPackageInfo javaPackage, A arg);

	public abstract R visit(JavaClassInfo javaClassInfo, A arg);

	public abstract R visit(JavaInterfaceInfo javaInterface, A arg);

	public abstract R visit(JavaMethodInfo javaMethod, A arg);

	public abstract R visit(JavaVariableInfo javaVariable, A arg);

}
