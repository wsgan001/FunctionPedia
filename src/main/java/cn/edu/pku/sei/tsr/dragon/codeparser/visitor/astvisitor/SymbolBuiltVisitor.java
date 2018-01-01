package cn.edu.pku.sei.tsr.dragon.codeparser.visitor.astvisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Stream;

import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import cn.edu.pku.sei.tsr.dragon.codeparser.code.entity.JavaBaseInfo;
import cn.edu.pku.sei.tsr.dragon.codeparser.code.entity.JavaClassInfo;
import cn.edu.pku.sei.tsr.dragon.codeparser.code.entity.JavaInterfaceInfo;
import cn.edu.pku.sei.tsr.dragon.codeparser.code.entity.JavaMethodInfo;
import cn.edu.pku.sei.tsr.dragon.codeparser.code.entity.JavaPackageInfo;
import cn.edu.pku.sei.tsr.dragon.codeparser.code.entity.JavaProjectInfo;
import cn.edu.pku.sei.tsr.dragon.codeparser.code.entity.JavaTypeInfo;
import cn.edu.pku.sei.tsr.dragon.codeparser.code.entity.JavaVariableInfo;

public class SymbolBuiltVisitor extends JavaASTVisitor {
	private JavaPackageInfo currentPackage;
	private List<String> imports = new ArrayList<>();
	private Stack<JavaTypeInfo> typeStack = new Stack<>();
	private JavaMethodInfo currentMethod;

	public SymbolBuiltVisitor(JavaProjectInfo project) {
		super(project);
	}

	public void reset() {
		imports.clear();
	}

	@Override
	public boolean visit(PackageDeclaration node) {
		String packageName = node.getName().toString();
		currentPackage = project.getPackage(packageName, false);
		return true;
	}

	@Override
	public boolean visit(ImportDeclaration node) {
		String importDec = node.toString();
		imports.add(importDec.substring(7, importDec.length() - 2));
		return true;
	}

	@Override
	public boolean visit(TypeDeclaration node) {
		JavaTypeInfo currentType = typeStack.empty() ? null : typeStack.peek();

		String nodeName = node.getName().toString();

		if (node.isInterface()) {
			JavaInterfaceInfo javaInterface;
			if (currentType != null)
				javaInterface = currentType.getInnerInterface(nodeName);
			else
				javaInterface = currentPackage.getInterface(nodeName);

			currentType = javaInterface;

		} else {
			JavaClassInfo javaClassInfo;
			if (currentType != null)
				javaClassInfo = currentType.getInnerClass(nodeName);
			else
				javaClassInfo = currentPackage.getClass(nodeName);

			// setup super class
			JavaClassInfo superClass = project.getClass(currentPackage, currentType, imports, node.getSuperclassType());
			javaClassInfo.setSuperClass(superClass);

			currentType = javaClassInfo;
		}

		for (Object x : node.superInterfaceTypes()) {
			Type interfaceType = (Type) x;
			currentType.addSuperInterface((JavaInterfaceInfo) project.getType(currentPackage, imports, interfaceType, JavaProjectInfo.TypeKind.INTERFACE));
		}

		typeStack.push(currentType);
		getModifiers(node,currentType);
		return true;
	}

	@Override
	public void endVisit(TypeDeclaration node) {
		typeStack.pop();
	}

	@Override
	public boolean visit(EnumDeclaration node) {
		JavaTypeInfo currentClass = null;
		if (!typeStack.empty()) currentClass = typeStack.peek();

		String nodeName = node.getName().toString();
		JavaClassInfo javaClassInfo;
		if (currentClass != null)
			javaClassInfo = currentClass.getInnerClass(nodeName);
		else
			javaClassInfo = currentPackage.getClass(nodeName);
		typeStack.push(javaClassInfo);
		return true;
	}

	@Override
	public void endVisit(EnumDeclaration node) {
		typeStack.pop();
	}

	@Override
	public boolean visit(AnonymousClassDeclaration node) {
		return false;
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		JavaTypeInfo returnType = project.getType(currentPackage, imports, node.getReturnType2(), JavaProjectInfo.TypeKind.TYPE);
		JavaTypeInfo belongType = typeStack.peek();
		JavaMethodInfo method = new JavaMethodInfo(node.getName().toString(), belongType, returnType, node.getBody());
		belongType.addMethod(method);
		currentMethod = method;
		getModifiers(node,method);
		return true;
	}

	@Override
	public void endVisit(MethodDeclaration node) {
		currentMethod = null;
	}

	@Override
	public boolean visit(VariableDeclarationExpression node) {
		if (currentMethod == null) return true; //TODO process static block

		JavaTypeInfo variableType = project.getType(currentPackage, imports, node.getType(), JavaProjectInfo.TypeKind.TYPE);

		Stream<JavaVariableInfo> variables = node.fragments().stream().map(x -> {
			VariableDeclarationFragment v = (VariableDeclarationFragment) x;
			return new JavaVariableInfo(v.getName().toString(), variableType);
		});

		variables.forEach(currentMethod::addLocalVariable);
		return true;
	}

	@Override
	public boolean visit(VariableDeclarationStatement node) {
		if (currentMethod == null) return true; //TODO process static block

		JavaTypeInfo variableType = project.getType(currentPackage, imports, node.getType(), JavaProjectInfo.TypeKind.TYPE);

		Stream<JavaVariableInfo> variables = node.fragments().stream().map(x -> {
			VariableDeclarationFragment v = (VariableDeclarationFragment) x;
			return new JavaVariableInfo(v.getName().toString(), variableType);
		});

//		variables.forEach(methodStack.peek()::addLocalVariable);
		variables.forEach(currentMethod::addLocalVariable);
		return true;
	}

	@Override
	public boolean visit(SingleVariableDeclaration node) {
		if (currentMethod == null) return true; //TODO process static block

		JavaTypeInfo variableType = project.getType(currentPackage, imports, node.getType(), JavaProjectInfo.TypeKind.TYPE);
		JavaVariableInfo variable = new JavaVariableInfo(node.getName().toString(), variableType);
//		methodStack.peek().addArg(variable);
		currentMethod.addArg(variable);
		return true;
	}

	@Override
	public boolean visit(FieldDeclaration node) {
		JavaTypeInfo variableType = project.getType(currentPackage, imports, node.getType(), JavaProjectInfo.TypeKind.TYPE);

		JavaTypeInfo currentClass = typeStack.peek();

		Stream<JavaVariableInfo> variables = node.fragments().stream().map(x -> {
			VariableDeclarationFragment v = (VariableDeclarationFragment) x;
			JavaVariableInfo ret = new JavaVariableInfo(v.getName().toString(), variableType);
			getModifiers(node,ret);
			return ret;
		});

		variables.forEach(currentClass::addField);

		return true;
	}
	
	public void getModifiers(BodyDeclaration node,JavaBaseInfo element){
		//System.out.println(element.getName() + " "+ node.getModifiers() + " "+Modifier.isPublic(node.getModifiers()));
		element.setModifier(node.getModifiers());
	}
	
}
