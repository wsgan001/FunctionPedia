package cn.edu.pku.sei.tsr.dragon.codeparser.ir.statement;

import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.basiccfg.BasicCFGConditionBlock;
import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.basiccfg.BasicCFGConditionBlock.Condition;
import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.basiccfg.BasicCFGConditionBlock.Condition.BooleanCondition;
import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.basiccfg.BasicCFGRegularBlock;
import cn.edu.pku.sei.tsr.dragon.codeparser.ir.IRExpression;
import cn.edu.pku.sei.tsr.dragon.codeparser.ir.IRScope;
import cn.edu.pku.sei.tsr.dragon.utils.StringUtils;

public class IRIf implements IRAbstractStatement {
	private int depth = 1;
	private IRExpression condition;
	private List<IRAbstractStatement> thenStatements = new ArrayList<>();
	private List<IRAbstractStatement> elseStatements = new ArrayList<>();

	private IRScope thenScope = thenStatements::add;
	private IRScope elseScope = elseStatements::add;

	public IRIf(IRExpression condition, int depth) {
		this.condition = condition;
		this.depth = depth;
	}

	public IRScope getThenScope() {
		return thenScope;
	}

	public IRScope getElseScope() {
		return elseScope;
	}

	@Override
	public String toString() {
		String result = String.format("if (%s):\n", condition);
		for (IRAbstractStatement statement : thenStatements)
			result += StringUtils.getNTabs(depth) + statement + "\n";
		if (!elseStatements.isEmpty()) {
			result += StringUtils.getNTabs(depth - 1) + "else:\n";
			for (IRAbstractStatement statement : elseStatements)
				result += StringUtils.getNTabs(depth) + statement + "\n";
		}

		result += StringUtils.getNTabs(depth - 1) + "end if";
		return result;
	}

	@Override
	public BasicCFGRegularBlock buildCFG(BasicCFGRegularBlock block) {
		BasicCFGRegularBlock thenBlock = block.getCFG().createRegularBlock();
		BasicCFGRegularBlock elseBlock = block.getCFG().createRegularBlock();
		BasicCFGRegularBlock endBlock = block.getCFG().createRegularBlock();
		Condition ifCondition = new BooleanCondition(condition, thenBlock, elseBlock);
		BasicCFGConditionBlock conditionBlock = block.getCFG().createConditionBlock(ifCondition);
		block.setNext(conditionBlock);
		for (IRAbstractStatement thenStatement : thenStatements) thenBlock = thenStatement.buildCFG(thenBlock);
		for (IRAbstractStatement elseStatement : elseStatements) elseBlock = elseStatement.buildCFG(elseBlock);
		thenBlock.setNext(endBlock);
		elseBlock.setNext(endBlock);
		return endBlock;
	}

}
