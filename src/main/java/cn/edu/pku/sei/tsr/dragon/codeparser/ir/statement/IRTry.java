package cn.edu.pku.sei.tsr.dragon.codeparser.ir.statement;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Joiner;

import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.basiccfg.BasicCFGRegularBlock;
import cn.edu.pku.sei.tsr.dragon.codeparser.ir.IRExpression;
import cn.edu.pku.sei.tsr.dragon.codeparser.ir.IRScope;
import cn.edu.pku.sei.tsr.dragon.utils.StringUtils;

public class IRTry implements IRAbstractStatement {
	private int depth = 1;
	private List<IRExpression> resources = new ArrayList<>();
	private List<IRAbstractStatement> tryStatements = new ArrayList<>();
	private List<IRAbstractStatement> finallyStatements = new ArrayList<>();

	public IRTry(List<IRExpression> resources, int depth) {
		this.resources = resources;
		this.depth = depth;
	}

	public IRScope getTryScope() {
		return tryStatements::add;
	}

	public IRScope getFinallyScope() {
		return finallyStatements::add;
	}

	@Override
	public String toString() {
		String result = String.format("try (%s):\n", Joiner.on(", ").join(resources));
		for (IRAbstractStatement statement : tryStatements)
			result += StringUtils.getNTabs(depth) + statement + "\n";
		if (!finallyStatements.isEmpty())
			result += StringUtils.getNTabs(depth - 1) + "finally:\n";
		for (IRAbstractStatement statement : finallyStatements)
			result += StringUtils.getNTabs(depth) + statement + "\n";
		result += StringUtils.getNTabs(depth - 1) + "end try";
		return result;
	}

	@Override
	public BasicCFGRegularBlock buildCFG(BasicCFGRegularBlock block) {
		for (IRAbstractStatement tryStatement : tryStatements) block = tryStatement.buildCFG(block);
		for (IRAbstractStatement finallyStatement : finallyStatements) block = finallyStatement.buildCFG(block);
		return block;
	}

}
