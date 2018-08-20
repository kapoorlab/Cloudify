package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;

import pluginTools.InteractiveCloudify;


public class SaverDirectory implements ActionListener {
	
    InteractiveCloudify parent;
    
	public SaverDirectory(InteractiveCloudify parent) {

		this.parent = parent;

	}
	
	
	

	@Override
	public void actionPerformed(final ActionEvent arg0) {
		
		
		parent.chooserA = new JFileChooser();
		if(parent.saveFile == null)
		parent.chooserA.setCurrentDirectory(new java.io.File("."));
		else
			parent.chooserA.setCurrentDirectory(parent.saveFile);	
		
		
		parent.chooserA.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		//
		
		//
		if (parent.chooserA.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
			System.out.println("getCurrentDirectory(): " + parent.chooserA.getCurrentDirectory());
			System.out.println("getSelectedFile() : " + parent.chooserA.getSelectedFile());
			parent.saveFile = parent.chooserA.getSelectedFile();
		} else {
			System.out.println("No Selection ");
		}
		
		
	}


}
