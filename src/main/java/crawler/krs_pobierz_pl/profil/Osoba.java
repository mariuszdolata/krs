package crawler.krs_pobierz_pl.profil;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Osoba {
	@Id
	@GeneratedValue
	private long id;

	private String imie;
	private String nazwisko;
	private String stanowisko;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getImie() {
		return imie;
	}
	public void setImie(String imie) {
		this.imie = imie;
	}
	public String getNazwisko() {
		return nazwisko;
	}
	public void setNazwisko(String nazwisko) {
		this.nazwisko = nazwisko;
	}
	public String getStanowisko() {
		return stanowisko;
	}
	public void setStanowisko(String stanowisko) {
		this.stanowisko = stanowisko;
	}
	public Osoba(String imie, String nazwisko, String stanowisko) {
		super();
		this.imie = imie;
		this.nazwisko = nazwisko;
		this.stanowisko = stanowisko;
	}

	

}
