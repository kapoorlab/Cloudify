package cloudTracker;

import cloudFinder.CloudObject;
import costMatrix.CostFunction;
import utility.ThreeDRoiobject;

public class CloudTrackCostFunction  implements CostFunction< CloudObject, CloudObject >
{

	
// Alpha is the weightage given to distance and Beta is the weightage given to the ratio of pixels
	public final double beta;
	public final double alpha;
	
	

	
	public double getAlpha(){
		
		return alpha;
	}
	
  
	public double getBeta(){
		
		return beta;
	}

	public CloudTrackCostFunction (double alpha, double beta){
		
		this.alpha = alpha;
		this.beta = beta;
		
	}
	
	
@Override
public double linkingCost( final CloudObject source, final CloudObject target )
{
	return source.DistanceTo(target, alpha, beta);
}
	



}
