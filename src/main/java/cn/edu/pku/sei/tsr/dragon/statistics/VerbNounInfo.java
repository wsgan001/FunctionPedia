package cn.edu.pku.sei.tsr.dragon.statistics;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import cn.edu.pku.sei.tsr.dragon.content.entity.PhraseInfo;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;

public class VerbNounInfo implements Serializable {
	private static final long serialVersionUID = 7019232103882587949L;

	private static final List<Predicate<String>> speechFilterList = new ArrayList<>();
	private static final List<Predicate<String>> wordFilterList = new ArrayList<>();

	private enum StatusEnum {
		NONE, FIND_VP, FIND_VP_END
	}

	private class TreeStatus {
		private StatusEnum status = StatusEnum.NONE;
	}

	private String verb;
	private String noun;

	private List<String> nouns = new ArrayList<>();

	static {
		speechFilterList.add(x -> x.equals("DT"));
	}

	public VerbNounInfo(PhraseInfo phrase) {
		Tree tree = phrase.getStemmedTree();
		visit(tree, new TreeStatus());
	}

	@Override
	public String toString() {
		return String.format("%s %s", verb, noun);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj.getClass() != this.getClass()) return false;
		VerbNounInfo other = (VerbNounInfo) obj;
		return verb.equals(other.verb) && noun.equals(other.noun);
	}

	@Override
	public int hashCode() {
		return verb.hashCode() + noun.hashCode();
	}

	public String getVerb() {
		return verb;
	}

	public String getNoun() {
		return noun;
	}

	public List<String> getNouns() {
		return nouns;
	}

	private boolean visit(Tree tree, TreeStatus status) {
		String label = tree.label().toString();
		if (status.status == StatusEnum.NONE && label.equals("VP")) {
			status.status = StatusEnum.FIND_VP;
			for (Tree subTree : tree.children()) if (visit(subTree, status)) return true;
		} else if (status.status == StatusEnum.FIND_VP && tree.isLeaf()) {
			verb = tree.label().toString();
			status.status = StatusEnum.FIND_VP_END;
		} else if (status.status == StatusEnum.FIND_VP_END && label.equals("NP")) {
			Optional<Tree> firtsNP = Arrays.asList(tree.children())
					.stream()
					.filter(subTree -> subTree.label().toString().equals("NP"))
					.findFirst();
			Tree npTree = firtsNP.isPresent() ? firtsNP.get() : tree;
			// ��ȡ�����б�
			TregexPattern tregexPattern = TregexPattern.compile("/.*/ < /.*/");
			TregexMatcher matcher = tregexPattern.matcher(npTree);
			while (matcher.findNextMatchingNode()){
				Tree t = matcher.getMatch();
				if (t.depth() == 1) {
					String speech = t.label().toString();
					String word = t.getChild(0).label().toString();
					if(!speechFilterList.stream().anyMatch(pre -> pre.test(speech)) &&
						!wordFilterList.stream().anyMatch(pre -> pre.test(speech))) nouns.add(word);
				}
			}
			if (nouns.size() != 0) noun = nouns.get(nouns.size() - 1);
			return true;
		} else {
			for (Tree subTree : tree.children()) if (visit(subTree, status)) return true;
		}
		return false;
	}
}
