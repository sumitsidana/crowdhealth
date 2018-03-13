package consolidation;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class UserNameRemover {


	static String parse(String tweetText) {

		String patternStr = "(?:\\s|\\A)[##]+([A-Za-z0-9-_]+)";
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(tweetText);
		String result = "";

		// Search for Hashtags
		while (matcher.find()) {
			result = matcher.group();
			result = result.replace(" ", "");
			String searchHTML = "";
			tweetText = tweetText.replace(result,searchHTML);
		}

		// Search for Users
		patternStr = "(?:\\s|\\A)[@]+([A-Za-z0-9-_]+)";
		pattern = Pattern.compile(patternStr);
		matcher = pattern.matcher(tweetText);
		while (matcher.find()) {
			result = matcher.group();
			result = result.replace(" ", "");
			String userHTML = "";
			tweetText = tweetText.replace(result,userHTML);
		}
		return tweetText;
	}

	public static Map<Long,Tweet> userNameRemove(Map<Long,Tweet> tweetsWOURL)
	{
		Map<Long,Tweet>tweetsWOUserName = new LinkedHashMap<Long,Tweet>();
		Iterator iterator = tweetsWOURL.entrySet().iterator();
		long index = 0;
		while(iterator.hasNext())
		{
			index++;
			Map.Entry pairs = (Map.Entry)iterator.next();
			Tweet tweet = (Tweet) pairs.getValue();
			String tweetTextBefore = tweet.text;
			String tweetTextAfter = parse(tweetTextBefore);
			tweet.text = tweetTextAfter;
			tweetsWOUserName.put(index, tweet);
		}
		return tweetsWOUserName;
	}
}
