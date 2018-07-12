package cloudFinder;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import ij.gui.Roi;
import interactivePreprocessing.InteractiveMethods;
import interactivePreprocessing.InteractiveMethods.ValueChange;
import mserGUI.CovistoMserPanel;
import net.imglib2.algorithm.componenttree.mser.MserTree;
import net.imglib2.util.Pair;
import pluginTools.InteractiveCloudify;
import timeGUI.CovistoTimeselectPanel;
import utility.PreRoiobject;
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
		if (CovistoMserPanel.darktobright)

			parent.newtree = MserTree.buildMserTree(parent.newimg, CovistoMserPanel.delta, CovistoMserPanel.minSize, CovistoMserPanel.maxSize,
					CovistoMserPanel.Unstability_Score, CovistoMserPanel.minDiversity, true);

		else

			parent.newtree = MserTree.buildMserTree(parent.newimg, CovistoMserPanel.delta, CovistoMserPanel.minSize, CovistoMserPanel.maxSize,
					CovistoMserPanel.Unstability_Score, CovistoMserPanel.minDiversity, false);
		 parent.overlay.clear();
			parent.Rois = utility.FinderUtils.getcurrentRois(parent.newtree);

			parent.CurrentCloudobject = new ArrayList<CloudObject>();
			ArrayList<double[]> centerRoi = utility.FinderUtils.getRoiMean(parent.newtree);
			
			
			
			
			for (int index = 0; index < centerRoi.size(); ++index) {

				Roi or = parent.Rois.get(index);

				or.setStrokeColor(parent.colorDrawMser);
				parent.overlay.add(or);
				
			
			}
			for (Roi currentroi: parent.Rois) {
				
				final double[] geocenter = currentroi.getContourCentroid();
				final Pair<Double, Integer> Intensityandpixels = CloudObject.getIntensity(currentroi, parent.CurrentViewOrig);
				final double intensity = Intensityandpixels.getA();
				final double numberofpixels = Intensityandpixels.getB();
				final double averageintensity = intensity / numberofpixels;
				CloudObject currentobject = new CloudObject(currentroi, geocenter, numberofpixels, intensity, averageintensity, CovistoZselectPanel.thirdDimension, CovistoTimeselectPanel.fourthDimension);
				parent.CurrentCloudobject.add(currentobject);
			}

			for (Map.Entry<String, ArrayList<CloudObject>> entry : parent.ZTRois.entrySet()) {

				ArrayList<CloudObject> current = entry.getValue();
				for (CloudObject currentroi : current) {

					if (currentroi.fourthDimension == CovistoTimeselectPanel.fourthDimension && currentroi.thirdDimension == CovistoZselectPanel.thirdDimension) {

						currentroi.roi.setStrokeColor(parent.colorSnake);
						parent.overlay.add(currentroi.roi);
						
					}

				}
			}
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
