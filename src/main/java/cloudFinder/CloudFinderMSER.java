package cloudFinder;

import java.util.ArrayList;

import javax.swing.JProgressBar;

import ij.gui.Roi;
import mserGUI.CovistoMserPanel;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.componenttree.mser.MserTree;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.Pair;
import pluginTools.InteractiveCloudify;


public class CloudFinderMSER<T extends RealType<T> & NativeType<T>> implements CloudFinders<T> {

	public final InteractiveCloudify parent;
	public final RandomAccessibleInterval<T> source;
	public final JProgressBar jpb;
	private static final String BASE_ERROR_MSG = "[MSER-Finder]";
	protected String errorMessage;
	public final int thirdDimension;
	public final int fourthDimension;
	
	
	public CloudFinderMSER(final InteractiveCloudify parent, final RandomAccessibleInterval<T> source,
			final JProgressBar jpb, int thirdDimension, int fourthDimension) {

		this.parent = parent;
		this.source = source;
		this.thirdDimension = thirdDimension;
		this.fourthDimension = fourthDimension;
		this.jpb = jpb;

	}

	@Override
	public ArrayList<CloudObject> getResult() {
		
		return parent.CurrentCloudobject;
	}

	@Override
	public boolean checkInput() {
		if (source.numDimensions() > 4) {
			errorMessage = BASE_ERROR_MSG + " Can only operate on 4D, make slices of your stack . Got "
					+ source.numDimensions() + "D.";
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
		MserTree<T> newtree;
		// Compute the component tree
		if (CovistoMserPanel.darktobright)

			newtree = MserTree.buildMserTree(source, CovistoMserPanel.delta, CovistoMserPanel.minSize, CovistoMserPanel.maxSize,
					CovistoMserPanel.Unstability_Score, CovistoMserPanel.minDiversity, true);

		else

			newtree = MserTree.buildMserTree(source, CovistoMserPanel.delta, CovistoMserPanel.minSize, CovistoMserPanel.maxSize,
					CovistoMserPanel.Unstability_Score, CovistoMserPanel.minDiversity, false);

		parent.Rois = utility.FinderUtils.getcurrentRois(newtree);
		parent.CurrentCloudobject = new ArrayList<CloudObject>();
		for (Roi currentroi : parent.Rois) {

			final double[] geocenter = currentroi.getContourCentroid();
			final Pair<Double, Integer> Intensityandpixels = CloudObject.getIntensity(currentroi, source);
			final double intensity = Intensityandpixels.getA();
			final double numberofpixels = Intensityandpixels.getB();
			final double averageintensity = intensity / numberofpixels;
			CloudObject currentobject = new CloudObject(currentroi, geocenter, numberofpixels, intensity, averageintensity, thirdDimension, fourthDimension);
			parent.CurrentCloudobject.add(currentobject);
		}
		
		String uniqueID = Integer.toString(thirdDimension) + Integer.toString(fourthDimension);
		parent.ZTRois.put(uniqueID, parent.CurrentCloudobject);
		return true;
	}

}
