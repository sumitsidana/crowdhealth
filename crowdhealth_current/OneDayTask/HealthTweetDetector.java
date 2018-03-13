package consolidation;
import java.util.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.*;

public class HealthTweetDetector {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Map<Long,Tweet>allTweets = new LinkedHashMap<Long,Tweet>();
		Map<Long,Tweet>englishTweets = new LinkedHashMap<Long,Tweet>();
		Map<Long,Tweet>tweetWoURL = new LinkedHashMap<Long, Tweet>();
		Map<Long,Tweet>tweetWoUnRT = new LinkedHashMap<Long,Tweet>();
		Map<Long,Tweet>filteredTweets = new LinkedHashMap<Long,Tweet>();
		Map<Long,Tweet>healthClassifiedTweets = new LinkedHashMap<Long,Tweet>();
		Map<Long,Tweet>healthTrainingData = new LinkedHashMap<Long,Tweet>();
		
        Set<String>stopWords = new HashSet<String>();
		try{
			/*
			 * Do precomputation
			 */
			long countOfTrainingData = 0;
			FileInputStream fstream = new FileInputStream
					(args[1]);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line = null;
			while((line = br.readLine())!=null)
			{
				countOfTrainingData++;
				String [] array = line.split("\t");
				Tweet tweet = new Tweet();
				tweet.text = array[2];
				tweet.label = array[1];
				healthTrainingData.put(countOfTrainingData,tweet);
			}
            FileInputStream fstream1  = new FileInputStream(args[2]);
            DataInputStream in1       = new DataInputStream(fstream1);
            BufferedReader br1        = new BufferedReader(new InputStreamReader(in1));
            String line1;
            while((line1 = br1.readLine())!=null)
            {
                    stopWords.add(line1.toLowerCase());
            }
            
		}
		catch(Exception e) {e.printStackTrace();}

		try{
			Class.forName("org.postgresql.Driver");
		}
		catch (ClassNotFoundException e) {

			System.out.println("Where is your PostgreSQL JDBC Driver? "
					+ "Include in your library path!");
			e.printStackTrace();
			return;
		}
		System.out.println("PostgreSQL JDBC Driver Registered!");
		Connection connection = null;
		Statement stmt = null;
		try {

			connection = DriverManager.getConnection(
					"jdbc:postgresql://localhost:5432/twitterdb", "twitter_ro","");
			connection.setAutoCommit(false);

			DateTimeFormatter format = DateTimeFormat.forPattern
					("yyyy-MM-dd");
			DateTime now = new DateTime();
			System.out.println("Previous :" + format.print(now));	
			DateTime fourDaysAgo = now.minusDays(4);
			System.out.println("Updated :" + format.print(fourDaysAgo));
			double time3=System.currentTimeMillis();
			for(LocalDate date = fourDaysAgo.toLocalDate() ;
					date.isBefore(now.toLocalDate());date = date.plusDays(1) )
			{
				double time1=System.currentTimeMillis();
				System.out.println("Joining Tables");
				stmt = connection.createStatement();
		                ResultSet rs = stmt.executeQuery("select * from tweet join twitter_user on " +
                                "tweet.user_id = twitter_user.user_id where" +
                                " twitter_user.lang_id = 2 " +
                                "and tweet.created_at >=" +"\'"+date+"\'"+
                                "and tweet.created_at <"+"\'"+date.plusDays(1)+"\'"+";");
				System.out.println("Joined Tables");
				System.out.println("Populating English Tables");
				long index = 0;
				while(rs.next() ) {
					index ++;
					Tweet tweet = new Tweet();
					tweet.tweet_id = rs.getLong("tweet_id") ;
					tweet.user_id = rs.getLong("user_id") ;
					tweet.text = rs.getString("text") ;
					tweet.created_at = rs.getString(4) ;//This is ambiguous
					tweet.captured_at = rs.getString("captured_at") ;
					tweet.user_tweet_num = rs.getLong("user_tweet_num") ;
					tweet.saved_at = rs.getString("saved_at") ;
					tweet.gps = rs.getString("gps") ;
					tweet.user_created_at = rs.getString(13); //This is ambiguous
					tweet.user_first_seen_at = rs.getString("first_seen_at");
					tweet.user_lang_id = rs.getLong("lang_id");
					tweet.user_stats_first_seen = rs.getString("stats_first_seen");
					tweet.user_stats_now = rs.getString("stats_now");
					englishTweets.put(index, tweet);
				}
				System.out.println("Populated English Tweets "+date);
				System.out.println("English Tweets Size: "+ englishTweets.size());
				tweetWoURL = URLRemover.urlRemover(englishTweets);
				System.out.println("size: "+tweetWoURL.size());
				System.out.println("URL Removed");
				tweetWoUnRT =UserNameRemover.userNameRemove(tweetWoURL);
				System.out.println("size: "+tweetWoUnRT.size());
				System.out.println("User Name and HashTag removed");
				Filter filter = new Filter
						(args[0]);
				filteredTweets = filter.filterOnHealthKeyWords(tweetWoUnRT);
				System.out.println("size: "+filteredTweets.size());
				System.out.println("Filtering Done");
				CreateSVMInput.create(healthTrainingData, filteredTweets, stopWords, args[3], args[7]);
				System.out.println("SVM preliminary input created");
				CreateTrainandTestFile.create(args[3],args[5],args[4]);
				System.out.println("SVM final output created");
				System.out.println("Working Directory = " +
              			System.getProperty("user.dir"));
				try{
					Process process2 = Runtime.getRuntime().exec
					(System.getProperty("user.dir")+"/"+args[6]+"TwoClassClassificationInsideIdtonne.m",
							null,new File(System.getProperty("user.dir")+"/"+args[6]));
					try
        				{
				           int exitVal =  process2.waitFor();
					  System.out.println("value of thread: "+ exitVal);  	
        				}
        				catch(InterruptedException e)
        				{
            					System.out.println("Command failed");
        				}
				}
				
				catch(Exception e)
				{
					e.printStackTrace();
				}
				
				System.out.println("Classification Done");
				/*
				 * Assuming  decision_value has been written
				 */
                                System.out.println("Working Directory = " +
                                System.getProperty("user.dir"));
				System.out.println("Extracting Health Tweets");
				ExtractHealthTweets.Extract(filteredTweets, args[8]);
				System.out.println("Health File Written");
				/*
				 * After using delete the files InputFile, TrainFile, InputFileTemp,
				 * decision_value, predicted_label, accuracy
				 * 
				 */
				rs.close();
				stmt.close();
				System.out.println("Deleting Files");
				DeleteFile.deleteF(args[3]);
				DeleteFile.deleteF(args[4]);
				DeleteFile.deleteF(args[5]);
				DeleteFile.deleteF(args[7]);
				DeleteFile.deleteF(args[8]);
				DeleteFile.deleteF(args[9]);
				
				englishTweets.clear(); 
				tweetWoURL.clear();
				tweetWoUnRT.clear();		
				filteredTweets.clear();
				double time2=System.currentTimeMillis();
				System.out.println("\n Total Time: "+(time2-time1));
			}
			healthTrainingData.clear();
			connection.close();
			double time4=System.currentTimeMillis();
			System.out.println("\n Total Time for all dates: "+(time4-time3));
		}
		catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
		}
	}
}
