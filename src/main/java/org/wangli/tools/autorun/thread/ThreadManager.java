package org.wangli.tools.autorun.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadManager {

	private static ExecutorService threadPool;

	/**
	 * 获取线程池
	 * 
	 * @return
	 */
	public static ExecutorService getThreadPool() {
		if (threadPool == null) {
			threadPool = Executors.newCachedThreadPool();
		}
		return threadPool;
	}

}
