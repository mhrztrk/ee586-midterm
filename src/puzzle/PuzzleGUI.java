package puzzle;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import puzzle.Puzzle;
import puzzle.Puzzle.DIR;
import puzzle.Puzzle.PuzzleListener;
import puzzle.Puzzle.PuzzleSolver.HEURISTIC;

import lib.GraphSearch.ALGO;
import lib.GraphSearch.MODE;

public class PuzzleGUI {
	
	private final ButtonGroup grpAlgoSelection = new ButtonGroup();
	private JTextField tfMCRunCnt;
	private JTextField tfMCRandomMoves;
	private final ButtonGroup grpHeuristicSelection = new ButtonGroup();
	private JTextField tfNrSteps;
	int iPUzzleSize;
	
	public PuzzleGUI() {
	}
	
	public void enableComponents(Container container, boolean enable) {
		
		Component[] components = container.getComponents();
		for (Component component : components) {
			component.setEnabled(enable);
			if(component instanceof Container) {
				enableComponents((Container)component, enable);
			}
		}
		
	}
	
	/**
	 * @wbp.parser.entryPoint
	 */
	public void Create(Container parent) {
		
		final JPanel pnMain = new JPanel();
		pnMain.setBounds(2, 1, 638, 603);
		pnMain.setLayout(null);
		parent.add(pnMain);
		
		final JPanel pnAlgoSelection = new JPanel();
		pnAlgoSelection.setAlignmentY(Component.TOP_ALIGNMENT);
		pnAlgoSelection.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		pnAlgoSelection.setBounds(0, 30, 174, 518);
		pnMain.add(pnAlgoSelection);
		pnAlgoSelection.setLayout(null);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel_1.setBounds(10, 11, 154, 496);
		pnAlgoSelection.add(panel_1);
		panel_1.setLayout(null);

		JLabel lblNewLabel = new JLabel("Algorithms");
		lblNewLabel.setBounds(7, 7, 137, 15);
		panel_1.add(lblNewLabel);
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
		
		final JRadioButton rbSelectAlgoBFS = new JRadioButton("Breadth-First");
		rbSelectAlgoBFS.setBounds(7, 35, 137, 23);
		panel_1.add(rbSelectAlgoBFS);
		rbSelectAlgoBFS.setSelected(true);
		grpAlgoSelection.add(rbSelectAlgoBFS);
		rbSelectAlgoBFS.setFont(new Font("Tahoma", Font.PLAIN, 11));
		
		final JRadioButton rbSelectAlgoDFS = new JRadioButton("Depth First");
		rbSelectAlgoDFS.setBounds(7, 63, 137, 23);
		panel_1.add(rbSelectAlgoDFS);
		grpAlgoSelection.add(rbSelectAlgoDFS);
		rbSelectAlgoDFS.setFont(new Font("Tahoma", Font.PLAIN, 11));
		
		final JRadioButton rbSelectAlgoIDS = new JRadioButton("Iterative-deeping");
		rbSelectAlgoIDS.setBounds(7, 91, 137, 23);
		panel_1.add(rbSelectAlgoIDS);
		grpAlgoSelection.add(rbSelectAlgoIDS);
		rbSelectAlgoIDS.setFont(new Font("Tahoma", Font.PLAIN, 11));
		
		final JRadioButton rbSelectAlgoAstar = new JRadioButton("A* Search");
		rbSelectAlgoAstar.setBounds(7, 119, 127, 23);
		panel_1.add(rbSelectAlgoAstar);
		grpAlgoSelection.add(rbSelectAlgoAstar);
		rbSelectAlgoAstar.setFont(new Font("Tahoma", Font.PLAIN, 11));
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(7, 26, 137, 2);
		panel_1.add(separator_1);
		
		JLabel lblNewLabel_3 = new JLabel("Heuristic Function");
		lblNewLabel_3.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_3.setFont(new Font("Tahoma", Font.PLAIN, 10));
		lblNewLabel_3.setBounds(30, 150, 114, 14);
		panel_1.add(lblNewLabel_3);
		
		final JRadioButton rbHeuristicMisplaced = new JRadioButton("misplaced");
		rbHeuristicMisplaced.setSelected(true);
		rbHeuristicMisplaced.setFont(new Font("Tahoma", Font.PLAIN, 10));
		grpHeuristicSelection.add(rbHeuristicMisplaced);
		rbHeuristicMisplaced.setBounds(25, 174, 109, 23);
		panel_1.add(rbHeuristicMisplaced);
		
		JRadioButton rdbtnManhattan = new JRadioButton("manhattan");
		rdbtnManhattan.setFont(new Font("Tahoma", Font.PLAIN, 10));
		grpHeuristicSelection.add(rdbtnManhattan);
		rdbtnManhattan.setBounds(26, 200, 109, 23);
		panel_1.add(rdbtnManhattan);
		
		JSeparator separator_8 = new JSeparator();
		separator_8.setBounds(28, 170, 116, 2);
		panel_1.add(separator_8);
		
		JSeparator separator_9 = new JSeparator();
		separator_9.setBounds(5, 232, 137, 2);
		panel_1.add(separator_9);
		
		JLabel lblMonteCarlo = new JLabel("Monte Carlo\r\n");
		lblMonteCarlo.setHorizontalTextPosition(SwingConstants.CENTER);
		lblMonteCarlo.setHorizontalAlignment(SwingConstants.CENTER);
		lblMonteCarlo.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblMonteCarlo.setBounds(6, 251, 137, 15);
		panel_1.add(lblMonteCarlo);
		
		JLabel lblOfRuns = new JLabel("# of runs");
		lblOfRuns.setFont(new Font("Tahoma", Font.PLAIN, 10));
		lblOfRuns.setBounds(7, 277, 61, 14);
		panel_1.add(lblOfRuns);
		
		JLabel lblOfRandom = new JLabel("# of random moves");
		lblOfRandom.setFont(new Font("Tahoma", Font.PLAIN, 10));
		lblOfRandom.setBounds(7, 296, 95, 14);
		panel_1.add(lblOfRandom);
		
		tfMCRunCnt = new JTextField();
		tfMCRunCnt.setText("100");
		tfMCRunCnt.setBounds(108, 274, 36, 20);
		panel_1.add(tfMCRunCnt);
		tfMCRunCnt.setColumns(10);
		
		tfMCRandomMoves = new JTextField();
		tfMCRandomMoves.setText("5");
		tfMCRandomMoves.setBounds(108, 293, 36, 20);
		panel_1.add(tfMCRandomMoves);
		tfMCRandomMoves.setColumns(10);
		
		JLabel lblStateExp = new JLabel("Avg. # of state exp.:");
		lblStateExp.setFont(new Font("Tahoma", Font.PLAIN, 10));
		lblStateExp.setHorizontalTextPosition(SwingConstants.LEFT);
		lblStateExp.setHorizontalAlignment(SwingConstants.LEFT);
		lblStateExp.setBounds(7, 355, 109, 14);
		panel_1.add(lblStateExp);
		
		JLabel lblOfTile = new JLabel("Avg. # of tile moved:");
		lblOfTile.setFont(new Font("Tahoma", Font.PLAIN, 10));
		lblOfTile.setHorizontalTextPosition(SwingConstants.LEFT);
		lblOfTile.setHorizontalAlignment(SwingConstants.LEFT);
		lblOfTile.setBounds(7, 380, 109, 14);
		panel_1.add(lblOfTile);
		
		JLabel label = new JLabel("0");
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		label.setBounds(98, 355, 46, 14);
		panel_1.add(label);
		
		JLabel label_1 = new JLabel("0");
		label_1.setHorizontalAlignment(SwingConstants.RIGHT);
		label_1.setBounds(98, 380, 46, 14);
		panel_1.add(label_1);
		
		JPanel pnPuzzleArea = new JPanel();
		pnPuzzleArea.setBounds(new Rectangle(5, 0, 0, 0));
		pnPuzzleArea.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		pnPuzzleArea.setBounds(174, 30, 458, 325);
		pnMain.add(pnPuzzleArea);
		pnPuzzleArea.setLayout(null);

		
		final JPanel pnPuzzleTiles[] = new JPanel[2];
		pnPuzzleTiles[0] = new JPanel();
		pnPuzzleTiles[0].setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		pnPuzzleTiles[0].setBounds(10, 11, 300, 300);
		pnPuzzleTiles[1] = new JPanel();
		pnPuzzleTiles[1].setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		pnPuzzleTiles[1].setBounds(10, 11, 300, 300);
		pnPuzzleArea.add(pnPuzzleTiles[0]);
		pnPuzzleArea.add(pnPuzzleTiles[1]);
		
		final JPanel pnPuzzleControl = new JPanel();
		pnPuzzleControl.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		pnPuzzleControl.setBounds(320, 11, 128, 300);
		pnPuzzleArea.add(pnPuzzleControl);
		pnPuzzleControl.setLayout(null);
		
		JLabel lblPuzzleSize = new JLabel("Puzzle Size");
		lblPuzzleSize.setBounds(13, 37, 52, 24);
		pnPuzzleControl.add(lblPuzzleSize);
		lblPuzzleSize.setFont(new Font("Tahoma", Font.PLAIN, 11));
		
		JPanel pnStatusBar = new JPanel();
		FlowLayout fl_pnStatusBar = (FlowLayout) pnStatusBar.getLayout();
		fl_pnStatusBar.setAlignment(FlowLayout.LEFT);
		pnStatusBar.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		pnStatusBar.setBounds(0, 548, 632, 27);
		pnMain.add(pnStatusBar);
		
		final JLabel lblStatusBar = new JLabel("");
		lblStatusBar.setForeground(Color.BLACK);
		lblStatusBar.setFont(new Font("Times New Roman", Font.PLAIN, 12));
		lblStatusBar.setHorizontalAlignment(SwingConstants.LEFT);
		pnStatusBar.add(lblStatusBar);
		
		final JComboBox cboxPuzzleSizeSelection = new JComboBox();
		cboxPuzzleSizeSelection.setBounds(75, 39, 43, 20);
		pnPuzzleControl.add(cboxPuzzleSizeSelection);
		cboxPuzzleSizeSelection.setFont(new Font("Tahoma", Font.PLAIN, 11));
		cboxPuzzleSizeSelection.setModel(new DefaultComboBoxModel(new String[] {"3", "8", "15", "24", "35", "48", "63"}));
		cboxPuzzleSizeSelection.setSelectedIndex(1);
		
		final Puzzle puzzle = new Puzzle(pnPuzzleTiles, iPUzzleSize){
			@Override
			public void Log(String str) {
				lblStatusBar.setText(str);
			}
		};
	
		iPUzzleSize = (int)Math.sqrt((double)(Integer.parseInt(cboxPuzzleSizeSelection.getSelectedItem().toString()) + 1));
		puzzle.Init(iPUzzleSize, null);
		
		JButton btInitPuzzle = new JButton("Initialize");
		btInitPuzzle.setFont(new Font("Tahoma", Font.PLAIN, 10));
		btInitPuzzle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				iPUzzleSize = (int)Math.sqrt((double)(Integer.parseInt(cboxPuzzleSizeSelection.getSelectedItem().toString()) + 1));
				puzzle.Init(iPUzzleSize, null);
			}
		});
		btInitPuzzle.setBounds(13, 68, 105, 24);
		pnPuzzleControl.add(btInitPuzzle);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(13, 27, 105, 2);
		pnPuzzleControl.add(separator);
		
		final JLabel lblControlPanel = new JLabel("Puzzle Control");
		lblControlPanel.setHorizontalAlignment(SwingConstants.CENTER);
		lblControlPanel.setBounds(13, 12, 105, 14);
		pnPuzzleControl.add(lblControlPanel);
		
		JButton btnShufflePuzzle = new JButton("Shuffle");
		btnShufflePuzzle.setFont(new Font("Tahoma", Font.PLAIN, 10));
		btnShufflePuzzle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Runnable r = new Runnable() {
					@Override
					public void run() {
						puzzle.Shuffle(6);
					}
				};
				new Thread(r).start();
			}
		});
		btnShufflePuzzle.setBounds(13, 102, 105, 24);
		pnPuzzleControl.add(btnShufflePuzzle);
		
		final JPanel pnMoveTileCtrl = new JPanel();
		pnMoveTileCtrl.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		pnMoveTileCtrl.setBounds(13, 204, 105, 85);
		pnPuzzleControl.add(pnMoveTileCtrl);
		pnMoveTileCtrl.setLayout(new GridLayout(0, 3, 0, 0));
		
		JButton button = new JButton("");
		button.setVisible(false);
		button.setBorder(new EmptyBorder(0, 0, 0, 0));
		button.setEnabled(false);
		button.setPreferredSize(new Dimension(32, 32));
		button.setMargin(new Insets(0, 0, 0, 0));
		pnMoveTileCtrl.add(button);
		
		JButton btnMoveNorth = new JButton("^");
		pnMoveTileCtrl.add(btnMoveNorth);
		btnMoveNorth.setMargin(new Insets(0, 0, 0, 0));
		btnMoveNorth.setPreferredSize(new Dimension(40, 40));
		btnMoveNorth.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				puzzle.Move(DIR.NORTH);
			}
		});
		btnMoveNorth.setFont(new Font("Tahoma", Font.BOLD, 11));
		
		JButton button_1 = new JButton("");
		button_1.setVisible(false);
		button_1.setBorder(new EmptyBorder(0, 0, 0, 0));
		button_1.setEnabled(false);
		button_1.setPreferredSize(new Dimension(32, 32));
		button_1.setMargin(new Insets(0, 0, 0, 0));
		pnMoveTileCtrl.add(button_1);
		
		JButton btnMoveWest = new JButton("<");
		btnMoveWest.setFont(new Font("Tahoma", Font.BOLD, 11));
		pnMoveTileCtrl.add(btnMoveWest);
		btnMoveWest.setPreferredSize(new Dimension(40, 40));
		btnMoveWest.setMargin(new Insets(0, 0, 0, 0));
		btnMoveWest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				puzzle.Move(DIR.WEST);
			}
		});
		
		JButton button_2 = new JButton("");
		button_2.setBackground(Color.LIGHT_GRAY);
		button_2.setEnabled(false);
		button_2.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		button_2.setPreferredSize(new Dimension(32, 32));
		button_2.setMargin(new Insets(0, 0, 0, 0));
		pnMoveTileCtrl.add(button_2);
		
		JButton btnMoveEast = new JButton(">");
		btnMoveEast.setPreferredSize(new Dimension(40, 40));
		btnMoveEast.setFont(new Font("Tahoma", Font.BOLD, 11));
		pnMoveTileCtrl.add(btnMoveEast);
		btnMoveEast.setMargin(new Insets(0, 0, 0, 0));
		btnMoveEast.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				puzzle.Move(DIR.EAST);
			}
		});
		
		JButton button_3 = new JButton("");
		button_3.setVisible(false);
		button_3.setBorder(new EmptyBorder(0, 0, 0, 0));
		button_3.setEnabled(false);
		button_3.setPreferredSize(new Dimension(32, 32));
		button_3.setMargin(new Insets(0, 0, 0, 0));
		pnMoveTileCtrl.add(button_3);
		
		JButton btnMoveSouth = new JButton("v");
		btnMoveSouth.setPreferredSize(new Dimension(40, 40));
		btnMoveSouth.setFont(new Font("Tahoma", Font.PLAIN, 11));
		pnMoveTileCtrl.add(btnMoveSouth);
		btnMoveSouth.setMargin(new Insets(0, 0, 0, 0));
		btnMoveSouth.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				puzzle.Move(DIR.SOUTH);
			}
		});
		
		JButton button_4 = new JButton("");
		button_4.setVisible(false);
		button_4.setBorder(new EmptyBorder(0, 0, 0, 0));
		button_4.setEnabled(false);
		button_4.setPreferredSize(new Dimension(32, 32));
		button_4.setMargin(new Insets(0, 0, 0, 0));
		pnMoveTileCtrl.add(button_4);
		
		JLabel lblNewLabel_1 = new JLabel("Move Tiles");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setBounds(13, 184, 105, 14);
		pnPuzzleControl.add(lblNewLabel_1);
		
		JSeparator separator_2 = new JSeparator();
		separator_2.setBounds(13, 201, 105, 2);
		pnPuzzleControl.add(separator_2);
		
		final JPanel pnPuzzleSolve = new JPanel();
		pnPuzzleSolve.setFont(new Font("Tahoma", Font.PLAIN, 11));
		pnPuzzleSolve.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		pnPuzzleSolve.setBounds(174, 356, 458, 192);
		pnMain.add(pnPuzzleSolve);
		pnPuzzleSolve.setLayout(null);
		
		final JPanel pnSingleStepCtrl = new JPanel();
		pnSingleStepCtrl.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		pnSingleStepCtrl.setBounds(236, 9, 212, 115);
		pnPuzzleSolve.add(pnSingleStepCtrl);
		pnSingleStepCtrl.setLayout(null);
		
		JButton btnNextState = new JButton("Iterate N Steps");
		btnNextState.setFont(new Font("Tahoma", Font.PLAIN, 10));
		btnNextState.setBounds(12, 47, 130, 24);
		pnSingleStepCtrl.add(btnNextState);
		btnNextState.setMargin(new Insets(2, 5, 2, 5));
		
		JLabel lblStepMode = new JLabel("Single Step Mode");
		lblStepMode.setHorizontalAlignment(SwingConstants.CENTER);
		lblStepMode.setBounds(12, 11, 190, 14);
		pnSingleStepCtrl.add(lblStepMode);
		
		JSeparator separator_6 = new JSeparator();
		separator_6.setBounds(12, 34, 190, 2);
		pnSingleStepCtrl.add(separator_6);
		
		tfNrSteps = new JTextField();
		tfNrSteps.setText("1");
		tfNrSteps.setBounds(170, 49, 32, 20);
		pnSingleStepCtrl.add(tfNrSteps);
		tfNrSteps.setColumns(10);
		
		JLabel lblNewLabel_5 = new JLabel("N:");
		lblNewLabel_5.setBounds(152, 52, 17, 14);
		pnSingleStepCtrl.add(lblNewLabel_5);
		
		JLabel lblIteration = new JLabel("Iteration:");
		lblIteration.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblIteration.setBounds(12, 90, 51, 14);
		pnSingleStepCtrl.add(lblIteration);
		
		final JLabel lblIterationCount = new JLabel("0");
		lblIterationCount.setBounds(68, 91, 134, 14);
		pnSingleStepCtrl.add(lblIterationCount);
		
		JPanel pnContinuousCtrl = new JPanel();
		pnContinuousCtrl.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		pnContinuousCtrl.setBounds(10, 9, 216, 115);
		pnPuzzleSolve.add(pnContinuousCtrl);
		pnContinuousCtrl.setLayout(null);
		
		JButton btStopSolving = new JButton("Stop");
		btStopSolving.setFont(new Font("Tahoma", Font.PLAIN, 10));
		btStopSolving.setMargin(new Insets(2, 0, 2, 0));
		btStopSolving.setBounds(61, 82, 48, 24);
		pnContinuousCtrl.add(btStopSolving);
		
		final JButton btnSolve = new JButton("Solve");
		btnSolve.setFont(new Font("Tahoma", Font.PLAIN, 10));
		btnSolve.setBounds(10, 47, 190, 24);
		pnContinuousCtrl.add(btnSolve);
		btnSolve.setPreferredSize(new Dimension(56, 23));
		
		final JButton btnShowsolution = new JButton("Show Solution");
		btnShowsolution.setBounds(119, 82, 81, 24);
		pnContinuousCtrl.add(btnShowsolution);
		btnShowsolution.setMargin(new Insets(2, 5, 2, 5));
		btnShowsolution.setFont(new Font("Tahoma", Font.PLAIN, 10));
		
		JSeparator separator_5 = new JSeparator();
		separator_5.setBounds(10, 34, 190, 2);
		pnContinuousCtrl.add(separator_5);
		
		JLabel lblNewLabel_4 = new JLabel("Continuous Mode");
		lblNewLabel_4.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_4.setBounds(10, 11, 190, 14);
		pnContinuousCtrl.add(lblNewLabel_4);
		
		JButton btPauseSolving = new JButton("Pause");
		btPauseSolving.setFont(new Font("Tahoma", Font.PLAIN, 10));
		btPauseSolving.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				puzzle.Pause();
			}
		});
		btPauseSolving.setMargin(new Insets(2, 0, 2, 0));
		btPauseSolving.setBounds(10, 82, 48, 24);
		pnContinuousCtrl.add(btPauseSolving);
		
		JSeparator separator_10 = new JSeparator();
		separator_10.setOrientation(SwingConstants.VERTICAL);
		separator_10.setBounds(114, 84, 2, 20);
		pnContinuousCtrl.add(separator_10);
		
		JPanel panel_4 = new JPanel();
		panel_4.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel_4.setBounds(10, 135, 438, 46);
		pnPuzzleSolve.add(panel_4);
		panel_4.setLayout(null);
		
		
		final StringBuilder MCResultsBuffer = new StringBuilder();
		
		JButton btMCRun = new JButton("Run");
		btMCRun.setMinimumSize(new Dimension(24, 23));
		btMCRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int RunCnt = Integer.parseInt(tfMCRunCnt.getText());
				final int RandomMoves = Integer.parseInt(tfMCRandomMoves.getText());
				final int algo;
				final int hcost; 
				ArrayList<Integer> nSolMove = new ArrayList<Integer>();
				
				MCResultsBuffer.setLength(0);
				
				/* print header */
				MCResultsBuffer.append("Run    TileMoveCnt  StateTransCnt    SolutionLen    MaxQueueLen   ExecTime(ms)\n");
				MCResultsBuffer.append("------------------------------------------------------------------------------\n");
				
				if (rbSelectAlgoBFS.isSelected()) {
					algo = ALGO.BFS;
				} else if (rbSelectAlgoDFS.isSelected()) {
					algo = ALGO.DFS;
				} else if (rbSelectAlgoIDS.isSelected()) {
					algo = ALGO.IDS;
				} else if (rbSelectAlgoAstar.isSelected()){
					algo = ALGO.ASTAR;
				} else {
					return;
				}
				
				if(rbHeuristicMisplaced.isSelected()) {
					hcost = HEURISTIC.MISPLACED;
				} else {
					hcost = HEURISTIC.MANHATTAN;
				}
				
				for (int i = 0; i < RunCnt; i++) {
					Runnable r = new Runnable() {
						@Override
						public void run() {
							iPUzzleSize = (int)Math.sqrt((double)(Integer.parseInt(cboxPuzzleSizeSelection.getSelectedItem().toString()) + 1));
							puzzle.Init(iPUzzleSize, null);
							puzzle.Shuffle(RandomMoves);
							puzzle.setStepSize(0);	
							puzzle.Solve(algo, MODE.CONTINUOUS, hcost);
						}
					};
					Thread t = new Thread(r);
					t.start(); 
					while(t.isAlive()) {}
					
					MCResultsBuffer.append(String.format("%3d %14d %14d %14d %14d %14.3f\n", i, 
							puzzle.getStatistics().TileMoveCnt,
							puzzle.getStatistics().StateTransitionCnt,
							puzzle.getStatistics().SolutionLength,
							puzzle.getStatistics().MaxQueueLength,
							puzzle.getStatistics().msExecTime));
					
					nSolMove.add(puzzle.getStatistics().TileMoveCnt);
					System.out.printf("Run %3d> # of moves = %3d\n", i+1, puzzle.getStatistics().TileMoveCnt);
				}
				
			}
		});
		btMCRun.setBounds(7, 321, 64, 23);
		panel_1.add(btMCRun);
		
		JSeparator separator_11 = new JSeparator();
		separator_11.setBounds(5, 235, 137, 2);
		panel_1.add(separator_11);
		
		JSeparator separator_12 = new JSeparator();
		separator_12.setBounds(7, 268, 137, 2);
		panel_1.add(separator_12);
		
		JButton btMCStop = new JButton("Stop");
		btMCStop.setMinimumSize(new Dimension(24, 23));
		btMCStop.setBounds(80, 321, 64, 23);
		panel_1.add(btMCStop);
		
		JButton btnMCSaveResults = new JButton("Save Results");
		btnMCSaveResults.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				JFileChooser saveFile = new JFileChooser();
				int retval = saveFile.showSaveDialog(null);
				
				if(retval == JFileChooser.APPROVE_OPTION) {
				
					try {
						FileWriter file = new FileWriter(saveFile.getSelectedFile());
						file.write(MCResultsBuffer.toString());
						file.close();
						
					} catch (IOException e) {
						e.printStackTrace();
					} 
				}
			}
		});
		btnMCSaveResults.setBounds(7, 405, 137, 23);
		panel_1.add(btnMCSaveResults);
		
		final JSlider slSpeedCtrl = new JSlider();
		slSpeedCtrl.setValue(3);
		slSpeedCtrl.setMaximum(6);
		slSpeedCtrl.setPaintTicks(true);
		slSpeedCtrl.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				
				int val = ((JSlider)arg0.getSource()).getValue();
				int mid_val = (((JSlider)arg0.getSource()).getMaximum() - ((JSlider)arg0.getSource()).getMaximum())/2;
				int def_stepsize = 1000;	/* 1 second */
				
				if(val > mid_val) {
					puzzle.setStepSize(def_stepsize/(val - mid_val + 1));
				} else {
					puzzle.setStepSize(def_stepsize*(mid_val - val + 1));
				}
			}
		});
		slSpeedCtrl.setSnapToTicks(true);
		slSpeedCtrl.setMinorTickSpacing(1);
		slSpeedCtrl.setBounds(188, 7, 240, 31);
		panel_4.add(slSpeedCtrl);
		
		final JCheckBox cbShowMoves = new JCheckBox("Show Moves");
		cbShowMoves.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				
				if(cbShowMoves.isSelected()) {
					int val = slSpeedCtrl.getValue();
					int mid_val = (slSpeedCtrl.getMaximum() - slSpeedCtrl.getMaximum())/2;
					int def_stepsize = 1000;	/* 1 second */
					
					if(val > mid_val) {
						puzzle.setStepSize(def_stepsize/(val - mid_val + 1));
					} else {
						puzzle.setStepSize(def_stepsize*(mid_val - val + 1));
					}
				} else {
					puzzle.setStepSize(0);
				}
			}
		});
		cbShowMoves.setSelected(true);
		cbShowMoves.setBounds(6, 11, 104, 23);
		panel_4.add(cbShowMoves);
		
		
		JLabel lblNewLabel_2 = new JLabel("Speed");
		lblNewLabel_2.setBounds(146, 15, 46, 14);
		panel_4.add(lblNewLabel_2);
		
		JSeparator separator_7 = new JSeparator();
		separator_7.setOrientation(SwingConstants.VERTICAL);
		separator_7.setBounds(116, 11, 2, 20);
		panel_4.add(separator_7);
		btnShowsolution.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Runnable r = new Runnable() {
					@Override
					public void run() {
						puzzle.ShowSolution();
					}
				};
				new Thread(r).start();
			}
		});
		btnSolve.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				/* Initial GUI State */
				enableComponents(pnAlgoSelection, false);
				enableComponents(pnMoveTileCtrl, false);
				enableComponents(pnPuzzleControl, false);
				enableComponents(pnSingleStepCtrl, false);
				btnSolve.setEnabled(false);
				btnShowsolution.setEnabled(false);
				
				
				puzzle.addListener(new PuzzleListener() {

					@Override
					public void PuzzleSolved() {
						lblIterationCount.setText(String.format("%d", puzzle.getIterationCount()));
						enableComponents(pnAlgoSelection, true);
						enableComponents(pnMoveTileCtrl, true);
						enableComponents(pnPuzzleControl, true);
						enableComponents(pnSingleStepCtrl, true);
						btnSolve.setEnabled(true);
						btnShowsolution.setEnabled(true);
					}

					@Override
					public void PuzzleTerminated() {
						enableComponents(pnAlgoSelection, true);
						enableComponents(pnMoveTileCtrl, true);
						enableComponents(pnPuzzleControl, true);
						enableComponents(pnSingleStepCtrl, true);
						btnSolve.setEnabled(true);
					}

					@Override
					public void PuzzlePaused() {
						enableComponents(pnAlgoSelection, true);
						enableComponents(pnMoveTileCtrl, true);
						enableComponents(pnPuzzleControl, true);
						enableComponents(pnSingleStepCtrl, true);
						btnSolve.setEnabled(true);
					}
					
				});
				
				Runnable r = new Runnable() {
					@Override
					public void run() {
						
						int hcost;
						
						if(rbHeuristicMisplaced.isSelected()) {
							hcost = HEURISTIC.MISPLACED;
						} else {
							hcost = HEURISTIC.MANHATTAN;
						}
						
						if (cbShowMoves.isSelected()) {
							int val = slSpeedCtrl.getValue();
							int mid_val = (slSpeedCtrl.getMaximum() - slSpeedCtrl.getMaximum())/2;
							int def_stepsize = 1000;	/* 1 second */
							
							if(val > mid_val) {
								puzzle.setStepSize(def_stepsize/(val - mid_val + 1));
							} else {
								puzzle.setStepSize(def_stepsize*(mid_val - val + 1));
							}
						} else {
							puzzle.setStepSize(0);
						}
						
						if (rbSelectAlgoBFS.isSelected()) {
							puzzle.Solve(ALGO.BFS, MODE.CONTINUOUS, hcost);
						} else if (rbSelectAlgoDFS.isSelected()) {
							puzzle.Solve(ALGO.DFS, MODE.CONTINUOUS, hcost);
						} else if (rbSelectAlgoIDS.isSelected()) {
							puzzle.Solve(ALGO.IDS, MODE.CONTINUOUS, hcost);
						} else if (rbSelectAlgoAstar.isSelected()){
							puzzle.Solve(ALGO.ASTAR, MODE.CONTINUOUS, hcost);
						}
					}
				};
				new Thread(r).start();
				
			}
		});
		btStopSolving.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				puzzle.Terminate();
			}
		});
		btnNextState.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				int nstep = Integer.parseInt(tfNrSteps.getText());
				
				if(puzzle.IsSearchInProgress()) {
					puzzle.Iterate(nstep);
				} else {
					Runnable r = new Runnable() {
						@Override
						public void run() {
							
							int hcost;
							
							if(rbHeuristicMisplaced.isSelected()) {
								hcost = HEURISTIC.MISPLACED;
							} else {
								hcost = HEURISTIC.MANHATTAN;
							}
							
							if (rbSelectAlgoBFS.isSelected()) {
								puzzle.Solve(ALGO.BFS, MODE.SINGLESTEP, hcost);
							} else if (rbSelectAlgoDFS.isSelected()) {
								puzzle.Solve(ALGO.DFS, MODE.SINGLESTEP, hcost);
							} else if (rbSelectAlgoIDS.isSelected()) {
								puzzle.Solve(ALGO.IDS, MODE.SINGLESTEP, hcost);
							} else if (rbSelectAlgoAstar.isSelected()){
								puzzle.Solve(ALGO.ASTAR, MODE.SINGLESTEP, hcost);
							}
						}
					};
					new Thread(r).start();
					
					try {
						/* FIXME: A small delay to be sure solver is running */
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					puzzle.Iterate(nstep - 1);
				}
			}
		});
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setMargin(new Insets(0, 24, 0, 24));
		menuBar.setBounds(0, 0, 632, 31);
		pnMain.add(menuBar);
		
		Component horizontalStrut = Box.createHorizontalStrut(4);
		menuBar.add(horizontalStrut);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmLoadState = new JMenuItem("Load State");
		mntmLoadState.addMouseListener(new MouseAdapter() {
			private BufferedReader br;

			@Override
			public void mousePressed(MouseEvent arg0) {
				JFileChooser openFile = new JFileChooser();
				int retval = openFile.showOpenDialog(null);
				
				if(retval == JFileChooser.APPROVE_OPTION) {
				
					ArrayList<Integer> puzzleLoc = new ArrayList<Integer>();
					
					try {
						FileReader file = new FileReader(openFile.getSelectedFile());
						br = new BufferedReader(file);
						String temp = br.readLine();
						if(temp != null) {
							String[] spaceSeperatedArray = temp.split(" ");
							for (String item : spaceSeperatedArray) {
								if(item.trim().length() > 0) {
									puzzleLoc.add(Integer.parseInt(item));
								}
								
							}
						}
						
						puzzle.Init((int)Math.sqrt((double)puzzleLoc.size()), null);
						puzzle.setTileLocations(puzzleLoc);
						
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					
				}
			}
		});
		mnFile.add(mntmLoadState);
		
		JMenuItem mntmSaveState = new JMenuItem("Save State");
		mntmSaveState.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				JFileChooser saveFile = new JFileChooser();
				int retval = saveFile.showSaveDialog(null);
				ArrayList<Integer> tileLocs = puzzle.getTileLocations();
				
				if(retval == JFileChooser.APPROVE_OPTION) {
				
					try {
						FileWriter file = new FileWriter(saveFile.getSelectedFile());
						for (int i = 0; i < tileLocs.size(); i++) {
							file.write(String.format("%d ", tileLocs.get(i)));
						}
						file.close();
						
					} catch (IOException e) {
						e.printStackTrace();
					} 
				}
			}
		});
		mnFile.add(mntmSaveState);
		
		JMenuItem mntmSaveSolution = new JMenuItem("Save Solution");
		mnFile.add(mntmSaveSolution);
		
		JSeparator separator_4 = new JSeparator();
		mnFile.add(separator_4);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.setMnemonic(KeyEvent.VK_E);
		mnFile.add(mntmExit);
		
		Component horizontalStrut_1 = Box.createHorizontalStrut(4);
		menuBar.add(horizontalStrut_1);
		
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		JMenuItem mntmManual = new JMenuItem("Manual");
		mnHelp.add(mntmManual);
		
		JSeparator separator_3 = new JSeparator();
		mnHelp.add(separator_3);
		
		JMenuItem mntmAbout = new JMenuItem("About");
		mnHelp.add(mntmAbout);
		
		/* Use a timer to update iteration count */
		Timer timer = new Timer();
		
		timer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				if(puzzle.IsSearchInProgress()) {
					lblIterationCount.setText(String.format("%d", puzzle.getIterationCount()));
				}
			}
		}, 0, 100); /* 100 ms*/		
	}
}
