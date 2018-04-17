package be.vdab.fietsacademy.entities;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

// "Many-to-many associatie"

@Entity
@Table(name = "verantwoordelijkheden")
public class Verantwoordelijkheid implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String naam;
	// @ManyToMany staat bij een variabele die een many-to-many associatie voorstelt.
	@ManyToMany
	/*
	 * @JoinTable duidt de tussentable aan die hoort bij de associatie.
	 * - name bevat de naam van de tussentable.
	 * - joinColumns bevat de naam van de kolom in de tussentable die de foreign key is naar de primary key van de table (verantwoordelijkheden)
	 *   die hoort bij de huidige entity (Verantwoordelijkheid).
	 *   Je vult joinColumns met een @JoinColumn.
	 * - inverseJoinColumns bevat de kolomnaam in de tussentable die de foreign key is naar de primary key van de table (docenten) 
	 *   die hoort bij de entity aan de andere associatie kant (Docent).
	 *   Je vult inverseJoinColumns met een @JoinColumn.
	 */
	@JoinTable(
			name = "docentenverantwoordelijkheden",
			joinColumns = @JoinColumn(name = "verantwoordelijkheidId"),
			inverseJoinColumns = @JoinColumn(name = "docentid"))
	private Set<Docent> docenten = new LinkedHashSet<>();
	
	public Verantwoordelijkheid(String naam) {
		this.naam = naam;
	}
	protected Verantwoordelijkheid() {
	}
	
	public long getId() {
		return id;
	}
	public String getNaam() {
		return naam;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((naam == null) ? 0 : naam.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Verantwoordelijkheid))
			return false;
		Verantwoordelijkheid other = (Verantwoordelijkheid) obj;
		if (naam == null) {
			if (other.naam != null)
				return false;
		} else if (!naam.equals(other.naam))
			return false;
		return true;
	}
	
	public boolean add(Docent docent) {
//		throw new UnsupportedOperationException();
		boolean toegevoegd = docenten.add(docent);
		if (! docent.getVerantwoordelijkheden().contains(this)) {
			docent.add(this);
		}
		return toegevoegd;
	}
	public boolean remove(Docent docent) {
//		throw new UnsupportedOperationException();
		boolean verwijderd = docenten.remove(docent);
		if (docent.getVerantwoordelijkheden().contains(this)) {
			docent.remove(this);
		}
		return verwijderd;
	}
	public Set<Docent> getDocenten() {
//		throw new UnsupportedOperationException();
		return Collections.unmodifiableSet(docenten);
	}
}
