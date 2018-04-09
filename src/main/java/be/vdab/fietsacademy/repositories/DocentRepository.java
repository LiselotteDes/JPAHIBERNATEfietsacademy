package be.vdab.fietsacademy.repositories;

import java.util.Optional;

import be.vdab.fietsacademy.entities.Docent;

public interface DocentRepository {
	Optional<Docent> read(long id);
	void create(Docent docent);
	void delete(long id);
}
