package puzzle;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.SystemColor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

import lib.GraphSearch;
import lib.GraphSearch.ALGO;
import lib.GraphSearch.MODE;
import lib.GraphSearch.STATUS;



public class Puzzle {

	private PuzzleTileClass PuzzleTiles[];
	private JPanel pnPuzzleTiles[];
	private PuzzleSolver solver;
	
	private int iPUzzleSize;
	private int iTileCnt;
	private int StepSize = 500;
	private boolean TerminatePuzzleSolver = false;
	private int SearchMode = MODE.CONTINUOUS;
	private int panelno = 0;
	private PuzzleState CurrState;
	private int TileMoveCnt = 0;
	public PuzzleState InitialState;
	ArrayList<PuzzleState> Solution;
	private boolean debug = false;
	private boolean SearchInProgress = false;
	private int IterationCount = 0;
	private Statistics SolStatistic; 
	
	public class Statistics {
		public int StateTransitionCnt;
		public int IterationCnt;
		public int TileMoveCnt;
		public int SolutionLength;
		public int MaxQueueLength;
		public double msExecTime;
	}
	
	public interface PuzzleListener {
		public void PuzzleSolved();
		public void PuzzleTerminated();
		public void PuzzlePaused();
	}
	
	List<PuzzleListener> listeners = new ArrayList<PuzzleListener>();
	
	public void Log(String str) {
		System.out.println(str);
	}

	public void addListener(PuzzleListener toAdd) {
		listeners.add(toAdd);
	}
	
	public void NotifyPuzzleSolved() {
		for (PuzzleListener listener : listeners)
			listener.PuzzleSolved();
	}
	
	public void NotifyPuzzleTerminated() {
		for (PuzzleListener listener : listeners)
			listener.PuzzleTerminated();
	}
	
	public void NotifyPuzzlePaused() {
		for (PuzzleListener listener : listeners)
			listener.PuzzlePaused();
	}
	
	public void setStepSize(int arg0) {
		StepSize = arg0;
	}
	
 	public class PuzzleState {
		public PuzzleState(ArrayList<Integer> list, PuzzleState prev) {
			this.list = list;
			this.prev = prev;
		}
		public PuzzleState(PuzzleState initState) {
			this.list = initState.list;
			this.prev = initState.prev;
		}
		private ArrayList<Integer> list;
		private PuzzleState prev;
		
		public boolean IsEqual(PuzzleState State) {
			for (int j = 0; j < this.list.size(); j++) {
				if(this.list.get(j) != State.list.get(j)) {
					return false;
				}
			}
			return true;	
		}
	}
	
	public class PuzzleSolver extends  GraphSearch<PuzzleState> {

		public int heuristic = HEURISTIC.MISPLACED;
		
		public PuzzleSolver(int algo, int mode) {
			super(algo, mode);
		}
		
		public class HEURISTIC {
			   public static final int MISPLACED = 0;
			   public static final int MANHATTAN = 1;
		}

		@Override
		public ArrayList<PuzzleState> Successor() {
	
			ArrayList<PuzzleState> succs = new ArrayList<PuzzleState>(); 
			
			if(debug) {
				System.out.println("--------------------------\n");
				for (int i = 0; i < CurrState.list.size(); i++) {
					System.out.printf("%2d ", CurrState.list.get(i));
				}
				System.out.printf("\n");
				System.out.println("--------------------------\n");
			}
			
			/* WEST */
			if (PuzzleTiles[iTileCnt-1].Loc.X != 0) {
				succs.add(new PuzzleState(MoveEmptyTile(PuzzleTiles[iTileCnt-1].Loc.X - 1, 
						PuzzleTiles[iTileCnt-1].Loc.Y).list, CurrState));
			}

			/* NORTH */
			if (PuzzleTiles[iTileCnt-1].Loc.Y != 0) {
				succs.add(new PuzzleState(MoveEmptyTile(PuzzleTiles[iTileCnt-1].Loc.X, 
						PuzzleTiles[iTileCnt-1].Loc.Y-1).list, CurrState));			
			}

			/* EAST */
			if (PuzzleTiles[iTileCnt-1].Loc.X != (iPUzzleSize-1)) {
				succs.add(new PuzzleState(MoveEmptyTile(PuzzleTiles[iTileCnt-1].Loc.X+1, 
						PuzzleTiles[iTileCnt-1].Loc.Y).list, CurrState));		
			}

			/* SOUTH */
			if (PuzzleTiles[iTileCnt-1].Loc.Y != (iPUzzleSize-1)) {
				succs.add(new PuzzleState(MoveEmptyTile(PuzzleTiles[iTileCnt-1].Loc.X, 
						PuzzleTiles[iTileCnt-1].Loc.Y+1).list, CurrState));
			}
			
			if(debug) {
				for (int i = 0; i < succs.size(); i++) {
					for (int j = 0; j < succs.get(i).list.size(); j++) {
						System.out.printf("%2d ", succs.get(i).list.get(j));
					}
					System.out.printf("\n");
				}
				System.out.println("--------------------------\n");
			}
			return succs;
		};
		
		@Override
		public boolean IsLoopExist(PuzzleState State, ArrayList<PuzzleState> VisitedStates) {
			PuzzleState item;
			int flag;
			
			for (int i = 0; i < VisitedStates.size(); i++) {
				item = VisitedStates.get(i);
				flag = 0;
				for (int j = 0; j < item.list.size(); j++) {
					if(item.list.get(j) != State.list.get(j)) {
						flag = 1;
						break;
					}
				}
				if(flag == 0) { // loop detected 
					if(debug) {
						System.out.println("loop detected ...");
					}
					return true;
				}	
			}
			
			return false;
		}
		
		@Override
		public void SetState(PuzzleState State) {
			if(State != null) {
				//UpdateState(State);
				MoveState(State);
			}
		};
		
		@Override
		public boolean IsGoalReached(PuzzleState State) {
			
			for (int i = 0; i < CurrState.list.size(); i++) {
				if (CurrState.list.get(i) != i) {
					return false;
				}
			}
			return true;
		}
		
		@Override
		public int getHeuristicCost (PuzzleState state) {

			int cost = 0;
			
			if (heuristic == HEURISTIC.MISPLACED) {
				// heuristic - 1: # of misplaced tiles
				for (int i = 0; i < state.list.size(); i++) {
					if(state.list.get(i) != (state.list.size()-1)) {	 /* ignore empty tile */
						if(state.list.get(i) != i) {
							cost++;
						}
					}
				}
				
				if(debug) {
					System.out.printf("heuristic 1 = %d\n", cost);
				}
			
			} else {
				cost = 0;
				// heuristic - 2: sum of Manhattan distances between tiles and corresponding actual positions
				for (int i = 0; i < state.list.size(); i++) {
					if(state.list.get(i) != (state.list.size()-1)) {	 /* ignore empty tile */
						cost += Math.abs(PuzzleTiles[state.list.get(i)].Loc.X - (PuzzleTiles[state.list.get(i)].Id % iPUzzleSize)) + 
								Math.abs(PuzzleTiles[state.list.get(i)].Loc.Y - (PuzzleTiles[state.list.get(i)].Id / iPUzzleSize));
					}
				}
				 
				if(debug) {
					System.out.printf("heuristic 2 = %d\n", cost);
				}
			}
			return cost;
		}
	
		public void setHeuristicCostFunc(int hcost) {
			heuristic = hcost;
		}
	}
	
	/*
	 * Return the puzzle state after empty tile moved to location (X,Y)
	 */
	private PuzzleState MoveEmptyTile(int X, int Y) {
		
		int tile = CurrState.list.get(Loc2Idx(X, Y));
		
		PuzzleState UpdatedState = new PuzzleState(new ArrayList<Integer>(CurrState.list), null);
		
		/* update puzzle state */
		UpdatedState.list.set(Loc2Idx(PuzzleTiles[tile].Loc.X, PuzzleTiles[tile].Loc.Y), PuzzleTiles[iTileCnt-1].Id);
		UpdatedState.list.set(Loc2Idx(PuzzleTiles[iTileCnt-1].Loc.X, PuzzleTiles[iTileCnt-1].Loc.Y), PuzzleTiles[tile].Id);	
		
		return UpdatedState;
	}
	
	public void UpdateState(PuzzleState State) {
		
		if(CurrState == State) {
			return;
		}

		IterationCount++;
		
		CurrState = State;
		
		/* Update tile locations */
		for (int i = 0; i < State.list.size(); i++) {
			PuzzleTiles[State.list.get(i)].Loc.X = i % iPUzzleSize;
			PuzzleTiles[State.list.get(i)].Loc.Y = i / iPUzzleSize;
		}
		
		
		if(SearchMode == MODE.CONTINUOUS) {
			if(StepSize > 0) {
				try {
					Thread.sleep(StepSize);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Repaint();
			}
		} else {
			try {
				Thread.sleep(StepSize);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Repaint();
		}
	}
	
	public void MoveState(PuzzleState State) {
		
		if(CurrState == State) {
			return;
		}
		PuzzleState s1, s2;
		
		IterationCount++;
		
		/* First,  find common root node */
		boolean found = false;
		s1 = State.prev;
		
		while(s1 != null) {
			s2 = CurrState;
			while(s2 != null) {
				if(s1 == s2) {
					found = true;
					break;
				}
				s2 = s2.prev;
			}
			if(found) {
				break;
			}
			s1 = s1.prev;
		}
		
		ArrayList<PuzzleState> moves = new ArrayList<PuzzleState>();
		int commnode;
		
		if(found) {
			s2 = CurrState;
			while(s2 != s1) {
				moves.add(s2);
				s2 = s2.prev;
			}
			moves.add(s2);
			
			commnode = moves.size();
			
			s1 = State;
			while(s1 != s2) {
				moves.add(commnode, s1);
				s1 = s1.prev;
			}
			
		} 
		
		if(found) {
			/* Now, move CurrState -> s1, move s1 -> FinalState */
			
			if((SearchMode == MODE.SINGLESTEP) && (moves.size() <= 1)) {
				CurrState = State;
				TileMoveCnt++;
				Repaint();
			} else {
			
				for (int i = 0; i < moves.size(); i++) {
					CurrState = moves.get(i);
					TileMoveCnt++;
					if(SearchMode == MODE.CONTINUOUS) {
						if(StepSize > 0) {
							try {
								Thread.sleep(StepSize);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							Repaint();
						}
					} else {
						try {
							Thread.sleep(250);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						Repaint();
					}
				}
			}
			
		} else {
			CurrState = State;
			TileMoveCnt++;
			if(SearchMode == MODE.CONTINUOUS) {
				if(StepSize > 0) {
					try {
						Thread.sleep(StepSize);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Repaint();
				}
			} else {
				Repaint();
			}
		}
		
		
		/* Update tile locations */
		for (int i = 0; i < State.list.size(); i++) {
			PuzzleTiles[State.list.get(i)].Loc.X = i % iPUzzleSize;
			PuzzleTiles[State.list.get(i)].Loc.Y = i / iPUzzleSize;
		}
		
	}
	
	public class PuzzleTileClass {
		public PuzzleTileClass(JLabel obj, int id, int locX, int locY) {
			Obj = obj;
			Id = id;
			Loc = new TileLoc();
			Loc.X = locX;
			Loc.Y = locY;
		}
		class TileLoc {
			int X;
			int Y;
		};
		int Id;
		JLabel Obj;
		TileLoc Loc;
	}
	
	public class DIR {
		   public static final int WEST = 0;
		   public static final int NORTH = 1;
		   public static final int EAST = 2;
		   public static final int SOUTH = 3;
	}
		
	private int Loc2Idx(int X, int Y) {
		return ((Y*iPUzzleSize)+X);
	}
	
	public Puzzle(JPanel pnPuzzle[], int iSize) {
		pnPuzzleTiles = pnPuzzle;
		iPUzzleSize = iSize;
	}
	
	public void Init (int iSize, ArrayList<Integer> initialLocs) {
		iPUzzleSize = iSize;
		iTileCnt = iPUzzleSize*iPUzzleSize;
		PuzzleTiles = new PuzzleTileClass[iTileCnt];
		
		pnPuzzleTiles[panelno].removeAll();
		pnPuzzleTiles[panelno].setLayout(new GridLayout(iPUzzleSize, iPUzzleSize, 2, 2));
		
		CurrState = new PuzzleState(new ArrayList<Integer>(), null);
		
		JLabel TileObjects[] = new JLabel[iTileCnt];
		for (int i = 0; i < iTileCnt; i++) {
			TileObjects[i] = new JLabel();
			TileObjects[i].setFont(new Font("Tahoma", Font.BOLD, 18));
			TileObjects[i].setHorizontalAlignment(SwingConstants.CENTER);
			TileObjects[i].setPreferredSize(new Dimension(20, 20));
			if(i != (iTileCnt-1)) {
				TileObjects[i].setBorder(new BevelBorder(BevelBorder.RAISED, Color.BLACK, null, null, null));
				TileObjects[i].setText(Integer.toString(i+1));
				TileObjects[i].setOpaque(true);
				TileObjects[i].setBackground(SystemColor.activeCaption);
			}
			
			pnPuzzleTiles[panelno].add(TileObjects[i]);
			PuzzleTiles[i] = new PuzzleTileClass(TileObjects[i], 	/* object graphically representing tile */ 
					i, 												/* Tile id from 0 to (PuzzleSize+1), (PuzzleSize+1) is empty tile */
					i%iPUzzleSize,									/* Tile location in X direction */ 
					i/iPUzzleSize); 								/* Tile location in Y direction */
		
			CurrState.list.add(i);
		}
		
		this.Repaint();
	}
	
	public void Shuffle (int nMove) {
		
		
		int sel;
		ArrayList<PuzzleState> succs = new ArrayList<PuzzleState>();
		ArrayList<PuzzleState> visitedStates = new ArrayList<PuzzleState>();
		
		PuzzleSolver sol = new PuzzleSolver(ALGO.BFS, ALGO.IDS);
		
		visitedStates.add(CurrState);
		for (int i = 0; i < nMove; i++) {
			succs = sol.Successor();
			do {
				sel = (int)(Math.random() * succs.size());
			} while (sol.IsLoopExist(succs.get(sel), visitedStates));
			visitedStates.add(succs.get(sel));
			UpdateState(succs.get(sel));
		}
		
		Repaint();	/* XXX: Just in case stepSize = 0.*/
	}
	
	public int Move(int dir) {
		
		int tile_locX, tile_locY;
		int tile;
		
		switch(dir) {
			case DIR.EAST:
				if (PuzzleTiles[iTileCnt-1].Loc.X == 0) {
					return -1;
				}
				tile_locX = PuzzleTiles[iTileCnt-1].Loc.X - 1; 
				tile_locY = PuzzleTiles[iTileCnt-1].Loc.Y;
				break;
			case DIR.SOUTH:
				if (PuzzleTiles[iTileCnt-1].Loc.Y == 0) {
					return -1;
				}
				tile_locX = PuzzleTiles[iTileCnt-1].Loc.X; 
				tile_locY = PuzzleTiles[iTileCnt-1].Loc.Y-1;
				break;
			case DIR.WEST:
				if (PuzzleTiles[iTileCnt-1].Loc.X == (iPUzzleSize-1)) {
					return -1;
				}
				tile_locX = PuzzleTiles[iTileCnt-1].Loc.X+1;
				tile_locY = PuzzleTiles[iTileCnt-1].Loc.Y;
				break;
			case DIR.NORTH:
				if (PuzzleTiles[iTileCnt-1].Loc.Y == (iPUzzleSize-1)) {
					return -1;
				}
				tile_locX = PuzzleTiles[iTileCnt-1].Loc.X;
				tile_locY = PuzzleTiles[iTileCnt-1].Loc.Y+1;
				break;
			default:
					return -1;
		}
		
		tile = CurrState.list.get(Loc2Idx(tile_locX, tile_locY));
		
		if(debug) {
			System.out.printf("%2d <-> %2d | dir=%s\n", tile, (iTileCnt-1), 
					(dir==DIR.WEST)?("W"):(
							(dir==DIR.EAST)?("E"):(
									(dir==DIR.SOUTH)?("S"):("N"))));
		}
		
		/* switch locations */
		PuzzleTiles[tile].Loc.X = PuzzleTiles[iTileCnt-1].Loc.X;
		PuzzleTiles[tile].Loc.Y = PuzzleTiles[iTileCnt-1].Loc.Y;
		
		PuzzleTiles[iTileCnt-1].Loc.X = tile_locX;
		PuzzleTiles[iTileCnt-1].Loc.Y = tile_locY;
		
		/* update puzzle state */
		CurrState.list.set(Loc2Idx(PuzzleTiles[tile].Loc.X, PuzzleTiles[tile].Loc.Y), PuzzleTiles[tile].Id);
		CurrState.list.set(Loc2Idx(PuzzleTiles[iTileCnt-1].Loc.X, PuzzleTiles[iTileCnt-1].Loc.Y), PuzzleTiles[iTileCnt-1].Id);
		
		/* repaint puzzle */
		Repaint();
		
		return 0;
	}
	
	public void Clear() {
		/*
		pnPuzzleTiles.removeAll();
		pnPuzzleTiles.invalidate();
		pnPuzzleTiles.repaint();
		*/
		PuzzleSolver solver = new PuzzleSolver(ALGO.ASTAR, MODE.CONTINUOUS);
		solver.getHeuristicCost (CurrState);
	}
	
	public void Repaint() {
		
		int oldpanelno = panelno;
		
		/* double buffering to prevent flickering effect */
		if(panelno == 0) {
			panelno = 1;
		} else {
			panelno = 0;
		}
		
		pnPuzzleTiles[panelno].removeAll();
		pnPuzzleTiles[panelno].setLayout(new GridLayout(iPUzzleSize, iPUzzleSize, 2, 2));
		
		for (int i = 0; i < CurrState.list.size(); i++) {
			pnPuzzleTiles[panelno].add(PuzzleTiles[CurrState.list.get(i)].Obj);
			pnPuzzleTiles[panelno].validate();
		}
		
		pnPuzzleTiles[panelno].repaint();
		pnPuzzleTiles[panelno].updateUI();
		
		pnPuzzleTiles[oldpanelno].setVisible(false);
		pnPuzzleTiles[panelno].setVisible(true);
	}
	
	public void Solve(int algo, int mode, int heuristic) {
		
		SolStatistic = new Statistics();
		
		SearchInProgress = true;
		IterationCount = 0;
		
		this.SearchMode = mode;
		TileMoveCnt = 0;
		
		/* Save initial state */
		InitialState = new PuzzleState(new ArrayList<Integer>(CurrState.list), null);
		
		/* Run searching algorithm */
		solver = new PuzzleSolver(algo, SearchMode);
		CurrState.prev = null;  
	
		if(algo == ALGO.ASTAR) {
			solver.setHeuristicCostFunc(heuristic);
		}
		
		Runnable r = new Runnable() {
			@Override
			public void run() {
				solver.Solve(CurrState);
			}
		};
		Thread t = new Thread(r);
		
		TerminatePuzzleSolver = false;
		t.start(); 
		while(t.isAlive()) {
			if(TerminatePuzzleSolver) {
				solver.Terminate();
			}
		}
		
		
		if(solver.IsGoalFound()) {
			
			NotifyPuzzleSolved();
			
			UpdateState(solver.getFinalState());
			Repaint();
			
			/* generate solution path */
			Solution = new ArrayList<PuzzleState>();
			PuzzleState temp = solver.getFinalState();
			do {
				Solution.add(temp);
				temp = temp.prev;
			} while (temp != null);
			Collections.reverse(Solution);
			
			Log(String.format("Puzzle solved... (" +
					"# of state traversed = %d | # of tiles moved = %d | Solution Length = %d | " +
					"Exec Time = %.2f ms)\n", solver.getStatistics().nStateTransitions,
					TileMoveCnt, Solution.size()-1, solver.getStatistics().msExecTime));
			
			// System.out.printf("# of state trans.   = %d\n", solver.getStatistics().nStateTransitions);
			// System.out.printf("# of detected loops = %d\n", solver.getStatistics().nDetectedLoops);			
			
			SolStatistic.StateTransitionCnt = solver.getStatistics().nStateTransitions;
			SolStatistic.SolutionLength = (Solution.size()-1);
			SolStatistic.TileMoveCnt = TileMoveCnt;
			SolStatistic.MaxQueueLength = solver.getStatistics().MaxQueueLength;
			SolStatistic.msExecTime = solver.getStatistics().msExecTime;
			
			System.out.printf(	"# of tiles moved = %d | " +
								"# of state trans.= %d | " +
								"Solution Length  = %d | " +
								"Max Queue Length = %d | " +
								"Exec Time = %.3f ms\n", 
								SolStatistic.TileMoveCnt,
								SolStatistic.StateTransitionCnt,
								SolStatistic.SolutionLength,
								SolStatistic.MaxQueueLength,
								SolStatistic.msExecTime);
			
			
		} else {
			
			NotifyPuzzleTerminated();
			
			Log(String.format("Puzzle NOT solved...\n"));
		}
		
		SearchInProgress = false;
	}
	
	public void ShowSolution() {
		
		StepSize = 500;
		
		if(Solution == null) {
			System.out.println("No solution!!!");
			return;
		}
		
		for (int i = 0; i < Solution.size(); i++) {
			UpdateState(Solution.get(i));
		}
		
	}

	public void Pause() {
		setRunMode(MODE.SINGLESTEP);
		
		while(solver.getStatus() == STATUS.RUNNING){};
		
		NotifyPuzzlePaused();
	}
	
	public void Terminate() {
		TerminatePuzzleSolver = true;
	}

	public void Iterate(int N) {
		int state;
		if (SearchMode == MODE.SINGLESTEP) {
			while(N-- > 0) {
				do{
					state = solver.getStatus();
					if(state == STATUS.FINISHED) {
						return;
					}
				} while(state != STATUS.PAUSED);
				
				solver.Iterate();
			};
		}
	}
	
	public ArrayList<Integer> getTileLocations() {
		return CurrState.list;
	}
	
	public int setTileLocations(ArrayList<Integer> locs) {
		
		PuzzleState State = new PuzzleState(locs, null);

		CurrState = State;
		
		/* Update tile locations */
		for (int i = 0; i < State.list.size(); i++) {
			PuzzleTiles[State.list.get(i)].Loc.X = i % iPUzzleSize;
			PuzzleTiles[State.list.get(i)].Loc.Y = i / iPUzzleSize;
		}
		Repaint();
		
		return 0;
	}

	public Statistics getStatistics() {
		return SolStatistic;
	}

	public void setRunMode(int mode) {
		SearchMode = mode;
		solver.setMode(mode);
	}
	
	public boolean IsSearchInProgress() {
		return SearchInProgress;
	}
	
	public int getIterationCount() {
		return IterationCount;
	}
}
