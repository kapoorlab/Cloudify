package pluginTools;

import javax.swing.JFrame;

import ij.ImageJ;
import ij.ImagePlus;
import ij.io.Opener;

public class InteractiveCloudTracker {

	
	public static void main(String[] args) {
		
		new ImageJ();
		JFrame frame = new JFrame("");
		
		
		ImagePlus impB = new Opener().openImage("/Users/aimachine/Documents/JuliaData/Julia/res_MAX_20180629-TX1072-CL60-Dox0820_visit_1_ch_1.tif");
		impB.show();
		
		ImagePlus impD = new Opener().openImage("/Users/aimachine/Documents/JuliaData/Julia/res_MAX_20180629-TX1072-CL60-Dox0820_visit_10_ch_2.tif");
		impD.show();
		
		ImagePlus impC = new Opener().openImage("/Users/aimachine/Documents/JuliaData/Julia/res_MAX_20180629-TX1072-CL60-Dox0820_visit_10_ch_2.tif");
		impC.show();
		
		
		CloudifyFileChooser panel = new CloudifyFileChooser();
		
		frame.getContentPane().add(panel, "Center");
		frame.setSize(panel.getPreferredSize());
		
		
		
	}
	
}
