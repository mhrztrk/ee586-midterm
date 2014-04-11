package lib;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class GraphSearch<T> {
	
	private int algo;
	private boolean GoalFound = false;
	private T FinalState;
	private boolean TerminateSearch = false;	
	private ArrayList<T> VisitedStates; 
	
	public class Statistics {
		public Statistics() {
			nStateTransitions = 0;
			nDetectedLoops = 0;
			MaxQueueLength = 0;
			msExecTime = 0.0;
		}
		public int nStateTransitions;
		public int nDetectedLoops;
		public int MaxQueueLength;
		public double msExecTime;
	}
	
	private Statistics SearchStat;
	
	public void Terminate() {
		TerminateSearch = true;
	}
	
	public boolean IsGoalFound() {
		return GoalFound;
	}
	
	public T getFinalState() {
		return FinalState;
	}
	
	private class PriorityQueueItem {
		public PriorityQueueItem(T state, int cost){
			this.state = state;
			this.cost = cost;
		}
		T state;
		int cost;
	}
	
	public class ALGO{
		   public static final int BFS = 0;
		   public static final int DFS = 1;
		   public static final int IDS = 2;
		   public static final int ASTAR = 3;
	}
	
	/* Constructor  */
	public GraphSearch(int algo) {
		this.algo = algo;
	}
	
	public class CostComparator implements Comparator<GraphSearch<T>.PriorityQueueItem> {
		@Override
		public int compare(PriorityQueueItem item1, PriorityQueueItem item2) {
			
			if (item1.cost < item2.cost) {
				return -1;
			} else if (item1.cost > item2.cost) {
				return 1;
			} else {
				return 0;
			}
			
		}
	}
	
	private void BFS(T root) {
		
		LinkedList<T> queue = new LinkedList<T>();
		T node;
		ArrayList<T> succs;
		
		queue.addFirst(root);
		while(!queue.isEmpty()) {
			node = queue.remove();
			SearchStat.nStateTransitions++;
			
			/*
			 * Check if the goal is reached
			 */
			if(IsGoalReached(node)) {
				GoalFound = true;
				FinalState = node;
				return;
			}
			
			/*
			 * Get successor states and push them to queue 
			 */
			succs = Successor();
			for (int i = 0; i < succs.size(); i++) {
				queue.add(succs.get(i));
			}
			
			if(SearchStat.MaxQueueLength < queue.size()) {
				SearchStat.MaxQueueLength = queue.size();
			}
			
			/* FIXME: remove instead use interrupted() */
			if(TerminateSearch) { 
				TerminateSearch = false;
				break;
			}
		}
		return;
	}
	
	private void DFS(T root) {
		ArrayList<T> succs;
		
		if(root == null) {
			return;
		}
		
		/* Detect loop */
		if(IsLoopExist(root, VisitedStates)) {
			SearchStat.nDetectedLoops++;
			return;
		}
		
		/* save visited states to detect loops */
		VisitedStates.add(root);
		
		SearchStat.nStateTransitions++;
		
		/*
		 * Check if goal is reached
		 */
		if(IsGoalReached(root)) {
			GoalFound = true;
			FinalState = root;
			return;
		}
		
		/*
		 * Get successor states and push them to queue 
		 */
		succs = Successor();
		for (int i = 0; i < succs.size(); i++) {
			DFS(succs.get(i));
			if(GoalFound || TerminateSearch) {
				return;
			}
		}
		
		return;
	}
	
	private void DFSLimited(T root, int maxDepth) {
		
		ArrayList<T> succs;
		
		if(root == null) {
			return;
		}
		
		if(maxDepth < 1) {
			return;
		}
			
		SearchStat.nStateTransitions++;
		
		/*
		 * Check if goal is reached
		 */
		if(IsGoalReached(root)) {
			GoalFound = true;
			FinalState = root;
			return;
		}
		
		/* Get successor states  */
		succs = Successor();
		for (int i = 0; i < succs.size(); i++) {
			DFSLimited(succs.get(i), maxDepth-1);
			if(GoalFound || TerminateSearch) {
				return;
			}
		}
		
		return;
	}
	
	private boolean IDS(T root) {
		
		int maxDepth = 1; 
		while(!GoalFound) {
			/*
			 * In each depth, we need to clear list of visited nodes.
			 */
			DFSLimited(root, maxDepth);
			maxDepth++;
			
			if(TerminateSearch) { 
				TerminateSearch = false;
				break;
			}
		}
		
		return GoalFound;
	}
	
	private void AStar(T root) {
		
		T currNode;
		ArrayList<T> succs;
		
		/*
		 * Construct priority queue  
		 */
		CostComparator comparator = new CostComparator();
		Queue<PriorityQueueItem> queue = new PriorityQueue<PriorityQueueItem>(1, comparator);
		
		queue.add(new PriorityQueueItem(root, getHeuristicCost(root)));
		
		currNode = root;
		while(!queue.isEmpty()) {
			
			currNode = queue.remove().state;
			SearchStat.nStateTransitions++;
			
			/*
			 * Check if goal is reached
			 */
			if(IsGoalReached(currNode)) {
				GoalFound = true;
				FinalState = currNode;
				return;
			}
			
			VisitedStates.add(currNode);
			
			/*
			 * Get successor states and push them to queue 
			 */
			succs = Successor();
			for (int i = 0; i < succs.size(); i++) {
				/*
				 * If already visited, do not add to queue (loop avoidance)
				 */
				if(!IsLoopExist(succs.get(i), VisitedStates)) {
					queue.add(new PriorityQueueItem(succs.get(i), getHeuristicCost(succs.get(i))));
				} else {
					SearchStat.nDetectedLoops++;
				}
			}
			
			if(SearchStat.MaxQueueLength < queue.size()) {
				SearchStat.MaxQueueLength = queue.size();
			}
			
			if(TerminateSearch) { 
				TerminateSearch = false;
				return;
			}
		}
		
	}
	
	public void Solve(T InitialState) {
		
		SearchStat = new Statistics();
		TerminateSearch = false;
		VisitedStates = new ArrayList<T>();
		
		long stime = System.nanoTime();
		
		if(algo == ALGO.BFS) {
			BFS(InitialState);
		} else if (algo == ALGO.DFS) {
			DFS(InitialState);
		} else if (algo == ALGO.IDS) {
			IDS(InitialState);
		} else if (algo == ALGO.ASTAR) {
			AStar(InitialState);
		}
		
		SearchStat.msExecTime = (double)(System.nanoTime() - stime)/1000000;
		
		return;
	}
	
	public Statistics getStatistics() {
		return SearchStat;
	}
	
	/*
	 * These functions need to be overrided accordingly 
	 */
	public ArrayList<T> Successor() {return null;};
	public int getHeuristicCost (T state) {return 0;}
	public boolean IsGoalReached(T state) {return false;}
	public boolean IsLoopExist(T state, ArrayList<T> VisitedStates) {return false;}
}
