package binaryTree;

import JSONPackages.WikiPage;

public class PageNode {
	public String name;
	public WikiPage pageData;
	public PageNode left;
	public PageNode right;

	public PageNode(String n){
		name = n;
	}
	
	public PageNode(String n, WikiPage data){
		name = n;
		pageData = data;
	}
}