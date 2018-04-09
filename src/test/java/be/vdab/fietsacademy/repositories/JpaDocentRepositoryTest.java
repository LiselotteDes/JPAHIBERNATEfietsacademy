package be.vdab.fietsacademy.repositories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.junit.Before;
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
	private EntityManager manager;
	// Variabele toegevoegd om create method te kunnen testen
	private Docent docent;
	@Before
	public void before() {
		docent = new Docent("test", "test", BigDecimal.TEN, "test@fietsacademy.be", Geslacht.MAN);
	}
	
	private long idVanNieuweMan() {
		// Je specifieert een uit te voeren SQL statement met de method createNativeQuery.
		manager.createNativeQuery("insert into docenten("
				+ "voornaam, familienaam, wedde, emailadres, geslacht)"
				+ "values('jean', 'smits', 1000, 'jean.smits@fietsacademy.be', 'MAN')")
		// Je voert dit statement uit met method executeUpdate, als dit een insert/update/delete statment is (retourneert # aangepaste entities)
		.executeUpdate();
		return ((Number) manager
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
		manager.createNativeQuery("insert into docenten(voornaam, familienaam, wedde, emailadres, geslacht)"
				+ "values('jeanine', 'smits', 1000, 'jeanine.smits@fietsacademy.be','VROUW')").executeUpdate();
		return ((Number) manager.createNativeQuery("select id from docenten where emailadres='jeanine.smits@fietsacademy.be'")
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
	@Test
	public void create() {
		repository.create(docent);
		long autoNumberId = docent.getId();
		/*
		 * De private variabele id, die JPA heeft ingevuld, na het toevoegen van het nieuwe record, met het getal in de kolom,
		 * mag niet 0 zijn.
		 */
		assertNotEquals(0, autoNumberId);
		/*
		 * Als je het record terug zoekt met een SQL statement, mbv het id, dan moet het emailadres dat gevonden wordt in de db 
		 * gelijk zijn aan het emailadres van het Docent object dat werd meegegeven.
		 */
		assertEquals("test@fietsacademy.be",
				// Je geeft een parameter aan met een : teken gevolgd door de naam van de parameter.
				(String) manager.createNativeQuery("select emailadres from docenten where id = :id")
				// Je vult de parameter id in.
				.setParameter("id", autoNumberId)
				.getSingleResult());
	}
	@Test
	public void delete() {
		// Eerst een docent toevoegen, id opslaan, om hem dan te verwijderen.
		long id = idVanNieuweMan();
		repository.delete(id);
		/*
		 * Je hebt op de vorige regel de docent verwijderd.
		 * De EntityManager voert verwijderingen niet onmiddelijk uit, maar spaart die op tot juist voor de commit van de transactie.
		 * Hij stuurt dan alle opgespaarde verwijderingen in één keer naar de database, via JDBC batch updates.
		 * Dit verhoogt de performantie.
		 * In deze test is het WEL nodig dat de docent onmiddelijk verwijderd wordt,
		 * zodat we de correcte werking van onze delete method kunnen testen.
		 * Je voert de flush method uit die gevraagde verwijderingen onmiddelijk uitvoert.
		 */
		manager.flush();
		// Als het verwijderen gelukt is, zijn er nog 0 docenten met het verwijderde id.
		assertEquals(0, ((Number) manager.createNativeQuery("select count(*) from docenten where id = :id")
				.setParameter("id", id)
				.getSingleResult()).longValue());
	}
	@Test
	public void findAll() {
		// *** Test dat alle entities gelezen worden ***
		idVanNieuweMan();
		List<Docent> docenten = repository.findAll();
		long aantalDocenten = ((Number) manager.createNativeQuery("select count(*) from docenten")
				.getSingleResult()).longValue();
		assertEquals(aantalDocenten, docenten.size());
		// *** Test of de docenten op wedde gesorteerd zijn ***
		BigDecimal vorigeWedde = BigDecimal.ZERO;
		for (Docent docent : docenten) {
			assertTrue(docent.getWedde().compareTo(vorigeWedde) >= 0);
			vorigeWedde = docent.getWedde();
		}
	}
	@Test
	public void findByWeddeBetween() {
		idVanNieuweMan();
		List<Docent> docenten = repository.findByWeddeBetween(BigDecimal.valueOf(1_000), BigDecimal.valueOf(2_000));
		long aantalDocenten = ((Number) manager.createNativeQuery("select count(*) from docenten where wedde between 1000 and 2000")
				.getSingleResult()).longValue();
		assertEquals(aantalDocenten, docenten.size());
	}
}
