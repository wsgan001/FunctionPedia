package cn.edu.pku.sei.tsr.dragon.codeparser.ir.statement;

import java.util.stream.Stream;

import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.basiccfg.BasicCFGRegularBlock;
import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.mining.MiningNode;
import cn.edu.pku.sei.tsr.dragon.codeparser.ir.IRExpression;

public class IRAssert extends IRStatement {
	private IRExpression condition;

	public IRAssert(IRExpression condition) {
		addUse(condition);

		this.condition = condition;
	}

	@Override
	public String toString() {
		return String.format("assert %s", condition);
	}

	@Override
	public BasicCFGRegularBlock buildCFG(BasicCFGRegularBlock block) {
		// TODO: 16-1-8
		block.addNode(this);
		return block;
	}

	@Override
	public Stream<IRExpression> getUses(ExpressionFilter builder) {
		return builder.add(condition).build();
	}

	@Override
	public MiningNode toMiningNode() {
		return MiningNode.ASSERT;
	}

}
