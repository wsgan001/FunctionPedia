package cn.edu.pku.sei.tsr.dragon.email.mbox.crawler.thread;

/**
 * @Title: Configurable.java
 * @Package cn.edu.pku.EOS.crawler.thread
 * @Description: config
 * @author jinyong jinyonghorse@hotmail.com
 * @date 2013-6-3 10:16:33
 */

public class Configurable {

	protected CrawlerConfig	config;

	public CrawlerConfig getConfig() {
		return config;
	}

	public void setConfig(CrawlerConfig config) {
		this.config = config;
	}

	public Configurable(CrawlerConfig config) {
		this.config = config;
	}
}
