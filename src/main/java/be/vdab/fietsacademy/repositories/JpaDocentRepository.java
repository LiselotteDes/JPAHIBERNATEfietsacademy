package be.vdab.fietsacademy.repositories;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

@Repository
class JpaDocentRepository implements DocentRepository {
	private final EntityManager entityManager;
	JpaDocentRepository(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
}
