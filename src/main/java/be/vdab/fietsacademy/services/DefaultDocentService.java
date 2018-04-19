package be.vdab.fietsacademy.services;
import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import be.vdab.fietsacademy.entities.Docent;
import be.vdab.fietsacademy.exceptions.DocentNietGevondenException;
import be.vdab.fietsacademy.repositories.DocentRepository;

@Service
@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
class DefaultDocentService implements DocentService {
	private final DocentRepository docentRepository;
	DefaultDocentService (DocentRepository docentRepository) {
		this.docentRepository = docentRepository;
	}
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED)
	public void opslag(long id, BigDecimal percentage) {
		// Je wijzigt een entity in twee stappen: eerst lees je de te wijzigen entity
//		Optional<Docent> optionalDocent = docentRepository.read(id);
		Optional<Docent> optionalDocent = docentRepository.readWithLock(id);	// "Pessimistic record locking"
		if (optionalDocent.isPresent()) {
			// Vervolgens wijzig je de private variabelen van die entity.
			optionalDocent.get().opslag(percentage);
			/*
			 * JPA stuurt dan, bij de commit op deze transactie, automatisch een update statement naar de database 
			 * en wijzigt hiermee het record dat bij de gewijzigde entity hoort.
			 * Je hoeft dus zelf geen update statement naar de database te sturen !
			 */
		} else {
			throw new DocentNietGevondenException();
		}
	}
}
