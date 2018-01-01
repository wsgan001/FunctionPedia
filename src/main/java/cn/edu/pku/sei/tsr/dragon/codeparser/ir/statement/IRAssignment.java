package cn.edu.pku.sei.tsr.dragon.codeparser.ir.statement;

import java.util.stream.Stream;

import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.basiccfg.BasicCFGRegularBlock;
import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.mining.MiningNode;
import cn.edu.pku.sei.tsr.dragon.codeparser.ir.IRExpression;

public class IRAssignment extends IRStatement {
	private IRExpression source;

	public IRAssignment(IRExpression source, IRExpression.IRAbstractVariable target) {
		addUse(source);
		addDef(target);

		this.source = source;
		this.target = target;
	}

	@Override
	public String toString() {
		return String.format("%s = %s", target, source);
	}

	@Override
	public BasicCFGRegularBlock buildCFG(BasicCFGRegularBlock block) {
		block.addNode(this);
		return block;
	}

	@Override
	public Stream<IRExpression> getUses(ExpressionFilter builder) {
		return builder.add(source).build();
	}

	@Override
	public MiningNode toMiningNode() {
		return MiningNode.ASSIGNMENT;
	}

}
