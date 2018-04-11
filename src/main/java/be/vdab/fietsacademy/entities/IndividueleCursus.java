package be.vdab.fietsacademy.entities;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
//@DiscriminatorValue("I")				// Table per class
@Table(name = "individuelecursussen")	// Table per subclass
public class IndividueleCursus extends Cursus {
	private static final long serialVersionUID = 1L;
	private int duurtijd;
	public IndividueleCursus(String naam, int duurtijd) {
		super(naam);
		this.duurtijd = duurtijd;
	}
	protected IndividueleCursus() {
	}
	public int getDuurtijd() {
		return duurtijd;
	}
}
