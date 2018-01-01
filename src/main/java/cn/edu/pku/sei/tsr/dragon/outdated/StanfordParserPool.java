package cn.edu.pku.sei.tsr.dragon.outdated;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import edu.stanford.nlp.trees.Tree;

@Deprecated
public class StanfordParserPool {
	public static final Logger logger = Logger.getLogger(StanfordParserPool.class);

	private static final int	TIME_UNIT				= 10;
	private static final int	ROUNDROBIN_TIME_LIMIT	= 600000;	// 600s
	private static final int	PARSER_LIMIT			= 32;

	private static List<StanfordParserCaller>	parserPool;
	private static int							currentIndex	= 0;

	static {
		parserPool = new ArrayList<>();
		for (int i = 0; i < PARSER_LIMIT; i++) {
			parserPool.add(new StanfordParserCaller());
		}
	}

	private static StanfordParserCaller getAvailableParser() throws InterruptedException {
		int initIndex = currentIndex;
		int localIndex = currentIndex;
		long timeInit = System.currentTimeMillis();
		while (true) {
			StanfordParserCaller parser = parserPool.get(localIndex);
			if (!parser.isLocked()) {
				parser.setLocked(true);
				if (parser.isAvailable()) {
					parser.setAvailable(false);
					// System.err.println("StanfordParser:" + localIndex);
					currentIndex = (localIndex + 1) % PARSER_LIMIT;
					return parser;
				}
				parser.setLocked(false);
			}
			localIndex = (localIndex + 1) % PARSER_LIMIT;
			if (localIndex == initIndex)
				Thread.sleep(TIME_UNIT);
			if (System.currentTimeMillis() - timeInit > ROUNDROBIN_TIME_LIMIT)
				return null;
		}
	}

	public static Tree parseGrammaticalTree(String str) {
		StanfordParserCaller parser = null;
		try {
			parser = getAvailableParser();
			if (parser == null) {
				logger.info("Fail to gain an available StanfordParser caller...");
				return null;
			}

			Tree tree = parser.parseTree(str);
			return tree;
		}
		catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		}
		finally {
			if (parser != null) {
				parser.setAvailable(true);
				parser.setLocked(false);
			}
		}

	}
}
