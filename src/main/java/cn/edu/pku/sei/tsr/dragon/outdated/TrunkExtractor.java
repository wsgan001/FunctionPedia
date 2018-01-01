package cn.edu.pku.sei.tsr.dragon.outdated;

import java.util.List;

import org.apache.log4j.Logger;

import cn.edu.pku.sei.tsr.dragon.content.SentenceParser;
import cn.edu.pku.sei.tsr.dragon.content.entity.PhraseInfo;
import cn.edu.pku.sei.tsr.dragon.content.entity.SentenceInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.parser.PhraseExtractor;
import cn.edu.pku.sei.tsr.dragon.nlp.parser.PhraseFilter;
import cn.edu.pku.sei.tsr.dragon.nlp.parser.Proof;
import cn.edu.pku.sei.tsr.dragon.nlp.parser.ProofType;
import cn.edu.pku.sei.tsr.dragon.utils.TreeUtils;
import edu.stanford.nlp.trees.Tree;

@Deprecated
public class TrunkExtractor {
	public static final Logger logger = Logger.getLogger(TrunkExtractor.class);

	public static final int	DEFAULT		= 0;
	public static final int	KERNAL		= 1;
	public static final int	TRUNK		= 2;
	public static final int	SKELETON	= 3;

	private static final int	BEFORE_VB			= 1;
	private static final int	AFTER_VB_BEFORE_NP	= 2;
	private static final int	AFTER_NP_BEFORE_PP	= 3;
	private static final int	AFTER_PP			= 4;

	private static String	NP_NN			= "NP < /NN.*/";
	private static String	NP_NP			= "NP !< /NN.*/ < NP";
	private static String	NP_DT_PRP		= "NP <: DT|PRP";
	private static String	NP_PP			= "NP < PP";
	private static String	PP_NN			= "PP < /NN.*/";
	private static String	PP_NP			= "PP !< /NN.*/ < NP !<PP";
	private static String	PP_PP			= "PP !< /NN.*/ !< NP < PP";
	private static String	PP_NP_PP		= "PP !< /NN.*/ < NP < PP";
	private static String	PP_MEANINGLESS	= "PP !< /NN.*/ !< NP !< PP";

	private PhraseInfo	phrase;
	private Tree		copiedRootTree;

	public TrunkExtractor(PhraseInfo phraseInfo) {
		this.phrase = phraseInfo;
	}

	public void extract() {
		TrunkInfo trunkInfo = new TrunkInfo();

		Tree kernal = extractVP(KERNAL);
		Tree trunk = extractVP(TRUNK);
		Tree skeleton = extractVP(SKELETON);

		if (kernal != null)
			trunkInfo.setKernal(new PhraseInfo(kernal));
		if (trunk != null)
			trunkInfo.setTrunk(new PhraseInfo(trunk));
		if (skeleton != null)
			trunkInfo.setSkeleton(new PhraseInfo(skeleton));

		phrase.setTrunk(trunkInfo);
	}

	public Tree extractVP(int type) {
		if (phrase == null || phrase.getStemmedTree() == null || !phrase.isVP())
			return null;

		int state = BEFORE_VB;
		copiedRootTree = phrase.getStemmedTree().deepCopy();

		// copiedRootTree = rootTreeCopy; // 只在每次extract的一开始改变copiedRootTree的值。

		// // 当一个FIFO的队列来用
		// LinkedList<Tree> queue = new LinkedList<Tree>();
		// queue.addLast(rootTreeCopy);
		//
		// while (queue.size() > 0) {
		// Tree tree = queue.removeFirst();
		//
		// if (tree == rootTreeCopy) {
		// // root
		Tree[] children = copiedRootTree.children();
		for (int i = 0; i < children.length; i++) {
			// queue.addLast(children[i]);
			// }
			// }
			Tree child = children[i];

			if (state == BEFORE_VB) {
				if (TreeUtils.isVB(child))
					state = AFTER_VB_BEFORE_NP;
				else {
					// VB之前出现的元素移除
					TreeUtils.removeSubTree(child, copiedRootTree);
				}
			}
			else if (state == AFTER_VB_BEFORE_NP) {
				if (TreeUtils.isNP(child)) {
					state = AFTER_NP_BEFORE_PP;
					if (TreeUtils.matchPattern(child, NP_DT_PRP))
						TreeUtils.removeSubTree(child, copiedRootTree);
					processNP(child, type);
				}
				else if (TreeUtils.isPP(child) && phrase.hasProof(ProofType.FORM_VP_PP)) {
					// 如果本来不包含np，那么直接遇到了PP，要处理
					state = AFTER_PP;
					processPP(child, type);
				}
				else if (TreeUtils.isParticle(child)) {
					// && phrase.hasProof(ProofType.FORM_VP_PP)) {
					// // 比如playing around with xxxx，around是助词，保留
					// // 只在vp=vb+pp模式中使用
				}
				else {
					// 非NP元素移除
					TreeUtils.removeSubTree(child, copiedRootTree);
				}
			}
			else if (state == AFTER_NP_BEFORE_PP) {
				if ((type == TRUNK || type == SKELETON) && TreeUtils.isPP(child)) {
					state = AFTER_PP;
					processPP(child, type);
				}
				else {
					// 非PP元素移除
					TreeUtils.removeSubTree(child, copiedRootTree);
				}
			}
			else if (state == AFTER_PP) {
				if ((type == TRUNK || type == SKELETON) && TreeUtils.isPP(child)) {
					state = AFTER_PP;
					processPP(child, type);
				}
				else {
					// 非PP元素移除
					TreeUtils.removeSubTree(child, copiedRootTree);
				}
			}

		}
		return copiedRootTree;
	}

	public void processNP(Tree phrasalRootTree, int type) {
		Tree[] children = phrasalRootTree.children();
		if (TreeUtils.matchPattern(phrasalRootTree, NP_NN)) {
			boolean lastNN = true;
			for (int i = children.length - 1; i >= 0; i--) {
				Tree child = children[i];
				if (TreeUtils.isNN(child)) {
					if (type == KERNAL && !lastNN) {
						// kernal只读取最后一个nn
						TreeUtils.removeSubTree(child, phrasalRootTree);
					}
					lastNN = false;
				}
				else if (TreeUtils.isNP(child)) {
					if (type == SKELETON) {
						// 只有skeleton保留NP下辖NN时的NP
					}
					else {
						TreeUtils.removeSubTree(child, phrasalRootTree);
					}
				}
				else if (TreeUtils.isAdjective(child)) {
					if (type == KERNAL) {
						// kernal不读取形容词
						TreeUtils.removeSubTree(child, phrasalRootTree);
					}
					else if (type == TRUNK && !TreeUtils.isJJ(child)) {
						// trunk只保留纯形容词jj，不保留比较级
						TreeUtils.removeSubTree(child, phrasalRootTree);
					}
					// skeleton都保留
				}
				else if (TreeUtils.isPrepOrConjunction(child)) {
					// 保留介词/连词
				}
				else {
					TreeUtils.removeSubTree(child, phrasalRootTree);
				}
			}
		}
		else if (TreeUtils.matchPattern(phrasalRootTree, NP_NP)) {
			for (int i = 0; i < children.length; i++) {
				Tree child = children[i];
				if (TreeUtils.isNP(child)) {
					processNP(child, type);
				}
				else if (TreeUtils.isPrepOrConjunction(child)) {
					// 保留介词/连词
				}
				else if (TreeUtils.isPP(child) && TreeUtils.matchPattern(phrasalRootTree, NP_PP)) {
					// 如果是np下辖pp的形式, 遇到pp时的处理
					if ((type == TRUNK && phrase.hasProof(ProofType.FORM_VP_NP))
							&& TreeUtils.hasChild(copiedRootTree, child) || type == SKELETON) {
						// 如获取trunk形式, vp=vb+np（np+pp）时, 且当前节点为根节点的儿子, 则保留;
						// 如获取skeleton时，一律保留
						processPP(child, type);
					}
					else {
						TreeUtils.removeSubTree(child, phrasalRootTree);
					}
				}
				else {
					TreeUtils.removeSubTree(child, phrasalRootTree);
				}
			}
		}
		else if (TreeUtils.matchPattern(phrasalRootTree, NP_DT_PRP)) {
			if (type == SKELETON && TreeUtils.hasChild(copiedRootTree, phrasalRootTree)
					&& (phrase.hasProof(ProofType.FORM_VP_PP)
							&& (phrase.hasProof(ProofType.FEATURE_NP_DT)
									|| phrase.hasProof(ProofType.FEATURE_NP_PRP)))) {
				// vp=vb+np(dt|prp)+pp时，此处np（dt|prp）是root直辖的，才予保留
			}
			else {
				// 直接将该np从根树下移除。
				TreeUtils.removeSubTree(phrasalRootTree, copiedRootTree);
			}
		}
		else {
			TreeUtils.removeSubTree(phrasalRootTree, copiedRootTree);
			return;
		}

		// 如果当前phrase变成光杆司令，删掉它！
		if (phrasalRootTree.isLeaf())
			TreeUtils.removeSubTree(phrasalRootTree, copiedRootTree);
	}

	public void processPP(Tree phrasalRootTree, int type) {
		Tree[] children = phrasalRootTree.children();

		if (TreeUtils.matchPattern(phrasalRootTree, PP_NN)) {
			boolean lastNN = true;
			for (int i = children.length - 1; i >= 0; i--) {
				Tree child = children[i];
				if (TreeUtils.isPreposition(child) || TreeUtils.isVB(child)) {
					// 保留介词和to
				}
				else if (TreeUtils.isNN(child)) {
					if (type == KERNAL && !lastNN) {
						// kernal只读取最后一个nn
						TreeUtils.removeSubTree(child, phrasalRootTree);
					}
					lastNN = false;
				}
				else if (TreeUtils.isAdjective(child)) {
					if (type == KERNAL) {
						// kernal不读取形容词
						TreeUtils.removeSubTree(child, phrasalRootTree);
					}
					else if (type == TRUNK && !TreeUtils.isJJ(child)) {
						// trunk只保留纯形容词jj，不保留比较级
						TreeUtils.removeSubTree(child, phrasalRootTree);
					}
					// skeleton都保留
				}
				else {
					TreeUtils.removeSubTree(child, phrasalRootTree);
				}
			}
		}
		else if (TreeUtils.matchPattern(phrasalRootTree, PP_NP)) {
			for (int i = 0; i < children.length; i++) {
				Tree child = children[i];
				if (TreeUtils.isPreposition(child) || TreeUtils.isVB(child)) {
					// 保留介词和to
				}
				else if (TreeUtils.isNP(child)) {
					processNP(child, type);
				}
				else if (TreeUtils.isCC(child)) {
					if (type == KERNAL || type == TRUNK) {
						// 不保留连词，如and等
						TreeUtils.removeSubTree(child, phrasalRootTree);
					}
					// skeleton都保留
				}
				else {
					TreeUtils.removeSubTree(child, phrasalRootTree);
				}
			}
		}
		else if (TreeUtils.matchPattern(phrasalRootTree, PP_NP_PP)) {
			for (int i = 0; i < children.length; i++) {
				Tree child = children[i];
				if (TreeUtils.isPreposition(child) || TreeUtils.isVB(child)) {
					// 保留介词和to
				}
				else if (TreeUtils.isNP(child)) {
					processNP(child, type);
				}
				else if (TreeUtils.isPP(child)) {
					if (type == SKELETON)
						processPP(child, type);
				}
				else if (TreeUtils.isCC(child)) {
					if (type == KERNAL || type == TRUNK) {
						// 不保留连词，如and等
						TreeUtils.removeSubTree(child, phrasalRootTree);
					}
					// skeleton都保留
				}
				else {
					TreeUtils.removeSubTree(child, phrasalRootTree);
				}
			}
		}
		else if (TreeUtils.matchPattern(phrasalRootTree, PP_PP)) {
			for (int i = 0; i < children.length; i++) {
				Tree child = children[i];
				if (TreeUtils.isPrepOrConjunction(child) || TreeUtils.isVB(child)) {
					// 保留介词和连词
				}
				else if (TreeUtils.isPP(child)) {
					processPP(child, type);
				}
				else {
					TreeUtils.removeSubTree(child, phrasalRootTree);
				}
			}
		}
		else {
			TreeUtils.removeSubTree(phrasalRootTree, copiedRootTree);
			return;
		}

		// 如果当前phrase变成光杆司令，删掉它！
		if (phrasalRootTree.isLeaf())
			TreeUtils.removeSubTree(phrasalRootTree, copiedRootTree);

		if (TreeUtils.matchPattern(phrasalRootTree, PP_MEANINGLESS))
			TreeUtils.removeSubTree(phrasalRootTree, copiedRootTree);
	}

	public static void main(String[] args) {
		String string = // "I've created a JTextArea to append the elements but
						// the results is 'loaded' instead of being listed
						// out.";
		// "I'm playing around with natural language parse trees, he want to
		// parse strings to trees, it's tree parsing.";
		// "He wants to put that into this box, I hope I can move these from
		// there to his room.";
		// ", he'll be a dancer, and he wouldn't said that his father might be a
		// farmer, his family will never be Chinese.";
		// "Android: how to get the ConnectionTimeOut Value set to a
		// HttpClient";
		// "Here is the code I'm using to create my multithreaded httpclient
		// object. I'm trying to have a 4 second timeout across the board so if
		// nothing happens for 4 seconds to drop the requests. ";
		"Well,I think you should go with the 1 st Approach by using Front Controller pattern. It should consist of only a SINGLE SERVLET which provides a centralized entry point for all requests.This servlet will delegate all request to the required servlet. You need to do only following thing to apply the front controller pattern in your application:";

		SentenceInfo sentence = new SentenceInfo(string);
		SentenceParser.parseGrammaticalTree(sentence);
		sentence.getGrammaticalTree().pennPrint();
		PhraseExtractor.extractVerbPhrases(sentence);
		PhraseExtractor.extractNounPhrases(sentence);

		for (PhraseInfo phrase : sentence.getPhrases()) {

			long t1 = System.currentTimeMillis();
			PhraseFilter.filter(phrase, sentence);
			long t2 = System.currentTimeMillis();
			// System.out.println("TIME: " + (t2 - t1) + "ms");
			if (phrase.getProofTotalScore() > Proof.MIN) {
				phrase.getStemmedTree().pennPrint();
				System.out.println(phrase.getProofString());
				Tree trunk = new TrunkExtractor(phrase).extractVP(TrunkExtractor.KERNAL);
				if (trunk != null) {
					System.out.println("====kernal===");
					trunk.pennPrint();
				}
				trunk = new TrunkExtractor(phrase).extractVP(TrunkExtractor.TRUNK);
				if (trunk != null) {
					System.out.println("====trunk===");
					trunk.pennPrint();
				}

				trunk = new TrunkExtractor(phrase).extractVP(TrunkExtractor.SKELETON);
				if (trunk != null) {
					System.out.println(" ====skeleton===");
					trunk.pennPrint();
				}

				System.out.println("=============================");
			}
		}

		// tree.printLocalTree();
		//
		// System.out.println(tree.getLeaves());
		// System.out.println(tree.nodeString());
		// System.out.println(tree.score());
		// System.out.println(tree.value());
		// System.out.println(tree.constituents());
		// System.out.println(tree.toString());
	}

	@Deprecated
	public static PhraseInfo extractPhraseTrunk(PhraseInfo phrase) {
		if (phrase == null)
			return null;
		PhraseInfo trunk = new PhraseInfo();
		Tree tree = phrase.getStemmedTree().deepCopy();
		if (phrase.hasProof(ProofType.INIT_EXTRACTION_VP)) {
			boolean verbMatched = false;
			boolean npMatched = false;

			// 抽取VP的主干
			if (phrase.hasProof(ProofType.PASS_VP_NP_FORM)) {
				List<Tree> firstLevelChildren = tree.getChildrenAsList();
				for (int i = 0; i < firstLevelChildren.size(); i++) {
					Tree child = firstLevelChildren.get(i);
					String label = child.label().toString();
					if (label.startsWith("VB") && !verbMatched) {
						verbMatched = true;
					}
					else if (label.equals("NP") && !npMatched) {
						npMatched = true;
						// HashMap<Integer, Tree> levelChildMap = new
						// HashMap<Integer, Tree>();
						processNP_old(child);
					}
					else {
						tree.removeChild(i);
						// i--;
					}

				}
			}
			else if (phrase.hasProof(ProofType.PASS_VP_PP_FORM)
					|| phrase.hasProof(ProofType.PASS_VP_DT_PP_FORM)
					|| phrase.hasProof(ProofType.PASS_VP_PRP_PP_FORM)) {
				boolean ppMatched = false;

				List<Tree> firstLevelChildren = tree.getChildrenAsList();
				for (int i = 0; i < firstLevelChildren.size(); i++) {
					Tree child = firstLevelChildren.get(i);
					String childLabel = child.label().toString();
					if (childLabel.startsWith("VB") && !verbMatched) {
						verbMatched = true;
					}
					else if (childLabel.equals("PP") && !ppMatched) {
						ppMatched = true;
						List<Tree> secondLevelChildren = child.getChildrenAsList();
						for (int j = 0; j < secondLevelChildren.size(); j++) {
							Tree grandChild = secondLevelChildren.get(j);
							String grandChildLabel = grandChild.label().toString();
							if (grandChildLabel.equals("NP") && !npMatched) {
								npMatched = true;
								processNP_old(grandChild);
							}
							else if (!grandChildLabel.equals("IN")
									&& !grandChildLabel.equals("TO")) {
								child.remove(i);
							}
						}
					}
					else {
						tree.removeChild(i);
						// i--;
					}

				}
			}

		}
		else if (phrase.hasProof(ProofType.INIT_EXTRACTION_NP)) {
			// 抽取NP的主干
		}
		trunk.setTree(tree);
		return trunk;
	}

	@Deprecated
	private static int processNP_old(Tree tree) {
		if (tree == null || !tree.label().toString().equals("NP"))
			return -1;
		boolean nounOccured = false;
		int stopLevel = 0;

		List<Tree> children = tree.getChildrenAsList();
		for (int i = 0; i < children.size(); i++) {
			Tree child = children.get(i);

			if (child.label().toString().startsWith("NN")) {
				nounOccured = true;
				break;
			}
		}

		if (nounOccured) {
			stopLevel = 1;
			for (int i = 0; i < children.size(); i++) {
				Tree child = children.get(i);
				if (!child.label().toString().startsWith("NN")) {
					tree.removeChild(i);
				}
			}
		}
		else {
			int deepestLevel = 0;
			for (int i = 0; i < children.size(); i++) {
				Tree child = children.get(i);
				if (!child.label().toString().equals("NP")) {
					tree.removeChild(i);
				}
				else {
					int returnedLevel = processNP_old(child);
					if (returnedLevel > deepestLevel)
						deepestLevel++;
				}
			}
			stopLevel += deepestLevel;
		}
		System.out.println("HH" + stopLevel);
		return stopLevel;
	}
}
