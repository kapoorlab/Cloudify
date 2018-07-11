package fileListeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

import ij.WindowManager;
import pluginTools.CloudifyFileChooser;

public class ChooseOrigMap implements ActionListener {
	
	
	final CloudifyFileChooser parent;
	final JComboBox<String> choice;
	
	
	public ChooseOrigMap(final CloudifyFileChooser parent, final JComboBox<String> choice ) {
		
		
		this.parent = parent;
		this.choice = choice;
		
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		
		String imagename = (String) choice.getSelectedItem();
		
		
		
    	parent.impOrig = WindowManager.getImage(imagename);
    	
 
		
	}
	

}
