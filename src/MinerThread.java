import com.google.gson.Gson;

public class MinerThread extends Thread {
	private Thread t;
	private String threadName;
	private static PageTree myTree;
	private static Gson gson = MiningFuncs.getGsonObject();
	public boolean finished = false;

	public void run() {
		finished = false;
		for(int i = 0; i < 10; i++){
			PageNode next = myTree.getRandUnindexed();
			if(next == null){
				System.out.println("getUnindexed() returned null. Mischief managed.");
				System.exit(0);
			}
			MiningFuncs.getPagesLinkedFrom(new WikiPageStore(next), myTree);
			MiningFuncs.writeToFile("PageDataTree.json", gson.toJson(myTree));
		}
		finished = true;
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
