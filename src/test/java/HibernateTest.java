import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import domain.Company;
import domain.Country;
import domain.Person;

/**
 * This test is run with Spring. Spring makes sure we have a HSQL DB to play
 * with, a Hibernate configuration and a datasource to use.
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:context.xml" })
@Transactional
public class HibernateTest {

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private DataSource dataSource;

	@Test
	public void shouldPersistBasicDomain() {
		Long personId = 1L;
		Country norway = createDefaultTestCountry();
		Person johnny = createDefaultTestPerson(personId, norway);
		
		Session session = getSession();
		session.save(norway);
		session.save(johnny);
		
		HibernateUtil.flushAndClearCaches(session);
		
		Person savedPerson = (Person) session.get(Person.class, personId);
		
		assertNotNull(savedPerson);
		assertEquals(johnny.getName(), savedPerson.getName());
	}

	@Test
	public void shouldPersistDomainWithRelations() {
		Long personId = 1L;
		Long companyId = 1L;
		Country norway = createDefaultTestCountry();
		Company nydra = new Company(companyId, "Nydra International", norway);
		Person johnny = createDefaultTestPerson(personId, norway);
		
		johnny.addJob("Chief engineer", nydra);
		
		Session session = getSession();
		session.save(norway);
		session.save(nydra);
		session.save(johnny);
		
		HibernateUtil.flushAndClearCaches(session);
		
		Person savedPerson = (Person) session.get(Person.class, personId);
		
		assertNotNull(savedPerson);
		assertTrue(savedPerson.hasAJob());
	}

	private Person createDefaultTestPerson(Long personId, Country countryOfResidence) {
		return new Person(personId, "Johnny Nilsson", countryOfResidence);
	}

	private Country createDefaultTestCountry() {
		return new Country("NO", "Norway");
	}

	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	/**
	 * Even though Spring creates transactions for us, it does not force a flush
	 * after running the test. That means that some of the database operations
	 * will NOT be tested. So we force a flush at the end to make sure all
	 * statements are tested against the database.
	 */
	@After
	public void flushToTest() {
		getSession().flush();
	}

}
