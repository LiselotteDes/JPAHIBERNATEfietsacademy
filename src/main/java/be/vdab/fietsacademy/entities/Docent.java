package be.vdab.fietsacademy.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import be.vdab.fietsacademy.enums.Geslacht;

// "Named queries in entity classes"
//@NamedQuery(name = "Docent.findByWeddeBetween", query = "select d from Docent d where d.wedde between :van and :tot order by d.wedde, d.id")
// (verwijderd om te verplaatsen naar orm.xml)

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
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	// JPA associeert een private variabele met een table-kolom met dezelfde naam. JPA associeert dus de variabele voornaam met een kolom voornaam.
	private String voornaam;
	private String familienaam;
	private BigDecimal wedde;
	private String emailAdres;
	// Geeft aan dat bij de variabele een varchar kolom hoort: De kolom waarde MAN hoort bij de enum waarde MAN, idem voor VROUW.
	@Enumerated(EnumType.STRING)
	private Geslacht geslacht;
	/*
	 * "Verzameling value objects met een basistype": Definieer de verzameling bijnamen als een Set<String>
	 * JPA ondersteunt de types List, Set, Map.
	 * 
	 * @ElementCollection 	staat voor een verzameling value objects.
	 * @CollectionTable 	duidt de table naam aan die de value objects bevat.
	 * @JoinColumn 			duidt een kolom in deze table aan.
	 * 						Het is de foreign key kolom die verwijst naar de primary kolom in de table (docenten) die hoort bij de huidige entity class.
	 * 						Je vult met @JoinColumn de parameter joinColumns van @CollectionTable.
	 * @Column 				duidt de kolom naam aan die hoort bij de value objects in de verzameling.
	 */
	@ElementCollection
	@CollectionTable(name = "docentenbijnamen", joinColumns = @JoinColumn(name = "docentid"))
	@Column(name = "bijnaam")
	private Set<String> bijnamen;
	
	// constructors
	protected Docent() {
	}
	
	public Docent(String voornaam, String familienaam, BigDecimal wedde, String emailAdres, Geslacht geslacht) {
		this.voornaam = voornaam;
		this.familienaam = familienaam;
		this.wedde = wedde;
		this.emailAdres = emailAdres;
		this.geslacht = geslacht;
		this.bijnamen = new LinkedHashSet<>();		//  "Verzameling value objects met een basistype"
	}

	// getters
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
	
	// Je maakt een nieuwe method waarmee de gebruiker één docent opslag geeft, als voorbeeld van hoe je een entity wijzigt.
	public void opslag(BigDecimal percentage) {
		if (percentage.compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException();
		}
		BigDecimal factor = BigDecimal.ONE.add(percentage.divide(BigDecimal.valueOf(100)));
		wedde = wedde.multiply(factor, new MathContext(2, RoundingMode.HALF_UP));
	}
	
	//  "Verzameling value objects met een basistype": methods die met deze Set samenwerken
	/*
	 * JPA stelt zelf geen eisen aan een getter voor de verzameling value objects.
	 * Los van JPA wordt volgende manier aangeraden:
	 */
	public Set<String> getBijnamen() {
//		throw new UnsupportedOperationException();
		// NIET ZO
//		return bijnamen;
		/*
		 * Je leest met de method getBijnamen de bijnamen van een Docent.
		 * Als je return bijnamen; schrijft, kan je met getBijnamen per ongeluk een bijnaam toevoegen aan de docent:
		 * docent.getBijnamen().add("Polle pap");
		 * Je kan ook per ongeluk een bijnaam verwijderen:
		 * docent.getBijnamen().remove("Polle pap");
		 */
		// WEL ZO
		return Collections.unmodifiableSet(bijnamen);
		/*
		 * Je verhindert dit met de static Collections method unmodifiableSet.
		 * Je geeft een Set mee.
		 * Je krijgt een Set terug met dezelfde elementen.
		 * Als je op die Set add of remove uitvoert, krijg je een UnsupportedOperationException.
		 * De getter geeft zo een read-only voorstelling van de Set.
		 */
	}
	public boolean addBijnaam(String bijnaam) {
//		throw new UnsupportedOperationException();
		if (bijnaam.trim().isEmpty()) {
			throw new IllegalArgumentException();
		}
		return bijnamen.add(bijnaam);
	}
	public boolean removeBijnaam(String bijnaam) {
//		throw new UnsupportedOperationException();
//		if (bijnaam.trim().isEmpty()) {					// Dit had ik fout, is hier niet nodig!
//			throw new IllegalArgumentException();
//		}
		return bijnamen.remove(bijnaam);
	}
}
