package crawler.bisnode_pl.index;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class IndexBisNode {
	@Id
	@GeneratedValue
	private long id;
	private String nazwa;
	private String url;
	private String adres;
	private String krs;
	private String nip;
	private String meta;
	private String threadId;
	private String hostId;
	
	
	public String getMeta() {
		return meta;
	}
	public void setMeta(String meta) {
		this.meta = meta;
	}
	
	public String getThreadId() {
		return threadId;
	}
	public void setThreadId(String threadId) {
		this.threadId = threadId;
	}
	public String getHostId() {
		return hostId;
	}
	public void setHostId(String hostId) {
		this.hostId = hostId;
	}
	public String getNazwa() {
		return nazwa;
	}
	public void setNazwa(String nazwa) {
		this.nazwa = nazwa;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getAdres() {
		return adres;
	}
	public void setAdres(String adres) {
		this.adres = adres;
	}
	public String getKrs() {
		return krs;
	}
	public void setKrs(String krs) {
		this.krs = krs;
	}
	public String getNip() {
		return nip;
	}
	public void setNip(String nip) {
		this.nip = nip;
	}
	public IndexBisNode(String nazwa) {
		super();
		this.nazwa = nazwa;
	}
	public IndexBisNode() {
		super();
	}
	@Override
	public String toString() {
		return "IndexBisNode [nazwa=" + nazwa + ", url=" + url + ", adres=" + adres + ", krs=" + krs + ", nip=" + nip
				+ "]";
	}
	
	

}
