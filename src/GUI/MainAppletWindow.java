package GUI;


import java.awt.Component;
import java.awt.Container;

import javax.swing.JApplet;
import javax.swing.SwingUtilities;

import puzzle.PuzzleGUI;


public class MainAppletWindow extends JApplet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int iPUzzleSize;
	
	
    public static void main(String[] args) {
        JApplet applet = new MainAppletWindow();
        applet.init();
        applet.start();
    }
    
	/**
	 * Create the applet.
	 */
	public MainAppletWindow() {
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
	public void init() {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    createGUI();
                }
            });
        }
        catch (Exception e) {
            System.err.println("createGUI didn't successfully complete: " + e);
        }	
	}
	
	
	/**
	 * 
	 */
	private void createGUI() {
		this.getContentPane().setLayout(null);
		this.setBounds(1, 1, 638, 603);
		
		new PuzzleGUI().Create(this.getContentPane());
		
		
	}
}
