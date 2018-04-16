package be.vdab.fietsacademy.repositories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
import be.vdab.fietsacademy.valueobjects.Adres;
import be.vdab.fietsacademy.valueobjects.TelefoonNr;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import(JpaCampusRepository.class)
public class JpaCampusRepositoryTest {
	
	@Autowired
	private JpaCampusRepository repository;
	@Autowired
	private EntityManager manager;
	private Campus campus;
	
	@Before
	public void before() {
		campus = new Campus("test", new Adres("test", "test", "test", "test"));
	}
	
	private long idVanNieuweCampus() {
		manager.createNativeQuery("insert into campussen(naam,straat,huisNr,postCode,gemeente) values('test','test','test','test','test')")
				.executeUpdate();
		return ((Number) manager.createNativeQuery("select id from campussen where naam = 'test'").getSingleResult()).longValue();
	}
	
	@Test
	public void read() {
		Campus campus = repository.read(idVanNieuweCampus()).get();
		// Test of het attribuut van het entity object juist gelezen wordt:
		assertEquals("test", campus.getNaam());
		// Test of het attribuut van het bijbehorende value object juist gelezen wordt
		assertEquals("test", campus.getAdres().getGemeente());
	}

	@Test
	public void create() {
		repository.create(campus);
		long id = campus.getId();
		// Test of het attribuut van het entity object juist weggeschreven werd
		String naam = (String)(manager.createNativeQuery("select naam from campussen where id = :id").setParameter("id", id).getSingleResult());
		assertEquals("test", naam);
		// Test of het attribuut van het bijbehorende value object juist weggeschreven werd
		String gemeente = (String) (manager.createNativeQuery("select gemeente from campussen where id = :id").setParameter("id", id).getSingleResult());
		assertEquals("test", gemeente);
	}
	
	// "Verzameling value objects met een eigen type lezen uit de database"
	@Test
	public void telefoonNrsLezen() {
		long id = idVanNieuweCampus();
		manager.createNativeQuery("insert into campussentelefoonnrs(campusid,nummer,fax,opmerking) values(:id,'1',false,'')")
				.setParameter("id", id).executeUpdate();
		Campus campus = repository.read(id).get();
		assertEquals(1, campus.getTelefoonNrs().size());
		assertTrue(campus.getTelefoonNrs().contains(new TelefoonNr("1", false, "")));
	}
	
	// "One-to-many is lazy loading"
	@Test
	public void docentenLazyLoaded() {
		long id = idVanNieuweCampus();
		manager.createNativeQuery("insert into docenten(voornaam,familienaam,wedde,emailAdres,geslacht,campusId)"
				+ " values('test','test',1000,'test@fietsacademy.be','MAN',:campusId)").setParameter("campusId", id).executeUpdate();
		Campus campus = repository.read(id).get();
		/*
		 * Als je de test uitvoert, zie je in het venster Console dat JPA bij bovenstaande enkel een record uit de table campussen leest.
		 * JPA leest pas bij het statement hieronder de records uit de table docenten die bij de campus horen.
		 */
		assertEquals(1, campus.getDocenten().size());
		assertEquals("test", campus.getDocenten().stream().findFirst().get().getVoornaam());
	}
}
