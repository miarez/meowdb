
package stas.website.Index.Btree;

import java.util.ArrayList;
import java.util.TreeMap;

public class BTree {
    private static class Node {
        TreeMap<String, ArrayList<Integer>> keys = new TreeMap<>();
        ArrayList<Node> children = new ArrayList<>();
        boolean isLeaf;
        int t; // Minimum degree (defines the range for number of keys)

        Node(int t, boolean isLeaf) {
            this.t = t;
            this.isLeaf = isLeaf;
        }
    }
    public void printTree(Node node, String indent, boolean last) {
        // Print the current node's keys
        if (last) {
            System.out.println(indent + "`-- " + node.keys);
            indent += "    ";
        } else {
            System.out.println(indent + "|-- " + node.keys + "\n");
            indent += "|   ";
        }
    
        // Recursively print each child
        for (int i = 0; i < node.children.size(); i++) {
            printTree(node.children.get(i), indent, i == node.children.size() - 1);
        }
    }
    
    

    public Node root;
    private int t; // Minimum degree

    public BTree(int t) {
        this.root = new Node(t, true);
        this.t = t;
    }

    public void insert(String key, int offset) {
        Node r = this.root;
        if (r.keys.size() == 2 * t - 1) {
            Node s = new Node(t, false);
            this.root = s;
            s.children.add(r);
            splitChild(s, 0, r);
            insertNonFull(s, key, offset);
        } else {
            insertNonFull(r, key, offset);
        }
    }

    private void insertNonFull(Node x, String key, int offset) {
        if (x.isLeaf) {
            if (!x.keys.containsKey(key)) {
                x.keys.put(key, new ArrayList<>());
            }
            x.keys.get(key).add(offset);
        } else {
            int i = 0;
            String lastKey = "";
            for (String k : x.keys.keySet()) {
                if (key.compareTo(k) < 0) break;
                lastKey = k;
                i++;
            }
            if (key.equals(lastKey)) {
                x.keys.get(lastKey).add(offset);
            } else {
                Node child = x.children.get(i);
                if (child.keys.size() == 2 * t - 1) {
                    splitChild(x, i, child);
                    if (key.compareTo(x.keys.lastKey()) > 0) {
                        i++;
                    }
                }
                insertNonFull(x.children.get(i), key, offset);
            }
        }
    }

    private void splitChild(Node parent, int i, Node y) {
        Node z = new Node(t, y.isLeaf);
        for (int j = 0; j < t - 1; j++) {
            z.keys.putAll(y.keys.tailMap((String) y.keys.keySet().toArray()[t]));
        }
        if (!y.isLeaf) {
            for (int j = 0; j < t; j++) {
                z.children.add(y.children.remove(t));
            }
        }
        parent.children.add(i + 1, z);
        parent.keys.put((String) y.keys.keySet().toArray()[t - 1], new ArrayList<>(y.keys.get((String) y.keys.keySet().toArray()[t - 1])));
        y.keys.keySet().removeAll(new ArrayList<>(y.keys.keySet()).subList(t - 1, y.keys.keySet().size()));
    }

    public ArrayList<Integer> search(String key) {
        return search(root, key);
    }

    private ArrayList<Integer> search(Node x, String key) {
        int i = 0;
        for (String k : x.keys.keySet()) {
            if (key.compareTo(k) <= 0)
                break;
            i++;
        }
        if (x.keys.containsKey(key)) {
            return x.keys.get(key);
        } else if (x.isLeaf) {
            return null;
        } else {
            return search(x.children.get(i), key);
        }
    }
}
