package cn.edu.pku.sei.tsr.dragon.codeparser.ir.statement;

import java.util.stream.Stream;

import com.google.common.base.Joiner;

import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.basiccfg.BasicCFGRegularBlock;
import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.basiccfg.CFGVariableImpl;
import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.mining.MiningNode;
import cn.edu.pku.sei.tsr.dragon.codeparser.ir.IRExpression;

public class IRPhi extends IRStatement {
	private IRExpression.IRAbstractVariable[] source;
	private CFGVariableImpl var;

	public IRPhi(int size, CFGVariableImpl target) {
		source = new IRExpression.IRAbstractVariable[size];
		this.target = new IRExpression.IRVariable(target.getVariableUnit());
		var = target;

		addDef(this.target);
	}

	public CFGVariableImpl getVar() {
		return var;
	}

	public void replaceVar(int index, int version) {
		source[index] = new IRExpression.IRVariable(var.getVariableUnit());
		source[index].setVersion(version);
		addUse(source[index]);
	}

	@Override
	public String toString() {
		return String.format("%s = phi(%s)", target, Joiner.on(", ").join(source));
	}

	@Override
	public BasicCFGRegularBlock buildCFG(BasicCFGRegularBlock block) {
		block.addNode(this);
		return block;
	}

	@Override
	public Stream<IRExpression> getUses(ExpressionFilter builder) {
		return builder.addAll(source).build();
	}

	@Override
	public MiningNode toMiningNode() {
		return MiningNode.PHI;
	}

}
