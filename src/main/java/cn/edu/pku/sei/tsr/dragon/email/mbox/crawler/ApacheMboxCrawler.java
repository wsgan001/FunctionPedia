package cn.edu.pku.sei.tsr.dragon.email.mbox.crawler;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import cn.edu.pku.sei.tsr.dragon.email.entity.Project;
import cn.edu.pku.sei.tsr.dragon.email.mbox.crawler.thread.CrawlerConfig;
import cn.edu.pku.sei.tsr.dragon.email.mbox.crawler.thread.CrawlerController;
import cn.edu.pku.sei.tsr.dragon.email.mbox.crawler.thread.MainCrawler;
import cn.edu.pku.sei.tsr.dragon.email.mbox.crawler.thread.URLManager;
import cn.edu.pku.sei.tsr.dragon.email.utils.MBoxURLExtractor;

public class ApacheMboxCrawler extends Thread {

	protected static final Logger	logger			= Logger.getLogger(ApacheMboxCrawler.class
															.getName());

	protected CrawlerController		controller;
	protected CrawlerConfig			config;
	protected URLManager			urlManager		= new URLManager();
	private static final int		MAX_CRAWLER_NUM	= 20;

	private ArrayList<String>		urlList			= new ArrayList<String>();
	private Project					project			= new Project();

	public static int getMaxCrawlerNum() {
		return MAX_CRAWLER_NUM;
	}

	public ApacheMboxCrawler(Project project, ArrayList<String> urlList) {
		this.project = project;
		this.urlList = urlList;
		init();
	}

	public ApacheMboxCrawler() {
		super();
	}

	public void Crawl() {
		this.controller.start(new MainCrawler(), MAX_CRAWLER_NUM, false);
	}

	public static void main(String args[]) {
		//projectid=1  for Apache POI Lucene
		//projectid=2  for Apache tomcat
//		Project p = new Project();
//		p.setProjectID(1);
//		ArrayList<String> list = new ArrayList<String>();
//		list.add("http://mail-archives.apache.org/mod_mbox/lucene-java-user/");
		//list.add("http://mail-archives.apache.org/mod_mbox/lucene-lucene-net-user/");
		//list.add("http://mail-archives.apache.org/mod_mbox/lucene-solr-user/");
		
		//projectid=11  for Apache POI
		Project p = new Project();
		p.setProjectID(11);
		ArrayList<String> list = new ArrayList<String>();
		list.add("http://mail-archives.apache.org/mod_mbox/poi-user");


		ApacheMboxCrawler mailingListCrawler = new ApacheMboxCrawler(p, list);
		mailingListCrawler.Crawl();
	}

	public void init() {
		config = new CrawlerConfig();
		logger.info("Init the url waiting queue......");
		for (String url : urlList) {
			ArrayList<String> mboxURLList = new ArrayList<String>();
			mboxURLList = new MBoxURLExtractor(url).getMBoxURLList();
			logger.info("URL list size: " + mboxURLList.size());
			for (String mboxUrlString : mboxURLList) {
				logger.info("insert a task into list: " + mboxUrlString);
				urlManager.insertWaitingQueue(mboxUrlString);
			}
		}
		// urlManager.insertWaitingQueue("http://mail-archives.apache.org/mod_mbox/lucene-java-user/201309.mbox");
		controller = new CrawlerController(this, config, urlManager, project);
	}

}
