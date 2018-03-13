package consolidation;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;

public class Filter {

	public static List<String> ngrams(int n, String str) {
		List<String> ngrams = new ArrayList<String>();
		String[] words = str.split("[\\s+\\-*/\\^:;\\[\\]\\\\()_#@$%&|<>\'\",.?{}!=`~]+");
		for (int i = 0; i < words.length - n + 1; i++)
			ngrams.add(concat(words, i, i+n));
		return ngrams;
	}

	public static String concat(String[] words, int start, int end) {
		StringBuilder sb = new StringBuilder();
		for (int i = start; i < end; i++)
			sb.append((i > start ? " " : "") + words[i]);
		return sb.toString();
	}
	private String dataSetsPath;
	public Filter(String dataSetsPath)
	{
		this.dataSetsPath = dataSetsPath;
	}
	public  Map<Long,Tweet>filterOnHealthKeyWords(Map<Long,Tweet>cleanTweets)
	{
		Map<String,Integer> scrapedTokens = new LinkedHashMap<String,Integer>();
		try{
			File dir = new File(dataSetsPath);
			String[] chld = dir.list();
			Arrays.sort(chld);
			int numberofHealthTokens = 0;
			for (int i = 0; i < chld.length; i++) {
				FileInputStream fileInputStream = new FileInputStream(dataSetsPath+chld[i]);
				DataInputStream in = new DataInputStream(fileInputStream);
				BufferedReader br = new BufferedReader(new InputStreamReader(in,"UTF-8"));
				String strLine;

				while ((strLine = br.readLine()) != null)   {
					scrapedTokens.put(strLine.toLowerCase(),numberofHealthTokens++ );
				}
				in.close();
			}

		}
		catch (Exception e){//Catch exception if any
			e.printStackTrace();
		}

		Map<Long,Tweet>filteredTweets = new LinkedHashMap<Long,Tweet>();
		Iterator iterator = cleanTweets.entrySet().iterator();
		long index = 0;
		while(iterator.hasNext())
		{
			Map.Entry pairs = (Map.Entry)iterator.next();
			Tweet tweet = (Tweet) pairs.getValue();
			String tweetText =  tweet.text;
			Pattern unicodeOutliers = Pattern.compile("[^\\x00-\\x7F]", Pattern.UNICODE_CASE | Pattern.CANON_EQ | Pattern.CASE_INSENSITIVE);
			Matcher unicodeOutlierMatcher = unicodeOutliers.matcher(tweetText);
			//  System.out.println("Before: " + tweetText);
			String tweetTextBefore = tweetText;
			tweetText = unicodeOutlierMatcher.replaceAll(" ");
			//  System.out.println("After: " + tweetText);
			String [] tokenizer = tweetText.split("[\\s+\\-*/\\^:;\\[\\]\\\\()_#@$%&|<>\'\",.?{}!=`~]+");
			int length = tokenizer.length;
			if(length ==0)
				continue;
			String [] tokens = null;
			String []temp = null;
			ArrayList<String>tempforadding = new ArrayList<String>();
			ArrayList<String>nGrams=null;

			for(int n = 1 ; n <= length ; n++)
			{

				nGrams = (ArrayList<String>) ngrams(n,tweetText);
				temp = nGrams.toArray(new String[nGrams.size()]);
				for(int m = 0 ; m < temp.length ; m++)
				{
					tempforadding.add(temp[m]);
				}
				tokens = tempforadding.toArray(new String[tempforadding.size()]);
			}
			for(int i = 0 ; i < tokens.length ; i ++)
			{
				if(scrapedTokens.containsKey(tokens[i].toLowerCase()))
				{
					//System.out.println("token : "+ tokens[i]);
					index++;
					tweet.text = tweetText;
					filteredTweets.put(index, tweet);
					//out.write(strLine.replace(tweetTextBefore,tweetText)+"\n");
					break;
				}
			}


		}


		return filteredTweets;
	}

}
