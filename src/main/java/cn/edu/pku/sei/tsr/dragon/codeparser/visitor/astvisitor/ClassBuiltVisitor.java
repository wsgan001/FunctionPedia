package cn.edu.pku.sei.tsr.dragon.codeparser.visitor.astvisitor;

import java.util.Stack;

import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import cn.edu.pku.sei.tsr.dragon.codeparser.code.entity.JavaClassInfo;
import cn.edu.pku.sei.tsr.dragon.codeparser.code.entity.JavaEnumClassInfo;
import cn.edu.pku.sei.tsr.dragon.codeparser.code.entity.JavaInterfaceInfo;
import cn.edu.pku.sei.tsr.dragon.codeparser.code.entity.JavaPackageInfo;
import cn.edu.pku.sei.tsr.dragon.codeparser.code.entity.JavaProjectInfo;
import cn.edu.pku.sei.tsr.dragon.codeparser.code.entity.JavaTypeInfo;

public class ClassBuiltVisitor extends JavaASTVisitor {
	private JavaPackageInfo currentPackage = null;
	private Stack<JavaTypeInfo> typeStack = new Stack<>();

	public ClassBuiltVisitor(JavaProjectInfo project) {
		super(project);
	}

	@Override
	public void reset() {

	}

	@Override
	public boolean visit(PackageDeclaration node) {
		String packageName = node.getName().toString();
		currentPackage = project.getPackage(packageName, true);
		return true;
	}

	@Override
	public boolean visit(TypeDeclaration node) {
		JavaTypeInfo currentType = typeStack.empty() ? null : typeStack.peek();
		if (node.isInterface()) {
			JavaInterfaceInfo javaInterface = new JavaInterfaceInfo(node.getName().toString(), currentPackage, currentType);
			project.addInterface(javaInterface);
			if (currentType != null) {
				currentType.addInnerInterface(javaInterface);
			} else {
				currentPackage.addInterface(javaInterface);
			}
			typeStack.push(javaInterface);
		} else {
			JavaClassInfo javaClassInfo = new JavaClassInfo(node.getName().toString(), currentPackage, currentType);
			project.addClass(javaClassInfo);
			if (currentType != null) {
				currentType.addInnerClass(javaClassInfo);
			} else {
				currentPackage.addClass(javaClassInfo);
			}
			typeStack.push(javaClassInfo);
		}
		return true;
	}

	@Override
	public void endVisit(TypeDeclaration node) {
		typeStack.pop();
	}

	@Override
	public boolean visit(EnumDeclaration node) {
		JavaTypeInfo currentType = typeStack.empty() ? null : typeStack.peek();

		JavaEnumClassInfo javaEnumClass = new JavaEnumClassInfo(node.getName().toString(), currentPackage, currentType);
		project.addClass(javaEnumClass);
		if (currentType != null) {
			// 内部类
			currentType.addInnerClass(javaEnumClass);
		} else {
			currentPackage.addClass(javaEnumClass);
		}
		typeStack.push(javaEnumClass);

		return true;
	}

	@Override
	public void endVisit(EnumDeclaration node) {
		typeStack.pop();
	}
}
