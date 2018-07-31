package cloudFinder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import ij.gui.Roi;
import mserGUI.CovistoMserPanel;
import net.imglib2.algorithm.componenttree.mser.MserTree;
import pluginTools.InteractiveCloudify;
import timeGUI.CovistoTimeselectPanel;
import zGUI.CovistoZselectPanel;

public class MSERSeg extends SwingWorker<Void, Void> {

	final InteractiveCloudify parent;
	final JProgressBar jpb;

	public MSERSeg(final InteractiveCloudify parent, final JProgressBar jpb) {

		this.parent = parent;
		this.jpb = jpb;

	}

	@Override
	protected Void doInBackground() throws Exception {
		
		/**
		 * 
		 * 
		 * Create a watershed object that defines the region of interest, then make a roi object inside that region to get the clouds, finally put it all together as a cloud object
		 * and enumerate it all in a list and a hashmap for that time.
		 */
	
		
		String uniqueID = Integer.toString(CovistoZselectPanel.thirdDimension) + Integer.toString(CovistoTimeselectPanel.fourthDimension);
		
		// Make the watershed object
		
		StaticMethods.GetPixelList(parent, parent.IntSegoriginalimg);
		
		
		Iterator<Integer> setiter = parent.pixellist.iterator();
		 parent.overlay.clear();
		 
		 ArrayList<CloudObject> Allclouds = new ArrayList<CloudObject>();
		while (setiter.hasNext()) {

			int label = setiter.next();

			// Get the region 
			Watershedobject current = Watershedobject.CurrentLabelImage(parent, parent.IntSegoriginalimg, label);
			
			parent.newimg = utility.CovistoSlicer.PREcopytoByteImage(current.source);
			
			if (CovistoMserPanel.darktobright)

				parent.newtree = MserTree.buildMserTree(parent.newimg, CovistoMserPanel.delta, CovistoMserPanel.minSize, CovistoMserPanel.maxSize,
						CovistoMserPanel.Unstability_Score, CovistoMserPanel.minDiversity, true);

			else

				parent.newtree = MserTree.buildMserTree(parent.newimg, CovistoMserPanel.delta, CovistoMserPanel.minSize, CovistoMserPanel.maxSize,
						CovistoMserPanel.Unstability_Score, CovistoMserPanel.minDiversity, false);
			

			parent.Rois = utility.FinderUtils.getcurrentRois(parent.newtree);
			
			ArrayList<RoiObject> currentLabelObject = new ArrayList<RoiObject>();
			for(Roi roi : parent.Rois) {
				
				double[] centroid = roi.getContourCentroid();
				
				double Intensity = StaticMethods.getIntensity(parent.CurrentViewOrig, roi);
				
				double numPixels = StaticMethods.getNumberofPixels(parent.CurrentViewOrig, roi);
				
				double meanIntensity = Intensity / numPixels;
				
				RoiObject currentRoiobject = new RoiObject(roi, centroid, meanIntensity, Intensity, numPixels);
				
				currentLabelObject.add(currentRoiobject);
				

				roi.setStrokeColor(parent.colorDrawMser);
				parent.overlay.add(roi);
				
				
			}
			
			
			CloudObject currentCloud = new CloudObject(parent.IntSegoriginalimg, currentLabelObject, current.centroid, current.NumPixels, current.totalIntensity, current.meanIntensity,
					CovistoZselectPanel.thirdDimension, CovistoTimeselectPanel.fourthDimension, label);
			Allclouds.add(currentCloud);
			
		}
		
		
		  parent.ZTRois.put(uniqueID, Allclouds);
		

		

		
			parent.imp.setOverlay(parent.overlay);
			parent.imp.updateAndDraw();
			
			
			
				utility.CovsitoProgressBar.CovistoSetProgressBar(jpb, "Done");
			
		return null;
	}

	@Override
	protected void done() {
		
       
		
		try {
			get();
		} catch (InterruptedException e) {

		} catch (ExecutionException e) {

		}

	}

}
