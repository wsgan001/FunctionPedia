package cn.edu.pku.sei.tsr.dragon.codeparser.ir;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;

import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.basiccfg.BasicCFGRegularBlock;
import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.plaincfg.PlainCFG;
import cn.edu.pku.sei.tsr.dragon.codeparser.ir.IRExpression.IRExtern;
import cn.edu.pku.sei.tsr.dragon.codeparser.ir.IRExpression.IRTemp;
import cn.edu.pku.sei.tsr.dragon.codeparser.ir.IRExpression.IRVariable;
import cn.edu.pku.sei.tsr.dragon.codeparser.ir.statement.IRAbstractStatement;
import cn.edu.pku.sei.tsr.dragon.codeparser.ir.statement.IRLabel;
import cn.edu.pku.sei.tsr.dragon.codeparser.visitor.astvisitor.IRBuiltVisitor;

public class IRRepresentation implements IRScope {
	private static ASTParser parser = ASTParser.newParser(AST.JLS8);

	private int tempNum = 0, labelNum = 0;
	private List<IRAbstractStatement> statements = new ArrayList<>();
	private Map<String, VariableUnit> variables = new HashMap<>();

	public IRRepresentation(String methodBody) {
		Hashtable options = JavaCore.getOptions();
		options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_8);
		parser.setCompilerOptions(options);
		parser.setSource(methodBody.toCharArray());
		parser.setKind(ASTParser.K_STATEMENTS);
		final ASTNode cu = parser.createAST(null);
		cu.accept(new IRBuiltVisitor(this));
	}

	public IRRepresentation(PlainCFG cfg) {

	}

	@Override
	public void addStatement(IRAbstractStatement statement) {
		statements.add(statement);
	}

	public IRExpression getVariableOrExtern(String name) {
		VariableUnit v = variables.get(name);
		if (v != null) return new IRVariable(v);
		return new IRExtern(name);
	}

	public Collection<VariableUnit> getVariables() {
		return variables.values();
	}

	public IRVariable getOrCreateVariable(String name) {
		VariableUnit v;
		v = variables.get(name);
		if (v == null) {
			v = new VariableUnit(name);
			variables.put(name, v);
		}
		return new IRVariable(v);
	}

	public IRTemp createTempVariable() {
		IRTemp result = new IRTemp(tempNum++);
		variables.put(result.toString(), result.getVariable());
		return result;
	}

	public IRLabel getLabel() {
		return new IRLabel(labelNum++);
	}

	public void output() {
		statements.forEach(System.out::println);

		variables.forEach((k, v) -> System.out.println(v));
	}

	public BasicCFGRegularBlock buildCFG(BasicCFGRegularBlock block) {
		for (IRAbstractStatement statement : statements) {
			block = statement.buildCFG(block);
		}
		return block;
	}

}
