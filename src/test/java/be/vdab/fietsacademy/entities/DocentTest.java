package be.vdab.fietsacademy.entities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import be.vdab.fietsacademy.enums.Geslacht;
import be.vdab.fietsacademy.valueobjects.Adres;

public class DocentTest {
	private static final BigDecimal ORIGINELE_WEDDE = BigDecimal.valueOf(200);
	private Docent docent1, nogEensDocent1, docent2;
	private Campus campus1, campus2;
	private Verantwoordelijkheid verantwoordelijkheid1;
	
	@Before
	public void before() {
		campus1 = new Campus("test", new Adres("test", "test", "test", "test"));
		campus2 = new Campus("test2", new Adres("test2", "test2", "test2", "test2"));
		docent1 = new Docent("test", "test", ORIGINELE_WEDDE, "test@fietsacademy.be", Geslacht.MAN, campus1);
		/*
		 * "one-to-many associatie" aantonen dat equals en hashcode gebaseerd op id niet werken: 
		 * Gezien als deze Docent objecten als id 0 hebben,
		 * laat de Set<Docent> in het Campus object niet meerdere keren nieuwe Docent objecten toe.
		 */
		docent2 = new Docent("test2", "test2", ORIGINELE_WEDDE, "test2@fietsacademy.be", Geslacht.MAN, campus1);	
		// Om equals en hashCode ten gronde te kunnen testen:
		nogEensDocent1 = new Docent("test", "test", ORIGINELE_WEDDE, "test@fietsacademy.be", Geslacht.MAN, campus1);
		// "Many-to-many associatie"
		verantwoordelijkheid1 = new Verantwoordelijkheid("EHBO");
	}
	
	// "Entity wijzigen": *** method opslag ***
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
	
	/*
	 * "One-to-many associatie: Set en de methods equals en hashcode"
	 * *** Test die aantoont dat een Set met objecten die een equals/hashcode method gebaseerd op id, niet meerdere objecten kan bevatten ***
	 */
	@Test
	public void eenCampusKanMeerdereDocentenBevatten() {
//		assertTrue(campus1.addDocent(docent1));
//		assertTrue(campus1.addDocent(docent2));
		/*
		 * De voorgaande test mislukt op de 2° assertTrue als equals/hashcode op id gebaseerd zijn: 
		 * Java beslist op basis van de equals method in Docent dat er al eenzelfde docent aanwezig is in de Set in Campus:
		 * die geeft aan dat docent2 gelijk is aan docent1: ze hebben beiden 0 als id.
		 */
		
		// "Bidirectionele associatie"
		assertTrue(campus1.getDocenten().contains(docent1));
		assertTrue(campus1.getDocenten().contains(docent2));
	}
	
	// *** equals en hashCode ten gronde testen ***
	@Test
	public void docentenZijnGelijkAlsHunEmailAdressenGelijkZijn() {
		assertEquals(docent1, nogEensDocent1);
	}
	@Test
	public void docentenZijnVerschillendAlsHunEmailAdressenVerschillen() {
		assertNotEquals(docent1, docent2);
	}
	@Test
	public void eenDocentVerschiltVanNull() {
		assertNotEquals(docent1, null);
	}
	@Test
	public void eenDocentVerschiltVanEenAnderTypeObject() {
		assertNotEquals(docent1, "");
	}
	@Test
	public void gelijkeDocentenGevenDezelfdeHashCode() {
		assertEquals(docent1.hashCode(), nogEensDocent1.hashCode());
	}
	
	// *** Bidirectionele associatie (vanuit het standpunt van Docent) ***
	@Test
	public void docent1MoetOokVoorkomenInSetVanCampus1() {
		assertEquals(docent1.getCampus(), campus1);
		assertEquals(2, campus1.getDocenten().size());
		assertTrue(campus1.getDocenten().contains(docent1));
	}
	@Test
	public void docent1VerhuistVanCampus1NaarCampus2() {
		docent1.setCampus(campus2);
		assertEquals(docent1.getCampus(), campus2);
		assertEquals(1, campus1.getDocenten().size());
		assertEquals(1, campus2.getDocenten().size());
		assertTrue(campus2.getDocenten().contains(docent1));
	}
	
	// "Many-to-many associatie"
	@Test
	public void verantwoordelijkheidToevoegen() {
		// *** Er zijn nog geen verantwoordelijkheden voor het Docent object ***
		assertTrue(docent1.getVerantwoordelijkheden().isEmpty());
		// *** Er kan een verantwoordelijkheid toegevoegd worden ***
		assertTrue(docent1.add(verantwoordelijkheid1));
		// *** Docent object heeft juiste aantal verantwoordelijkheden ***
		assertEquals(1, docent1.getVerantwoordelijkheden().size());
		// *** In de verantwoordelijkheden van het Docent object bevindt zich de toegevoegde verantwoordelijkheid ***
		assertTrue(docent1.getVerantwoordelijkheden().contains(verantwoordelijkheid1));
		// *** Verantwoordelijkheid object heeft juiste aantal docenten ***
		assertEquals(1, verantwoordelijkheid1.getDocenten().size());
		// *** In de docenten van het Verantwoordelijkheid object bevindt zich dit Docent object ***
		assertTrue(verantwoordelijkheid1.getDocenten().contains(docent1));
	}
	@Test
	public void verantwoordelijkheidVerwijderen() {
		// *** Er kan een verantwoordelijkheid toegevoegd worden ***
		assertTrue(docent1.add(verantwoordelijkheid1));
		// *** De verantwoordelijkheid kan terug verwijderd worden ***
		assertTrue(docent1.remove(verantwoordelijkheid1));
		// *** De Set verantwoordelijkheden van het Docent object is terug leeg ***
		assertTrue(docent1.getVerantwoordelijkheden().isEmpty());
		// *** De Set docenten van het toegevoegde/verwijderde Verantwoordelijkheid object is terug leeg ***
		assertTrue(verantwoordelijkheid1.getDocenten().isEmpty());
	}
}
