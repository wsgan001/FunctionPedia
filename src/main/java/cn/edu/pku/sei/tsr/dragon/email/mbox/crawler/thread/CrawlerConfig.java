package cn.edu.pku.sei.tsr.dragon.email.mbox.crawler.thread;

/**
 * @Title: CrawlerConfig.java
 * @Package cn.edu.pku.EOS.crawler.thread
 * @Description: config of the crawler
 * @author jinyong jinyonghorse@hotmail.com
 * @date 2013-6-3 10:11:59
 */

public class CrawlerConfig {

	/**
	 * delay between sending two requests to the same host.
	 */
	private int	politenessDelay			= 200;

	/**
	 * max connection for each host
	 */
	private int	maxConnectionPerHost	= 100;

	/**
	 * max connection num
	 */
	private int	maxTotalConnections		= 100;

	/**
	 * Socket timeout in milliseconds
	 */
	private int	socketTimeout			= 20000;

	/**
	 * Connection timeout in milliseconds
	 */
	private int	connectionTimeout		= 30000;

	public int getPolitenessDelay() {
		return politenessDelay;
	}

	public void setPolitenessDelay(int politenessDelay) {
		this.politenessDelay = politenessDelay;
	}

	public int getMaxConnectionPerHost() {
		return maxConnectionPerHost;
	}

	public void setMaxConnectionPerHost(int maxConnectionPerHost) {
		this.maxConnectionPerHost = maxConnectionPerHost;
	}

	public int getMaxTotalConnections() {
		return maxTotalConnections;
	}

	public void setMaxTotalConnections(int maxTotalConnections) {
		this.maxTotalConnections = maxTotalConnections;
	}

	public int getSocketTimeout() {
		return socketTimeout;
	}

	public void setSocketTimeout(int socketTimeout) {
		this.socketTimeout = socketTimeout;
	}

	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

}
