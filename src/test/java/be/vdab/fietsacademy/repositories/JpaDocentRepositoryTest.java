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
import be.vdab.fietsacademy.enums.Geslacht;

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
				+ "voornaam, familienaam, wedde, emailadres, geslacht)"
				+ "values('jean', 'smits', 1000, 'jean.smits@fietsacademy.be', 'MAN')")
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
	private long idVanNieuweVrouw() {
		entityManager.createNativeQuery("insert into docenten(voornaam, familienaam, wedde, emailadres, geslacht)"
				+ "values('jeanine', 'smits', 1000, 'jeanine.smits@fietsacademy.be','VROUW')").executeUpdate();
		return ((Number) entityManager.createNativeQuery("select id from docenten where emailadres='jeanine.smits@fietsacademy.be'")
				.getSingleResult()).longValue();
	}
	
	@Test
	public void read() {
		Optional<Docent> optionalDocent = repository.read(idVanNieuweMan());
		assertTrue(optionalDocent.isPresent());
		assertEquals("jean.smits@fietsacademy.be", optionalDocent.get().getEmailAdres());
	}
	@Test
	public void man() {
		assertEquals(Geslacht.MAN, repository.read(idVanNieuweMan()).get().getGeslacht());
	}
	@Test
	public void vrouw() {
		assertEquals(Geslacht.VROUW, repository.read(idVanNieuweVrouw()).get().getGeslacht());
	}
}
