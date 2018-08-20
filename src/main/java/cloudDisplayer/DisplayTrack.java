package cloudDisplayer;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;

import org.jgrapht.graph.DefaultWeightedEdge;

import cloudFinder.CloudObject;
import cloudTracker.TrackModel;
import fiji.tool.SliceListener;
import fiji.tool.SliceObserver;
import ij.ImagePlus;
import ij.gui.Line;
import ij.gui.Overlay;
import ij.gui.TextRoi;
import net.imglib2.img.display.imagej.ImageJFunctions;
import pluginTools.InteractiveCloudify;

public class DisplayTrack {

	public TrackModel model;

	public InteractiveCloudify parent;

	public final int ndims;

	public DisplayTrack(final InteractiveCloudify parent, final TrackModel model) {

		this.parent = parent;

		this.model = model;

		parent.resultimp = ImageJFunctions.show(parent.originalimg);

		ndims = parent.resultimp.getNDimensions();
		
		new SliceObserver(parent.resultimp, new ImagePlusListener());
	}

	public ImagePlus getImp() {
		return parent.resultimp;
	}

	protected class ImagePlusListener implements SliceListener {
		@Override
		public void sliceChanged(ImagePlus arg0) {


		
			parent.resultimp.show();
			Overlay o = parent.resultimp.getOverlay();

			if (getImp().getOverlay() == null) {
				o = new Overlay();
				getImp().setOverlay(o);
			}

			o.clear();
			getImp().getOverlay().clear();

			String ID = (String) parent.table.getValueAt(parent.row, 0);
			int id = Integer.valueOf(ID);

				// Get the corresponding set for each id
				final HashSet<CloudObject> Snakeset = model.trackCloudObjects(id);
				ArrayList<CloudObject> list = new ArrayList<CloudObject>();

				Comparator<CloudObject> ThirdDimcomparison = new Comparator<CloudObject>() {

					@Override
					public int compare(final CloudObject A, final CloudObject B) {

						return A.thirdDimension - B.thirdDimension;

					}

				};

				Comparator<CloudObject> FourthDimcomparison = new Comparator<CloudObject>() {

					@Override
					public int compare(final CloudObject A, final CloudObject B) {

						return A.fourthDimension - B.fourthDimension;

					}

				};

				Iterator<CloudObject> Snakeiter = Snakeset.iterator();
				while (Snakeiter.hasNext()) {

					CloudObject currentsnake = Snakeiter.next();

					for (int d = 0; d < ndims - 1; ++d)
						if (currentsnake.geometriccenter[d] != Double.NaN)
							list.add(currentsnake);

				}
				Collections.sort(list, ThirdDimcomparison);
				if (parent.resultimp.getNDimensions() > 3)
					Collections.sort(list, FourthDimcomparison);

				for (DefaultWeightedEdge e : model.edgeSet()) {

					CloudObject Spotbase = model.getEdgeSource(e);
					CloudObject Spottarget = model.getEdgeTarget(e);

					final double[] startedge = new double[ndims];
					final double[] targetedge = new double[ndims];
					for (int d = 0; d < ndims - 1; ++d) {

						startedge[d] = Spotbase.geometriccenter[d];

						targetedge[d] = Spottarget.geometriccenter[d];

					}
					
					if(model.trackIDOf(Spotbase) == id) {
						TextRoi newellipse = new TextRoi(list.get(0).geometriccenter[0], list.get(0).geometriccenter[1], "TrackID: " + id );

						o.add(newellipse);
						o.drawLabels(true);

						o.drawNames(true);

						Line newline = new Line(startedge[0], startedge[1], targetedge[0], targetedge[1]);
						newline.setStrokeColor(Color.GREEN);
						newline.setStrokeWidth(2);

						o.add(newline);

					}

				
				}
			

			parent.resultimp.updateAndDraw();
		}
	}

}
