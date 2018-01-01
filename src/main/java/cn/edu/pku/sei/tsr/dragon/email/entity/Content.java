package cn.edu.pku.sei.tsr.dragon.email.entity;

import java.io.Serializable;
import java.util.ArrayList;

public class Content implements Serializable{

	private static final long serialVersionUID = 1581414664749286982L;
	private ArrayList<Segment>	segments;

	public ArrayList<Segment> getSegments() {
		return segments;
	}

	public void setSegments(ArrayList<Segment> segments) {
		this.segments = segments;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("content segments:" + "\n");
		for (Segment s : segments) {
			sb.append(s.toString() + "\n");
		}
		return sb.toString();
	}
}
