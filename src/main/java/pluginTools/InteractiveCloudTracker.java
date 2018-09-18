package pluginTools;

import javax.swing.JFrame;

import ij.ImageJ;
import ij.ImagePlus;
import ij.io.Opener;

public class InteractiveCloudTracker {

	
	public static void main(String[] args) {
		
		new ImageJ();
		JFrame frame = new JFrame("");
		
		ImagePlus impA = new Opener().openImage("/Volumes/TRANSCEND/DeconImagesJulia/NucleiSegmentation/NonOverlap/SmallLabelIgnoredObjectMedianC1-MAX_20180625-visit_12_decon_.tif");
		impA.show();
		
		ImagePlus impB = new Opener().openImage("/Volumes/TRANSCEND/DeconImagesJulia/CloudSegmentation/CloudC1-MAX_20180625-visit_12_decon_.tif");
		impB.show();
		
		ImagePlus impC = new Opener().openImage("/Volumes/TRANSCEND/DeconImagesJulia/CloudSegmentation/CloudC2-MAX_20180625-visit_12_decon_.tif");
		impC.show();
		
		ImagePlus impD = new Opener().openImage("/Users/aimachine/Documents/JuliaData/Julia/C1-MAX_20180625-visit_12_ch_1.tif");
		impD.show();
		
		ImagePlus impE = new Opener().openImage("/Users/aimachine/Documents/JuliaData/Julia/C2-MAX_20180625-visit_12_ch_2.tif");
		impE.show();
		
		
		CloudifyFileChooser panel = new CloudifyFileChooser();
		
		frame.getContentPane().add(panel, "Center");
		frame.setSize(panel.getPreferredSize());
		
		
		
	}
	
}
