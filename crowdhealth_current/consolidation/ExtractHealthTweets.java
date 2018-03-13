package consolidation;
import java.util.*;
import java.io.*;
import java.util.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;

public class ExtractHealthTweets {
	
	public static void Extract(Map<Long,Tweet>tweetsTobeTested,String fileDecisionValue
			/*args8*/)
	{
		try{
        FileInputStream fstream2    = new FileInputStream(fileDecisionValue);
        DataInputStream in2       = new DataInputStream(fstream2);
        BufferedReader br2        = new BufferedReader(new InputStreamReader(in2));
        String strLine2;
        int iterator = 0;
	int numHealthTweets = 0;
        List<Double> labels = new ArrayList<Double> ();
        while((strLine2 = br2.readLine())!=null)
        {
                labels.add(Double.parseDouble(strLine2));
        }
        //System.out.println(labels.size());
        Iterator iteratorMap = tweetsTobeTested.entrySet().iterator();
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
                PreparedStatement stmt = null;
                connection = DriverManager.getConnection(
                                        "jdbc:postgresql://localhost:5432/twitterdb", "twitter_health","");
		

        
        while(iteratorMap.hasNext())
        {
        		Map.Entry pairs = (Map.Entry)iteratorMap.next();
                        Tweet tweetObj = (Tweet) pairs.getValue();

        		
                if((labels.get(iterator)) > 0.4349999999999998)
                {
			String query = "INSERT INTO healthtweet(tweet_id,disease_id) VALUES(?, ?)";
                	stmt = connection.prepareStatement(query);
			stmt.setLong(1,tweetObj.tweet_id);
			stmt.setNull(2,java.sql.Types.INTEGER);
			int rowCount = stmt.executeUpdate();
			numHealthTweets++;
                }
			
			iterator++;
        }
	connection.close();
	System.out.println("number of Health Tweets: "+numHealthTweets);
      }
      catch(Exception e){
              e.printStackTrace();
      }
}

	}


