package cn.edu.pku.sei.tsr.dragon.utils;

import org.apache.log4j.Logger;

public class MonitorThread extends Thread {
	public static final Logger	logger			= Logger.getLogger(MonitorThread.class);

	public static final long	TIME_OUT		= 600000;									
	public static final long	TIME_UNIT		= 60000;									
	public static final long	MEMORY_LIMIT	= 100000000000L;								

	public MonitorThread() {
		super();
		this.setDaemon(true);
	}

	@Override
	public void run() {
		Runtime runtime = Runtime.getRuntime();
		long startTime = System.currentTimeMillis();
		long memoryInitUsed = runtime.totalMemory() - runtime.freeMemory();
		try {
			while (true) {
				sleep(TIME_UNIT);

				long memoryCurrUsed = runtime.totalMemory() - runtime.freeMemory();
				long memoryIncreased = memoryCurrUsed - memoryInitUsed;
				long currTime = System.currentTimeMillis();

				boolean memoryLimitBroken = memoryIncreased > MEMORY_LIMIT;
				boolean timeLimitBroken = currTime - startTime > TIME_OUT;

				// System.out.println("Memory Limit:" + memoryLimitBroken +
				// " Time Limit:" + timeLimitBroken);
				if (memoryLimitBroken) {

					System.err.println("[MEMORY LIMIT BROKEN!!] Increased Memory:"
							+ ((float) (memoryIncreased / 1000) / 1000) + "MB; Curr Memory Used:"
							+ ((float) (memoryCurrUsed / 1000) / 1000) + "MB.");
					// System.gc();
					// memoryCurrUsed = runtime.totalMemory() -
					// runtime.freeMemory();
					// System.out.println("After gc() Memory Used:" +
					// (memoryCurrUsed / 1000) + "KB.");
				}
				if (timeLimitBroken) {
					System.err.println("[TIME LIMIT BROKEN!!] Time Used:" + (currTime - startTime) + "ms.");
				}

				if (memoryLimitBroken || timeLimitBroken);
					Thread.currentThread().getThreadGroup().interrupt();
			}
		}
		catch (InterruptedException e) {
			// System.err.println(e.getMessage());
		}
	}

}
