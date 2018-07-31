package cloudTracker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import cloudFinder.CloudObject;
import net.imglib2.algorithm.MultiThreaded;
import utility.FeatureFilter;


public class CloudObjectCollection implements MultiThreaded
{

	public static final Double ZERO = Double.valueOf( 0d );

	public static final Double ONE = Double.valueOf( 1d );

	public static final String VISIBLITY = "VISIBILITY";

	/**
	 * Time units for filtering and cropping operation timeouts. Filtering
	 * should not take more than 1 minute.
	 */
	private static final TimeUnit TIME_OUT_UNITS = TimeUnit.MINUTES;

	/**
	 * Time for filtering and cropping operation timeouts. Filtering should not
	 * take more than 1 minute.
	 */
	private static final long TIME_OUT_DELAY = 1;

	/** The Time by Time list of CloudObject this object wrap. */
	private ConcurrentSkipListMap< String, Set< CloudObject > > content = new ConcurrentSkipListMap< >();

	private int numThreads;

	/*
	 * CONSTRUCTORS
	 */

	/**
	 * Construct a new empty CloudObject collection.
	 */
	public CloudObjectCollection()
	{
		setNumThreads();
	}

	/*
	 * METHODS
	 */

	/**
	 * Retrieves and returns the {@link CloudObject} object in this collection with the
	 * specified ID. Returns <code>null</code> if the CloudObject cannot be found. All
	 * ThreeDRoiobjects, visible or not, are searched for.
	 *
	 * @param ID
	 *            the ID to look for.
	 * @return the CloudObject with the specified ID or <code>null</code> if this CloudObject
	 *         does not exist or does not belong to this collection.
	 */
	public CloudObject search( final int ID )
	{
		CloudObject CloudObject = null;
		for ( final CloudObject s : iterable( false ) )
		{
			if ( s.ID() == ID )
			{
				CloudObject = s;
				break;
			}
		}
		return CloudObject;
	}

	@Override
	public String toString()
	{
		String str = super.toString();
		str += ": contains " + getNThreeDRoiobjects( false ) + " ThreeDRoiobjects total in " + keySet().size() + " different Times, over which " + getNThreeDRoiobjects( true ) + " are visible:\n";
		for ( final String key : content.keySet() )
		{
			str += "\tTime " + key + ": " + getNThreeDRoiobjects( key, false ) + " ThreeDRoiobjects total, " + getNThreeDRoiobjects( key, true ) + " visible.\n";
		}
		return str;
	}

	/**
	 * Adds the given CloudObject to this collection, at the specified Time, and mark
	 * it as visible.
	 * <p>
	 * If the Time does not exist yet in the collection, it is created and
	 * added. Upon adding, the added CloudObject has its feature {@link CloudObject#Time}
	 * updated with the passed Time value.
	 * 
	 * @param CloudObject
	 *            the CloudObject to add.
	 * @param Time
	 *            the Time to add it to.
	 */
	public void add( final CloudObject CloudObject, final String Time )
	{
		Set< CloudObject > ThreeDRoiobjects = content.get( Time );
		if ( null == ThreeDRoiobjects )
		{
			ThreeDRoiobjects = new HashSet< >();
			content.put( Time, ThreeDRoiobjects );
		}
		ThreeDRoiobjects.add( CloudObject );
		CloudObject.putFeature( CloudObject.ThirdDimension, Double.valueOf( Time ) );
		CloudObject.putFeature( VISIBLITY, ONE );
	}

	/**
	 * Removes the given CloudObject from this collection, at the specified Time.
	 * <p>
	 * If the CloudObject Time collection does not exist yet, nothing is done and
	 * <code>false</code> is returned. If the CloudObject cannot be found in the Time
	 * content, nothing is done and <code>false</code> is returned.
	 * 
	 * @param CloudObject
	 *            the CloudObject to remove.
	 * @param Time
	 *            the Time to remove it from.
	 * @return <code>true</code> if the CloudObject was succesfully removed.
	 */
	public boolean remove( final CloudObject CloudObject, final Integer Time )
	{
		final Set< CloudObject > ThreeDRoiobjects = content.get( Time );
		if ( null == ThreeDRoiobjects ) { return false; }
		return ThreeDRoiobjects.remove( CloudObject );
	}

	/**
	 * Marks all the content of this collection as visible or invisible.
	 *
	 * @param visible
	 *            if true, all ThreeDRoiobjects will be marked as visible.
	 */
	public void setVisible( final boolean visible )
	{
		final Double val = visible ? ONE : ZERO;
		final Collection< String > Times = content.keySet();

		final ExecutorService executors = Executors.newFixedThreadPool( numThreads );
		for ( final String Time : Times )
		{

			final Runnable command = new Runnable()
			{
				@Override
				public void run()
				{

					final Set< CloudObject > ThreeDRoiobjects = content.get( Time );
					for ( final CloudObject CloudObject : ThreeDRoiobjects )
					{
						CloudObject.putFeature( VISIBLITY, val );
					}

				}
			};
			executors.execute( command );
		}

		executors.shutdown();
		try
		{
			final boolean ok = executors.awaitTermination( TIME_OUT_DELAY, TIME_OUT_UNITS );
			if ( !ok )
			{
				System.err.println( "[ThreeDRoiobjectCollection.setVisible()] Timeout of " + TIME_OUT_DELAY + " " + TIME_OUT_UNITS + " reached." );
			}
		}
		catch ( final InterruptedException e )
		{
			e.printStackTrace();
		}
	}

	/**
	 * Filters out the content of this collection using the specified
	 * {@link FeatureFilter}. ThreeDRoiobjects that are filtered out are marked as
	 * invisible, and visible otherwise.
	 *
	 * @param featurefilter
	 *            the filter to use.
	 */
	public final void filter( final FeatureFilter featurefilter )
	{

		final Collection< String > Times = content.keySet();
		final ExecutorService executors = Executors.newFixedThreadPool( numThreads );

		for ( final String Time : Times )
		{

			final Runnable command = new Runnable()
			{
				@Override
				public void run()
				{

					Double val, tval;

					final Set< CloudObject > ThreeDRoiobjects = content.get( Time );
					tval = featurefilter.value;

					if ( featurefilter.isAbove )
					{

						for ( final CloudObject CloudObject : ThreeDRoiobjects )
						{
							val = CloudObject.getFeature( featurefilter.feature );
							if ( val.compareTo( tval ) < 0 )
							{
								CloudObject.putFeature( VISIBLITY, ZERO );
							}
							else
							{
								CloudObject.putFeature( VISIBLITY, ONE );
							}
						}

					}
					else
					{

						for ( final CloudObject CloudObject : ThreeDRoiobjects )
						{
							val = CloudObject.getFeature( featurefilter.feature );
							if ( val.compareTo( tval ) > 0 )
							{
								CloudObject.putFeature( VISIBLITY, ZERO );
							}
							else
							{
								CloudObject.putFeature( VISIBLITY, ONE );
							}
						}
					}
				}
			};
			executors.execute( command );
		}

		executors.shutdown();
		try
		{
			final boolean ok = executors.awaitTermination( TIME_OUT_DELAY, TIME_OUT_UNITS );
			if ( !ok )
			{
				System.err.println( "[ThreeDRoiobjectCollection.filter()] Timeout of " + TIME_OUT_DELAY + " " + TIME_OUT_UNITS + " reached while filtering." );
			}
		}
		catch ( final InterruptedException e )
		{
			e.printStackTrace();
		}
	}

	/**
	 * Filters out the content of this collection using the specified
	 * {@link FeatureFilter} collection. ThreeDRoiobjects that are filtered out are marked
	 * as invisible, and visible otherwise. To be marked as visible, a CloudObject must
	 * pass <b>all</b> of the specified filters (AND chaining).
	 *
	 * @param filters
	 *            the filter collection to use.
	 */
	public final void filter( final Collection< FeatureFilter > filters )
	{

		final Collection< String > Times = content.keySet();
		final ExecutorService executors = Executors.newFixedThreadPool( numThreads );

		for ( final String Time : Times )
		{
			final Runnable command = new Runnable()
			{
				@Override
				public void run()
				{
					final Set< CloudObject > ThreeDRoiobjects = content.get( Time );

					Double val, tval;
					boolean isAbove, shouldNotBeVisible;
					for ( final CloudObject CloudObject : ThreeDRoiobjects )
					{

						shouldNotBeVisible = false;
						for ( final FeatureFilter featureFilter : filters )
						{

							val = CloudObject.getFeature( featureFilter.feature );
							tval = featureFilter.value;
							isAbove = featureFilter.isAbove;

							if ( isAbove && val.compareTo( tval ) < 0 || !isAbove && val.compareTo( tval ) > 0 )
							{
								shouldNotBeVisible = true;
								break;
							}
						} // loop over filters

						if ( shouldNotBeVisible )
						{
							CloudObject.putFeature( VISIBLITY, ZERO );
						}
						else
						{
							CloudObject.putFeature( VISIBLITY, ONE );
						}
					} // loop over ThreeDRoiobjects

				}

			};
			executors.execute( command );
		}

		executors.shutdown();
		try
		{
			final boolean ok = executors.awaitTermination( TIME_OUT_DELAY, TIME_OUT_UNITS );
			if ( !ok )
			{
				System.err.println( "[ThreeDRoiobjectCollection.filter()] Timeout of " + TIME_OUT_DELAY + " " + TIME_OUT_UNITS + " reached while filtering." );
			}
		}
		catch ( final InterruptedException e )
		{
			e.printStackTrace();
		}
	}

	/**
	 * Returns the closest {@link CloudObject} to the given location (encoded as a
	 * CloudObject), contained in the Time <code>Time</code>. If the Time has no
	 * CloudObject, return <code>null</code>.
	 *
	 * @param location
	 *            the location to search for.
	 * @param Time
	 *            the Time to inspect.
	 * @param visibleThreeDRoiobjectsOnly
	 *            if true, will only search though visible ThreeDRoiobjects. If false, will
	 *            search through all ThreeDRoiobjects.
	 * @return the closest CloudObject to the specified location, member of this
	 *         collection.
	 */
	public final CloudObject getClosestThreeDRoiobject( final CloudObject location, final int Time, final boolean visibleThreeDRoiobjectsOnly )
	{
		final Set< CloudObject > ThreeDRoiobjects = content.get( Time );
		if ( null == ThreeDRoiobjects )
			return null;
		double d2;
		double minDist = Double.POSITIVE_INFINITY;
		CloudObject target = null;
		for ( final CloudObject s : ThreeDRoiobjects )
		{

			if ( visibleThreeDRoiobjectsOnly && ( s.getFeature( VISIBLITY ).compareTo( ZERO ) <= 0 ) )
			{
				continue;
			}

			d2 = s.squareDistanceTo( location );
			if ( d2 < minDist )
			{
				minDist = d2;
				target = s;
			}

		}
		return target;
	}

	/**
	 * Returns the {@link CloudObject} at the given location (encoded as a CloudObject),
	 * contained in the Time <code>Time</code>. A CloudObject is returned <b>only</b>
	 * if there exists a CloudObject such that the given location is within the CloudObject
	 * radius. Otherwise <code>null</code> is returned.
	 *
	 * @param location
	 *            the location to search for.
	 * @param Time
	 *            the Time to inspect.
	 * @param visibleThreeDRoiobjectsOnly
	 *            if true, will only search though visible ThreeDRoiobjects. If false, will
	 *            search through all ThreeDRoiobjects.
	 * @return the closest CloudObject such that the specified location is within its
	 *         radius, member of this collection, or <code>null</code> is such a
	 *         ThreeDRoiobjects cannot be found.
	 */
	public final CloudObject getThreeDRoiobjectAt( final CloudObject location, final int Time, final boolean visibleThreeDRoiobjectsOnly )
	{
		final Set< CloudObject > ThreeDRoiobjects = content.get( Time );
		if ( null == ThreeDRoiobjects || ThreeDRoiobjects.isEmpty() ) { return null; }

		final TreeMap< Double, CloudObject > distanceToThreeDRoiobject = new TreeMap< >();
		double d2;
		for ( final CloudObject s : ThreeDRoiobjects )
		{
			if ( visibleThreeDRoiobjectsOnly && ( s.getFeature( VISIBLITY ).compareTo( ZERO ) <= 0 ) )
				continue;

			d2 = s.squareDistanceTo( location );
				distanceToThreeDRoiobject.put( d2, s );
		}
		if ( distanceToThreeDRoiobject.isEmpty() )
			return null;

		return distanceToThreeDRoiobject.firstEntry().getValue();
	}
	
	/**
	 * Returns the <code>n</code> closest {@link CloudObject} to the given location
	 * (encoded as a CloudObject), contained in the Time <code>Time</code>. If the
	 * number of ThreeDRoiobjects in the Time is exhausted, a shorter list is returned.
	 * <p>
	 * The list is ordered by increasing distance to the given location.
	 *
	 * @param location
	 *            the location to search for.
	 * @param Time
	 *            the Time to inspect.
	 * @param n
	 *            the number of ThreeDRoiobjects to search for.
	 * @param visibleThreeDRoiobjectsOnly
	 *            if true, will only search though visible ThreeDRoiobjects. If false, will
	 *            search through all ThreeDRoiobjects.
	 * @return a new list, with of at most <code>n</code> ThreeDRoiobjects, ordered by
	 *         increasing distance from the specified location.
	 */
	public final List< CloudObject > getNClosestThreeDRoiobjects( final CloudObject location, final int Time, int n, final boolean visibleThreeDRoiobjectsOnly )
	{
		final Set< CloudObject > ThreeDRoiobjects = content.get( Time );
		final TreeMap< Double, CloudObject > distanceToThreeDRoiobject = new TreeMap< >();

		double d2;
		for ( final CloudObject s : ThreeDRoiobjects )
		{

			if ( visibleThreeDRoiobjectsOnly && ( s.getFeature( VISIBLITY ).compareTo( ZERO ) <= 0 ) )
			{
				continue;
			}

			d2 = s.squareDistanceTo( location );
			distanceToThreeDRoiobject.put( d2, s );
		}

		final List< CloudObject > selectedThreeDRoiobjects = new ArrayList< >( n );
		final Iterator< Double > it = distanceToThreeDRoiobject.keySet().iterator();
		while ( n > 0 && it.hasNext() )
		{
			selectedThreeDRoiobjects.add( distanceToThreeDRoiobject.get( it.next() ) );
			n--;
		}
		return selectedThreeDRoiobjects;
	}

	/**
	 * Returns the total number of ThreeDRoiobjects in this collection, over all Times.
	 *
	 * @param visibleThreeDRoiobjectsOnly
	 *            if true, will only count visible ThreeDRoiobjects. If false count all
	 *            ThreeDRoiobjects.
	 * @return the total number of ThreeDRoiobjects in this collection.
	 */
	public final int getNThreeDRoiobjects( final boolean visibleThreeDRoiobjectsOnly )
	{
		int nThreeDRoiobjects = 0;
		if ( visibleThreeDRoiobjectsOnly )
		{

			final Iterator< CloudObject > it = iterator( true );
			while ( it.hasNext() )
			{
				it.next();
				nThreeDRoiobjects++;
			}

		}
		else
		{

			for ( final Set< CloudObject > ThreeDRoiobjects : content.values() )
				nThreeDRoiobjects += ThreeDRoiobjects.size();
		}
		return nThreeDRoiobjects;
	}

	/**
	 * Returns the number of ThreeDRoiobjects at the given Time.
	 *
	 * @param Time
	 *            the Time.
	 * @param visibleThreeDRoiobjectsOnly
	 *            if true, will only count visible ThreeDRoiobjects. If false count all
	 *            ThreeDRoiobjects.
	 * @return the number of ThreeDRoiobjects at the given Time.
	 */
	public int getNThreeDRoiobjects( final String Time, final boolean visibleThreeDRoiobjectsOnly )
	{
		if ( visibleThreeDRoiobjectsOnly )
		{
			final Iterator< CloudObject > it = iterator( Time, true );
			int nThreeDRoiobjects = 0;
			while ( it.hasNext() )
			{
				it.next();
				nThreeDRoiobjects++;
			}
			return nThreeDRoiobjects;
		}

		final Set< CloudObject > ThreeDRoiobjects = content.get( Time );
		if ( null == ThreeDRoiobjects )
			return 0;
		
		return ThreeDRoiobjects.size();
	}

	/*
	 * FEATURES
	 */

	/**
	 * Builds and returns a new map of feature values for this CloudObject collection.
	 * Each feature maps a double array, with 1 element per {@link CloudObject}, all
	 * pooled together.
	 *
	 * @param features
	 *            the features to collect
	 * @param visibleOnly
	 *            if <code>true</code>, only the visible CloudObject values will be
	 *            collected.
	 * @return a new map instance.
	 */
	public Map< String, double[] > collectValues( final Collection< String > features, final boolean visibleOnly )
	{
		final Map< String, double[] > featureValues = new ConcurrentHashMap< >( features.size() );
		final ExecutorService executors = Executors.newFixedThreadPool( numThreads );

		for ( final String feature : features )
		{
			final Runnable command = new Runnable()
			{
				@Override
				public void run()
				{
					final double[] values = collectValues( feature, visibleOnly );
					featureValues.put( feature, values );
				}

			};
			executors.execute( command );
		}

		executors.shutdown();
		try
		{
			final boolean ok = executors.awaitTermination( TIME_OUT_DELAY, TIME_OUT_UNITS );
			if ( !ok )
			{
				System.err.println( "[ThreeDRoiobjectCollection.collectValues()] Timeout of " + TIME_OUT_DELAY + " " + TIME_OUT_UNITS + " reached while filtering." );
			}
		}
		catch ( final InterruptedException e )
		{
			e.printStackTrace();
		}

		return featureValues;
	}

	/**
	 * Returns the feature values of this CloudObject collection as a new double array.
	 * <p>
	 * If some ThreeDRoiobjects do not have the interrogated feature set (stored value is
	 * <code>null</code>) or if the value is {@link Double#NaN}, they are
	 * skipped. The returned array might be therefore of smaller size than the
	 * number of ThreeDRoiobjects interrogated.
	 *
	 * @param feature
	 *            the feature to collect.
	 * @param visibleOnly
	 *            if <code>true</code>, only the visible CloudObject values will be
	 *            collected.
	 * @return a new <code>double</code> array.
	 */
	public final double[] collectValues( final String feature, final boolean visibleOnly )
	{
		final double[] values = new double[ getNThreeDRoiobjects( visibleOnly ) ];
		int index = 0;
		for ( final CloudObject CloudObject : iterable( visibleOnly ) )
		{
			final Double feat = CloudObject.getFeature( feature );
			if ( null == feat )
			{
				continue;
			}
			final double val = feat.doubleValue();
			if ( Double.isNaN( val ) )
			{
				continue;
			}
			values[ index ] = val;
			index++;
		}
		return values;
	}

	/*
	 * ITERABLE & co
	 */

	/**
	 * Return an iterator that iterates over all the ThreeDRoiobjects contained in this
	 * collection.
	 *
	 * @param visibleThreeDRoiobjectsOnly
	 *            if true, the returned iterator will only iterate through
	 *            visible ThreeDRoiobjects. If false, it will iterate over all ThreeDRoiobjects.
	 * @return an iterator that iterates over this collection.
	 */
	public Iterator< CloudObject > iterator( final boolean visibleThreeDRoiobjectsOnly )
	{
		if ( visibleThreeDRoiobjectsOnly )
			return new VisibleThreeDRoiobjectsIterator();

		return new AllThreeDRoiobjectsIterator();
	}

	/**
	 * Return an iterator that iterates over the ThreeDRoiobjects in the specified Time.
	 *
	 * @param visibleThreeDRoiobjectsOnly
	 *            if true, the returned iterator will only iterate through
	 *            visible ThreeDRoiobjects. If false, it will iterate over all ThreeDRoiobjects.
	 * @param Time
	 *            the Time to iterate over.
	 * @return an iterator that iterates over the content of a Time of this
	 *         collection.
	 */
	public Iterator< CloudObject > iterator( final String Time, final boolean visibleThreeDRoiobjectsOnly )
	{
		final Set< CloudObject > TimeContent = content.get( Time );
		if ( null == TimeContent ) { return EMPTY_ITERATOR; }
		if ( visibleThreeDRoiobjectsOnly )
			return new VisibleThreeDRoiobjectsTimeIterator( TimeContent );

		return TimeContent.iterator();
	}

	/**
	 * A convenience methods that returns an {@link Iterable} wrapper for this
	 * collection as a whole.
	 *
	 * @param visibleThreeDRoiobjectsOnly
	 *            if true, the iterable will contains only visible ThreeDRoiobjects.
	 *            Otherwise, it will contain all the ThreeDRoiobjects.
	 * @return an iterable view of this CloudObject collection.
	 */
	public Iterable< CloudObject > iterable( final boolean visibleThreeDRoiobjectsOnly )
	{
		return new WholeCollectionIterable( visibleThreeDRoiobjectsOnly );
	}

	/**
	 * A convenience methods that returns an {@link Iterable} wrapper for a
	 * specific Time of this CloudObject collection. The iterable is backed-up by the
	 * actual collection content, so modifying it can have unexpected results.
	 *
	 * @param visibleThreeDRoiobjectsOnly
	 *            if true, the iterable will contains only visible ThreeDRoiobjects of the
	 *            specified Time. Otherwise, it will contain all the ThreeDRoiobjects of
	 *            the specified Time.
	 * @param Time
	 *            the Time of the content the returned iterable will wrap.
	 * @return an iterable view of the content of a single Time of this CloudObject
	 *         collection.
	 */
	public Iterable< CloudObject > iterable( final int Time, final boolean visibleThreeDRoiobjectsOnly )
	{
		if ( visibleThreeDRoiobjectsOnly )
			return new TimeVisibleIterable( Time );

		return content.get( Time );
	}

	/*
	 * SORTEDMAP
	 */

	/**
	 * Stores the specified ThreeDRoiobjects as the content of the specified Time. The
	 * added ThreeDRoiobjects are all marked as not visible. Their {@link CloudObject#Time} is
	 * updated to be the specified Time.
	 *
	 * @param Time
	 *            the Time to store these ThreeDRoiobjects at. The specified ThreeDRoiobjects replace
	 *            the previous content of this Time, if any.
	 * @param ThreeDRoiobjects
	 *            the ThreeDRoiobjects to store.
	 */
	public void put( final String Time, final Collection< CloudObject > ThreeDRoiobjects )
	{
		final Set< CloudObject > value = new HashSet< >( ThreeDRoiobjects );
		for ( final CloudObject CloudObject : value )
		{
			CloudObject.putFeature( CloudObject.ThirdDimension, Double.valueOf( Time ) );
			CloudObject.putFeature( VISIBLITY, ZERO );
		}
		content.put( Time, value );
	}

	/**
	 * Returns the first (lowest) Time currently in this collection.
	 *
	 * @return the first (lowest) Time currently in this collection.
	 */
	public String firstKey()
	{
		if ( content.isEmpty() ) { return Integer.toString(0); }
		return content.firstKey();
	}

	/**
	 * Returns the last (highest) Time currently in this collection.
	 *
	 * @return the last (highest) Time currently in this collection.
	 */
	public String lastKey()
	{
		if ( content.isEmpty() ) { return Integer.toString(0); }
		return content.lastKey();
	}

	/**
	 * Returns a NavigableSet view of the Times contained in this collection.
	 * The set's iterator returns the keys in ascending order. The set is backed
	 * by the map, so changes to the map are reflected in the set, and
	 * vice-versa. The set supports element removal, which removes the
	 * corresponding mapping from the map, via the Iterator.remove, Set.remove,
	 * removeAll, retainAll, and clear operations. It does not support the add
	 * or addAll operations.
	 * <p>
	 * The view's iterator is a "weakly consistent" iterator that will never
	 * throw ConcurrentModificationException, and guarantees to traverse
	 * elements as they existed upon construction of the iterator, and may (but
	 * is not guaranteed to) reflect any modifications subsequent to
	 * construction.
	 *
	 * @return a navigable set view of the Times in this collection.
	 */
	public NavigableSet< String > keySet()
	{
		return content.keySet();
	}

	/**
	 * Removes all the content from this collection.
	 */
	public void clear()
	{
		content.clear();
	}

	/*
	 * MULTITHREADING
	 */

	@Override
	public void setNumThreads()
	{
		this.numThreads = Runtime.getRuntime().availableProcessors();
	}

	@Override
	public void setNumThreads( final int numThreads )
	{
		this.numThreads = numThreads;
	}

	@Override
	public int getNumThreads()
	{
		return numThreads;
	}

	/*
	 * PRIVATE CLASSES
	 */

	private class AllThreeDRoiobjectsIterator implements Iterator< CloudObject >
	{

		private boolean hasNext = true;

		private final Iterator< String > TimeIterator;

		private Iterator< CloudObject > contentIterator;

		private CloudObject next = null;

		public AllThreeDRoiobjectsIterator()
		{
			this.TimeIterator = content.keySet().iterator();
			if ( !TimeIterator.hasNext() )
			{
				hasNext = false;
				return;
			}
			final Set< CloudObject > currentTimeContent = content.get( TimeIterator.next() );
			contentIterator = currentTimeContent.iterator();
			iterate();
		}

		private void iterate()
		{
			while ( true )
			{

				// Is there still ThreeDRoiobjects in current content?
				if ( !contentIterator.hasNext() )
				{
					// No. Then move to next Time.
					// Is there still Times to iterate over?
					if ( !TimeIterator.hasNext() )
					{
						// No. Then we are done
						hasNext = false;
						next = null;
						return;
					}
					
					contentIterator = content.get( TimeIterator.next() ).iterator();
					continue;
				}
				next = contentIterator.next();
				return;
			}
		}

		@Override
		public boolean hasNext()
		{
			return hasNext;
		}

		@Override
		public CloudObject next()
		{
			final CloudObject toReturn = next;
			iterate();
			return toReturn;
		}

		@Override
		public void remove()
		{
			throw new UnsupportedOperationException( "Remove operation is not supported for ThreeDRoiobjectCollection iterators." );
		}

	}

	private class VisibleThreeDRoiobjectsIterator implements Iterator< CloudObject >
	{

		private boolean hasNext = true;

		private final Iterator< String > TimeIterator;

		private Iterator< CloudObject > contentIterator;

		private CloudObject next = null;

		private Set< CloudObject > currentTimeContent;

		public VisibleThreeDRoiobjectsIterator()
		{
			this.TimeIterator = content.keySet().iterator();
			if ( !TimeIterator.hasNext() )
			{
				hasNext = false;
				return;
			}
			currentTimeContent = content.get( TimeIterator.next() );
			contentIterator = currentTimeContent.iterator();
			iterate();
		}

		private void iterate()
		{

			while ( true )
			{
				// Is there still ThreeDRoiobjects in current content?
				if ( !contentIterator.hasNext() )
				{
					// No. Then move to next Time.
					// Is there still Times to iterate over?
					if ( !TimeIterator.hasNext() )
					{
						// No. Then we are done
						hasNext = false;
						next = null;
						return;
					}
					
					// Yes. Then start iterating over the next Time.
					currentTimeContent = content.get( TimeIterator.next() );
					contentIterator = currentTimeContent.iterator();
					continue;
				}
				next = contentIterator.next();
				// Is it visible?
				if ( next.getFeature( VISIBLITY ).compareTo( ZERO ) > 0 )
				{
					// Yes! Be happy and return
					return;
				}
			}
		}

		@Override
		public boolean hasNext()
		{
			return hasNext;
		}

		@Override
		public CloudObject next()
		{
			final CloudObject toReturn = next;
			iterate();
			return toReturn;
		}

		@Override
		public void remove()
		{
			throw new UnsupportedOperationException( "Remove operation is not supported for ThreeDRoiobjectCollection iterators." );
		}

	}

	private class VisibleThreeDRoiobjectsTimeIterator implements Iterator< CloudObject >
	{

		private boolean hasNext = true;

		private CloudObject next = null;

		private final Iterator< CloudObject > contentIterator;

		public VisibleThreeDRoiobjectsTimeIterator( final Set< CloudObject > TimeContent )
		{
			if ( null == TimeContent )
			{
				this.contentIterator = EMPTY_ITERATOR;
			}
			else
			{
				this.contentIterator = TimeContent.iterator();
			}
			iterate();
		}

		private void iterate()
		{
			while ( true )
			{
				if ( !contentIterator.hasNext() )
				{
					// No. Then we are done
					hasNext = false;
					next = null;
					return;
				}
				next = contentIterator.next();
				// Is it visible?
				if ( next.getFeature( VISIBLITY ).compareTo( ZERO ) > 0 )
				{
					// Yes. Be happy, and return.
					return;
				}
			}
		}

		@Override
		public boolean hasNext()
		{
			return hasNext;
		}

		@Override
		public CloudObject next()
		{
			final CloudObject toReturn = next;
			iterate();
			return toReturn;
		}

		@Override
		public void remove()
		{
			throw new UnsupportedOperationException( "Remove operation is not supported for ThreeDRoiobjectCollection iterators." );
		}

	}

	/**
	 * Returns a new {@link ThreeDRoiobjectCollection}, made of only the ThreeDRoiobjects marked as
	 * visible. All the ThreeDRoiobjects will then be marked as not-visible.
	 *
	 * @return a new CloudObject collection, made of only the ThreeDRoiobjects marked as visible.
	 */
	public CloudObjectCollection crop()
	{
		final CloudObjectCollection ns = new CloudObjectCollection();
		ns.setNumThreads( numThreads );

		final Collection< String > Times = content.keySet();
		final ExecutorService executors = Executors.newFixedThreadPool( numThreads );
		for ( final String Time : Times )
		{

			final Runnable command = new Runnable()
			{
				@Override
				public void run()
				{
					final Set< CloudObject > fc = content.get( Time );
					final Set< CloudObject > nfc = new HashSet< >( getNThreeDRoiobjects( Time, true ) );

					for ( final CloudObject CloudObject : fc )
					{
						if ( CloudObject.getFeature( VISIBLITY ).compareTo( ZERO ) > 0 )
						{
							nfc.add( CloudObject );
							CloudObject.putFeature( VISIBLITY, ZERO );
						}
					}
					ns.content.put( Time, nfc );
				}
			};
			executors.execute( command );
		}

		executors.shutdown();
		try
		{
			final boolean ok = executors.awaitTermination( TIME_OUT_DELAY, TIME_OUT_UNITS );
			if ( !ok )
			{
				System.err.println( "[ThreeDRoiobjectCollection.crop()] Timeout of " + TIME_OUT_DELAY + " " + TIME_OUT_UNITS + " reached while cropping." );
			}
		}
		catch ( final InterruptedException e )
		{
			e.printStackTrace();
		}
		return ns;
	}

	/**
	 * A convenience wrapper that implements {@link Iterable} for this CloudObject
	 * collection.
	 */
	private final class WholeCollectionIterable implements Iterable< CloudObject >
	{

		private final boolean visibleThreeDRoiobjectsOnly;

		public WholeCollectionIterable( final boolean visibleThreeDRoiobjectsOnly )
		{
			this.visibleThreeDRoiobjectsOnly = visibleThreeDRoiobjectsOnly;
		}

		@Override
		public Iterator< CloudObject > iterator()
		{
			if ( visibleThreeDRoiobjectsOnly )
				return new VisibleThreeDRoiobjectsIterator();

			return new AllThreeDRoiobjectsIterator();
		}
	}

	/**
	 * A convenience wrapper that implements {@link Iterable} for this CloudObject
	 * collection.
	 */
	private final class TimeVisibleIterable implements Iterable< CloudObject >
	{

		private final int Time;

		public TimeVisibleIterable( final int Time )
		{
			this.Time = Time;
		}

		@Override
		public Iterator< CloudObject > iterator()
		{
			return new VisibleThreeDRoiobjectsTimeIterator( content.get( Time ) );
		}
	}

	private static final Iterator< CloudObject > EMPTY_ITERATOR = new Iterator< CloudObject >()
	{

		@Override
		public boolean hasNext()
		{
			return false;
		}

		@Override
		public CloudObject next()
		{
			return null;
		}

		@Override
		public void remove()
		{}
	};

	/*
	 * STATIC METHODS
	 */

	/**
	 * Creates a new {@link ThreeDRoiobjectCollection} containing only the specified ThreeDRoiobjects.
	 * Their Time origin is retrieved from their {@link CloudObject#Time} feature, so
	 * it must be set properly for all ThreeDRoiobjects. All the ThreeDRoiobjects of the new
	 * collection have the same visibility that the one they carry.
	 *
	 * @param ThreeDRoiobjects
	 *            the CloudObject collection to build from.
	 * @return a new {@link ThreeDRoiobjectCollection} instance.
	 */
	public static CloudObjectCollection fromCollection( final Iterable< CloudObject > ThreeDRoiobjects )
	{
		final CloudObjectCollection sc = new CloudObjectCollection();
		for ( final CloudObject CloudObject : ThreeDRoiobjects )
		{
			final String Time = Double.toString(CloudObject.getFeature( CloudObject.ThirdDimension )) + Double.toString(CloudObject.getFeature( CloudObject.FourthDimension ))  ;
			Set< CloudObject > fc = sc.content.get( Time );
			if ( null == fc )
			{
				fc = new HashSet< >();
				sc.content.put( Time, fc );
			}
			fc.add( CloudObject );
		}
		return sc;
	}

	/**
	 * Creates a new {@link ThreeDRoiobjectCollection} from a copy of the specified map of
	 * sets. The ThreeDRoiobjects added this way are completely untouched. In particular,
	 * their {@link #VISIBLITY} feature is left untouched, which makes this
	 * method suitable to de-serialize a {@link ThreeDRoiobjectCollection}.
	 *
	 * @param source
	 *            the map to buidl the CloudObject collection from.
	 * @return a new ThreeDRoiobjectCollection.
	 */
	public static CloudObjectCollection fromMap( final Map< String, Set< CloudObject > > source )
	{
		final CloudObjectCollection sc = new CloudObjectCollection();
		sc.content = new ConcurrentSkipListMap< >( source );
		return sc;
	}
}
