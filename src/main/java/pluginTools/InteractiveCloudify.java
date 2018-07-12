package pluginTools;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JPanel;
import javax.swing.JProgressBar;

import cloudFinder.CloudObject;
import cloudFinder.DOGSeg;
import cloudFinder.MSERSeg;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.plugin.PlugIn;
import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.Point;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.componenttree.mser.MserTree;
import net.imglib2.algorithm.localextrema.RefinedPeak;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;
import timeGUI.CovistoTimeselectPanel;
import zGUI.CovistoZselectPanel;

public class InteractiveCloudify extends JPanel implements PlugIn {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static int standardSensitivity = 4;
	public int sensitivity = standardSensitivity;
	public ArrayList<RefinedPeak<Point>> peaks;
	public FinalInterval interval;
	public JProgressBar jpb;
	public boolean apply3D = false;
	public ArrayList<Roi> Rois;
	public ImagePlus imp;
	public int ndims;
	public final int scrollbarSize = 1000;
	public HashMap<String, ArrayList<CloudObject>> ZTRois;
	public ArrayList<CloudObject> CurrentCloudobject;
	public RandomAccessibleInterval<FloatType> originalimg;
	public RandomAccessibleInterval<FloatType> originalSecimg;																			
	public RandomAccessibleInterval<FloatType> CurrentViewOrig;
	public RandomAccessibleInterval<FloatType> Segoriginalimg;
	public RandomAccessibleInterval<FloatType> SegoriginalSecimg;
	public RandomAccessibleInterval<UnsignedByteType> newimg;
	public RandomAccessibleInterval<IntType> IntSegoriginalimg;
	public RandomAccessibleInterval<IntType> IntSegoriginalSecimg;
	public boolean showMSER = false;
	public boolean showDOG = false;
	public MserTree<UnsignedByteType> newtree;
	public Color colorDrawMser = Color.green;
	public Color colorDrawDog = Color.red;
	public Color colorConfirm = Color.blue;
	public Color colorSnake = Color.YELLOW;
	public Color colorTrack = Color.GREEN;
	public Overlay overlay;
	
	
	
	public static enum ValueChange {
		
		MSER, DOG, FOURTHDIMmouse, THIRDDIMmouse;
		
	}

	public InteractiveCloudify(final RandomAccessibleInterval<FloatType> originalimg,
			final RandomAccessibleInterval<FloatType> Segoriginalimg,
			final RandomAccessibleInterval<FloatType> SegoriginalSecimg) {

		this.originalimg = originalimg;
		this.Segoriginalimg = Segoriginalimg;
		this.SegoriginalSecimg = SegoriginalSecimg;
		this.ndims = originalimg.numDimensions();
	}

	public InteractiveCloudify(final RandomAccessibleInterval<FloatType> originalimg,
			final RandomAccessibleInterval<FloatType> originalSecimg,
			final RandomAccessibleInterval<FloatType> Segoriginalimg,
			final RandomAccessibleInterval<FloatType> SegoriginalSecimg) {

		this.originalimg = originalimg;
		this.originalSecimg = originalSecimg;
		this.Segoriginalimg = Segoriginalimg;
		this.SegoriginalSecimg = SegoriginalSecimg;
		this.ndims = originalimg.numDimensions();
	}
	
	public InteractiveCloudify(final RandomAccessibleInterval<FloatType> originalimg,
			final RandomAccessibleInterval<FloatType> originalSecimg,
			final RandomAccessibleInterval<FloatType> Segoriginalimg,
			final RandomAccessibleInterval<FloatType> SegoriginalSecimg,
			final RandomAccessibleInterval<IntType> IntSegoriginalimg,
			final RandomAccessibleInterval<IntType> IntSegoriginalSecimg) {

		this.originalimg = originalimg;
		this.originalSecimg = originalSecimg;
		this.Segoriginalimg = Segoriginalimg;
		this.SegoriginalSecimg = SegoriginalSecimg;
		this.IntSegoriginalimg = IntSegoriginalimg;
		this.IntSegoriginalSecimg = IntSegoriginalSecimg;
		this.ndims = originalimg.numDimensions();
	}

	@Override
	public void run(String arg0) {
		jpb = new JProgressBar();
		interval = new FinalInterval(originalimg.dimension(0), originalimg.dimension(1));
		peaks = new ArrayList<RefinedPeak<Point>>();
		CurrentCloudobject = new ArrayList<CloudObject>();
		ZTRois = new HashMap<String, ArrayList<CloudObject>>();
		
		if (ndims < 3) {

			CovistoZselectPanel.thirdDimensionSize = 0;
			CovistoTimeselectPanel.fourthDimensionSize = 0;
		}

		if (ndims == 3) {

			CovistoTimeselectPanel.fourthDimension = 0;
			CovistoTimeselectPanel.fourthDimensionsliderInit = 0;
			CovistoZselectPanel.thirdDimension = 1;
			CovistoTimeselectPanel.fourthDimensionSize = 0;
			CovistoZselectPanel.thirdDimensionSize = (int) originalimg.dimension(2);

		}

		if (ndims == 4) {

			CovistoTimeselectPanel.fourthDimension = 1;
			CovistoZselectPanel.thirdDimension = 1;
			CovistoZselectPanel.thirdDimensionSize = (int) originalimg.dimension(2);
			CovistoTimeselectPanel.fourthDimensionSize = (int) originalimg.dimension(3);

			
		}

		CurrentViewOrig = utility.CovistoSlicer.getCurrentView(originalimg, CovistoTimeselectPanel.fourthDimension,
				CovistoZselectPanel.thirdDimensionSize, CovistoZselectPanel.thirdDimension,
				CovistoTimeselectPanel.fourthDimensionSize);

		imp = ImageJFunctions.show(CurrentViewOrig);
	
		imp.setTitle("Active image" + " " + "time point : " + CovistoTimeselectPanel.fourthDimension + " " + " Z: "
				+ CovistoZselectPanel.thirdDimension);
	}
	
	
	public void updatePreview(final ValueChange change) {
		
		overlay = imp.getOverlay();
		int localthirddim = CovistoZselectPanel.thirdDimension, localfourthdim = CovistoTimeselectPanel.fourthDimension;
		
		if (change == ValueChange.FOURTHDIMmouse || change == ValueChange.THIRDDIMmouse) {

			if (imp == null) {
				imp = ImageJFunctions.show(CurrentViewOrig);

			}

			else {

				final float[] pixels = (float[]) imp.getProcessor().getPixels();
				final Cursor<FloatType> c = Views.iterable(CurrentViewOrig).cursor();

				for (int i = 0; i < pixels.length; ++i)
					pixels[i] = c.next().get();

				imp.updateAndDraw();

			}

			imp.setTitle("Active image" + " " + "time point : " + CovistoTimeselectPanel.fourthDimension + " " + " Z: "
					+ CovistoZselectPanel.thirdDimension);

			newimg = utility.CovistoSlicer.PREcopytoByteImage(CurrentViewOrig);

			if (showMSER) {

				MSERSeg computeMSER = new MSERSeg(this, jpb);
				computeMSER.execute();

			}

			if (showDOG) {

				DOGSeg computeDOG = new DOGSeg(this, jpb);
				computeDOG.execute();
			}

			CovistoZselectPanel.zText.setText("Current Z = " + localthirddim);
			CovistoZselectPanel.zgenText.setText("Current Z / T = " + localthirddim);
			CovistoZselectPanel.zslider.setValue(utility.CovistoSlicer.computeScrollbarPositionFromValue(localthirddim,
					CovistoZselectPanel.thirdDimensionsliderInit, CovistoZselectPanel.thirdDimensionSize,
					scrollbarSize));
			CovistoZselectPanel.zslider.repaint();
			CovistoZselectPanel.zslider.validate();

			CovistoTimeselectPanel.timeText.setText("Current T = " + localfourthdim);
			CovistoTimeselectPanel.timeslider.setValue(utility.CovistoSlicer.computeScrollbarPositionFromValue(
					localfourthdim, CovistoTimeselectPanel.fourthDimensionsliderInit,
					CovistoTimeselectPanel.fourthDimensionSize, CovistoTimeselectPanel.scrollbarSize));
			CovistoTimeselectPanel.timeslider.repaint();
			CovistoTimeselectPanel.timeslider.validate();
		}

		if (change == ValueChange.MSER) {
			if (imp == null) {
				imp = ImageJFunctions.show(CurrentViewOrig);

			}

			else {

				final float[] pixels = (float[]) imp.getProcessor().getPixels();
				final Cursor<FloatType> c = Views.iterable(CurrentViewOrig).cursor();

				for (int i = 0; i < pixels.length; ++i)
					pixels[i] = c.next().get();

				imp.updateAndDraw();

			}

			imp.setTitle("Active image" + " " + "time point : " + CovistoTimeselectPanel.fourthDimension + " " + " Z: "
					+ CovistoZselectPanel.thirdDimension);

			newimg = utility.CovistoSlicer.PREcopytoByteImage(CurrentViewOrig);

			MSERSeg computeMSER = new MSERSeg(this, jpb);
			computeMSER.execute();

		}

		
	}
	
	

}
