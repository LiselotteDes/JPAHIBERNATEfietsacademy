package be.vdab.fietsacademy.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.Table;
import javax.persistence.Version;

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
 * "Entity Graph"
 * @NamedEntityGraph: Schrijf je voor de entity class die de behoefte heeft om niet enkel die entity te lezen, maar ook een direct geassocieerde entity.
 * - Elke name entity graph moet een unieke naam hebben.
 *   De kans daartoe vergroot als je die naam begint met de naam van de huidige class.
 * - Je vermeldt de naam van de private variabele campus in de huidige class Docent.
 *   Je drukt zo de behoefte uit dat bij het lezen van een Docent entity uit de database, JPA direct ook de bijbehorende Camus entity moet lezen (via een join).
 */
@NamedEntityGraph(name = Docent.MET_CAMPUS, attributeNodes = @NamedAttributeNode("campus"))
/*
 * JPA raadt aan dat de class Serializable implementeert.
 * Dit is NIET noodzakelijk voor de samenwerking met de database.
 * Het is WEL noodzakelijk als je objecten via serialization naar een binair bestand zou wegschrijven,
 * of over het netwerk zou transporteren met serialization.
 */
public class Docent implements Serializable {
	
	// *** PRIVATE VARIABELEN ***
	
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
	 * 						(! voor value objects met een basistype)
	 */
	@ElementCollection
	@CollectionTable(name = "docentenbijnamen", joinColumns = @JoinColumn(name = "docentid"))
	@Column(name = "bijnaam")
	private Set<String> bijnamen;
	/*
	 * "Many-to-one associatie"
	 * 
	 * @ManyToOne 	staat bij een variabele die een many-to-one associatie voorstelt.
	 * 				- De foreign key kolom campusId, die bij deze associatie hoort, is in de database gedefinieerd als verplicht in te vullen.
	 * 				  Je plaatst dan de parameter optional op false.
	 * 				  JPA controleert dan voor het toevoegen/wijzigen van een record dat deze kolom wel degelijk ingevuld is 
	 * 				  en werpt een exception als dit niet het geval is.
	 * 				- Je stelt lazy loading in (ipv de standaard eager loading) met de parameter fetch.
	 * @JoinColumn	De table docenten hoort bij de huidige class Docent.
	 * 				@JoinColumn duidt de kolom campusid in deze table aan.
	 * 				Je kiest de foreign key kolom die verwijst naar de table campussen die hoort bij de geassocieerde entity (Campus).
	 */
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "campusid")
	private Campus campus;
	/*
	 * "Many-to-many associatie"
	 * @ManyToMany staat bij een variabele die een many-to-many associatie voorstelt.
	 * > mappadBy bevat de variabele naam (docenten), die aan de andere associatie kant (Verantwoordelijkheid), de associatie voorstelt.
	 */
	@ManyToMany(mappedBy = "docenten")
	Set<Verantwoordelijkheid> verantwoordelijkheden = new LinkedHashSet<>();
	// "Entity Graph: Refactoring"
	public static final String MET_CAMPUS = "Docent.metCampus";		 // ! PUBLIC CONSTANTE
	// "Optimistic record locking"
	/*
	 * Je tikt @Version voor de private variabele die hoort bij de kolom die JPA kan gebruiken voor de versie controle.
	 */
	@Version
//	private long versie;		// "Versie kolom met een geheel getal"
	private Timestamp versie;	// "Versie kolom met een timestamp"
	
	// *** CONSTRUCTORS **
	
	protected Docent() {
	}
	
	public Docent(String voornaam, String familienaam, BigDecimal wedde, String emailAdres, Geslacht geslacht, Campus campus) {
		this.voornaam = voornaam;
		this.familienaam = familienaam;
		this.wedde = wedde;
		this.emailAdres = emailAdres;
		this.geslacht = geslacht;
		this.bijnamen = new LinkedHashSet<>();		// "Verzameling value objects met een basistype"
		setCampus(campus);							// "Many-to-one associatie"
	}

	// *** GETTERS ***
	
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
	/*
	 * "Verzameling value objects met een basistype": methods die met deze Set samenwerken
	 * 
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
	public Campus getCampus() {
		return campus;
	}
	
	// *** SETTERS ***
	
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
	
	public void setCampus(Campus campus) {
		if (campus == null) {
			throw new NullPointerException();
		}
		// Code bijgevoegd voor de "Bidirectionele associatie"
		if (! campus.getDocenten().contains(this)) {
			campus.addDocent(this);
		} // Einde code "Bidirectionele associatie"
		this.campus = campus;
	}
	
	// *** ANDERE METHODS ***
	
	// Je maakt een nieuwe method waarmee de gebruiker één docent opslag geeft, als voorbeeld van hoe je een entity wijzigt.
	public void opslag(BigDecimal percentage) {
		if (percentage.compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException();
		}
		BigDecimal factor = BigDecimal.ONE.add(percentage.divide(BigDecimal.valueOf(100)));
		wedde = wedde.multiply(factor, new MathContext(2, RoundingMode.HALF_UP));
	}
	
	// EQUALS & HASHCODE

	@Override
	public boolean equals(Object object) {
		if (! (object instanceof Docent)) {
			return false;
		}
		// 1° poging: verkeerdelijk gebaseerd op id = private variabele die hoort bij de automatisch gegenereerde primary key.
//		Docent docent = (Docent) object; 
//		return this.id == docent.id;
		// Juist: gabaseerd op emailadres. 
		if (emailAdres == null) {
			return false;
		}
		// Je maakt bij het vergelijken van email adressen geen onderscheid tussen kleine en hoofdletters.
		return emailAdres.equalsIgnoreCase(((Docent) object).emailAdres);
	}
	@Override
	public int hashCode() {
		// 1° poging: verkeerdelijk gebaseerd op id = private variabele die hoort bij de automatisch gegenereerde primary key.
//		return (int) id;
		// Juist: gebaseerd op emailadres.
		// Je maakt bij het bepalen van de hashCode geen onderscheid tussen kleine en hoofdletters.
		return emailAdres == null ? 0 : emailAdres.toLowerCase().hashCode();
	}
	
	// "Many-to-many associatie"
	
	public boolean add(Verantwoordelijkheid verantwoordelijkheid) {
//		throw new UnsupportedOperationException();
		boolean toegevoegd = verantwoordelijkheden.add(verantwoordelijkheid);
		if (! verantwoordelijkheid.getDocenten().contains(this)) {
			verantwoordelijkheid.add(this);
		}
		return toegevoegd;
	}
	public boolean remove(Verantwoordelijkheid verantwoordelijkheid) {
//		throw new UnsupportedOperationException();
		boolean verwijderd = verantwoordelijkheden.remove(verantwoordelijkheid);
		if (verantwoordelijkheid.getDocenten().contains(this)) {
			verantwoordelijkheid.remove(this);
		}
		return verwijderd;
	}
	public Set<Verantwoordelijkheid> getVerantwoordelijkheden() {
//		throw new UnsupportedOperationException();
		return Collections.unmodifiableSet(verantwoordelijkheden);
	}
}
