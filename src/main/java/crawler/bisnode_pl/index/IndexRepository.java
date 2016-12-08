package crawler.bisnode_pl.index;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.zip.CheckedInputStream;

import com.mysql.cj.api.jdbc.Statement;

import crawler.api.MainCrawler;

public class IndexRepository extends MainCrawler implements Runnable {

	private int threadId;
	private Properties properties;
	private String DB_DRIVER;
	private String DB_URL;
	private String DB_USER;
	private String DB_PASSWORD;
	private Connection conn = null;
	private Statement stmt = null;
	private ResultSet rs = null;

	public IndexRepository(Properties properties, int threadId) {
		this.threadId = threadId;
		this.properties = properties;
		this.DB_DRIVER = "com.mysql.cj.jdbc.Driver";
		this.DB_URL = "jdbc:mysql://" + properties.getProperty("serverName") + "/"
				+ properties.getProperty("databaseName") + properties.getProperty("databaseProp");
		this.DB_USER = properties.getProperty("user");
		this.DB_PASSWORD = properties.getProperty("password");
		try {
			Class.forName(DB_DRIVER);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		try {
			this.conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
			this.stmt = (Statement) conn.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		// sprawdzenie czy nie trzeba dokoñczyæ pierwszej strony w indexie
		try {
			while (checkIndex())
				;
			System.out.println("Poziom 1 z 3 strony bisnode.pl jest ju¿ skoñczony");
		} catch (SQLException e) {
			System.err.println("w¹tek nr " + this.threadId + " B³¹d pobierania danych z tabeli letters");
			e.printStackTrace();
		}
	}

	/**
	 * Sprawdzanie czy crawler wykona³ wszystkie kombinacje liter. Jeœli nie
	 * wykona³ w pierwszej kolejnoœæi dokoñczy je jednoczeœnie zapisuj¹c za
	 * pomoc¹ obiektu GetIndex pierwsze 20 wyników dla zadanego ci¹gu znaków.
	 * Jesli ¿adna kombinacja liter nie ma NULL w liczbie firm crawler
	 * przystêpuje do dokoñczenia indexu.
	 * 
	 * @return
	 * @throws SQLException
	 */
	private boolean checkIndex() throws SQLException {
		ResultSet unfinishedLetters = null;
		try {
			this.rs = getRecords(
					"SELECT letters, numberOfCompanies FROM bisnode_pl.letters where numberOfCompanies is null order by rand() limit 100");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			System.out.println("M: nie mo¿na pobraæ danych z tabeli letters. IndexRepository.run().getRecords()");
			e.printStackTrace();
		}
		// wykryto niedokoñczony index
		// rs.beforeFirst();
		if (rs.next()) {
			rs.beforeFirst();
			System.out.println("Wykryto niedokoñczony index");
			// rozpoczêcie procesu dokañczania
			while (rs.next()) {
				System.out.println("watek nr " + this.threadId + ", " + rs.getString("letters"));
				String urlToScrape = "http://www.bisnode.pl/wyniki-wyszukiwania/?nazwa=" + rs.getString("letters");
				// Tutaj nastêpuje wczytanie podstony indexu
				// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
				GetIndex getIndex = new GetIndex(urlToScrape, this.properties);
				
				//Uaktualnienie informacji o liczbie firm dla zadanego ci¹gu znaków
				sqlExecute("UPDATE letters set numberOfCompanies=" + getIndex.getNumberOfCompanies()
						+ "WHERE letters like '" + rs.getString("letters") + "'");
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Uniwersalna metoda zwracaj¹ca zapytanie SELECT w postaci obiektu
	 * ResultSet. Celowo na koñcu nie ma pozamykanych stmt oraz conn. Jest to
	 * nieestetyczne ale praktyczne ;]
	 * 
	 * @param sqlSelect
	 * @return
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	private void sqlExecute(String sql) {
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			System.err.println("Nie uda³o siê zaktualizowaæ informacji o liczbie firm");
		}
	}

	private ResultSet getRecords(String sqlSelect) throws SQLException, ClassNotFoundException {

		try {

			// execute select SQL stetement
			rs = stmt.executeQuery(sqlSelect);

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} finally {
			// if (stmt != null) {
			// stmt.close();
			// }
			// if (conn != null) {
			// conn.close();
			// }
		}
		return rs;
	}
}
