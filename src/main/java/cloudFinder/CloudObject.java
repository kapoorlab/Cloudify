package cloudFinder;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import ij.gui.Roi;
import net.imglib2.AbstractEuclideanSpace;
import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import net.imglib2.view.Views;

public class CloudObject extends AbstractEuclideanSpace implements RealLocalizable, Comparable<CloudObject> {


	public RandomAccessibleInterval<IntType> Intimg;
	public double[] geometriccenter;
	public double area;
	public double totalintensity;
	public double averageintensity;
	public int thirdDimension;
	public int fourthDimension;
	public ArrayList<RoiObject> roiobject;
	public int label;
	private final int ID;
	private String name;
	private final ConcurrentHashMap< String, Double > features = new ConcurrentHashMap< String, Double >();
	public static AtomicInteger IDcounter = new AtomicInteger( -1 );
	/**
	 * 
	 * Integer image with the label, geometric center, intensity and area are for the image
	 * Roi object contains the properties of Rois inside that integer image
	 * 
	 * @param Intimg
	 * @param roiobject
	 * @param geomtericcenter
	 * @param area
	 * @param totalintensity
	 * @param averageintensity
	 * @param thirdDimension
	 * @param fourthDimension
	 */
	
	public CloudObject(final RandomAccessibleInterval<IntType> Intimg, final ArrayList<RoiObject> roiobject, final double[] geomtericcenter, 
			final double area, final double totalintensity, final double averageintensity, final int thirdDimension, final int fourthDimension, final int label) {
		super(3);
		this.Intimg = Intimg;
        this.roiobject = roiobject;
        this.geometriccenter = geomtericcenter;
        this.area = area;
        this.totalintensity = totalintensity;
        this.averageintensity = averageintensity;
        this.thirdDimension = thirdDimension;
        this.fourthDimension = fourthDimension;
        this.label = label;
     	this.ID = IDcounter.incrementAndGet();
		this.name = "ID" + ID;
    	    putFeature(ThirdDimension,  (double) thirdDimension);
		putFeature(FourthDimension, (double) fourthDimension);
		putFeature(XPOSITION, geometriccenter[0]);
		putFeature(YPOSITION, geometriccenter[1]);
	}

	@Override
	public int compareTo(CloudObject o) {
	
		return hashCode() - o.hashCode();
	}
	@Override
	public double getDoublePosition(int d) {
		
		return (float) getDoublePosition(d);
	}

	@Override
	public float getFloatPosition(int d) {
		
		return (float) getDoublePosition(d);
	}

	@Override
	public void localize(float[] position) {
		int n = position.length;
		for (int d = 0; d < n; ++d)
			position[d] = getFloatPosition(d);
		
	}

	@Override
	public void localize(double[] position) {
		int n = position.length;
		for (int d = 0; d < n; ++d)
			position[d] = getFloatPosition(d);
		
	}
	
	public void setName( final String name )
	{
		this.name = name;
	}

	public int ID()
	{
		return ID;
	}
	/** The name of the blob X position feature. */
	public static final String XPOSITION = "XPOSITION";

	/** The name of the blob Y position feature. */
	public static final String YPOSITION = "YPOSITION";
	


	

	/** The name of the frame feature. */
	public static final String FourthDimension = "FourthDimension";

	/** The name of the frame feature. */
	public static final String ThirdDimension = "ThirdDimension";
	
	
	public final Double getFeature( final String feature )
	{
		return features.get( feature );
	}
	public final void putFeature( final String feature, final Double value )
	{
		features.put( feature, value );
	}
	
	public static < T extends RealType< T > & NativeType< T >> Pair<Double, Integer> getIntensity(Roi roi, RandomAccessibleInterval<T> source) {

		double Intensity = 0;
        int NumberofPixels = 0;
		Cursor<T> currentcursor = Views.iterable(source).localizingCursor();

		final double[] position = new double[source.numDimensions()];

		while (currentcursor.hasNext()) {

			currentcursor.fwd();

			currentcursor.localize(position);

			int x = (int) position[0];
			int y = (int) position[1];

			if (roi.contains(x, y)) {

				Intensity += currentcursor.get().getRealDouble();

				NumberofPixels++;
			}

		}

		
		
		return new ValuePair<Double, Integer>(Intensity, NumberofPixels);

	}
	
	/**
	 * Returns the difference between the location of two clouds, this operation
	 * returns ( <code>A.diffTo(B) = - B.diffTo(A)</code>)
	 *
	 * @param target
	 *            the Cloud to compare to.
	 * @param int
	 *            n n = 0 for X- coordinate, n = 1 for Y- coordinate
	 * @return the difference in co-ordinate specified.
	 */
	public double diffTo(final CloudObject target, int n) {

		final double thisBloblocation = geometriccenter[n];
		final double targetBloblocation = target.geometriccenter[n];
		return thisBloblocation - targetBloblocation;
	}
	
	/**
	 * Returns the squared distance between two clouds.
	 *
	 * @param target
	 *            the Cloud to compare to.
	 *
	 * @return the distance to the current cloud to target cloud specified.
	 */

	public double squareDistanceTo(CloudObject target) {
		// Returns squared distance between the source Blob and the target Blob.

		final double[] sourceLocation = geometriccenter;
		final double[] targetLocation = target.geometriccenter;

		double distance = 0;

		for (int d = 0; d < sourceLocation.length; ++d) {

			distance += (sourceLocation[d] - targetLocation[d]) * (sourceLocation[d] - targetLocation[d]);
		}

		return distance;
	}
	public double DistanceTo(CloudObject target, final double alpha, final double beta) {
		// Returns squared distance between the source Blob and the target Blob.

		final double[] sourceLocation = geometriccenter;
		final double[] targetLocation = target.geometriccenter;

		double distance = 1.0E-5;

		for (int d = 0; d < sourceLocation.length; ++d) {

			distance += (sourceLocation[d] - targetLocation[d]) * (sourceLocation[d] - targetLocation[d]);
		}

			return distance;
	}
}
