package cn.edu.pku.sei.tsr.dragon.test;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import de.parsemis.Miner;
import de.parsemis.algorithms.gSpan.Algorithm;
import de.parsemis.graph.Edge;
import de.parsemis.graph.Graph;
import de.parsemis.graph.ListGraph;
import de.parsemis.graph.Node;
import de.parsemis.miner.environment.Settings;
import de.parsemis.miner.general.Fragment;
import de.parsemis.miner.general.IntFrequency;
import de.parsemis.parsers.LabelParser;
import de.parsemis.strategy.BFSStrategy;

class MyLabelParser implements LabelParser<Integer> {
	@Override
	public Integer parse(String s) throws ParseException {
		return Integer.parseInt(s);
	}

	@Override
	public String serialize(Integer integer) {
		return integer.toString();
	}
}

public class ParsemisGraphTest {
	public static ListGraph<Integer, Integer> graph1() {
		ListGraph<Integer, Integer> graph = new ListGraph<>();
		Node<Integer, Integer> node0 = graph.addNode(0);
		Node<Integer, Integer> node1 = graph.addNode(1);
		Node<Integer, Integer> node2 = graph.addNode(2);
		Node<Integer, Integer> node3 = graph.addNode(3);
		Node<Integer, Integer> node4 = graph.addNode(4);
		Node<Integer, Integer> node5 = graph.addNode(5);
		graph.addEdge(node0, node1, 0, Edge.OUTGOING);
		graph.addEdge(node0, node2, 0, Edge.OUTGOING);
		graph.addEdge(node0, node3, 0, Edge.OUTGOING);
		graph.addEdge(node2, node4, 0, Edge.OUTGOING);
		graph.addEdge(node2, node5, 0, Edge.OUTGOING);
		return graph;
	}

	public static ListGraph<Integer, Integer> graph2() {
		ListGraph<Integer, Integer> graph = new ListGraph<>();
		Node<Integer, Integer> node5 = graph.addNode(5);
		Node<Integer, Integer> node4 = graph.addNode(4);
		Node<Integer, Integer> node3 = graph.addNode(3);
		Node<Integer, Integer> node2 = graph.addNode(2);
		Node<Integer, Integer> node1 = graph.addNode(1);
		Node<Integer, Integer> node0 = graph.addNode(0);
		graph.addEdge(node0, node1, 0, Edge.OUTGOING);
		graph.addEdge(node0, node2, 0, Edge.OUTGOING);
		graph.addEdge(node2, node3, 0, Edge.OUTGOING);
		graph.addEdge(node2, node4, 0, Edge.OUTGOING);
		graph.addEdge(node2, node5, 0, Edge.OUTGOING);
		return graph;
	}

	public static ListGraph<Integer, Integer> graph3() {
		ListGraph<Integer, Integer> graph = new ListGraph<>();
		Node<Integer, Integer> node0 = graph.addNode(0);
		Node<Integer, Integer> node1 = graph.addNode(1);
		Node<Integer, Integer> node2 = graph.addNode(2);
		Node<Integer, Integer> node3 = graph.addNode(3);
		Node<Integer, Integer> node4 = graph.addNode(4);
		Node<Integer, Integer> node5 = graph.addNode(5);
		graph.addEdge(node0, node1, 0, Edge.OUTGOING);
		graph.addEdge(node0, node2, 0, Edge.OUTGOING);
		graph.addEdge(node2, node3, 0, Edge.OUTGOING);
		graph.addEdge(node2, node4, 0, Edge.OUTGOING);
		graph.addEdge(node1, node5, 0, Edge.OUTGOING);
		return graph;
	}

	public static void main(String[] args) {
		ParsemisGraphTest t = new ParsemisGraphTest();
		List<Graph<Integer, Integer>> graphs = new ArrayList<>();
		graphs.add(graph1());
		graphs.add(graph2());
		graphs.add(graph3());

		//print(combineGraphs(graph1(), graph2(), graph3()));

		Settings<Integer, Integer> settings = new Settings<>();
		settings.algorithm = new Algorithm<>();
		settings.strategy = new BFSStrategy<>();
		settings.minFreq = new IntFrequency(2);
		settings.factory = new ListGraph.Factory<>(new MyLabelParser(), new MyLabelParser());
		Collection<Fragment<Integer, Integer>> result = Miner.mine(graphs, settings);
		result.forEach(x -> {
			System.out.println("---");
			System.out.println("++++" + x.frequency().toString());
			Graph<Integer, Integer> g = x.toGraph();
			Iterator<Node<Integer, Integer>> iteNode = g.nodeIterator();
			while (iteNode.hasNext()) {
				Node<Integer, Integer> n = iteNode.next();
				System.out.println(n.getLabel());
			}
			Iterator<Edge<Integer, Integer>> ite = g.edgeIterator();
			while (ite.hasNext()) {
				Edge<Integer, Integer> e = ite.next();
				System.out.print(e.getNodeA().getLabel());
				System.out.print(" -> ");
				System.out.println(e.getNodeB().getLabel());

			}
		});
	}

	public static ListGraph<Integer, Integer> combineGraphs(ListGraph<Integer, Integer>... graphs) {
		if (graphs.length <= 0)
			return new ListGraph<>();

		// 合并的图都加入到图0中，所以从图1开始遍历
		for (int i = 1; i < graphs.length; i++) {
			Iterator<Node<Integer, Integer>> iteratorNode = graphs[i].nodeIterator();
			while (iteratorNode.hasNext()) {
				Node<Integer, Integer> curNode = iteratorNode.next();
				graphs[0].addNode(curNode.getLabel());
			}
			Iterator<Edge<Integer, Integer>> iteratorEdge = graphs[i].edgeIterator();
			while (iteratorEdge.hasNext()) {
				Edge<Integer, Integer> curEdge = iteratorEdge.next();
				Node<Integer, Integer> nodeA = curEdge.getNodeA();
				Node<Integer, Integer> nodeB = curEdge.getNodeB();
				graphs[0].addEdge(nodeA, nodeB, 0, curEdge.getDirection());
			}
		}

		return graphs[0];
	}

	public static String print(ListGraph<Integer, Integer> graph) {
		StringBuilder str = new StringBuilder("[graph: [node:");
		Iterator<Node<Integer, Integer>> iteratorNode = graph.nodeIterator();
		while (iteratorNode.hasNext()) {
			Node<Integer, Integer> curNode = iteratorNode.next();
			str.append(curNode.getLabel());
			if (iteratorNode.hasNext())
				str.append(", ");
		}
		str.append("][edge:");
		Iterator<Edge<Integer, Integer>> iteratorEdge = graph.edgeIterator();
		while (iteratorEdge.hasNext()) {
			Edge<Integer, Integer> curEdge = iteratorEdge.next();
			String direction;
			switch (curEdge.getDirection()) {
			case Edge.INCOMING:
				direction = "<-";
				break;
			case Edge.OUTGOING:
				direction = "->";
				break;
			case Edge.UNDIRECTED:
				direction = "--";
				break;
			default:
				direction = ", ";
				break;
			}
			str.append("(" + curEdge.getNodeA().getLabel() + " " + direction + " "
					+ curEdge.getNodeB().getLabel() + ")");
			if (iteratorNode.hasNext())
				str.append(", ");
		}
		str.append("]]");
		System.out.println(str);
		return str.toString();
	}
}
