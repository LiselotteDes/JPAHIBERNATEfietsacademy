package be.vdab.fietsacademy.valueobjects;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class TelefoonNr implements Serializable {
	private static final long serialVersionUID = 1L;
	private String nummer;
	private boolean fax;
	private String opmerking;
	// geparametriseerde constructor
	public TelefoonNr(String nummer, boolean fax, String opmerking) {
		this.nummer = nummer;
		this.fax = fax;
		this.opmerking = opmerking;
	}
	// een protected default constructor
	protected TelefoonNr() {
	}
	// getters voor nummer, fax en opmerking.
	public String getNummer() {
		return nummer;
	}
	public boolean getFax() {
		return fax;
	}
	public String getOpmerking() {
		return opmerking;
	}
	// overschreven methods
	/*
	 * Je stelt stratks in Campus de verzameling telefoonnummers voor als een Set<TelefoonNr>.
	 * Deze Set laat geen TelefoonNr objecten met hetzelfde nummer toe.
	 * Je baseert daartoe de equals method op het nummer.
	 */
	@Override
	public boolean equals(Object object) {
		if (! (object instanceof TelefoonNr)) {
			return false;
		}
		TelefoonNr telefoonNr = (TelefoonNr) object;
		return nummer.equalsIgnoreCase(telefoonNr.nummer);
	}
	/*
	 * Je baseert de method hashCode ook op het nummer.
	 */
	@Override
	public int hashCode() {
		return nummer.toUpperCase().hashCode();
	}
}
