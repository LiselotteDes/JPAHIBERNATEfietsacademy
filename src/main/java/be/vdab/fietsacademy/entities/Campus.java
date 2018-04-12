package be.vdab.fietsacademy.entities;

import java.io.Serializable;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import be.vdab.fietsacademy.valueobjects.Adres;

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
	
	// een geparametriseerde constructor (zonder id parameter)
	public Campus(String naam, Adres adres) {
		this.naam = naam;
		this.adres = adres;
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
}
