package pluginTools;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;

public class InteractiveCloudify {

	public RandomAccessibleInterval<FloatType> originalimg;
	public RandomAccessibleInterval<FloatType> originalSecimg;																			

	public RandomAccessibleInterval<BitType> Segoriginalimg;
	public RandomAccessibleInterval<BitType> SegoriginalSecimg;

	public RandomAccessibleInterval<IntType> IntSegoriginalimg;
	public RandomAccessibleInterval<IntType> IntSegoriginalSecimg;

	public InteractiveCloudify(final RandomAccessibleInterval<FloatType> originalimg,
			final RandomAccessibleInterval<BitType> Segoriginalimg,
			final RandomAccessibleInterval<BitType> SegoriginalSecimg) {

		this.originalimg = originalimg;
		this.Segoriginalimg = Segoriginalimg;
		this.SegoriginalSecimg = SegoriginalSecimg;

	}

	public InteractiveCloudify(final RandomAccessibleInterval<FloatType> originalimg,
			final RandomAccessibleInterval<FloatType> originalSecimg,
			final RandomAccessibleInterval<BitType> Segoriginalimg,
			final RandomAccessibleInterval<BitType> SegoriginalSecimg) {

		this.originalimg = originalimg;
		this.originalSecimg = originalSecimg;
		this.Segoriginalimg = Segoriginalimg;
		this.SegoriginalSecimg = SegoriginalSecimg;

	}
	
	public InteractiveCloudify(final RandomAccessibleInterval<FloatType> originalimg,
			final RandomAccessibleInterval<FloatType> originalSecimg,
			final RandomAccessibleInterval<BitType> Segoriginalimg,
			final RandomAccessibleInterval<BitType> SegoriginalSecimg,
			final RandomAccessibleInterval<IntType> IntSegoriginalimg,
			final RandomAccessibleInterval<IntType> IntSegoriginalSecimg) {

		this.originalimg = originalimg;
		this.originalSecimg = originalSecimg;
		this.Segoriginalimg = Segoriginalimg;
		this.SegoriginalSecimg = SegoriginalSecimg;
		this.IntSegoriginalimg = IntSegoriginalimg;
		this.IntSegoriginalSecimg = IntSegoriginalSecimg;

	}
	

}
