package cn.edu.pku.sei.tsr.dragon.outdated;

import java.io.Serializable;
import java.util.UUID;

import cn.edu.pku.sei.tsr.dragon.utils.UUIDInterface;
import de.parsemis.graph.Edge;

public class EdgeInfo implements Serializable, UUIDInterface {
	private static final long serialVersionUID = 6038310513494512664L;

	public static final int	INCOMING	= Edge.INCOMING;
	public static final int	OUTGOING	= Edge.OUTGOING;
	public static final int	UNDIRECTED	= Edge.UNDIRECTED;

	private String		uuid;
	private NodeInfo	nodeA;
	private NodeInfo	nodeB;
	private int			direction;

	public EdgeInfo(NodeInfo nodeA, NodeInfo nodeB, int direction) {
		uuid = UUID.randomUUID().toString();
		this.nodeA = nodeA;
		this.nodeB = nodeB;
		this.direction = direction;
	}

	public NodeInfo getNodeA() {
		return nodeA;
	}

	public void setNodeA(NodeInfo nodeA) {
		this.nodeA = nodeA;
	}

	public NodeInfo getNodeB() {
		return nodeB;
	}

	public void setNodeB(NodeInfo nodeB) {
		this.nodeB = nodeB;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	// Depends on equals method of NodeInfo
	// 如果两条边的两端对象相同、类型相同，那就是同一条边
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof EdgeInfo))
			return false;
		EdgeInfo objEdge = (EdgeInfo) obj;
		return this.nodeA.equals(objEdge.nodeA) && this.nodeB.equals(objEdge.nodeB)
				&& this.direction == objEdge.direction;
	}

	@Override
	public int hashCode() {
		return nodeA.hashCode() + nodeB.hashCode() + direction;
	}

	@Override
	public String toString() {
		String directionStr;
		switch (direction) {
		case INCOMING:
			directionStr = "<-";
			break;
		case OUTGOING:
			directionStr = "->";
			break;
		case UNDIRECTED:
			directionStr = "--";
			break;
		default:
			directionStr = ", ";
			break;
		}
		return "(" + nodeA + " " + directionStr + " " + nodeB + ")";
	}

	@Override
	public String getUuid() {
		return uuid;
	}
}
