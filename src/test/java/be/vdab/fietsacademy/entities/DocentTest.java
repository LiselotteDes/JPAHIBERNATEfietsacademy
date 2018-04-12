package be.vdab.fietsacademy.entities;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import be.vdab.fietsacademy.enums.Geslacht;

public class DocentTest {
	private static final BigDecimal ORIGINELE_WEDDE = BigDecimal.valueOf(200);
	private Docent docent1;
	@Before
	public void before() {
		docent1 = new Docent("test", "test", ORIGINELE_WEDDE, "test@fietsacademy.be", Geslacht.MAN);
	}
	@Test
	public void opslag() {
		docent1.opslag(BigDecimal.TEN);
		// !! Gebruik 0 en compareTo om de gelijkheid van 2 BigDecimals te controleren.
		assertEquals(0, BigDecimal.valueOf(220).compareTo(docent1.getWedde()));
	}
	@Test(expected = NullPointerException.class)
	public void opslagMetNullKanNiet() {
		docent1.opslag(null);
		// *** wedde docent mag niet gewijzigd zijn: *** 
		assertEquals(0, ORIGINELE_WEDDE.compareTo(docent1.getWedde()));
	}
	@Test(expected = IllegalArgumentException.class)
	public void negatieveOpslagKanNiet() {
		docent1.opslag(BigDecimal.valueOf(-1));
		assertEquals(0, ORIGINELE_WEDDE.compareTo(docent1.getWedde()));
	}
	
	//  "Verzameling value objects met een basistype" Tests
	@Test
	public void eenNieuweDocentHeeftGeenBijnamen() {
		assertTrue(docent1.getBijnamen().isEmpty());
	}
	@Test
	public void bijnaamToevoegen() {
		// *** Toevoegen lukt *** 
		assertTrue(docent1.addBijnaam("test"));
		// *** Na verzameling bevat, na het toevoegen van een bijnaam bij nieuwe docent, 1 bijnaam *** 
		assertEquals(1, docent1.getBijnamen().size());
		// *** De set bijnamen bevat de toegevoegde bijnaam *** 
		assertTrue(docent1.getBijnamen().contains("test"));
	}
	@Test
	public void tweeKeerDezelfdeBijnaamToevoegenKanNiet() {
		docent1.addBijnaam("Test");
		assertFalse(docent1.addBijnaam("Test"));
		assertEquals(1, docent1.getBijnamen().size());
	}
	@Test(expected = NullPointerException.class)
	public void nullAlsBijnaamToevoegenKanNiet() {
		docent1.addBijnaam(null);
	}
	@Test(expected = IllegalArgumentException.class)
	public void eenLegeBijnaamToevoegenKanNiet() {
		docent1.addBijnaam("");
	}
	@Test(expected = IllegalArgumentException.class)
	public void eenBijnaamMetEnkelSpatiesToevoegenKanNiet() {
		docent1.addBijnaam("   ");
	}
	@Test
	public void bijnaamVerwijderen() {
		docent1.addBijnaam("test");
		// *** bijnaam verwijderen die in de set zit lukt en vermindert de omvang van de set ***
		assertTrue(docent1.removeBijnaam("test"));
		assertTrue(docent1.getBijnamen().isEmpty());
	}
	@Test
	public void eenBijnaamVerwijderenDieJeNietToevoegdeKanNiet() {
		docent1.addBijnaam("test");
		// *** bijnaam verwijderen die niet in de set zit geeft false en wijzigt niets aan de lengte van de set ***
		assertFalse(docent1.removeBijnaam("test2"));
		assertEquals(1, docent1.getBijnamen().size());
		// *** de set bevat nog steeds de toegevoegde bijnaam die niet verwijderd werd
		assertTrue(docent1.getBijnamen().contains("test"));
	}
}
