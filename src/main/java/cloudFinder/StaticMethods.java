package cloudFinder;

import java.util.Iterator;

import ij.gui.Roi;
import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;
import pluginTools.InteractiveCloudify;

public class StaticMethods {

	
	
	public static void GetPixelList(InteractiveCloudify parent, RandomAccessibleInterval<IntType> intimg) {

		IntType min = new IntType();
		IntType max = new IntType();
		computeMinMax(Views.iterable(intimg), min, max);
		Cursor<IntType> intCursor = Views.iterable(intimg).cursor();
		// Neglect the background class label
		int currentLabel = max.get();
		int currentlabelmax = max.get();
		parent.pixellist.clear();
		
		
		while (intCursor.hasNext()) {
			intCursor.fwd();
			int i = intCursor.get().get();
			if (i != currentLabel && i!=currentlabelmax) {

				parent.pixellist.add(i);

				currentLabel = i;

			}

		}

	}
	
	public static <T extends Comparable<T> & Type<T>> void computeMinMax(final Iterable<T> input, final T min, final T max) {
		// create a cursor for the image (the order does not matter)
		final Iterator<T> iterator = input.iterator();

		// initialize min and max with the first image value
		T type = iterator.next();

		min.set(type);
		max.set(type);

		// loop over the rest of the data and determine min and max value
		while (iterator.hasNext()) {
			// we need this type more than once
			type = iterator.next();

			if (type.compareTo(min) < 0)
				min.set(type);

			if (type.compareTo(max) > 0)
				max.set(type);
		}
	}
	
	public static double getIntensity(RandomAccessibleInterval<FloatType> source, Roi roi) {

		double Intensity = 0;

		Cursor<FloatType> currentcursor = Views.iterable(source).localizingCursor();

		final double[] position = new double[source.numDimensions()];

		while (currentcursor.hasNext()) {

			currentcursor.fwd();

			currentcursor.localize(position);

			int x = (int) position[0];
			int y = (int) position[1];
			if (roi.contains(x, y)) {

				Intensity += currentcursor.get().get();

			}

		}

		return Intensity;

	}
	
	public static double getNumberofPixels(RandomAccessibleInterval<FloatType> source, Roi roi) {

		double NumberofPixels = 0;

		Cursor<FloatType> currentcursor = Views.iterable(source).localizingCursor();

		final double[] position = new double[source.numDimensions()];

		while (currentcursor.hasNext()) {

			currentcursor.fwd();

			currentcursor.localize(position);

			int x = (int) position[0];
			int y = (int) position[1];

			if (roi.contains(x, y)) {

				NumberofPixels++;

			}

		}

		return NumberofPixels;

	}
	
}
