package crawler.krs_pobierz_pl.profil;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class GetProfile {
	private String urlToScrape;
	private String idHost;

	public String getUrlToScrape() {
		return urlToScrape;
	}

	public void setUrlToScrape(String urlToScrape) {
		this.urlToScrape = urlToScrape;
	}

	public GetProfile(String urlToScrape, String idHost, String idThread) {

		this.idHost = idHost;
		this.urlToScrape = urlToScrape;
		Profil mainProfil = new Profil();
		mainProfil.setIdHost(this.getIdHost());
		mainProfil.setIdThread(idThread);
		try {
			WebClient client = new WebClient();
			HtmlPage mainPage = client.getPage(urlToScrape);
			String htmlCode = mainPage.asText();
			DomElement contactData = mainPage.getElementById("contactData");
			DomElement basicDataTable = mainPage.getElementById("basicDataTable");
			DomElement pageHeader = mainPage.getFirstByXPath("//div[@class=\"page-header\"]");
			List<Object> osoby = (List<Object>) mainPage
					.getByXPath("//tr[@itemtype=\"http://data-vocabulary.org/Person\"]");
			
			String [] header;
			String[] basicDataTableLines;
			String[] contactDataLines;
			try{
				 header = pageHeader.asText().split("\n");
				 basicDataTableLines = basicDataTable.asText().split("\n");
				 contactDataLines = contactData.asText().split("\n");
			}catch(Exception e){
				System.err.println("M:prawdopodobnie nie pobralo kodu zrodlowego dla strony "+urlToScrape);
				header = new String[1];
				header[0]="";
				 basicDataTableLines = new String[1];
				basicDataTableLines[0]="";
				contactDataLines = new String[1];
				contactDataLines[0]="";
			}
			
			for (int i = 0; i < contactDataLines.length; i++) {
				if (contactDataLines[i].contains("Adres strony WWW:"))
					mainProfil.setWebsite(contactDataLines[i].substring(18));
				if (contactDataLines[i].contains("Adres email:	"))
					mainProfil.setEmail(contactDataLines[i].substring(13));
			}
			for (int i = 0; i < header.length; i++) {
				System.out.println("Header(" + i + ")" + header[i]);
			}
			mainProfil.setMeta(urlToScrape);
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
					mainProfil.getOsoby()
							.add(new Osoba(imieNazwisko[0], imieNazwisko[1], osobyRole.get(iter).asText()));
				} else {
					mainProfil.getOsoby()
							.add(new Osoba("", osobyNode.get(iter).asText(), osobyRole.get(iter).asText()));
				}
			}
			if (mainProfil.getNazwa().length() > 2) {
				EntityManagerFactory entityManagerFactory = Persistence
						.createEntityManagerFactory("crawling_krs_pobierz_pl_profil");
				EntityManager entityManager = entityManagerFactory.createEntityManager();
				entityManager.getTransaction().begin();
				entityManager.persist(mainProfil);
				entityManager.getTransaction().commit();
				entityManager.close();
				entityManagerFactory.close();
			}
			// System.out.println(elementStr);
		} catch (Exception e) {
			System.err.println("M: problem z wczytaniem strony");
			e.printStackTrace();
		}
		// nie ³¹czy siê z baz¹ w przypadku NULL
		
	}

	public String getIdHost() {
		return idHost;
	}

	public void setIdHost(String idHost) {
		this.idHost = idHost;
	}

}
