package be.vdab.fietsacademy.repositories;

import java.util.Optional;

import be.vdab.fietsacademy.entities.Cursus;

public interface CursusRepository {
//	Optional<Cursus> read(long id);			// Table per class & Table per subclass
	Optional<Cursus> read(String id);		// Table per concrete class
	void create(Cursus cursus);
}
