package cloudFinder;

import java.util.ArrayList;

import javax.swing.JProgressBar;

import dogGUI.CovistoDogPanel;
import ij.gui.Roi;
import mpicbg.imglib.algorithm.fft.FourierTransform.PreProcessing;
import net.imglib2.FinalInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.dog.DogDetection;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.Pair;
import net.imglib2.view.Views;
import pluginTools.InteractiveCloudify;

public class CloudFinderDOG<T extends RealType<T> & NativeType<T>> implements CloudFinders<T> {

	public final InteractiveCloudify parent;
	public final RandomAccessibleInterval<T> source;
	public final JProgressBar jpb;
	public final int thirdDimension;
	public final int fourthDimension;
	public boolean apply3D;
	private static final String BASE_ERROR_MSG = "[DOG-Finder]";
	protected String errorMessage;
	
	
	public CloudFinderDOG(final InteractiveCloudify parent, final RandomAccessibleInterval<T> source,
			final JProgressBar jpb,  boolean apply3D, int thirdDimension, int fourthDimension) {

		this.parent = parent;
		this.source = source;
		this.thirdDimension = thirdDimension;
		this.fourthDimension = fourthDimension;
		this.jpb = jpb;
		this.apply3D = apply3D;
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

		
		final DogDetection.ExtremaType type;
		if (CovistoDogPanel.lookForMaxima)
			type = DogDetection.ExtremaType.MINIMA;
		else
			type = DogDetection.ExtremaType.MAXIMA;
		CovistoDogPanel.sigma2 = utility.ScrollbarUtils.computeSigma2(CovistoDogPanel.sigma, parent.sensitivity);
		final DogDetection<T> newdog = new DogDetection<T>(Views.extendBorder(source),
				parent.interval, new double[] { 1, 1 }, CovistoDogPanel.sigma, CovistoDogPanel.sigma2, type, CovistoDogPanel.threshold, true);
		parent.peaks = newdog.getSubpixelPeaks();
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
