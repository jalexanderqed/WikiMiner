import org.jsoup.Jsoup;

public class StatFinder extends NodeOperator {
	int index = 0;
	StringBuilder stats;

	public StatFinder(){
		stats = new StringBuilder();
		stats.append("Page Title,Number of Words,Number of Sentences,Average Word Length,Average Sentence Length," +
				"Incoming Links,Outgoing Links\n");
	}

	public boolean call(PageNode node, PageTree myTree){
		index++;
		if(index % 100 == 0){
			System.out.println("StatFinder at index " + index);
		}
		WikiPageStore current = new WikiPageStore(node);
		String normalizedText = Jsoup.parse(current.text).text();
		String[] sentences = getSentences(normalizedText);
		String[] words = normalizedText.split(" ");
		current.sentences = sentences.length;
		current.words = words.length;
		current.sentenceLength = ((double)current.words) / current.sentences;
		current.wordLength = ((double)normalizedText.length()) / current.words;
		stats.append("\"" + current.name + "\"" + "," + current.words + "," + current.sentences + "," + current.wordLength
				+ "," + current.sentenceLength + "," + node.linkedFrom.size() + "," + node.links.length + "\n");
		return true;
	}

	public static String[] getSentences(String str){
		return str.split("(?<=[.?!])\\s+(?=[a-zA-Z0-9])");
	}
}
