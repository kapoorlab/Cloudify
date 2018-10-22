package pluginTools;

import javax.swing.JFrame;

import ij.ImageJ;
import ij.ImagePlus;
import ij.io.Opener;

public class InteractiveCloudTracker {

	
	public static void main(String[] args) {
		
		new ImageJ();
		JFrame frame = new JFrame("");
		
		ImagePlus impA = new Opener().openImage("/Users/aimachine/Documents/JuliaData/CloudChoneSegmentation/CloudSUM_C1-20180629-TX1072-CL60-Dox0820_visit_1.tif");
		impA.show();
		
		ImagePlus impB = new Opener().openImage("/Users/aimachine/Documents/JuliaData/CloudChtwoSegmentation/CloudSUM_C2-20180629-TX1072-CL60-Dox0820_visit_1.tif");
		impB.show();
		
		ImagePlus impC = new Opener().openImage("/Users/aimachine/Documents/JuliaData/OriginalImageChtwo/SUM_C2-20180629-TX1072-CL60-Dox0820_visit_1.tif");
		impC.show();
		
		ImagePlus impD = new Opener().openImage("/Users/aimachine/Documents/JuliaData/OriginalImageChone/SUM_C1-20180629-TX1072-CL60-Dox0820_visit_1.tif");
		impD.show();
		
		ImagePlus impE = new Opener().openImage("/Users/aimachine/Documents/JuliaData/NucleiSegmentation/StarProbLabelsStemCell256RaysSUM_C1-20180629-TX1072-CL60-Dox0820_visit_1_cor.tif");
		impE.show();
		
		
		CloudifyFileChooser panel = new CloudifyFileChooser();
		
		frame.getContentPane().add(panel, "Center");
		frame.setSize(panel.getPreferredSize());
		
		
		
	}
	
}
