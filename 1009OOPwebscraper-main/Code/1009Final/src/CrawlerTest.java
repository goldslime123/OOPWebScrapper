import static org.junit.Assert.fail;
import java.sql.*;
import org.junit.jupiter.api.*;

class CrawlerTest {

	Crawler crawler;

	@BeforeEach
	void setup() throws Exception {
		crawler = new RedditCrawler();
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	// Test if NLP will process HTML link
	@Test
	void testSentimentForHtml() {
		// return Neutral
		String sentence = crawler.Analyse("https://www.google.com/");
		// Test case failed since NLP is able to return a sentiment for HTML link
		if (sentence.contains("Neutral") || sentence.contains("Positive") || sentence.contains("Negative")) {
			fail("NLP is inaccurate");
		}
	}

	// Test if NLP sentiments will be affected with filler words
	@Test
	void testSentimentForFillerWords() {
		// return Very Positive
		String sentence = crawler.Analyse("Seeing this in real time is absolutely amazing!");
		// return Neutral
		String sentenceWithFilter = crawler
				.Analyse("Seeing this in real time is absolutely amazing! if if if if if if if if if if if if");
		// Test case failed since sentiment changes
		if (!sentence.equals(sentenceWithFilter)) {
			fail("NLP cannot filter filler words");
		}

	}

	// Test for two different sentiments in one sentence
	@Test
	void testSentimentForTwoSentimentInOneSentence() {
		// return Positive
		String sentence = crawler.Analyse("GME is absolutely amazing and GME is absolutely bad");
		// return Positive
		String positiveSentence = crawler.Analyse("GME is absolutely amazing");
		// return Negative
		String negativeSentence = crawler.Analyse("GME is absolutely bad");
		// NLP should return Neutral so Test case failed
		if (sentence.contains("Positive") || sentence.contains("Negative")) {
			fail("NLP cannot differentiate two sentiment in one sentence");
		}
	}

	// Test NLP sentiment will be affected when User name is part of a comment or
	// Tweet for both Reddit or Twitter
	// If both sentiment matches, means NLP can differentiate them
	@Test
	void testSentimentForUsernameInComment() {
		// return Positive
		String sentenceWithUsername = crawler.Analyse("@jedimarkus77 But checks into GME makes such a great story");
		// return Positive
		String sentenceWithoutUsername = crawler.Analyse("But checks into GME makes such a great story");
		// Test case pass since both sentiments are Positive
		if (sentenceWithoutUsername.equals(sentenceWithUsername)) {
			System.out.println("NLP can differentiate username from sentences");
		}
	}

	// Test inserting SQL Query in VALUE SECTION for INSERT statement insert a
	// DELETE statement under the column for comment Iterate through the SQL
	// TableCheck if DELETE query will be executed in SQL
	@Test
	void testSQLQueryInValueForInsertStatement() {
		String jdbcurl = "jdbc:mysql://localhost:3306/?user=root";
		String username = "root";
		String password = "password";
		String stockType = "AMC";
		String source = "Reddit";
		String Comment = "good day";
		String commentSentiment = crawler.Analyse(Comment);
		String Query = "DELETE FROM sys.stocks WHERE id <> 0";

		Connection connection = null;
		Statement statement = null;
		Statement statement2 = null;
		ResultSet resultSet = null;

		try {
			// Establish the connection.
			connection = DriverManager.getConnection(jdbcurl, username, password);
			statement = connection.createStatement();
			statement2 = connection.createStatement();
			resultSet = statement.executeQuery("SELECT * FROM sys.stocks");
			statement2.executeUpdate(
					"INSERT INTO `sys`.`stocks` (`stock`,`source`,`comment`,`sentiment`) VALUES (" + "\"" + stockType
							+ "\"," + "\"" + source + "\"," + "\"" + Query + "\"," + "\"" + commentSentiment + "\")");

			ResultSetMetaData rsmd = resultSet.getMetaData();
			int columnsNumber = rsmd.getColumnCount();
			while (resultSet.next()) {
				for (int i = 1; i <= columnsNumber; i++) {
					String columnValue = resultSet.getString(i);
					// Test case pass when delete statement is not executed
					if (rsmd.getColumnName(i).contains("Comment")) {
						System.out.println("SQL query will not execute in insert statement.");
					}
				}
			}
		}

		catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
		}
	}

}
