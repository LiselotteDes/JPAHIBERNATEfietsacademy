package be.vdab.fietsacademy.repositories;

import static org.junit.Assert.assertNotNull;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class EntityManagerTest {
	// Je injecteert de EntityManager bean die Spring automatisch maakt
	@Autowired
	private EntityManager manager;
	@Test
	public void initialisatie() {
		// Je controleert of de variabele manager wel degelijk naar een object verwijst.
		assertNotNull(manager);
	}

}
