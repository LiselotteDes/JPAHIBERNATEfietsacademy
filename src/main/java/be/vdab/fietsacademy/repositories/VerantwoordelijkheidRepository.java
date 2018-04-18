package be.vdab.fietsacademy.repositories;

import java.util.Optional;

import be.vdab.fietsacademy.entities.Verantwoordelijkheid;

public interface VerantwoordelijkheidRepository {
	Optional<Verantwoordelijkheid> read(long id);
	void create(Verantwoordelijkheid verantwoordelijkheid);
}
