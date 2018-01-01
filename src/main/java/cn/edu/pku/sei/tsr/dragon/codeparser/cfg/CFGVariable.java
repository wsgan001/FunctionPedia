package cn.edu.pku.sei.tsr.dragon.codeparser.cfg;

import com.google.common.collect.ImmutableSet;

import cn.edu.pku.sei.tsr.dragon.codeparser.ir.statement.IRStatement;

public interface CFGVariable {
	String getName();

	ImmutableSet<? extends CFGBlock> getDefBlocks();

	ImmutableSet<? extends CFGBlock> getUseBlocks();

	ImmutableSet<IRStatement> getDefStatements();

	ImmutableSet<IRStatement> getUseStatements();

}
