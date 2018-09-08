package pluginTools;

import javax.swing.JFrame;

import ij.ImageJ;
import ij.ImagePlus;
import ij.io.Opener;

public class InteractiveCloudTracker {

	
	public static void main(String[] args) {
		
		new ImageJ();
		JFrame frame = new JFrame("");
		
		ImagePlus impA = new Opener().openImage("/Users/aimachine/Documents/StemCellJulia/IlastikTraining/TestImages/decon_for_segmentation/Clouds.tif");
		impA.show();
		
		ImagePlus impB = new Opener().openImage("/Users/aimachine/Documents/StemCellJulia/IlastikTraining/TestImages/decon_for_segmentation/SmallLabelIgnoredGood2Bad1.tif");
		impB.show();
		
		ImagePlus impC = new Opener().openImage("/Users/aimachine/Documents/StemCellJulia/IlastikTraining/TestImages/rawdata/C1-MAX_20180907-CL60-Dox0900-TT3h-TL5min_visit_5.tif");
		impC.show();
		
		ImagePlus impD = new Opener().openImage("/Users/aimachine/Documents/StemCellJulia/IlastikTraining/TestImages/rawdata/C2-MAX_20180907-CL60-Dox0900-TT3h-TL5min_visit_5.tif");
		impD.show();
		
		
		
		CloudifyFileChooser panel = new CloudifyFileChooser();
		
		frame.getContentPane().add(panel, "Center");
		frame.setSize(panel.getPreferredSize());
		
		
		
	}
	
}
