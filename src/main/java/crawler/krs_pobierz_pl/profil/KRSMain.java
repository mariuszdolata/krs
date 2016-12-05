package crawler.krs_pobierz_pl.profil;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import crawler.api.DatabaseConfig;

public class KRSMain {

	public static void main(String[] args) {
		System.out.println("START HtmlUnit");

//
//		ApplicationContext context = new AnnotationConfigApplicationContext(DatabaseConfig.class);
//		ProfilRepository profilRepository = context.getBean("profilRepository", ProfilRepository.class);
//		List<String> urlsToScrape = new ArrayList<String>();
//		do {
//			urlsToScrape.clear();
//			urlsToScrape = profilRepository.fetchUrlToScrape();
//			String idHost = "0";
//			for (int i = 0; i < urlsToScrape.size(); i++) {
//				System.out.println("urlToScrape=" + urlsToScrape.get(i));
//				GetProfile getProfile = new GetProfile(urlsToScrape.get(i), idHost);
//				System.gc();
//			}
//		} while (!urlsToScrape.isEmpty());
		
		int numberOfThread = 5;
		ProfilThread[] threads = new ProfilThread[numberOfThread];
		for(int i=0;i<numberOfThread;i++){
			threads[i]=new ProfilThread(i);
		}
		Thread[] thr = new Thread[numberOfThread];
		for(int i=0;i<numberOfThread;i++){
			thr[i]=new Thread(threads[i]);
		}
		for(int i=0;i<numberOfThread;i++){
			thr[i].start();
		}
		
		
		

		// System.out.println("test");
		// KRSProfil krsprofil=new KRSProfil();

	}

}
