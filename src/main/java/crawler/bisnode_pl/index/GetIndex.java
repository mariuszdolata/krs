package crawler.bisnode_pl.index;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import com.gargoylesoftware.htmlunit.html.HtmlTableDataCell;
import com.mysql.cj.api.jdbc.Statement;

import crawler.api.Scrape;

public class GetIndex implements Scrape {
	private String urlToScrape;
	private HtmlPage currentPage;
	private List<IndexBisNode> indexList;
	private Properties properties;
	public int numberOfCompanies;
	private String DB_DRIVER;
	private String DB_URL;
	private String DB_USER;
	private String DB_PASSWORD;
	private Connection conn = null;
	private Statement stmt = null;
	private ResultSet rs = null;

	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public String getDB_DRIVER() {
		return DB_DRIVER;
	}

	public void setDB_DRIVER(String dB_DRIVER) {
		DB_DRIVER = dB_DRIVER;
	}

	public String getDB_URL() {
		return DB_URL;
	}

	public void setDB_URL(String dB_URL) {
		DB_URL = dB_URL;
	}

	public String getDB_USER() {
		return DB_USER;
	}

	public void setDB_USER(String dB_USER) {
		DB_USER = dB_USER;
	}

	public String getDB_PASSWORD() {
		return DB_PASSWORD;
	}

	public void setDB_PASSWORD(String dB_PASSWORD) {
		DB_PASSWORD = dB_PASSWORD;
	}

	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	public Statement getStmt() {
		return stmt;
	}

	public void setStmt(Statement stmt) {
		this.stmt = stmt;
	}

	public ResultSet getRs() {
		return rs;
	}

	public void setRs(ResultSet rs) {
		this.rs = rs;
	}

	public String getUrlToScrape() {
		return urlToScrape;
	}

	public void setUrlToScrape(String urlToScrape) {
		this.urlToScrape = urlToScrape;
	}

	public HtmlPage getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(HtmlPage currentPage) {
		this.currentPage = currentPage;
	}

	public List<IndexBisNode> getIndexList() {
		return indexList;
	}

	public void setIndexList(List<IndexBisNode> indexList) {
		this.indexList = indexList;
	}

	public int getNumberOfCompanies() {
		return numberOfCompanies;
	}

	public void setNumberOfCompanies(int numberOfCompanies) {
		this.numberOfCompanies = numberOfCompanies;
	}

	/**
	 * Podczas tworzenia obiektu wykonywane jest pobranie strony o podanym
	 * adresie, sparsowanie, stworzenie odpowiedniego obiektu oraz zapisanie do
	 * bazy danych. Jeden obiekt GetIndex to jedna podstona indexu zawieraj¹ca
	 * 20 firm
	 * 
	 * @param urlToScrape
	 * @param properties
	 */
	public GetIndex(String urlToScrape, Properties properties) {
		this.setProperties(properties);
		this.DB_DRIVER = "com.mysql.cj.jdbc.Driver";
		this.DB_URL = "jdbc:mysql://" + properties.getProperty("serverName") + "/"
				+ properties.getProperty("databaseName") + properties.getProperty("databaseProp");
		this.DB_USER = properties.getProperty("user");
		this.DB_PASSWORD = properties.getProperty("password");
		try {
			Class.forName(DB_DRIVER);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			this.conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
			this.stmt = (Statement) conn.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.urlToScrape = urlToScrape;
		try {
			this.currentPage = this.getPage(urlToScrape);
			// jeœli strona wczytana nie jest pusta
			if (this.currentPage != null) {
				this.indexList = (List<IndexBisNode>) this.parsing(this.currentPage, new IndexBisNode());
				insertDataListEntity(this.getIndexList());
			} else {
				System.err.println("strona indexu jest pusta");
			}
		} catch (Exception e) {
			System.err.println("M: Nie uda³o siê pobraæ lub zapisaæ do bazy strony " + this.urlToScrape);
			e.printStackTrace();
		}
	}

	public List<String> fetchUrlsToScrape() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Pobranie strony z serwera z wy³¹czeniem javascript
	 */
	public HtmlPage getPage(String url) {
		WebClient client = new WebClient();
		client.getOptions().setThrowExceptionOnScriptError(false);
		client.getOptions().setJavaScriptEnabled(false);
		try {
			return client.getPage(urlToScrape);
		} catch (FailingHttpStatusCodeException e) {
			e.printStackTrace();
			return null;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Parsowanie strony www, tworzenie obiektu List<IndexBisNode> i
	 * umieszczenie w nim danych
	 */
	public Object parsing(HtmlPage page, Object mainProfil) {
		System.out.println("Wejœcie w metodê parsing dla indexu");
		HtmlSpan liczbaFirm = (HtmlSpan) page
				.getByXPath("//span[@style=\"color: #fff; font-size: 20px; float: left; font-weight: bold;\"]").get(0);
		int spacePosition = liczbaFirm.asText().indexOf(" ");
		String liczbaFirmTmp = liczbaFirm.asText().substring(0, spacePosition);
		this.setNumberOfCompanies(Integer.parseInt(liczbaFirmTmp));
		// System.out.println("liczba firm = "+liczbaFirm.asText()+"
		// int="+liczba);
		List<IndexBisNode> firmy = new ArrayList<IndexBisNode>();
		List<HtmlDivision> nazwy = (List<HtmlDivision>) page.getByXPath("//tr/td[2]/a/div");
		List<HtmlAnchor> urls = (List<HtmlAnchor>) page.getByXPath("//tr/td[2]/a");
		List<HtmlTableDataCell> dane = (List<HtmlTableDataCell>) page.getByXPath("//tr/td[2]");
		// Stworzenie listy dla indexu
		for (int i = 0; i < nazwy.size(); i++) {
			IndexBisNode index = new IndexBisNode();
			index.setNazwa(nazwy.get(i).asText());
			index.setUrl("http://www.bisnode.pl" + urls.get(i).getHrefAttribute());
			String[] allData = dane.get(i).asText().split("\n");
			if (allData.length >= 2)
				index.setAdres(allData[1]);
			if (allData.length >= 3) {
				int krsPos = allData[2].indexOf("KRS");
				try {
					String krs = allData[2].substring(krsPos + 5);
					index.setKrs(krs);
				} catch (Exception e) {
					System.err.println("brak krs");
				}
				try {
					String nip = allData[2].substring(5, krsPos);
					index.setNip(nip);
				} catch (Exception e) {
					System.err.println("brak nipu");
				}
			}
			index.setHostId(String.valueOf(properties.get("idHost")));
			index.setThreadId(String.valueOf(properties.get("numberOfThreads")));
			index.setMeta(this.getUrlToScrape());
			firmy.add(index);
		}
		return firmy;
	}

	/**
	 * Wstawianie firm do tabeli INDEX (multiinsert) metoda niezalecana
	 */
	public Boolean insertData(Object objectToInsert) {
		List<IndexBisNode> firmy = (List<IndexBisNode>) objectToInsert;
		StringBuilder sqlInsert = new StringBuilder("INSERT INTO `index` (nazwa, url, adres, krs, nip, meta) VALUES ");
		for (IndexBisNode firma : firmy) {
			sqlInsert.append("(\"" + firma.getNazwa() + "\",\"" + firma.getUrl() + "\",\"" + firma.getAdres() + "\",\""
					+ firma.getKrs() + "\",\"" + firma.getNip() + "\",\"" + this.urlToScrape + "\"),");
		}
		String sql = sqlInsert.substring(0, sqlInsert.length() - 1);
		System.out.println(sql);
		try {
			stmt.executeUpdate(sql);
			return true;
		} catch (SQLException e) {
			System.out.println("Nie uda³o siê zapisaæ danych do tabeli INDEX");
			e.printStackTrace();
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see crawler.bisnode_pl.index.Scrape#insertDataEntity(java.lang.Object)
	 */
	public void insertDataEntity(Object o) {
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("bisnode_pl");
		EntityManager entityManager = entityManagerFactory.createEntityManager();

		entityManager.getTransaction().begin();
		entityManager.persist(o);
		entityManager.getTransaction().commit();

		entityManager.close();
		entityManagerFactory.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see crawler.bisnode_pl.index.Scrape#insertDataListEntity(java.util.List)
	 */
	public <T> void insertDataListEntity(List<T> list) {
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("bisnode_pl");
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();
		for (T object : list)
			entityManager.persist(object);
		entityManager.getTransaction().commit();
		entityManager.close();
		entityManagerFactory.close();
	}

	public void supportFetchUrlsToScrape() {
		// TODO Auto-generated method stub

	}

	public void supportGetPage() {
		// TODO Auto-generated method stub

	}

	public void supportParsing() {
		// TODO Auto-generated method stub

	}

	public void supportInsertData() {
		// TODO Auto-generated method stub

	}

	public void mainProcess() {
		// TODO Auto-generated method stub

	}

	public void logger() {
		// TODO Auto-generated method stub

	}

}
