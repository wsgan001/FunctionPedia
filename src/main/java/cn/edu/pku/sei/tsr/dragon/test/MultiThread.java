package cn.edu.pku.sei.tsr.dragon.test;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import cn.edu.pku.sei.tsr.dragon.utils.MonitorThread;
import cn.edu.pku.sei.tsr.dragon.utils.nlp.StanfordParser;
import edu.stanford.nlp.trees.Tree;

public class MultiThread implements Callable<Tree> {
	private String	str;

	public MultiThread(String str) {
		this.str = str;
	}

	@Override
	public Tree call() throws Exception {
		Thread monitor = new Thread(new MonitorThread());
		monitor.start();

		Tree tr = StanfordParser.parseTree(str);
		monitor.interrupt();
		
//		long t1 = System.currentTimeMillis();
//		while (monitor.isAlive()) {
//			System.out.println(monitor.isAlive());
//		}
//		System.out.println(monitor.isAlive());
//		long t2 = System.currentTimeMillis();
//		System.out.println(t2 - t1 + "ms");
		
		return tr;
	}

	public static Tree parse(String str) {
		// String str = "test whether this works:";
		Tree tr = null;
		try {
			MultiThread mt = new MultiThread(str);
			FutureTask<Tree> futureTask = new FutureTask<Tree>(mt);
			Thread thread = new Thread(futureTask);
			thread.start();

//			Runtime rt = Runtime.getRuntime();
//			long mf = rt.freeMemory();
//			long mtl = rt.totalMemory();
//			long mu = mtl - mf;
//			long mm = rt.maxMemory();
//			System.out.println("used:" + mu + "  free:" + mf + "  total:" + mtl + "  max:" + mm);
			// while (true) {
			// OldThreadInfo newThreadInfo = new OldThreadInfo();
			// SOThread.sleep(180);

			tr = futureTask.get();

//			mf = rt.freeMemory();
//			mtl = rt.totalMemory();
//			mu = mtl - mf;
//			mm = rt.maxMemory();
//			System.out.println("used:" + mu + "  free:" + mf + "  total:" + mtl + "  max:" + mm);
			// }

		}
		catch (Exception e) {
			System.err.println("miaomiaomiao");
			System.err.println(e.toString());
		}
		return tr;
	}

	public static void main(String args[]) {
		String str = "Sep 17, 2009 10:13:53 AM org.apache.solr.common.SolrException log SEVERE: "
				+ "com.ctc.wstx.exc.WstxEOFException: Unexpected end of input block in end tag at [row,col {unknown-source}]: "
				+ "[26,1266] at com.ctc.wstx.sr.StreamScanner.throwUnexpectedEOB(StreamScanner.java:700) at com.ctc.wstx.sr."
				+ "StreamScanner.loadMoreFromCurrent(StreamScanner.java:1054) at com.ctc.wstxsr.StreamScanner.getNextCharFromCurrent"
				+ "(StreamScanner.java:811) at com.ctc.wstx.sr.BasicStreamReader.readEndElem(BasicStreamReader.java:3211) "
				+ "at com.ctc.wstx.sr.BasicStreamReader.nextFromTree(BasicStreamReader.java:2832) at com.ctc.wstx.sr."
				+ "BasicStreamReader.next(BasicStreamReader.java:1019) at org.apache.solr.handler.XmlUpdateRequestHandler."
				+ "processUpdate(XmlUpdateRequestHandler.java:148) at org.apache.solr.handler.XmlUpdateRequestHandler."
				+ "handleRequestBody(XmlUpdateRequestHandler.java:123) at org.apache.solr.handler.RequestHandlerBase."
				+ "handleRequest(RequestHandlerBase.java:131) at org.apache.solr.core.SolrCore.execute(SolrCore.java:1204)"
				+ " at org.apache.solr.servlet.SolrDispatchFilter.execute(SolrDispatchFilter.java:303) at org.apache.solr."
				+ "servlet.SolrDispatchFilter.doFilter(SolrDispatchFilter.java:232) at org.apache.catalina.core.ApplicationFilterChain"
				+ ".internalDoFilter(ApplicationFilterChain.java:235) at org.apache.catalina.core.ApplicationFilterChain."
				+ "doFilter(ApplicationFilterChain.java:206) at org.apache.catalina.core.StandardWrapperValve.invoke"
				+ "(StandardWrapperValve.java:233) at org.apache.catalina.core.StandardContextValve.invoke"
				+ "(StandardContextValve.java:191) at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:128)"
				+ " at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:102) at org.apache."
				+ "catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:109) at org.apache.catalina.connector."
				+ "CoyoteAdapter.service(CoyoteAdapter.java:293) at org.apache.coyote.http11.Http11AprProcessor.process"
				+ "(Http11AprProcessor.java:859) at org.apache.coyote.http11.Http11AprProtocol$Htt";
		String str2 = "You may also be interested in the output of the explain()"
				+ " method, which will give you an idea of how things are scored(that's not \"enough\": ) the way they are:";
		long t1 = System.currentTimeMillis();
		Tree tr = StanfordParser.parseTree(str);
		long t2 = System.currentTimeMillis();
		System.out.println(t2 - t1 + "ms");
		if (tr != null) {
			System.out.println("========");
			System.out.println(tr);
		}
		else
			System.err.println(tr);
	}

}
