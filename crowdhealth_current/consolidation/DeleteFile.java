package consolidation;
import java.io.*;
import java.io.File;

public class DeleteFile {

	public static void deleteF(String strFilePath)
	{
		File file = new File(strFilePath);
		file.delete();
	}
}
