package be.vdab.fietsacademy.repositories;

import static org.junit.Assert.assertEquals;

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
import be.vdab.fietsacademy.valueobjects.Adres;

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
}
