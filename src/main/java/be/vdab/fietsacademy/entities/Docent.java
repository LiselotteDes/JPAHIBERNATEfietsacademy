package be.vdab.fietsacademy.entities;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import be.vdab.fietsacademy.enums.Geslacht;

/*
 * De JPA annotaties die de mapping informatie beschrijven in een entity class:
 * - Je tikt @Entity juist voor de entity class.
 * - Je tikt @Table voor de entity class, met de table naam in de database.
 *   Je mag @Table weglaten als de table naam gelijk is aan de class naam.
 */
@Entity
@Table(name = "docenten")
/*
 * JPA raadt aan dat de class Serializable implementeert.
 * Dit is NIET noodzakelijk voor de samenwerking met de database.
 * Het is WEL noodzakelijk als je objecten via serialization naar een binair bestand zou wegschrijven,
 * of over het netwerk zou transporteren met serialization.
 */
public class Docent implements Serializable {
	private static final long serialVersionUID = 1L;
	// Je tikt @Id voor de private variabele die hoort bij de primary key kolom.
	@Id
	private long id;
	// JPA associeert een private variabele met een table-kolom met dezelfde naam. JPA associeert dus de variabele voornaam met een kolom voornaam.
	private String voornaam;
	private String familienaam;
	private BigDecimal wedde;
	private String emailAdres;
	// Geeft aan dat bij de variabele een varchar kolom hoort: De kolom waarde MAN hoort bij de enum waarde MAN, idem voor VROUW.
	@Enumerated(EnumType.STRING)
	private Geslacht geslacht;
	public long getId() {
		return id;
	}
	public String getVoornaam() {
		return voornaam;
	}
	public String getFamilienaam() {
		return familienaam;
	}
	public BigDecimal getWedde() {
		return wedde;
	}
	public String getEmailAdres() {
		return emailAdres;
	}
	public Geslacht getGeslacht() {
		return geslacht;
	}
	
}
