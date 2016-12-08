package crawler.krs_pobierz_pl.profil;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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
//		int a=Integer.parseInt(System.getProperty("id"));
//		int b=Integer.parseInt(System.getProperty("threads"));
//		System.out.println("a="+a+", b="+b);
		Properties prop = new Properties();
		InputStream input = null;
		int numberOfThread = 99;
		int idHost=99;

		try {

			input = new FileInputStream("c:\\config.properties");

			// load a properties file
			prop.load(input);

			// get the property value and print it out
			System.out.println("idHost ="+prop.getProperty("idHost"));
			idHost=Integer.parseInt(prop.getProperty("idHost"));
			System.out.println("numberOfThreads ="+prop.getProperty("numberOfThreads"));
			numberOfThread = Integer.parseInt(prop.getProperty("numberOfThreads"));
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		
//		int numberOfThread = Integer.parseInt(System.getProperty("threads"));
//		int idHost=Integer.parseInt(System.getProperty("id"));
		ProfilThread[] threads = new ProfilThread[numberOfThread];
		for(int i=0;i<numberOfThread;i++){
			threads[i]=new ProfilThread(i, idHost);
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
