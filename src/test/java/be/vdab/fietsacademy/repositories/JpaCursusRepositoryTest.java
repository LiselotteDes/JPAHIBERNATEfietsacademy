package be.vdab.fietsacademy.repositories;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
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

import be.vdab.fietsacademy.entities.Cursus;
import be.vdab.fietsacademy.entities.GroepsCursus;
import be.vdab.fietsacademy.entities.IndividueleCursus;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import(JpaCursusRepository.class)
public class JpaCursusRepositoryTest {
	@Autowired
	private EntityManager manager;
	@Autowired
	private JpaCursusRepository repository;
	
	private String idVanNieuweGroepsCursus() {
		// "Table per class"
//		manager.createNativeQuery("insert into cursussen(naam,van,tot,soort) values('testGroep','2019-01-01','2019-01-10','G')").executeUpdate();
//		return ((Number) manager.createNativeQuery("select id from cursussen where naam='testGroep'").getSingleResult()).longValue();
		
		// "Table per subclass"
//		manager.createNativeQuery("insert into cursussen(naam) values('testGroep')").executeUpdate();
//		long id = ((Number) manager.createNativeQuery("select id from cursussen where naam='testGroep'").getSingleResult()).longValue();
//		manager.createNativeQuery("insert into groepscursussen(id,van,tot) values(:id, '2019-01-01','2019-01-10')")
//			.setParameter("id", id).executeUpdate();
//		return id;
		
		// "Table per concrete class"
		manager.createNativeQuery("insert into groepscursussen(id,naam,van,tot) values(uuid(),'testGroep','2018-01-01','2018-01-01')").executeUpdate();
		return (String) manager.createNativeQuery("select id from groepscursussen where naam='testGroep'").getSingleResult(); 
	}
	private String idVanNieuweIndividueleCursus() {
		// "Table per class"
//		manager.createNativeQuery("insert into cursussen(naam,duurtijd,soort) values('testIndividueel',3,'I')").executeUpdate();
//		return ((Number) manager.createNativeQuery("select id from cursussen where naam='testIndividueel'").getSingleResult()).longValue();
		
		// "Table per subclass"
//		manager.createNativeQuery("insert into cursussen(naam) values('testIndividueel')").executeUpdate();
//		long id = ((Number) manager.createNativeQuery("select id from cursussen where naam='testIndividueel'").getSingleResult()).longValue();
//		manager.createNativeQuery("insert into individuelecursussen(id,duurtijd) values(:id, 3)").setParameter("id", id).executeUpdate();
//		return id;
		
		// "Table per concrete class"
		manager.createNativeQuery("insert into individuelecursussen(id,naam,duurtijd) values(uuid(),'testIndividueel',3)").executeUpdate();
		return (String) manager.createNativeQuery("select id from individuelecursussen where naam='testIndividueel'").getSingleResult();
	}
	
	@Test
	public void readGroepsCursus() {
		Optional<Cursus> optionalCursus = repository.read(idVanNieuweGroepsCursus());
		assertEquals("testGroep",((GroepsCursus) optionalCursus.get()).getNaam());
	}
	@Test
	public void readIndividueleCursus() {
		Optional<Cursus> optionalCursus = repository.read(idVanNieuweIndividueleCursus());
		assertEquals("testIndividueel", ((IndividueleCursus) optionalCursus.get()).getNaam());
	}
	
	@Test
	public void createGroepsCursus() {
		GroepsCursus cursus = new GroepsCursus("test", LocalDate.of(2018, 1, 1), LocalDate.of(2018, 10, 1));
		repository.create(cursus);
//		long id = cursus.getId();										// Table per class & Table per subclass
		assertEquals("test", (String) manager.createNativeQuery(
//				"select naam from cursussen where id = :id")			// Table per class & Table per subclass
				"select naam from groepscursussen where id = :id")		// Table per concrete class
				.setParameter("id", cursus.getId())						// Table per concrete class
				.getSingleResult());
	}
	@Test
	public void createIndividueleCursus() {
		IndividueleCursus cursus = new IndividueleCursus("test", 7);
		repository.create(cursus);
		assertEquals("test", (String) manager.createNativeQuery(
//				"select naam from cursussen where id = :id")			// Table per class & Table per subclass
				"select naam from individuelecursussen where id = :id")	//Table per concrete class
				.setParameter("id", cursus.getId())
				.getSingleResult());
	}
}
