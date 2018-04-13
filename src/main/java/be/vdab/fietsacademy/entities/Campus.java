package be.vdab.fietsacademy.entities;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import be.vdab.fietsacademy.valueobjects.Adres;
import be.vdab.fietsacademy.valueobjects.TelefoonNr;

@Entity
@Table(name = "campussen")
public class Campus implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String naam;
	@Embedded
	// @Embedded staat voor een variabele met als type een value object class
	private Adres adres;
	/*
	 * "Verzameling value objects met een eigen type"
	 * 
	 * @ElementCollection 	staat bij een variabele met een verzameling value objects.
	 * @CollectionTable 	duidt de naam van de table aan die de value objects bevat.
	 * @JoinColumn 			duidt een kolom in deze table aan.
	 * 						Het is de foreign key kolom die verwijst naar 
	 * 						primary kolom in de table (campussen) die hoort bij de huidige entity class (Campus).
	 * 						Je vult met @JoinColumn de parameter joinColumns van @CollectionTable.
	 * @OrderBy 			definieert de volgorde waarmee JPA de value objects leest uit de database.
	 * 						Je vermeldt de naam van één of meerdere private variabelen (gescheiden door komma) die horen bij de kolom waarop je wil sorteren.
	 * 						Je kan omgekeerd sorteren met desc na een private variabele.
	 * 						(! voor value objects met een eigen type)
	 */
	@ElementCollection
	@CollectionTable(name = "campussentelefoonnrs", joinColumns = @JoinColumn(name = "campusid"))
	@OrderBy("fax")
	private Set<TelefoonNr> telefoonNrs;
	/*
	 * "One-to-many associatie"
	 * 
	 * @OneToMany 	staat bij een variabele die een one-to-many associatie voorstelt.
	 * @JoinColumn	Bij de variabele docenten hoort de table docenten.
	 * 				@JoinColumn duidt in die table de foreign key kolom aan die verwijst naar de primary key van de table (campussen), 
	 * 				die hoort bij de huidige class (Campus).
	 * @OrderBy		definieert de volgorde waarmee JPA de Docent entities aan de many kant leest uit de database.
	 * 				Je vermeldt de naam van één of meerdere private variabelen die horen bij de kolom(men) waarop je wil sorteren.
	 */
	@OneToMany
	@JoinColumn(name = "campusid")
	@OrderBy("voornaam, familienaam")
	private Set<Docent> docenten;
	
	// een geparametriseerde constructor (zonder id parameter)
	public Campus(String naam, Adres adres) {
		this.naam = naam;
		this.adres = adres;
		this.telefoonNrs = new LinkedHashSet<>();
		this.docenten = new LinkedHashSet<>();
	}
	
	// een protected default constructor
	protected Campus() {
	}
	
	// getters voor id, naam en adres
	public long getId() {
		return id;
	}
	public String getNaam() {
		return naam;
	}
	public Adres getAdres() {
		return adres;
	}
	// getter voor de verzameling value objects met een eigen type
	public Set<TelefoonNr> getTelefoonNrs() {
		return Collections.unmodifiableSet(telefoonNrs);
	}
	// getter voor de one-to-many associatie naar Docent
	public Set<Docent> getDocenten() {
		return Collections.unmodifiableSet(docenten);
	}
	
	// (mogelijke extra methods: add(TelefoonNr telefoonNr) en remove(TelefoonNr nr) )
	
	public boolean addDocent(Docent docent) {
		if (docent == null) {
			throw new NullPointerException();
		}
		return docenten.add(docent);
	}
}
