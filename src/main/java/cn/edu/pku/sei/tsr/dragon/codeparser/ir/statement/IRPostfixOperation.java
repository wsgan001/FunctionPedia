package cn.edu.pku.sei.tsr.dragon.codeparser.ir.statement;

import java.util.stream.Stream;

import org.eclipse.jdt.core.dom.PostfixExpression;

import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.basiccfg.BasicCFGRegularBlock;
import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.mining.MiningNode;
import cn.edu.pku.sei.tsr.dragon.codeparser.ir.IRExpression;

public class IRPostfixOperation extends IRStatement {
	private PostfixExpression.Operator operator;
	private IRExpression operand;

	public IRPostfixOperation(PostfixExpression.Operator operator, IRExpression operand, IRExpression.IRAbstractVariable target) {
		addUse(operand);
		addDef(target);

		this.operator = operator;
		this.operand = operand;
		this.target = target;
	}

	public PostfixExpression.Operator getOperator() {
		return operator;
	}

	@Override
	public String toString() {
		return String.format("%s = %s%s", target, operand, operator);
	}

	@Override
	public BasicCFGRegularBlock buildCFG(BasicCFGRegularBlock block) {
		block.addNode(this);
		return block;
	}

	@Override
	public Stream<IRExpression> getUses(ExpressionFilter builder) {
		return builder.add(operand).build();
	}

	@Override
	public MiningNode toMiningNode() {
		if (operator == PostfixExpression.Operator.INCREMENT) return MiningNode.POST_INCREMENT;
		if (operator == PostfixExpression.Operator.DECREMENT) return MiningNode.POST_DECREMENT;
		throw new RuntimeException("Known operator!");
	}

}
