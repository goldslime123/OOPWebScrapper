/**
 * @author Class done by: CHEW SONG QING IVAN
 * 
 */

import java.util.Date;
import java.util.List;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

abstract class Crawler implements Database {
	
	/**
	 * Abstract method for crawling contents from Reddit/Twitter
	 * @param s the name of the stocks to be crawled
	 */
	public abstract void crawl(String s);

	/**
	 * Implementation of interface methods
	 * </p>Methods to be Over-ride by child class (TwitterCrawler/RedditCrawler)
	 * @param jdbcurl the connection path to the database
	 * @param stockType the name of the stock crawled
	 * @param source the name of which social media the content is being crawled from
	 * @param date the date of the content being created 
	 * @param title the title of the Posts crawled 
	 * @param titleSentiment the sentiment results of the posts
	 */
	public void InsertToDatabase(String jdbcurl, String stockType, String source, String date, String title,
			String titleSentiment) {

		System.out.println("Error inserting Reddit Posts into Database! ");
	}
	
	/**
	 * Implementation of interface methods
	 * </p>Methods to be Over-ride by child class (TwitterCrawler/RedditCrawler)
	 * @param jdbcurl the connection path to the database
	 * @param stockType the name of the stock crawled
	 * @param source the name of which social media the content is being crawled from
	 * @param newComment the comments of the Posts crawled
	 * @param commentSentiment the sentiment results of the posts
	 */
	public void InsertToDatabase(String jdbcurl, String stockType, String source, String newComment,
			String commentSentiment) {

		System.out.println("Error inserting Reddit Comments into Database! ");
	}
	
	/**
	 * Implementation of interface methods
	 * </p>Methods to be Over-ride by child class (TwitterCrawler/RedditCrawler)
	 * @param s is the stock name
	 * @param jdbcurl the connection path to the database
	 * @param tweetDateCreated the Date that the tweets were being created
	 * @param newTweetsText the tweets being crawled
	 * @param sentiments the sentiment results of the tweets
	 */
	public void InsertToDatabase(String s, String jdbcurl, Date tweetDateCreated, String newTweetsText, String sentiments) {

		System.out.println("Error inserting Twitter tweets into Database! ");
	}

	/**
	 * Analyse content of the Posts/Comments/Tweets and return the sentiment result
	 * @param s is the content crawled to be analysed 
	 * @return the sentiment of Very Positive / Positive / Neutral / Negative / Very Negative 
	 */
	public String Analyse(String s) {
		String sentiment = "";
		StanfordCoreNLP stanfordCoreNLP = Pipeline.getPipeLine();
		// store paragraph or sentences into document
		CoreDocument coreDocument = new CoreDocument(s);
		// NLP will highlight keywords such as name,verb
		stanfordCoreNLP.annotate(coreDocument);
		// iterate through each word in the sentences and get the sentiment
		List<CoreSentence> sentences = coreDocument.sentences();
		for (CoreSentence sentence : sentences) {
			sentiment = sentence.sentiment();

		}
		return sentiment;
	}

}
