package riderdispatcher.core;

import java.util.LinkedList;
import java.util.List;

public class FlowNetwork {
    private int V;
    private List<FlowEdge>[] adj;

    public FlowNetwork(int V) {
        this.V = V;
        adj = new LinkedList[V];
        for (int i = 0; i < adj.length; i++) {
            adj[i] = new LinkedList();
        }
    }

    public Iterable<FlowEdge> adj(int v) {
        return adj[v];
    }

    public int V() {
        return V;
    }

    public void addEdge(FlowEdge e) {
        int v = e.from();
        adj[v].add(e);
    }

    public double getEageFlow(int v, int w) {
        for (int i = 0; i < adj[v].size(); i++) {
            if (adj[v].get(i).to() == w) return adj[v].get(i).flow();
        }
        return 0;
    }

    @Override
    public String toString() {
        String result = "";
        for (int i = 0; i < V; i++) {
            result += i + ":";
            for(FlowEdge e:adj[i]) {
                result += " " + e.toString();
            }
            result += "\n";
        }
        return result;
    }
}