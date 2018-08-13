import dao.JdbcRoleDao;
import entity.Role;
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
import java.sql.SQLException;
import java.util.ResourceBundle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class JdbcRoleDaoTest {

    private static final String SQL_SCHEMA = "resources/schema.sql";
    private static final String SQL_DATASET = "resources/dataset.xml";
    private static final String SQL_DATABASE = "test";
    private static IDatabaseTester databaseTester = null;

    @BeforeClass
    public static void createSchema() {
        ResourceBundle resourceBundle = ResourceBundle.getBundle(SQL_DATABASE);
        String url = resourceBundle.getString("jdbc.url");
        String user = resourceBundle.getString("jdbc.username");
        String password = resourceBundle.getString("jdbc.password");
        try {
            RunScript.execute(url, user, password, SQL_SCHEMA, Charset.forName("UTF-8"), false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
        ResourceBundle resourceBundle;
        resourceBundle = ResourceBundle.getBundle(SQL_DATABASE);
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
        JdbcRoleDao jdbcRoleDao = JdbcRoleDao.class.newInstance();
        jdbcRoleDao.setBasicDataSource(dataSource());
        Role newRole = new Role(4L, "testRole");
        jdbcRoleDao.create(newRole);
        assertEquals("Should contain object that was insert", 4, databaseTester.getConnection().createDataSet()
                .getTable("Role").getRowCount());
    }

    @Test(expected = NullPointerException.class)
    public void testCreateNull() throws IllegalAccessException, InstantiationException {
        JdbcRoleDao jdbcRoleDao = JdbcRoleDao.class.newInstance();
        jdbcRoleDao.setBasicDataSource(dataSource());
        jdbcRoleDao.create(null);
    }

    @Test
    public void testUpdate() throws Exception {
        JdbcRoleDao jdbcRoleDao = JdbcRoleDao.class.newInstance();
        jdbcRoleDao.setBasicDataSource(dataSource());
        Role role = new Role(3L, "updatedRole");
        jdbcRoleDao.update(role);
        assertEquals("object should be updated ", databaseTester.getConnection().createDataSet().getTable("Role")
                .getValue(2, "name"), role.getName());
    }

    @Test(expected = NullPointerException.class)
    public void testUpdateNull() throws IllegalAccessException, InstantiationException {
        JdbcRoleDao jdbcRoleDao = JdbcRoleDao.class.newInstance();
        jdbcRoleDao.setBasicDataSource(dataSource());
        jdbcRoleDao.update(null);
    }

    @Test
    public void testRemove() throws Exception {
        JdbcRoleDao jdbcRoleDao = JdbcRoleDao.class.newInstance();
        jdbcRoleDao.setBasicDataSource(dataSource());
        Role role = new Role(1L, "removeRole");
        jdbcRoleDao.remove(role);
        assertEquals("size after remove should be 2", 2, databaseTester.getConnection().createDataSet()
                .getTable("Role").getRowCount());
    }

    @Test(expected = NullPointerException.class)
    public void testRemoveNull() throws IllegalAccessException, InstantiationException {
        JdbcRoleDao jdbcRoleDao = JdbcRoleDao.class.newInstance();
        jdbcRoleDao.setBasicDataSource(dataSource());
        jdbcRoleDao.remove(null);
    }

    @Test
    public void testFindByName() throws Exception {
        JdbcRoleDao jdbcRoleDao = JdbcRoleDao.class.newInstance();
        jdbcRoleDao.setBasicDataSource(dataSource());
        String roleName = String.valueOf(databaseTester.getConnection().createDataSet().
                getTable("Role").getValue(2, "name"));
        Role role = jdbcRoleDao.findByName(roleName);
        assertNotNull("Should find role by name", role);
    }

    private BasicDataSource dataSource() {
        ResourceBundle resourceBundle = ResourceBundle.getBundle(SQL_DATABASE);
        BasicDataSource basicDataSource = null;
        if (resourceBundle != null) {
            basicDataSource = new BasicDataSource();
            basicDataSource.setUrl(resourceBundle.getString("jdbc.url"));
            basicDataSource.setUsername(resourceBundle.getString("jdbc.username"));
            basicDataSource.setPassword(resourceBundle.getString("jdbc.password"));
        }
        return basicDataSource;
    }

}
