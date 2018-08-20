package cloudFinder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import ij.gui.Roi;
import mserGUI.CovistoMserPanel;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.componenttree.mser.MserTree;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;
import pluginTools.InteractiveCloudify;
import timeGUI.CovistoTimeselectPanel;
import zGUI.CovistoZselectPanel;

public class MSERSeg {

	final InteractiveCloudify parent;
	final JProgressBar jpb;

	public MSERSeg(final InteractiveCloudify parent, final JProgressBar jpb) {

		this.parent = parent;
		this.jpb = jpb;

	}

	public void execute()  {
		
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
			
			if (CovistoMserPanel.darktobright)

				parent.newtree = MserTree.buildMserTree(current, CovistoMserPanel.delta, CovistoMserPanel.minSize, CovistoMserPanel.maxSize,
						CovistoMserPanel.Unstability_Score, CovistoMserPanel.minDiversity, true);

			else

				parent.newtree = MserTree.buildMserTree(current, CovistoMserPanel.delta, CovistoMserPanel.minSize, CovistoMserPanel.maxSize,
						CovistoMserPanel.Unstability_Score, CovistoMserPanel.minDiversity, false);
			

			parent.Rois = utility.FinderUtils.getcurrentRois(parent.newtree);
			
			ArrayList<RoiObject> currentLabelObject = new ArrayList<RoiObject>();
			ArrayList<RoiObject> SeccurrentLabelObject = new ArrayList<RoiObject>();
			
			for(Roi roi : parent.Rois) {
				
				double[] centroid = roi.getContourCentroid();
				
				double Intensity = StaticMethods.getIntensity(parent.CurrentViewOrig, roi);
				
				double SecIntensity = StaticMethods.getIntensity(parent.CurrentViewSecOrig, roi);
				
				double numPixels = StaticMethods.getNumberofPixels(parent.CurrentViewOrig, roi);
				
				double meanIntensity = Intensity / numPixels;
				
				double SecmeanIntensity = SecIntensity / numPixels;
				
				RoiObject currentRoiobject = new RoiObject(roi, centroid, meanIntensity, Intensity, numPixels);
				
				currentLabelObject.add(currentRoiobject);
				
				 RoiObject SeccurrentRoiobject = new RoiObject(roi, centroid, SecmeanIntensity, SecIntensity, numPixels);
	                
	             SeccurrentLabelObject.add(SeccurrentRoiobject);
				
	            roi.setStrokeColor(parent.colorDrawMser);
				parent.overlay.add(roi);
				
			}
			
			MeasureProperties CloudandCell = new MeasureProperties(parent, currentLabelObject, label);
			Allclouds.addAll(CloudandCell.GetCurrentCloud());
			
			MeasureProperties SecCloudandCell = new MeasureProperties(parent, SeccurrentLabelObject, label);
			SecAllclouds.addAll(SecCloudandCell.GetCurrentCloud());
			
		}
		
		
		  parent.AllClouds.put(uniqueID, Allclouds);
		  parent.AllCloudsChannelTwo.put(uniqueID, SecAllclouds);

		

		
			parent.imp.setOverlay(parent.overlay);
			parent.imp.updateAndDraw();
			
			
			
			
	}

	

	

}
