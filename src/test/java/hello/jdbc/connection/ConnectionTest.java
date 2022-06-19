package hello.jdbc.connection;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Slf4j
public class ConnectionTest {


    @Test
    void driverManager() throws SQLException {
        Connection connection1 = DriverManager.getConnection(ConnectionCost.URL, ConnectionCost.USERNAME, ConnectionCost.PASSWORD);
        Connection connection2 = DriverManager.getConnection(ConnectionCost.URL, ConnectionCost.USERNAME, ConnectionCost.PASSWORD);

        log.info("connection1 = {}, class = {}", connection1, connection1.getClass());
        log.info("connection2 = {}, class = {}", connection2, connection2.getClass());
    }

    @Test
    void dataSourceDriverManager() throws SQLException {
        // 항상 새로운 커넥션을 획득함
        // 스프링이 제공하는 DriverManager -> DriverManager가 DataSource를 상속받지않으므로 스프링이 만듬

        DataSource driverManagerDataSource = new DriverManagerDataSource(ConnectionCost.URL, ConnectionCost.USERNAME, ConnectionCost.PASSWORD);
        useDataSource(driverManagerDataSource);
    }

    private void useDataSource(DataSource dataSource) throws SQLException {
        Connection con1 = dataSource.getConnection();
        Connection con2 = dataSource.getConnection();
        log.info("connection1 = {}, class = {}", con1, con1.getClass());
        log.info("connection2 = {}, class = {}", con2, con2.getClass());
    }

    /*
    * 만약 커넥션을 10개 이상 만들었을경우 ( 설정을 10개로함 )
    * 다음 커넥션을 반환할때까지 block을 시키고 대기를 시키도록함
    * readTimeOut과 같은 설정을 block을 얼마나 시킬지 설정해야함
    * */
    @Test
    void dataSourceConnectionPool() throws SQLException, InterruptedException {
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setJdbcUrl(ConnectionCost.URL);
        hikariDataSource.setUsername(ConnectionCost.USERNAME);
        hikariDataSource.setPassword(ConnectionCost.PASSWORD);
        hikariDataSource.setMaximumPoolSize(10);
        hikariDataSource.setPoolName("MyPool");

        useDataSource(hikariDataSource);
        Thread.sleep(1000);

    }
}
