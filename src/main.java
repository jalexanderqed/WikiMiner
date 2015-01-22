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

import storageClasses.WikiPageStore;

public class main {
	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		Gson gson = getGsonObject();

		String pageText = getUrl("http://en.wikipedia.org/w/api.php?action=parse&"
				+ "page=Max_Schneider&contentmodel=json&format=json");

		WikiPage firstPage = new WikiPage();

		try{
			firstPage = gson.fromJson(pageText, WikiPage.class);
		}
		catch(com.google.gson.JsonSyntaxException e){
			System.out.println("Invalid URL: " + e.getMessage());
		}

		WikiPageStore firstPageStore = new WikiPageStore(firstPage.parse.title, firstPage.parse.links);
		writeToFile("page_data/" + firstPageStore.name + ".json", gson.toJson(firstPageStore));

		PageTree myTree = new PageTree(new PageNode(firstPageStore.name, "page_data/" + firstPageStore.name + ".json"));

		for(int i = 0; i < firstPageStore.links.length; i++){
			if(firstPageStore.links[i].exists != null){
				pageText = getUrl("http://en.wikipedia.org/w/api.php?action=parse&"
						+ "page=" + firstPageStore.links[i].page.replace(' ', '_')
						+ "&contentmodel=json&format=json");

				WikiPage currentPage = new WikiPage();

				try{
					currentPage = gson.fromJson(pageText, WikiPage.class);
				}
				catch(com.google.gson.JsonSyntaxException e){
					System.out.println("Invalid URL: " + e.getMessage());
				}

				WikiPageStore currentPageStore = new WikiPageStore(currentPage.parse.title, currentPage.parse.links);
				writeToFile("page_data/" + currentPage.parse.title + ".json", gson.toJson(currentPageStore));
				myTree.addPage(new PageNode(currentPageStore.name, "page_data/" + currentPageStore.name + ".json"));
			}
		}
		writeToFile("PageDataTree.json", gson.toJson(myTree));
		System.out.println("Total program time: " + ((System.currentTimeMillis() - start) / 1000) + " seconds.");
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
