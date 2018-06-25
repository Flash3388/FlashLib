package edu.flash3388.flashlib.communications.controller;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import edu.flash3388.flashlib.communications.connection.Connector;

public class CommunicationController {

	private static final int THREAD_POOL_SIZE = 2;
	
	private Connector mConnector;
	private ExecutorService mExecutorService;
	private Future<?> mCommunicationTaskFuture;
	
	public CommunicationController(Connector connector) {
		mConnector = connector;
		mExecutorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
		mCommunicationTaskFuture = null;
	}
	
	public void start() {
		if (mCommunicationTaskFuture != null) {
			return;
		}
		
		
	}
	
	public void stop() {
		if (mCommunicationTaskFuture == null) {
			return;
		}
		
		try {
			mConnector.close();
		} catch (IOException e) {
		}
		
		mExecutorService.shutdownNow();
		
		try {
			while(!mExecutorService.awaitTermination(500, TimeUnit.MILLISECONDS));
		} catch (InterruptedException e) {
		}
	}
}
