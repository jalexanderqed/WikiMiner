package storageClasses;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import binaryTree.PageNode;
import JSONPackages.WikiPage;
import WikiPageClasses.linkObject;

public class WikiPageStore {
	public String name;
	public linkObject[] links;

	public WikiPageStore(String pageName, linkObject[] pageLinks){
		name = pageName;
		links = pageLinks;
	}

	public WikiPageStore(PageNode source){
		Gson gson = getGsonObject();
		try{
			WikiPageStore newPage = gson.fromJson(readFromFile(source.fileName), WikiPageStore.class);
			name = newPage.name;
			links = newPage.links;
		}
		catch(com.google.gson.JsonSyntaxException e){
			System.out.println("Invalid data in file: " + e.getMessage());
		}
	}

	public static String readFromFile(String fileName){
		System.out.print("Reading from file: ");
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
			System.out.println(System.currentTimeMillis() - start);
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
