package pluginTools;

import javax.swing.JFrame;

import ij.ImageJ;
import ij.ImagePlus;
import ij.io.Opener;

public class InteractiveCloudTracker {

	
	public static void main(String[] args) {
		
		new ImageJ();
		JFrame frame = new JFrame("");
		
		ImagePlus impA = new Opener().openImage("/Volumes/u934/service_imagerie/v_kapoor/HEARD/Feb2019/decon_reg_maxProj/Nuclei_Connected/InstanceSegBinary_res_MAX_20190115-CL60-TT4h-TL10min-Dox0920_visit_1_ch_2.tif");
		impA.show();
		
		ImagePlus impB = new Opener().openImage("/Volumes/u934/service_imagerie/v_kapoor/HEARD/Feb2019/decon_reg_maxProj/rawData_MaxProj/MAX_res_20190115-CL60-TT4h-TL10min-Dox0920_visit_1_ch_1.tif");
		impB.show();
		
		ImagePlus impC = new Opener().openImage("/Volumes/u934/service_imagerie/v_kapoor/HEARD/Feb2019/decon_reg_maxProj/rawData_MaxProj/MAX_res_20190115-CL60-TT4h-TL10min-Dox0920_visit_1_ch_2.tif");
		impC.show();
		
		ImagePlus impD = new Opener().openImage("/Volumes/u934/service_imagerie/v_kapoor/HEARD/Feb2019/decon_reg_maxProj/CloudSeg_NonNorm/Ch2-Class1/Cloudres_MAX_20190115-CL60-TT4h-TL10min-Dox0920_visit_1_ch_2_Probabilities.tif");
		impD.show();
		
		ImagePlus impE = new Opener().openImage("/Volumes/u934/service_imagerie/v_kapoor/HEARD/Feb2019/decon_reg_maxProj/CloudSeg_NonNorm/Ch1-Class1/Cloudres_MAX_20190115-CL60-TT4h-TL10min-Dox0920_visit_1_ch_1_Probabilities.tif");
		impE.show();
		
		
		CloudifyFileChooser panel = new CloudifyFileChooser();
		
		frame.getContentPane().add(panel, "Center");
		frame.setSize(panel.getPreferredSize());
		
		
		
	}
	
}
