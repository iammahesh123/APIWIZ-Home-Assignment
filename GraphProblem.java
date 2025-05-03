import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class GraphProblem {

    static Map<Integer, String> vertexMap = new HashMap<>();
    static Map<Integer, List<Integer>> adjList = new ConcurrentHashMap<>();
    static Map<Integer, AtomicInteger> inDegree = new ConcurrentHashMap<>();
    static Set<Integer> visited = Collections.synchronizedSet(new HashSet<>());

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = Integer.parseInt(sc.nextLine());

        for (int i = 0; i < n; i++) {
            String[] parts = sc.nextLine().split(":");
            vertexMap.put(Integer.parseInt(parts[0]), parts[1]);
        }

        int m = Integer.parseInt(sc.nextLine());

        for (int i = 1; i <= n; i++) {
            adjList.put(i, new ArrayList<>());
            inDegree.put(i, new AtomicInteger(0));
        }

        for (int i = 0; i < m; i++) {
            String[] edge = sc.nextLine().split(":");
            int from = Integer.parseInt(edge[0]);
            int to = Integer.parseInt(edge[1]);
            adjList.get(from).add(to);
            inDegree.get(to).incrementAndGet();
        }

        for (int nodeId : vertexMap.keySet()) {
            if (inDegree.get(nodeId).get() == 0) {
                new Thread(new WorkflowTask(nodeId)).start();
            }
        }
    }

    static class WorkflowTask implements Runnable {
        private final int nodeId;

        public WorkflowTask(int nodeId) {
            this.nodeId = nodeId;
        }

        @Override
        public void run() {
            if (visited.contains(nodeId)) return;

            synchronized (visited) {
                if (visited.contains(nodeId)) return;
                visited.add(nodeId);
            }

            // Execute node
            System.out.println(vertexMap.get(nodeId));

            for (int child : adjList.get(nodeId)) {
                int remaining = inDegree.get(child).decrementAndGet();
                if (remaining == 0) {
                    new Thread(new WorkflowTask(child)).start();
                }
            }
        }
    }
}
