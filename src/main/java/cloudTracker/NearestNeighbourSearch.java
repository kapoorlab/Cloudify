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
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import utility.FlagNode;

public class NearestNeighbourSearch implements CloudTracker {

	private final HashMap<String, ArrayList<CloudObject>> ALLIntersections;
	private SimpleWeightedGraph<CloudObject, DefaultWeightedEdge> simplegraph;
	private HashMap<String, Integer> Accountedframes;
	protected String errorMessage;
	private final int z;
	private final int fourthDimSize;
	private final double maxdistance;

	public NearestNeighbourSearch(final HashMap<String, ArrayList<CloudObject>> ALLIntersections,
			final int z, final int fourthDimSize, final double maxdistance,
			final HashMap<String, Integer> Accountedframes) {

		this.ALLIntersections = ALLIntersections;
		this.z = z;
		this.fourthDimSize = fourthDimSize;
		this.maxdistance = maxdistance;
		this.Accountedframes = Accountedframes;

	}

	@Override
	public SimpleWeightedGraph<CloudObject, DefaultWeightedEdge> getResult() {

		return simplegraph;
	}

	@Override
	public boolean checkInput() {

		return true;
	}

	@Override
	public boolean process() {


			reset();
			Iterator<Map.Entry<String, Integer>> it = Accountedframes.entrySet().iterator();
			while (it.hasNext()) {

				int t = it.next().getValue();

				while (it.hasNext()) {
					int nextt = it.next().getValue();

					String uniqueID = Integer.toString(z) + Integer.toString(t);
					String uniqueIDnext = Integer.toString(z) + Integer.toString(nextt);
					String Zid = Integer.toString(z);

					ArrayList<CloudObject> baseobject = ALLIntersections.get(uniqueID);
					ArrayList<CloudObject> targetobject = ALLIntersections.get(uniqueIDnext);

					if (targetobject != null && targetobject.size() > 0) {

						Iterator<CloudObject> baseobjectiterator = baseobject.iterator();

						final int Targetintersections = targetobject.size();

						final List<RealPoint> targetCoords = new ArrayList<RealPoint>(Targetintersections);

						final List<FlagNode<CloudObject>> targetNodes = new ArrayList<FlagNode<CloudObject>>(
								Targetintersections);

						for (int index = 0; index < targetobject.size(); ++index) {

							targetCoords.add(new RealPoint(targetobject.get(index).geometriccenter));
							targetNodes.add(new FlagNode<CloudObject>(targetobject.get(index)));

						}

						if (targetNodes.size() > 0 && targetCoords.size() > 0) {

							final KDTree<FlagNode<CloudObject>> Tree = new KDTree<FlagNode<CloudObject>>(
									targetNodes, targetCoords);

							final NNFlagsearchKDtree<CloudObject> Search = new NNFlagsearchKDtree<CloudObject>(
									Tree);

							while (baseobjectiterator.hasNext()) {

								final CloudObject source = baseobjectiterator.next();

								final RealPoint sourceCoords = new RealPoint(source.geometriccenter);
								Search.search(sourceCoords);
								final double squareDist = Search.getSquareDistance();
								final FlagNode<CloudObject> targetNode = Search.getSampler().get();

								targetNode.setVisited(true);

								synchronized (simplegraph) {

									simplegraph.addVertex(source);
									simplegraph.addVertex(targetNode.getValue());
									final DefaultWeightedEdge edge = simplegraph.addEdge(source, targetNode.getValue());
									simplegraph.setEdgeWeight(edge, squareDist);

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

	public void reset() {
		simplegraph = new SimpleWeightedGraph<CloudObject, DefaultWeightedEdge>(DefaultWeightedEdge.class);

			if (Accountedframes.entrySet().iterator().hasNext()) {

				String TID = Integer.toString(Accountedframes.entrySet().iterator().next().getValue());
                 String uniqueID = z + TID;
					if (ALLIntersections.get(uniqueID) != null) {
						final Iterator<CloudObject> it = ALLIntersections.get(uniqueID).iterator();
						
						while (it.hasNext()) {
							simplegraph.addVertex(it.next());
						}
					}
				}
			}


}
