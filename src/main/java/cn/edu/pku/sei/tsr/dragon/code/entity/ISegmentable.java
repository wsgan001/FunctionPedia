package cn.edu.pku.sei.tsr.dragon.code.entity;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

public interface ISegmentable {
	List<String> segmentWords();
	List<Pair<String, String>> segmentPairs();
}
