/**
 * P2_Team15: 
 * </p>KENJI LEONG,
 * </p>CAI ZHAO AN,
 * </p>PHUA KIA KAI,
 * </p>CHNG JUN JIE JEREMY,
 * </p>CHEW SONG QING IVAN
 * 
 * @author Class done by: CHEW SONG QING IVAN
 * 
 */
public class MainProgram {

	/**
	 * This method is to crawl contents based on user inputs
	 * @param crawler is a crawler object 
	 * @param s is the stock name to be crawled
	 */
	public void crawl(Crawler crawler, String s) {
		crawler.crawl(s);
	}

	/**
	 * Create and initialise objects (RedditCrawler and TwitterCrawler) to crawl data
	 * @param args[0] the selection of Reddit/Twitter through command line from UI
	 * @param args[1] the name of the stocks input through command line from UI
	 */
	public static void main(String args[]) {
		MainProgram program = new MainProgram();
		Crawler reddit = new RedditCrawler();
		Crawler twitter = new TwitterCrawler();

		// reddit
		if (args[0].contains("1")) {
			program.crawl(reddit, args[1]);
		}
		// twitter
		if (args[0].contains("2")) {
			program.crawl(twitter, args[1]);
		}
		// both reddit and twitter
		else {
			program.crawl(reddit, args[1]);
			program.crawl(twitter, args[1]);
		}

	}

}