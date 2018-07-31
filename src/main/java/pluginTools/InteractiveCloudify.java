package pluginTools;

import java.awt.CardLayout;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import cloudFinder.CloudObject;
import cloudFinder.DOGSeg;
import cloudFinder.MSERSeg;
import dogGUI.CovistoDogPanel;
import ij.ImageJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.io.Opener;
import ij.plugin.PlugIn;
import interactivePreprocessing.DoDOGListener;
import interactivePreprocessing.DoMSERListener;
import listeners.*;
import mserGUI.CovistoMserPanel;
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
	public NumberFormat nf;
	public final int scrollbarSize = 1000;
	public HashMap<String, ArrayList<CloudObject>> ZTRois;
	public RandomAccessibleInterval<FloatType> originalimg;
	public RandomAccessibleInterval<FloatType> originalSecimg;
	
	
	
	public RandomAccessibleInterval<FloatType> CurrentViewOrig;
	public RandomAccessibleInterval<FloatType> CurrentViewSecOrig;
	
	public RandomAccessibleInterval<FloatType> Segoriginalimg;
	public RandomAccessibleInterval<IntType> IntSegoriginalimg;
	
	public RandomAccessibleInterval<FloatType> CurrentViewSegoriginalimg;
	public RandomAccessibleInterval<IntType> CurrentViewIntSegoriginalimg;
	
	
	public boolean showMSER = true;
	public boolean showDOG = false;
	public MserTree<FloatType> newtree;
	public Color colorDrawMser = Color.green;
	public Color colorDrawDog = Color.red;
	public Color colorConfirm = Color.blue;
	public Color colorSnake = Color.YELLOW;
	public Color colorTrack = Color.GREEN;
	public Overlay overlay;
	public Set<Integer> pixellist;

	public static enum ValueChange {

		MSER, DOG, FOURTHDIMmouse, THIRDDIMmouse;

	}

	/**
	 * Current constructor, two channel images and an integer labelled image for
	 * nuclei
	 * 
	 * @param originalimg
	 * @param originalSecimg
	 * @param IntSegoriginalSecimg
	 */
	public InteractiveCloudify(final RandomAccessibleInterval<FloatType> originalimg,
			final RandomAccessibleInterval<FloatType> originalSecimg,
			final RandomAccessibleInterval<IntType> IntSegoriginalimg,
			final RandomAccessibleInterval<FloatType> Segoriginalimg) {

		this.originalimg = originalimg;
		this.originalSecimg = originalSecimg;
		this.IntSegoriginalimg = IntSegoriginalimg;
		this.Segoriginalimg = Segoriginalimg;
		this.ndims = originalimg.numDimensions();
	}

	

	@Override
	public void run(String arg0) {
		nf = NumberFormat.getInstance(Locale.ENGLISH);
		nf.setMaximumFractionDigits(3);
		jpb = new JProgressBar();
		interval = new FinalInterval(originalimg.dimension(0), originalimg.dimension(1));
		peaks = new ArrayList<RefinedPeak<Point>>();
		ZTRois = new HashMap<String, ArrayList<CloudObject>>();
		pixellist = new HashSet<Integer>();

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

		CurrentViewOrig = utility.CovistoSlicer.getCurrentView(originalimg,  CovistoZselectPanel.thirdDimension,
				CovistoZselectPanel.thirdDimensionSize,CovistoTimeselectPanel.fourthDimension,
				CovistoTimeselectPanel.fourthDimensionSize);

		CurrentViewIntSegoriginalimg = utility.CovistoSlicer.getCurrentView(IntSegoriginalimg,
				 CovistoZselectPanel.thirdDimension,
					CovistoZselectPanel.thirdDimensionSize,CovistoTimeselectPanel.fourthDimension,
					CovistoTimeselectPanel.fourthDimensionSize);
		
		CurrentViewSecOrig = utility.CovistoSlicer.getCurrentView(originalSecimg,  CovistoZselectPanel.thirdDimension,
				CovistoZselectPanel.thirdDimensionSize,CovistoTimeselectPanel.fourthDimension,
				CovistoTimeselectPanel.fourthDimensionSize);
		
		CurrentViewSegoriginalimg = utility.CovistoSlicer.getCurrentView(Segoriginalimg,  CovistoZselectPanel.thirdDimension,
				CovistoZselectPanel.thirdDimensionSize,CovistoTimeselectPanel.fourthDimension,
				CovistoTimeselectPanel.fourthDimensionSize);

		imp = ImageJFunctions.show(CurrentViewOrig);

		imp.setTitle("Active image" + " " + "time point : " + CovistoTimeselectPanel.fourthDimension + " " + " Z: "
				+ CovistoZselectPanel.thirdDimension);
		
		Card();
	}

	public void updatePreview(final ValueChange change) {

		overlay = imp.getOverlay();
		if (overlay == null) {

			overlay = new Overlay();
			imp.setOverlay(overlay);
		}
		int localthirddim = CovistoZselectPanel.thirdDimension, localfourthdim = CovistoTimeselectPanel.fourthDimension;

		if (change == ValueChange.FOURTHDIMmouse || change == ValueChange.THIRDDIMmouse) {

			CurrentViewOrig = utility.CovistoSlicer.getCurrentView(originalimg,  CovistoZselectPanel.thirdDimension,
					CovistoZselectPanel.thirdDimensionSize,CovistoTimeselectPanel.fourthDimension,
					CovistoTimeselectPanel.fourthDimensionSize);

			CurrentViewIntSegoriginalimg  = utility.CovistoSlicer.getCurrentView(IntSegoriginalimg,
					 CovistoZselectPanel.thirdDimension,
						CovistoZselectPanel.thirdDimensionSize,CovistoTimeselectPanel.fourthDimension,
						CovistoTimeselectPanel.fourthDimensionSize);
			
			CurrentViewSecOrig = utility.CovistoSlicer.getCurrentView(originalSecimg,  CovistoZselectPanel.thirdDimension,
					CovistoZselectPanel.thirdDimensionSize,CovistoTimeselectPanel.fourthDimension,
					CovistoTimeselectPanel.fourthDimensionSize);
			
			CurrentViewSegoriginalimg = utility.CovistoSlicer.getCurrentView(Segoriginalimg,  CovistoZselectPanel.thirdDimension,
					CovistoZselectPanel.thirdDimensionSize,CovistoTimeselectPanel.fourthDimension,
					CovistoTimeselectPanel.fourthDimensionSize);

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


			MSERSeg computeMSER = new MSERSeg(this, jpb);
			computeMSER.execute();

		}

		if (change == ValueChange.DOG) {
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

			DOGSeg computeDOG = new DOGSeg(this, jpb);
			computeDOG.execute();

		}

	}

	public JFrame Cardframe = new JFrame("Cloud Tracker");
	public JPanel KalmanPanel = new JPanel();
	public JPanel panelFirst = new JPanel();
	public JPanel panelCont = new JPanel();
	public JPanel DetectionPanel = new JPanel();
	public JPanel Zselect = new JPanel();
	public JPanel Timeselect = new JPanel();
	public JPanel MserPanel = new JPanel();
	public JPanel DogPanel = new JPanel();

	public CheckboxGroup detection = new CheckboxGroup();
	final Checkbox DOG = new Checkbox("Do DoG detection", detection, showDOG);
	final Checkbox MSER = new Checkbox("Do MSER detection", detection, showMSER);

	public final Insets insets = new Insets(10, 0, 0, 0);
	public final GridBagLayout layout = new GridBagLayout();
	public final GridBagConstraints c = new GridBagConstraints();

	public void Card() {

		CardLayout cl = new CardLayout();

		c.insets = new Insets(5, 5, 5, 5);
		panelCont.setLayout(cl);

		panelCont.add(panelFirst, "1");
		panelFirst.setLayout(layout);
		c.anchor = GridBagConstraints.BOTH;
		c.ipadx = 35;

		c.gridwidth = 10;
		c.gridheight = 10;
		c.gridy = 1;
		c.gridx = 0;

		Border methodborder = new CompoundBorder(new TitledBorder("Choose a colud finder"), new EmptyBorder(c.insets));
		// Put time slider
		Timeselect = CovistoTimeselectPanel.TimeselectPanel(ndims);

		panelFirst.add(Timeselect, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));
		// Put z slider

		Zselect = CovistoZselectPanel.ZselectPanel(ndims);

		panelFirst.add(Zselect, new GridBagConstraints(3, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));
		DetectionPanel.add(DOG, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));
		DetectionPanel.add(MSER, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		DetectionPanel.setBorder(methodborder);
		panelFirst.add(DetectionPanel, new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		// Difference of Gaussian detection panel
		DogPanel = CovistoDogPanel.DogPanel();
		panelFirst.add(DogPanel, new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		// Mser detection panel
		MserPanel = CovistoMserPanel.MserPanel();
		panelFirst.add(MserPanel, new GridBagConstraints(3, 3, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.RELATIVE, new Insets(10, 10, 0, 10), 0, 0));

		CovistoDogPanel.findminima.addItemListener(new FindMinimaListener(this));
		CovistoDogPanel.findmaxima.addItemListener(new FindMaximaListener(this));
		CovistoMserPanel.findminimaMser.addItemListener(new FindMinimaMserListener(this));
		CovistoMserPanel.findmaximaMser.addItemListener(new FindMaximaMserListener(this));
		CovistoDogPanel.AllDog.addActionListener(new PREApplyDog3DListener(this));
		CovistoMserPanel.AllMser.addActionListener(new PREZMserListener(this));
		DOG.addItemListener(new CloudDoDOGListener(this));
		MSER.addItemListener(new CloudDoMSERListener(this));
		CovistoDogPanel.sigmaslider.addAdjustmentListener(new PreSigmaListener(this, CovistoDogPanel.sigmaText,
				CovistoDogPanel.sigmastring, CovistoDogPanel.sigmaMin, CovistoDogPanel.sigmaMax,
				CovistoDogPanel.scrollbarSize, CovistoDogPanel.sigmaslider));

		CovistoMserPanel.deltaS.addAdjustmentListener(new PREDeltaListener(this, CovistoMserPanel.deltaText,
				CovistoMserPanel.deltastring, CovistoMserPanel.deltaMin, CovistoMserPanel.deltaMax,
				CovistoMserPanel.scrollbarSize, CovistoMserPanel.deltaS));

		CovistoMserPanel.Unstability_ScoreS.addAdjustmentListener(new PREUnstability_ScoreListener(this,
				CovistoMserPanel.Unstability_ScoreText, CovistoMserPanel.Unstability_Scorestring,
				CovistoMserPanel.Unstability_ScoreMin, CovistoMserPanel.Unstability_ScoreMax,
				CovistoMserPanel.scrollbarSize, CovistoMserPanel.Unstability_ScoreS));

		CovistoMserPanel.minDiversityS.addAdjustmentListener(new PREMinDiversityListener(this,
				CovistoMserPanel.minDivText, CovistoMserPanel.minDivstring, CovistoMserPanel.minDiversityMin,
				CovistoMserPanel.minDiversityMax, CovistoMserPanel.scrollbarSize, CovistoMserPanel.minDiversityS));

		CovistoMserPanel.minSizeS.addAdjustmentListener(new PREMinSizeListener(this, CovistoMserPanel.minSizeText,
				CovistoMserPanel.minSizestring, CovistoMserPanel.minSizemin, CovistoMserPanel.minSizemax,
				CovistoMserPanel.scrollbarSize, CovistoMserPanel.minSizeS));

		CovistoMserPanel.maxSizeS.addAdjustmentListener(new PREMaxSizeListener(this, CovistoMserPanel.maxSizeText,
				CovistoMserPanel.maxSizestring, CovistoMserPanel.maxSizemin, CovistoMserPanel.maxSizemax,
				CovistoMserPanel.scrollbarSize, CovistoMserPanel.maxSizeS));

		CovistoDogPanel.thresholdslider.addAdjustmentListener(new PreThresholdListener(this,
				CovistoDogPanel.thresholdText, CovistoDogPanel.thresholdstring, CovistoDogPanel.thresholdMin,
				CovistoDogPanel.thresholdMax, CovistoDogPanel.scrollbarSize, CovistoDogPanel.thresholdslider));

		CovistoTimeselectPanel.timeslider.addAdjustmentListener(
				new PreTimeListener(this, CovistoTimeselectPanel.timeText, CovistoTimeselectPanel.timestring,
						CovistoTimeselectPanel.fourthDimensionsliderInit, CovistoTimeselectPanel.fourthDimensionSize,
						CovistoTimeselectPanel.scrollbarSize, CovistoTimeselectPanel.timeslider));

		if (ndims > 3)
			CovistoZselectPanel.zslider.addAdjustmentListener(new PreZListener(this, CovistoZselectPanel.zText,
					CovistoZselectPanel.zstring, CovistoZselectPanel.thirdDimensionsliderInit,
					CovistoZselectPanel.thirdDimensionSize, scrollbarSize, CovistoZselectPanel.zslider));
		else
			CovistoZselectPanel.zslider.addAdjustmentListener(new PreZListener(this, CovistoZselectPanel.zgenText,
					CovistoZselectPanel.zgenstring, CovistoZselectPanel.thirdDimensionsliderInit,
					CovistoZselectPanel.thirdDimensionSize, scrollbarSize, CovistoZselectPanel.zslider));

		CovistoZselectPanel.inputFieldZ.addTextListener(new PreZlocListener(this, false));
		CovistoTimeselectPanel.inputFieldT.addTextListener(new PreTlocListener(this, false));

		Cardframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		cl.show(panelCont, "1");

		Cardframe.add(panelCont, "Center");
		Cardframe.add(jpb, "Last");
		panelFirst.setVisible(true);
		Cardframe.pack();
		Cardframe.setVisible(true);
	}

	public static void main(String[] args) {

		new ImageJ();
		JFrame frame = new JFrame("");
		
		ImagePlus impB = new Opener().openImage("/Users/aimachine/Documents/JLMCurvature/RegisteredImages/TrainingandBatch/Raw.tif");
		impB.show();
		
		ImagePlus impD = new Opener().openImage("/Users/aimachine/Documents/JLMCurvature/RegisteredImages/TrainingandBatch/C2Raw.tif");
		impD.show();
		
		ImagePlus impC = new Opener().openImage("/Users/aimachine/Documents/JLMCurvature/RegisteredImages/TrainingandBatch/Boundary.tif");
		impC.show();
		
		ImagePlus impA = new Opener().openImage("/Users/aimachine/Documents/JLMCurvature/RegisteredImages/TrainingandBatch/MultiCut.tif");
		impA.show();
		
		
		CloudifyFileChooser panel = new CloudifyFileChooser();

		frame.getContentPane().add(panel, "Center");
		frame.setSize(panel.getPreferredSize());

	}

}
