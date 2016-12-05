package crawler.krs_pobierz_pl.profil;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

@Entity
public class Profil {
	@Id
	@GeneratedValue
	private long id;

	private String nazwa;
	private String krs;
	private String nip;
	private String regon;
	private String kapitalZakladowy;
	private String formaPrawna;
	private String adresLinia1;
	private String adresLinia2;
	private String wojewodztwo;
	private String dataRejestracjiKrs;
	private String ostatniaZmianaKrs;
	private String reprezentacja;
	private String sposobReprezentacji;
	private String sad;
	private String sygnatura;
	private String przewazajacaDzialalnoscGospodarcza;
	private String website;
	private String email;
	private String ostatniaAktualizacjaDanych;
	private String meta;
	private String idHost;
	private String idThread;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "company_id")
	private List<Osoba> osoby = new ArrayList<Osoba>();

	
	public String getIdThread() {
		return idThread;
	}

	public void setIdThread(String idThread) {
		this.idThread = idThread;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNazwa() {
		return nazwa;
	}

	public void setNazwa(String nazwa) {
		this.nazwa = nazwa;
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

	public String getRegon() {
		return regon;
	}

	public void setRegon(String regon) {
		this.regon = regon;
	}

	public String getKapitalZakladowy() {
		return kapitalZakladowy;
	}

	public void setKapitalZakladowy(String kapitalZakladowy) {
		this.kapitalZakladowy = kapitalZakladowy;
	}

	public String getFormaPrawna() {
		return formaPrawna;
	}

	public void setFormaPrawna(String formaPrawna) {
		this.formaPrawna = formaPrawna;
	}

	public String getAdresLinia1() {
		return adresLinia1;
	}

	public void setAdresLinia1(String adresLinia1) {
		this.adresLinia1 = adresLinia1;
	}

	public String getAdresLinia2() {
		return adresLinia2;
	}

	public void setAdresLinia2(String adresLinia2) {
		this.adresLinia2 = adresLinia2;
	}

	public String getWojewodztwo() {
		return wojewodztwo;
	}

	public void setWojewodztwo(String wojewodztwo) {
		this.wojewodztwo = wojewodztwo;
	}

	public String getDataRejestracjiKrs() {
		return dataRejestracjiKrs;
	}

	public void setDataRejestracjiKrs(String dataRejestracjiKrs) {
		this.dataRejestracjiKrs = dataRejestracjiKrs;
	}

	public String getOstatniaZmianaKrs() {
		return ostatniaZmianaKrs;
	}

	public void setOstatniaZmianaKrs(String ostatniaZmianaKrs) {
		this.ostatniaZmianaKrs = ostatniaZmianaKrs;
	}

	public String getReprezentacja() {
		return reprezentacja;
	}

	public void setReprezentacja(String reprezentacja) {
		this.reprezentacja = reprezentacja;
	}

	public String getSposobReprezentacji() {
		return sposobReprezentacji;
	}

	public void setSposobReprezentacji(String sposobReprezentacji) {
		this.sposobReprezentacji = sposobReprezentacji;
	}

	public String getSad() {
		return sad;
	}

	public void setSad(String sad) {
		this.sad = sad;
	}

	public String getSygnatura() {
		return sygnatura;
	}

	public void setSygnatura(String sygnatura) {
		this.sygnatura = sygnatura;
	}

	public String getPrzewazajacaDzialalnoscGospodarcza() {
		return przewazajacaDzialalnoscGospodarcza;
	}

	public void setPrzewazajacaDzialalnoscGospodarcza(String przewazajacaDzialalnoscGospodarcza) {
		this.przewazajacaDzialalnoscGospodarcza = przewazajacaDzialalnoscGospodarcza;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<Osoba> getOsoby() {
		return osoby;
	}

	public void setOsoby(List<Osoba> osoby) {
		this.osoby = osoby;
	}

	public String getOstatniaAktualizacjaDanych() {
		return ostatniaAktualizacjaDanych;
	}

	public void setOstatniaAktualizacjaDanych(String ostatniaAktualizacjaDanych) {
		this.ostatniaAktualizacjaDanych = ostatniaAktualizacjaDanych;
	}

	public String getMeta() {
		return meta;
	}

	public void setMeta(String meta) {
		this.meta = meta;
	}

	public String getIdHost() {
		return idHost;
	}

	public void setIdHost(String idHost) {
		this.idHost = idHost;
	}
	
	

}