package cloudTracker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;

import cloudFinder.CloudObject;





public class TimeDirectedSortedDepthFirstIterator extends SortedDepthFirstIterator<CloudObject, DefaultWeightedEdge> {

	public TimeDirectedSortedDepthFirstIterator(final Graph<CloudObject, DefaultWeightedEdge> g, final CloudObject startVertex, final Comparator<CloudObject> comparator) {
		super(g, startVertex, comparator);
	}



    @Override
	protected void addUnseenChildrenOf(final CloudObject vertex) {

		// Retrieve target vertices, and sort them in a list
		final List< CloudObject > sortedChildren = new ArrayList< CloudObject >();
    	// Keep a map of matching edges so that we can retrieve them in the same order
    	final Map<CloudObject, DefaultWeightedEdge> localEdges = new HashMap<CloudObject, DefaultWeightedEdge>();

    	final int ts = vertex.getFeature(CloudObject.ThirdDimension).intValue();
        for (final DefaultWeightedEdge edge : specifics.edgesOf(vertex)) {

        	final CloudObject oppositeV = Graphs.getOppositeVertex(graph, edge, vertex);
        	final int tt = oppositeV.getFeature(CloudObject.ThirdDimension).intValue();
        	if (tt <= ts) {
        		continue;
        	}

        	if (!seen.containsKey(oppositeV)) {
        		sortedChildren.add(oppositeV);
        	}
        	localEdges.put(oppositeV, edge);
        }

		Collections.sort( sortedChildren, Collections.reverseOrder( comparator ) );
		final Iterator< CloudObject > it = sortedChildren.iterator();
        while (it.hasNext()) {
			final CloudObject child = it.next();

            if (nListeners != 0) {
                fireEdgeTraversed(createEdgeTraversalEvent(localEdges.get(child)));
            }

            if (seen.containsKey(child)) {
                encounterVertexAgain(child, localEdges.get(child));
            } else {
                encounterVertex(child, localEdges.get(child));
            }
        }
    }



}
