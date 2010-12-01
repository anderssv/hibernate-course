import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.StatementCallback;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.SimpleJdbcTestUtils;
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
		Person.Builder andersBuilder = createDefaultTestPerson(personId, norway)
				.name("Anders Andersson");
		Person anders = andersBuilder.build();

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

	private Company.Builder createDefaultCompany(Long companyId, Country country) {
		return new Company.Builder().id(companyId).name("Nydra International")
				.countryOfHeadquarters(country);
	}

	private Person.Builder createDefaultTestPerson(Long personId,
			Country countryOfResidence) {
		return new Person.Builder().id(personId).name("Johnny Olsen")
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
	
	private void deleteAllRowsFromTable(String... tablenames) {
		SimpleJdbcTestUtils.deleteFromTables(new SimpleJdbcTemplate(dataSource), tablenames);
	}

}
