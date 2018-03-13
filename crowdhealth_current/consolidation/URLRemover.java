package consolidation;
import java.util.*;
import org.apache.commons.validator.routines.*;;

public class URLRemover {
	public static Map<Long,Tweet> urlRemover(Map<Long,Tweet> englishTweets)
	{
        String[] schemes = {"http","https"};
        UrlValidator urlValidator = new UrlValidator(schemes);
        boolean flagWriteStringintoFile = true;
		Map<Long,Tweet>tweetsURLRemoved = new LinkedHashMap<Long,Tweet>();
		Iterator iterator = englishTweets.entrySet().iterator();
		long index = 0;
		while(iterator.hasNext())
		{
			Map.Entry pairs = (Map.Entry)iterator.next();
			Tweet tweet = (Tweet) pairs.getValue();
			String tweetText = tweet.text;
			String []tokenspresentInTweetText = tweetText.split(" ");
			for(int i = 0 ; i < tokenspresentInTweetText.length ; i++)
			{
				if(urlValidator.isValid(tokenspresentInTweetText[i]))
				{
					flagWriteStringintoFile = false;
					//System.out.println("String containes url :" + strLine);
					break;
				}
			}
			if(tweetText.contains("RT @"))
			{
				flagWriteStringintoFile = false;
			}
			if(flagWriteStringintoFile)
			{
				index ++;
				tweetsURLRemoved.put(index, tweet);
			}

			flagWriteStringintoFile = true;

		}
		return tweetsURLRemoved;
}

}
