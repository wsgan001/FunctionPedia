package cn.edu.pku.sei.tsr.dragon.test;

import cn.edu.pku.sei.tsr.dragon.graph.GraphUtils;
import cn.edu.pku.sei.tsr.dragon.graph.entity.EdgeInfo;
import cn.edu.pku.sei.tsr.dragon.graph.entity.GraphInfo;
import cn.edu.pku.sei.tsr.dragon.graph.entity.NodeInfo;
import cn.edu.pku.sei.tsr.dragon.graph.entity.NodeLabel;
import cn.edu.pku.sei.tsr.dragon.graph.graphofstring.GraphBuilderOfStringNode;

public class GraphTest {
	public static void main(String[] args) {
		GraphInfo graphInfo = graph();
		System.out.println(graphInfo);
		System.err.println("==================");
		GraphUtils.toString(GraphBuilderOfStringNode.convertToParsemisGraph(graphInfo));
	}

	public static GraphInfo graph() {
		NodeInfo n1 = new NodeInfo(NodeLabel.VP, null);
		NodeInfo n2 = new NodeInfo(NodeLabel.verb, "convert");
		NodeInfo n3 = new NodeInfo(NodeLabel.NP, null);
		NodeInfo n4 = new NodeInfo(NodeLabel.noun, "date");
		NodeInfo n5 = new NodeInfo(NodeLabel.noun, "string");
		NodeInfo n6 = new NodeInfo(NodeLabel.PP, null);
		NodeInfo n7 = new NodeInfo(NodeLabel.conj, "from");
		NodeInfo n8 = new NodeInfo(NodeLabel.NP, null);
		NodeInfo n9 = new NodeInfo(NodeLabel.adj, "American");
		NodeInfo n10 = new NodeInfo(NodeLabel.noun, "format");
		NodeInfo n11 = new NodeInfo(NodeLabel.PP, "");
		NodeInfo n12 = new NodeInfo(NodeLabel.conj, "to");
		NodeInfo n13 = new NodeInfo(NodeLabel.NP, null);
		NodeInfo n14 = new NodeInfo(NodeLabel.noun, "euro");
		NodeInfo n15 = new NodeInfo(NodeLabel.noun, "format");
		EdgeInfo e1 = new EdgeInfo(n1, n2, 1);
		EdgeInfo e2 = new EdgeInfo(n1, n3, 1);
		EdgeInfo e3 = new EdgeInfo(n3, n4, 1);
		EdgeInfo e4 = new EdgeInfo(n3, n5, 1);
		EdgeInfo e5 = new EdgeInfo(n1, n6, 1);
		EdgeInfo e6 = new EdgeInfo(n6, n7, 1);
		EdgeInfo e7 = new EdgeInfo(n6, n8, 1);
		EdgeInfo e8 = new EdgeInfo(n8, n9, 1);
		EdgeInfo e9 = new EdgeInfo(n8, n10, 1);
		EdgeInfo e10 = new EdgeInfo(n1, n11, 1);
		EdgeInfo e11 = new EdgeInfo(n11, n12, 1);
		EdgeInfo e12 = new EdgeInfo(n11, n13, 1);
		EdgeInfo e13 = new EdgeInfo(n13, n14, 1);
		EdgeInfo e14 = new EdgeInfo(n13, n15, 1);
		GraphInfo graph = new GraphInfo();
		graph.addNode(n1);
		graph.addNode(n2);
		graph.addNode(n3);
		graph.addNode(n4);
		graph.addNode(n5);
		graph.addNode(n6);
		graph.addNode(n7);
		graph.addNode(n8);
		graph.addNode(n9);
		graph.addNode(n10);
		graph.addNode(n11);
		graph.addNode(n12);
		graph.addNode(n13);
		graph.addNode(n14);
		graph.addNode(n15);
		graph.addEdge(e1);
		graph.addEdge(e2);
		graph.addEdge(e3);
		graph.addEdge(e4);
		graph.addEdge(e5);
		graph.addEdge(e6);
		graph.addEdge(e7);
		graph.addEdge(e8);
		graph.addEdge(e9);
		graph.addEdge(e10);
		graph.addEdge(e11);
		graph.addEdge(e12);
		graph.addEdge(e13);
		graph.addEdge(e14);
		return graph;
	}
}
