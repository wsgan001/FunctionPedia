package cn.edu.pku.sei.tsr.dragon.visitor;

import cn.edu.pku.sei.tsr.dragon.code.entity.JavaBase;
import cn.edu.pku.sei.tsr.dragon.code.entity.JavaClass;
import cn.edu.pku.sei.tsr.dragon.code.entity.JavaFile;
import cn.edu.pku.sei.tsr.dragon.code.entity.JavaMethod;
import cn.edu.pku.sei.tsr.dragon.code.entity.JavaPackage;
import cn.edu.pku.sei.tsr.dragon.code.entity.JavaVariable;

public abstract class JavaBaseVisitor<R> {
	public abstract R visit(JavaBase javaBase);

	public abstract R visit(JavaVariable javaVariable);

	public abstract R visit(JavaMethod javaMethod);

	public abstract R visit(JavaClass javaClass);

	public abstract R visit(JavaFile javaFile);

	public abstract R visit(JavaPackage javaPackage);
}
