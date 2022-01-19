import java.util.Properties;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

/**
 * Sentiment analysis using Stanford JavaNLP API
 * </p>
 * @see <a href="https://nlp.stanford.edu/nlp/javadoc/javanlp/">https://nlp.stanford.edu/nlp/javadoc/javanlp/</a>
 * </p>
 * </p>Initialisaion of Pipeline.
 * </p>Pipeline will run in the order of 
 * </p>1. tokenize - split each word in the sentence into token
 * </p>2. ssplit - split word based on white space 
 * </p>3. pos - tag each word eg. NN rep "noun" VBD rep "verb" 
 * </p>4. parse - parse each token with the tag into a Parse tree 
 * </p>5. sentiment - analyse sentence as a whole through machine learning
 */
public class Pipeline {
	private static StanfordCoreNLP stanfordCoreNLP;
	private static Properties properties;
	private static String propertiesName = "tokenize, ssplit, pos, parse, sentiment";

	private Pipeline() {

	}

	static {
		properties = new Properties();
		properties.setProperty("annotators", propertiesName);
	}

	public static StanfordCoreNLP getPipeLine() {
		if (stanfordCoreNLP == null) {
			stanfordCoreNLP = new StanfordCoreNLP(properties);

		}
		return stanfordCoreNLP;
	}

}
