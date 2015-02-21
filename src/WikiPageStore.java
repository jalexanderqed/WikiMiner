

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import WikiPageClasses.linkObject;

public class WikiPageStore {
	public String name;
	public linkObject[] links;
	public String text;
	public String normalizedText;

	public WikiPageStore(String pageName, linkObject[] pageLinks, String t){
		name = pageName;
		links = pageLinks;
		text = t;
	}
	
	public WikiPageStore(String pageName, linkObject[] pageLinks, String t, String nt){
		name = pageName;
		links = pageLinks;
		text = t;
		normalizedText = nt;
	}

	/* Can be used to quickly and easily generate a WikiPageStore object
	 * using the data from a PageNode object, saving time for the caller.
	 */
	public WikiPageStore(PageNode source){
		Gson gson = getGsonObject();
		try{
			WikiPageStore newPage = gson.fromJson(readFromFile(source.fileName), WikiPageStore.class);
			name = newPage.name;
			links = newPage.links;
			text = newPage.text;
		}
		catch(com.google.gson.JsonSyntaxException e){
			System.out.println("Invalid data in file: " + e.getMessage());
		}
	}

	public static String readFromFile(String fileName){
		long start = System.currentTimeMillis();
		try {
			File file = new File(fileName);
			BufferedReader input = new BufferedReader(new FileReader(file));
			String nextLine;
			StringBuilder inText = new StringBuilder();
			while((nextLine = input.readLine()) != null){
				inText.append(nextLine);
			}
			input.close();
			return inText.toString();
		}
		catch(IOException e){
			System.out.println("Could not read from file: " + fileName + "\n" + e.getMessage());
			return "";
		}
	}
	
	public static Gson getGsonObject(){
		return new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	}
}
