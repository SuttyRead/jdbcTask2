import dao.JdbcUserDao;
import entity.Role;
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

import java.io.File;
import java.nio.charset.Charset;
import java.sql.Date;
import java.util.ResourceBundle;

import static org.junit.Assert.assertEquals;

public class JdbcUserDaoTest {

    private static final String SQL_SCHEMA = "resources/schema.sql";
    private static final String SQL_DATASET = "resources/dataset.xml";
    private static final String SQL_DATABASE = "test";

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
        return new FlatXmlDataSetBuilder().build(new File(SQL_DATASET));
    }

    private void cleanlyInsert(IDataSet dataSet) throws Exception {
        ResourceBundle resourceBundle = ResourceBundle.getBundle(SQL_DATABASE);
        String url = resourceBundle.getString("jdbc.url");
        String user = resourceBundle.getString("jdbc.username");
        String password = resourceBundle.getString("jdbc.password");
        String driver = resourceBundle.getString("jdbc.driver-class-name");
        IDatabaseTester databaseTester = new JdbcDatabaseTester(driver, url, user, password);
        databaseTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
        databaseTester.setDataSet(dataSet);
        databaseTester.onSetup();
    }

    @Test
    public void testCreate() throws IllegalAccessException, InstantiationException {
        JdbcUserDao jdbcUserDao = JdbcUserDao.class.newInstance();
        jdbcUserDao.setBasicDatSource(dataSource());
        User user = new User(4L, "user4", "root", "user4@email.com", "Rich", "Brown",
                new Date(System.currentTimeMillis()), new Role(4L, "Role"));
        jdbcUserDao.create(user);
        assertEquals("should contain object that was insert", 4, jdbcUserDao.findAll().size());
    }

    @Test(expected = NullPointerException.class)
    public void testCreateNull() throws IllegalAccessException, InstantiationException {
        JdbcUserDao jdbcUserDao = JdbcUserDao.class.newInstance();
        jdbcUserDao.setBasicDatSource(dataSource());
        jdbcUserDao.create(null);
    }

    @Test
    public void testUpdate() throws IllegalAccessException, InstantiationException {
        JdbcUserDao jdbcUserDao = JdbcUserDao.class.newInstance();
        jdbcUserDao.setBasicDatSource(dataSource());
        User user = new User(3L, "updatedUser", "newPass", "user3@email.com", "Bob", "Brown",
                new Date(System.currentTimeMillis()), new Role(4L, "Role"));
        jdbcUserDao.update(user);
        assertEquals("object should be updated ", jdbcUserDao.findAll().get(2).getLogin(), user.getLogin());
    }

    @Test(expected = NullPointerException.class)
    public void testUpdateNull() throws IllegalAccessException, InstantiationException {
        JdbcUserDao jdbcUserDao = JdbcUserDao.class.newInstance();
        jdbcUserDao.setBasicDatSource(dataSource());
        jdbcUserDao.update(null);
    }

    @Test
    public void testRemove() throws IllegalAccessException, InstantiationException {
        JdbcUserDao jdbcUserDao = JdbcUserDao.class.newInstance();
        jdbcUserDao.setBasicDatSource(dataSource());
        User user = jdbcUserDao.findAll().get(0);
        jdbcUserDao.remove(user);
        assertEquals("should not contains deleted object", 2, jdbcUserDao.findAll().size());
    }

    @Test(expected = NullPointerException.class)
    public void testRemoveNull() throws IllegalAccessException, InstantiationException {
        JdbcUserDao jdbcUserDao = JdbcUserDao.class.newInstance();
        jdbcUserDao.setBasicDatSource(dataSource());
        jdbcUserDao.remove(null);
    }

    @Test
    public void testFindByLogin() throws IllegalAccessException, InstantiationException {
        JdbcUserDao jdbcUserDao = JdbcUserDao.class.newInstance();
        jdbcUserDao.setBasicDatSource(dataSource());
        User user = jdbcUserDao.findAll().get(1);
        User secondUser = jdbcUserDao.findByLogin(user.getLogin());
        assertEquals("object should be equals", user.getId(), secondUser.getId());
    }

    @Test(expected = NullPointerException.class)
    public void testFindByLoginNull() throws IllegalAccessException, InstantiationException {
        JdbcUserDao jdbcUserDao = JdbcUserDao.class.newInstance();
        jdbcUserDao.setBasicDatSource(dataSource());
        jdbcUserDao.findByLogin(null);
    }

    @Test
    public void testFindByEmail() throws IllegalAccessException, InstantiationException {
        JdbcUserDao jdbcUserDao = JdbcUserDao.class.newInstance();
        jdbcUserDao.setBasicDatSource(dataSource());
        User user = jdbcUserDao.findAll().get(2);
        User secondUser = jdbcUserDao.findByEmail(user.getEmail());
        assertEquals("object should be equals", user.getId(), secondUser.getId());
    }

    @Test(expected = NullPointerException.class)
    public void testFindByEmailNull() throws IllegalAccessException, InstantiationException {
        JdbcUserDao jdbcUserDao = JdbcUserDao.class.newInstance();
        jdbcUserDao.setBasicDatSource(dataSource());
        jdbcUserDao.findByEmail(null);
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
