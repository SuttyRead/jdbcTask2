import dao.JdbcUserDao;
import entity.User;
import org.apache.commons.dbcp2.BasicDataSource;
import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.h2.tools.RunScript;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.charset.Charset;
import java.sql.Date;
import java.util.ResourceBundle;

import static org.junit.Assert.*;

public class JdbcUserDaoTest {

    private static final String SQL_SCHEMA = "resources/schema.sql";
    private static final String SQL_DATASET = "dataset.xml";
    private static final String SQL_DATABASE = "test";
    private static IDatabaseTester databaseTester = null;

    @BeforeClass
    public static void createSchema() throws Exception {
        ResourceBundle resourceBundle = ResourceBundle.getBundle(SQL_DATABASE);
        String url = resourceBundle.getString("jdbc.url");
        String user = resourceBundle.getString("jdbc.username");
        String password = resourceBundle.getString("jdbc.password");
        RunScript.execute(url, user, password, SQL_SCHEMA, Charset.forName("UTF-8"), false);
    }

    @Before
    public void importDataSet() throws Exception {
        IDataSet dataSet = readDataSet();
        cleanlyInsert(dataSet);
    }

    private IDataSet readDataSet() throws Exception {
        return new FlatXmlDataSetBuilder().build(getClass().getResourceAsStream(SQL_DATASET));
    }

    private void cleanlyInsert(IDataSet dataSet) throws Exception {
        ResourceBundle resourceBundle = ResourceBundle.getBundle(SQL_DATABASE);
        String url = resourceBundle.getString("jdbc.url");
        String user = resourceBundle.getString("jdbc.username");
        String password = resourceBundle.getString("jdbc.password");
        String driver = resourceBundle.getString("jdbc.driver-class-name");
        databaseTester = new JdbcDatabaseTester(driver, url, user, password);
        databaseTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
        databaseTester.setDataSet(dataSet);
        databaseTester.onSetup();
    }

    @Test
    public void testCreate() throws Exception {
        JdbcUserDao jdbcUserDao = JdbcUserDao.class.newInstance();
        jdbcUserDao.setBasicDataSource(dataSource());
        User user = new User(4L, "user4", "root", "user4@email.com", "Rich", "Brown",
                new Date(System.currentTimeMillis()), 3L);
        jdbcUserDao.create(user);
        assertEquals("should contain object that was insert", 4,
                databaseTester.getConnection().createDataSet().getTable("User").getRowCount());
        assertEquals("user added not correctly", databaseTester.getConnection().createDataSet().getTable("User")
                .getValue(3, "login"), user.getLogin());
        assertEquals("user added not correctly", databaseTester.getConnection().createDataSet().getTable("User")
                .getValue(3, "email"), user.getEmail());
    }

    @Test(expected = NullPointerException.class)
    public void testCreateNull() throws IllegalAccessException, InstantiationException {
        JdbcUserDao jdbcUserDao = JdbcUserDao.class.newInstance();
        jdbcUserDao.setBasicDataSource(dataSource());
        jdbcUserDao.create(null);
    }

    @Test
    public void testUpdate() throws Exception {
        JdbcUserDao jdbcUserDao = JdbcUserDao.class.newInstance();
        jdbcUserDao.setBasicDataSource(dataSource());
        User user = new User(3L, "updatedUser", "newPass", "user3@email.com", "Bob", "Brown",
                new Date(System.currentTimeMillis()), 3L);
        jdbcUserDao.update(user);
        assertEquals("user should be updated ", databaseTester.getConnection().createDataSet().getTable("User")
                .getValue(2, "login"), user.getLogin());
        assertEquals("user should be updated ", databaseTester.getConnection().createDataSet().getTable("User")
                .getValue(2, "email"), user.getEmail());
        assertEquals("user should be updated ", databaseTester.getConnection().createDataSet().getTable("User")
                .getValue(2, "firstname"), user.getFirstName());
    }

    @Test(expected = NullPointerException.class)
    public void testUpdateNull() throws IllegalAccessException, InstantiationException {
        JdbcUserDao jdbcUserDao = JdbcUserDao.class.newInstance();
        jdbcUserDao.setBasicDataSource(dataSource());
        jdbcUserDao.update(null);
    }

    @Test
    public void testRemove() throws Exception {
        JdbcUserDao jdbcUserDao = JdbcUserDao.class.newInstance();
        jdbcUserDao.setBasicDataSource(dataSource());
        User user = new User(2L, "user2", "root", "user1@email.com", "josh", "wayne",
                new Date(System.currentTimeMillis()), 2L);
        jdbcUserDao.remove(user);
        assertEquals("should not contains deleted object", 2,
                databaseTester.getConnection().createDataSet().getTable("User").getRowCount());
    }

    @Test(expected = NullPointerException.class)
    public void testRemoveNull() throws IllegalAccessException, InstantiationException {
        JdbcUserDao jdbcUserDao = JdbcUserDao.class.newInstance();
        jdbcUserDao.setBasicDataSource(dataSource());
        jdbcUserDao.remove(null);
    }

    @Test
    public void testFindByLogin() throws Exception {
        JdbcUserDao jdbcUserDao = new JdbcUserDao(dataSource(), "test");
        String userName = String.valueOf(databaseTester.getConnection().createDataSet().getTable("User")
                .getValue(0, "login"));
        User user = jdbcUserDao.findByLogin(userName);
        assertNotNull("Should find user by login", user);
    }

    @Test
    public void testFindByEmail() throws Exception {
        JdbcUserDao jdbcUserDao = new JdbcUserDao(dataSource(), "test");
        String userMail = String.valueOf(databaseTester.getConnection().createDataSet().getTable("User")
                .getValue(0, "email"));
        User user = jdbcUserDao.findByEmail(userMail);
        assertNotNull("Should find user by email", user);
    }

    private BasicDataSource dataSource() {
        ResourceBundle resourceBundle = ResourceBundle.getBundle(SQL_DATABASE);
        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setUrl(resourceBundle.getString("jdbc.url"));
        basicDataSource.setUsername(resourceBundle.getString("jdbc.username"));
        basicDataSource.setPassword(resourceBundle.getString("jdbc.password"));
        return basicDataSource;
    }

}
