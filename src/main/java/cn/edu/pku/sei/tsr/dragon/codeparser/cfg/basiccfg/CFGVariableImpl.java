package cn.edu.pku.sei.tsr.dragon.codeparser.cfg.basiccfg;

import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;

import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.CFGVariable;
import cn.edu.pku.sei.tsr.dragon.codeparser.ir.VariableUnit;
import cn.edu.pku.sei.tsr.dragon.codeparser.ir.statement.IRStatement;
import cn.edu.pku.sei.tsr.dragon.codeparser.utils.Predicates;

public class CFGVariableImpl implements CFGVariable {
	private VariableUnit variableUnit;

	public CFGVariableImpl(VariableUnit variableUnit) {
		this.variableUnit = variableUnit;
	}

	public VariableUnit getVariableUnit() {
		return variableUnit;
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
		return String.format("[CFGVariableImpl]%s", variableUnit.getName());
	}
}
