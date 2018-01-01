package cn.edu.pku.sei.tsr.dragon.codeparser.code;

import java.io.File;
import java.util.Hashtable;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import cn.edu.pku.sei.tsr.dragon.codeparser.code.entity.JavaProjectInfo;
import cn.edu.pku.sei.tsr.dragon.codeparser.visitor.astvisitor.ClassBuiltVisitor;
import cn.edu.pku.sei.tsr.dragon.codeparser.visitor.astvisitor.JavaASTVisitor;
import cn.edu.pku.sei.tsr.dragon.codeparser.visitor.astvisitor.SymbolBuiltVisitor;
import cn.edu.pku.sei.tsr.dragon.codeparser.visitor.projectvisitor.MethodAnalyzeVisitor;
import cn.edu.pku.sei.tsr.dragon.utils.FileUtils;

public class CodeParser {
	private static ASTParser parser = ASTParser.newParser(AST.JLS8);

	public static JavaProjectInfo parse(String projectName, String projectPath) {
		JavaProjectInfo project = new JavaProjectInfo(projectName);
		parseDir(project, new File(projectPath), new ClassBuiltVisitor(project));
		parseDir(project, new File(projectPath), new SymbolBuiltVisitor(project));
		project.accept(new MethodAnalyzeVisitor(), null);
		return project;
	}

	private static void parseDir(JavaProjectInfo project, File dir, JavaASTVisitor visitor) {
		File[] files = dir.listFiles();
		if (files == null) return;
		for (File file : files) {
			if (file.isDirectory()) {
				parseDir(project, file, visitor);
			} else {
				parseFile(project, file, visitor);
			}
		}
	}

	private static void parseFile(JavaProjectInfo project, File file, JavaASTVisitor visitor) {
		String name = file.getName();
		if (!name.endsWith(".java")) return;
		// set compiler version
		Hashtable options = JavaCore.getOptions();
		options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_8);
		parser.setCompilerOptions(options);
		parser.setSource(FileUtils.getFileContent(file).toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		visitor.reset();
		cu.accept(visitor);
	}

	public static void main(String[] args) {
//		parse("lucene", "D:\\Dragon Project\\subjects\\lucene-5.2.1\\core\\src\\java").printAllClassesAndInterfaces();
		JavaProjectInfo project = parse("lucene", "D:\\Codes\\lucene-5.2.1\\core\\src\\java");
//		JavaProject project = parse("gson", "/home/woooking/java/projects/gson/gson/src/main/java");
//		project.accept(new OutputVisitor(), null);
//		List<JavaMethod> methods = new ArrayList<>();
//		project.accept(new MethodCollectVisitor(), methods);
//		List<JavaVariable> fields = new ArrayList<>();
//		project.accept(new FieldCollectVisitor(), fields);
//		parse("jena", "/home/woooking/java/projects/apache-jena-3.0.0/javadoc-core").printAllClassesAndInterfaces();
//		JavaProject project = parse("codeparser", "/home/woooking/java/CodeParser/src");
//		System.out.println(project);
//		String jdkPath = "/usr/lib/jvm/java-7-openjdk-amd64/jre/lib/";
//		String classPath = "bin" +
//			":" + "/home/woooking/java/projects/lucene-5.2.1/core/src/java" +
//			":" + jdkPath + "rt.jar" +
//			":" + jdkPath + "jce.jar" +
//			":" + jdkPath + "jsse.jar";
//
//		PackManager.v().getPack("wjtp").add(new Transform("wjtp.showclasses", new SceneTransformer() {
//			@Override
//			protected void internalTransform(String s, Map map) {
//				for (SootClass sootClass : Scene.v().getApplicationClasses()) {
//					System.out.println(sootClass);
//				}
//			}
//		}));
//
//		soot.Main.main(new String[]{
//			"-app",
//			"-allow-phantom-refs",
//			"-soot-class-path", classPath,
//			"-process-dir", "/home/woooking/java/projects/lucene-5.2.1/core/src/java"
//		});
	}

}

