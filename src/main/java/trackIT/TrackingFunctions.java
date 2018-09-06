package trackIT;

import java.util.ArrayList;
import java.util.Map;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import cloudFinder.CloudObject;
import cloudTracker.CloudTrackCostFunction;
import cloudTracker.KFsearch;
import kalmanGUI.CovistoKalmanPanel;
import pluginTools.InteractiveCloudify;
import zGUI.CovistoZselectPanel;

public class TrackingFunctions {

	
	final InteractiveCloudify parent;
	
	public TrackingFunctions(final InteractiveCloudify parent) {
		
		this.parent = parent;
		
	}
	
	
	public SimpleWeightedGraph<CloudObject, DefaultWeightedEdge> Trackfunction() {
		
		parent.UserchosenCostFunction = new CloudTrackCostFunction(CovistoKalmanPanel.alpha, CovistoKalmanPanel.beta);
		

		ArrayList<ArrayList<CloudObject>> colllist = new ArrayList<ArrayList<CloudObject>>();
		parent.AllClouds = hashMapSorter.SortTimeorZ.sortByIntegerInter(parent.AllClouds);
		for (Map.Entry<String, ArrayList<CloudObject>> entry : parent.AllClouds.entrySet()) {

			ArrayList<CloudObject> bloblist = entry.getValue();
			if(bloblist.size() > 0)
			colllist.add(bloblist);

		}

		
		KFsearch Tsearch = new KFsearch(colllist, parent.UserchosenCostFunction,  CovistoKalmanPanel.maxSearchradius ,
				CovistoKalmanPanel.maxSearchradius, 
				CovistoKalmanPanel.maxframegap, parent.AccountedZ, parent.jpb);
		Tsearch.process();
		SimpleWeightedGraph<CloudObject, DefaultWeightedEdge> simplegraph = Tsearch.getResult();

		return simplegraph;
		
		
	}
	
	
	
}
