package cn.edu.pku.sei.tsr.dragon.outdated;

import java.util.List;

import org.apache.log4j.Logger;

import cn.edu.pku.sei.tsr.dragon.content.SentenceParser;
import cn.edu.pku.sei.tsr.dragon.content.entity.PhraseInfo;
import cn.edu.pku.sei.tsr.dragon.content.entity.SentenceInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.entity.NounPhraseStructureInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.entity.PrepPhraseStructureInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.entity.VerbalPhraseStructureInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.entity.dictionary.AdjectiveInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.entity.dictionary.ConjunctionInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.entity.dictionary.DictionaryInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.entity.dictionary.NounInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.entity.dictionary.VerbInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.entity.dictionary.WordInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.parser.PhraseExtractor;
import cn.edu.pku.sei.tsr.dragon.nlp.parser.PhraseFilter;
import cn.edu.pku.sei.tsr.dragon.nlp.parser.filters.FilterDeterminer;
import cn.edu.pku.sei.tsr.dragon.utils.TreeUtils;
import edu.stanford.nlp.trees.Tree;

@Deprecated
public class PhraseStructureExtractor {
	public static final Logger logger = Logger.getLogger(PhraseStructureExtractor.class);

	private static final int	BEFORE_VB			= 1;
	private static final int	AFTER_VB_BEFORE_NP	= 2;
	private static final int	AFTER_NP_BEFORE_PP	= 3;
	private static final int	AFTER_PP			= 4;

	public static VerbalPhraseStructureInfo extractVPStructure(Tree tree) {
		VerbalPhraseStructureInfo vpStructure = new VerbalPhraseStructureInfo();

		if (!TreeUtils.isVP(tree))
			return null;

		// 处理vp期间，当前child的位置
		// BEFORE_VB -> AFTER_VB_BEFORE_NP -> AFTER_NP_BEFORE_PP -> AFTER_PP
		int pos = BEFORE_VB;
		Tree[] children = tree.children();
		for (int i = 0; i < children.length; i++) {
			Tree child = children[i];
			if (pos == BEFORE_VB) {
				if (TreeUtils.isVB(child)) {
					String verb = TreeUtils.getLeafString(child);
					VerbInfo keyVerb = DictionaryInfo.addVerb(verb);
					vpStructure.setVerb(keyVerb);
					pos = AFTER_VB_BEFORE_NP;
				}
				// 没有读到vb就继续保持当前状态读
			}
			else if (pos == AFTER_VB_BEFORE_NP) {
				if (TreeUtils.isParticle(child)) {
					// 比如set up the database，up是助词，保留
					Tree particleWord = child.getChild(0);
					if (TreeUtils.isParticleWord(particleWord)) {
						String particle = TreeUtils.getLeafString(particleWord);
						ConjunctionInfo particleConj = DictionaryInfo.addConjunction(particle);
						vpStructure.setParticle(particleConj);
					}
				}
				else if (TreeUtils.isNP(child)) {
					NounPhraseStructureInfo npStructure = extractNPStructure(child);
					vpStructure.setSubNP(npStructure);
					pos = AFTER_NP_BEFORE_PP;
				}
				else if (TreeUtils.isPP(child)) {
					// 如果本来不包含np，那么直接遇到了PP，要处理
					PrepPhraseStructureInfo ppStructure = extractPPStructure(child);
					vpStructure.getSubPPList().add(ppStructure);
					pos = AFTER_PP;
				}
			}
			else if (pos == AFTER_NP_BEFORE_PP || pos == AFTER_PP) {
				if (TreeUtils.isPP(child)) {
					// PP可以有多个
					PrepPhraseStructureInfo ppStructure = extractPPStructure(child);
					vpStructure.getSubPPList().add(ppStructure);
					pos = AFTER_PP;
				}
			}
		}

		// return vpStructure;
		return adjust(vpStructure);
	}

	public static NounPhraseStructureInfo extractNPStructure(Tree tree) {
		NounPhraseStructureInfo npStructure = new NounPhraseStructureInfo();

		if (!TreeUtils.isNP(tree))
			return null;

		Tree[] children = tree.children();
		for (int i = 0; i < children.length; i++) {
			Tree child = children[i];
			if (TreeUtils.isDT(child)) {
				// 保留all、each、both、as、or这类冠词
				String dt = TreeUtils.getLeafString(child);
				if (FilterDeterminer.isValuable(dt)) {
					WordInfo dtWord = DictionaryInfo.addOtherWord(dt);
					npStructure.addWordToChain(dtWord);
				}
			}
			else if (TreeUtils.isAdjective(child)) {
				// 保留形容词
				String adj = TreeUtils.getLeafString(child);
				AdjectiveInfo adjInfo = DictionaryInfo.addAdjective(adj);
				npStructure.addWordToChain(adjInfo);
			}
			else if (TreeUtils.isNN(child)) {
				String noun = TreeUtils.getLeafString(child);
				NounInfo nounInfo = DictionaryInfo.addNoun(noun);
				npStructure.addWordToChain(nounInfo);
			}
			else if (TreeUtils.isNP(child)) {
				// 子np中的所有名词提上一级到当前名词链，pp则舍去
				NounPhraseStructureInfo subNP = extractNPStructure(child);
				List<WordInfo> subNPWordChain = subNP.getWordChain();
				npStructure.getWordChain().addAll(subNPWordChain);
			}
			else if (TreeUtils.isCC(child)) {
				// 保留连词
				String cc = TreeUtils.getLeafString(child);
				ConjunctionInfo conjInfo = DictionaryInfo.addConjunction(cc);
				npStructure.addWordToChain(conjInfo);
			}
			else if (TreeUtils.isPP(child)) {
				// 如果是np下辖pp的形式, 遇到pp时的处理
				// 如vp=vb+np（np+pp）时, 且当前节点为根节点的儿子, 则保留;
				PrepPhraseStructureInfo subPP = extractPPStructure(child);
				npStructure.setSubPP(subPP);
			}
		}

		return adjust(npStructure);
	}

	public static PrepPhraseStructureInfo extractPPStructure(Tree tree) {
		PrepPhraseStructureInfo ppStructure = new PrepPhraseStructureInfo();

		if (!TreeUtils.isPP(tree))
			return null;

		Tree[] children = tree.children();
		for (int i = 0; i < children.length; i++) {
			Tree child = children[i];
			if (TreeUtils.isPreposition(child)) {
				// 保留介词和to
				// 只保留第一个遇到的？
				if (ppStructure.getConjunction() == null) {
					String prep = TreeUtils.getLeafString(child);
					ConjunctionInfo conjInfo = DictionaryInfo.addConjunction(prep);
					ppStructure.setConjunction(conjInfo);
				}
			}
			else if (TreeUtils.isNP(child)) {
				NounPhraseStructureInfo subNP = extractNPStructure(child);
				ppStructure.setSubNP(subNP);
			}
			else if (TreeUtils.isPP(child)) {
				PrepPhraseStructureInfo subPP = extractPPStructure(child);
				ppStructure.setSubPP(subPP);
			}
			else if (TreeUtils.isCC(child)) {
				// 不保留连词，如and等
			}
		}

		return ppStructure;
	}

	public static VerbalPhraseStructureInfo adjust(VerbalPhraseStructureInfo vpStructure) {
		if (vpStructure == null)
			return null;
		if (vpStructure.getSubPPList().size() <= 0) // 该VP不包含subPP
			if (vpStructure.getSubNP() != null && vpStructure.getSubNP().getSubPP() != null) {
				// subNP包含了一个pp
				vpStructure.getSubPPList().add(vpStructure.getSubNP().getSubPP());
				vpStructure.getSubNP().setSubPP(null);
			}
		return vpStructure;
	}

	public static NounPhraseStructureInfo adjust(NounPhraseStructureInfo np) {
		if (np == null)
			return null;
		if (np.getWordChain().size() <= 0) {
			// 如果名词结构中不包含名词，说明该名词可能是个代词，用sth.代替
			NounInfo sth = DictionaryInfo.addNoun("SOMETHING");
			np.getWordChain().add(sth);
		}
		return np;
	}

	public static void main(String[] args) {
		String string = "This program is designed to iterate over all the row cells and convert them from American formats to either Chinese format or the euro format.";
		// "I tried to add an object to files.";
		// "This program is designed to convert all the time string from American formats to either
		// Chinese format or the euro format.";
		// "Would you please set up the database for further processing?";
		// "I've created JTextArea to append the elements but the results is 'loaded' instead of
		// being listed out.";
		// "I'm playing around with natural language parse trees, he want to parse strings to
		// trees.";
		// " it's tree parsing.";
		// "He wants to put that into this box, I hope I can move these from there to his room.";
		// "he'll be a dancer, and he wouldn't said that his father might be a farmer, his family
		// will never be Chinese.";
		// "Android: how to get the ConnectionTimeOut Value set to a HttpClient";
		// "Here is the code I'm using to create my multithreaded httpclient object. I'm trying to
		// have a 4 second timeout across the board so if nothing happens for 4 seconds to drop the
		// requests. ";
		// "Well,I think you should go with the 1 st Approach by using Front Controller pattern. It
		// should consist of only a SINGLE SERVLET which provides a centralized entry point for all
		// requests.This servlet will delegate all request to the required servlet. You need to do
		// only following thing to apply the front controller pattern in your application:";

		SentenceInfo sentence = new SentenceInfo(string);
		SentenceParser.parseGrammaticalTree(sentence);
		sentence.getGrammaticalTree().pennPrint();
		PhraseExtractor.extractVerbPhrases(sentence);

		for (PhraseInfo phrase : sentence.getPhrases()) {
			PhraseFilter.filter(phrase, sentence);
			// if (phrase.getProofTotalScore() >= Proof.MID) {
			Tree t = phrase.getStemmedTree();
			VerbalPhraseStructureInfo vp = new VerbalPhraseStructureInfo(phrase);
			System.out.println(vp);
			// }
		}
	}
}
