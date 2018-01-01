package cn.edu.pku.sei.tsr.dragon.email.mbox.crawler.thread;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

import org.apache.log4j.Logger;

/**
 * @Title: URLManager.java
 * @Package cn.edu.pku.EOS.crawler.thread
 * @Description: url management
 * @author jinyong jinyonghorse@hotmail.com
 * @date 2013-6-2 2:22:07
 */

public class URLManager {

	protected Logger		logger	= Logger.getLogger(URLManager.class.getName());

	private Queue<String>	waitingQueue;
	private Queue<String>	runningQueue;
	private Queue<String>	visitedQueue;
	private Queue<String>	unreachableQueue;

	public URLManager() {
		waitingQueue = new LinkedBlockingDeque<String>();
		runningQueue = new LinkedBlockingDeque<String>();
		visitedQueue = new LinkedBlockingDeque<String>();
		unreachableQueue = new LinkedBlockingDeque<String>();
	}

	public synchronized String getURLFromWaitingQueue() {
		if (waitingQueue.isEmpty()) {
			return null;
		}
		logger.info("get one url: " + waitingQueue.peek() + " from the url waiting queue.");
		return waitingQueue.poll();
	}

	public synchronized void insertWaitingQueue(String url) {
		waitingQueue.add(url);
	}

	public synchronized void insertRunningQueue(String url) {
		runningQueue.add(url);
	}

	public synchronized void insertVisitedQueue(String url) {
		visitedQueue.add(url);
	}

	public synchronized void insertUnreachableQueue(String url) {
		unreachableQueue.add(url);
	}

	public Queue<String> getWaitingQueue() {
		return waitingQueue;
	}

	public void setWaitingQueue(Queue<String> waitingQueue) {
		this.waitingQueue = waitingQueue;
	}

	public Queue<String> getRunningQueue() {
		return runningQueue;
	}

	public void setRunningQueue(Queue<String> runningQueue) {
		this.runningQueue = runningQueue;
	}

	public Queue<String> getVisitedQueue() {
		return visitedQueue;
	}

	public void setVisitedQueue(Queue<String> visitedQueue) {
		this.visitedQueue = visitedQueue;
	}

	public Queue<String> getUnreachableQueue() {
		return unreachableQueue;
	}

	public void setUnreachableQueue(Queue<String> unreachableQueue) {
		this.unreachableQueue = unreachableQueue;
	}

}
