package be.vdab.fietsacademy.services;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import be.vdab.fietsacademy.entities.Campus;
import be.vdab.fietsacademy.entities.Docent;
import be.vdab.fietsacademy.enums.Geslacht;
import be.vdab.fietsacademy.repositories.DocentRepository;
import be.vdab.fietsacademy.valueobjects.Adres;

/*
 * Je maakt een integration test om daarin te testen wat je in de unit test niet kan testen:
 * is de docent niet enkel gewijzigd in het interne geheugen, maar ook in de database?
 */
@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
/*
 * @ComponentScan scant alle classes uit een bepaalde package.
 * Je geeft één (om het even welke) van de classes uit de package mee als parameter.
 * @ComponentScan maakt een bean van elke class uit die package, als die class voorzien is van @Component, @Repository, @Service of @Controller.
 * @ComponentScan zal hier een bean maken van JpadocentRepository.
 * Je kan geen bean maken van JpaDocentRepository met @Import:
 * JpaDocentRepository heeft package visibility en is dus niet zichtbaar in de huidige code.
 */
@ComponentScan(basePackageClasses = be.vdab.fietsacademy.repositories.JpaDocentRepositoryTest.class)
@Import(DefaultDocentService.class)
public class DefaultDocentServiceIntegrationTest {
	@Autowired
	private DefaultDocentService service;
	@Autowired
	/*
	 * Je injecteert de bean die DocentRepository implementeert: JpaDocentRepository.
	 * Je gebruikt deze bean om in je test een docent toe te voegen en een docent te lezen.
	 * Dit toevoegen en lezen ZELF is correct: je hebt het uitgetest in JpaDocentRepositoryTest.
	 */
	private DocentRepository repository;
	@Autowired
	private EntityManager manager;
	@Test
	public void opslag() {
		Campus campus = new Campus("test", new Adres("test", "test", "test", "test"));
		manager.persist(campus);
		Docent docent = new Docent("test", "test", BigDecimal.valueOf(200), "test@fietsacademy.be", Geslacht.MAN, campus);
		repository.create(docent);
		long id = docent.getId();	// create gebruikt persist method van de EntityManager, waardoor JPA zelf het id variabele van het object invult.
		/*
		 * Je roept de method opslag op. Deze method wordt normaal uitgevoerd binnen een transactie, want de method is voorzien van @Transactional.
		 * De method wordt nu echter uitgevoerd binnen een test.
		 * De method wordt dan uitgevoerd binnen de transactie van de test, die op het einde van de test gerollbacked wordt.
		 */
		service.opslag(id, BigDecimal.TEN);
		/*
		 * Je hebt op de vorige regel de docent gewijzigd.
		 * De EntityManager voert wijzigingen niet onmiddelijk uit, maar spaart die op tot juist voor de commit van de transactie.
		 * Hij stuurt dan alle opgespaarde wijzigingen in één keer naar de database, via JDBC batch updates. Dit verhoogt de performantie.
		 * In deze test is het wel nodig dat de docent onmiddelijk gewijzigd wordt, zodat we de correcte werking van onze opslag method kunnen testen.
		 * Je voert de flush method uit die gevraagde wijzigingen onmiddelijk uitvoert.
		 */
		manager.flush();
		/*
		 * Je leest op de volgende regel de docent uit de database. De EntityManager gedraagt zich echter als een cache: 
		 * als je in een transactie een entity leest uit de database (in de method opslag), 
		 * en je leest dezelfde entity verder in die transactie nog een keer uit de database (volgende regel),
		 * leest de EntityManager de entity geen tweede keer uit de database, maar leest hem uit een interne cache.
		 * Dit bevordert de performantie.
		 * In deze test is het echter belangrijk dat de docent uit de database gelezen wordt.
		 * Je maakt daarom de cache van de EntityManager leeg met de clear method.
		 * De EntityManager is nu verplicht de docent die je op de volgende regel leest uit de database te lezen.
		 */
		manager.clear();
		docent = repository.read(id).get();
		assertEquals(0, BigDecimal.valueOf(220).compareTo(docent.getWedde()));
	}

}
