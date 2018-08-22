package cloudFinder;

import ij.gui.Roi;

public class RoiObject {
	
	public Roi roi;
	public double[] geometriccenter;
	public double averageintensity;
	public double totalintensity;
	public double averageintensityB;
	public double totalintensityB;
	public double numberofpixels;
	
	public RoiObject(final Roi roi, final double[] geometriccenter, final double averageintensity, final double totalintensity, 
			final double averageintensityB, final double totalintensityB,final double numberopfpixels) {
		
		this.roi = roi;
		this.geometriccenter = geometriccenter;
		this.averageintensity = averageintensity;
		this.totalintensity = totalintensity;
		this.averageintensityB = averageintensityB;
		this.totalintensityB = totalintensityB;
		this.numberofpixels = numberopfpixels;
		
	}

}
