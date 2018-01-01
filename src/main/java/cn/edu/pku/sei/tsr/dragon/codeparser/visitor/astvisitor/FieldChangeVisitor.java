package cn.edu.pku.sei.tsr.dragon.codeparser.visitor.astvisitor;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.ThisExpression;

import cn.edu.pku.sei.tsr.dragon.codeparser.code.entity.JavaMethodInfo;

public class FieldChangeVisitor extends ASTVisitor {
	private ExpressionResolvedVisitor expressionResolvedVisitor = new ExpressionResolvedVisitor();
	private JavaMethodInfo method;

	public FieldChangeVisitor(JavaMethodInfo method) {
		this.method = method;
	}

	@Override
	public boolean visit(AnonymousClassDeclaration node) {
		return false;
	}

	@Override
	public boolean visit(Assignment node) {
		Expression lhs = node.getLeftHandSide();
		lhs.accept(expressionResolvedVisitor);
		return true;
	}

	private class ExpressionResolvedVisitor extends ASTVisitor {
		@Override
		public boolean visit(ArrayAccess node) {
			return false;
		}

		@Override
		public boolean visit(ArrayCreation node) {
			return false;
		}

		@Override
		public boolean visit(ArrayInitializer node) {
			return false;
		}

		@Override
		public boolean visit(CastExpression node) {
			node.getExpression().accept(this);
			return false;
		}

		@Override
		public boolean visit(ClassInstanceCreation node) {
			return false;
		}

		@Override
		public boolean visit(ConditionalExpression node) {
			node.getThenExpression().accept(this);
			node.getElseExpression().accept(this);
			return false;
		}

		@Override
		public boolean visit(FieldAccess node) {
			// only process this.XXX and field.XXX
			Expression reciever = node.getExpression();
			SimpleName field = node.getName();
			if (reciever instanceof ThisExpression) {
				method.changeVariable(field.getFullyQualifiedName());
			} else {
				reciever.accept(this);
			}
			return false;
		}

		@Override
		public boolean visit(InfixExpression node) {
			return false;
		}

		@Override
		public boolean visit(MethodInvocation node) {
			return false;
		}

		@Override
		public boolean visit(QualifiedName node) {
			// only process field.XXX
			Name reciever = node.getQualifier();
			reciever.accept(this);
			return false;
		}

		@Override
		public boolean visit(SimpleName node) {
			method.changeVariable(node.getFullyQualifiedName());
			return false;
		}

		@Override
		public boolean visit(SuperFieldAccess node) {
			// only process field.XXX
			SimpleName field = node.getName();
			method.changeVariable(field.getFullyQualifiedName());
			return false;
		}

		@Override
		public boolean visit(SuperMethodInvocation node) {
			return false;
		}

	}
}
