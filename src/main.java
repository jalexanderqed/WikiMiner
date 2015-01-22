import JSONPackages.WikiPage;
import binaryTree.PageNode;
import binaryTree.PageTree;

import com.google.gson.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class main {
	public static void main(String[] args) {
		Gson gson = getGsonObject();
		

		String pageText = getUrl("http://en.wikipedia.org/w/api.php?action=parse&page=Max_Schneider&contentmodel=json&format=json");
		
		System.out.print("Writing first json to file: ");
		start = System.currentTimeMillis();
		writeToFile("page_data/page.txt", pageText);
		System.out.println(System.currentTimeMillis() - start);

		WikiPage page = new WikiPage();
		
		try{
			System.out.print("Converting to object: ");
			start = System.currentTimeMillis();
			page = gson.fromJson(pageText, WikiPage.class);
			System.out.println(System.currentTimeMillis() - start);
		}
		catch(com.google.gson.JsonSyntaxException e){
			System.out.println("Invalid URL: " + e.getMessage());
		}
		
		System.out.print("Converting back to string: ");
		start = System.currentTimeMillis();
		String pageTextFromJson = gson.toJson(page);
		System.out.println(System.currentTimeMillis() - start);
		
		System.out.print("Writing second json to file: ");
		start = System.currentTimeMillis();
		writeToFile("page_data/pageJSON.txt", pageTextFromJson);
		System.out.println(System.currentTimeMillis() - start);
	}
	
	public static String getUrl(String urlText){
		System.out.print("Loading page: ");
		long start = System.currentTimeMillis();
		try{
			final URL url = new URL(urlText);
			InputStreamReader i = new InputStreamReader(url.openStream());
			BufferedReader in = new BufferedReader(i);
			
			StringBuilder totalText = new StringBuilder();
			String inputLine;

			while ((inputLine = in.readLine()) != null) {
				totalText.append(inputLine + "\n");
			}

			System.out.println(System.currentTimeMillis() - start);
			return totalText.toString();
		}
		catch(MalformedURLException e){
			System.out.println("Could not access URL: " + e.getMessage());
			return "";
		}
		catch(IOException e){
			System.out.println("Could not access URL: " + e.getMessage());
			return "";
		}
	}

	public static boolean writeToFile(String fileName, String text){
		System.out.print("Writing to file: ");
		long start = System.currentTimeMillis();
		try {
			File file = new File(fileName);
			BufferedWriter output = new BufferedWriter(new FileWriter(file));
			output.write(text);
			output.close();
			System.out.println(System.currentTimeMillis() - start);
			return true;
		}
		catch(IOException e){
			System.out.println("Could not write to file: " + fileName + "\n" + e.getMessage());
			return false;
		}
	}
	
	public static Gson getGsonObject(){
		return new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	}
}
