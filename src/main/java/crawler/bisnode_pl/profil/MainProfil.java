package crawler.bisnode_pl.profil;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import crawler.api.Scrape;
import crawler.bisnode_pl.index.*;

import com.mysql.cj.api.jdbc.Statement;

public class MainProfil {

	public static void main(String[] args) {
		// wczytanie ustawien dla crawlera z pliku tekstowego
		Properties properties = loadProperties();
		// true - start index
		// false - restart index
		boolean startIndex;
		if (!(properties.getProperty("test_index").contains("1")
				|| properties.getProperty("test_profile").contains("1"))) {
			if (properties.getProperty("level1").contains("1")) {
				startIndex = true;
				System.out.println("level1 zosta³ uruchomiony");
			} else {
				startIndex = false;
				System.out.println("level1 zosta³ pominiêty");
			}
			if (properties.getProperty("level2").contains("1")) {

				System.out.println("level2 zosta³ uruchomiony");
			} else
				System.out.println("level2 zosta³ pominiêty");
			if (properties.getProperty("level3").contains("1")) {
				System.out.println("level3 zosta³ uruchomiony");
			} else
				System.out.println("level3 zosta³ pominiêty");
		} else {
			System.out.println("Wykryto sesjê testow¹. Level1, Level2, Level3 zosta³y pominiête");
			if (properties.getProperty("test_index").contains("1")) {
				System.out.println("zosta³ utuchomiony test indexu");
				Scrape index = new GetIndex("http://www.bisnode.pl/wyniki-wyszukiwania/?nazwa=orl", properties);
			} else
				System.out.println("test indexu zosta³ pominiêty");
			if (properties.getProperty("test_profile").contains("1")) {
				System.out.println("Zosta³ uruchomiony test profilu");
				Scrape profil = new GetProfile("http://www.bisnode.pl/firma/?id=777814&nazwa=WIS£A_P£OCK_S_A");
			} else
				System.out.println("test profilu zosta³ pominiêty");
		}

		// startIndex(properties, startIndex);
		// startProfile(properties);

		// pojedynczy index - do usuniecia. Operacja bêdzie wykonywana z poziomu
		// IndexRepository

	}

	public static Properties loadProperties() {
		Properties properties = new Properties();
		InputStream input = null;
		int numberOfThread = 99;
		int idHost = 99;

		try {
			input = new FileInputStream("c:\\crawlers\\properties\\bisnode_pl.properties");
			// load a properties file
			properties.load(input);
			// get the property value and print it out
			System.out.println("idHost =" + properties.getProperty("idHost"));
			idHost = Integer.parseInt(properties.getProperty("idHost"));
			System.out.println("numberOfThreads =" + properties.getProperty("numberOfThreads"));
			numberOfThread = Integer.parseInt(properties.getProperty("numberOfThreads"));

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return properties;
	}

	public static void startProfile(Properties properties) {
		int numberOfThreads = Integer.parseInt(properties.getProperty("numberOfThreads"));

		ProfilRepository[] profilRepository = new ProfilRepository[numberOfThreads];
		for (int i = 0; i < numberOfThreads; i++) {
			profilRepository[i] = new ProfilRepository(properties, i);
		}
		Thread[] threads = new Thread[numberOfThreads];
		for (int i = 0; i < numberOfThreads; i++) {
			threads[i] = new Thread(profilRepository[i]);
		}
		for (int i = 0; i < numberOfThreads; i++) {
			threads[i].start();
		}
	}

	/**
	 * Scraping ca³ego indexu
	 * 
	 * @param properties
	 */
	public static void startIndex(Properties properties, boolean startIndex) {
		// tylko podczas startu scrapowania indexu uruchom
		if (startIndex)
			createTables(properties);
		int numberOfThreads = Integer.parseInt(properties.getProperty("numberOfThreads"));
		IndexRepository[] indexRepository = new IndexRepository[numberOfThreads];
		for (int i = 0; i < numberOfThreads; i++) {
			indexRepository[i] = new IndexRepository(properties, i);
		}
		Thread[] threads = new Thread[numberOfThreads];
		for (int i = 0; i < numberOfThreads; i++) {
			threads[i] = new Thread(indexRepository[i]);
		}
		for (int i = 0; i < numberOfThreads; i++) {
			threads[i].start();
		}

	}

	/**
	 * Tworzenie tabeli letters ('aaa', 'zzz') z informacj¹ ile firm dla danego
	 * ci¹gu Tworzenie tabeli index_pages przechowuj¹c¹ wszystkie URL do
	 * scrapowania indexu
	 * 
	 * @param properties
	 */
	public static void createTables(Properties properties) {
		final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
		final String DB_URL = "jdbc:mysql://" + properties.getProperty("serverName") + "/"
				+ properties.getProperty("databaseName") + properties.getProperty("databaseProp");
		final String USER = properties.getProperty("user");
		final String PASS = properties.getProperty("password");
		Connection conn = null;
		Statement stmt = null;
		try {
			// STEP 2: Register JDBC driver
			Class.forName("com.mysql.cj.jdbc.Driver");

			// STEP 3: Open a connection
			System.out.println("Connecting to a selected database...");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			System.out.println("Connected database successfully...");

			// Usuniecie istniej¹cych tabel i stworzenie nowych
			System.out.println("Creating table in given database...");
			stmt = (Statement) conn.createStatement();
			String sql = "DROP TABLE IF EXISTS `" + properties.getProperty("databaseName") + "`.`letters`;";
			stmt.execute(sql);
			sql = "DROP TABLE IF EXISTS `" + properties.getProperty("databaseName") + "`.`index_pages`;";
			stmt.execute(sql);
			sql = "CREATE TABLE `" + properties.getProperty("databaseName")
					+ "`.`letters` (`letters` VARCHAR(3) NULL,`numberOfCompanies` INT NULL DEFAULT NULL);";
			stmt.executeUpdate(sql);
			System.out.println("Created table letters in given database...");
			sql = "CREATE TABLE `" + properties.getProperty("databaseName")
					+ "`.`index_pages` (  `letters` VARCHAR(3) NULL,  `url` VARCHAR(500) NULL,  `status` VARCHAR(5) NULL DEFAULT NULL);";
			stmt.executeUpdate(sql);
			System.out.println("Created table index_pages in given database...");
			StringBuilder letters = new StringBuilder(
					"INSERT INTO `" + properties.getProperty("databaseName") + "`.`letters`(letters)  VALUES  ");
			for (char first = 'a'; first <= 'z'; first++) {
				for (char second = 'a'; second <= 'z'; second++) {
					for (char third = 'a'; third <= 'z'; third++) {
						letters.append(
								"('" + String.valueOf(first) + String.valueOf(second) + String.valueOf(third) + "'),");
					}
				}
			}
			String sqlInsert = letters.substring(0, letters.length() - 1);
			stmt.execute(sqlInsert);
			// usuniêcie tabeli dla indexu
			sql = "DROP TABLE IF EXISTS " + properties.getProperty("databaseName") + ".`index`;";
			System.err.println(sql);
			stmt.executeUpdate(sql);
			// stworzenie nowej tabeli dla indexu
			sql = "CREATE TABLE `" + properties.getProperty("databaseName")
					+ "`.`index` (  `nazwa` MEDIUMTEXT NULL,  `url` VARCHAR(1000) NULL,  `adres` MEDIUMTEXT NULL,  `krs` VARCHAR(45) NULL,  `nip` VARCHAR(45) NULL,  `data` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP, `meta` VARCHAR(1000) NULL,  `status` VARCHAR(45) NULL DEFAULT NULL,  INDEX `index1` (`url` ASC),  INDEX `indexNip` (`nip` ASC),  INDEX `indexKrs` (`krs` ASC));";
			System.err.println(sql);
			stmt.executeUpdate(sql);

		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		} finally {
			// finally block used to close resources
			try {
				if (stmt != null)
					conn.close();
			} catch (SQLException se) {
			} // do nothing
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			} // end finally try
		} // end try

	}

}
