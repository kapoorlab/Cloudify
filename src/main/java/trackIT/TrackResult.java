package trackIT;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.swing.SwingWorker;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import cloudFinder.CloudObject;
import cloudTracker.TrackModel;
import ij.ImageStack;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import pluginTools.InteractiveCloudify;
import zGUI.CovistoZselectPanel;

public class TrackResult extends SwingWorker<Void, Void> {

	final InteractiveCloudify parent;

	public TrackResult(final InteractiveCloudify parent) {

		this.parent = parent;
	}

	@Override
	protected Void doInBackground() throws Exception {

		parent.jpb.setIndeterminate(false);
		parent.Cardframe.validate();

		parent.prestack = new ImageStack((int) parent.originalimg.dimension(0), (int) parent.originalimg.dimension(1),
				java.awt.image.ColorModel.getRGBdefault());

		parent.table.removeAll();
		parent.Tracklist.clear();

		TrackingFunctions track = new TrackingFunctions(parent);
		SimpleWeightedGraph<CloudObject, DefaultWeightedEdge> simplegraph = track.Trackfunction();

		// Display Graph results, make table etc
		DisplayGraph(simplegraph);

		return null;
	}

	protected void DisplayGraph(SimpleWeightedGraph<CloudObject, DefaultWeightedEdge> simplegraph) {

		int minid = Integer.MAX_VALUE;
		int maxid = Integer.MIN_VALUE;
		TrackModel model = new TrackModel(simplegraph);
		for (final Integer id : model.trackIDs(true)) {

			if (id > maxid)
				maxid = id;

			if (id < minid)
				minid = id;

		}
		if (minid != Integer.MAX_VALUE) {

			for (final Integer id : model.trackIDs(true)) {

				Comparator<Pair<String, CloudObject>> ThirdDimcomparison = new Comparator<Pair<String, CloudObject>>() {

					@Override
					public int compare(final Pair<String, CloudObject> A, final Pair<String, CloudObject> B) {

						return A.getB().thirdDimension - B.getB().thirdDimension;

					}

				};

			
				model.setName(id, "Track" + id);

				final HashSet<CloudObject> Angleset = model.trackCloudObjects(id);

				Iterator<CloudObject> Angleiter = Angleset.iterator();

				while (Angleiter.hasNext()) {

					CloudObject currentangle = Angleiter.next();
					parent.Tracklist.add(new ValuePair<String, CloudObject>(Integer.toString(id), currentangle));
				}
				Collections.sort(parent.Tracklist, ThirdDimcomparison);

			}

			for (int id = minid; id <= maxid; ++id) {
				CloudObject bestangle = null;
				
				if (model.trackCloudObjects(id) != null) {

					List<CloudObject> sortedList = new ArrayList<CloudObject>(model.trackCloudObjects(id));

					Collections.sort(sortedList, new Comparator<CloudObject>() {

						@Override
						public int compare(CloudObject o1, CloudObject o2) {

							return o1.thirdDimension - o2.thirdDimension;
						}

					});

					Iterator<CloudObject> iterator = sortedList.iterator();

					int count = 0;
					while (iterator.hasNext()) {

						CloudObject currentangle = iterator.next();
						if (count == 0)
							bestangle = currentangle;
						if (parent.originalimg.numDimensions() <= 3) {
							if (currentangle.thirdDimension == CovistoZselectPanel.thirdDimension) {
								bestangle = currentangle;
								count++;
								break;

							}

						}

					}
					parent.Finalresult.put(Integer.toString(id) , bestangle);
					
					
				}

			}

		}
	}
}
