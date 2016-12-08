package crawler.krs_pobierz_pl.profil;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import crawler.api.DatabaseConfig;
import crawler.api.MainCrawler;
import crawler.api.Scrape;

public class KRSProfil extends MainCrawler implements Scrape {
	private String currentUrl;
	private List<String> urlsToScrape = new ArrayList<String>();
	private HtmlPage currentPage;
	

	public String getCurrentUrl() {
		return currentUrl;
	}

	public void setCurrentUrl(String currentUrl) {
		this.currentUrl = currentUrl;
	}

	public HtmlPage getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(HtmlPage currentPage) {
		this.currentPage = currentPage;
	}

	public KRSProfil() {
		super();
		this.start();
	}

	public List<String> fetchUrlsToScrape() {
		ApplicationContext context = new AnnotationConfigApplicationContext(DatabaseConfig.class);
		ProfilRepository profilRepository = context.getBean("profilRepository", ProfilRepository.class);
		List<String> urlsToScrape = new ArrayList<String>();
		return urlsToScrape;
	}

	public HtmlPage getPage(String url) {
		this.currentUrl = url;
		try {
			WebClient client = new WebClient();
			HtmlPage page = client.getPage(url);
			return page;

		} catch (Exception e) {
			System.err.println("M: nie uda³o siê pobraæ strony o adresie: " + url);
			e.printStackTrace();
			return null;
		}

	}

	public Object parsing(HtmlPage mainPage) {
		Profil mainProfil = new Profil();
		String htmlCode = mainPage.asText();
		DomElement contactData = mainPage.getElementById("contactData");
		DomElement basicDataTable = mainPage.getElementById("basicDataTable");
		DomElement pageHeader = mainPage.getFirstByXPath("//div[@class=\"page-header\"]");
		List<Object> osoby = (List<Object>) mainPage
				.getByXPath("//tr[@itemtype=\"http://data-vocabulary.org/Person\"]");
		String[] header = pageHeader.asText().split("\n");
		String[] basicDataTableLines = basicDataTable.asText().split("\n");
		String[] contactDataLines = contactData.asText().split("\n");
		for (int i = 0; i < contactDataLines.length; i++) {
			if (contactDataLines[i].contains("Adres strony WWW:"))
				mainProfil.setWebsite(contactDataLines[i].substring(18));
			if (contactDataLines[i].contains("Adres email:	"))
				mainProfil.setEmail(contactDataLines[i].substring(13));
		}
		for (int i = 0; i < header.length; i++) {
			System.out.println("Header(" + i + ")" + header[i]);
		}
		mainProfil.setMeta(this.currentUrl);
		mainProfil.setNazwa(header[0]);
		mainProfil.setOstatniaAktualizacjaDanych(header[1]);
		for (int i = 0; i < basicDataTableLines.length; i++) {
			System.out.println(basicDataTableLines[i] + " <<<");

			if (basicDataTableLines[i].contains("KRS:	"))
				mainProfil.setKrs(basicDataTableLines[i].substring(5));
			if (basicDataTableLines[i].contains("NIP:	"))
				mainProfil.setNip(basicDataTableLines[i].substring(5));
			if (basicDataTableLines[i].contains("Regon:	"))
				mainProfil.setRegon(basicDataTableLines[i].substring(6));
			if (basicDataTableLines[i].contains("Kapita³ zak³adowy:	"))
				mainProfil.setKapitalZakladowy(basicDataTableLines[i].substring(18));
			if (basicDataTableLines[i].contains("Forma prawna:	"))
				mainProfil.setFormaPrawna(basicDataTableLines[i].substring(13));
			if (basicDataTableLines[i].contains("Adres:	 ")) {
				mainProfil.setAdresLinia1(basicDataTableLines[i].substring(7));
				mainProfil.setAdresLinia2(basicDataTableLines[i + 1]);
				mainProfil.setWojewodztwo(basicDataTableLines[i + 2]);
			}
			if (basicDataTableLines[i].contains("Data rejestracji KRS	"))
				mainProfil.setDataRejestracjiKrs(basicDataTableLines[i].substring(21));
			if (basicDataTableLines[i].contains("Ostatnia zmiana w KRS	"))
				mainProfil.setOstatniaZmianaKrs(basicDataTableLines[i].substring(22));
			if (basicDataTableLines[i].contains("Reprezentacja	"))
				mainProfil.setReprezentacja(basicDataTableLines[i].substring(13));
			if (basicDataTableLines[i].contains("Sposób reprezentacji	"))
				mainProfil.setSposobReprezentacji(basicDataTableLines[i].substring(20));
			if (basicDataTableLines[i].contains("S¹d	"))
				mainProfil.setSad(basicDataTableLines[i].substring(4));
			if (basicDataTableLines[i].contains("Sygnatura	"))
				mainProfil.setSygnatura(basicDataTableLines[i].substring(10));
			if (basicDataTableLines[i].contains("Przewa¿aj¹ca dzia³alnoœæ gospodarcza	"))
				mainProfil.setPrzewazajacaDzialalnoscGospodarcza(basicDataTableLines[i].substring(37));
			System.out.println(mainPage.asText());

			System.out.println("Koniec");

		}
		List<DomNode> osobyNode = (List<DomNode>) mainPage.getByXPath("//th[@itemprop=\"name\"]");
		List<DomNode> osobyRole = (List<DomNode>) mainPage.getByXPath("//div[@itemprop=\"role\"]");
		List<DomNode> osobyAffil = (List<DomNode>) mainPage.getByXPath("//td/span[@style=\"display:none\"]");
		for (int iter = 0; iter < osobyNode.size(); iter++) {
			System.out.println("OsobyNode.size()=" + osobyNode.size());
			System.out.println("osoba= " + osobyNode.get(iter).asText() + ", stanowisko= "
					+ osobyRole.get(iter).asText() + ", affil= " + osobyAffil.get(iter).asText());
			// String danePersonalne=
			String[] imieNazwisko = osobyNode.get(iter).asText().toString().split(" ");
			if (imieNazwisko.length >= 2) {
				mainProfil.getOsoby().add(new Osoba(imieNazwisko[0], imieNazwisko[1], osobyRole.get(iter).asText()));
			} else {
				mainProfil.getOsoby().add(new Osoba("", osobyNode.get(iter).asText(), osobyRole.get(iter).asText()));
			}
		}
		return mainProfil;
	}

	public Boolean insertData(Object objectToInsert) {
		EntityManagerFactory entityManagerFactory = Persistence
				.createEntityManagerFactory("crawling_krs_pobierz_pl_profil");
		EntityManager entityManager = entityManagerFactory.createEntityManager();

		entityManager.getTransaction().begin();
		entityManager.persist(objectToInsert);
		entityManager.getTransaction().commit();

		entityManager.close();
		entityManagerFactory.close();
		return true;
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

	public Object parsing(HtmlPage page, Object mainProfil) {
		// TODO Auto-generated method stub
		return null;
	}

	public void start() {

		do {
			this.urlsToScrape.clear();
			this.urlsToScrape = this.fetchUrlsToScrape();
			String idHost = "0";
			for (int i = 0; i < this.urlsToScrape.size(); i++) {
				System.out.println("urlToScrape=" + this.urlsToScrape.get(i));
				this.setCurrentPage(currentPage);
				Profil profil = (Profil)this.parsing(this.currentPage);
				if(this.insertData(profil)) System.out.println("zapisano obiekt");
				else System.out.println("Nie zapisano obiektu");
//				GetProfile getProfile = new GetProfile(this.urlsToScrape.get(i), idHost);
				System.gc();
			}
		} while (!this.urlsToScrape.isEmpty());
	}

	public void insertDataEntity(Object o) {
		// TODO Auto-generated method stub
		
	}

	public <T> void insertDataListEntity(List<T> list) {
		// TODO Auto-generated method stub
		
	}

}
