package pluginTools;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeriesCollection;

import cloudFinder.CloudObject;
import cloudFinder.CloudTrackObject;
import cloudFinder.DOGSeg;
import cloudFinder.MSERSeg;
import cloudTracker.TrackModel;
import costMatrix.CostFunction;
import dogGUI.CovistoDogPanel;
import ij.ImageJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.io.Opener;
import ij.plugin.PlugIn;
import kalmanGUI.CovistoKalmanPanel;
import kalmanTrackListeners.LinkobjectListener;
import kalmanTrackListeners.PREAlphaListener;
import kalmanTrackListeners.PREBetaListeners;
import kalmanTrackListeners.PREIniSearchListener;
import kalmanTrackListeners.PRELostFrameListener;
import kalmanTrackListeners.PREMaxSearchTListener;
import listeners.*;
import mserGUI.CovistoMserPanel;
import nearestNeighbourGUI.CovistoNearestNPanel;
import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.Point;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.componenttree.mser.MserTree;
import net.imglib2.algorithm.localextrema.RefinedPeak;
import net.imglib2.algorithm.stats.Normalize;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.Type;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.view.Views;
import saveTracks.CovistoSavePanel;
import timeGUI.CovistoTimeselectPanel;
import watershedGUI.CovistoWatershedPanel;
import zGUI.CovistoZselectPanel;

public class InteractiveCloudify extends JPanel implements PlugIn {

	/**
	 * 
	 */
	public String addToName = "CTrack";
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
	public HashMap<String, ArrayList<CloudObject>> AllClouds;
	public CostFunction<CloudObject, CloudObject> UserchosenCostFunction;
	public HashMap<String, Integer> AccountedZ;
	// Cost function parameters
	public float alphaMin = 0;
	public float alphaMax = 1;
	public float betaMin = 0;
	public float betaMax = 1;
	public int background = -1;
	public JTable table;
	public int row;
	public int tablesize;
	public String selectedID;
	// Kalman parameters
	public float maxSearchradius = 100;
	public float maxSearchradiusS = 10;
	public int missedframes = 200;
	public int maxSearchradiusInit = (int) maxSearchradius;
	public float maxSearchradiusMin = 1;
	public float maxSearchradiusMax = maxSearchradius;
	public float maxSearchradiusMinS = 1;
	public float maxSearchradiusMaxS = maxSearchradius;
	public MouseMotionListener ml;
	public HashMap<String, CloudObject> Finalresult;
	public MouseListener mvl;
	public int[] Clickedpoints;
	public ArrayList<Pair<String, CloudTrackObject>> resultIntensity;
	
	public File saveFile;
	public Frame jFreeChartFrameIntensityA;
	public Frame jFreeChartFrameIntensityB;
	
	public JFreeChart chartIntensityA;
	public JFreeChart chartIntensityB;
	
	public ImagePlus resultimp;
	
	public XYSeriesCollection IntensityAdataset;
	public XYSeriesCollection IntensityBdataset;
	
	
	public Frame jFreeChartFrameIntensityASec;
	public Frame jFreeChartFrameIntensityBSec;
	
	public JFreeChart chartIntensityASec;
	public JFreeChart chartIntensityBSec;
	
	
	
	public XYSeriesCollection IntensityAdatasetSec;
	public XYSeriesCollection IntensityBdatasetSec;
	
	public RandomAccessibleInterval<FloatType> originalimg;
	public RandomAccessibleInterval<FloatType> originalSecimg;
	
	
	
	public RandomAccessibleInterval<FloatType> CurrentViewOrig;
	public RandomAccessibleInterval<FloatType> CurrentViewSecOrig;
	
	public RandomAccessibleInterval<FloatType> Segoriginalimg;
	public RandomAccessibleInterval<IntType> IntSegoriginalimg;
	
	public RandomAccessibleInterval<FloatType> CurrentViewSegoriginalimg;
	public RandomAccessibleInterval<IntType> CurrentViewIntSegoriginalimg;
	public ArrayList<Pair<String, CloudObject>> Tracklist;
	public ArrayList<Pair<String, CloudObject>> TracklistChannelTwo;
	public boolean showMSER = true;
	public boolean showDOG = false;
	public int rowchoice;
		
		
		
	
	public TrackModel Globalmodel;
	public MserTree<FloatType> newtree;
	public Color colorDrawMser = Color.green;
	public Color colorDrawDog = Color.red;
	public Color colorConfirm = Color.blue;
	public Color colorSnake = Color.YELLOW;
	public Color colorTrack = Color.GREEN;
	public Overlay overlay;
	public Set<Integer> pixellist;
	public ImageStack prestack;
	public static enum ValueChange {

		MSER, DOG, FOURTHDIMmouse, THIRDDIMmouse, ALPHA, BETA, All;

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
		FloatType minval = new FloatType(0);
		FloatType maxval = new FloatType(255);
		Normalize.normalize(Views.iterable(Segoriginalimg), minval, maxval);
		this.IntensityAdataset = new XYSeriesCollection();
		this.IntensityBdataset = new XYSeriesCollection();
		
		this.IntensityAdatasetSec = new XYSeriesCollection();
		this.IntensityBdatasetSec = new XYSeriesCollection();
		
		this.chartIntensityA = utility.ChartMaker.makeChart(IntensityAdataset, "Cell - Cloud (Ch1) Intensity evolution", "Timepoint", "Intensity");
		
		
		this.jFreeChartFrameIntensityA = utility.ChartMaker.display(chartIntensityA, new Dimension(500, 500));
		this.jFreeChartFrameIntensityA.setVisible(false);
		
		this.chartIntensityB = utility.ChartMaker.makeChart(IntensityBdataset, "Cloud Intensity evolution (Ch1)", "Timepoint", "Intensity");
		this.jFreeChartFrameIntensityB = utility.ChartMaker.display(chartIntensityB, new Dimension(500, 500));
		this.jFreeChartFrameIntensityB.setVisible(false);
		
	    this.chartIntensityASec = utility.ChartMaker.makeChart(IntensityAdatasetSec, "Cell - Cloud Intensity evolution (Ch2)", "Timepoint", "Intensity");
		
		
		this.jFreeChartFrameIntensityASec = utility.ChartMaker.display(chartIntensityASec, new Dimension(500, 500));
		this.jFreeChartFrameIntensityASec.setVisible(false);
		
		this.chartIntensityBSec = utility.ChartMaker.makeChart(IntensityBdatasetSec, "Cloud Intensity evolution (Ch2)", "Timepoint", "Intensity");
		this.jFreeChartFrameIntensityBSec = utility.ChartMaker.display(chartIntensityBSec, new Dimension(500, 500));
		this.jFreeChartFrameIntensityBSec.setVisible(false);
	}
	

	

	@Override
	public void run(String arg0) {
		saveFile = new java.io.File(".");
		nf = NumberFormat.getInstance(Locale.ENGLISH);
		nf.setMaximumFractionDigits(3);
		
		jpb = new JProgressBar();
		Clickedpoints = new int[2];
		interval = new FinalInterval(originalimg.dimension(0), originalimg.dimension(1));
		peaks = new ArrayList<RefinedPeak<Point>>();
		AllClouds = new HashMap<String, ArrayList<CloudObject>>();
		pixellist = new HashSet<Integer>();
		AccountedZ = new HashMap<String, Integer>();
		Finalresult = new HashMap<String, CloudObject>();
		Tracklist = new ArrayList<Pair<String, CloudObject>>();
		TracklistChannelTwo = new ArrayList<Pair<String, CloudObject>>();
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
			prestack = new ImageStack((int) originalimg.dimension(0), (int) originalimg.dimension(1),
					java.awt.image.ColorModel.getRGBdefault());
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

		
		
		IntType min = new IntType();
		IntType max = new IntType();
		computeMinMax(Views.iterable(CurrentViewIntSegoriginalimg), min, max);
		// Neglect the background class label
		int currentLabel = min.get();

		background = currentLabel;
		
		imp = ImageJFunctions.show(CurrentViewOrig);

		imp.setTitle("Active image" + " " + "time point : " + CovistoTimeselectPanel.fourthDimension + " " + " Z: "
				+ CovistoZselectPanel.thirdDimension);
		updatePreview(ValueChange.THIRDDIMmouse);
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
			
			
			String ZID = Integer.toString( CovistoZselectPanel.thirdDimension);
			AccountedZ.put(ZID,  CovistoZselectPanel.thirdDimension);
			
			
			
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
	public JFileChooser chooserA = new JFileChooser();
	public JPanel panelFirst = new JPanel();

	
	public JPanel PanelSelectFile = new JPanel();
	public JPanel panelCont = new JPanel();
	public JPanel DetectionPanel = new JPanel();
	public JPanel Zselect = new JPanel();
	public JPanel Timeselect = new JPanel();
	public JPanel MserPanel = new JPanel();
	public JPanel DogPanel = new JPanel();
	public JPanel Original = new JPanel();
	public CheckboxGroup detection = new CheckboxGroup();
	final Checkbox DOG = new Checkbox("Do DoG detection", detection, showDOG);
	final Checkbox MSER = new Checkbox("Do MSER detection", detection, showMSER);
	
	public final Insets insets = new Insets(10, 0, 0, 0);
	public final GridBagLayout layout = new GridBagLayout();
	public final GridBagConstraints c = new GridBagConstraints();
	public JScrollPane scrollPane;
	int SizeX = 400;
	int SizeY = 200;
	public Border selectfile = new CompoundBorder(new TitledBorder("Select Track"), new EmptyBorder(c.insets));
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

		
		
		
		Object[] colnames = new Object[] { "Track Id", "Location X", "Location Y", "Location Z/T", "Mean Intensity" };

		Object[][] rowvalues = new Object[0][colnames.length];
		
		if (Finalresult != null && Finalresult.size() > 0) {

			rowvalues = new Object[Finalresult.size()][colnames.length];

		}
		
		table = new JTable(rowvalues, colnames);
		
		
		
		table.setFillsViewportHeight(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		scrollPane = new JScrollPane(table);

		scrollPane.getViewport().add(table);
		scrollPane.setAutoscrolls(true);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		PanelSelectFile.add(scrollPane, BorderLayout.CENTER);

		PanelSelectFile.setBorder(selectfile);
		int size = 100;
		table.getColumnModel().getColumn(0).setPreferredWidth(size);
		table.getColumnModel().getColumn(1).setPreferredWidth(size);
		table.getColumnModel().getColumn(2).setPreferredWidth(size);
		table.getColumnModel().getColumn(3).setPreferredWidth(size);
		table.getColumnModel().getColumn(4).setPreferredWidth(size);
		table.setPreferredScrollableViewportSize(table.getPreferredSize());
		table.setFillsViewportHeight(true);
		table.isOpaque();
		scrollPane.setMinimumSize(new Dimension(300, 200));
		scrollPane.setPreferredSize(new Dimension(300, 200));
		// Put z slider

		Zselect = CovistoZselectPanel.ZselectPanel(ndims);

		panelFirst.add(Zselect, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

		// Mser detection panel
		MserPanel = CovistoMserPanel.MserPanel();
		panelFirst.add(MserPanel, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

		KalmanPanel = CovistoKalmanPanel.KalmanPanel();
		
		
		

		panelFirst.add(KalmanPanel, new GridBagConstraints(3, 0, 2, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		panelFirst.add(PanelSelectFile, new GridBagConstraints(4, 2, 2, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		
	
		CovistoKalmanPanel.Timetrack.addActionListener(new LinkobjectListener(this));
		CovistoKalmanPanel.lostframe.addTextListener(new PRELostFrameListener(this));
		CovistoKalmanPanel.alphaS.addAdjustmentListener(new PREAlphaListener(this, CovistoKalmanPanel.alphaText,
				CovistoKalmanPanel.alphastring, CovistoKalmanPanel.alphaMin, CovistoKalmanPanel.alphaMax,
				CovistoKalmanPanel.scrollbarSize, CovistoKalmanPanel.alphaS));
		CovistoKalmanPanel.betaS.addAdjustmentListener(new PREBetaListeners(this, CovistoKalmanPanel.betaText,
				CovistoKalmanPanel.betastring, CovistoKalmanPanel.betaMin, CovistoKalmanPanel.betaMax,
				CovistoKalmanPanel.scrollbarSize, CovistoKalmanPanel.betaS));
		
		CovistoKalmanPanel.maxSearchKalman.addAdjustmentListener(new PREMaxSearchTListener(this,
				CovistoKalmanPanel.maxSearchTextKalman, CovistoKalmanPanel.maxSearchstringKalman,
				CovistoKalmanPanel.maxSearchradiusMin, CovistoKalmanPanel.maxSearchradiusMax,
				CovistoKalmanPanel.scrollbarSize, CovistoKalmanPanel.maxSearchSS));
		CovistoKalmanPanel.initialSearchS.addAdjustmentListener(new PREIniSearchListener(this,
				CovistoKalmanPanel.iniSearchText, CovistoKalmanPanel.initialSearchstring,
				CovistoKalmanPanel.initialSearchradiusMin, CovistoKalmanPanel.initialSearchradiusMax,
				CovistoKalmanPanel.scrollbarSize, CovistoKalmanPanel.initialSearchS));
		
		
	    Original = CovistoSavePanel.SavePanel();
		CovistoSavePanel.inputField.addTextListener(new CtrackFilenameListener(this));
		CovistoSavePanel.inputtrackField.addTextListener(new CTrackidListener(this));
		CovistoSavePanel.Savebutton.addActionListener(new SaveListener(this));
		CovistoSavePanel.SaveAllbutton.addActionListener(new SaveAllListener(this));
		CovistoSavePanel.ChooseDirectory.addActionListener(new SaverDirectory(this) );
		
		panelFirst.add(Original, new GridBagConstraints(4, 1, 2, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		
	
		
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
		//CovistoTimeselectPanel.inputFieldT.addTextListener(new PreTlocListener(this, false));

		Cardframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		cl.show(panelCont, "1");
		Cardframe.add(panelCont, "Center");
		Cardframe.add(jpb, "Last");
		panelFirst.setVisible(true);

		Cardframe.pack();
		Cardframe.setVisible(true);
	}



	public static <T extends Comparable<T> & Type<T>> void computeMinMax(final Iterable<T> input, final T min,
			final T max) {
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



}
