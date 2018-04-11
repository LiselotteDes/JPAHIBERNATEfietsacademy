package be.vdab.fietsacademy.entities;

import java.io.Serializable;
import java.util.UUID;

//import javax.persistence.DiscriminatorColumn;				// Table per class
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
//import javax.persistence.Table;							// Table per class & Table per subclass

// BASE CLASS

@Entity
/*
 * @Inheritance staat bij de HOOGSTE class in de inheritance hierarchy.
 * > strategy duidt de manier aan waarmee je inheritance nabootst in de database.
 * > SINGLE_TABLE staat voor "table per class hierarchy".
 */
//@Inheritance(strategy = InheritanceType.SINGLE_TABLE)		// Table per class
//@Inheritance(strategy = InheritanceType.JOINED)			// Table per subclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)	// Table per concrete class
//@Table(name = "cursussen")								// Table per class & Table per subclass
/*
 * @DiscriminatorColumn duidt de naam van discriminator kolom aan.
 * Opmerking: De discriminator kolom (soort) heeft geen bijbehorende variabele!
 */
//@DiscriminatorColumn(name = "soort")						// Table per class
public abstract class Cursus implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)		// Table per class & Table per subclass
//	private long id;										// Table per class & Table per subclass
	private String id;										// Table per concrete class
	private String naam;
	public Cursus(String naam) {
		this.naam = naam;
		this.id = UUID.randomUUID().toString();				// nieuwe unieke UUID toekennen: Table per concrete class
	}
	protected Cursus() {
	}
	public String getId() {
		return id;
	}
	public String getNaam() {
		return naam;
	}
}
