package cn.edu.pku.sei.tsr.dragon.codeparser.cfg.ddg;

import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;

import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.CFGVariable;
import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.basiccfg.AbstractBasicCFGBlock;
import cn.edu.pku.sei.tsr.dragon.codeparser.ir.IRExpression;
import cn.edu.pku.sei.tsr.dragon.codeparser.ir.VariableUnit;
import cn.edu.pku.sei.tsr.dragon.codeparser.ir.statement.IRStatement;
import cn.edu.pku.sei.tsr.dragon.codeparser.utils.Predicates;

public class DDGVariable implements CFGVariable {
	private VariableUnit variableUnit;
	private int version;

	public DDGVariable(IRExpression.IRAbstractVariable variable) {
		this.variableUnit = variable.getVariable();
		this.version = variable.getVersion();
	}

	@Override
	public int hashCode() {
		return variableUnit.hashCode() ^ version;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof DDGVariable)) return false;
		DDGVariable other = (DDGVariable) obj;
		return variableUnit == other.variableUnit && version == other.version;
	}

	@Override
	public String getName() {
		return variableUnit.getName();
	}

	public ImmutableSet<AbstractBasicCFGBlock> getDefBlocks() {
		return ImmutableSet.copyOf(variableUnit.getDefBoxes().map(IRStatement::getBelongBlock).filter(Predicates.notNull()).collect(Collectors.toSet()));
	}

	public ImmutableSet<AbstractBasicCFGBlock> getUseBlocks() {
		return ImmutableSet.copyOf(variableUnit.getUseBoxes().map(IRStatement::getBelongBlock).filter(Predicates.notNull()).collect(Collectors.toSet()));
	}

	@Override
	public ImmutableSet<IRStatement> getDefStatements() {
		return ImmutableSet.copyOf(variableUnit.getDefBoxes().collect(Collectors.toSet()));
	}

	@Override
	public ImmutableSet<IRStatement> getUseStatements() {
		return ImmutableSet.copyOf(variableUnit.getUseBoxes().collect(Collectors.toSet()));
	}

	@Override
	public String toString() {
		return String.format("[DDGVariable]%s@%s", variableUnit.getName(), version);
	}
}
