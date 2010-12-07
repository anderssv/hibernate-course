import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.persistence.OptimisticLockException;
import javax.sql.DataSource;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.StaleObjectStateException;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.StatementCallback;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.test.AssertThrows;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.SimpleJdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import domain.Company;
import domain.Country;
import domain.Person;
import domain.SocialSecurityNumber;

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
		Person johnny = createDefaultTestPerson(personId, norway).build();

		Session session = getSession();
		session.save(norway);
		session.save(johnny);

		session.flush();
		session.clear();

		Person savedPerson = (Person) session.get(Person.class, personId);

		assertNotNull(savedPerson);
		assertEquals(johnny.getName(), savedPerson.getName());
	}

	@Test
	public void shouldPersistDomainWithRelations() {
		Long personId = 1L;
		Long companyId = 1L;

		Country norway = createDefaultTestCountry();
		Company nydra = createDefaultCompany(companyId, norway).build();
		Person.Builder leifBuilder = createDefaultTestPerson(personId, norway)
				.name("Leif Andersson").socialSecurityNumber(
						new SocialSecurityNumber("10987654321"));
		Person anders = leifBuilder.build();

		anders.addJob("Chief engineer", nydra);

		Session session = getSession();
		session.save(norway);
		session.save(nydra);
		session.save(anders);

		session.flush();
		session.clear();

		Person savedPerson = (Person) session.get(Person.class, personId);

		assertNotNull(savedPerson);
		assertTrue(savedPerson.hasAJob());
		assertFalse(tableExists("PERSON_JOB"));
	}

	@Test
	public void shouldFetchOnlyPersonsWithoutAJob() {
		Session session = getSession();

		Country norway = createDefaultTestCountry();
		generatePersonsInTheDatabaseWithJobs(session, 0, 20, norway);
		generatePersonsInTheDatabaseWithoutJobs(session, 20, 20, norway);

		session.flush();

		// TODO Count number of persons in DB with criteria
		Criteria countCriteria = session.createCriteria(Person.class);
		countCriteria.setProjection(Projections.count("id"));
		assertEquals(40, countCriteria.uniqueResult());

		// TODO Fetch only the persons with no jobs
		Criteria unemployedCriteria = session.createCriteria(Person.class);
		unemployedCriteria.add(Restrictions.isEmpty("jobs"));

		List<Person> unemployedPersons = unemployedCriteria.list();
		for (Person person : unemployedPersons) {
			assertFalse(person.hasAJob());
		}
	}

	@Test
	public void shouldUpdateDomain() {
		Long personId = 1L;
		Country norway = createDefaultTestCountry();
		Person testPerson = createDefaultTestPerson(personId, norway).build();

		Session session = getSession();
		session.save(norway);
		session.save(testPerson);

		session.flush();

		// TODO Change name of person
		testPerson = (Person) session.get(Person.class, 1L);
		testPerson.changeName("KalleKlovn");

		session.flush();

		session.clear();
		Person dbPerson = (Person) session.get(Person.class, personId);
		assertEquals(dbPerson.getName(), "KalleKlovn");
	}

	@Test
	public void shouldDeleteFromDatabase() {
		Long personId = 1L;
		Country norway = createDefaultTestCountry();
		Person person = createDefaultTestPerson(personId, norway).build();

		Session session = getSession();
		session.save(norway);
		session.save(person);
		session.flush();

		assertNumberOfObjectsInDatabase(1, "Person");

		// TODO Delete person with Hibernate
		session.delete(person);

		Criteria criteria = session.createCriteria(Person.class);
		criteria.add(Restrictions.like("name", "%Olsen"));
		criteria.list();

		assertNumberOfObjectsInDatabase(0, "Person");
	}

	@Test
	public void shouldUseFirstLevelCacheForFetchingAnObject() {
		Long personId = 1L;
		Country norway = createDefaultTestCountry();
		Person testPerson = createDefaultTestPerson(personId, norway).build();

		Session session = getSession();
		session.save(norway);
		session.save(testPerson);

		session.flush();

		assertNumberOfObjectsInDatabase(1, "Person");
		deleteAllRowsFromTable("Person");
		assertNumberOfObjectsInDatabase(0, "Person");

		Person fetchedPerson = (Person) session.get(Person.class, personId);
		assertNotNull(fetchedPerson);
	}
	
	@Test
	public void shouldUseOptimisticLocking() {
		new AssertThrows(StaleObjectStateException.class) {
			
			@Override
			public void test() throws Exception {
				Long personId = 1L;
				Country norway = createDefaultTestCountry();
				Person testPerson = createDefaultTestPerson(personId, norway).build();

				Session session = getSession();
				session.save(norway);
				session.save(testPerson);
				
				session.flush();

				testPerson = (Person) session.get(Person.class, personId);
				
				final Integer verson = new Integer(testPerson.getVersion()+1);
				JdbcTemplate template = new JdbcTemplate(dataSource);
				Integer count = template.execute(new StatementCallback<Integer>() {
					@Override
					public Integer doInStatement(Statement stmt) throws SQLException,
							DataAccessException {
						ResultSet rs = stmt.executeQuery("UPDATE PERSON SET VERSION = 2 WHERE ID = 1");
						return 1;
					}
				});
				
				testPerson.changeName("SkalGiException");
				session.update(testPerson);
				session.flush();
				
			}
		}.runTest();
		getSession().clear();
	}

	private void generatePersonsInTheDatabaseWithJobs(Session session,
			int start, int number, Country country) {
		Company nydra = createDefaultCompany(1L, country).build();

		generatePersonInTheDatabase(session, start, number, nydra, country);
	}

	private void generatePersonInTheDatabase(Session session, int start,
			int number, Company company, Country country) {
		session.save(country);
		if (company != null) {
			session.save(company);
		}

		for (Long i = new Long(start); i < start + number; i++) {
			Person person = createDefaultTestPerson(i, country).build();
			if (company != null) {
				person.addJob("Engineer", company);
			}
			session.save(person);
		}
	}

	private void generatePersonsInTheDatabaseWithoutJobs(Session session,
			int start, int number, Country country) {
		generatePersonInTheDatabase(session, start, number, null, country);
	}

	private Company.Builder createDefaultCompany(Long companyId, Country country) {
		return new Company.Builder().id(companyId).name("Nydra International")
				.countryOfHeadquarters(country);
	}

	private Person.Builder createDefaultTestPerson(Long personId,
			Country countryOfResidence) {
		return new Person.Builder().id(personId).socialSecurityNumber(
				new SocialSecurityNumber("12345678901")).name("Johnny Olsen")
				.countryOfResidence(countryOfResidence);
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

	private void assertNumberOfObjectsInDatabase(int i, String table) {
		JdbcTemplate template = new JdbcTemplate(dataSource);
		Integer count = countEntriesInDatabase(template, table);
		assertEquals((Integer) i, count);
	}

	private void incrementVersion(final Long id, final String table, final Integer newVersion) {
		
		
	}
	
	private Integer countEntriesInDatabase(JdbcTemplate template,
			final String tableName) {
		Integer count = template.execute(new StatementCallback<Integer>() {
			@Override
			public Integer doInStatement(Statement stmt) throws SQLException,
					DataAccessException {
				ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM "
						+ tableName);
				rs.next();
				return rs.getInt(1);
			}
		});
		return count;
	}

	public boolean tableExists(final String tablename) {
		JdbcTemplate template = new JdbcTemplate(dataSource);
		return template.execute(new ConnectionCallback<Boolean>() {
			@Override
			public Boolean doInConnection(Connection con) throws SQLException,
					DataAccessException {
				ResultSet tableRs = con.getMetaData().getTables(null, null,
						tablename, null);
				if (tableRs.next()) {
					return true;
				}

				return false;
			}
		});
	}

	private void deleteAllRowsFromTable(String... tablenames) {
		SimpleJdbcTestUtils.deleteFromTables(
				new SimpleJdbcTemplate(dataSource), tablenames);
	}

}
