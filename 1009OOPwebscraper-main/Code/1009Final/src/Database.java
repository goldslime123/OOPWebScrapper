/**
 * @author Class done by: CHEW SONG QING IVAN
 * 
 */

import java.util.Date;

interface Database {
	
	/**
	 * Abstract Method
	 * </p>Method overloading for the storing of Reddit Posts into Database
	 * @param jdbcurl the connection path to the database
	 * @param jdbcurl the connection path to the database
	 * @param stockType the name of the stock crawled
	 * @param source the name of which social media the content is being crawled from
	 * @param date the date of the content being created 
	 * @param title the title of the Posts crawled 
	 * @param titleSentiment the sentiment results of the posts
	 */
	public void InsertToDatabase(String jdbcurl, String stockType, String source, String date, String title, String titleSentiment);
	
	/**
	 * Abstract Method
	 * </p>Method overloading for the storing of Reddit Comments into Database
	 * @param jdbcurl the connection path to the database
	 * @param stockType the name of the stock crawled
	 * @param source the name of which social media the content is being crawled from
	 * @param newComment the comments of the Posts crawled
	 * @param commentSentiment the sentiment results of the posts
	 */
	public void InsertToDatabase(String jdbcurl, String stockType, String source, String newComment, String commentSentiment);

	/**
	 * Abstract Method
	 * </p>Method overloading for the storing of Tweets into Database
	 * @param s is the stock name
	 * @param jdbcurl the connection path to the database
	 * @param tweetDateCreated the Date that the tweets were being created
	 * @param newTweetsText the tweets being crawled
	 * @param sentiments the sentiment results of the tweets
	 */
	public void InsertToDatabase(String s, String jdbcurl, Date tweetDateCreated, String newTweetsText, String sentiments);
	
}