package be.vdab.fietsacademy.repositories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import be.vdab.fietsacademy.entities.Docent;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
// @DataJpaTest maakt enkel een DataSource bean en een EntityManager bean. Je maakt hier een bean van de te testen class: JpaDocentRepository.
@Import(JpaDocentRepository.class)
public class JpaDocentRepositoryTest {
	@Autowired
	private JpaDocentRepository repository;
	@Autowired
	// Je injecteert de EntityManager om er SQL statments mee naar de database te sturen.
	private EntityManager entityManager;
	
	private long idVanNieuweMan() {
		// Je specifieert een uit te voeren SQL statement met de method createNativeQuery.
		entityManager.createNativeQuery("insert into docenten("
				+ "voornaam, familienaam, wedde, emailadres)"
				+ "values('jean', 'smits', 1000, 'jean.smits@fietsacademy.be')")
		// Je voert dit statement uit met method executeUpdate, als dit een insert/update/delete statment is (retourneert # aangepaste entities)
		.executeUpdate();
		return ((Number) entityManager
				.createNativeQuery("select id from docenten where emailadres='jean.smits@fietsacademy.be'")
				/*
				 * Je voert het SQL statement uit met de method getSingleResult als het een select statement is dat één waarde teruggeeft.
				 * De method getSingleResult geeft je die ene waarde onder de gedaante van Object.
				 * Je cast deze waarde naar Number, de base class van alle getal classes (Integer, Long, ...).
				 * Je vraagt de long waarde van dit Number met de method longValue.
				 */
				.getSingleResult())
				.longValue();
	}
	
	@Test
	public void read() {
		Optional<Docent> optionalDocent = repository.read(idVanNieuweMan());
		assertTrue(optionalDocent.isPresent());
		assertEquals("jean.smits@fietsacademy.be", optionalDocent.get().getEmailAdres());
	}

}
