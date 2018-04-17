package be.vdab.fietsacademy.entities;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import be.vdab.fietsacademy.enums.Geslacht;
import be.vdab.fietsacademy.valueobjects.Adres;

public class VerantwoordelijkheidTest {
	
	private Verantwoordelijkheid verantwoordelijkheid1;
	private Docent docent1;
	private Campus campus1;
	
	@Before
	public void before() {
		verantwoordelijkheid1 = new Verantwoordelijkheid("EHBO");
		campus1 = new Campus("test", new Adres("test", "test", "test", "test"));
		docent1 = new Docent("test", "test", BigDecimal.TEN, "test@fietsacademy.be", Geslacht.VROUW, campus1);
	}
	
	// "Many-to-many associatie
	@Test
	public void docentToevoegen() {
		// *** Er zijn nog geen docenten van het Verantwoordelijkheid object ***
		assertTrue(verantwoordelijkheid1.getDocenten().isEmpty());
		// *** Er kan een docent toegevoegd worden ***
		assertTrue(verantwoordelijkheid1.add(docent1));
		// *** Verantwoordelijkheid object heeft juiste aantal docenten ***
		assertEquals(1, verantwoordelijkheid1.getDocenten().size());
		// *** In de docenten van het Verantwoordelijkheid object bevindt zich de toegevoegde docent ***
		assertTrue(verantwoordelijkheid1.getDocenten().contains(docent1));
		// *** Docent object heeft juiste aantal verantwoordelijkheden ***
		assertEquals(1, docent1.getVerantwoordelijkheden().size());
		// *** In de verantwoordelijkheden van het Docent object bevindt zich dit Verantwoordelijkheid object ***
		assertTrue(docent1.getVerantwoordelijkheden().contains(verantwoordelijkheid1));
	}
	@Test
	public void docentVerwijderen() {
		// *** Er kan een docent toegevoegd worden ***
		assertTrue(verantwoordelijkheid1.add(docent1));
		// *** De docent kan terug verwijderd worden ***
		assertTrue(verantwoordelijkheid1.remove(docent1));
		// *** De Set docenten van het Verantwoordelijkheid object is terug leeg ***
		assertTrue(verantwoordelijkheid1.getDocenten().isEmpty());
		// *** De Set verantwoordelijkheden van het toegevoegde/verwijderde Docent object is terug leeg ***
		assertTrue(docent1.getVerantwoordelijkheden().isEmpty());
	}
}
