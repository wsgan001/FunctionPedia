package cn.edu.pku.sei.tsr.dragon.codeparser.cfg.mining;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Ordering;

import cn.edu.pku.sei.tsr.dragon.codeparser.adt.LispList;
import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.CFG;
import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.ddg.DDG;
import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.ddg.DDGBlock;
import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.plaincfg.PlainCFG;
import cn.edu.pku.sei.tsr.dragon.codeparser.graph.EdgeImpl;
import de.parsemis.graph.Edge;
import de.parsemis.graph.Graph;
import de.parsemis.graph.ListGraph;
import de.parsemis.graph.Node;
import de.parsemis.miner.general.Fragment;

public class MiningGraph {

	public static ListGraph<MiningNode, Integer> convertDDGToMiningGraph(DDG ddg) {
		Map<DDGBlock, Node<MiningNode, Integer>> blockMap = new HashMap<>();

		ListGraph<MiningNode, Integer> graph = new ListGraph<>();

		for (DDGBlock block : ddg.getBlocks()) {
			MiningNode node = block.getPlainCFGBlock().toMiningNode();
			blockMap.put(block, graph.addNode(node));
		}

		// 将DDG的边对应至MiningGraph的边
		blockMap.forEach((block, node) ->
			block.getNexts().stream().map(blockMap::get).filter(x -> node != x).forEach(x -> graph.addEdge(node, x, 0, Edge.OUTGOING))
		);

		// 添加常数、extern结点
//		blockMap.forEach((block, node) -> {
//			if (block.getStatement() == null) return;
//			block.getStatement().getUses(new IRAbstractStatement.PreDefinedFilter())
//					.filter(x -> !(x instanceof IRExpression.IRAbstractVariable))
//					.forEach(use -> graph.addEdge(graph.addNode(use.toMiningNode()), node, 0, Edge.OUTGOING));
//		});

		return graph;
	}

	public static <N, E> void printMiningGraph(Graph<N, E> g) {
		Iterator<Edge<N, E>> ite = g.edgeIterator();
		while (ite.hasNext()) {
			Edge<N, E> e = ite.next();
			switch (e.getDirection()) {
				case Edge.INCOMING:
					System.out.println(e.getNodeB().getLabel() + " -> " + e.getNodeA().getLabel());
					break;
				case Edge.UNDIRECTED:
					System.out.println(e.getNodeA().getLabel() + " -- " + e.getNodeB().getLabel());
					break;
				case Edge.OUTGOING:
					System.out.println(e.getNodeA().getLabel() + " -> " + e.getNodeB().getLabel());
					break;
			}
		}
		System.out.println();
	}

	public static <N, E> List<Graph<N, E>> resultFilter(Collection<Fragment<N, E>> result) {
		List<Graph<N, E>> graphs = new ArrayList<>();
		List<Set<cn.edu.pku.sei.tsr.dragon.codeparser.graph.Edge<N>>> edgeSets = new ArrayList<>();
		result.stream().map(Fragment::toGraph).sorted(Ordering.natural().reverse().onResultOf(Graph::getEdgeCount)).forEach(graph -> {
			Iterable<de.parsemis.graph.Edge<N, E>> ite = graph::edgeIterator;
			Set<cn.edu.pku.sei.tsr.dragon.codeparser.graph.Edge<N>> edges =
				StreamSupport.stream(ite.spliterator(), false).map(EdgeImpl<N>::new).collect(Collectors.toSet());
			boolean add = true;
			for (Set<cn.edu.pku.sei.tsr.dragon.codeparser.graph.Edge<N>> edgeSet : edgeSets) {
				if (edgeSet.containsAll(edges)) {
					add = false;
					break;
				}
			}
			if (add) {
				graphs.add(graph);
				edgeSets.add(edges);
			}
		});
		return graphs;
	}

	public static CFG createCFGFromMiningGraph(List<DDG> ddgs, Graph<MiningNode, Integer> result) {
		for (DDG ddg : ddgs) {
			if (ddg == ddgs.get(0)) continue;
			Set<DDGBlock> subBlocks = MiningGraph.findSubDDG(ddg, result);
			if (subBlocks == null) continue;
			CFG cfg = PlainCFG.createCFG(subBlocks);
			return cfg;
		}
		return null;
	}

	/**
	 * 判断挖掘出的图是否是给定DDG的一个子图
	 * @param ddg 给定的DDG
	 * @param sub 挖掘出的图
	 * @return 如果是子图，则返回sub在ddg中对应结点的集合，反之，返回null
	 */
	public static Set<DDGBlock> findSubDDG(DDG ddg, Graph<MiningNode, Integer> sub) {
		BiMap<DDGBlock, Node<MiningNode, Integer>> nodeMap = HashBiMap.create();
		LispList<Node<MiningNode, Integer>> remain = LispList.copyOf(sub.nodeIterator());
		if(findSubDDG(ddg, nodeMap, remain.car(), remain.cdr())) return nodeMap.keySet();
		return null;
	}

	private static boolean findSubDDG(DDG ddg, BiMap<DDGBlock, Node<MiningNode, Integer>> nodeMap, Node<MiningNode, Integer> current, LispList<Node<MiningNode, Integer>> remain) {
		for (DDGBlock block : ddg.getBlocks()) {
			if (nodeMap.containsKey(block)) continue;
			if (!equals(block, current.getLabel())) continue;

			boolean outDiff = false;
			Iterator<Edge<MiningNode, Integer>> outs = current.outgoingEdgeIterator();
			while (outs.hasNext()) {
				Edge<MiningNode, Integer> outEdge = outs.next();
				Node<MiningNode, Integer> other = outEdge.getOtherNode(current);
				if (!nodeMap.containsValue(other)) continue;
				DDGBlock mappedDDGBlock = nodeMap.inverse().get(other);
				if (!block.getNexts().contains(mappedDDGBlock)) {
					outDiff = true;
					break;
				}
			}
			if (outDiff) continue;

			boolean inDiff = false;
			Iterator<Edge<MiningNode, Integer>> ins = current.incommingEdgeIterator();
			while (ins.hasNext()) {
				Edge<MiningNode, Integer> inEdge = ins.next();
				Node<MiningNode, Integer> other = inEdge.getOtherNode(current);
				if (!nodeMap.containsValue(other)) continue;
				DDGBlock mappedDDGBlock = nodeMap.inverse().get(other);
				if (!mappedDDGBlock.getNexts().contains(block)) {
					inDiff = true;
					break;
				}
			}
			if (inDiff) continue;

			nodeMap.put(block, current);
			if (remain.isEmpty()) return true;
			if (findSubDDG(ddg, nodeMap, remain.car(), remain.cdr())) return true;
			nodeMap.remove(block);
		}

		return false;
	}

	private static boolean equals(DDGBlock block, MiningNode node) {
		return block.getPlainCFGBlock().toMiningNode().equals(node);
	}
}
