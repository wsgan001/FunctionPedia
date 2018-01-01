package cn.edu.pku.sei.tsr.dragon.codeparser.ir.statement;

import java.util.stream.Stream;

import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.basiccfg.BasicCFGRegularBlock;
import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.mining.MiningNode;
import cn.edu.pku.sei.tsr.dragon.codeparser.ir.IRExpression;

public class IRThrow extends IRStatement {
	private IRExpression exception;

	public IRThrow(IRExpression exception) {
		addUse(exception);

		this.exception = exception;
	}

	@Override
	public String toString() {
		return String.format("throw %s", exception);
	}

	@Override
	public BasicCFGRegularBlock buildCFG(BasicCFGRegularBlock block) {
		// TODO: 16-1-8
		block.addNode(this);
		return block;
	}

	@Override
	public Stream<IRExpression> getUses(ExpressionFilter builder) {
		return Stream.empty();
	}

	@Override
	public MiningNode toMiningNode() {
		return MiningNode.THROW;
	}

}
