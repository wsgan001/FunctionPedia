package cn.edu.pku.sei.tsr.dragon.test;

import java.io.File;

import org.apache.log4j.Logger;

import cn.edu.pku.sei.tsr.dragon.content.entity.ContentInfo;
import cn.edu.pku.sei.tsr.dragon.content.entity.ParagraphInfo;
import cn.edu.pku.sei.tsr.dragon.content.entity.PhraseInfo;
import cn.edu.pku.sei.tsr.dragon.content.entity.SentenceInfo;
import cn.edu.pku.sei.tsr.dragon.content.entity.VerbalPhraseInfo;
import cn.edu.pku.sei.tsr.dragon.experiment.APILibrary;
import cn.edu.pku.sei.tsr.dragon.nlp.entity.VerbalPhraseStructureInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.parser.Proof;
import cn.edu.pku.sei.tsr.dragon.utils.ObjectIO;

public class ForPaper {
	public static final Logger logger = Logger.getLogger(ForPaper.class);
	public static void main(String[] args) {
		File dataObjDirectory = ObjectIO.getDataObjDirectory(ObjectIO.CONTENT_STRUCTURED_LIBRARY);
		File[] subdirs = dataObjDirectory.listFiles();
		for (File file : subdirs) {
			if (file.getName().equals(APILibrary.POI))
				continue;
			if (file.isDirectory()) {
				File[] libContents = file.listFiles();
				for (File contentFile : libContents) {
					Object obj = ObjectIO.readObject(contentFile);
					if (obj != null && obj instanceof ContentInfo) {
						ContentInfo content = (ContentInfo) obj;

						try {
							// 很可能在某一次操作中，list并不包含子元素，或者出现nullPointer
							for (ParagraphInfo para : content.getParagraphList()) {
								for (SentenceInfo sentence : para.getSentences()) {
									for (PhraseInfo phrase : sentence.getPhrases()) {
										// 限制structure的数量，按照得分进行过滤, 20150819
										if (phrase.getProofTotalScore() >= Proof.MID) {
											if (phrase instanceof VerbalPhraseInfo) {
												VerbalPhraseInfo verbalPhrase = (VerbalPhraseInfo) phrase;
												VerbalPhraseStructureInfo vpstructure = verbalPhrase
														.getStructure();
												if (vpstructure != null) {
//													phrase.getStemmedTree().getChildrenAsList()
//															.stream().filter(x -> x.label()
//																	.toString().equals("PP"))
//															.forEach(y -> {
//																if(y.getChildrenAsList().stream()
//																		.filter(z -> z.label()
//																				.toString()
//																				.equals("S"))
//																		.collect(Collectors
//																				.toList()).size()>0)
//																	{System.err.println("==================");
//																	System.err.println("VPS:" + vpstructure);
//																	System.err.println(verbalPhrase.getTree()
//																			.pennString());
//																	System.err.println("Phrase:" + phrase);
//																	System.err.println("sentence:" + sentence);
//																	}
//																
//															});
													if (vpstructure.getSubPPList().size() == 1
															&& vpstructure.getSubNP() == null) {
														System.err.println("==================");
														System.err.println("VPS:" + vpstructure);
														System.err.println(verbalPhrase.getTree()
																.pennString());
														System.err.println("Phrase:" + phrase);
														System.err.println("sentence:" + sentence);
													}
												}
											}

										}
									}
								}
							}
						}
						catch (Exception e) {
						}
					}
				}
			}
		}
		System.err.println("Start to process contents and extract graphs...");

	}

}
