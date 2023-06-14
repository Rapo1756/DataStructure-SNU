import java.util.*;

// StationGraph: class that represents graph of stations
public class StationGraph {
    // stations: map that stores stations with id
    // adjacent: map that stores adjacent stations with id and weight of edge
    // between them (can be considered as adjacency hashmap)
    private final Map<String, Station> stations;
    private final Map<String, Map<String, Integer>> adjacent;

    public StationGraph() {
        adjacent = new HashMap<>();
        stations = new HashMap<>();
    }

    // clear: method that clears stations and adjacent
    public void clear() {
        adjacent.clear();
        stations.clear();
    }

    // isEmpty: method that checks if stations is empty
    // output: boolean (true if stations is empty, false otherwise)
    public boolean isEmpty() {
        return adjacent.isEmpty();
    }

    // addStation: method that adds station with id
    // input: station
    public void addStation(Station station) {
        if (station != null) {
            stations.put(station.id, station);
            adjacent.put(station.id, new HashMap<>());
        }
    }

    // addEdge: method that adds edge between two stations with id
    // input: s1, s2, weight
    // output: none
    // If there is no station with id s1 or s2, throw IllegalArgumentException
    public void addEdge(String s1, String s2, int weight) {
        if (getStation(s1) == null || getStation(s2) == null) {
            throw new IllegalArgumentException("역이 존재하지 않습니다.");
        }
        adjacent.get(s1).put(s2, weight);
    }

    // getStation: method that returns station with id
    // input: stationId
    // output: Station
    private Station getStation(String stationId) {
        return stations.get(stationId);
    }

    // getIdsWithName: method that returns ids of stations with name
    // input: stationName
    // output: Set<String> (ids of stations with name)
    private Set<String> getIdsWithName(String stationName) {
        Set<String> stationSet = new HashSet<>();
        for (Station station : stations.values()) {
            if (station.name.equals(stationName)) {
                stationSet.add(station.id);
            }
        }
        return stationSet;
    }

    // updateTransferInfo: method that updates transfer info
    // input: stationName, transferTime
    // output: none
    // In this implementation, consider transferring as edge with weight transferTime
    public void updateTransferInfo(String stationName, int transferTime) {
        Set<String> idSet = getIdsWithName(stationName);
        for (String s1 : idSet) {
            for (String s2 : idSet) {
                if (!s1.equals(s2)) {
                    addEdge(s1, s2, transferTime);
                    addEdge(s2, s1, transferTime);
                }
            }
        }
    }

    // makeTransferInfo: method that creates transfer info
    // In this implementation, consider transferring as edge with weight 5
    public void makeTransferInfo() {
        for (Station station : stations.values()) {
            updateTransferInfo(station.name, 5);
        }
    }

    // findPath: method that finds the shortest path between two stations with name
    // input: startName, endName
    // output: Pair<Integer, List<String>> (time, path)
    // After getting the ids of stations with name, find the shortest path between
    // two stations with id using findPathWithId method
    public Pair<Integer, List<String>> findPath(String startName, String endName) {
        Set<String> startIds = getIdsWithName(startName);
        Set<String> endIds = getIdsWithName(endName);
        if (startIds.isEmpty() || endIds.isEmpty()) {
            return null;
        }
        Pair<Integer, List<String>> minPath = null;
        for (String startId : startIds) {
            for (String endId : endIds) {
                Pair<Integer, List<String>> path = findPathWithId(startId, endId);
                if (minPath == null || (path != null && minPath.first > path.first)) {
                    minPath = path;
                }
            }
        }
        return minPath;
    }

    // findPathWithId: method that finds the shortest path between two stations
    // with id
    // input: startId, endId
    // output: Pair<Integer, List<String>> (time, path)
    // Use Dijkstra algorithm to find the shortest path between two stations with
    // id and return the time and path as Pair
    private Pair<Integer, List<String>> findPathWithId(String startId, String endId) {
        if (startId.equals(endId)) {
            return new Pair<>(0, new ArrayList<>(List.of(startId)));
        }
        Map<String, String> prev = new HashMap<>();
        MinTimeHeap timeHeap = new MinTimeHeap(stations.size());
        timeHeap.addStations(stations.values());
        timeHeap.setTime(startId, 0);
        Pair<Integer, String> min = timeHeap.deleteMin();
        while (!timeHeap.isEmpty() && !min.second.equals(endId) && min.first != Integer.MAX_VALUE) {
            for (Map.Entry<String, Integer> adjacent : adjacent.get(min.second).entrySet()) {
                int newTime = min.first + adjacent.getValue();
                if (timeHeap.contains(adjacent.getKey()) && newTime < timeHeap.getTime(adjacent.getKey())) {
                    timeHeap.setTime(adjacent.getKey(), newTime);
                    prev.put(adjacent.getKey(), min.second);
                }
            }
            min = timeHeap.deleteMin();
        }
        if (min == null || !min.second.equals(endId)) {
            return null;
        }
        List<String> path = new ArrayList<>();
        String current = endId;
        while (current != null) {
            String stationName = getStation(current).name;
            if (path.contains(getStation(current).name)) {
                path.remove(stationName);
                stationName = "[" + stationName + "]";
            }
            path.add(0, stationName);
            current = prev.get(current);
        }
        return new Pair<>(min.first, path);
    }
}

class MinTimeHeap {
    // first: time, second: station id
    List<Pair<Integer, String>> heapArray;
    int size;

    public MinTimeHeap(int n) {
        heapArray = new ArrayList<>(n);
        size = 0;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    // contains: method that checks if heap contains station id
    // input: id
    // output: boolean
    // If heap contains station id, return true
    // If heap does not contain station id, return false
    public boolean contains(String id) {
        for (int i = 0; i < size; i++) {
            if (heapArray.get(i).second.equals(id))
                return true;
        }
        return false;
    }

    // addStations: method that adds stations to heapArray
    // input: Collection<Station>
    // output: none
    // Add stations to heapArray and build heap
    public void addStations(Collection<Station> stations) {
        for (Station s : stations) {
            heapArray.add(new Pair<>(Integer.MAX_VALUE, s.id));
            size++;
        }
        buildHeap();
    }

    // getTime: method that returns the time of station id
    // input: id
    // output: time
    // If heap does not contain station id, return Integer.MAX_VALUE
    // If heap contains station id, return the time of station id
    public int getTime(String id) {
        for (int i = 0; i < size; i++) {
            if (heapArray.get(i).second.equals(id))
                return heapArray.get(i).first;
        }
        return Integer.MAX_VALUE;
    }

    // setTime: method that sets the time of station id as time and
    // percolates up
    // input: id, time
    // output: none
    // If heap does not contain station id or time is greater than or equal to the
    // current time of station id, do nothing
    // If time is less than the current time of station id, set the time of station
    // id as time and percolate up
    public void setTime(String s, int time) {
        for (int i = 0; i < size; i++) {
            if (heapArray.get(i).second.equals(s)) {
                if (time >= heapArray.get(i).first)
                    return;
                heapArray.get(i).first = time;
                percolateUp(i);
                return;
            }
        }
    }

    // deleteMin: method that deletes the minimum element in heap
    // output: Pair<Integer, String> (time, station id)
    // If heap is empty, return null
    // If heap is not empty, delete the minimum element and return it
    public Pair<Integer, String> deleteMin() {
        if (isEmpty())
            return null;
        Pair<Integer, String> min = heapArray.get(0);
        heapArray.set(0, heapArray.get(size - 1));
        heapArray.remove(size - 1);
        size--;
        percolateDown(0);
        return min;
    }

    private void buildHeap() {
        for (int i = size / 2 - 1; i >= 0; i--) {
            percolateDown(i);
        }
    }

    private void percolateDown(int i) {
        int child = 2 * i + 1;
        if (child >= size)
            return;
        if (child + 1 < size && heapArray.get(child + 1).first < heapArray.get(child).first)
            child++;
        Pair<Integer, String> temp = heapArray.get(i);
        Pair<Integer, String> childPair = heapArray.get(child);
        if (childPair.first < temp.first) {
            heapArray.set(i, childPair);
            heapArray.set(child, temp);
            percolateDown(child);
        }
    }

    private void percolateUp(int i) {
        int parent = (i - 1) / 2;
        if (parent < 0)
            return;
        Pair<Integer, String> temp = heapArray.get(i);
        Pair<Integer, String> parentPair = heapArray.get(parent);
        if (temp.first < parentPair.first) {
            heapArray.set(i, parentPair);
            heapArray.set(parent, temp);
            percolateUp(parent);
        }
    }
}
