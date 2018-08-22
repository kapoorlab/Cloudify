package cloudFinder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import dogGUI.CovistoDogPanel;
import ij.gui.Roi;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.dog.DogDetection;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.view.Views;
import pluginTools.InteractiveCloudify;
import timeGUI.CovistoTimeselectPanel;
import zGUI.CovistoZselectPanel;

public class DOGSeg  {

	final InteractiveCloudify parent;
	final JProgressBar jpb;

	public DOGSeg(final InteractiveCloudify parent, final JProgressBar jpb) {

		this.parent = parent;
		this.jpb = jpb;

	}

	
	public void execute() {
		
		/**
		 * 
		 * 
		 * Create a watershed object that defines the region of interest, then make a roi object inside that region to get the clouds, finally put it all together as a cloud object
		 * and enumerate it all in a list and a hashmap for that time.
		 */
	
		
		String uniqueID = Integer.toString(CovistoZselectPanel.thirdDimension) + Integer.toString(CovistoTimeselectPanel.fourthDimension);
		
		// Make the watershed object
		
		StaticMethods.GetPixelList(parent, parent.CurrentViewIntSegoriginalimg);
		
		
		Iterator<Integer> setiter = parent.pixellist.iterator();
		 parent.overlay.clear();
		 
		 ArrayList<CloudObject> Allclouds = new ArrayList<CloudObject>();
		 ArrayList<CloudObject> SecAllclouds = new ArrayList<CloudObject>();
		while (setiter.hasNext()) {

			int label = setiter.next();
			// Get the region 
		RandomAccessibleInterval<FloatType> current = Watershedobject.CurrentDetectionImage(parent, parent.CurrentViewIntSegoriginalimg, parent.CurrentViewSegoriginalimg , label);
						
						
		
			final DogDetection.ExtremaType type;
			if (CovistoDogPanel.lookForMaxima)
				type = DogDetection.ExtremaType.MINIMA;
			else
				type = DogDetection.ExtremaType.MAXIMA;
			CovistoDogPanel.sigma2 = utility.ScrollbarUtils.computeSigma2(CovistoDogPanel.sigma, parent.sensitivity);
			final DogDetection<FloatType> newdog = new DogDetection<FloatType>(Views.extendBorder(current),
					parent.interval, new double[] { 1, 1 }, CovistoDogPanel.sigma, CovistoDogPanel.sigma2, type, CovistoDogPanel.threshold, true);
			parent.peaks = newdog.getSubpixelPeaks();
			parent.Rois = utility.FinderUtils.getcurrentRois(parent.peaks, CovistoDogPanel.sigma, CovistoDogPanel.sigma2);
			
			
			ArrayList<RoiObject> currentLabelObject = new ArrayList<RoiObject>();
			ArrayList<RoiObject> SeccurrentLabelObject = new ArrayList<RoiObject>();
			
			for(Roi roi : parent.Rois) {
				
				double[] centroid = roi.getContourCentroid();
				
				double Intensity = StaticMethods.getIntensity(parent.CurrentViewOrig, roi);
				
				double SecIntensity = StaticMethods.getIntensity(parent.CurrentViewSecOrig, roi);
				
				double numPixels = StaticMethods.getNumberofPixels(parent.CurrentViewOrig, roi);
				
				double meanIntensity = Intensity / numPixels;
				
				double SecmeanIntensity = SecIntensity / numPixels;
		
				
				RoiObject currentRoiobject = new RoiObject(roi, centroid, meanIntensity, Intensity, SecmeanIntensity, SecIntensity, numPixels);
				
				currentLabelObject.add(currentRoiobject);
          
				

				roi.setStrokeColor(parent.colorDrawMser);
				parent.overlay.add(roi);
				
				
			}
			
			// Measure properties of cell excluding clouds
			
			
			Pair<RandomAccessibleInterval<FloatType>,RandomAccessibleInterval<FloatType>>  BothMissImage = Watershedobject.CurrentOrigLabelImage(parent, parent.CurrentViewIntSegoriginalimg, 
					parent.CurrentViewOrig, parent.CurrentViewSecOrig, label);
			MeasureProperties CloudandCell = new MeasureProperties(parent, BothMissImage.getA(), BothMissImage.getB(), currentLabelObject, label);
			Allclouds.addAll(CloudandCell.GetCurrentCloud());
			
		}

		  parent.AllClouds.put(uniqueID, Allclouds);
		
		

		
			parent.imp.setOverlay(parent.overlay);
			parent.imp.updateAndDraw();
			
			
			
				
	}

	

}
