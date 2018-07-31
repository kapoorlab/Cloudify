package cloudFinder;

import java.util.ArrayList;
import java.util.HashMap;

import net.imglib2.algorithm.OutputAlgorithm;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

public interface CloudFinders<T extends RealType<T> & NativeType<T>> extends OutputAlgorithm< HashMap<String, ArrayList<CloudObject>> > {

}
