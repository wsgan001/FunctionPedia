package cn.edu.pku.sei.tsr.dragon.visitor;

import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import cn.edu.pku.sei.tsr.dragon.code.entity.Comment;
import cn.edu.pku.sei.tsr.dragon.code.entity.JavaClass;
import cn.edu.pku.sei.tsr.dragon.code.entity.JavaFile;
import cn.edu.pku.sei.tsr.dragon.code.entity.JavaMethod;
import cn.edu.pku.sei.tsr.dragon.code.entity.JavaVariable;
import cn.edu.pku.sei.tsr.dragon.code.entity.Name;
import cn.edu.pku.sei.tsr.dragon.utils.JavaDocUtils;

public class JavaASTVisitor extends ASTVisitor {

	public static JavaASTVisitor instance = new JavaASTVisitor();

	private JavaFile currentFile = null;
	private JavaClass currentClass = null;
	private JavaMethod currentMethod = null;

	private JavaASTVisitor() {
	}

	public void setCurrentFile(JavaFile file) {
		currentFile = file;
	}

	@Override
	public boolean visit(TypeDeclaration node) {
		currentClass = new JavaClass(new Name(node.getName().toString()), currentFile.getPath());
		if (node.getJavadoc() != null) currentClass.setComment(new Comment(node.getJavadoc().toString()));
		currentFile.addClass(currentClass);
		return true;
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		currentMethod = new JavaMethod(new Name(node.getName().toString()), currentClass);
		if (node.getJavadoc() != null) currentMethod.setComment(new Comment(node.getJavadoc().toString()));
		currentClass.addMethod(currentMethod);
		for (Object obj : node.parameters()) {
			SingleVariableDeclaration arg = (SingleVariableDeclaration) obj;
			currentMethod.addArg(new JavaVariable(new Name(arg.getName().toString())));
		}
		return true;
	}

	@Override
	public boolean visit(VariableDeclarationFragment node) {
		JavaVariable variable = new JavaVariable(new Name(node.getName().toString()));
		if (currentMethod == null) {
			currentClass.addField(variable);
			if (node.getParent() instanceof FieldDeclaration) {
				FieldDeclaration field = (FieldDeclaration) node.getParent();
				if (field.getJavadoc() != null) variable.setComment(new Comment(field.getJavadoc().toString()));
			}
		} else currentMethod.addLocalVariable(variable);
		return true;
	}

	@Override
	public void endVisit(MethodDeclaration node) {
		Javadoc javadoc = node.getJavadoc();
		if (javadoc != null)
			for (Object obj : javadoc.tags()) {
				TagElement tag = (TagElement) obj;
				Pair<String, String> pair = JavaDocUtils.splitArgAndDescription(tag);
				if (currentMethod != null && pair != null)
					currentMethod.setCommentForArg(pair.getLeft(), new Comment(pair.getRight()));
			}
		currentMethod = null;
	}
}
