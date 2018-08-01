package hashMapSorter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cloudFinder.CloudObject;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;

public class SortTimeorZ {

	
	/**
	 * Sort Z or T hashmap by comparing the order in Z or T
	 * 
	 * @param map
	 * @return
	 */
	public static HashMap<String, Integer> sortByValues(HashMap<String, Integer> map) {
		List<Entry<String, Integer>> list = new LinkedList<Entry<String, Integer>>(map.entrySet());
		// Defined Custom Comparator here
		Collections.sort(list, new Comparator<Entry<String, Integer>>() {

			@Override
			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});

		// Here I am copying the sorted list in HashMap
		// using LinkedHashMap to preserve the insertion order
		HashMap<String, Integer> sortedHashMap = new LinkedHashMap<String, Integer>();
		for (Iterator<Entry<String, Integer>> it = list.iterator(); it.hasNext();) {
			Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>) it.next();
			sortedHashMap.put(entry.getKey(), entry.getValue());
		}
		return sortedHashMap;
	}

	
	public static HashMap<String, ArrayList<CloudObject>> sortByIntegerInter(HashMap<String, ArrayList<CloudObject>> map) {
		List<Entry<String, ArrayList<CloudObject>>> list = new LinkedList<Entry<String, ArrayList<CloudObject>>>(map.entrySet());
		// Defined Custom Comparator here
		Collections.sort(list, new Comparator<Entry<String, ArrayList<CloudObject>>>() {

			@Override
			public int compare(Entry<String, ArrayList<CloudObject>> o1, Entry<String, ArrayList<CloudObject>> o2) {
				
				return Integer.parseInt(o1.getKey()) - Integer.parseInt(o2.getKey());
			}
		});

		// Here I am copying the sorted list in HashMap
		// using LinkedHashMap to preserve the insertion order
		HashMap<String, ArrayList<CloudObject>> sortedHashMap = new LinkedHashMap<String, ArrayList<CloudObject>>();
		for (Iterator<Entry<String, ArrayList<CloudObject>>> it = list.iterator(); it.hasNext();) {
			Map.Entry<String, ArrayList<CloudObject>> entry = (Map.Entry<String, ArrayList<CloudObject>>) it.next();
			sortedHashMap.put(entry.getKey(), entry.getValue());
		}
		return sortedHashMap;
	}
	
	
	
	

	
	
}
