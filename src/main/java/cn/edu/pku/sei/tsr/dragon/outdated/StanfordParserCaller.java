package cn.edu.pku.sei.tsr.dragon.outdated;

import java.io.StringReader;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import org.apache.log4j.Logger;

import cn.edu.pku.sei.tsr.dragon.utils.Config;
import cn.edu.pku.sei.tsr.dragon.utils.MonitorThread;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.RuntimeInterruptedException;

@Deprecated
// 20150814
public class StanfordParserCaller implements Callable<Tree> {
	public static final Logger logger = Logger.getLogger(StanfordParserCaller.class);

	public LexicalizedParser			lexicalizedParser;
	public TokenizerFactory<CoreLabel>	tokenizerFactory;

	private String	strToParse;
	private Tree	parsedTree	= null;

	private boolean	isAvailable	= true;
	private boolean	locked		= false;

	public StanfordParserCaller() {
		super();
		lexicalizedParser = LexicalizedParser.loadModel(Config.getLexicalModelFile());
		tokenizerFactory = PTBTokenizer.factory(new CoreLabelTokenFactory(), "");
	}

	public StanfordParserCaller(String strToParse) {
		this();
		setStrToParse(strToParse);
	}

	// 两重锁的控制都移到调用该类的地方进行，不在内部加减锁
	public Tree parseTree(String str) {
		// 标志该次使用结束
		// setAvailable(false);
		// setLocked(true);
		// StanfordParserCaller parser = new StanfordParserCaller(str);
		strToParse = str;
		parsedTree = null;

		FutureTask<Tree> parserTask = new FutureTask<Tree>(this);
		ThreadGroup stanfordParserThreadGroup = new ThreadGroup(
				Thread.currentThread().getThreadGroup(), "Stanford-Parser-TG");
		Thread parserThread = new Thread(stanfordParserThreadGroup, parserTask);

		parserThread.start();

		try {
			Tree parsedTree = parserTask.get();
			return parsedTree;
		}
		catch (Exception e) {
			return null;
		}
		finally {
			// strToParse = null;
			// parsedTree = null;
			// setAvailable(true);
			// setLocked(false);
		}
	}

	// Callable, 会被FutureTask调用
	@Override
	public Tree call() throws Exception {
		MonitorThread monitor = new MonitorThread();
		monitor.start();

		this.parseTree();

		monitor.interrupt();

		return parsedTree;
	}

	private void parseTree() {
		try {
			long t1 = System.currentTimeMillis();
			List<CoreLabel> rawWords = tokenizerFactory.getTokenizer(new StringReader(strToParse))
					.tokenize();
			long t2 = System.currentTimeMillis();
			this.parsedTree = lexicalizedParser.apply(rawWords);
			long t3 = System.currentTimeMillis();
			// System.err.println("getTokenizer:" + (t2 - t1) + "ms. lexicalizedParser:" + (t3 - t2)
			// + "ms");

		}
		catch (RuntimeInterruptedException e) {

		}
	}

	public Tree getParsedTree() {
		return parsedTree;
	}

	public String getStrToParse() {
		return strToParse;
	}

	public void setStrToParse(String strToParse) {
		this.strToParse = strToParse;
	}

	public synchronized boolean isAvailable() {
		return isAvailable;
	}

	public synchronized void setAvailable(boolean isAvailable) {
		this.isAvailable = isAvailable;
	}

	public synchronized boolean isLocked() {
		return locked;
	}

	public synchronized void setLocked(boolean locked) {
		this.locked = locked;
	}

}
