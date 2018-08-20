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
	public final double[] centroid;
	public final double meanIntensity;
	public final double totalIntensity;
	public final double NumPixels;

	
	
	
	public Watershedobject(final InteractiveCloudify parent, 
			final double[] centroid,
			final double meanIntensity, final double totalIntensity,
			final double NumPixels) {
        this.parent = parent;
		this.centroid = centroid;
		this.meanIntensity = meanIntensity;
		this.totalIntensity = totalIntensity;
		this.NumPixels = NumPixels;

	}

	
	public static RandomAccessibleInterval<FloatType> CurrentDetectionImage(final InteractiveCloudify parent, RandomAccessibleInterval<IntType> Intimg,RandomAccessibleInterval<FloatType> Detimg, 
			int currentLabel) {
		
		int n = Intimg.numDimensions();
		long[] position = new long[n];

		Cursor<IntType> intCursor = Views.iterable(Intimg).cursor();

		RandomAccessibleInterval<FloatType> outimg = new ArrayImgFactory<FloatType>().create(Intimg, new FloatType());

		
		RandomAccess<FloatType> imageRA = outimg.randomAccess();
		
		RandomAccess<FloatType> detRA = Detimg.randomAccess();
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
				detRA.setPosition(intCursor);
				for (int d = 0; d < n; ++d) {
					if (position[d] < minVal[d]) {
						minVal[d] = position[d];
					}
					if (position[d] > maxVal[d]) {
						maxVal[d] = position[d];
					}

				}

				imageRA.get().set(detRA.get());
			} else {
				imageRA.get().setZero(); 
				
			}
		}
		
		return outimg;
	}
	
	
	public static RandomAccessibleInterval<FloatType> CurrentOrigLabelImage(final InteractiveCloudify parent, RandomAccessibleInterval<IntType> Intimg,RandomAccessibleInterval<FloatType> sourceimg, 
			int currentLabel) {
		
		int n = Intimg.numDimensions();
		long[] position = new long[n];

		Cursor<IntType> intCursor = Views.iterable(Intimg).cursor();

		RandomAccessibleInterval<FloatType> outimg = new ArrayImgFactory<FloatType>().create(Intimg, new FloatType());

		
		RandomAccess<FloatType> imageRA = outimg.randomAccess();
		
		RandomAccess<FloatType> detRA = sourceimg.randomAccess();
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
				detRA.setPosition(intCursor);
				for (int d = 0; d < n; ++d) {
					if (position[d] < minVal[d]) {
						minVal[d] = position[d];
					}
					if (position[d] > maxVal[d]) {
						maxVal[d] = position[d];
					}

				}

				imageRA.get().set(detRA.get());
			} else {
				imageRA.get().setZero(); 
				
			}
		}
		
		return outimg;
	}


	
	public static RandomAccessibleInterval<FloatType> extractImage(final RandomAccessibleInterval<FloatType> outimg,
			final FinalInterval interval) {

		return outimg;
	}

	

	

}
