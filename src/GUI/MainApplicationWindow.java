package GUI;

import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;

import javax.swing.JFrame;

import puzzle.PuzzleGUI;




public class MainApplicationWindow {
	
	private JFrame frmPuzzleSolver;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainApplicationWindow window = new MainApplicationWindow();
					window.frmPuzzleSolver.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainApplicationWindow() {
		initialize();
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
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		frmPuzzleSolver = new JFrame();
		frmPuzzleSolver.setResizable(false);
		frmPuzzleSolver.setTitle("Puzzle Solver");
		frmPuzzleSolver.setBounds(100, 100, 638, 603);
		frmPuzzleSolver.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmPuzzleSolver.getContentPane().setLayout(null);
		frmPuzzleSolver.setVisible(true);
		
		new PuzzleGUI().Create(frmPuzzleSolver.getContentPane());
	}
}
