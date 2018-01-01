package cn.edu.pku.sei.tsr.dragon.feature;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;

import cn.edu.pku.sei.tsr.dragon.content.entity.VerbalPhraseInfo;
import cn.edu.pku.sei.tsr.dragon.experiment.APILibrary;
import cn.edu.pku.sei.tsr.dragon.feature.entity.FeatureInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.entity.VerbalPhraseStructureInfo;
import cn.edu.pku.sei.tsr.dragon.utils.ObjectIO;

public class CodeMatcher {
	public static final Logger logger = Logger.getLogger(CodeMatcher.class);

	public static void main(String[] args) {

		for (String libName : APILibrary.getSubjectLibraryNames()){
			logger.info("Match code for: "+libName+"....");
			matchLibrary(libName);
		}
//		matchLibrary(APILibrary.POI);
	}

	public static void matchLibrary(String lib) {
		List<VerbalPhraseStructureInfo> featureVPS = new ArrayList<>();
		File featureDir = ObjectIO.getDataObjDirectory(ObjectIO.FEATURE_VERB_PHRASE_STRUCTURE + File.separator + lib);
		for (File file : featureDir.listFiles()) {
			Object obj = ObjectIO.readObject(file);
			Pair<VerbalPhraseStructureInfo, Integer> p = (Pair<VerbalPhraseStructureInfo, Integer>)obj;
			featureVPS.add(p.getLeft());
		}
		
		logger.info("features load end");

		Map<VerbalPhraseStructureInfo, FeatureInfo> map = new HashMap<>();

		File commentDir = ObjectIO.getDataCodeObjDirectory(ObjectIO.CONTENT_STRUCTURED_LIBRARY + File.separator + lib);
		ObjectIO.readContentsFromDir(commentDir).forEach(x -> {
			x.getParagraphList().forEach(y -> {
				y.getSentences().forEach(z -> {
					z.getPhrases().forEach(w -> {
						if (w instanceof VerbalPhraseInfo) {
							VerbalPhraseInfo vp = (VerbalPhraseInfo) w;
							VerbalPhraseStructureInfo vps = vp.getStructure();
							featureVPS.forEach(f -> {
								float score = FeatureLinker.calculateSimilarityToA(vps, f);
								if (score >= 0.25) {
									FeatureInfo info;
									if (!map.containsKey(f)) {
										info = new FeatureInfo();
										map.put(f, info);
									} else {
										info = map.get(f);
									}
									info.addUuid(x.getParentUuid(), score);
								}
							});
						}
					});
				});
			});
		});
		File matcher = ObjectIO.getDataObjDirectory(ObjectIO.CODE_MATCHER + File.separator + lib + ObjectIO.DAT_FILE_EXTENSION);
		try {
			PrintStream ps = new PrintStream(matcher);
			map.forEach((k, v) -> {
				ps.println(k.getUuid());
				v.getCodeInfo(lib).forEach(x -> {
					ps.print("\t");
					ps.println(x);
				});
			});
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}
}
