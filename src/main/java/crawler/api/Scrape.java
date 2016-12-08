package crawler.api;

import java.util.List;

import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public interface Scrape {
	public List<String> fetchUrlsToScrape();
	HtmlPage getPage(String url);

	Object parsing(HtmlPage page, Object mainProfil);
	
	Boolean insertData(Object objectToInsert);
	
	public void insertDataEntity(Object o);
	
	public <T> void insertDataListEntity(List<T> list);
	void supportFetchUrlsToScrape();
	void supportGetPage();
	void supportParsing();
	void supportInsertData();
	void mainProcess();
	void logger();

}
