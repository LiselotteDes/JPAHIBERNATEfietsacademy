package be.vdab.fietsacademy.repositories;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import be.vdab.fietsacademy.entities.Docent;

public interface DocentRepository {
	// "Entity zoeken via de primary key"
	Optional<Docent> read(long id);
	// "Entity toevoegen"
	void create(Docent docent);
	// "Entity verwijderen"
	void delete(long id);
	// "JPQL: Alle entities lezen"
	List<Docent> findAll();
	// "JPQL: Selecteren"
	List<Docent> findByWeddeBetween(BigDecimal van, BigDecimal tot);
}
