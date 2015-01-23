import com.google.gson.Gson;

import storageClasses.WikiPageStore;
import binaryTree.PageTree;

public class MinerThread implements Runnable {
	private Thread t;
	private String threadName;
	private static PageTree myTree;
	private static Gson gson = MiningFuncs.getGsonObject();

	public void run() {
		for(int i = 0; i < 1; i++){
			MiningFuncs.getPagesLinkedFrom(new WikiPageStore(myTree.getUnindexed()), myTree);
			MiningFuncs.writeToFile("PageDataTree.json", gson.toJson(myTree));
		}
	}

	public void start()
	{
		System.out.println("Starting " +  threadName );
		if(t == null)
		{
			t = new Thread (this, threadName);
			t.start();
		}
		else{
			t.start();
		}
	}

	public MinerThread(String name, PageTree mT) {
		threadName = name;
		myTree = mT;
		System.out.println("Creating " +  threadName );
	}
}
