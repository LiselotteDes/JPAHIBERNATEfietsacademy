package be.vdab.fietsacademy.repositories;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

/*
 * Annotaties 
 * - @DataJpaTest is vergelijkbaar met @JdbcTest.
 *   @DataJpaTest maakt enkel een DataSource bean en een EntityManager bean. Zo wordt de test snel uitgevoerd.
 *   
 * - @AutoConfigureTestDatabase:
 *   @DataJpaTest voert de tests standaard niet uit met de db beschreven in application.properties: een MySQL database.
 *   @DataJpaTest voert de tests standaard uit met een in-memory database. Zo'n db houdt de data bij in het interne geheugen, niet op de schijf.
 *   Dit houdt in dat als je programma crasht of stopt je alle data verliest. Dit is ontoelaatbaar in productie, gedurende een test kan het wel.
 *   Het gebruik van een in-memory db versnelt je test. (in volgende cursus)
 *   Hier test je nog met je MySQL db.
 *   Je geeft met deze annotatie aan dat Spring in je test de MySQL db niet moet vervangen door een in-memory db.
 */

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class DataSourceTest {
	@Autowired
	private DataSource dataSource;
	@Test
	public void getConnection() throws SQLException {
		try (Connection connection = dataSource.getConnection()) {
		}
	}

}
