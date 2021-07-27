package riderdispatcher.core;


import java.util.*;

public class FordFulkerson {
    private FlowEdge[] edgeTo;
    private double value;
    private FlowNetwork G;

    public FordFulkerson(FlowNetwork G, int s, int t) {
        this.G = G;
        // 一直添加流量直到无法再添加为止
        while (hasAugmentingPath(G, s, t)) {
            // 找出增广路的瓶颈
            double bottle = Double.POSITIVE_INFINITY;
            int v = t;
            while (v != s) {
                bottle = Math.min(bottle, edgeTo[v].residualCapacityTo(v));
                v = edgeTo[v].other(v);
            }

            // 添加整条路径的流量
            v = t;
            while (v != s) {
                edgeTo[v].addResidualFlowTo(v, bottle);
                v = edgeTo[v].other(v);
            }

            // 最大流添加
            value += bottle;
        }
    }

    public double value() {
        return value;
    }

    public void showEdge() {
        System.out.println(G);
    }

    public FlowNetwork getG() {
        return G;
    }
    // 推断是否有增广路
    // 有增广路的条件就是存在一条路径，这条路径上全部的边都能添加流量。

    private boolean hasAugmentingPath(FlowNetwork G, int s, int t) {
        edgeTo = new FlowEdge[G.V()]; // 注意，这句话是必需要有的。由于每次增广路径都不一样。
        boolean[] visited = new boolean[G.V()];

        // BFS
        Queue<Integer> q = new LinkedList<Integer>();
        q.add(s);
        visited[s] = true; // 注意：这句话不要遗漏
        while (!q.isEmpty()) {
            int v = q.poll();

            // 可以通过的条件是流量可以添加
            for (FlowEdge e : G.adj(v)) {
                int w = e.other(v);
                if (e.residualCapacityTo(w) > 0 && !visited[w]) {
                    edgeTo[w] = e;
                    q.add(w);
                    visited[w] = true;
                }
            }
        }

        // 有增广路的条件就是S点可以到达T点。
        return visited[t];
    }

    public static void main(String[] argv) {
        FlowNetwork g = new FlowNetwork(4);
        int[] data = {0, 1, r(), 0, 2, r(), 2, 1, r(), 1, 3, r(), 2, 3, r(), 0, 3, r()};
        for (int i = 0; i < data.length; i += 3) {
            g.addEdge(new FlowEdge(data[i], data[i + 1], data[i + 2]));
        }
        FordFulkerson result = new FordFulkerson(g, 0, 3);
        result.showEdge();
    }

    private static int r() {
//        return new Random().nextInt(1000);
        return 1;
    }
}