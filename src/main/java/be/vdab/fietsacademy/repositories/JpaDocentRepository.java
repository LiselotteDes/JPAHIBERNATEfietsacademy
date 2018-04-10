package be.vdab.fietsacademy.repositories;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import be.vdab.fietsacademy.entities.Docent;
import be.vdab.fietsacademy.valueobjects.IdEnEmailAdres;

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
	@Override
	public List<Docent> findAll() {
//		throw new UnsupportedOperationException();
		
		return manager.createQuery("select d from Docent d order by d.wedde", Docent.class).getResultList();
	}
	@Override
	public List<Docent> findByWeddeBetween(BigDecimal van, BigDecimal tot) {
//		throw new UnsupportedOperationException();
		return manager
				.createQuery("select d from Docent d where d.wedde between :van and :tot", Docent.class)	// retourneert TypedQuery<Docent>
				.setParameter("van", van)																	// retourneert TypedQuery<Docent>
				.setParameter("tot", tot)																	// retourneert TypedQuery<Docent>
				.getResultList();																			// retourneert List<Docent>
	}
	@Override
	public List<String> findEmailAdressen() {
//		throw new UnsupportedOperationException();
		return manager
				.createQuery("select d.emailAdres from Docent d", String.class)	// ! hoofdletter in 'emailAdres': naam van private variabele, niet van kolom.
				.getResultList();
	}
	@Override
	public List<IdEnEmailAdres> findIdsEnEmailAdressen() {
//		throw new UnsupportedOperationException();
		return manager
				.createQuery("select new be.vdab.fietsacademy.valueobjects.IdEnEmailAdres(d.id, d.emailAdres) from Docent d", IdEnEmailAdres.class)
				.getResultList();
	}
	@Override
	public BigDecimal findGrootsteWedde() {
//		throw new UnsupportedOperationException();
		return manager.createQuery("select max(d.wedde) from Docent d", BigDecimal.class).getSingleResult();
	}
}
