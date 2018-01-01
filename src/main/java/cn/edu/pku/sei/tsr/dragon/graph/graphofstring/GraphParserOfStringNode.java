package cn.edu.pku.sei.tsr.dragon.graph.graphofstring;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import cn.edu.pku.sei.tsr.dragon.graph.GraphUtils;
import cn.edu.pku.sei.tsr.dragon.graph.entity.EdgeInfo;
import cn.edu.pku.sei.tsr.dragon.graph.entity.GraphInfo;
import cn.edu.pku.sei.tsr.dragon.graph.entity.NodeInfo;
import cn.edu.pku.sei.tsr.dragon.graph.entity.NodeLabel;
import cn.edu.pku.sei.tsr.dragon.graph.entity.TreeInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.entity.NounPhraseStructureInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.entity.PrepPhraseStructureInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.entity.VerbalPhraseStructureInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.entity.dictionary.AdjectiveInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.entity.dictionary.ConjunctionInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.entity.dictionary.NounInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.entity.dictionary.VerbInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.entity.dictionary.WordInfo;
import cn.edu.pku.sei.tsr.dragon.test.GraphTest;
import de.parsemis.graph.Edge;
import de.parsemis.graph.Graph;
import de.parsemis.graph.Node;

public class GraphParserOfStringNode {
	public static final Logger logger = Logger.getLogger(GraphParserOfStringNode.class);

	public static GraphInfo parseGraph(Graph<String, Integer> graph) {
		if (graph == null)
			return null;
		GraphInfo graphInfo = new GraphInfo();

		HashMap<Integer, NodeInfo> nodeIndex = new HashMap<>();// 原图中的index，新的node

		Iterator<Node<String, Integer>> nodeIterator = graph.nodeIterator();
		while (nodeIterator.hasNext()) {
			Node<String, Integer> curNode = nodeIterator.next();
			String nodeValue = curNode.getLabel();
			NodeInfo nodeInfo = GraphUtils.parseStringToNodeInfo(nodeValue);
			if (nodeInfo != null) {
				graphInfo.addNode(nodeInfo);
				nodeIndex.put(curNode.getIndex(), nodeInfo);// 按照原图中的index添加到索引，便于建边时查找
				if (curNode.getInDegree() == 0) // 入度为0，树根
					graphInfo.setRoot(nodeInfo);
			}
		}

		Iterator<Edge<String, Integer>> edgeIterator = graph.edgeIterator();
		while (edgeIterator.hasNext()) {
			Edge<String, Integer> curEdge = edgeIterator.next();
			int nodeAIndex = curEdge.getNodeA().getIndex();
			int nodeBIndex = curEdge.getNodeB().getIndex();
			int direction = curEdge.getDirection();

			if (direction == Edge.INCOMING) // 都存成出边
				graphInfo.addEdge(nodeIndex.get(nodeBIndex), nodeIndex.get(nodeAIndex),
						EdgeInfo.OUTGOING);
			else
				graphInfo.addEdge(nodeIndex.get(nodeAIndex), nodeIndex.get(nodeBIndex), direction);
		}

		return graphInfo;
	}

	public static GraphInfo parseGraphAndValidate(Graph<String, Integer> graph) {
		if (graph == null)
			return null;
		GraphInfo graphInfo = new GraphInfo();
		boolean isVPRoot = false;
		boolean containsVerbLeaf = false;
		boolean containsNounLeaf = false;

		HashMap<Integer, NodeInfo> nodeIndex = new HashMap<>();// 原图中的index，新的node

		Iterator<Node<String, Integer>> nodeIterator = graph.nodeIterator();
		while (nodeIterator.hasNext()) {
			Node<String, Integer> curNode = nodeIterator.next();
			String nodeValue = curNode.getLabel();
			NodeInfo nodeInfo = GraphUtils.parseStringToNodeInfo(nodeValue);
			if (nodeInfo != null) {
				graphInfo.addNode(nodeInfo);
				nodeIndex.put(curNode.getIndex(), nodeInfo);// 按照原图中的index添加到索引，便于建边时查找

				if (!isVPRoot && curNode.getInDegree() == 0) {
					// 还没找到根的时候发现一个入度为0，树根
					graphInfo.setRoot(nodeInfo);
					if (nodeInfo.getLabel() == NodeLabel.VP)
						isVPRoot = true;
				}
				if (nodeInfo.getLabel() == NodeLabel.verb)
					containsVerbLeaf = true;
				if (nodeInfo.getLabel() == NodeLabel.noun)
					containsNounLeaf = true;
			}
		}

		if (!isVPRoot || !containsNounLeaf || !containsVerbLeaf) {
			// System.err.println("Invalid graph!");
			// System.err.println(GraphBuilderOfStringNode.getGraphString(graph));
			return null;
		}

		Iterator<Edge<String, Integer>> edgeIterator = graph.edgeIterator();
		while (edgeIterator.hasNext()) {
			Edge<String, Integer> curEdge = edgeIterator.next();
			int nodeAIndex = curEdge.getNodeA().getIndex();
			int nodeBIndex = curEdge.getNodeB().getIndex();
			int direction = curEdge.getDirection();

			if (direction == Edge.INCOMING) // 都存成出边
				graphInfo.addEdge(nodeIndex.get(nodeBIndex), nodeIndex.get(nodeAIndex),
						EdgeInfo.OUTGOING);
			else
				graphInfo.addEdge(nodeIndex.get(nodeAIndex), nodeIndex.get(nodeBIndex), direction);
		}

		return graphInfo;
	}

	public static VerbalPhraseStructureInfo parseGraphToPhraseStructure(GraphInfo graph) {
		if (graph != null) {
			TreeInfo tree = parseGraphToTree(graph);
			if (tree != null)
				return parseTreeToVPStructure(tree);
		}
		return null;
	}

	public static VerbalPhraseStructureInfo parseTreeToVPStructure(TreeInfo tree) {
		VerbalPhraseStructureInfo vpStructure = new VerbalPhraseStructureInfo();
		tree.getChildren().forEach(x -> {
			NodeLabel label = x.getLabel();
			switch (label) {
			case verb:
				vpStructure.setVerb(new VerbInfo(x.getValue()));
				break;
			case conj:
				vpStructure.setParticle(new ConjunctionInfo(x.getValue()));
				break;
			case NP:
				vpStructure.setSubNP(parseTreeToNPStructure(x));
				break;
			case PP:
				vpStructure.addSubPP(parseTreeToPPStructure(x));
				break;
			default:
				break;
			}
		});
		return vpStructure;
	}

	public static NounPhraseStructureInfo parseTreeToNPStructure(TreeInfo tree) {
		NounPhraseStructureInfo npStructure = new NounPhraseStructureInfo();
		List<WordInfo> wordChain = new ArrayList<>();
		wordChain.addAll(tree.getChildren().stream().filter(x -> x.getLabel() == NodeLabel.other)
				.map(x -> new WordInfo(x.getValue())).collect(Collectors.toList()));
		wordChain.addAll(tree.getChildren().stream().filter(x -> x.getLabel() == NodeLabel.conj)
				.map(x -> new ConjunctionInfo(x.getValue())).collect(Collectors.toList()));
		wordChain.addAll(tree.getChildren().stream().filter(x -> x.getLabel() == NodeLabel.adj)
				.map(x -> new AdjectiveInfo(x.getValue())).collect(Collectors.toList()));
		wordChain.addAll(tree.getChildren().stream().filter(x -> x.getLabel() == NodeLabel.noun)
				.map(x -> new NounInfo(x.getValue())).collect(Collectors.toList()));
		npStructure.setWordChain(wordChain);
		TreeInfo ppTree = tree.getChildren().stream().filter(x -> x.getLabel() == NodeLabel.PP)
				.findFirst().orElse(null);
		if (ppTree != null)
			npStructure.setSubPP(parseTreeToPPStructure(ppTree));
		return npStructure;
	}

	public static PrepPhraseStructureInfo parseTreeToPPStructure(TreeInfo tree) {
		PrepPhraseStructureInfo ppStructure = new PrepPhraseStructureInfo();
		tree.getChildren().stream().filter(x -> x.getLabel() == NodeLabel.conj).findFirst()
				.ifPresent(x -> ppStructure.setConjunction(new ConjunctionInfo(x.getValue())));
		tree.getChildren().stream().filter(x -> x.getLabel() == NodeLabel.NP).findFirst()
				.ifPresent(x -> ppStructure.setSubNP(parseTreeToNPStructure(x)));
		tree.getChildren().stream().filter(x -> x.getLabel() == NodeLabel.PP).findFirst()
				.ifPresent(x -> ppStructure.setSubPP(parseTreeToPPStructure(x)));
		return ppStructure;
	}

	// 将图转化为树
	public static TreeInfo parseGraphToTree(GraphInfo graph) {
		List<NodeInfo> roots = graph.getRoots();
		if (roots.size() != 1)
			return null;
		NodeInfo root = roots.get(0);
		TreeInfo tree = generateTreeFromNodeInGraph(root, graph);
		return tree;
	}

	// 从图中一个结点生成图中以其为根的树
	public static TreeInfo generateTreeFromNodeInGraph(NodeInfo node, GraphInfo graph) {
		if (!graph.contains(node))
			return null;
		System.out.println(graph.toStringWithOrderNumber());

		TreeInfo tree = new TreeInfo(node);
		List<NodeInfo> successors = graph.getSuccessors(node);
		for (NodeInfo successor : successors) {
			System.out.println(successor.toStringWithOrderNumber());
			TreeInfo child = generateTreeFromNodeInGraph(successor, graph);
			tree.addChild(child);
		}
		return tree;
	}

	public static void main(String[] args) {
		GraphInfo g1 = GraphTest.graph();
		System.out.println(g1);
		VerbalPhraseStructureInfo vp = parseTreeToVPStructure(parseGraphToTree(g1));
		System.out.println(vp);
		// Graph<String, Integer> g2 = GraphBuilderOfStringNode.convertToParsemisGraph(g1);
		// System.out.println("G1:" + g1);
		// System.out.println("G2:" + GraphBuilderOfStringNode.getGraphString(g2));
		// GraphInfo g3 = parseGraphAndValidate(g2);
		// System.out.println("G3:" + g3);
	}

}
