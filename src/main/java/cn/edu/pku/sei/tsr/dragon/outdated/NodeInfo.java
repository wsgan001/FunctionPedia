package cn.edu.pku.sei.tsr.dragon.outdated;

import java.io.Serializable;
import java.util.UUID;

import cn.edu.pku.sei.tsr.dragon.graph.entity.NodeLabel;
import cn.edu.pku.sei.tsr.dragon.utils.UUIDInterface;

public class NodeInfo implements Serializable, UUIDInterface {
	private static final long serialVersionUID = 9135849985209562786L;

	private String uuid = null;

	private NodeLabel	label;
	private String		value;

	public NodeInfo(NodeLabel label, String value) {
		uuid = UUID.randomUUID().toString();
		this.setLabel(label);
		this.setValue(value);
	}

	public NodeLabel getLabel() {
		return label;
	}

	public void setLabel(NodeLabel label) {
		this.label = label;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		if (value == "")
			this.value = null;
		else
			this.value = value;
	}

	@Override
	public String toString() {
		if (value == null)
			return "<" + label.toString() + ">";
		else
			return "<" + label.toString() + " " + value + ">";
	}

	// Did not override equals() and hashCode(), compare by memory address.

	public static void main(String[] args) {
		NodeInfo node = new NodeInfo(NodeLabel.NP, "");
		NodeInfo n2 = new NodeInfo(NodeLabel.NP, "a");
		NodeInfo n3 = new NodeInfo(NodeLabel.NP, null);
		NodeInfo n4 = new NodeInfo(NodeLabel.NP, null);
		NodeInfo n5 = new NodeInfo(NodeLabel.VP, null);
		System.out.println(node + "\t" + n2 + "\t" + n3 + "\t" + n4);
		System.out.println(node.equals(n2));
		System.out.println(node.equals(n3));
		System.out.println(n2.equals(n3));
		System.out.println(n4.equals(n3));
		System.out.println(n4.equals(n5));
	}

	@Override
	public String getUuid() {
		return uuid;
	}

	public void setUuid() {
		this.uuid = UUID.randomUUID().toString();
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

}
