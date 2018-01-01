package cn.edu.pku.sei.tsr.dragon.code.entity;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

public class Comment implements ISegmentable, Serializable {
	private static final long	serialVersionUID	= -8024668410920547749L;
	private String				comment;

	public Comment(String comment) {
		this.comment = comment;
	}

	@Override
	public String toString() {
		return comment;
	}

	@Override
	public List<String> segmentWords() {
		return null;
	}

	@Override
	public List<Pair<String, String>> segmentPairs() {
		return null;
	}

}
