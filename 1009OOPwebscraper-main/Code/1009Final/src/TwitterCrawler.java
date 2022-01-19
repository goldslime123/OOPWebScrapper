import twitter4j.*;
import java.util.Date;
import twitter4j.conf.*;
import java.sql.*;
import java.util.ArrayList;

/**
 * @author Class done by: CHNG JUN JIE JEREMY, CHEW SONG QING IVAN
 * 
 * </p>Libraries used: Twitter4J
 * @see http://twitter4j.org/oldjavadocs/4.0.7/index.html
 * 
 * </p>Parent class: Crawler
 * </p>Implements of abstract crawl class from Crawler 
 * </p>Calling of inheritance Analyse class for sentiment analysis 
 * </p>Over-riding of InsertToDatabase classes from Crawler
 * 
 */
public class TwitterCrawler extends Crawler {
	// setting up JDBC connection
	Connection conn = null;
	Statement stmt = null;

	/**
	 * </p>Implements of abstract Crawl class from Crawler 
	 * </p>Calling of inherited Analyse method class for sentiment analysis 
	 * </p>Calling of InsertToDatabase classes for the storing of data into the database
	 * 
	 */
	public void crawl(String s) {
		// totalTweets variable determines the amount of tweets to be crawled from the instance
		int totalTweets = 100;
		long lastID = Long.MAX_VALUE;

		String jdbcurl = "jdbc:sqlite:crawler.db";

		// Configurationbuilder creates an instance to access Twitter API
		// Along with authentication to access Twitter API
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true).setOAuthConsumerKey("SwEnOs3wjXJZ3C7hIVuXG2xuR")
				.setOAuthConsumerSecret("GE7S0xHjBu0V9hBw63IhmDia2g71mZpUXjg851ipMYgTzdyuWN")
				.setOAuthAccessToken("1364017893147439106-ieVSnBE3qKtGwnQxk8npxf9SC2Eloj")
				.setOAuthAccessTokenSecret("T8D7vCZtPrLiYGR5DaVh7jfjEASG2V4rZDefMdyIpFUfI");

		TwitterFactory tf = new TwitterFactory(cb.build());
		Twitter twitter = tf.getInstance();

		// Query searches the tweets that includes keywords specified, in this instance(string s) to be passed as an argument
		// that is specified in the command line in the UI layer
		// -filters filters unwanted tweets that include the specified attributes, this is to prevent NLP from processing 
		// sentiments of such tweets that dilutes the sentiments of tweets crawled
		Query query = new Query( s + "-filter:retweets -filter:links -filter:images");
		// additional query to only search English Tweets
		query.setLocale("en");
		query.setLang("en");
		// ArrayList with collection of all tweets and tweet attributes
		ArrayList<Status> tweets = new ArrayList<Status>();
		int counter = 0;
		while (counter <= totalTweets) {
			counter++;
			if (totalTweets - tweets.size() > 100)
				query.setCount(100);
			else
				query.setCount(totalTweets - tweets.size());

			try {
				QueryResult result = twitter.search(query);
				tweets.addAll(result.getTweets());

				for (Status tweet : tweets) {
					counter++;

					if (tweet.getId() < lastID)
						lastID = tweet.getId();
					
					// instantiates tweets attributes to variables to be recorded into the database
					String currTweet = tweet.getText();
					// Calling of inherited Analyse class and store the return sentiment analysis into the variable sentiments
					String sentiments = Analyse(currTweet);
					String tweetsText = tweet.getText().replace("'", "");
					Date tweetDateCreated = tweet.getCreatedAt();
					String newTweetsText = tweetsText.replaceAll("\\W+", " ");
					
					// Storing of variables into database by calling of function
					InsertToDatabase(s,jdbcurl, tweetDateCreated, newTweetsText, sentiments);

				}
			// catches Twitter exceptions, when query fails to search for tweets
			} catch (TwitterException se) {
				se.printStackTrace();
			}
		}

		query.setMaxId(lastID - 1);
	}

	/**
	 * Over-Riding InsertToDatabase class in Crawler for the storing of data into the database
	 * @param s is the stock name
	 * @param jdbcurl the connection path to the database
	 * @param tweetDateCreated the Date that the tweets were being created
	 * @param newTweetsText the tweets being crawled
	 * @param sentiments the sentiment results of the tweets
	 */
	public void InsertToDatabase(String s, String jdbcurl, Date tweetDateCreated, String newTweetsText, String sentiments) {
		// Opening a connection to MYSQL database
		try {
			String source = "Twitter";
			Class.forName("org.sqlite.JDBC");
			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(jdbcurl);
			System.out.println("Inserting records into the table...");
			stmt = conn.createStatement();

			String sql = " INSERT INTO stockdb" + "(Source,Stock,Date,Comment,Sentiment)" + " VALUES" + "('"
					+ source + "','" + s +"','" + tweetDateCreated + "','" + newTweetsText + "', '"
					+ sentiments + "')";

			// updates query into SQLite database
			stmt.executeUpdate(sql);
		} catch (ClassNotFoundException | SQLException e) {
			// Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		} finally {
			// finally block used to close resources
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException se2) {
			} // exception errors if resources cannot be closed
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			} 
		}

	}
}
