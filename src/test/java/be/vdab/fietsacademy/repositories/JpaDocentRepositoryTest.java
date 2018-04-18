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

import be.vdab.fietsacademy.entities.Campus;
import be.vdab.fietsacademy.entities.Docent;
import be.vdab.fietsacademy.entities.Verantwoordelijkheid;
import be.vdab.fietsacademy.enums.Geslacht;
import be.vdab.fietsacademy.valueobjects.AantalDocentenPerWedde;
import be.vdab.fietsacademy.valueobjects.Adres;
import be.vdab.fietsacademy.valueobjects.IdEnEmailAdres;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
// @DataJpaTest maakt enkel een DataSource bean en een EntityManager bean. Je maakt hier een bean van de te testen class: JpaDocentRepository.
@Import(JpaDocentRepository.class)
public class JpaDocentRepositoryTest {
	
	// *** PRIVATE VARIABELEN ***
	
	@Autowired
	private JpaDocentRepository repository;
	@Autowired
	// Je injecteert de EntityManager om er SQL statments mee naar de database te sturen.
	private EntityManager manager;
	// Variabele toegevoegd om create method te kunnen testen
	private Docent docent;
	private Campus campus;
	
	@Before
	public void before() {
		campus = new Campus("test", new Adres("test", "test", "test", "test"));
		docent = new Docent("test", "test", BigDecimal.TEN, "test@fietsacademy.be", Geslacht.MAN, campus);
//		campus.addDocent(docent);	// verwijderd: "Bidirectionele associatie"
	}
	
	// *** PRIVATE METHODS ***
	
	private long idVanNieuweMan() {
		// Je specifieert een uit te voeren SQL statement met de method createNativeQuery.
		manager.createNativeQuery("insert into docenten("
				+ "voornaam, familienaam, wedde, emailadres, geslacht, campusid)"
				+ "values('testM', 'testM', 1000, 'testM@fietsacademy.be', 'MAN', :campusid)")
			.setParameter("campusid", idVanNieuweCampus())
			// Je voert dit statement uit met method executeUpdate, als dit een insert/update/delete statment is (retourneert # aangepaste entities)
			.executeUpdate();
		return ((Number) manager
				.createNativeQuery("select id from docenten where emailadres='testM@fietsacademy.be'")
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
		manager.createNativeQuery("insert into docenten(voornaam, familienaam, wedde, emailadres, geslacht, campusid)"
				+ "values('testV', 'testV', 1000, 'testV@fietsacademy.be','VROUW', :campusid)")
			.setParameter("campusid", idVanNieuweCampus()).executeUpdate();
		return ((Number) manager.createNativeQuery("select id from docenten where emailadres='testV@fietsacademy.be'")
				.getSingleResult()).longValue();
	}
	private long idVanNieuweCampus() {
		manager.createNativeQuery("insert into campussen(naam,straat,huisNr,postCode,gemeente) values('test','test','test','test','test')").executeUpdate();
		return ((Number) manager.createNativeQuery("select id from campussen where naam='test'").getSingleResult()).longValue();
	}
	
	// *** TESTEN ***
	
	@Test
	public void read() {
		Optional<Docent> optionalDocent = repository.read(idVanNieuweMan());
		assertTrue(optionalDocent.isPresent());
		assertEquals("testM@fietsacademy.be", optionalDocent.get().getEmailAdres());
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
		manager.persist(campus);
		repository.create(docent);
		long autoNumberId = docent.getId();
		/*
		 * *** De private variabele id, die JPA heeft ingevuld, na het toevoegen van het nieuwe record, met het getal in de kolom,
		 * mag niet 0 zijn. ***
		 */
		assertNotEquals(0, autoNumberId);
		/*
		 * *** Als je het record terug zoekt met een SQL statement, mbv het id, dan moet het emailadres dat gevonden wordt in de db 
		 * gelijk zijn aan het emailadres van het Docent object dat werd meegegeven. ***
		 */
		assertEquals("test@fietsacademy.be",
				// Je geeft een parameter aan met een : teken gevolgd door de naam van de parameter.
				(String) manager.createNativeQuery("select emailadres from docenten where id = :id")	// retourneert Query
				// Je vult de parameter id in.
				.setParameter("id", autoNumberId)														// retourneert Query
				.getSingleResult());																	// retourneert Object
		/*
		 * "Many-to-one associatie"
		 * *** Je controleert of JPA het correcte campus id heeft ingevuld in het toegevoegde docenten record ***
		 */
		assertEquals(campus.getId(), ((Number) manager.createNativeQuery("select campusid from docenten where id = :id").setParameter("id", autoNumberId)
													.getSingleResult()).longValue());
		
		/*
		 * "One-to-many associatie"
		 * 
		 * *** Test om te tonen dat equals en hashcode gebaseerd op id niet lukken: ***
		 * de Set<Docent> in het Campus object, die deze methods oproept, vindt het Docent object niet meer in zijn verzameling.
		 */
		assertTrue(campus.getDocenten().contains(docent));
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
		// *** Juiste aantal ***
		long aantalDocenten = ((Number) manager.createNativeQuery("select count(*) from docenten where wedde between 1000 and 2000")
				.getSingleResult()).longValue();
		assertEquals(aantalDocenten, docenten.size());
		// *** Elke gevonden wedde w voldoet aan: 1000 <= w <= 2000
		BigDecimal duizend = BigDecimal.valueOf(1_000);
		BigDecimal tweeduizend = BigDecimal.valueOf(2_000);
		docenten.forEach(docent -> {
			assertTrue(docent.getWedde().compareTo(duizend) >= 0);
			assertTrue(docent.getWedde().compareTo(tweeduizend) <= 0);
		});
	}
	@Test
	public void findEmailAdressen() {
		idVanNieuweMan();
		List<String> adressen = repository.findEmailAdressen();
		// *** Juiste aantal ***
		long aantal = ((Number) manager.createNativeQuery("select count(emailadres) from docenten").getSingleResult()).longValue();
		assertEquals(aantal, adressen.size());
		// *** Elk gevonden resultaat is een emailadres
		adressen.forEach(adres -> assertTrue(adres.contains("@")));
	}
	@Test
	public void findIdsEnEmailAdressen() {
		idVanNieuweMan();
		List<IdEnEmailAdres> idsEnAdressen = repository.findIdsEnEmailAdressen();
		// *** Juiste aantal ***
		long aantal = ((Number) manager.createNativeQuery("select count(*) from docenten").getSingleResult()).longValue();
		assertEquals(aantal, idsEnAdressen.size());
	}
	@Test
	public void findGrootsteWedde() {
		idVanNieuweMan();
		BigDecimal grootsteWedde = repository.findGrootsteWedde();
		/*
		 * OPTIE 1 (mijn uitwerking)
		 * Met een andere "JpaDocentRepository method".
		 * Is ook goed.
		 * Gebruikt wel een andere method uit de te testen class: findAll, maar deze is hier bij mij al getest en mag dus gebruikt worden.
		 * Mogelijk nadeel (slechts een detail): als je deze manier gebruikt moet de method findAll al uitgewerkt zijn,
		 * je kan dan niet eerst de tests schrijven en dan de volgorde van de te implementeren methods kiezen.
		 */
		List<Docent> docenten = repository.findAll();
		docenten.forEach(docent -> assertTrue(docent.getWedde().compareTo(grootsteWedde) <= 0));
		/*
		 * OPTIE 2 (cursus)
		 * Met een "createNativeQuery"
		 */
		BigDecimal grootsteWedde2 = BigDecimal.valueOf(
				((Number) (manager.createNativeQuery("select max(wedde) from docenten").getSingleResult())).doubleValue());
		assertEquals(0, grootsteWedde.compareTo(grootsteWedde2));
		// (één van de twee mag in commentaar, maar het werkt met twee tests)
	}
	@Test
	public void findAantalDocentenPerWedde() {
		idVanNieuweMan();
		List<AantalDocentenPerWedde> aantalDocentenPerWedde = repository.findAantalDocentenPerWedde();
		// *** Juiste aantal weddes (rijen/resultaatobjecten) ***
		long aantalUniekeWeddes = ((Number) manager.createNativeQuery(
				"select count(distinct wedde) from docenten")
				.getSingleResult()).longValue();
		assertEquals(aantalUniekeWeddes, aantalDocentenPerWedde.size());
		// *** Juiste aantal docenten per (vb) wedde '1000'
		long aantalDocentenMetWedde1000 = ((Number) (manager.createNativeQuery(
				"select count(*) from docenten where wedde = 1000").getSingleResult())).longValue();
		aantalDocentenPerWedde.stream()
				.filter(aantalPerWedde -> aantalPerWedde.getWedde().compareTo(BigDecimal.valueOf(1000)) == 0)
				.forEach(aantalPerWedde -> assertEquals(aantalDocentenMetWedde1000, aantalPerWedde.getAantal()));
	}
	@Test
	public void algemeneOpslag() {
		long id = idVanNieuweMan();
		int aantalAangepast = repository.algemeneOpslag(BigDecimal.TEN);
		// *** Er moeten meer dan 0 records zijn gewijzigd ***
		assertNotEquals(0, aantalAangepast);
		// *** Controleren dat de wedde van de gekende Docent nu 1100 is
		BigDecimal wedde = BigDecimal.valueOf(((Number)
				manager.createNativeQuery("select wedde from docenten where id = :id")
				.setParameter("id", id)
				.getSingleResult()).doubleValue());
		assertEquals(0, BigDecimal.valueOf(1_100).compareTo(wedde));
	}
	
	// "Verzameling value objects met een basistype"
	@Test
	public void bijnamenLezen() {
		long id = idVanNieuweMan();
		manager.createNativeQuery("insert into docentenbijnamen(docentid,bijnaam) values(:id,'test')")
				.setParameter("id", id)
				.executeUpdate();
		Docent docent = repository.read(id).get();
		// *** aantal bijnamen van gelezen object is 1 ***
		assertEquals(1, docent.getBijnamen().size());
		// *** verzameling bijnamen bevat 'test' ***
		assertTrue(docent.getBijnamen().contains("test"));
	}
	@Test
	public void bijnaamToevoegen() {
		manager.persist(campus);	// "many-to-one associatie"
		repository.create(docent);
		docent.addBijnaam("test");
		assertEquals("test", (String) manager.createNativeQuery("select bijnaam from docentenbijnamen where docentid = :id")
											.setParameter("id", docent.getId())
											.getSingleResult());
	}
	// "many-to-one associatie": *** test die aantoont dat lazy loading werkt ***
	@Test
	public void campusLazyLoaded() {
		// JPA leest enkel een record uit de table docenten
		Docent docent = repository.read(idVanNieuweMan()).get();
		/*
		 * Je spreekt nu de campus aan die bij de docent hoort. 
		 * JPA leest nu het bijbehorende record uit de table campussen.
		 */
		assertEquals("test", docent.getCampus().getNaam());	
	}
	// "Many-to-many associatie"
	@Test
	public void verantwoordelijkhedenLezen() {
		// id van toegevoegd record in docenten verkrijgen
		long docentId = idVanNieuweMan();
		// verantwoordelijkheden record toevoegen in de database
		manager.createNativeQuery("insert into verantwoordelijkheden(naam) values('test')").executeUpdate();
		// id van toegevoegd record in verantwoordelijkheden verkrijgen
		long verantwoordelijkheidId = ((Number) manager.createNativeQuery("select id from verantwoordelijkheden where naam='test'").getSingleResult()).longValue();
		// docentenverantwoordelijkheden record toevoegen in de database
		manager.createNativeQuery("insert into docentenverantwoordelijkheden(docentId,verantwoordelijkheidId) values(:docentId,:verantwoordelijkheidId)")
			.setParameter("docentId", docentId)
			.setParameter("verantwoordelijkheidId", verantwoordelijkheidId)
			.executeUpdate();
		// Een Docent object lezen uit de database met het verkregen id
		Docent docent = repository.read(docentId).get();
		// *** juiste aantal verantwoordelijkheden bij het Docent object ***
		assertEquals(1, docent.getVerantwoordelijkheden().size());
		// *** verantwoordelijkheden in Docent object bevat de juiste verantwoordelijkheid ***
		assertTrue(docent.getVerantwoordelijkheden().contains(new Verantwoordelijkheid("test")));
	}
	@Test
	public void verantwoordelijkheidToevoegen() {
		// nieuw Verantwoordelijkheid object maken
		Verantwoordelijkheid verantwoordelijkheid = new Verantwoordelijkheid("test");
		
		// object opslaan in de db
		manager.persist(verantwoordelijkheid);
		// private variabele campus opslaan in de db
		manager.persist(campus);
		// private variabele docent opslaan in de db
		repository.create(docent);
		
		// Verantwoordelijkheid toevoegen aan Docent object
		docent.add(verantwoordelijkheid);
		
		// *** de toegevoegde Verantwoordelijkheid (aan docent) is in de database toegevoegd aan de table docentenverantwoordelijkheden ***
		assertEquals(verantwoordelijkheid.getId(),
					((Number) manager.createNativeQuery("select verantwoordelijkheidId from docentenverantwoordelijkheden where docentId = :id")
						.setParameter("id", docent.getId()).getSingleResult()).longValue());
	}
	
	// "N + 1 probleem"
	/*
	 * Volgende test simuleert de situatie waarbij je in een website de docenten van-tot wedde toont.
	 * Je roept daarbij de method findByWeddeBetween op.
	 * Je beslist in de JSP naast de naam en de wedde van de docent ook zijn campusnaam te tonen.
	 */
	@Test
	public void nPlus1Probleem() {
		idVanNieuweMan();
		List<Docent> docenten = repository.findByWeddeBetween(BigDecimal.ZERO, BigDecimal.valueOf(1_000_000));
		
		docenten.forEach(docent -> System.out.println(docent.getFamilienaam() + ':' + docent.getWedde() + ' ' + docent.getCampus().getNaam()));
	}
}
