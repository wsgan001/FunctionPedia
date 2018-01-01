package cn.edu.pku.sei.tsr.dragon.codeparser.ir.statement;

import java.util.stream.Stream;

import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.basiccfg.BasicCFGRegularBlock;
import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.mining.MiningNode;
import cn.edu.pku.sei.tsr.dragon.codeparser.ir.IRExpression;

public class IRFieldAccess extends IRStatement {
	private IRExpression receiver;
	private String field;

	public IRFieldAccess(IRExpression receiver, String field, IRExpression.IRAbstractVariable target) {
		addUse(receiver);
		addDef(target);

		this.receiver = receiver;
		this.field = field;
		this.target = target;
	}

	@Override
	public String toString() {
		return String.format("%s = %s.%s", target, receiver, field);
	}

	@Override
	public BasicCFGRegularBlock buildCFG(BasicCFGRegularBlock block) {
		block.addNode(this);
		return block;
	}

	@Override
	public Stream<IRExpression> getUses(ExpressionFilter builder) {
		return builder.add(receiver).build();
	}

	@Override
	public MiningNode toMiningNode() {
		return new MiningNode(MiningNode.OPType.FIELD_ACCESS, field);
	}
}
