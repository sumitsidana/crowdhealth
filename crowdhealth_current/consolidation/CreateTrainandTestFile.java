package consolidation;
import java.io.*;
public class CreateTrainandTestFile {
	
	public static void create(String pathToTempInputFormat, String pathToTrainFile, String pathToTestFile)
	{
		long lineNumber = 0;
		try{
			FileInputStream fstream = new FileInputStream
					(pathToTempInputFormat);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line = null;
	        PrintWriter output = new PrintWriter(new BufferedWriter
	        		(new FileWriter(pathToTrainFile,true)));/*args4*/
	        PrintWriter keyValueWriter = new PrintWriter(new BufferedWriter
	        		(new FileWriter(pathToTestFile,true)));/*args5*/
			while((line = br.readLine())!=null)
			{
				lineNumber++;
				if(lineNumber <= 5070)
				{
					output.write(line+"\n");
				}
				else
				{
					keyValueWriter.write(line+"\n");
				}
				
			}
			keyValueWriter.close();
			output.close();
			br.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
