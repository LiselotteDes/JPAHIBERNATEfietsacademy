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
		// wedde docent mag niet gewijzigd zijn:
		assertEquals(0, ORIGINELE_WEDDE.compareTo(docent1.getWedde()));
	}
	@Test(expected = IllegalArgumentException.class)
	public void negatieveOpslagKanNiet() {
		docent1.opslag(BigDecimal.valueOf(-1));
		assertEquals(0, ORIGINELE_WEDDE.compareTo(docent1.getWedde()));
	}

}
