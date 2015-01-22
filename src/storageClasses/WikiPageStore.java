package storageClasses;

import WikiPageClasses.linkObject;

public class WikiPageStore {
	public String name;
	public linkObject[] links;
	
	public WikiPageStore(String pageName, linkObject[] pageLinks){
		name = pageName;
		links = pageLinks;
	}
}
