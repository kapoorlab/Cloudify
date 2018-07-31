package cloudFinder;

import java.util.Iterator;

import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.RealSum;
import net.imglib2.view.Views;
import pluginTools.InteractiveCloudify;
import preProcessing.GenericFilters;

public class Watershedobject {
	
	public final InteractiveCloudify parent;
	public final RandomAccessibleInterval<FloatType> source;
	public final double[] centroid;
	public final double meanIntensity;
	public final double totalIntensity;
	public final double NumPixels;

	
	
	
	public Watershedobject(final InteractiveCloudify parent, final RandomAccessibleInterval<FloatType> source, 
			final double[] centroid,
			final double meanIntensity, final double totalIntensity,
			final double NumPixels) {
        this.parent = parent;
		this.source = source;
		this.centroid = centroid;
		this.meanIntensity = meanIntensity;
		this.totalIntensity = totalIntensity;
		this.NumPixels = NumPixels;

	}

	public static Watershedobject CurrentLabelImage(final InteractiveCloudify parent, RandomAccessibleInterval<IntType> Intimg, int currentLabel) {
		int n = Intimg.numDimensions();
		long[] position = new long[n];

		Cursor<IntType> intCursor = Views.iterable(Intimg).cursor();

		RandomAccessibleInterval<FloatType> outimg = new ArrayImgFactory<FloatType>().create(Intimg, new FloatType());

		RandomAccess<FloatType> imageRA = outimg.randomAccess();

		// Go through the whole image and add every pixel, that belongs to
		// the currently processed label
		long[] minVal = { Intimg.max(0), Intimg.max(1) };
		long[] maxVal = { Intimg.min(0), Intimg.min(1) };

		while (intCursor.hasNext()) {
			intCursor.fwd();
			imageRA.setPosition(intCursor);
			int i = intCursor.get().get();
			if (i == currentLabel) {
				intCursor.localize(position);
				for (int d = 0; d < n; ++d) {
					if (position[d] < minVal[d]) {
						minVal[d] = position[d];
					}
					if (position[d] > maxVal[d]) {
						maxVal[d] = position[d];
					}

				}

				imageRA.get().setOne();
			} else
				imageRA.get().setZero();

		}
		FinalInterval intervalsmall = new FinalInterval(minVal, maxVal);

		RandomAccessibleInterval<FloatType> outimgsmall = extractImage(outimg, intervalsmall);
		double meanIntensity = computeAverage(Views.iterable(outimgsmall));
		double totalIntensity = computeTotal(Views.iterable(outimgsmall));
		double numPixels = computeNumpixels(Views.iterable(outimgsmall));
		double[] centroid = computeCentroid(Views.iterable(outimgsmall));
		Watershedobject currentobject = new Watershedobject(parent, outimgsmall, centroid, meanIntensity, totalIntensity, numPixels);

		return currentobject;

	}

	public static  Watershedobject CurrentLabelBinaryImage(final InteractiveCloudify parent, RandomAccessibleInterval<IntType> Intimg, int currentLabel) {
		int n = Intimg.numDimensions();
		long[] position = new long[n];

		Cursor<IntType> intCursor = Views.iterable(Intimg).cursor();

		RandomAccessibleInterval<FloatType> outimg = new ArrayImgFactory<FloatType>().create(Intimg, new FloatType());
		RandomAccess<FloatType> imageRA = outimg.randomAccess();
		RandomAccessibleInterval<FloatType> currentimg = GenericFilters.GradientmagnitudeImage(Intimg);

		RandomAccess<FloatType> inputRA = currentimg.randomAccess();

		// Go through the whole image and add every pixel, that belongs to
		// the currently processed label
		long[] minVal = { Intimg.max(0), Intimg.max(1) };
		long[] maxVal = { Intimg.min(0), Intimg.min(1) };

		while (intCursor.hasNext()) {
			intCursor.fwd();
			inputRA.setPosition(intCursor);
			imageRA.setPosition(inputRA);
			int i = intCursor.get().get();
			if (i == currentLabel) {
				intCursor.localize(position);
				for (int d = 0; d < n; ++d) {
					if (position[d] < minVal[d]) {
						minVal[d] = position[d];
					}
					if (position[d] > maxVal[d]) {
						maxVal[d] = position[d];
					}

				}

				imageRA.get().set(inputRA.get());
			} else
				imageRA.get().setZero();

		}
		FinalInterval intervalsmall = new FinalInterval(minVal, maxVal);

		RandomAccessibleInterval<FloatType> outimgsmall = extractImage(outimg, intervalsmall);
		double meanIntensity = computeAverage(Views.iterable(outimgsmall));
		double totalIntensity = computeTotal(Views.iterable(outimgsmall));
		double numPixels = computeNumpixels(Views.iterable(outimgsmall));
		double[] centroid = computeCentroid(Views.iterable(outimgsmall));
		Watershedobject currentobject = new Watershedobject(parent, outimgsmall, centroid, meanIntensity, totalIntensity, numPixels);

		return currentobject;

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
			realSum.add(type.getRealDouble());
			++count;
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
		while(cur.hasNext()) {
			
			cur.fwd();
			
			for(int i = 0; i < numdim; ++i) {
				
				centroid[i]+=cur.getDoublePosition(i);
				
			}
		
		}
		
		for(int i = 0; i < numdim; ++i) {
			
			centroid[i] /= input.size();
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
			realSum.add(type.getRealDouble());
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
		   ++numPixels;
		}

		return numPixels;
	}
	public static RandomAccessibleInterval<FloatType> extractImage(final RandomAccessibleInterval<FloatType> outimg,
			final FinalInterval interval) {

		return outimg;
	}

	

	

}
