package be.vdab.fietsacademy.services;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

import be.vdab.fietsacademy.entities.Docent;
import be.vdab.fietsacademy.enums.Geslacht;
import be.vdab.fietsacademy.exceptions.DocentNietGevondenException;
import be.vdab.fietsacademy.repositories.DocentRepository;

// UNIT TESTS
public class DefaultDocentServiceTest {
	private DefaultDocentService service;
	private DocentRepository repository;
	private Docent docent;
	@Before
	public void before() {
		docent = new Docent("test", "test", BigDecimal.valueOf(100), "test@fietsacademy.be", Geslacht.MAN);
		repository = mock(DocentRepository.class);
		when(repository.read(1)).thenReturn(Optional.of(docent));
		when(repository.read(-1)).thenReturn(Optional.empty());
		service = new DefaultDocentService(repository);
	}
	@Test
	public void opslag() {
		service.opslag(1, BigDecimal.TEN);
		assertEquals(0, BigDecimal.valueOf(110).compareTo(docent.getWedde()));
		verify(repository).read(1);
	}
	@Test(expected = DocentNietGevondenException.class)
	public void opslagVoorOnbestaandeDocent() {
		service.opslag(-1, BigDecimal.TEN);
		verify(repository).read(-1);
	}

}
