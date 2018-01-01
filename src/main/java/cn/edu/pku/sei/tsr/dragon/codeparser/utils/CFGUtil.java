package cn.edu.pku.sei.tsr.dragon.codeparser.utils;

import com.github.woooking.graphlib.KotNode;
import com.github.woooking.graphlib.graph.DirectedGraph;
import com.github.woooking.graphlib.graphviz.BasicDirectedEdgeFormatter;
import com.github.woooking.graphlib.graphviz.BasicGraphFormatter;
import com.github.woooking.graphlib.graphviz.BasicNodeFormatter;
import com.github.woooking.graphlib.graphviz.GraphFormatter;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.CFG;
import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.CFGBlock;
import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.plaincfg.AbstractPlainCFGBlock;
import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.plaincfg.PlainCFGConditionBlock;

public class CFGUtil {
	public static void printCFG(CFG cfg) {
		cfg.getBlocks().forEach(b ->
			b.getNexts().forEach(n -> System.out.println(b + " -> " + n))
		);

		System.out.println("-----");

		cfg.getBlocks().forEach(b -> {
			System.out.println(b);
			b.getStatements().forEach(System.out::println);
			System.out.println();
		});
	}

	public static void saveCFG(CFG cfg, String fileName) {
		BasicNodeFormatter nodeFormatter = new BasicNodeFormatter<String, String>();
		BasicDirectedEdgeFormatter edgeFormatter = new BasicDirectedEdgeFormatter(nodeFormatter, true);
		BasicGraphFormatter graphFormatter = new BasicGraphFormatter(nodeFormatter, edgeFormatter);
		DirectedGraph<CFGBlock, String> g = new DirectedGraph<>();

		BiMap<CFGBlock, KotNode<CFGBlock, String>> map = HashBiMap.create();

		for (CFGBlock block : cfg.getBlocks()) {
			KotNode<CFGBlock, String> node = g.addNode(block);
			map.put(block, node);
		}

		for (CFGBlock block : cfg.getBlocks()) {
			for (CFGBlock next : block.getNexts()) {
				KotNode<CFGBlock, String> nodeA = map.get(block);
				KotNode<CFGBlock, String> nodeB = map.get(next);
				String l = "";
				if (block instanceof PlainCFGConditionBlock) {
					l = ((PlainCFGConditionBlock) block).getCondition((AbstractPlainCFGBlock) next).toString();
				}
				g.addEdge(nodeA, nodeB, l);
			}
		}
		graphFormatter.save(g, GraphFormatter.Type.PNG, fileName);
	}
}
