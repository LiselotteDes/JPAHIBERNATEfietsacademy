package be.vdab.fietsacademy.repositories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import javax.persistence.EntityManager;

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
import be.vdab.fietsacademy.valueobjects.Adres;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import(JpaVerantwoordelijkheidRepository.class)
public class JpaVerantwoordelijkheidRepositoryTest {
	
	@Autowired
	private EntityManager manager;
	@Autowired
	private JpaVerantwoordelijkheidRepository repository;
	
	
	private long idVanNieuweVerantwoordelijkheid() {
		manager.createNativeQuery("insert into verantwoordelijkheden(naam) values('test')").executeUpdate();
		return ((Number) manager.createNativeQuery("select id from verantwoordelijkheden where naam='test'").getSingleResult()).longValue();
	}
	
	// *** Interface methods testen ***
	
	@Test
	public void read() {
		Verantwoordelijkheid verantwoordelijkheid = repository.read(idVanNieuweVerantwoordelijkheid()).get();
		
		assertEquals("test", verantwoordelijkheid.getNaam());
	}
	@Test
	public void create() {
		Verantwoordelijkheid verantwoordelijkheid = new Verantwoordelijkheid("test");
		repository.create(verantwoordelijkheid);
		long id = verantwoordelijkheid.getId();
		
		assertEquals("test", (String) (manager.createNativeQuery("select naam from verantwoordelijkheden where id = :id").setParameter("id", id).getSingleResult()));
	}
	
	// *** Omdat Verantwoordelijkheid deel uitmaakt van een many-to-many associatie met Docent ook volgende tests. ***
	
	@Test
	public void docentenLezen() {
		// 2 id's nodig: 1° id van verantwoordelijkheden record
		long verantwoordelijkheidId = idVanNieuweVerantwoordelijkheid();
		// TUSSENSTAP: zorgen dat er minstens 1 record zit in docenten, en daarvoor 1° zorgen dat er een campussen record bestaat om het id ervan mee te geven
		manager.createNativeQuery("insert into campussen(naam,straat,huisNr,postcode,gemeente) values('test','test','test','test','test')").executeUpdate();
		long campusId = ((Number) manager.createNativeQuery("select id from campussen where naam='test'").getSingleResult()).longValue();
		manager.createNativeQuery("insert into docenten(voornaam,familienaam,wedde,emailAdres,geslacht,campusid) "
								+ "values('test','test',1000,'test@fietsacadmey.be','VROUW',:campusid)").setParameter("campusid", campusId).executeUpdate();
		// 2 id's nodig: 2° id van docenten record
		long docentId = ((Number) manager.createNativeQuery("select id from docenten where emailAdres='test@fietsacadmey.be'").getSingleResult()).longValue();
		
		// record in de tussentable voegen dat de twee verbindt
		manager.createNativeQuery("insert into docentenverantwoordelijkheden(docentId,verantwoordelijkheidId) values(:docentId,:verantwoordelijkheidId)")
				.setParameter("docentId", docentId).setParameter("verantwoordelijkheidId", verantwoordelijkheidId).executeUpdate();
		
		Verantwoordelijkheid verantwoordelijkheid = repository.read(verantwoordelijkheidId).get();
		
		assertEquals(1,verantwoordelijkheid.getDocenten().size());
		assertTrue(verantwoordelijkheid.getDocenten().contains(new Docent(
				"test", "test", BigDecimal.valueOf(1000), "test@fietsacadmey.be", Geslacht.VROUW, new Campus(
						"test", new Adres("test", "test", "test", "test")))));
	}
	@Test
	public void docentenToevoegen() {
		Verantwoordelijkheid verantwoordelijkheid = new Verantwoordelijkheid("test");
		repository.create(verantwoordelijkheid);
		
		Campus campus = new Campus("test", new Adres("test", "test", "test", "test"));
		Docent docent = new Docent("test", "test", BigDecimal.valueOf(1000), "test@fietsacademy.be", Geslacht.VROUW, campus);
		manager.persist(campus);
		manager.persist(docent);
		
		verantwoordelijkheid.add(docent);
		
		assertEquals(docent.getId(), ((Number) manager.createNativeQuery(
												"select docentId from docentenverantwoordelijkheden where verantwoordelijkheidId=:id")
												.setParameter("id", verantwoordelijkheid.getId()).getSingleResult()).longValue());
		
	}
}
