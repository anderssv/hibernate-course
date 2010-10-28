import static org.junit.Assert.assertEquals;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import javax.sql.DataSource;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.StatementCallback;

public class HibernateUtil {

	public static void flushAndClearCaches(Session session) {
		session.flush();
		evictSecondLevelCache(session);
		session.clear();
	}

	@SuppressWarnings("unchecked")
	public static void evictSecondLevelCache(Session session) {
		SessionFactory sessionFactory = session.getSessionFactory();
		Map<String, CollectionMetadata> roleMap = sessionFactory.getAllCollectionMetadata();
		for (String roleName : roleMap.keySet()) {
			sessionFactory.evictCollection(roleName);
		}

		Map<String, ClassMetadata> entityMap = sessionFactory.getAllClassMetadata();
		for (String entityName : entityMap.keySet()) {
			sessionFactory.evictEntity(entityName);
		}

		sessionFactory.evictQueries();
	}

	public static void assertNumberOfObjectsInDatabase(int i, String table, DataSource dataSource) {
		JdbcTemplate template = new JdbcTemplate(dataSource);
		Integer count = countEntriesInDatabase(template, table);
		assertEquals((Integer) i, count);
	}

	@SuppressWarnings("unchecked")
	public static Integer countEntriesInDatabase(JdbcTemplate template, final String tableName) {
		Integer count = (Integer) template.execute(new StatementCallback() {
			@Override
			public Object doInStatement(Statement stmt) throws SQLException, DataAccessException {
				ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + tableName);
				rs.next();
				return rs.getInt(1);
			}
		});
		return count;
	}

}
