package be.vdab.fietsacademy.repositories;

import java.util.Optional;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import be.vdab.fietsacademy.entities.Docent;

@Repository
class JpaDocentRepository implements DocentRepository {
	private final EntityManager entityManager;
	JpaDocentRepository(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	@Override
	public Optional<Docent> read(long id) {
		/*
		 * Je zoekt een Docent entity op de primary key met de find method.
		 * De 1° parameter is het type van de op te zoeken entity.
		 * De 2° parameter is de primary key waarde van de op te zoeken entity.
		 */
		return Optional.ofNullable(entityManager.find(Docent.class, id));
	}
}
