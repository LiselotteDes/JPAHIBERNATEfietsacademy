package be.vdab.fietsacademy.repositories;

import java.util.Optional;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import be.vdab.fietsacademy.entities.Docent;

@Repository
class JpaDocentRepository implements DocentRepository {
	private final EntityManager manager;
	JpaDocentRepository(EntityManager entityManager) {
		this.manager = entityManager;
	}
	@Override
	public Optional<Docent> read(long id) {
		/*
		 * Je zoekt een Docent entity op de primary key met de find method.
		 * De 1° parameter is het type van de op te zoeken entity.
		 * De 2° parameter is de primary key waarde van de op te zoeken entity.
		 */
		return Optional.ofNullable(manager.find(Docent.class, id));
	}
	@Override
	public void create(Docent docent) {
//		throw new UnsupportedOperationException();
		manager.persist(docent);
	}
	@Override
	public void delete(long id) {
//		throw new UnsupportedOperationException();
		
		// Je verwijdert een entity in twee stappen: je leest eerst de te verwijderen entity.
		read(id)
			// Je voert de EntityManager method remove uit en geeft die entity mee als parameter.
			.ifPresent(docent -> manager.remove(docent));
	}
}
