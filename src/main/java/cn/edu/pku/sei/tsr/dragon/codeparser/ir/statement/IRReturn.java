package cn.edu.pku.sei.tsr.dragon.codeparser.ir.statement;

import java.util.stream.Stream;

import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.basiccfg.BasicCFGRegularBlock;
import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.mining.MiningNode;
import cn.edu.pku.sei.tsr.dragon.codeparser.ir.IRExpression;

public class IRReturn extends IRStatement {
	private IRExpression result;

	public IRReturn(IRExpression result) {
		addUse(result);

		this.result = result;
	}

	@Override
	public String toString() {
		if (result == null) return "return";
		return String.format("return %s", result);
	}

	@Override
	public BasicCFGRegularBlock buildCFG(BasicCFGRegularBlock block) {
		block.setNext(block.getCFG().getExit());
		return block.getCFG().createRegularBlock();
	}

	@Override
	public Stream<IRExpression> getUses(ExpressionFilter builder) {
		return builder.add(result).build();
	}

	@Override
	public MiningNode toMiningNode() {
		return MiningNode.RETURN;
	}

}
