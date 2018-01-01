package cn.edu.pku.sei.tsr.dragon.code;

import java.io.File;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import cn.edu.pku.sei.tsr.dragon.code.entity.JavaFile;
import cn.edu.pku.sei.tsr.dragon.code.entity.JavaPackage;
import cn.edu.pku.sei.tsr.dragon.code.entity.Name;
import cn.edu.pku.sei.tsr.dragon.utils.FileUtils;
import cn.edu.pku.sei.tsr.dragon.visitor.JavaASTVisitor;

public class CodeParser {
	private static ASTParser parser = ASTParser.newParser(AST.JLS8);

	public static void parseDir(File dir, JavaPackage javaPackage) {
		File[] files = dir.listFiles();
		if (files == null) return;
		for (File child : files) {
			if (child.isDirectory()) {
				String parentPath = javaPackage.getPath();
				if (!parentPath.equals("")) parentPath += ".";
				JavaPackage subPackage = new JavaPackage(parentPath + child.getName());
				javaPackage.addPackage(subPackage);
				parseDir(child, subPackage);
			} else {
				parseFile(javaPackage, child);
			}
		}
	}

	public static void parseFile(JavaPackage javaPackage, File file) {
		String name = file.getName();
		if (!name.endsWith(".java")) return;
		JavaFile javaFile = new JavaFile(new Name(name.substring(0, name.length() - 5)), javaPackage.getPath().toString());
		javaPackage.addFile(javaFile);
		JavaASTVisitor.instance.setCurrentFile(javaFile);
		parser.setSource(FileUtils.getFileContent(file).toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		cu.accept(JavaASTVisitor.instance);
	}
}
