package cloudFinder;

import java.util.ArrayList;

import ij.gui.Roi;
import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.RealSum;
import net.imglib2.view.Views;
import pluginTools.InteractiveCloudify;
import timeGUI.CovistoTimeselectPanel;
import zGUI.CovistoZselectPanel;

public class MeasureProperties {

	
	final InteractiveCloudify parent;
	final int SegmentationLabel;
	final ArrayList<RoiObject> currentLabelObject;
	final RandomAccessibleInterval<FloatType> OrigImageA;
	final RandomAccessibleInterval<FloatType> OrigImageB;
	public MeasureProperties( final InteractiveCloudify parent, final RandomAccessibleInterval<FloatType> OrigImageA, final RandomAccessibleInterval<FloatType> OrigImageB, final ArrayList<RoiObject> currentLabelObject, final int SegmentationLabel) {
		
		
		this.parent = parent;
		this.currentLabelObject = currentLabelObject;
		this.SegmentationLabel = SegmentationLabel;
		this.OrigImageA = OrigImageA;
		this.OrigImageB = OrigImageB;
	}
	
	
	public ArrayList<CloudObject> GetCurrentCloud() {
		
		
		RandomAccessibleInterval<FloatType> outimg = new ArrayImgFactory<FloatType>().create(OrigImageA, new FloatType());
		ArrayList<CloudObject> Allclouds = new ArrayList<CloudObject>();
		Cursor<FloatType> iter = Views.iterable(OrigImageA).localizingCursor();
		
		RandomAccess<FloatType> ranac = outimg.randomAccess();
			
			while(iter.hasNext()) {
			
				iter.fwd();
				
				ranac.setPosition(iter);
				ranac.get().set(iter.get());
				int x = ranac.getIntPosition(0);
				int y = ranac.getIntPosition(1);
				
				
				for(Roi currentroi : parent.Rois) {
					
				if(currentroi.contains(x, y))
					ranac.get().setZero();
				
					
				
			}
			
			}
			
			
			RandomAccessibleInterval<FloatType> outimgB = new ArrayImgFactory<FloatType>().create(OrigImageB, new FloatType());
			Cursor<FloatType> iterB = Views.iterable(OrigImageB).localizingCursor();
			
			RandomAccess<FloatType> ranacB = outimgB.randomAccess();
				
				while(iterB.hasNext()) {
				
					iterB.fwd();
					
					ranacB.setPosition(iterB);
					ranacB.get().set(iterB.get());
					int x = ranacB.getIntPosition(0);
					int y = ranacB.getIntPosition(1);
					for(Roi currentroi : parent.Rois) {
						
					if(currentroi.contains(x, y))
						ranacB.get().setZero();
					
				}
				
				}
			
			
			double meanIntensity = computeAverage(Views.iterable(outimg));
			double totalIntensity = computeTotal(Views.iterable(outimg));
			double[] centroid = computeCentroid(Views.iterable(outimg));
			double NumPixels = computeNumpixels(Views.iterable(outimg));
			double meanIntensityB = computeAverage(Views.iterable(outimgB));
			double totalIntensityB = computeTotal(Views.iterable(outimgB));
			
		Watershedobject current = new Watershedobject(parent, centroid, meanIntensity, totalIntensity, meanIntensityB, totalIntensityB, NumPixels);
		CloudObject currentCloud = new CloudObject(parent.CurrentViewIntSegoriginalimg, currentLabelObject, current.centroid, current.NumPixels, current.totalIntensity, current.meanIntensity,
				current.totalIntensityB, current.meanIntensityB,
				CovistoZselectPanel.thirdDimension, CovistoTimeselectPanel.fourthDimension, SegmentationLabel);
		Allclouds.add(currentCloud);
		
		return Allclouds;
	}

	/**
	 * Compute the average intensity for an {@link Iterable}.
	 *
	 * @param input
	 *            - the input data
	 * @return - the average as double
	 */
	public static <T extends RealType<T>> double computeAverage(final Iterable<T> input) {
		// Count all values using the RealSum class.
		// It prevents numerical instabilities when adding up millions of pixels
		final RealSum realSum = new RealSum();
		long count = 0;
        double meanIntensity = 0;
		for (final T type : input) {
			if(type.getRealDouble() > 0) {
			realSum.add(type.getRealDouble());
			++count;
			}
		}
		if(count > 0)
			meanIntensity = realSum.getSum() / count;

		return meanIntensity;
	}
	
	
	/**
	 * Compute the average intensity for an {@link Iterable}.
	 *
	 * @param input
	 *            - the input data
	 * @return - the average as double
	 */
	public static <T extends RealType<T>> double[] computeCentroid(final IterableInterval<T> input) {
		
		
		
		Cursor<T> cur = input.cursor();
		int numdim = input.numDimensions();
		double[] centroid = new double[numdim];
		double Intensity = 0;
		while(cur.hasNext()) {
			
			cur.fwd();
			for(int i = 0; i < numdim; ++i) {
				
				centroid[i]+=cur.getDoublePosition(i)*cur.get().getRealDouble();
				
			}
			Intensity += cur.get().getRealDouble();
		
		}
		
		for(int i = 0; i < numdim; ++i) {
			
			centroid[i] /= Intensity;
		}
		return centroid;
		
	}
	
	
	
	/**
	 * Compute the average intensity for an {@link Iterable}.
	 *
	 * @param input
	 *            - the input data
	 * @return - the average as double
	 */
	public static <T extends RealType<T>> double computeTotal(final Iterable<T> input) {
		// Count all values using the RealSum class.
		// It prevents numerical instabilities when adding up millions of pixels
		final RealSum realSum = new RealSum();
        double totalIntensity = 0;
		for (final T type : input) {
			if(type.getRealDouble() > 0) {
			realSum.add(type.getRealDouble());
		}
		}
			totalIntensity = realSum.getSum();

		return totalIntensity;
	}

	/**
	 * Compute the average intensity for an {@link Iterable}.
	 *
	 * @param input
	 *            - the input data
	 * @return - the average as double
	 */
	public static <T extends RealType<T>> double computeNumpixels(final Iterable<T> input) {
		// Count all values using the RealSum class.
		// It prevents numerical instabilities when adding up millions of pixels
        double numPixels = 0;
		for (final T type : input) {
			if(type.getRealDouble() > 0)
		   ++numPixels;
		}

		return numPixels;
	}
}
