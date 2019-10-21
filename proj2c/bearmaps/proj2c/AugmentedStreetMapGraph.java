package bearmaps.proj2c;

import bearmaps.MyTrieSet;
import bearmaps.hw4.streetmap.Node;
import bearmaps.hw4.streetmap.StreetMapGraph;
import bearmaps.proj2ab.Point;
import bearmaps.proj2ab.PointSet;
import bearmaps.proj2ab.WeirdPointSet;

import java.util.*;

/**
 * An augmented graph that is more powerful that a standard StreetMapGraph.
 * Specifically, it supports the following additional operations:
 *
 *
 * @author Alan Yao, Josh Hug, ________
 */
public class AugmentedStreetMapGraph extends StreetMapGraph {

    // Part 2 things
    HashMap<Point, Node> storage;
    PointSet points;

    //Part 3 things
    HashMap<String, String> namesToCleanedNames; // Storing names and their cleaned counterparts.
    HashMap<String, String> cleanedNamesToNames; // Storing cleaned names to their named counterparts
    HashMap<String, HashSet<Node>> seenPlaces; // Keeping a list of places we've already seen in association with a name.
    HashMap<String, HashSet<String>> totalLocations; // I'm going to keep a HashMap that maps the name to an associated set of all names

    MyTrieSet searchTrie;

    public AugmentedStreetMapGraph(String dbPath) {
        super(dbPath);

        // Part 2 initializations
        storage = new HashMap<>();
        ArrayList<Point> pointSetStorage = new ArrayList<>();

        //Part 3 initializations - hashmap used to go between two names, trie used for searching
        namesToCleanedNames = new HashMap<>();
        cleanedNamesToNames = new HashMap<>();
        searchTrie = new MyTrieSet();
        seenPlaces = new HashMap<>();
        totalLocations = new HashMap<>();


        // Things for part 2
        for (Node n : this.getNodes()) {
            if (!this.neighbors(n.id()).isEmpty()) {
                Point add = new Point(n.lon(), n.lat());
                storage.put(add, n);
                pointSetStorage.add(add);
            }

            /** 1. Check if the node has a name.
             *  2. If it does, we want to take the cleaned name and add it to the trie set
             *   because that's what we're going to be using to do our searching.
             *  3. A name doesn't only map up to one location, though - since multiple places can have
             *  the same name. i.e. multiple Target stores in a city. So, we should keep a list of
             *  things we've already seen - and add the newer node when it turns out we've already seen
             *  the name before. */

            // Part 3 Code
            String name;
            String cleanedName;
            if (n.name() != null) { // If the current node has a name (it's a location we care about...)

                name = n.name(); // Take both of it's names - cleaned and uncleaned
                cleanedName = cleanString(name);

                // Add them to our maps and trie (with only cleaned names)
                namesToCleanedNames.put(name, cleanedName); // Nothing changes if the name already exists
                cleanedNamesToNames.put(cleanedName, name); // Nothing will be changed if the name already exists
                searchTrie.add(cleanedName); // Only contains cleaned strings

                if (!totalLocations.containsKey(cleanedName)) { // If we haven't encountered this location yet
                    HashSet<String> temp = new HashSet(); // Create a new HashSet and add this place's name into there
                    temp.add(n.name());
                    totalLocations.put(cleanedName, temp);
                }
                else if (totalLocations.containsKey(cleanedName)) {
                    totalLocations.get(cleanedName).add(n.name()); // If we've already seen it, just add the name to the hashset.
                }
                // If we haven't encountered this place yet...
                if (!seenPlaces.containsKey(cleanedName)) {
                    // We're going to put the cleaned name into our map with a new list of locations we've seen and add
                    // the current node that we're on
                    seenPlaces.put(cleanedName, new HashSet<Node>());
                    seenPlaces.get(cleanedName).add(n);
                }
                // If we have encountered this place already...
                else if (seenPlaces.containsKey(cleanedName)) {
                    // We just add the current node we're on to the list associated with that name
                    seenPlaces.get(cleanedName).add(n);
                }

                // Used for getLocationsByPrefix
            }
        }

        points = new WeirdPointSet(pointSetStorage);

    }


    /**
     * For Project Part II
     * Returns the vertex closest to the given longitude and latitude.
     * @param lon The target longitude.
     * @param lat The target latitude.
     * @return The id of the node in the graph closest to the target.
     */
    public long closest(double lon, double lat) {
        Point nearest = points.nearest(lon, lat);
        return storage.get(nearest).id();
    }


    /**
     * For Project Part III (gold points)
     * In linear time, collect all the names of OSM locations that prefix-match the query string.
     * @param prefix Prefix string to be searched for. Could be any case, with our without
     *               punctuation.
     * @return A <code>List</code> of the full names of locations whose cleaned name matches the
     * cleaned <code>prefix</code>.
     */
    public List<String> getLocationsByPrefix(String prefix) {
        // We clean the prefix so we can go into our trie.
        String cleanedPrefix = cleanString(prefix);
        // We find a list of all the names that share that prefix
        List<String> prefixList = searchTrie.keysWithPrefix(cleanedPrefix);
        List<String> retList = new ArrayList<>();
        // We're going to go through all of those keys...
        for (String s : prefixList) {
            // And see the associated set that has all of the keys
            for (String name : totalLocations.get(s)) {
                // Add each one to the return list
                retList.add(name);
            }
        }

        return retList;
    }

    /**
     * For Project Part III (gold points)
     * Collect all locations that match a cleaned <code>locationName</code>, and return
     * information about each node that matches.
     * @param locationName A full name of a location searched for.
     * @return A list of locations whose cleaned name matches the
     * cleaned <code>locationName</code>, and each location is a map of parameters for the Json
     * response as specified: <br>
     * "lat" -> Number, The latitude of the node. <br>
     * "lon" -> Number, The longitude of the node. <br>
     * "name" -> String, The actual name of the node. <br>
     * "id" -> Number, The id of the node. <br>
     */
    public List<Map<String, Object>> getLocations(String locationName) {
        // Get the cleanedname of the location
        String cleanedName = cleanString(locationName);
        List<Map<String, Object>> returnList = new ArrayList<>();
        // We have the cleaned name - we can access our seenPlaces HashMap that has all the cleaned names...
        // If there exists a place with that name...
        if (seenPlaces.containsKey(cleanedName)) {
            // We access the associated HashSet with all of the nodes associated with that name
            for (Node n : seenPlaces.get(cleanedName)) {
                // Create a temp hashmap and put all of the information we need into there.
                HashMap temp = new HashMap();
                temp.put("lat", n.lat());
                temp.put("lon", n.lon());
                temp.put("name", n.name());
                temp.put("id", n.id());
                returnList.add(temp);
                System.out.println(temp);
            }
        }
        return returnList;
    }


    /**
     * Useful for Part III. Do not modify.
     * Helper to process strings into their "cleaned" form, ignoring punctuation and capitalization.
     * @param s Input string.
     * @return Cleaned string.
     */
    private static String cleanString(String s) {
        return s.replaceAll("[^a-zA-Z ]", "").toLowerCase();
    }

}
