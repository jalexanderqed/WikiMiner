import JSONPackages.WikiPage;
import WikiPageClasses.linkObject;

import com.google.gson.*;

import org.jsoup.Jsoup;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class MiningFuncs {
	public static final int PROGRAM_TIME_SECONDS = 30;
	public static final int PROGRAM_TIME_MS = PROGRAM_TIME_SECONDS * 1000;
	public static final boolean REPRESS_PRINT = false;
	public static final String OPERATION = "run";

	public static void main(String[] args) {

		long start = System.currentTimeMillis();
		Gson gson = getGsonObject();
		File dataTreeFile = new File("PageDataTree.json");

		PageTree myTree;

		if(!dataTreeFile.exists()){ // If the binary tree storage file does not exist, build a new tree
			String pageText = getUrl("http://en.wikipedia.org/w/api.php?action=parse&"	// starting at the Wikipedia page
					+ "page=Page_(paper)&contentmodel=json&format=json");				// "Page_(paper)"

			WikiPage firstPage = new WikiPage();

			try{
				firstPage = gson.fromJson(pageText, WikiPage.class);
			}
			catch(com.google.gson.JsonSyntaxException e){
				System.out.println("Invalid URL: " + e.getMessage());
			}

			WikiPageStore firstPageStore = new WikiPageStore(firstPage.parse.title, firstPage.parse.links, firstPage.parse.text.text);
			writeToFile("page_data/" + normalizeTitle(firstPageStore.name) + ".json", gson.toJson(firstPageStore));

			myTree = new PageTree(new PageNode(firstPageStore.name, "page_data/" + normalizeTitle(firstPageStore.name) + ".json"));

			getPagesLinkedFrom(firstPageStore, myTree);
		}
		else{
			myTree = gson.fromJson(readFromFile("PageDataTree.json"), PageTree.class);
			long resetStart = System.currentTimeMillis();
			System.out.println("Resetting: " + (System.currentTimeMillis() - resetStart));
		}

		if(OPERATION.equals("query")){
			System.out.println("Tree size: " + myTree.size());
			System.out.println("Tree depth: " + myTree.depth());
			return;
		}
		else if(OPERATION.equals("index")){
			myTree.iterateWithCallTo(new BackIndexer());
			writeTree(myTree);
		}
		else if(OPERATION.equals("run")){}
		else{
			return;
		}


		MinerThread[] miners = new MinerThread[1];
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

		boolean finished = false;
		while(!finished){
			try{
				Thread.sleep(5000);
			} catch (InterruptedException e){
				System.out.println("Sleep interrupted: " + e.getMessage());
			}
			finished = true;
			for(int i = 0; i < miners.length; i++){
				if(!miners[i].finished){
					System.out.println("Thread " + i + " (indexing " + miners[i].indexing + ") " +
							" not complete. Last call was " + ((System.currentTimeMillis() - miners[i].lastCall) / 1000) +
							" seconds ago.");
					finished = false;
				}
			}
		}

		System.out.println("Total program time: " + ((System.currentTimeMillis() - start) / 60000.0) + " minutes.");
		System.out.println("Total program time: " + ((System.currentTimeMillis() - start) / 1000.0) + " seconds.");
		System.out.println("Total program time: " + (System.currentTimeMillis() - start) + " milliseconds.");
		System.out.println("Tree size: " + myTree.size());
		System.out.println("Tree depth: " + myTree.depth());
	}

	/* Adds all pages linked from the source page to the binary search tree and saves
	 * their JSON data to page_data with call to addArrayOfPages, which calls addPage
	 */
	public static void getPagesLinkedFrom(WikiPageStore sourcePage, PageTree myTree){
		System.out.println("Called getPagesLinkedFrom");
		PageNode sourcePageNode = myTree.getPage(new PageNode(sourcePage.name));
		sourcePageNode.beingIndexed = true;
		if(!REPRESS_PRINT) System.out.println("Getting pages linked from page: " + sourcePage.name);
		Gson gson = getGsonObject();

		addArrayOfPages(sourcePage.links, 0, sourcePage.links.length, myTree, sourcePage);

		sourcePageNode.indexed = true;
		writeTree(myTree);
	}

	/* Adds pages starting at "from" up to (not including) "to" in the array toAdd to the passed tree,
	 * using "sourcePage" as the page from which they are linked.
	 */
	public static void addArrayOfPages(linkObject[] toAdd, int from, int to, PageTree myTree, WikiPageStore sourcePage){
		System.out.println("Called addArrayOfPages from " + from + " to " + to + " for array length " + toAdd.length);
		if(to - from < 0){
			System.out.println("Error in addArrayOfPages. Attempted to add from " + from + " to " + to);
		}
		if(to == from){
			return;
		}
		else if(to - from > 1){
			int halfPoint = (to - from) / 2;
			if(!REPRESS_PRINT) System.out.println("Adding page at array index " + (from + halfPoint));
			addPage(toAdd[from + halfPoint], myTree, sourcePage);
			addArrayOfPages(toAdd, from, from + halfPoint, myTree, sourcePage);
			addArrayOfPages(toAdd, from + halfPoint + 1, to, myTree, sourcePage);
		}
		else{ // to - from = 1 (one element to add)
			if(!REPRESS_PRINT) System.out.println("Adding page at array index " + (from));
			addPage(toAdd[from], myTree, sourcePage);
		}
	}

	/* Adds a page to the binary search tree and saves its JSON data
	 * to page_data folder.
	 */
	public static void addPage(linkObject current, PageTree myTree, WikiPageStore sourcePage){
		System.out.println("Called addPage");
		if((current.page.indexOf("Help") == 0) || (current.page.indexOf("Wikipedia") == 0) ||
				(current.page.indexOf("Talk") == 0) || (current.page.indexOf("Portal") == 0)){
			System.out.println("Did not create page " + current.page.replace(' ', '_'));
			return;
		}
		Gson gson = getGsonObject();
		if(current.exists != null){
			// Only create new page if the page is new
			if(!myTree.contains(new PageNode(current.page))){
				String pageText = getUrl("http://en.wikipedia.org/w/api.php?action=parse&"
						+ "page=" + current.page.replace(' ', '_')
						+ "&contentmodel=json&format=json");

				WikiPage currentPage = new WikiPage();

				try{
					currentPage = gson.fromJson(pageText, WikiPage.class);
				}
				catch(com.google.gson.JsonSyntaxException e){
					System.out.println("Invalid URL: " + e.getMessage());
					return;
				}

				if(currentPage == null || currentPage.parse == null || currentPage.parse.title == null ||
						currentPage.parse.links == null || (currentPage.parse.title.indexOf("Template") == 0)){
					System.out.println("Could not create page " + current.page.replace(' ', '_'));
					return;
				}
				WikiPageStore currentPageStore = new WikiPageStore(currentPage.parse.title, currentPage.parse.links, currentPage.parse.text.text);

				if(writeToFile("page_data/" + normalizeTitle(currentPage.parse.title) + ".json", gson.toJson(currentPageStore))){
					long start = System.currentTimeMillis();
					PageNode newPage = new PageNode(currentPageStore.name, "page_data/" + normalizeTitle(currentPage.parse.title) + ".json");
					newPage.linkedFrom.addPage(new PageNode(sourcePage.name));
					myTree.addPage(newPage);
					if(!REPRESS_PRINT) System.out.println("Writing page " + currentPage.parse.title + " to tree: " + (System.currentTimeMillis() - start));
				}
				else{
					System.out.println("Could not write " + currentPage.parse.title + " to file.");
					return;
				}
			}
			else{
				if(!REPRESS_PRINT) System.out.println("Data tree already contained page " + current.page);
				return;
			}
		}
	}

	public static void indicatePagesLinkedFrom(PageNode sP, PageTree myTree){
		WikiPageStore sourcePage = new WikiPageStore(sP);
		for(int i = 0; i < sourcePage.links.length; i++){
			PageNode latestLink = myTree.getPage(new PageNode(sourcePage.links[i].page));
			if(latestLink != null){
				latestLink.linkedFrom.addPage(new PageNode(sourcePage.name, false));
				if(!REPRESS_PRINT) System.out.println("Indicated page " + sourcePage.links[i].page + " linked from " + sourcePage.name);
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

			if(!REPRESS_PRINT) System.out.println("Loading page: " + (System.currentTimeMillis() - start));
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
			if(!REPRESS_PRINT) System.out.println("Writing to file: " + (System.currentTimeMillis() - start));
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
			if(!REPRESS_PRINT) System.out.println("Reading from file " + fileName + ": " + (System.currentTimeMillis() - start));
			return inText.toString();
		}
		catch(IOException e){
			System.out.println("Could not read from file: " + fileName + "\n" + e.getMessage());
			return "";
		}
	}

	public static void writeTree(PageTree myTree){
		writeToFile("PageDataTree.json", MiningFuncs.getGsonObject().toJson(myTree));
	}

	public static Gson getGsonObject(){
		return new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	}

	public static String normalizeTitle(String title){
		return title.replaceAll("[^A-Za-z0-9 ]", "" + (int)title.charAt(0));
	}
}
