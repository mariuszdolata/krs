package crawler.bisnode_pl.profil;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

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

import crawler.api.Scrape;

public class GetProfile implements Scrape {

	private String urlToScrape;
	private HtmlPage currentPage;
	private ProfilBisNode profil;
	
	
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
	public ProfilBisNode getProfil() {
		return profil;
	}
	public void setProfil(ProfilBisNode profil) {
		this.profil = profil;
	}
	public GetProfile(String urlToScrape){
		System.out.println("Konstruktor GetProfile dla url="+urlToScrape);
		this.urlToScrape=urlToScrape;
		try{
			this.currentPage = this.getPage(urlToScrape);
//			System.out.println(this.getCurrentPage().asXml());
			//jeœli strona wczytana nie jest pusta
			if(this.currentPage!=null){
				this.profil = (ProfilBisNode) this.parsing(this.currentPage, new ProfilBisNode());
//				if(this.insertData(this.profil))System.out.println("Profil zapisany");
//				else System.err.println("Nie udalo sie zapisac profilu");
				insertDataEntity(this.getProfil());
			}	
		}catch(Exception e){
			System.err.println("M: Nie uda³o siê pobraæ strony "+this.urlToScrape);
			e.printStackTrace();
		}
	}
	public List<String> fetchUrlsToScrape() {
		// TODO Auto-generated method stub
		return null;
	}

	public HtmlPage getPage(String url) {
		WebClient client = new WebClient();
//		client.getOptions().setThrowExceptionOnScriptError(false);
		client.getOptions().setJavaScriptEnabled(false);
		try {
			return client.getPage(url);
		} catch (FailingHttpStatusCodeException e) {
			System.err.println("FailingHttpStatusException<<<<<<<<<<<<<<");
			e.printStackTrace();
			return null;
		} catch (MalformedURLException e) {
			System.err.println("MalformedURLException<<<<<<<<<<<<<<");
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
	}

	public Object parsing(HtmlPage page, Object mainProfil) {
		System.out.println("this.parsing() - START");
		ProfilBisNode profil = new ProfilBisNode();
		System.out.println(page.asXml());
		//nazwa
    	List<HtmlSpan> nazwy = (List<HtmlSpan>) page.getByXPath("//span[@itemprop='legalName']");
     	for(HtmlSpan nazwa:nazwy){profil.setNazwa(nazwa.asText());}
     	//dzialalnosc
    	List<HtmlSpan> dzialalnosci = (List<HtmlSpan>) page.getByXPath("//span[@itemprop='description']");
     	for(HtmlSpan dzialalnosc:dzialalnosci){profil.setDzialalnosc(dzialalnosc.asText());}
     	//data rozpoczecia dzialalnosci
    	List<HtmlSpan> daty = (List<HtmlSpan>) page.getByXPath("//span[@itemprop='foundingDate']");
     	for(HtmlSpan data:daty){profil.setDataRozpoczeciaDzialalnosci(data.asText());}
		//email
		List<HtmlSpan> emails = (List<HtmlSpan>) page.getByXPath("//span[@itemprop='email']");
     	for(HtmlSpan email:emails){profil.setEmail(email.asText());}
     	//website
    	List<HtmlSpan> websites = (List<HtmlSpan>) page.getByXPath("//span[@itemprop='url']");
     	for(HtmlSpan website:websites){profil.setWebsite(website.asText());}
     	//nip
    	List<HtmlSpan> nipy = (List<HtmlSpan>) page.getByXPath("//span[@itemprop='vatID']");
     	for(HtmlSpan nip:nipy){profil.setNip(nip.asText());}
     	//regon
    	List<HtmlSpan> regony = (List<HtmlSpan>) page.getByXPath("//span[@itemprop='taxID']");
     	for(HtmlSpan regon:regony){profil.setRegon(regon.asText());}
     	//lokalizacja
    	List<HtmlSpan> lokalizacje = (List<HtmlSpan>) page.getByXPath("//span[@itemprop='address']");
     	for(HtmlSpan lokalizacja:lokalizacje){profil.setLokalizacja(lokalizacja.asText());}
     	//telefon
    	List<HtmlSpan> telefony = (List<HtmlSpan>) page.getByXPath("//span[@itemprop='telephone']");
     	for(HtmlSpan telefon:telefony){profil.setPhone(telefon.asText());}
     	List<HtmlDivision> opisy = (List<HtmlDivision>) page.getByXPath("//div[@itemtype='http://schema.org/LocalBusiness']");
     	for(HtmlDivision opis:opisy){profil.setOpis(opis.asText());}
     	String opis = profil.getOpis();
     	String keyKRS="KRS";
     	try{
     		int krsPosition = opis.indexOf(keyKRS);
         	profil.setKrs(opis.substring(krsPosition+4, krsPosition+14));
     	}catch(Exception e){
     		System.err.println("Nie znaleziono numeru KRS");
     		profil.setKrs("NULL");
     	}
     	
     	     	
     	//forma prawna
     	try{
     		String formaPrawnaKey="Forma prawna firmy";
         	int formaPrawna = opis.indexOf(formaPrawnaKey);
         	String formaPrawnaS=opis.substring(formaPrawna+19);
         	System.err.println(formaPrawnaS);
         	int toPosition=formaPrawnaS.indexOf("to");
         	int dotPosition = formaPrawnaS.indexOf(".");
    	    profil.setFormaPrawna(formaPrawnaS.substring(toPosition+3, dotPosition));
     	}catch(Exception e){
     		System.out.println("Nie uda³o wydzieliæ siê formy prawnej");
     		profil.setFormaPrawna("NULL");
     	}
     	
	    System.out.println(profil.toString());
	    //DUNS
	    Object object = page.getByXPath("//a[@id='linkduns']").get(0);
	    try {
	    	HtmlAnchor dunsAnchor = page.getAnchorByText("Poka¿ numer DUNS");
			HtmlPage pageDuns = dunsAnchor.click();
			HtmlTableDataCell duns = (HtmlTableDataCell) pageDuns.getByXPath("//td[@id=\"duns\"]").get(0);
			profil.setDuns(duns.asText());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    
	    profil.setMeta(this.urlToScrape);
		return profil;
	}

	public Boolean insertData(Object objectToInsert) {
		
		try{
			EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("bisnode_pl");
			EntityManager entityManager = entityManagerFactory.createEntityManager();
			System.out.println("utworzono entityManagera");
			entityManager.getTransaction().begin();
			crawler.bisnode_pl.profil.ProfilBisNode profilTemp=(crawler.bisnode_pl.profil.ProfilBisNode) objectToInsert;
			entityManager.persist(profilTemp);
			entityManager.getTransaction().commit();
			System.out.println("zapisano  entityManagera");
			entityManager.close();
			entityManagerFactory.close();
			return true;
		}catch(Exception e){
			System.err.println("M: nie mozna zapisac obiektu do tabeli");
			e.printStackTrace();
			return false;
		}
		
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
	public void insertDataEntity(Object o) {
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("bisnode_pl");
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		
		entityManager.getTransaction().begin();
		entityManager.persist(o);
		entityManager.getTransaction().commit();
		
		entityManager.close();
		entityManagerFactory.close();
		
	}
	public <T> void insertDataListEntity(List<T> list) {
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("bisnode_pl");
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		
		entityManager.getTransaction().begin();
		for(T object:list)entityManager.persist(object);
		entityManager.getTransaction().commit();
		
		entityManager.close();
		entityManagerFactory.close();
		
	}
	

}
