package crawler.bisnode_pl.profil;

import java.util.Properties;

import crawler.api.MainCrawler;

public class ProfilRepository extends MainCrawler implements Runnable {

	public ProfilRepository(Properties properties, int thread){
		this.setIdHost(Integer.parseInt(properties.getProperty("idHost")));
		this.setNumberOfThreads(thread);
		this.setDelayMiliSec(Integer.parseInt(properties.getProperty("delay")));
		this.setThreadPriority(Integer.parseInt(properties.getProperty("priority")));
		
	}
	public void run() {
		//while - pobiera liste adresow url oraz wywoluje getProfile dla okreslonego profilu
		String urlTemp="http://www.bisnode.pl/firma/?id=1926254&nazwa=ORGANIKA_CZ%C4%98_STOCHOWA_SP_Z_O_O";
		GetProfile getProfile = new GetProfile(urlTemp);

	}

}
