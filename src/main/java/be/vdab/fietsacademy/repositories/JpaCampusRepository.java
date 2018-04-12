package be.vdab.fietsacademy.repositories;

import java.util.Optional;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import be.vdab.fietsacademy.entities.Campus;

@Repository
class JpaCampusRepository implements CampusRepository {
	private final EntityManager manager;
	JpaCampusRepository(EntityManager manager) {
		this.manager = manager;
	}

	@Override
	public void create(Campus campus) {
//		throw new UnsupportedOperationException();
		manager.persist(campus);

	}

	@Override
	public Optional<Campus> read(long id) {
//		throw new UnsupportedOperationException();
		return Optional.ofNullable(manager.find(Campus.class, id));
	}

}
