import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import java.sql.*;

/**
 * @author Class done by: KENJI LEONG
 * 
 * </p> Libraries used: JSoup, Selenium
 * </p>@see <a href="https://www.javatpoint.com/jsoup-api">https://www.javatpoint.com/jsoup-api</a>
 * </p>@see <a href="https://www.selenium.dev/selenium/docs/api/java/overview-summary.html">https://www.selenium.dev/selenium/docs/api/java/overview-summary.html</a>
 * 
 * </p>Parent class: Crawler
 * </p>Implements of abstract crawl class from Crawler 
 * </p>Calling of inheritance Analyse class for sentiment analysis 
 * </p>Over-riding of InsertToDatabase classes from Crawler
 * 
 */
public class RedditCrawler extends Crawler {
	// SQL link
	Connection connection = null;
	Statement statement = null;
	final String jdbcurl = "jdbc:sqlite:crawler.db";
	// Reddit Links
	final String urlGME = "https://www.reddit.com/r/GME/search?q=GME&restrict_sr=1&t=all&sort=top";
	final String urlAMC = "https://www.reddit.com/r/amcstock/search/?q=AMC&restrict_sr=1&sort=top&t=all";
	final String source = "Reddit";

	/**
	 * Implements of abstract Crawl class from Crawler 
	 * </p>Calling of inherited Analyse method class for sentiment analysis 
	 * </p>Calling of InsertToDatabase classes for the storing of data into the database
	 * 
	 */
	public void crawl(String s) {
		// GME STOCK
		if (s.contains("GME")) {
			try {
				String stockType = "GME";
				// Gecko driver required for Selenium to open windows
				System.setProperty("webdriver.gecko.driver", "C:/Users/kiaka/Onedrive/Desktop/geckodriver.exe");
				// Set Firefox properties "dom.webnotifications.enabled", false = remove all pop
				// up notification
				// "--headless"=runs in headless mode so FireFox can run in background without
				// any icon on taskbar
				FirefoxOptions options = new FirefoxOptions();
				options.addPreference("dom.webnotifications.enabled", false);
				options.addArguments("--headless");
				FirefoxDriver driver = new FirefoxDriver(options);
				// Go to URL page for GME
				driver.get(urlGME);
				// Wait for page to load
				Thread.sleep(5000);
				// Click on the first thread with their id
				driver.findElement(By.xpath("//*[@id=\"t3_lthi3p\"]/div[2]/div")).click();
				// Wait for page to load
				Thread.sleep(5000);
				// findElement will look for view comment button based on xpath
				// return true when found, else it will keep scrolling down until button is
				// found
				// break while loop when is False
				boolean a = driver.findElement(By.xpath("//button[contains(.,'View Entire Discussion')]"))
						.isDisplayed();
				while (a == false) {
					JavascriptExecutor jse = (JavascriptExecutor) driver;
					jse.executeScript("window.scrollBy(0,925)", "");
					Thread.sleep(3000);
					a = driver.findElement(By.xpath("//button[contains(.,'View Entire Discussion')]")).isDisplayed();
					if (a == true) {
						driver.findElement(By.xpath("//button[contains(.,'View Entire Discussion')]")).click();
						break;
					}
				}
				// Wait for page to load comments
				Thread.sleep(5000);
				// Parse URL link to Jsoup
				Document document = Jsoup.parse(driver.getPageSource());

				// Post-content is a class value from Reddit. Covers the title and post section
				// for the author
				for (Element row : document.getElementsByAttributeValue("data-test-id", "post-content")) {
					// get date
					Elements dates = row.getElementsByAttributeValue("data-click-id", "timestamp");
					// Reddit does not provide actual date in DD//MM/YYYY
					String date = dates.text();
					// get title
					String title = row.select("h1._eYtD2XCVieq6emjKBH3m").text();
					String newTitle = title;
					// Regex command - remove all symbol and only allow alphanumeric character
					newTitle = newTitle.replaceAll("\\W+", " ");
					// Calling of inherited Analyse class and store the return sentiment analysis into the variable sentiments
					// Analyse using NLP Library for title
					String titleSentiment = Analyse(newTitle);
					// Insert to Database
					InsertToDatabase(jdbcurl, stockType, source, date, title, titleSentiment);
				}
				// "div._2M2wOqmeoPVvcSsJ6Po9-V " represent whole section for comments
				// "p._1qeIAgB0cPwnLhDF9XSiJM" represent class used for all comment
				for (Element row : document.select("div._2M2wOqmeoPVvcSsJ6Po9-V ")) {
					for (Element comment : row.select("p._1qeIAgB0cPwnLhDF9XSiJM")) {
						String newComment = comment.text();
						// Similarly remove all symbol
						newComment = newComment.replaceAll("\\W+", " ");
						// If user comment only include symbol only replaceAll("\\W+", " ") will remove
						// symbols string will be empty
						// Thus, replace empty field with value empty
						boolean checkEmpty = newComment.isBlank();
						if (checkEmpty == true) {
							newComment = "Empty";
						}
						// Calling of inherited Analyse class and store the return sentiment analysis into the variable sentiments
						// Analyse using NLP Library for comments
						String commentSentiment = Analyse(newComment);
						InsertToDatabase(jdbcurl, stockType, source, newComment, commentSentiment);
					}
				}

				// Driver2
				FirefoxDriver driver2 = new FirefoxDriver(options);
				driver.get(urlGME);
				Thread.sleep(5000);
				driver.findElement(By.xpath("//*[@id=\"t3_lvgdwx\"]/div[2]/div")).click();
				Thread.sleep(5000);
				boolean b = driver.findElement(By.xpath("//button[contains(.,'View Entire Discussion')]"))
						.isDisplayed();
				while (b == false) {
					JavascriptExecutor jse = (JavascriptExecutor) driver;
					jse.executeScript("window.scrollBy(0,925)", "");
					Thread.sleep(3000);
					b = driver.findElement(By.xpath("//button[contains(.,'View Entire Discussion')]")).isDisplayed();
					if (b == true) {
						driver.findElement(By.xpath("//button[contains(.,'View Entire Discussion')]")).click();
						break;
					}
				}
				Thread.sleep(5000);
				Document document2 = Jsoup.parse(driver.getPageSource());

				for (Element row : document2.getElementsByAttributeValue("data-test-id", "post-content")) {
					Elements dates = row.getElementsByAttributeValue("data-click-id", "timestamp");
					String date = dates.text();
					String title = row.select("h1._eYtD2XCVieq6emjKBH3m").text();
					String newTitle = title;
					newTitle = newTitle.replaceAll("\\W+", " ");
					String titleSentiment = Analyse(newTitle);
					InsertToDatabase(jdbcurl, stockType, source, date, title, titleSentiment);
				}

				for (Element row : document2.select("div._2M2wOqmeoPVvcSsJ6Po9-V ")) {
					for (Element comment : row.select("p._1qeIAgB0cPwnLhDF9XSiJM")) {
						String newComment = comment.text();
						newComment = newComment.replaceAll("\\W+", " ");
						boolean checkEmpty = newComment.isBlank();
						if (checkEmpty == true) {
							newComment = "Empty";
						}
						String commentSentiment = Analyse(newComment);
						InsertToDatabase(jdbcurl, stockType, source, newComment, commentSentiment);
					}
				}

				// Driver3
				FirefoxDriver driver3 = new FirefoxDriver(options);
				driver.get(urlGME);
				Thread.sleep(5000);
				driver.findElement(By.xpath("//*[@id=\"t3_ltua0n\"]/div[2]/div")).click();
				Thread.sleep(5000);
				boolean c = driver.findElement(By.xpath("//button[contains(.,'View Entire Discussion')]"))
						.isDisplayed();
				while (c == false) {
					JavascriptExecutor jse = (JavascriptExecutor) driver;
					jse.executeScript("window.scrollBy(0,925)", "");
					Thread.sleep(3000);
					c = driver.findElement(By.xpath("//button[contains(.,'View Entire Discussion')]")).isDisplayed();
					if (c == true) {
						driver.findElement(By.xpath("//button[contains(.,'View Entire Discussion')]")).click();
						break;
					}
				}
				Thread.sleep(5000);
				Document document3 = Jsoup.parse(driver.getPageSource());

				for (Element row : document3.getElementsByAttributeValue("data-test-id", "post-content")) {
					Elements dates = row.getElementsByAttributeValue("data-click-id", "timestamp");
					String date = dates.text();
					String title = row.select("h1._eYtD2XCVieq6emjKBH3m").text();
					String newTitle = title;
					newTitle = newTitle.replaceAll("\\W+", " ");
					String titleSentiment = Analyse(newTitle);
					InsertToDatabase(jdbcurl, stockType, source, date, title, titleSentiment);
				}

				for (Element row : document3.select("div._2M2wOqmeoPVvcSsJ6Po9-V ")) {
					for (Element comment : row.select("p._1qeIAgB0cPwnLhDF9XSiJM")) {
						String newComment = comment.text();
						newComment = newComment.replaceAll("\\W+", " ");
						boolean checkEmpty = newComment.isBlank();
						if (checkEmpty == true) {
							newComment = "Empty";
						}
						String commentSentiment = Analyse(newComment);
						InsertToDatabase(jdbcurl, stockType, source, newComment, commentSentiment);
					}
				}

				// close browser at end of program
				driver.close();
				driver2.close();
				driver3.close();

			} catch (Exception e) {
				// handle exception
				e.printStackTrace();
			}
		}

		if (s.contains("AMC")) {
			// AMC STOCK
			try

			{
				String stockType = "AMC";
				System.setProperty("webdriver.gecko.driver", "C:/Users/kiaka/Onedrive/Desktop/geckodriver.exe");
				FirefoxOptions options = new FirefoxOptions();
				options.addPreference("dom.webnotifications.enabled", false);
				options.addArguments("--headless");
				FirefoxDriver driver = new FirefoxDriver(options);
				driver.get(urlAMC);
				Thread.sleep(5000);
				driver.findElement(By.xpath("//*[@id=\"t3_m2885k\"]")).click();
				Thread.sleep(5000);
				boolean a = driver.findElement(By.xpath("//button[contains(.,'View Entire Discussion')]"))
						.isDisplayed();
				while (a == false) {
					JavascriptExecutor jse = (JavascriptExecutor) driver;
					jse.executeScript("window.scrollBy(0,925)", "");
					Thread.sleep(3000);
					a = driver.findElement(By.xpath("//button[contains(.,'View Entire Discussion')]")).isDisplayed();
					if (a == true) {
						driver.findElement(By.xpath("//button[contains(.,'View Entire Discussion')]")).click();
						break;
					}
				}
				Thread.sleep(10 * 500);
				Document document = Jsoup.parse(driver.getPageSource());

				for (Element row : document.getElementsByAttributeValue("data-test-id", "post-content")) {
					Elements dates = row.getElementsByAttributeValue("data-click-id", "timestamp");
					String date = dates.text();
					String title = row.select("h1._eYtD2XCVieq6emjKBH3m").text();
					String newTitle = title;
					newTitle = newTitle.replaceAll("\\W+", " ");
					String titleSentiment = Analyse(newTitle);
					InsertToDatabase(jdbcurl, stockType, source, date, title, titleSentiment);
				}
				for (Element row : document.select("div._2M2wOqmeoPVvcSsJ6Po9-V ")) {
					for (Element comment : row.select("p._1qeIAgB0cPwnLhDF9XSiJM")) {
						String newComment = comment.text();
						newComment = newComment.replaceAll("\\W+", " ");
						boolean checkEmpty = newComment.isBlank();
						if (checkEmpty == true) {
							newComment = "Empty";
						}
						String commentSentiment = Analyse(newComment);
						InsertToDatabase(jdbcurl, stockType, source, newComment, commentSentiment);
					}
				}
				// Driver2
				FirefoxDriver driver2 = new FirefoxDriver(options);
				driver.get(urlAMC);
				Thread.sleep(5000);
				driver.findElement(By.xpath("//*[@id=\"t3_lt7txe\"]")).click();
				Thread.sleep(5000);
				boolean b = driver.findElement(By.xpath("//button[contains(.,'View Entire Discussion')]"))
						.isDisplayed();
				while (b == false) {
					JavascriptExecutor jse = (JavascriptExecutor) driver;
					jse.executeScript("window.scrollBy(0,925)", "");
					Thread.sleep(3000);
					b = driver.findElement(By.xpath("//button[contains(.,'View Entire Discussion')]")).isDisplayed();
					if (b == true) {
						driver.findElement(By.xpath("//button[contains(.,'View Entire Discussion')]")).click();
						break;
					}
				}
				Thread.sleep(5000);
				Document document2 = Jsoup.parse(driver.getPageSource());

				for (Element row : document2.getElementsByAttributeValue("data-test-id", "post-content")) {
					Elements dates = row.getElementsByAttributeValue("data-click-id", "timestamp");
					String date = dates.text();
					String title = row.select("h1._eYtD2XCVieq6emjKBH3m").text();
					String newTitle = title;
					newTitle = newTitle.replaceAll("\\W+", " ");
					String titleSentiment = Analyse(newTitle);
					InsertToDatabase(jdbcurl, stockType, source, date, title, titleSentiment);
				}

				for (Element row : document2.select("div._2M2wOqmeoPVvcSsJ6Po9-V ")) {
					for (Element comment : row.select("p._1qeIAgB0cPwnLhDF9XSiJM")) {
						String newComment = comment.text();
						newComment = newComment.replaceAll("\\W+", " ");
						boolean checkEmpty = newComment.isBlank();
						if (checkEmpty == true) {
							newComment = "Empty";
						}
						String commentSentiment = Analyse(newComment);
						InsertToDatabase(jdbcurl, stockType, source, newComment, commentSentiment);
					}
				}
				// Driver3
				FirefoxDriver driver3 = new FirefoxDriver(options);
				driver.get(urlAMC);
				Thread.sleep(5000);
				driver.findElement(By.xpath("//*[@id=\"t3_lbx6cj\"]")).click();
				Thread.sleep(5000);
				boolean c = driver.findElement(By.xpath("//button[contains(.,'View Entire Discussion')]"))
						.isDisplayed();
				while (c == false) {
					JavascriptExecutor jse = (JavascriptExecutor) driver;
					jse.executeScript("window.scrollBy(0,925)", "");
					Thread.sleep(3000);
					c = driver.findElement(By.xpath("//button[contains(.,'View Entire Discussion')]")).isDisplayed();
					if (c == true) {
						driver.findElement(By.xpath("//button[contains(.,'View Entire Discussion')]")).click();
						break;
					}
				}
				Thread.sleep(5000);
				Document document3 = Jsoup.parse(driver.getPageSource());

				for (Element row : document3.getElementsByAttributeValue("data-test-id", "post-content")) {
					Elements dates = row.getElementsByAttributeValue("data-click-id", "timestamp");
					String date = dates.text();
					String title = row.select("h1._eYtD2XCVieq6emjKBH3m").text();
					String newTitle = title;
					newTitle = newTitle.replaceAll("\\W+", " ");
					String titleSentiment = Analyse(newTitle);
					InsertToDatabase(jdbcurl, stockType, source, date, title, titleSentiment);
				}

				for (Element row : document3.select("div._2M2wOqmeoPVvcSsJ6Po9-V ")) {
					for (Element comment : row.select("p._1qeIAgB0cPwnLhDF9XSiJM")) {
						String newComment = comment.text();
						newComment = newComment.replaceAll("\\W+", " ");
						boolean checkEmpty = newComment.isBlank();
						if (checkEmpty == true) {
							newComment = "Empty";
						}
						String commentSentiment = Analyse(newComment);
						InsertToDatabase(jdbcurl, stockType, source, newComment, commentSentiment);
					}
				}
				driver.close();
				driver2.close();
				driver3.close();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}

	/**
	 * Over-Riding InsertToDatabase class in Crawler for the storing of data into the database
	 * @param jdbcurl the connection path to the database
	 * @param stockType the name of the stock crawled
	 * @param source the name of which social media the content is being crawled from
	 * @param date the date of the content being created 
	 * @param title the title of the Posts crawled 
	 * @param titleSentiment the sentiment results of the posts
	 */
	public void InsertToDatabase(String jdbcurl, String stockType, String source, String date, String title,
			String titleSentiment) {
		// Insert Title section into the database
		try {
			Class.forName("org.sqlite.JDBC");
			System.out.println("Connecting to database...");
			connection = DriverManager.getConnection(jdbcurl);
			System.out.println("Inserting records into the table...");
			statement = connection.createStatement();
			statement.executeUpdate("INSERT INTO stockdb (`stock`,`source`,`date`, `title`,`sentiment`) VALUES (" + "\""
					+ stockType + "\"," + "\"" + source + "\"," + "\"" + date + "\"," + "\"" + title + "\"," + "\""
					+ titleSentiment + "\")");
		} catch (ClassNotFoundException | SQLException e) {
			// Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		} finally {
			// finally block used to close resources
			try {
				if (statement != null) {
					statement.close();
				}
			} catch (SQLException se2) {
			}
			try {
				if (connection != null) {
					connection.close();
				}

			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
	}

	/**
	 * Over-Riding InsertToDatabase class in Crawler for the storing of data into the database
	 * </p>Insert Comment section into the database
	 * @param jdbcurl the connection path to the database
	 * @param stockType the name of the stock crawled
	 * @param source the name of which social media the content is being crawled from
	 * @param newComment the comments of the Posts crawled
	 * @param commentSentiment the sentiment results of the posts
	 */
	public void InsertToDatabase(String jdbcurl, String stockType, String source, String newComment,
			String commentSentiment) {
		try {
			Class.forName("org.sqlite.JDBC");
			System.out.println("Connecting to database...");
			connection = DriverManager.getConnection(jdbcurl);
			System.out.println("Inserting records into the table...");
			statement = connection.createStatement();
			statement.executeUpdate(
					"INSERT INTO stockdb (`stock`,`source`,`comment`,`sentiment`) VALUES (" + "\"" + stockType + "\","
							+ "\"" + source + "\"," + "\"" + newComment + "\"," + "\"" + commentSentiment + "\")");
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		} finally {
			// finally block used to close resources
			try {
				if (statement != null)
					statement.close();
			} catch (SQLException se2) {
			} // exception errors if resources cannot be closed
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException se) {
				se.printStackTrace();
			} // end finally try
		}
	}
}
