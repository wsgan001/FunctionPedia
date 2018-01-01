package cn.edu.pku.sei.tsr.dragon.codeparser.graph;

import com.google.common.collect.ImmutableSet;

public interface Graph {
	ImmutableSet<? extends Node> getNodes();
}
