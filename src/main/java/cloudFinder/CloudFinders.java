package cloudFinder;

import java.util.ArrayList;

import net.imglib2.algorithm.OutputAlgorithm;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

public interface CloudFinders<T extends RealType<T> & NativeType<T>> extends OutputAlgorithm< ArrayList<CloudObject> > {

}
