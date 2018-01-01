package cn.edu.pku.sei.tsr.dragon.codeparser.visitor.astvisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.CreationReference;
import org.eclipse.jdt.core.dom.Dimension;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EmptyStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionMethodReference;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.IntersectionType;
import org.eclipse.jdt.core.dom.LabeledStatement;
import org.eclipse.jdt.core.dom.LambdaExpression;
import org.eclipse.jdt.core.dom.LineComment;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MemberRef;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.MethodRef;
import org.eclipse.jdt.core.dom.MethodRefParameter;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.NameQualifiedType;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.SuperMethodReference;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TextElement;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclarationStatement;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.TypeMethodReference;
import org.eclipse.jdt.core.dom.TypeParameter;
import org.eclipse.jdt.core.dom.UnionType;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;
import org.eclipse.jdt.core.dom.WildcardType;

import cn.edu.pku.sei.tsr.dragon.codeparser.ir.IRExpression;
import cn.edu.pku.sei.tsr.dragon.codeparser.ir.IRExpression.IRAbstractVariable;
import cn.edu.pku.sei.tsr.dragon.codeparser.ir.IRExpression.IRChar;
import cn.edu.pku.sei.tsr.dragon.codeparser.ir.IRExpression.IRNumber;
import cn.edu.pku.sei.tsr.dragon.codeparser.ir.IRExpression.IRString;
import cn.edu.pku.sei.tsr.dragon.codeparser.ir.IRExpression.IRTemp;
import cn.edu.pku.sei.tsr.dragon.codeparser.ir.IRRepresentation;
import cn.edu.pku.sei.tsr.dragon.codeparser.ir.IRScope;
import cn.edu.pku.sei.tsr.dragon.codeparser.ir.statement.IRArrayAccess;
import cn.edu.pku.sei.tsr.dragon.codeparser.ir.statement.IRArrayCreation;
import cn.edu.pku.sei.tsr.dragon.codeparser.ir.statement.IRAssert;
import cn.edu.pku.sei.tsr.dragon.codeparser.ir.statement.IRAssignment;
import cn.edu.pku.sei.tsr.dragon.codeparser.ir.statement.IRBinaryOperation;
import cn.edu.pku.sei.tsr.dragon.codeparser.ir.statement.IRFieldAccess;
import cn.edu.pku.sei.tsr.dragon.codeparser.ir.statement.IRGoto;
import cn.edu.pku.sei.tsr.dragon.codeparser.ir.statement.IRIf;
import cn.edu.pku.sei.tsr.dragon.codeparser.ir.statement.IRLabel;
import cn.edu.pku.sei.tsr.dragon.codeparser.ir.statement.IRMethodInvocation;
import cn.edu.pku.sei.tsr.dragon.codeparser.ir.statement.IRPrefixOperation;
import cn.edu.pku.sei.tsr.dragon.codeparser.ir.statement.IRReference;
import cn.edu.pku.sei.tsr.dragon.codeparser.ir.statement.IRReturn;
import cn.edu.pku.sei.tsr.dragon.codeparser.ir.statement.IRStatement;
import cn.edu.pku.sei.tsr.dragon.codeparser.ir.statement.IRSwitch;
import cn.edu.pku.sei.tsr.dragon.codeparser.ir.statement.IRThrow;
import cn.edu.pku.sei.tsr.dragon.codeparser.ir.statement.IRTry;

public class IRBuiltVisitor extends ASTVisitor {
	private IRRepresentation block;
	private Stack<IRScope> scopeStack = new Stack<>();
	private Stack<Pair<IRLabel, IRLabel>> loopStack = new Stack<>();
	private Object ret;

	public IRBuiltVisitor(IRRepresentation block) {
		this.block = block;
		scopeStack.push(block);
	}

	public boolean visit(AnnotationTypeDeclaration node) {
		return false;
	}

	public boolean visit(AnnotationTypeMemberDeclaration node) {
		return true;
	}

	public boolean visit(AnonymousClassDeclaration node) {
		return false;
	}

	public boolean visit(ArrayAccess node) {
		node.getArray().accept(this);
		IRExpression array = (IRExpression) ret;
		node.getIndex().accept(this);
		IRExpression index = (IRExpression) ret;
		IRTemp target = block.createTempVariable();
		scopeStack.peek().addStatement(new IRArrayAccess(array, index, target));
		ret = target;
		return false;
	}

	public boolean visit(ArrayCreation node) {
		List<IRExpression> sizes = new ArrayList<>();
		node.dimensions().forEach(x -> {
			((Expression) x).accept(this);
			sizes.add((IRExpression) ret);
		});

		List<IRExpression> initializers = null;
		if (node.getInitializer() != null) {
			initializers = new ArrayList<>();
			initializers.addAll((List<IRExpression>)
				node.getInitializer().expressions().stream().map(arg -> {
					((Expression) arg).accept(this);
					return ret;
				}).collect(Collectors.toList())
			);
		}
		IRTemp target = block.createTempVariable();
		scopeStack.peek().addStatement(new IRArrayCreation(node.getType().getElementType().toString(), sizes, initializers, target));
		ret = target;
		return false;
	}

	public boolean visit(ArrayInitializer node) {
		return true;
	}

	public boolean visit(ArrayType node) {
		return false;
	}

	public boolean visit(AssertStatement node) {
		node.getExpression().accept(this);
		scopeStack.peek().addStatement(new IRAssert((IRExpression) ret));
		return false;
	}

	public boolean visit(Assignment node) {
		node.getRightHandSide().accept(this);
		IRExpression rhs = (IRExpression) ret;
		node.getLeftHandSide().accept(this);
		IRAbstractVariable lhs;
		if (ret instanceof IRAbstractVariable) lhs = (IRAbstractVariable) ret;
		else lhs = block.getOrCreateVariable(ret.toString());
		IRStatement irAssignment = null;
		if (node.getOperator() == Assignment.Operator.ASSIGN)
			irAssignment = new IRAssignment(rhs, lhs);
		else if (node.getOperator() == Assignment.Operator.PLUS_ASSIGN)
			irAssignment = new IRBinaryOperation(IRBinaryOperation.Operator.PLUS, lhs.clone(), rhs, lhs);
		else if (node.getOperator() == Assignment.Operator.MINUS_ASSIGN)
			irAssignment = new IRBinaryOperation(IRBinaryOperation.Operator.MINUS, lhs.clone(), rhs, lhs);
		else if (node.getOperator() == Assignment.Operator.TIMES_ASSIGN)
			irAssignment = new IRBinaryOperation(IRBinaryOperation.Operator.TIMES, lhs.clone(), rhs, lhs);
		else if (node.getOperator() == Assignment.Operator.DIVIDE_ASSIGN)
			irAssignment = new IRBinaryOperation(IRBinaryOperation.Operator.DIVIDE, lhs.clone(), rhs, lhs);
		else if (node.getOperator() == Assignment.Operator.BIT_AND_ASSIGN)
			irAssignment = new IRBinaryOperation(IRBinaryOperation.Operator.AND, lhs.clone(), rhs, lhs);
		else if (node.getOperator() == Assignment.Operator.BIT_OR_ASSIGN)
			irAssignment = new IRBinaryOperation(IRBinaryOperation.Operator.OR, lhs.clone(), rhs, lhs);
		else if (node.getOperator() == Assignment.Operator.BIT_XOR_ASSIGN)
			irAssignment = new IRBinaryOperation(IRBinaryOperation.Operator.XOR, lhs.clone(), rhs, lhs);
		else if (node.getOperator() == Assignment.Operator.REMAINDER_ASSIGN)
			irAssignment = new IRBinaryOperation(IRBinaryOperation.Operator.REMAINDER, lhs.clone(), rhs, lhs);
		else if (node.getOperator() == Assignment.Operator.LEFT_SHIFT_ASSIGN)
			irAssignment = new IRBinaryOperation(IRBinaryOperation.Operator.LEFT_SHIFT, lhs.clone(), rhs, lhs);
		else if (node.getOperator() == Assignment.Operator.RIGHT_SHIFT_SIGNED_ASSIGN)
			irAssignment = new IRBinaryOperation(IRBinaryOperation.Operator.RIGHT_SHIFT_SIGNED, lhs.clone(), rhs, lhs);
		else if (node.getOperator() == Assignment.Operator.RIGHT_SHIFT_UNSIGNED_ASSIGN)
			irAssignment = new IRBinaryOperation(IRBinaryOperation.Operator.RIGHT_SHIFT_UNSIGNED, lhs.clone(), rhs, lhs);
		scopeStack.peek().addStatement(irAssignment);
		ret = lhs;
		return false;
	}

	public boolean visit(Block node) {
		return true;
	}

	public boolean visit(BlockComment node) {
		return false;
	}

	public boolean visit(BooleanLiteral node) {
		ret = node.booleanValue() ? IRExpression.TRUE : IRExpression.FALSE;
		return false;
	}

	public boolean visit(BreakStatement node) {
		scopeStack.peek().addStatement(new IRGoto(loopStack.peek().getRight()));
		ret = null;
		return false;
	}

	public boolean visit(CastExpression node) {
		return true;
	}

	public boolean visit(CatchClause node) {
		return true;
	}

	public boolean visit(CharacterLiteral node) {
		ret = new IRChar(node.charValue());
		return false;
	}

	public boolean visit(ClassInstanceCreation node) {
		List<IRExpression> args = new ArrayList<>();
		node.arguments().forEach(arg -> {
			((Expression) arg).accept(this);
			args.add((IRExpression) ret);
		});
		IRTemp target = block.createTempVariable();
		scopeStack.peek().addStatement(new IRMethodInvocation(null, node.getType().toString() + ".<init>", args, target));
		ret = target;
		return false;
	}

	public boolean visit(CompilationUnit node) {
		return true;
	}

	public boolean visit(ConditionalExpression node) {
		node.getExpression().accept(this);
		IRExpression condition = (IRExpression) ret;
		node.getThenExpression().accept(this);
		IRExpression thenExp = (IRExpression) ret;
		node.getElseExpression().accept(this);
		IRExpression elseExp = (IRExpression) ret;
		IRTemp target = block.createTempVariable();
		IRIf irIf = new IRIf(condition, scopeStack.size());
		irIf.getThenScope().addStatement(new IRAssignment(thenExp, target));
		irIf.getElseScope().addStatement(new IRAssignment(elseExp, target));
		scopeStack.peek().addStatement(irIf);
		ret = target;
		return false;
	}

	public boolean visit(ConstructorInvocation node) {
		List<IRExpression> args = new ArrayList<>();
		node.arguments().forEach(arg -> {
			((Expression) arg).accept(this);
			args.add((IRExpression) ret);
		});
		IRTemp target = block.createTempVariable();
		scopeStack.peek().addStatement(new IRMethodInvocation(null, "this", args, target));
		ret = target;
		return false;
	}

	public boolean visit(ContinueStatement node) {
		assert node.getLabel() == null;
		scopeStack.peek().addStatement(new IRGoto(loopStack.peek().getLeft()));
		return false;
	}

	public boolean visit(CreationReference node) {
		IRTemp target = block.createTempVariable();
		IRExpression operand = block.getVariableOrExtern(node.getType().toString());
		scopeStack.peek().addStatement(new IRReference(operand, "new", target));
		return false;
	}

	public boolean visit(Dimension node) {
		return true;
	}

	public boolean visit(DoStatement node) {
		IRLabel loopLabel = block.getLabel();
		IRLabel breakLabel = block.getLabel();

		scopeStack.peek().addStatement(loopLabel);
		loopStack.push(Pair.of(loopLabel, breakLabel));

		node.getBody().accept(this);
		node.getExpression().accept(this);
		IRExpression condition = (IRExpression) ret;
		IRIf irIf = new IRIf(condition, scopeStack.size());
		scopeStack.peek().addStatement(irIf);
		irIf.getThenScope().addStatement(new IRGoto(loopLabel));

		loopStack.pop();
		scopeStack.peek().addStatement(breakLabel);
		ret = null;
		return false;
	}

	public boolean visit(EmptyStatement node) {
		return false;
	}

	public boolean visit(EnhancedForStatement node) {
		IRLabel loopLabel = block.getLabel();
		IRLabel breakLabel = block.getLabel();

		// init
		node.getExpression().accept(this);
		IRExpression expression = (IRExpression) ret;
		IRTemp ite = block.createTempVariable();
		scopeStack.peek().addStatement(new IRMethodInvocation(expression, "iterator", new ArrayList<>(), ite));
		scopeStack.peek().addStatement(loopLabel);

		// condition
		IRTemp condition = block.createTempVariable();
		scopeStack.peek().addStatement(new IRMethodInvocation(ite, "hasNext", new ArrayList<>(), condition));
		IRIf irIf = new IRIf(condition, scopeStack.size());
		scopeStack.peek().addStatement(irIf);

		// body
		loopStack.push(Pair.of(loopLabel, breakLabel));
		scopeStack.push(irIf.getThenScope());
		scopeStack.peek().addStatement(new IRMethodInvocation(ite, "next", new ArrayList<>(), block.getOrCreateVariable(node.getParameter().getName().toString())));
		node.getBody().accept(this);

		scopeStack.peek().addStatement(new IRGoto(loopLabel));
		scopeStack.pop();
		loopStack.pop();

		scopeStack.peek().addStatement(breakLabel);
		ret = null;
		return false;
	}

	public boolean visit(EnumConstantDeclaration node) {
		throw new UnsupportedOperationException();
	}

	public boolean visit(EnumDeclaration node) {
		throw new UnsupportedOperationException();
	}

	public boolean visit(ExpressionMethodReference node) {
		node.getExpression().accept(this);
		IRTemp target = block.createTempVariable();
		scopeStack.peek().addStatement(new IRReference((IRExpression) ret, node.getName().toString(), target));
		return false;
	}

	public boolean visit(ExpressionStatement node) {
		return true;
	}

	public boolean visit(FieldAccess node) {
		node.getExpression().accept(this);
		IRExpression receiver = (IRExpression) ret;
		IRTemp target = block.createTempVariable();
		scopeStack.peek().addStatement(new IRFieldAccess(receiver, node.getName().toString(), target));
		ret = target;
		return false;
	}

	public boolean visit(FieldDeclaration node) {
		throw new UnsupportedOperationException();
	}

	public boolean visit(ForStatement node) {
		IRLabel loopLabel = block.getLabel();
		IRLabel continueLabel = block.getLabel();
		IRLabel breakLabel = block.getLabel();

		// init
		node.initializers().forEach(i -> ((ASTNode) i).accept(this));
		scopeStack.peek().addStatement(loopLabel);

		IRExpression condition;
		// condition
		if (node.getExpression() == null) {
			condition = IRExpression.TRUE;
		} else {
			node.getExpression().accept(this);
			condition = (IRExpression) ret;
		}
		IRIf irIf = new IRIf(condition, scopeStack.size());
		scopeStack.peek().addStatement(irIf);

		// body
		loopStack.push(Pair.of(continueLabel, breakLabel));
		scopeStack.push(irIf.getThenScope());
		node.getBody().accept(this);

		// update
		scopeStack.peek().addStatement(continueLabel);
		node.updaters().forEach(i -> ((ASTNode) i).accept(this));
		scopeStack.peek().addStatement(new IRGoto(loopLabel));
		scopeStack.pop();
		loopStack.pop();

		scopeStack.peek().addStatement(breakLabel);
		ret = null;
		return false;
	}

	public boolean visit(IfStatement node) {
		node.getExpression().accept(this);
		IRExpression condition = (IRExpression) ret;
		IRIf irIf = new IRIf(condition, scopeStack.size());
		scopeStack.peek().addStatement(irIf);
		scopeStack.push(irIf.getThenScope());
		node.getThenStatement().accept(this);
		scopeStack.pop();
		if (node.getElseStatement() != null) {
			scopeStack.push(irIf.getElseScope());
			node.getElseStatement().accept(this);
			scopeStack.pop();
		}
		ret = null;
		return false;
	}

	public boolean visit(ImportDeclaration node) {
		return false;
	}

	public boolean visit(InfixExpression node) {
		node.getLeftOperand().accept(this);
		IRExpression left = (IRExpression) ret;
		node.getRightOperand().accept(this);
		IRExpression right = (IRExpression) ret;
		IRTemp target = block.createTempVariable();
		scopeStack.peek().addStatement(new IRBinaryOperation(IRBinaryOperation.Operator.toOperator(node.getOperator().toString()), left, right, target));
		ret = target;
		return false;
	}

	public boolean visit(Initializer node) {
		return true;
	}

	public boolean visit(InstanceofExpression node) {
		node.getLeftOperand().accept(this);
		IRExpression left = (IRExpression) ret;
		IRExpression right = block.getVariableOrExtern(node.getRightOperand().toString());
		IRTemp target = block.createTempVariable();
		scopeStack.peek().addStatement(new IRBinaryOperation(IRBinaryOperation.Operator.INSTANCE_OF, left, right, target));
		ret = target;
		return false;
	}

	public boolean visit(IntersectionType node) {
		return false;
	}

	public boolean visit(LabeledStatement node) {
		throw new UnsupportedOperationException();
	}

	public boolean visit(LambdaExpression node) {
		throw new UnsupportedOperationException();
	}

	public boolean visit(LineComment node) {
		return false;
	}

	public boolean visit(MarkerAnnotation node) {
		return false;
	}

	public boolean visit(MemberRef node) {
		return false;
	}

	public boolean visit(MemberValuePair node) {
		return false;
	}

	public boolean visit(MethodRef node) {
		return false;
	}

	public boolean visit(MethodRefParameter node) {
		return false;
	}

	public boolean visit(MethodDeclaration node) {
		return true;
	}

	public boolean visit(MethodInvocation node) {
		Expression receiver = node.getExpression();
		IRExpression receiverExp = IRExpression.THIS;
		if (receiver != null) {
			receiver.accept(this);
			receiverExp = (IRExpression) ret;
		}
		List<IRExpression> args = new ArrayList<>();
		node.arguments().forEach(arg -> {
			((Expression) arg).accept(this);
			args.add((IRExpression) ret);
		});
		IRTemp target = block.createTempVariable();
		scopeStack.peek().addStatement(new IRMethodInvocation(receiverExp, node.getName().toString(), args, target));
		ret = target;
		return false;
	}

	public boolean visit(Modifier node) {
		return false;
	}

	public boolean visit(NameQualifiedType node) {
		return false;
	}

	public boolean visit(NormalAnnotation node) {
		return false;
	}

	public boolean visit(NullLiteral node) {
		ret = IRExpression.NULL;
		return false;
	}

	public boolean visit(NumberLiteral node) {
		ret = new IRNumber(node.getToken());
		return true;
	}

	public boolean visit(PackageDeclaration node) {
		return false;
	}

	public boolean visit(ParameterizedType node) {
		return false;
	}

	public boolean visit(ParenthesizedExpression node) {
		return true;
	}

	public boolean visit(PostfixExpression node) {
		node.getOperand().accept(this);
		IRExpression operand = (IRExpression) ret;
		IRTemp target = block.createTempVariable();
//		IRAbstractVariable ope = (IRAbstractVariable) operand;
		IRAbstractVariable ope = block.getOrCreateVariable(operand.toString());
		scopeStack.peek().addStatement(new IRAssignment(ope.clone(), target));
		IRBinaryOperation.Operator operator;
		if (node.getOperator() == PostfixExpression.Operator.INCREMENT) operator = IRBinaryOperation.Operator.PLUS;
		else if (node.getOperator() == PostfixExpression.Operator.DECREMENT)
			operator = IRBinaryOperation.Operator.MINUS;
		else throw new RuntimeException("Unknown Postfix operator!");
		scopeStack.peek().addStatement(new IRBinaryOperation(operator, ope.clone(), new IRNumber("1"), ope.clone()));
		//scopeStack.peek().addStatement(new IRPostfixOperation(node.getOperator(), operand, target));
		ret = target;
		return false;
	}

	public boolean visit(PrefixExpression node) {
		node.getOperand().accept(this);
		IRExpression operand = (IRExpression) ret;
//		IRAbstractVariable ope = (IRAbstractVariable) operand;
		IRAbstractVariable ope = block.getOrCreateVariable(operand.toString());
		IRTemp target = block.createTempVariable();
		IRBinaryOperation.Operator operator;
		if (node.getOperator() == PrefixExpression.Operator.INCREMENT) operator = IRBinaryOperation.Operator.PLUS;
		else if (node.getOperator() == PrefixExpression.Operator.DECREMENT) operator = IRBinaryOperation.Operator.MINUS;
		else {
			 scopeStack.peek().addStatement(new IRPrefixOperation(node.getOperator(), operand, target));
			ret = target;
			return false;
		}
		scopeStack.peek().addStatement(new IRBinaryOperation(operator, ope.clone(), new IRNumber("1"), ope.clone()));
		scopeStack.peek().addStatement(new IRAssignment(ope.clone(), target));
		ret = target;
		return false;
	}

	public boolean visit(PrimitiveType node) {
		return false;
	}

	public boolean visit(QualifiedName node) {
		node.getQualifier().accept(this);
		IRExpression receiver = (IRExpression) ret;
		IRTemp target = block.createTempVariable();
		scopeStack.peek().addStatement(new IRFieldAccess(receiver, node.getName().toString(), target));
		ret = target;
		return false;
	}

	public boolean visit(QualifiedType node) {
		return false;
	}

	public boolean visit(ReturnStatement node) {
		IRExpression result = null;
		if (node.getExpression() != null) {
			node.getExpression().accept(this);
			result = (IRExpression) ret;
		}
		scopeStack.peek().addStatement(new IRReturn(result));
		return false;
	}

	public boolean visit(SimpleName node) {
		ret = block.getVariableOrExtern(node.getFullyQualifiedName());
		return false;
	}

	public boolean visit(SimpleType node) {
		return false;
	}

	public boolean visit(SingleMemberAnnotation node) {
		return false;
	}

	public boolean visit(SingleVariableDeclaration node) {
		throw new UnsupportedOperationException();
	}

	public boolean visit(StringLiteral node) {
		ret = new IRString(node.getLiteralValue());
		return false;
	}

	public boolean visit(SuperConstructorInvocation node) {
		List<IRExpression> args = new ArrayList<>();
		node.arguments().forEach(arg -> {
			((Expression) arg).accept(this);
			args.add((IRExpression) ret);
		});
		IRTemp target = block.createTempVariable();
		scopeStack.peek().addStatement(new IRMethodInvocation(null, "super", args, target));
		ret = target;
		return false;
	}

	public boolean visit(SuperFieldAccess node) {
		IRTemp target = block.createTempVariable();
		scopeStack.peek().addStatement(new IRFieldAccess(IRExpression.SUPER, node.getName().toString(), target));
		ret = target;
		return true;
	}

	public boolean visit(SuperMethodInvocation node) {
		List<IRExpression> args = new ArrayList<>();
		node.arguments().forEach(arg -> {
			((Expression) arg).accept(this);
			args.add((IRExpression) ret);
		});
		IRTemp target = block.createTempVariable();
		scopeStack.peek().addStatement(new IRMethodInvocation(IRExpression.SUPER, node.getName().toString(), args, target));
		ret = target;
		return true;
	}

	public boolean visit(SuperMethodReference node) {
		IRTemp target = block.createTempVariable();
		scopeStack.peek().addStatement(new IRReference(IRExpression.SUPER, node.getName().toString(), target));
		return false;
	}

	public boolean visit(SwitchCase node) {
		return true;
	}

	public boolean visit(SwitchStatement node) {
		IRLabel breakLabel = block.getLabel();

		node.getExpression().accept(this);
		IRExpression expression = (IRExpression) ret;
		IRSwitch irSwitch = new IRSwitch(expression, scopeStack.size());
		node.statements().stream().filter(x -> x instanceof SwitchCase).forEach(x -> {
			SwitchCase s = (SwitchCase) x;
			if (s.isDefault()) return;
			s.getExpression().accept(this);
			IRExpression caseExp = (IRExpression) ret;
			irSwitch.addCase(caseExp);
		});
		scopeStack.peek().addStatement(irSwitch);

		loopStack.push(Pair.of(null, breakLabel));
		scopeStack.push(null); // 加入一个null，不需要特判第一个case
		node.statements().forEach(x -> {
			Statement s = (Statement) x;
			if (s instanceof SwitchCase) {
				scopeStack.pop();
				scopeStack.push(((SwitchCase) s).isDefault() ? irSwitch.getDefaultScope() : irSwitch.getRegularScope());
			} else {
				s.accept(this);
			}
		});
		scopeStack.pop();
		loopStack.pop();
		scopeStack.peek().addStatement(breakLabel);
		ret = null;
		return false;
	}

	public boolean visit(SynchronizedStatement node) {
		node.getBody().accept(this);
		return false;
	}

	public boolean visit(TagElement node) {
		return false;
	}

	public boolean visit(TextElement node) {
		return false;
	}

	public boolean visit(ThisExpression node) {
		ret = IRExpression.THIS;
		return true;
	}

	public boolean visit(ThrowStatement node) {
		node.getExpression().accept(this);
		scopeStack.peek().addStatement(new IRThrow((IRExpression) ret));
		return false;
	}

	public boolean visit(TryStatement node) {
		List<IRExpression> resources = new ArrayList<>();
		node.resources().forEach(x -> {
			VariableDeclarationExpression s = (VariableDeclarationExpression) x;
			s.accept(this);
			resources.addAll((List<IRExpression>) ret);
		});
		IRTry irTry = new IRTry(resources, scopeStack.size());
		scopeStack.peek().addStatement(irTry);
		scopeStack.push(irTry.getTryScope());
		node.getBody().accept(this);
		scopeStack.pop();
		if (node.getFinally() != null) {
			scopeStack.push(irTry.getFinallyScope());
			node.getFinally().accept(this);
			scopeStack.pop();
		}
		return false;
	}

	public boolean visit(TypeDeclaration node) {
		return true;
	}

	public boolean visit(TypeDeclarationStatement node) {
		return true;
	}

	public boolean visit(TypeLiteral node) {
		return false;
	}

	public boolean visit(TypeMethodReference node) {
		IRTemp target = block.createTempVariable();
		scopeStack.peek().addStatement(new IRReference(block.getVariableOrExtern(node.getType().toString()), node.getName().toString(), target));
		return false;
	}

	public boolean visit(TypeParameter node) {
		return false;
	}

	public boolean visit(UnionType node) {
		return false;
	}

	public boolean visit(VariableDeclarationExpression node) {
		List<IRExpression> vars = new ArrayList<>();
		node.fragments().forEach(x -> {
			((VariableDeclarationFragment) x).accept(this);
			vars.add((IRExpression) ret);
		});
		ret = vars;
		return false;
	}

	public boolean visit(VariableDeclarationStatement node) {
		List<IRExpression> vars = new ArrayList<>();
		node.fragments().forEach(x -> {
			((VariableDeclarationFragment) x).accept(this);
			vars.add((IRExpression) ret);
		});
		ret = null;
		return false;
	}

	public boolean visit(VariableDeclarationFragment node) {
		if (node.getInitializer() != null) {
			node.getInitializer().accept(this);
			IRExpression source = (IRExpression) ret;
			scopeStack.peek().addStatement(new IRAssignment(source, block.getOrCreateVariable(node.getName().toString())));
		}
		ret = null;
		return false;
	}

	public boolean visit(WhileStatement node) {
		IRLabel loopLabel = block.getLabel();
		IRLabel breakLabel = block.getLabel();

		// condition
		scopeStack.peek().addStatement(loopLabel);
		node.getExpression().accept(this);
		IRExpression condition = (IRExpression) ret;
		IRIf irIf = new IRIf(condition, scopeStack.size());
		scopeStack.peek().addStatement(irIf);

		// body
		loopStack.push(Pair.of(loopLabel, breakLabel));
		scopeStack.push(irIf.getThenScope());
		node.getBody().accept(this);
		scopeStack.peek().addStatement(new IRGoto(loopLabel));
		scopeStack.pop();
		loopStack.pop();
		scopeStack.peek().addStatement(breakLabel);
		ret = null;
		return false;
	}

	public boolean visit(WildcardType node) {
		return false;
	}

}
