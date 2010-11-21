import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

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
	public void shouldPersistDomain() {

	}

	/**
	 * Even though Spring creates transactions for us, it does not force a flush
	 * after running the test. That means that some of the database operations
	 * will NOT be tested. So we force a flush at the end to make sure all
	 * statements are tested against the database.
	 */
	@After
	public void flushToTest() {
		sessionFactory.getCurrentSession().flush();
	}

}
