package crawler.krs_pobierz_pl.profil;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ProfilRepository {

	private JdbcTemplate jdbcTemplate;
	@Autowired
	public void setDataSource(DataSource dataSource){
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	public List<String> fetchUrlToScrape(){
		return jdbcTemplate.queryForList("SELECT url FROM missing_profile", String.class);
	}
}

