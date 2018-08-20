package trackIT;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import cloudFinder.CloudObject;
import cloudFinder.RoiObject;
import cloudTracker.TrackModel;
import ij.ImageStack;
import kalmanTrackListeners.DisplaySelectedTrack;
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

		System.out.println("Making tracks");
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
						System.out.println(id + " " + currentangle.averageintensity);
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
			
			
			CreateTableView(parent);
			DisplaySelectedTrack.Select(parent);
			

		}
	}
	
	
	public void CreateTableView(InteractiveCloudify parent) {
		
		

		parent.resultIntensityA = new ArrayList<Pair<String, double[]>>();
		parent.resultIntensityB = new ArrayList<Pair<String, double[]>>();
		for (Pair<String, CloudObject> currentangle : parent.Tracklist) {
			
				parent.resultIntensityA.add(new ValuePair<String, double[]>(currentangle.getA(),
						new double[] { currentangle.getB().thirdDimension, currentangle.getB().totalintensity }));
				
				
				double cloudintensity = 0;
				
				for (int i= 0; i < currentangle.getB().roiobject.size(); ++i) {
					
					RoiObject roiob = currentangle.getB().roiobject.get(i);
					cloudintensity+=roiob.totalintensity;
				}
				
				parent.resultIntensityB.add(new ValuePair<String, double[]>(currentangle.getA(),
						new double[] { currentangle.getB().thirdDimension, cloudintensity }));
			


		}
		Object[] colnames = new Object[] { "Track Id", "Location X", "Location Y", "Location Z/T", "Mean Intensity" };

		Object[][] rowvalues = new Object[0][colnames.length];

		rowvalues = new Object[parent.Finalresult.size()][colnames.length];

		parent.table = new JTable(rowvalues, colnames);
		parent.row = 0;
		NumberFormat f = NumberFormat.getInstance();
		for (Map.Entry<String, CloudObject> entry : parent.Finalresult.entrySet()) {

			CloudObject currentangle = entry.getValue();
			parent.table.getModel().setValueAt(entry.getKey(), parent.row, 0);
			parent.table.getModel().setValueAt(f.format(currentangle.geometriccenter[0]), parent.row, 1);
			parent.table.getModel().setValueAt(f.format(currentangle.geometriccenter[1]), parent.row, 2);
			parent.table.getModel().setValueAt(f.format(currentangle.thirdDimension), parent.row, 3);
			parent.table.getModel().setValueAt(f.format(currentangle.averageintensity), parent.row, 4);

			parent.row++;

			parent.tablesize = parent.row;
		}

		makeGUI(parent);
		
	}
	
	
	public static void makeGUI(final InteractiveCloudify parent) {

		parent.PanelSelectFile.removeAll();

		parent.table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		parent.scrollPane = new JScrollPane(parent.table);

		parent.scrollPane.getViewport().add(parent.table);
		parent.scrollPane.setAutoscrolls(true);
		parent.PanelSelectFile.add(parent.scrollPane, BorderLayout.CENTER);

		parent.PanelSelectFile.setBorder(parent.selectcell);

	
		
		parent.PanelSelectFile.repaint();
		parent.PanelSelectFile.validate();
		parent.table.repaint();
		parent.table.validate();
		parent.panelSecond.repaint();
		parent.panelSecond.validate();
		parent.Cardframe.repaint();
		parent.Cardframe.validate();

	}
}