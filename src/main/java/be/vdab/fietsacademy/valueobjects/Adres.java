package be.vdab.fietsacademy.valueobjects;

import java.io.Serializable;

import javax.persistence.Embeddable;

// @Embeddable staat voor een value object class
@Embeddable		
public class Adres implements Serializable {
	private static final long serialVersionUID = 1L;
	/*
	 * Je kan bij de variabelen FINAL tikken als je geen JPA gebruikt.
	 * De compiler controleert dat dat je de variabele enkel bij zijn declaratie of in de constructor invult.
	 * JPA werkt echter NIET samen met final variabelen.
	 */
	private String straat;
	private String huisNr;
	private String postcode;
	private String gemeente;
	
	// een geparametriseerde constructor
	public Adres(String straat, String huisNr, String postcode, String gemeente) {
		this.straat = straat;
		this.huisNr = huisNr;
		this.postcode = postcode;
		this.gemeente = gemeente;
	}
	
	// een protected default constructor
	/*
	 * Een value object class moet bij JPA een default constructor hebben.
	 * Voor JPA volstaat het deze constructor protected te maken.
	 */
	protected Adres() {
	}
	
	// getters voor straat, huisNr, postcode en gemeente
	public String getStraat() {
		return straat;
	}
	public String getHuisNr() {
		return huisNr;
	}
	public String getPostcode() {
		return postcode;
	}
	public String getGemeente() {
		return gemeente;
	}
}
