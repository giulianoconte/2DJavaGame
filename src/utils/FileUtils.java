package utils;

import static utils.OutputUtils.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FileUtils {
	
	private FileUtils() {
		
	}
	
	public static String loadAsString(String file) {
		StringBuilder result = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String buffer = "";
			while ((buffer = reader.readLine()) != null) {
				result.append(buffer + '\n');
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result.toString();
	}
	
	private static void writeLog(String className, ArrayList<String> outputContent) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		String timeStamp = dateFormat.format(date); //2017/10/15 12:27:39
		outputContent.add(0, timeStamp);
		
		String pathString = "logs/" + className.replace('.', '/');
		Path fileOutputDirectory = Paths.get(pathString);
		Path fileOutputPath = Paths.get(pathString + "/log.txt");
		try {
			if (!Files.exists(fileOutputDirectory)) Files.createDirectories(fileOutputDirectory);
			Files.write(fileOutputPath, outputContent);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void writeLog(Class caller, ArrayList<String> outputContent) {
		String className = caller.getCanonicalName();
		writeLog(className, outputContent);
	}
}
