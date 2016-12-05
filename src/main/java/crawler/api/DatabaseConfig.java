package crawler.api;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import com.mysql.cj.jdbc.MysqlDataSource;

@Configuration
@ComponentScan(basePackages = "crawler.krs_pobierz_pl.profil")
public class DatabaseConfig {

    @Bean
    public DataSource dataSource() {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setDatabaseName("krs_pobierz_pl_profil?useTimezone=true&serverTimezone=GMT");
//        dataSource.setServerName("2001:db8:123::2");
        dataSource.setServerName("176.119.61.229");
        dataSource.setUser("java");
        dataSource.setPassword("haslo");
        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource());
    }
}