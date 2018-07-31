package cloudTracker;
import java.util.HashMap;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import cloudFinder.CloudObject;
import net.imglib2.algorithm.OutputAlgorithm;
import net.imglib2.util.Pair;

public interface CloudTracker extends OutputAlgorithm< SimpleWeightedGraph< CloudObject, DefaultWeightedEdge >> {
	
	
		
		

}
