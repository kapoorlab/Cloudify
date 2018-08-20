package pluginTools;

import javax.swing.JFrame;

import ij.ImageJ;
import ij.ImagePlus;
import ij.io.Opener;

public class InteractiveCloudTracker {

	
	public static void main(String[] args) {
		
		new ImageJ();
		JFrame frame = new JFrame("");
		
		ImagePlus impB = new Opener().openImage("/Users/aimachine/Documents/CTrackTesting/IntegerCtrack.tif");
		impB.show();
		
		ImagePlus impD = new Opener().openImage("/Users/aimachine/Documents/CTrackTesting/CloudCTrack.tif");
		impD.show();
		
		
		
		CloudifyFileChooser panel = new CloudifyFileChooser();
		
		frame.getContentPane().add(panel, "Center");
		frame.setSize(panel.getPreferredSize());
		
		
		
	}
	
}
