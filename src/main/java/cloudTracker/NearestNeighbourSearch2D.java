package cloudTracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import cloudFinder.CloudObject;
import net.imglib2.KDTree;
import net.imglib2.RealPoint;
import utility.FlagNode;

public class NearestNeighbourSearch2D implements CloudTracker {

	private final HashMap<String, ArrayList<CloudObject>> ALLIntersections;
	private SimpleWeightedGraph<CloudObject, DefaultWeightedEdge> graph;
	protected String errorMessage;
	private final int fourthDimSize;
	private final double maxdistance;
	private HashMap<String, Integer> Accountedframes;
	public final double mindistance;
	public NearestNeighbourSearch2D(final HashMap<String, ArrayList<CloudObject>> ALLIntersections ,
			final int fourthDimSize, final double maxdistance,  HashMap<String, Integer> Accountedframes, double mindistance) {

		this.ALLIntersections = ALLIntersections;
		this.fourthDimSize = fourthDimSize;
		this.maxdistance = maxdistance;
        this.Accountedframes = Accountedframes;
        this.mindistance = mindistance;
	}

	@Override
	public SimpleWeightedGraph<CloudObject, DefaultWeightedEdge> getResult() {

		return graph;
	}

	@Override
	public boolean checkInput() {

		return true;
	}

	@Override
	public boolean process() {
		
		graph = new SimpleWeightedGraph<CloudObject, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		Iterator<Map.Entry<String, Integer>> it = Accountedframes.entrySet().iterator();
		while (it.hasNext()) {

			int t = it.next().getValue();
			while (it.hasNext()) {
				int nextt = it.next().getValue();
			String uniqueID =  Integer.toString(t) + Integer.toString(1);
			String uniqueIDnext =  Integer.toString(nextt) + Integer.toString(1);

			ArrayList<CloudObject> baseobject = ALLIntersections.get(uniqueID);
			ArrayList<CloudObject> targetobject = ALLIntersections.get(uniqueIDnext);

			
			if(targetobject!=null && targetobject.size() > 0) {

			Iterator<CloudObject> baseobjectiterator = baseobject.iterator();

			final int Targetintersections = targetobject.size();

			final List<RealPoint> targetCoords = new ArrayList<RealPoint>(Targetintersections);

			final List<FlagNode<CloudObject>> targetNodes = new ArrayList<FlagNode<CloudObject>>(
					Targetintersections);

			for (int index = 0; index < targetobject.size(); ++index) {

					targetCoords.add(new RealPoint( targetobject.get(index).geometriccenter));
					targetNodes.add(new FlagNode<CloudObject>(targetobject.get(index)));


			}
			if (targetNodes.size() > 0 && targetCoords.size() > 0) {

				final KDTree<FlagNode<CloudObject>> Tree = new KDTree<FlagNode<CloudObject>>(targetNodes,
						targetCoords);

				final NNFlagsearchKDtree<CloudObject> Search = new NNFlagsearchKDtree<CloudObject>(Tree);

				while (baseobjectiterator.hasNext()) {

					final CloudObject source = baseobjectiterator.next();

					
						final RealPoint sourceCoords = new RealPoint(source.geometriccenter);
						Search.search(sourceCoords);
						final double squareDist = Search.getSquareDistance();
					
						final FlagNode<CloudObject> targetNode = Search.getSampler().get();
						

						targetNode.setVisited(true);

						synchronized (graph) {

							graph.addVertex(source);
							graph.addVertex(targetNode.getValue());
							final DefaultWeightedEdge edge = graph.addEdge(source, targetNode.getValue());
							graph.setEdgeWeight(edge, squareDist);

						}

					}

			}
			}
			
			t = nextt;
			}
		}

		return true;
	}

	@Override
	public String getErrorMessage() {

		return errorMessage;
	}

}