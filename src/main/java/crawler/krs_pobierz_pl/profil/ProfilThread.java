package crawler.krs_pobierz_pl.profil;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import crawler.api.DatabaseConfig;

public class ProfilThread implements Runnable{
	private int number;
	

	public ProfilThread(int number) {
		super();
		this.number = number;
	}


	public void run() {
		ApplicationContext context = new AnnotationConfigApplicationContext(DatabaseConfig.class);
		ProfilRepository profilRepository = context.getBean("profilRepository", ProfilRepository.class);
		List<String> urlsToScrape = new ArrayList<String>();
		do {
			urlsToScrape.clear();
			urlsToScrape = profilRepository.fetchUrlToScrape();
			String idHost = "5";
			String idThread= String.valueOf(this.number);
			for (int i = 0; i < urlsToScrape.size(); i++) {
				System.out.println("urlToScrape=" + urlsToScrape.get(i));
				GetProfile getProfile = new GetProfile(urlsToScrape.get(i), idHost, idThread);
				System.gc();
			}
		} while (!urlsToScrape.isEmpty());
		
	}

}
