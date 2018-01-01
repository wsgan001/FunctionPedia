package cn.edu.pku.sei.tsr.dragon.visitor;

import cn.edu.pku.sei.tsr.dragon.code.entity.JavaBase;
import cn.edu.pku.sei.tsr.dragon.code.entity.JavaClass;
import cn.edu.pku.sei.tsr.dragon.code.entity.JavaFile;
import cn.edu.pku.sei.tsr.dragon.code.entity.JavaMethod;
import cn.edu.pku.sei.tsr.dragon.code.entity.JavaPackage;
import cn.edu.pku.sei.tsr.dragon.code.entity.JavaVariable;

public class CountVisitor extends JavaBaseVisitor<Object> {
	private int classNum = 0;
	private int methodNum = 0;

	@Override
	public Object visit(JavaBase javaBase) {
		return null;
	}

	@Override
	public Object visit(JavaVariable javaVariable) {
		return null;
	}

	@Override
	public Object visit(JavaMethod javaMethod) {
		++methodNum;
		return null;
	}

	@Override
	public Object visit(JavaClass javaClass) {
		++classNum;
		return null;
	}

	@Override
	public Object visit(JavaFile javaFile) {
		return null;
	}

	@Override
	public Object visit(JavaPackage javaPackage) {
		return null;
	}

	public void print() {
		System.out.println("class num: " + classNum);
		System.out.println("method num: " + methodNum);
	}
}
