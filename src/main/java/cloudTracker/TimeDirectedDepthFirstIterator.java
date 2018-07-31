package cloudTracker;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;

import cloudFinder.CloudObject;



public class TimeDirectedDepthFirstIterator extends SortedDepthFirstIterator<CloudObject, DefaultWeightedEdge> {

	public TimeDirectedDepthFirstIterator(Graph<CloudObject, DefaultWeightedEdge> g, CloudObject startVertex) {
		super(g, startVertex, null);
	}
	
	
	
    protected void addUnseenChildrenOf(CloudObject vertex) {
    	
    	int ts = vertex.getFeature(CloudObject.ThirdDimension).intValue();
        for (DefaultWeightedEdge edge : specifics.edgesOf(vertex)) {
            if (nListeners != 0) {
                fireEdgeTraversed(createEdgeTraversalEvent(edge));
            }

            CloudObject oppositeV = Graphs.getOppositeVertex(graph, edge, vertex);
            int tt = oppositeV.getFeature(CloudObject.ThirdDimension).intValue();
            if (tt <= ts) {
            	continue;
            }

            if ( seen.containsKey(oppositeV)) {
                encounterVertexAgain(oppositeV, edge);
            } else {
                encounterVertex(oppositeV, edge);
            }
        }
    }

	
	
}
