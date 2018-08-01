package cloudFinder;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JProgressBar;

import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import pluginTools.InteractiveCloudify;

public class CloudFinderDOG<T extends RealType<T> & NativeType<T>> implements CloudFinders<T> {

	public final InteractiveCloudify parent;
	public final JProgressBar jpb;
	public final int thirdDimension;
	public final int fourthDimension;
	private static final String BASE_ERROR_MSG = "[DOG-Finder]";
	protected String errorMessage;
	
	
	public CloudFinderDOG(final InteractiveCloudify parent, 
			final JProgressBar jpb, int thirdDimension, int fourthDimension) {

		this.parent = parent;
		this.thirdDimension = thirdDimension;
		this.fourthDimension = fourthDimension;
		this.jpb = jpb;
	}

	@Override
	public HashMap<String, ArrayList<CloudObject>> getResult() {
		
		return parent.AllClouds;
	}

	@Override
	public boolean checkInput() {
		if (parent.CurrentViewOrig.numDimensions() > 4) {
			errorMessage = BASE_ERROR_MSG + " Can only operate on 4D, make slices of your stack . Got "
					+ parent.CurrentViewOrig.numDimensions() + "D.";
			return false;
		}
		return true;
	}

	@Override
	public String getErrorMessage() {
		
		return errorMessage;
	}

	@Override
	public boolean process() {

		
		DOGSeg computeDOG = new DOGSeg(parent, jpb);
		try {
			computeDOG.execute();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}

}
