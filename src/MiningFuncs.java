import JSONPackages.WikiPage;
import binaryTree.PageNode;
import binaryTree.PageTree;

import com.google.gson.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import storageClasses.WikiPageStore;

public class MiningFuncs {
	public static void main(String[] args) {
		
		long start = System.currentTimeMillis();
		Gson gson = getGsonObject();
		File dataTreeFile = new File("PageDataTree.json");

		PageTree myTree;

		if(!dataTreeFile.exists()){
			String pageText = getUrl("http://en.wikipedia.org/w/api.php?action=parse&"
					+ "page=Infinite_set&contentmodel=json&format=json");

			WikiPage firstPage = new WikiPage();

			try{
				firstPage = gson.fromJson(pageText, WikiPage.class);
			}
			catch(com.google.gson.JsonSyntaxException e){
				System.out.println("Invalid URL: " + e.getMessage());
			}

			WikiPageStore firstPageStore = new WikiPageStore(firstPage.parse.title, firstPage.parse.links);
			writeToFile("page_data/" + firstPageStore.name + ".json", gson.toJson(firstPageStore));

			myTree = new PageTree(new PageNode(firstPageStore.name, "page_data/" + firstPageStore.name + ".json"));

			getPagesLinkedFrom(firstPageStore, myTree);
		}
		else{
			myTree = gson.fromJson(readFromFile("PageDataTree.json"), PageTree.class);
		}
		
		myTree.resetCalls();

		MinerThread[] miners = new MinerThread[50];
		for(int i = 0; i < miners.length; i++){
			miners[i] = new MinerThread("miner" + i, myTree);
		}

		for(int i = 0; i < miners.length; i++){
			miners[i].start();
			try{
				Thread.sleep(50);
			} catch (InterruptedException e){
				System.out.println("Sleep interrupted: " + e.getMessage());
			}
		}

		for(int i = 0; i < miners.length; i++){
			if(!miners[i].finished){
				try{
					System.out.println("Thread " + i + " not complete.");
					Thread.sleep(500);
					i = -1;
				} catch (InterruptedException e){
					System.out.println("Sleep interrupted: " + e.getMessage());
				}
			}
		}

		System.out.println("Total program time: " + ((System.currentTimeMillis() - start) / 60000.0) + " minutes.");
		System.out.println("Total program time: " + ((System.currentTimeMillis() - start) / 1000.0) + " seconds.");
		System.out.println("Total program time: " + (System.currentTimeMillis() - start) + " milliseconds.");
	}

	public static void getPagesLinkedFrom(WikiPageStore sourcePage, PageTree myTree){
		myTree.getPage(new PageNode(sourcePage.name)).beingIndexed = true;
		System.out.println("Getting pages linked from page: " + sourcePage.name);
		Gson gson = getGsonObject();

		int pagesAdded = 0;
		int halfLinksArrayLength = sourcePage.links.length / 2;

		for(int i = 0; i <= halfLinksArrayLength; i++){
			int index = halfLinksArrayLength + i;
			for(int j = 0; j < 2; j++){
				System.out.println("Index: " + index);
				if(index >= 0 && index < sourcePage.links.length){
					pagesAdded++;
					if(sourcePage.links[index].exists != null){
						// Only create new page if the page is new
						if(!myTree.contains(new PageNode(sourcePage.links[index].page))){
							String pageText = getUrl("http://en.wikipedia.org/w/api.php?action=parse&"
									+ "page=" + sourcePage.links[index].page.replace(' ', '_')
									+ "&contentmodel=json&format=json");

							WikiPage currentPage = new WikiPage();

							try{
								currentPage = gson.fromJson(pageText, WikiPage.class);
							}
							catch(com.google.gson.JsonSyntaxException e){
								System.out.println("Invalid URL: " + e.getMessage());
								break;
							}

							if(currentPage == null || currentPage.parse == null || currentPage.parse.title == null || currentPage.parse.links == null){
								break;
							}
							WikiPageStore currentPageStore = new WikiPageStore(currentPage.parse.title, currentPage.parse.links);

							if(writeToFile("page_data/" + currentPage.parse.title + ".json", gson.toJson(currentPageStore))){
								long start = System.currentTimeMillis();
								PageNode newPage = new PageNode(currentPageStore.name, "page_data/" + currentPageStore.name + ".json");
								newPage.linkedFrom.addPage(new PageNode(sourcePage.name));
								myTree.addPage(newPage);
								System.out.println("Writing page " + currentPage.parse.title + " to tree: " + (System.currentTimeMillis() - start));
							}
							else{
								System.out.println("Could not write " + currentPage.parse.title + " to file.");
							}
						}
						else{
							System.out.println("Data tree already contained page " + sourcePage.links[index].page);
						}
					}
				}
				if(i != 0){
					index = halfLinksArrayLength - i;
				}
				else break;
			}
		}
		myTree.getPage(new PageNode(sourcePage.name)).indexed = true;
		writeToFile("PageDataTree.json", gson.toJson(myTree));
		System.out.println("Length of links array: " + sourcePage.links.length);
		System.out.println("Number of pages added: " + pagesAdded);
	}
	
	public static void indicatePagesLinkedFrom(PageNode sP, PageTree myTree){
		WikiPageStore sourcePage = new WikiPageStore(sP);
		for(int i = 0; i < sourcePage.links.length; i++){
			System.out.println("Indicating page " + sourcePage.links[i].page + " linked from " + sourcePage.name);
			PageNode latestLink = myTree.getPage(new PageNode(sourcePage.links[i].page));
			if(latestLink != null){
				latestLink.linkedFrom.addPage(new PageNode(sourcePage.name));
			}
		}
	}

	public static String getUrl(String urlText){
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

			System.out.println("Loading page: " + (System.currentTimeMillis() - start));
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
		long start = System.currentTimeMillis();
		try {
			File file = new File(fileName);
			BufferedWriter output = new BufferedWriter(new FileWriter(file));
			output.write(text);
			output.close();
			System.out.println("Writing to file: " + (System.currentTimeMillis() - start));
			return true;
		}
		catch(IOException e){
			System.out.println("Could not write to file: " + fileName + "\n" + e.getMessage());
			return false;
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
			System.out.println("Reading from file " + fileName + ": " + (System.currentTimeMillis() - start));
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
