package cn.edu.pku.sei.tsr.dragon.email.mbox.crawler.thread;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.log4j.Logger;

import cn.edu.pku.sei.tsr.dragon.email.entity.Project;
import cn.edu.pku.sei.tsr.dragon.email.utils.URL2File;

public class MainCrawler implements Runnable {

	protected static final Logger	logger	= Logger.getLogger(MainCrawler.class.getName());

	private int						myID;

	private String					tempDir	= "D:\\Dragon Project\\mbox_file\\poi\\";

	private CrawlerController		controller;

	private Thread					myThread;

	private URLManager				urlManager;

	private boolean					isWaitingForNewURL;

	private Project					project;

	public void init(int myID, CrawlerController controller, Project project) {
		this.myID = myID;
		this.controller = controller;
		this.urlManager = controller.getUrlManager();
		this.isWaitingForNewURL = false;
		this.project = project;
		//this.tempDir = project.getMboxPath();
	}

	@Override
	public void run() {
		while (true) {
			this.isWaitingForNewURL = true;
			String assignedURL = this.urlManager.getURLFromWaitingQueue();
			if (assignedURL == null)
				return;
			this.isWaitingForNewURL = false;
			logger.info("now is process URL: " + assignedURL);
			processURL(assignedURL);
		}

	}

	private File downloadFromUrl(String url) {
		String tempPath = tempDir;
		logger.info("download url content to file path : " + tempPath);
		File tempFile = null;
		try {
			tempFile = URL2File.getFileFromUrl(url, tempDir);
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return tempFile;
	}

	public void processURL(String assignedURL) {
		long start = System.currentTimeMillis();

		downloadFromUrl(assignedURL);
		long end = System.currentTimeMillis();

		logger.info("Crawler time of URL : " + assignedURL + " is " + (start - end) + "milis");
	}

	public CrawlerController getController() {
		return controller;
	}

	public void setController(CrawlerController controller) {
		this.controller = controller;
	}

	public Thread getMyThread() {
		return myThread;
	}

	public void setMyThread(Thread myThread) {
		this.myThread = myThread;
	}

	public URLManager getUrlManager() {
		return urlManager;
	}

	public void setUrlManager(URLManager urlManager) {
		this.urlManager = urlManager;
	}

	public boolean isWaitingForNewURL() {
		return isWaitingForNewURL;
	}

	public void setWaitingForNewURL(boolean isWaitingForNewURL) {
		this.isWaitingForNewURL = isWaitingForNewURL;
	}

	public int getMyID() {
		return myID;
	}

	public void setMyID(int myID) {
		this.myID = myID;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}
}
