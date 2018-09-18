package cloudFinder;

public class CloudTrackObject {

	
	public String trackID;
	public int thirdDimension;
	public double totalIntensityChA;
	public double totalIntensityChB;
	
	public double Nucleiarea;
	
	public double CloudIntensityChA;
	public double CloudIntensityChB;
	
	public double CloudareaA;
	public double CloudareaB;
	
	public CloudTrackObject(final String trackID, final int thirdDimension, final double totalIntensityChA, final double totalIntensityChB, final double Nucleiarea, final double CloudIntensityChA,
			final double CloudIntensityChB, final double  CloudareaA, final double CloudareaB) {
		
		
		
		this.trackID = trackID;
		this.thirdDimension = thirdDimension;
		this.totalIntensityChA = totalIntensityChA;
		this.totalIntensityChB = totalIntensityChB;
		this.Nucleiarea = Nucleiarea;
		this.CloudIntensityChA = CloudIntensityChA;
		this.CloudIntensityChB = CloudIntensityChB;
		this .CloudareaA = CloudareaA;
		this.CloudareaB = CloudareaB;
	}
	
}
