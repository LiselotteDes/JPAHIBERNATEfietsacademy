package be.vdab.fietsacademy.repositories;

import java.util.Optional;

import be.vdab.fietsacademy.entities.Campus;

public interface CampusRepository {
	void create(Campus campus);
	Optional<Campus> read(long id);
}
