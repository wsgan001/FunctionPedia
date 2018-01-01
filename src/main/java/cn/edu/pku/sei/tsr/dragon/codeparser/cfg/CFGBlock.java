package cn.edu.pku.sei.tsr.dragon.codeparser.cfg;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import cn.edu.pku.sei.tsr.dragon.codeparser.graph.Node;
import cn.edu.pku.sei.tsr.dragon.codeparser.ir.statement.IRStatement;

public interface CFGBlock extends Node {
	@Override
	ImmutableSet<? extends CFGBlock> getNexts();

	@Override
	ImmutableSet<? extends CFGBlock> getPrevs();

	ImmutableList<IRStatement> getStatements();
}
