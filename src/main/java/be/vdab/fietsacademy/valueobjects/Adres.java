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
	
	// "Equals en hashcode laten genereren"

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((gemeente == null) ? 0 : gemeente.hashCode());
		result = prime * result + ((huisNr == null) ? 0 : huisNr.hashCode());
		result = prime * result + ((postcode == null) ? 0 : postcode.hashCode());
		result = prime * result + ((straat == null) ? 0 : straat.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Adres))
			return false;
		Adres other = (Adres) obj;
		if (gemeente == null) {
			if (other.gemeente != null)
				return false;
		} else if (!gemeente.equals(other.gemeente))
			return false;
		if (huisNr == null) {
			if (other.huisNr != null)
				return false;
		} else if (!huisNr.equals(other.huisNr))
			return false;
		if (postcode == null) {
			if (other.postcode != null)
				return false;
		} else if (!postcode.equals(other.postcode))
			return false;
		if (straat == null) {
			if (other.straat != null)
				return false;
		} else if (!straat.equals(other.straat))
			return false;
		return true;
	}

}
