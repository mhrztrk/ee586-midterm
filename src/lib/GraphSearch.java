package lib;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class GraphSearch<T> {
	
	private int algo;
	private int mode;
	private boolean GoalFound = false;
	private T FinalState;
	private boolean TerminateSearch = false;	
	private ArrayList<T> VisitedStates; 
	private Semaphore semMoveControl;	
	
	private int Status = STATUS.UNINITIALIZED;
	
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

	public class MODE{
		   public static final int CONTINUOUS = 0;
		   public static final int SINGLESTEP = 1;;
	}
	
	public class STATUS{
		   public static final int UNINITIALIZED = 0;
		   public static final int RUNNING = 1;
		   public static final int STOPPED = 2;;
		   public static final int PAUSED = 3;
		   public static final int FINISHED = 4;
	}
	
	/* Constructor  */
	public GraphSearch(int algo, int mode) {
		this.algo = algo;
		this.mode = mode;
		
		semMoveControl = new Semaphore(1);	/* binary semaphore */
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
			SetState(node);
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
			 * In batch mode, for next move wait user command
			 */
			if(mode == MODE.SINGLESTEP) {
				try {
					Status = STATUS.PAUSED;
					semMoveControl.acquire();
					Status = STATUS.RUNNING;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
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
		
		SetState(root);
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
		 * In batch mode, for next move wait user command
		 */
		if(mode == MODE.SINGLESTEP) {
			try {
				Status = STATUS.PAUSED;
				semMoveControl.acquire();
				Status = STATUS.RUNNING;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
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
			
		SetState(root);
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
		 * In batch mode, for next move wait user command
		 */
		if(mode == MODE.SINGLESTEP) {
			try {
				Status = STATUS.PAUSED;
				semMoveControl.acquire();
				Status = STATUS.RUNNING;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
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
			
			/* FIXME: Use an event listener instead */
			SetState(currNode);
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
			 * In batch mode, for next move wait user command
			 */
			if(mode == MODE.SINGLESTEP) {	/* FIXME: Remove this */
				try {
					Status = STATUS.PAUSED;
					semMoveControl.acquire();
					Status = STATUS.RUNNING;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
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
	
	public int Iterate() {
		semMoveControl.release();
		return 0;
	}
	
	public void Solve(T InitialState) {
		
		SearchStat = new Statistics();
		TerminateSearch = false;
		VisitedStates = new ArrayList<T>();
		
		Status = STATUS.RUNNING;
		
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
		
		Status = STATUS.FINISHED;
		
		SearchStat.msExecTime = (double)(System.nanoTime() - stime)/1000000;
		
		return;
	}
	
	public Statistics getStatistics() {
		return SearchStat;
	}
	
	public void setMode(int mode) {
		this.mode = mode;
	}
	
	public int getStatus() {
		return Status;
	}
	
	/*
	 * These functions need to be overrided accordingly 
	 */
	public ArrayList<T> Successor() {return null;};
	public void SetState(T state) {};
	public int getHeuristicCost (T state) {return 0;}
	public boolean IsGoalReached(T state) {return false;}
	public boolean IsLoopExist(T state, ArrayList<T> VisitedStates) {return false;}
}
