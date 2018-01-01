package cn.edu.pku.sei.tsr.dragon.codeparser.ir.statement;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.basiccfg.BasicCFGConditionBlock;
import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.basiccfg.BasicCFGConditionBlock.Condition.CaseCondition;
import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.basiccfg.BasicCFGRegularBlock;
import cn.edu.pku.sei.tsr.dragon.codeparser.ir.IRExpression;
import cn.edu.pku.sei.tsr.dragon.codeparser.ir.IRScope;
import cn.edu.pku.sei.tsr.dragon.utils.StringUtils;

public class IRSwitch implements IRAbstractStatement {
	private int depth = 1;
	private IRExpression expression;
	private List<Pair<IRExpression, List<IRAbstractStatement>>> cases = new ArrayList<>();
	private List<IRAbstractStatement> defaultCase = new ArrayList<>();

	private int scopeIndex = 0;

	public IRSwitch(IRExpression expression, int depth) {
		this.expression = expression;
		this.depth = depth;
	}

	public void addCase(IRExpression caseExp) {
		cases.add(Pair.of(caseExp, new ArrayList<>()));
	}

	public IRScope getRegularScope() {
		IRScope scope = cases.get(scopeIndex).getValue()::add;
		++scopeIndex;
		return scope;
	}

	public IRScope getDefaultScope() {
		return defaultCase::add;
	}

	@Override
	public String toString() {
		String result = String.format("switch (%s):\n", expression);
		for (Pair<IRExpression, List<IRAbstractStatement>> casePair : cases) {
			IRExpression caseExpression = casePair.getKey();
			result += StringUtils.getNTabs(depth - 1) + String.format("case (%s):\n", caseExpression);
			for (IRAbstractStatement statement : casePair.getValue())
				result += StringUtils.getNTabs(depth) + statement + "\n";
		}

		if (defaultCase != null) {
			result += StringUtils.getNTabs(depth - 1) + "default:\n";
			for (IRAbstractStatement statement : defaultCase)
				result += StringUtils.getNTabs(depth) + statement + "\n";
		}
		result += StringUtils.getNTabs(depth - 1) + "end switch";
		return result;
	}

	@Override
	public BasicCFGRegularBlock buildCFG(BasicCFGRegularBlock block) {
		// TODO: 16-1-8 处理break和fall through
		BasicCFGRegularBlock endBlock = block.getCFG().createRegularBlock();
		CaseCondition caseCondition = new CaseCondition(expression);
		cases.forEach(casePair -> {
			BasicCFGRegularBlock caseBlock = block.getCFG().createRegularBlock();
			caseCondition.addNext(casePair.getKey(), caseBlock);
			for (IRAbstractStatement statement : casePair.getValue()) caseBlock = statement.buildCFG(caseBlock);
			caseBlock.setNext(endBlock);
		});
		BasicCFGRegularBlock defaultBlock = block.getCFG().createRegularBlock();

		caseCondition.setDefault(defaultBlock);

		for (IRAbstractStatement statement : defaultCase) defaultBlock = statement.buildCFG(defaultBlock);
		defaultBlock.setNext(endBlock);

		BasicCFGConditionBlock conditionBlock = block.getCFG().createConditionBlock(caseCondition);
		block.setNext(conditionBlock);
		return endBlock;
	}

}
