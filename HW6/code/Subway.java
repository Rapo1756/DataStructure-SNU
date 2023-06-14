import java.io.*;
import java.util.List;
import java.util.Objects;

public class Subway {
    static StationGraph stationGraph;

    public static void main(String[] args) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        if (args.length != 1) {
            System.out.println("Usage: java Subway [input file]");
            System.exit(1);
        }
        if ((stationGraph = readData(args[0])).isEmpty()) {
            System.exit(1);
        }
        while (true) {
            try {
                String input = br.readLine();
                if (input.compareTo("QUIT") == 0)
                    break;
                command(input);
            } catch (IOException e) {
                System.out.println("입력이 잘못되었습니다. 오류 : " + e);
            }
        }
    }

    // command: parse input and call findPath
    // input: "station1 station2"
    // print path to destination and time (if exists)
    // ex)
    //  station1 ... station2
    //  time
    private static void command(String input) {
        String[] stations = parse(input);
        if (stations == null) {
            System.out.println("입력이 잘못되었습니다. 다시 입력해주세요.");
            return;
        }
        Pair<Integer, List<String>> path = stationGraph.findPath(stations[0], stations[1]);
        printPath(path);
    }

    // parse: parse input string to station names
    // input: "station1 station2"
    // output: ["station1", "station2"]
    private static String[] parse(String input) {
        String[] data = input.split(" ");
        if (data.length != 2) {
            return null;
        }
        return data;
    }

    // printPath: print path to destination
    // input: Pair<Integer, List<String>> path
    // output: "station1 station2 station3"
    // "time"
    private static void printPath(Pair<Integer, List<String>> path) {
        if (path == null) {
            System.out.println("경로가 존재하지 않습니다.");
            return;
        }
        StringBuilder pathString = new StringBuilder();
        for (String station : path.second) {
            pathString.append(station).append(" ");
        }
        System.out.println(pathString.toString().trim());
        System.out.println(path.first);
    }

    // readData: read data from file
    // input: String filePath
    // output: StationGraph
    private static StationGraph readData(String filePath) {
        StationGraph stationGraph = new StationGraph();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while (!Objects.equals(line = br.readLine(), "") && line != null) {
                String[] data = line.split(" ");
                Station station = new Station(data[0], data[1], data[2]);
                stationGraph.addStation(station);
            }
            while (line != null && !Objects.equals(line = br.readLine(), "") && line != null) {
                String[] data = line.split(" ");
                stationGraph.addEdge(data[0], data[1], Integer.parseInt(data[2]));
            }
            stationGraph.makeTransferInfo();
            while (line != null && !Objects.equals(line = br.readLine(), "") && line != null) {
                String[] data = line.split(" ");
                stationGraph.updateTransferInfo(data[0], Integer.parseInt(data[1]));
            }
        } catch (IOException | ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
            System.out.println("입력이 잘못되었습니다. 오류 : " + e);
            stationGraph.clear();
        }
        return stationGraph;
    }
}