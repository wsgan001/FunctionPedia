package cn.edu.pku.sei.tsr.dragon.email.mbox.crawler.thread;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import cn.edu.pku.sei.tsr.dragon.email.entity.Project;
import cn.edu.pku.sei.tsr.dragon.email.mbox.crawler.ApacheMboxCrawler;

/**
 * @Title: CrawlerController.java
 * @Package cn.edu.pku.EOS.crawler.thread
 * @Description: class of the crawler controler
 * @author jinyong jinyonghorse@hotmail.com
 * @date 2013-6-3 10:41:26
 */

public class CrawlerController extends Configurable {

	protected static final Logger	logger		= Logger.getLogger(CrawlerController.class
														.getName());

	protected ApacheMboxCrawler		fatherThread;

	protected boolean				finished;

	protected boolean				shutdown;

	protected URLManager			urlManager;

	public final Object				waitingLock	= new Object();

	protected Project				project;

	public CrawlerController(ApacheMboxCrawler fatherThread, CrawlerConfig config,
			URLManager urlManager, Project project) {
		super(config);
		this.urlManager = urlManager;
		this.project = project;
		this.fatherThread = fatherThread;

		finished = false;

		shutdown = false;
	}

	public void start(MainCrawler c, int numOfCrawlers, boolean isBlocking) {
		try {
			finished = false;
			final List<Thread> threads = new ArrayList<Thread>();
			final List<MainCrawler> crawlers = new ArrayList<MainCrawler>();

			for (int i = 1; i <= numOfCrawlers; i++) {
				MainCrawler crawler = new MainCrawler();
				Thread thread = new Thread(crawler, "Crawler " + i);
				crawler.setMyThread(thread);
				crawler.init(i, this, project);
				thread.start();
				crawlers.add(crawler);
				threads.add(thread);
			}

			final CrawlerController controller = this;

			Thread monitorThread = new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						synchronized (waitingLock) {

							while (true) {
								sleep(3);
								boolean someoneIsWorking = false;
								for (int i = 0; i < threads.size(); i++) {
									Thread thread = threads.get(i);
									if (!thread.isAlive()) {
										if (!shutdown) {
											MainCrawler crawler = new MainCrawler();
											thread = new Thread(crawler, "Crawler " + (i + 1));
											threads.remove(i);
											threads.add(i, thread);
											crawler.setMyThread(thread);
											crawler.init(i + 1, controller, project);
											thread.start();
											crawlers.remove(i);
											crawlers.add(i, crawler);
										}
									}
									else if (!crawlers.get(i).isWaitingForNewURL()) {
										someoneIsWorking = true;
									}
								}
								if (!someoneIsWorking) {
									// Make sure again that none of the threads
									// are
									// alive.
									sleep(3);

									someoneIsWorking = false;
									for (int i = 0; i < threads.size(); i++) {
										Thread thread = threads.get(i);
										if (thread.isAlive()
												&& !crawlers.get(i).isWaitingForNewURL()) {
											someoneIsWorking = true;
										}
									}
									if (!someoneIsWorking) {
										if (!shutdown) {
											if (!(urlManager.getWaitingQueue().size() == 0))
												continue;
										}

										// At this step, frontier notifies the
										// threads that were
										// waiting for new URLs and they should
										// stop
										logger.info("All task finished! Call father thread's finish function");
										// fatherThread.finish();

										finished = true;
										waitingLock.notifyAll();
										return;
									}
								}
							}
						}
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			monitorThread.start();

			if (isBlocking) {
				waitUntilFinish();
			}

		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Wait until this crawling session finishes.
	 */
	public void waitUntilFinish() {
		while (!finished) {
			synchronized (waitingLock) {
				if (finished) {
					return;
				}
				try {
					waitingLock.wait();
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	protected void sleep(int seconds) {
		try {
			Thread.sleep(seconds * 1000);
		}
		catch (Exception ignored) {
		}
	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	public boolean isShutdown() {
		return shutdown;
	}

	public void setShutdown(boolean shutdown) {
		this.shutdown = shutdown;
	}

	public URLManager getUrlManager() {
		return urlManager;
	}

	public void setUrlManager(URLManager urlManager) {
		this.urlManager = urlManager;
	}
}
