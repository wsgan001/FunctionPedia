package cn.edu.pku.sei.tsr.dragon.feature;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;

import cn.edu.pku.sei.tsr.dragon.experiment.APILibrary;
import cn.edu.pku.sei.tsr.dragon.graph.entity.GraphInfo;
import cn.edu.pku.sei.tsr.dragon.graph.graphofnodeinfo.GraphParser;
import cn.edu.pku.sei.tsr.dragon.nlp.entity.VerbalPhraseStructureInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.parser.Rules;
import cn.edu.pku.sei.tsr.dragon.utils.ObjectIO;

public class FrequentStructureFilter {
	public static final Logger logger = Logger.getLogger(FrequentStructureFilter.class);

	public static void main(String[] args) {
		// for (String libName : APILibrary.getSubjectLibraryNames())
		// filterGraphOfLibrary(libName);
		//filterGraphOfLibrary(APILibrary.POI);
		filterGraphOfLibrary(APILibrary.LUCENE);
	}

	@SuppressWarnings("unchecked")
	public static void filterGraphOfLibrary(String libraryName) {
		File freqMapFile = ObjectIO.getDataObjDirectory(
				ObjectIO.FREQUENT_SUBGRAPH_LIBRARY + File.separator + ObjectIO.FREQUENCY_MAP_PREFIX
						+ libraryName + ObjectIO.DAT_FILE_EXTENSION);
		Object mapObj = ObjectIO.readObject(freqMapFile);

		HashMap<String, Integer> graphUUIDToFrequencyMap;
		if (mapObj == null) {
			String mapPath = ObjectIO.FREQUENT_SUBGRAPH_LIBRARY + File.separator
					+ ObjectIO.FREQUENCY_FILE_PREFIX + libraryName + ObjectIO.DAT_FILE_EXTENSION;
			logger.info("FrequentMap file is invalid! Read from plain output <" + mapPath
					+ "> and rebuild maps....");
			graphUUIDToFrequencyMap = ObjectIO.readMapFromFile(mapPath);
		}
		else {
			graphUUIDToFrequencyMap = (HashMap<String, Integer>) mapObj;
		}

		if (graphUUIDToFrequencyMap == null || graphUUIDToFrequencyMap.isEmpty())
			return;

		logger.info("[" + libraryName + "] Load in frequent subgraph list...");
		long t0 = System.currentTimeMillis();

		File libfile = ObjectIO.getDataObjDirectory(ObjectIO.FREQUENT_SUBGRAPH_LIBRARY
				+ File.separator + ObjectIO.FREQUENCY_GRAPH_LIST_PREFIX + libraryName
				+ ObjectIO.DAT_FILE_EXTENSION);
		Object obj = ObjectIO.readObject(libfile);
		long t1 = System.currentTimeMillis();
		logger.info("Loaded subgraph list:" + (t1 - t0) + "ms");

		if (obj != null && obj instanceof List<?>) {
			List<GraphInfo> graphList = (List<GraphInfo>) obj;

			List<Pair<GraphInfo, VerbalPhraseStructureInfo>> graphVPSPairList = graphList.stream()
					.map(x -> new ImmutablePair<>(x, GraphParser.parseGraphToPhraseStructure(x)))
					.filter(x -> PhraseStructureAnalyzer.valiadateStructure(x.getRight()))
					.filter(x -> x.getLeft().getUuid() != null)
					.filter(x -> graphUUIDToFrequencyMap.get(x.getLeft().getUuid()) != null)
					.collect(Collectors.toList());

			List<Pair<VerbalPhraseStructureInfo, Integer>> result = graphVPSPairList.stream()
					.sorted((x, y) -> {
						int freqX = graphUUIDToFrequencyMap.get(x.getLeft().getUuid());
						int freqY = graphUUIDToFrequencyMap.get(y.getLeft().getUuid());
						return freqX - freqY;
					})
					.map(x -> new ImmutablePair<>(x.getRight(),
							graphUUIDToFrequencyMap.get(x.getLeft().getUuid())))
					.collect(Collectors.toList());

			PhraseStructureAnalyzer.removeSmallVPS(result);

			result = result.stream()
					.filter(x -> !Arrays.asList(x.getLeft().toNLText().split(" ")).stream()
							.anyMatch(
									y -> Rules.valuable_determiners.contains(y) || y.equals("and")))
					.collect(Collectors.toList());

			result.forEach(x -> {
				String path = ObjectIO.FEATURE_VERB_PHRASE_STRUCTURE + File.separator + libraryName
						+ File.separator + x.getLeft().getUuid() + ObjectIO.DAT_FILE_EXTENSION;
				ObjectIO.writeObject(x, path);
			});

			PhraseStructureAnalyzer.splitLayers(result, libraryName);

			for (Pair<VerbalPhraseStructureInfo, Integer> pair : result) {
				logger.info(pair.getRight() + "\t" + pair.getLeft().toNLText() + "\t"
						+ pair.getLeft().toString());
			}

			long t2 = System.currentTimeMillis();
			logger.info("Extracted " + result.size() + " valid phrases from " + graphList.size()
					+ " frequent subgraphs... " + (t2 - t1) + "ms");
		}
	}
}