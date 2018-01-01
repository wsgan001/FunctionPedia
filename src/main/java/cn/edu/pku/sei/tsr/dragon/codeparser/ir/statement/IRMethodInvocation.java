package cn.edu.pku.sei.tsr.dragon.codeparser.ir.statement;

import java.util.List;
import java.util.stream.Stream;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;

import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.basiccfg.BasicCFGRegularBlock;
import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.mining.MiningNode;
import cn.edu.pku.sei.tsr.dragon.codeparser.ir.IRExpression;

public class IRMethodInvocation extends IRStatement {
	private IRExpression receiver;
	private String methodName;
	private IRExpression[] args;

	public IRMethodInvocation(IRExpression receiver, String methodName, List<IRExpression> args, IRExpression.IRAbstractVariable target) {
		addUse(receiver);
		args.forEach(this::addUse);
		addDef(target);

		this.receiver = receiver;
		this.methodName = methodName;
		this.args = Iterables.toArray(args, IRExpression.class);
		this.target = target;
	}

	public String getMethodName() {
		return methodName;
	}

	@Override
	public String toString() {
		if (receiver != null)
			return String.format("%s = %s.%s(%s)", target, receiver, methodName, Joiner.on(", ").join(args));
		return String.format("%s = %s(%s)", target, methodName, Joiner.on(", ").join(args));
	}

	@Override
	public BasicCFGRegularBlock buildCFG(BasicCFGRegularBlock block) {
		block.addNode(this);
		return block;
	}

	@Override
	public Stream<IRExpression> getUses(ExpressionFilter builder) {
		return builder.add(receiver).addAll(args).build();
	}

	@Override
	public MiningNode toMiningNode() {
		return new MiningNode(MiningNode.OPType.METHOD_INVOCATION, methodName);
	}
}
