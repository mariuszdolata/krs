package crawler.api;

import java.util.ArrayList;
import java.util.List;

public class MainCrawler {
	private int numberOfThreads;
	private int threadPriority;
	private int delayMiliSec;
	private int idHost;
	private List<String> urlsToScrape;

	public List<String> getUrlsToScrape() {
		return urlsToScrape;
	}

	public void setUrlsToScrape(List<String> urlsToScrape) {
		this.urlsToScrape = urlsToScrape;
	}

	public int getNumberOfThreads() {
		return numberOfThreads;
	}

	public void setNumberOfThreads(int numberOfThreads) {
		this.numberOfThreads = numberOfThreads;
	}

	public int getDelayMiliSec() {
		return delayMiliSec;
	}

	public void setDelayMiliSec(int delayMiliSec) {
		this.delayMiliSec = delayMiliSec;
	}

	public int getIdHost() {
		return idHost;
	}

	public void setIdHost(int idHost) {
		this.idHost = idHost;
	}

	public int getThreadPriority() {
		return threadPriority;
	}

	public void setThreadPriority(int threadPriority) {
		this.threadPriority = threadPriority;
	}

	public MainCrawler() {
			
	}

	

}
