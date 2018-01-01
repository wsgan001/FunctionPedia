package cn.edu.pku.sei.tsr.dragon.outdated;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GraphInfo implements Serializable {
	private static final long serialVersionUID = 5189153241004867175L;

	private String			name;
	private NodeInfo		root;
	private List<NodeInfo>	nodeList;
	private List<EdgeInfo>	edgeList;	// edge是有序的，未来从图中恢复出短语时还要按照加入的顺序，也就是下标，所以不能是set

	public GraphInfo() {
		nodeList = new ArrayList<>();
		edgeList = new ArrayList<>();
	}

	public GraphInfo(String name) {
		this();
		setName(name);
	}

	public GraphInfo merge(GraphInfo... graphs) {
		// 不能用addAll，万一已经存在则重复了！
		for (int i = 0; i < graphs.length; i++) {
			for (int j = 0; j < graphs[i].getNodeList().size(); j++) {
				NodeInfo node = graphs[i].getNodeList().get(j);
				if (!nodeList.contains(node)) // 结点比较要按照不同内存对象
					addNode(node);
			}
			for (int j = 0; j < graphs[i].getEdgeList().size(); j++) {
				EdgeInfo edge = graphs[i].getEdgeList().get(j);
				if (!edgeList.contains(edge)) // 边的比较要依赖子定义的方法，两边节点和方向一样就算一样
					addEdge(edge);
			}
		}
		return this;
	}

	public boolean addNode(NodeInfo node) {
		try {
			if (node == null)
				return false;
			nodeList.add(node);
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean addEdge(NodeInfo nodeA, NodeInfo nodeB, int direction) {
		EdgeInfo edge = new EdgeInfo(nodeA, nodeB, direction);
		return addEdge(edge);
	}

	// 结果表示该边已经加入图中
	public boolean addEdge(EdgeInfo edge) {
		try {
			if (edge == null)
				return false;

			// 没有重复边才加
			if (!edgeList.contains(edge)) {
				if (!nodeList.contains(edge.getNodeA()))
					nodeList.add(edge.getNodeA());
				if (!nodeList.contains(edge.getNodeB()))
					nodeList.add(edge.getNodeB());
				edgeList.add(edge);
			}
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<NodeInfo> getNodeList() {
		return nodeList;
	}

	public List<EdgeInfo> getEdgeList() {
		return edgeList;
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder("[graph: [node:");
		for (int i = 0; i < nodeList.size(); i++) {
			str.append(nodeList.get(i));
			if (i < nodeList.size() - 1)
				str.append(", ");
		}
		str.append("][edge:");
		for (int i = 0; i < edgeList.size(); i++) {
			str.append(edgeList.get(i));
			if (i < edgeList.size() - 1)
				str.append(", ");
		}
		str.append("]]");
		return str.toString();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public NodeInfo getRoot() {
		if (root == null)
			return nodeList.get(0);
		return root;
	}

	public void setRoot(NodeInfo root) {
		if (!nodeList.contains(root))
			nodeList.add(root);
		this.root = root;
	}

}
