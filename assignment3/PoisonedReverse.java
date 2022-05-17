import java.util.*;
import java.util.regex.Pattern;

public class PoisonedReverse {
    private static final Scanner SYS_SCANNER = new Scanner(System.in);
    private static final List<String> ROUTER_NAME_LIST = new ArrayList<>();
    private static int time = 0;
    private static List<PREdge> EDGE_LIST = new ArrayList<>();
    private static final Pattern PATTERN = Pattern.compile("^[-\\+]?[\\d]*$");

    /**
     * store router name and adjacent edges
     **/
    private static final List<PRNode> NODE_LIST = new ArrayList<>();

    public static void main(String[] args) {
        init();

        while (true) {
            if (time == 0) {
                boolean edgeInfo = getEdgeInfo();

                if (edgeInfo) {
                    for (PRNode value : NODE_LIST) {
                        value.printTable(time);
                    }
                    time++;

                    update();
                }
                printMinCost();
            } else {
                boolean updateEdgeInfo = updateEdgeInfo();

                if (updateEdgeInfo) {
                    for (PRNode value : NODE_LIST) {
                        value.printTable(time);
                    }
                    time++;

                    update();
                    printMinCost();
                } else {
                    break;
                }
            }
        }
    }

    private static boolean updateEdgeInfo() {
        EDGE_LIST = new ArrayList<>();

        lo:
        while (true) {
            String s = SYS_SCANNER.nextLine();
            if (s == null || s.trim().length() == 0) {
                break;
            } else {
                while (true) {
                    String[] split = s.trim().split("\\s+");
                    if (split.length == 3 && isLetter(split[0]) && isLetter(split[1]) && isNumber(split[2])) {
                        EDGE_LIST.add(new PREdge(split[0], split[1], Integer.parseInt(split[2])));
                        break;
                    } else {
                        System.out.println("Wrong input format. Please enter again.");
                        s = SYS_SCANNER.nextLine();
                        if (s == null || s.trim().length() == 0) {
                            break lo;
                        }
                    }
                }
            }
        }

        for (int i = 0; i < NODE_LIST.size(); i++) {
            PRNode node = NODE_LIST.get(i);
            List<String> neighborList = node.getNeighborList();
            List<Integer> neighborListIndex = node.getNeighborListIndex();
            int nodeIndex = node.getNodeIndex();
            String routerName = node.getRouterName();
            //renew arr in each node according to edgeList
            for (PREdge edge : EDGE_LIST) {
                String router1Name = edge.getRouter1Name();
                String router2Name = edge.getRouter2Name();
                int distance = edge.getDistance();
                int router1Index = edge.getRouter1Index(NODE_LIST);
                int router2Index = edge.getRouter2Index(NODE_LIST);
                boolean addNeighbor = false;

                if (distance == -1) {
                    if (ROUTER_NAME_LIST.contains(router1Name) && ROUTER_NAME_LIST.contains(router2Name)) {
                        if (router1Name.equals(routerName)) {
                            neighborList.remove(router2Name);
                            neighborListIndex.remove((Integer) router2Index);
                        } else if (router2Name.equals(routerName)) {
                            neighborList.remove(router1Name);
                            neighborListIndex.remove((Integer) router1Index);
                        }
                    }
                }

                if (!neighborListIndex.contains(router1Index) && router2Index == nodeIndex) {
                    neighborList.add(router1Name);
                    neighborListIndex.add(router1Index);
                    addNeighbor = true;
                } else if (!neighborListIndex.contains(router2Index) && router1Index == nodeIndex) {
                    neighborList.add(router2Name);
                    neighborListIndex.add(router2Index);
                    addNeighbor = true;
                }

                if (addNeighbor) {
                    neighborList.sort((o1, o2) -> {
                        char ch1 = o1.charAt(0);
                        char ch2 = o2.charAt(0);
                        if (ch1 >= 65 && ch1 <= 90 && ch2 >= 65 && ch2 <= 90) {
                            return ch1 - ch2;
                        }

                        if (ch1 >= 97 && ch1 <= 122 && ch2 >= 97 && ch2 <= 122) {
                            return ch1 - ch2;
                        }

                        if (ch1 >= 65 && ch1 <= 90 && ch2 >= 97 && ch2 <= 122) {
                            int diff = (ch1 - 65) - (ch2 - 97);
                            //e.g. X & x, assume lower case is greater than upper case
                            if (diff == 0) {
                                return -1;
                            }

                            return diff;
                        }

                        if (ch1 >= 97 && ch1 <= 122 && ch2 >= 65 && ch2 <= 90) {
                            int diff = (ch1 - 97) - (ch2 - 65);
                            //e.g. x & X, assume lower case is greater than upper case
                            if (diff == 0) {
                                return 1;
                            }

                            return diff;
                        }

                        throw new RuntimeException("Wrong!");
                    });

                    neighborListIndex.sort(Comparator.comparingInt(o -> o));
                }

                if (router1Index == nodeIndex || router2Index == nodeIndex) {
                    NODE_LIST.get(i).getArr()[router1Index][router2Index] = edge.getDistance();
                    NODE_LIST.get(i).getArr()[router2Index][router1Index] = edge.getDistance();
                }
            }
        }

        return EDGE_LIST.size() > 0;
    }

    private static void init() {
        while (true) {
            String routerName = SYS_SCANNER.nextLine();
            if (routerName == null || routerName.trim().length() == 0) {
                break;
            } else {
                while (true) {
                    if (isLetter(routerName)) {
                        NODE_LIST.add(new PRNode(routerName));
                        ROUTER_NAME_LIST.add(routerName);
                        break;
                    } else {
                        System.out.println("Wrong input for router name. Please input again:");
                        routerName = SYS_SCANNER.nextLine();
                    }
                }
            }
        }

        ROUTER_NAME_LIST.sort((o1, o2) -> {
            char ch1 = o1.charAt(0);
            char ch2 = o2.charAt(0);
            if (ch1 >= 65 && ch1 <= 90 && ch2 >= 65 && ch2 <= 90) {
                return ch1 - ch2;
            }

            if (ch1 >= 97 && ch1 <= 122 && ch2 >= 97 && ch2 <= 122) {
                return ch1 - ch2;
            }

            if (ch1 >= 65 && ch1 <= 90 && ch2 >= 97 && ch2 <= 122) {
                int diff = (ch1 - 65) - (ch2 - 97);
                //e.g. X & x, assume lower case is greater than upper case
                if (diff == 0) {
                    return -1;
                }

                return diff;
            }

            if (ch1 >= 97 && ch1 <= 122 && ch2 >= 65 && ch2 <= 90) {
                int diff = (ch1 - 97) - (ch2 - 65);
                //e.g. x & X, assume lower case is greater than upper case
                if (diff == 0) {
                    return 1;
                }

                return diff;
            }

            throw new RuntimeException("Wrong!");
        });

        NODE_LIST.sort((o1, o2) -> {
            char ch1 = o1.getRouterName().charAt(0);
            char ch2 = o2.getRouterName().charAt(0);
            if (ch1 >= 65 && ch1 <= 90 && ch2 >= 65 && ch2 <= 90) {
                return ch1 - ch2;
            }

            if (ch1 >= 97 && ch1 <= 122 && ch2 >= 97 && ch2 <= 122) {
                return ch1 - ch2;
            }

            if (ch1 >= 65 && ch1 <= 90 && ch2 >= 97 && ch2 <= 122) {
                int diff = (ch1 - 65) - (ch2 - 97);
                //e.g. X & x, assume lower case is greater than upper case
                if (diff == 0) {
                    return -1;
                }

                return diff;
            }

            if (ch1 >= 97 && ch1 <= 122 && ch2 >= 65 && ch2 <= 90) {
                int diff = (ch1 - 97) - (ch2 - 65);
                //e.g. x & X, assume lower case is greater than upper case
                if (diff == 0) {
                    return 1;
                }

                return diff;
            }

            throw new RuntimeException("Wrong!");
        });

        for (int i = 0; i < NODE_LIST.size(); i++) {
            NODE_LIST.get(i).setNodeIndex(i);
        }

        for (int i = 0; i < NODE_LIST.size(); i++) {
            PRNode node = NODE_LIST.get(i);
            List<String> destinationList = new ArrayList<>();
            List<Integer> destinationIndexList = new ArrayList<>();
            for (int j = 0; j < NODE_LIST.size(); j++) {
                if (!node.getRouterName().equals(NODE_LIST.get(j).getRouterName())) {
                    destinationList.add(NODE_LIST.get(j).getRouterName());
                    destinationIndexList.add(j);
                }
            }
            node.setDestinationList(destinationList);
            node.setDestinationListIndex(destinationIndexList);
        }
    }

    private static boolean isNumber(String routerName) {
        return PATTERN.matcher(routerName).matches();
    }

    private static boolean isLetter(String routerName) {
        boolean isLetter = false;

        if (routerName.length() == 1) {
            char c = routerName.charAt(0);
            if ((c >= 65 && c <= 90) || (c >= 97 && c <= 122)) {
                isLetter = true;
            }
        }

        return isLetter;
    }

    private static void printMinCost() {
        for (PRNode node : NODE_LIST) {
            List<String> destinationList = node.getDestinationList();
            List<String> neighborList = node.getNeighborList();
            String routerName = node.getRouterName();
            int[][] trimedArr = node.getTrimedArr();
            //store the smallest value in trimedArr in each row
            if (trimedArr[0].length > 0) {
                int[] minIndexArr = new int[trimedArr.length];

                for (int j = 0; j < trimedArr.length; j++) {
                    int minVal = trimedArr[j][0];
                    int minIndex = 0;
                    for (int k = 1; k < trimedArr[j].length; k++) {
                        if (minVal == -1) {
                            minVal = trimedArr[j][k];
                        } else if (trimedArr[j][k] < minVal && trimedArr[j][k] >= 0) {
                            minVal = trimedArr[j][k];
                            minIndex = k;
                        }
                    }

                    minIndexArr[j] = minIndex;
                }

                for (int j = 0; j < destinationList.size(); j++) {
                    int minIndex = minIndexArr[j];
                    int minCost = trimedArr[j][minIndex];
                    String minNeighbor = neighborList.get(minIndex);
                    String destination = destinationList.get(j);
                    if (minCost != Integer.MAX_VALUE) {
                        System.out.printf("router %s: %s is %d routing through %s\n", routerName, destination, minCost, minNeighbor);
                    } else {
                        System.out.printf("router %s: %s is unreachable\n", routerName, destination);
                    }
                }
            } else {
                for (String destination : destinationList) {
                    System.out.printf("router %s: %s is unreachable\n", routerName, destination);
                }
            }
        }
    }

    private static void update() {
        while (true) {
            List<int[][]> nodeArrToBeUpdated = new ArrayList<>();
            boolean changed = false;

            for (int i = 0; i < NODE_LIST.size(); i++) {
                PRNode node = NODE_LIST.get(i);
                int[][] nodeArr = node.getArr();
                int[][] nodeArrToBeHandled = new int[nodeArr.length][nodeArr.length];
                int nodeIndex = node.getNodeIndex();

                for (int j = 0; j < nodeArrToBeHandled.length; j++) {
                    System.arraycopy(nodeArr[j], 0, nodeArrToBeHandled[j], 0, nodeArrToBeHandled.length);
                }

                for (int j = 0; j < NODE_LIST.size(); j++) {
                    if (i != j) {
                        PRNode nodeMessageToBeExtracted = NODE_LIST.get(j);
                        int[][] trimedArr = nodeMessageToBeExtracted.getTrimedArr();
                        int nodeMessageToBeExtractedIndex = nodeMessageToBeExtracted.getNodeIndex();
                        List<Integer> destinationListIndex = nodeMessageToBeExtracted.getDestinationListIndex();
                        List<Integer> neighborListIndex = nodeMessageToBeExtracted.getNeighborListIndex();

                        for (int k = 0; k < nodeArr.length; k++) {
                            for (int l = 0; l < nodeArr[k].length; l++) {
                                if (k != nodeIndex && l != nodeIndex) {
                                    if (k == nodeMessageToBeExtractedIndex && l != nodeMessageToBeExtractedIndex) {
                                        int index = destinationListIndex.indexOf(l);
                                        int minCostOfOneLineInTrimedArr = getMinCostOfOneLineInTrimedArr(trimedArr, index, nodeIndex, neighborListIndex);
                                        if (minCostOfOneLineInTrimedArr != nodeArrToBeHandled[k][l]) {
                                            nodeArrToBeHandled[k][l] = minCostOfOneLineInTrimedArr;
                                            changed = true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                nodeArrToBeUpdated.add(nodeArrToBeHandled);

                NODE_LIST.get(i).setArr(nodeArr);
            }


            for (int i = 0; i < nodeArrToBeUpdated.size(); i++) {
                NODE_LIST.get(i).setArr(nodeArrToBeUpdated.get(i));
            }

            if (changed) {
                for (PRNode node : NODE_LIST) {
                    node.printTable(time);
                }
                time++;
            } else {
                break;
            }
        }
    }

    private static int getMinCostOfOneLineInTrimedArr(int[][] trimedArr, int index, int nodeIndex, List<Integer> neighborListIndex) {
        int[] destinationRow = trimedArr[index];
        if (destinationRow.length == 0) {
            return -1;
        } else {
            int minCost = destinationRow[0];
            int minCostIndex = 0;
            for (int i = 1; i < destinationRow.length; i++) {
                if (minCost == -1) {
                    minCost = destinationRow[i];
                    minCostIndex = i;
                } else if (destinationRow[i] >= 0 && destinationRow[i] < minCost) {
                    minCost = destinationRow[i];
                    minCostIndex = i;
                }
            }

            minCostIndex = neighborListIndex.get(minCostIndex);

            if (minCostIndex == nodeIndex) {
                return Integer.MAX_VALUE;
            } else {
                return minCost;
            }
        }
    }

    private static boolean getEdgeInfo() {
        // store every times' edges
        //
        //X Z 7
        //X Y 2
        //Y Z 1
        //
        //X Z 5
        //Z Y -1
        //record each line break num
        boolean get = false;
        EDGE_LIST = new ArrayList<>();

        lo:
        while (true) {
            String s = SYS_SCANNER.nextLine();
            if (s == null || s.trim().length() == 0) {
                break;
            } else {
                while (true) {
                    String[] split = s.trim().split("\\s+");
                    if (split.length == 3 && ROUTER_NAME_LIST.contains(split[0]) && ROUTER_NAME_LIST.contains(split[1]) && isNumber(split[2])) {
                        EDGE_LIST.add(new PREdge(split[0], split[1], Integer.parseInt(split[2])));
                        get = true;
                        break;
                    } else {
                        System.out.println("Wrong input format. Please enter again.");
                        s = SYS_SCANNER.nextLine();
                        if (s == null || s.trim().length() == 0) {
                            break lo;
                        }
                    }
                }
            }
        }

        for (PRNode node : NODE_LIST) {
            List<String> strings = new ArrayList<>();
            for (PREdge edge : EDGE_LIST) {
                if (node.getRouterName().equals(edge.getRouter1Name())) {
                    strings.add(edge.getRouter2Name());
                } else if (node.getRouterName().equals(edge.getRouter2Name())) {
                    strings.add(edge.getRouter1Name());
                }
            }

            strings.sort((o1, o2) -> {
                char ch1 = o1.charAt(0);
                char ch2 = o2.charAt(0);
                if (ch1 >= 65 && ch1 <= 90 && ch2 >= 65 && ch2 <= 90) {
                    return ch1 - ch2;
                }

                if (ch1 >= 97 && ch1 <= 122 && ch2 >= 97 && ch2 <= 122) {
                    return ch1 - ch2;
                }

                if (ch1 >= 65 && ch1 <= 90 && ch2 >= 97 && ch2 <= 122) {
                    int diff = (ch1 - 65) - (ch2 - 97);
                    //e.g. X & x, assume lower case is greater than upper case
                    if (diff == 0) {
                        return -1;
                    }

                    return diff;
                }

                if (ch1 >= 97 && ch1 <= 122 && ch2 >= 65 && ch2 <= 90) {
                    int diff = (ch1 - 97) - (ch2 - 65);
                    //e.g. x & X, assume lower case is greater than upper case
                    if (diff == 0) {
                        return 1;
                    }

                    return diff;
                }

                throw new RuntimeException("Wrong!");
            });

            List<Integer> neighborListIndex = new ArrayList<>();
            int count = 0;
            for (int j = 0; j < ROUTER_NAME_LIST.size(); j++) {
                if (count < strings.size()) {
                    if (ROUTER_NAME_LIST.get(j).equals(strings.get(count))) {
                        neighborListIndex.add(j);
                        count++;
                    }
                }
            }
            node.setNeighborList(strings);
            node.setNeighborListIndex(neighborListIndex);
        }

        int[][] table = new int[NODE_LIST.size()][NODE_LIST.size()];

        for (int[] ints : table) {
            Arrays.fill(ints, Integer.MAX_VALUE);
        }

        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table.length; j++) {
                if (i == j) {
                    table[i][j] = 0;
                }
            }
        }

        for (PREdge prEdge : EDGE_LIST) {
            String router1Name = prEdge.getRouter1Name();
            String router2Name = prEdge.getRouter2Name();
            int router1Index = 0;
            int router2Index = 0;
            for (int j = 0; j < NODE_LIST.size(); j++) {
                if (router1Name.equals(NODE_LIST.get(j).getRouterName())) {
                    router1Index = j;
                } else if (router2Name.equals(NODE_LIST.get(j).getRouterName())) {
                    router2Index = j;
                }
            }

            table[router1Index][router2Index] = prEdge.getDistance();
            table[router2Index][router1Index] = prEdge.getDistance();
        }

        for (int i = 0; i < NODE_LIST.size(); i++) {
            PRNode node = NODE_LIST.get(i);

            int[][] arr = new int[NODE_LIST.size()][NODE_LIST.size()];

            for (int[] ints : arr) {
                Arrays.fill(ints, Integer.MAX_VALUE);
            }

            node.setArr(arr);
        }

        for (int i = 0; i < NODE_LIST.size(); i++) {
            PRNode node = NODE_LIST.get(i);
            for (int j = 0; j < NODE_LIST.size(); j++) {
                node.getArr()[j][j] = 0;
            }
        }

        for (int i = 0; i < NODE_LIST.size(); i++) {
            int[] tmpArr = table[i];
            PRNode node = NODE_LIST.get(i);

            for (int j = 0; j < tmpArr.length; j++) {
                node.getArr()[i][j] = tmpArr[j];
                node.getArr()[j][i] = tmpArr[j];
            }
        }

        return get;
    }
}

/**
 * Store two adjacent routers and their distance
 * e.g. X Y 1
 */
class PREdge {
    private String router1Name;
    private String router2Name;
    private int distance;

    public PREdge() {
    }

    public PREdge(String router1Name, String router2Name, int distance) {
        this.router1Name = router1Name;
        this.router2Name = router2Name;
        this.distance = distance;
    }

    public String getRouter1Name() {
        return router1Name;
    }

    public void setRouter1Name(String router1Name) {
        this.router1Name = router1Name;
    }

    public String getRouter2Name() {
        return router2Name;
    }

    public void setRouter2Name(String router2Name) {
        this.router2Name = router2Name;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "Edge{" +
                "router1Name='" + router1Name + '\'' +
                ", router2Name='" + router2Name + '\'' +
                ", distance=" + distance +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PREdge edge = (PREdge) o;
        return distance == edge.distance &&
                Objects.equals(router1Name, edge.router1Name) &&
                Objects.equals(router2Name, edge.router2Name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(router1Name, router2Name, distance);
    }

    public int getRouter1Index(List<PRNode> nodeList) {
        int index = 0;

        for (PRNode prNode : nodeList) {
            if (prNode.getRouterName().equals(router1Name)) {
                index = prNode.getNodeIndex();
            }
        }

        return index;
    }

    public int getRouter2Index(List<PRNode> nodeList) {
        int index = 0;

        for (PRNode prNode : nodeList) {
            if (prNode.getRouterName().equals(router2Name)) {
                index = prNode.getNodeIndex();
            }
        }

        return index;
    }
}

class PRNode {
    private String routerName;
    private List<String> neighborList = new ArrayList<>();
    private List<Integer> neighborListIndex = new ArrayList<>();
    private List<String> destinationList = new ArrayList<>();
    private List<Integer> destinationListIndex = new ArrayList<>();
    private int nodeIndex;
    //create a two-dimensional array to store distance (do not show the distance to itself)
    //column is distance
    //row shows via which node
    private int[][] arr;

    public void printTable(int time) {
        //print("router X at t=0")
        System.out.printf("router %s at t=%d\n", routerName, time);
        //print("	Y	Z")
        System.out.print("\t");
        for (String s : destinationList) {
            System.out.printf("%s\t", s);
        }
        System.out.println();
        int[][] trimedArr = getOutputTirmedArr();

        //print("Y 2   INF")
        //print("Z INF 7")
        for (int j = 0; j < destinationList.size(); j++) {
            System.out.printf("%s\t", destinationList.get(j));
            int[] row = trimedArr[j];

            for (int i : row) {
                if (i == Integer.MAX_VALUE) {
                    System.out.printf("%s\t", "INF");
                } else if (i == -1) {
                    System.out.printf("%s\t", "-");
                } else {
                    System.out.printf("%d\t", i);
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    /**
     * turn rows from neighbor to destination
     *
     * @return trimed arr used for output
     */
    private int[][] getOutputTirmedArr() {
        int[][] trimedArr = getTrimedArr();
        int[][] outputTrimedArr = new int[destinationList.size()][destinationList.size()];

        for (int i = 0; i < destinationListIndex.size(); i++) {
            for (int j = 0; j < destinationListIndex.size(); j++) {
                Integer destinationIndex = destinationListIndex.get(j);
                if (neighborListIndex.contains(destinationIndex)) {
                    int index = neighborListIndex.indexOf(destinationIndex);
                    outputTrimedArr[i][j] = trimedArr[i][index];
                } else {
                    outputTrimedArr[i][j] = Integer.MAX_VALUE;
                }
            }
        }

        return outputTrimedArr;
    }

    //[1, 2147483647, 2147483647]
    //[2147483647, 2, 2147483647]
    //[2147483647, 2147483647, 7]
    public int[][] getTrimedArr() {
        int[][] trimedArr = new int[destinationList.size()][neighborList.size()];
        int[] ints = arr[nodeIndex];
        int[] eleArr = new int[ints.length - 1];
        System.arraycopy(ints, 0, eleArr, 0, nodeIndex);

        if (eleArr.length - nodeIndex >= 0)
            System.arraycopy(ints, nodeIndex + 1, eleArr, nodeIndex, eleArr.length - nodeIndex);

        int count = 0;
        int[] effiArr = new int[neighborList.size()];
        for (int i = 0; i < effiArr.length; i++) {
            if (eleArr[i] == Integer.MAX_VALUE) {
                count++;
            }

            effiArr[i] = eleArr[i + count];
        }

        //store [1,2], [1,3], [2,3], [7,2]
        List<int[]> arrStoredList = new ArrayList<>();
        for (int i = 0; i < destinationList.size() + 1; i++) {
            int[] tmpArr = new int[destinationList.size() - 1];
            arrStoredList.add(tmpArr);
        }

        int handleArrTime;
        for (int i = 0; i < arr.length; i++) {
            handleArrTime = 0;
            count = 0;
            for (int j = 0; j < arr[i].length; j++) {
                if (i == j) {
                    count++;
                    continue;
                } else if (j == nodeIndex) {
                    count++;
                    continue;
                }
                if (handleArrTime < destinationList.size() - 1) {
                    arrStoredList.get(i)[j - count] = arr[i][j];
                    handleArrTime++;
                }
            }
        }

        //only contains neighbor arr[]
        //[1,2], [2,3]
        List<int[]> trimedArrStoredList = new ArrayList<>();
        for (int index : neighborListIndex) {
            trimedArrStoredList.add(arrStoredList.get(index));
        }

        count = 0;
        for (int[] value : trimedArr) {
            System.arraycopy(effiArr, 0, value, 0, value.length);
        }

        //length == neighborListIndex, all ele == 0
        List<Integer> neighborListUsedCondition = new ArrayList<>();
        for (int i = 0; i < neighborList.size(); i++) {
            neighborListUsedCondition.add(0);
        }

        for (int i = 0; i < trimedArr.length; i++) {
            for (int j = 0; j < trimedArr[i].length; j++) {
                if (!neighborList.get(j).equals(destinationList.get(i))) {
                    int val = trimedArrStoredList.get(j)[neighborListUsedCondition.get(j)];
                    if (val == Integer.MAX_VALUE) {
                        trimedArr[i][j] = Integer.MAX_VALUE;
                    } else if (trimedArr[i][j] == -1) {
                        trimedArr[i][j] = -1;
                    } else if (val == -1) {
                        //trimedArr[i][j] =
                    } else {
                        trimedArr[i][j] = val + trimedArr[i][j];
                    }
                    neighborListUsedCondition.set(j, neighborListUsedCondition.get(j) + 1);
                }
            }
        }
        return trimedArr;
    }

    public PRNode() {
    }

    public PRNode(String routerName) {
        this.routerName = routerName;
    }

    public PRNode(String routerName, List<String> neighborList, List<String> destinationList, int[][] arr) {
        this.routerName = routerName;
        this.neighborList = neighborList;
        this.destinationList = destinationList;
        this.arr = arr;
    }

    public String getRouterName() {
        return routerName;
    }

    public void setRouterName(String routerName) {
        this.routerName = routerName;
    }

    public int[][] getArr() {
        return arr;
    }

    public void setArr(int[][] arr) {
        this.arr = arr;
    }

    public List<String> getNeighborList() {
        return neighborList;
    }

    public void setNeighborList(List<String> neighborList) {
        this.neighborList = neighborList;
    }

    public List<String> getDestinationList() {
        return destinationList;
    }

    public void setDestinationList(List<String> destinationList) {
        this.destinationList = destinationList;
    }

    public int getNodeIndex() {
        return nodeIndex;
    }

    public void setNodeIndex(int nodeIndex) {
        this.nodeIndex = nodeIndex;
    }

    public List<Integer> getNeighborListIndex() {
        return neighborListIndex;
    }

    public void setNeighborListIndex(List<Integer> neighborListIndex) {
        this.neighborListIndex = neighborListIndex;
    }

    public List<Integer> getDestinationListIndex() {
        return destinationListIndex;
    }

    public void setDestinationListIndex(List<Integer> destinationListIndex) {
        this.destinationListIndex = destinationListIndex;
    }

    @Override
    public String toString() {
        return "Node{" +
                "routerName='" + routerName + '\'' +
                ", neighborList=" + neighborList +
                ", neighborListIndex=" + neighborListIndex +
                ", destinationList=" + destinationList +
                ", destinationListIndex=" + destinationListIndex +
                ", nodeIndex=" + nodeIndex +
                ", arr=" + Arrays.deepToString(arr) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PRNode node = (PRNode) o;
        return nodeIndex == node.nodeIndex &&
                Objects.equals(routerName, node.routerName) &&
                Objects.equals(neighborList, node.neighborList) &&
                Objects.equals(neighborListIndex, node.neighborListIndex) &&
                Objects.equals(destinationList, node.destinationList) &&
                Objects.equals(destinationListIndex, node.destinationListIndex) &&
                Arrays.equals(arr, node.arr);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(routerName, neighborList, neighborListIndex, destinationList, destinationListIndex, nodeIndex);
        result = 31 * result + Arrays.hashCode(arr);
        return result;
    }
}
