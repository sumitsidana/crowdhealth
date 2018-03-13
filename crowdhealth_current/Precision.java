import java.io.*;
import java.util.*;
public class Precision {
	public static void main(String[] args){
	try{
		
		FileInputStream fileInputStream  = new FileInputStream(args[0]);
		DataInputStream dataInputStream       = new DataInputStream(fileInputStream);
		BufferedReader bufferedReader        = new BufferedReader(new InputStreamReader(dataInputStream));
		String line1;
		long precision = 0;
		long sum = 0;
		int index = 0;
		String date = null;
		while((line1 = bufferedReader.readLine())!=null){	
			index++;
			
			if(((index - 7)%25)==0){
			date = line1.substring(line1.indexOf("Populated English Tweets ")+25);
			}
			if(((index - 28)%25)==0){
			 if(index ==3)
                        continue;

			precision = Long.parseLong((line1.substring(line1.indexOf(" Total Time: ")+13)).replace(".0",""));
			sum = sum+precision;
			System.out.println(date+","+precision);
			}	
			}	
	System.out.println("Sum: "+sum);
	bufferedReader.close();
	}
	
	catch(Exception e)
	{
		e.printStackTrace();
	}
	}

}
