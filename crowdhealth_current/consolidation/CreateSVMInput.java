package consolidation;
import java.util.*;
import java.io.*;
public class CreateSVMInput {


	public  HashMap<String,Integer> wordMap;
	public List <Integer> tweetAllWords;
	public Set <Integer> tweetUniqueWords;

	public static List<String> ngrams(int n, String str) {
		List<String> ngrams = new ArrayList<String>();
		String[] words = str.split("[\\s+\\-*/\\^:;\\[\\]\\\\()_#@$%&|<>\'\",.?{}!=`~]+");
		for (int i = 0; i < words.length - n + 1; i++)
			ngrams.add(concat(words, i, i+n));
		return ngrams;
	}

	public static String concat(String[] words, int start, int end) {
		StringBuilder sb = new StringBuilder();
		for (int i = start; i < end;    i++)
			sb.append((i > start ? " " : "") + words[i]);
		return sb.toString();
	}
	
    public CreateSVMInput writeFile(CreateSVMInput createSVMInput, 
    		String strLine,Set<String>stopWords, PrintWriter output,
    		PrintWriter keyValueWriter, String label)
    {
		
		createSVMInput.tweetAllWords = new ArrayList<Integer>();
		String [] tokens = null;
		String []temp = null;
		ArrayList<String>tempforadding = new ArrayList<String>();
		ArrayList<String>nGrams=null;
		for(int n = 1 ; n <= 3 ; n++)
		{
			nGrams = (ArrayList<String>) ngrams(n,strLine);
			temp = nGrams.toArray(new String[nGrams.size()]);
			for(int m = 0 ; m < temp.length ; m++)
			{
				tempforadding.add(temp[m]);
			}

			tokens = tempforadding.toArray(new String[tempforadding.size()]);
		}

		int N = tokens.length;


		for(int n = 0 ; n < N ; n++)
		{
			int key = createSVMInput.wordMap.size();
			String word = tokens[n].toLowerCase();
			if(stopWords.contains(tokens[n].toLowerCase()))
				continue;
			//System.out.println("word: "+word);
			if(!(createSVMInput.wordMap.containsKey(tokens[n].toLowerCase())))
			{
				createSVMInput.wordMap.put(tokens[n].toLowerCase(), new Integer(key));
			}
			else
			{
				key = createSVMInput.wordMap.get(tokens[n].toLowerCase());
			}
			createSVMInput.tweetAllWords.add(key);
			keyValueWriter.write(word+":"+key+"\n");
		}

		Collections.sort(createSVMInput.tweetAllWords);
		createSVMInput.tweetUniqueWords = new TreeSet<Integer>();
		output.write(label+" ");
		
		N = createSVMInput.tweetAllWords.size();
		for(int i = 0 ; i < N ; i++)
		{
			int count = 1;
			int     key = createSVMInput.tweetAllWords.get(i);

			for(int j = i+1 ; j < N ; j++)

			{
				if(createSVMInput.tweetUniqueWords.contains(key))
				{
					continue;
				}

				int key1 = createSVMInput.tweetAllWords.get(j);

				if(key == key1)
				{
					count++;
				}
			}

			if(!(createSVMInput.tweetUniqueWords.contains(key)))
			{
				if(i<(N-1))
					output.write(key+":"+count+" ");
				else
					output.write(key+":"+count);
			}


			createSVMInput.tweetUniqueWords.add(key);
		}       
		output.write("\n");
		return createSVMInput;
    }

	public static void create(Map<Long,Tweet>healthTrainingData,Map<Long,Tweet>filteredTweets
			,Set<String>stopWords,String tempTrainFile,String keyValuePair)
	{
		/*
		 * Open file for writing
		 */
		System.out.println("Health Training Data Size: " + healthTrainingData.size());
		System.out.println("Filtered Tweets Size:" + filteredTweets.size());
		CreateSVMInput createSVMInput = new CreateSVMInput();
		createSVMInput.wordMap = new HashMap<String,Integer>();
		try{
			
        PrintWriter output = new PrintWriter(new BufferedWriter
        		(new FileWriter(tempTrainFile,true)));
        PrintWriter keyValueWriter = new PrintWriter(new BufferedWriter
        		(new FileWriter(keyValuePair,true)));
        
		Iterator iterator1 = healthTrainingData.entrySet().iterator();
		String text = null;
		String label = null;
		while(iterator1.hasNext())
		{
			Map.Entry pairs = (Map.Entry) iterator1.next();
			Tweet tweet = (Tweet)pairs.getValue();
			text = (String)tweet.text;
			label = (String)tweet.label;
			createSVMInput = createSVMInput.writeFile(createSVMInput, text, stopWords,
					output, keyValueWriter, label);
		}
		Iterator iterator2 = filteredTweets.entrySet().iterator();
		while(iterator2.hasNext())
		{
			Map.Entry pairs = (Map.Entry)iterator2.next();
			Tweet tweet = (Tweet) pairs.getValue();
			text = tweet.text;
			createSVMInput = createSVMInput.writeFile(createSVMInput, text, stopWords,
					output, keyValueWriter, "1");
		}
		output.close();
		keyValueWriter.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
