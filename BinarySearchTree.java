package ds;

import java.util.ArrayList;
import java.util.List;

// Simple Binary Search Tree with iterative insert and in-order traversal
public class BinarySearchTree<K extends Comparable<? super K>, V> {

    public class Node {
        public K key;
        public V value;
        public Node left, right;

        public Node(K key, V value) {
            this.key = key;
            this.value = value;
            this.left = null;
            this.right = null;
        }
    }

    public Node root = null;

    // Insert a node into BST (iterative, simple style)
    public void insert(K key, V value) {
        Node n = new Node(key, value);
        if (root == null) {
            root = n;
            return;
        }
        Node temp = root;
        while (true) {
            int cmp = key.compareTo(temp.key);
            if (cmp < 0) {
                if (temp.left == null) {
                    temp.left = n;
                    return;
                } else {
                    temp = temp.left;
                }
            } else if (cmp > 0) {
                if (temp.right == null) {
                    temp.right = n;
                    return;
                } else {
                    temp = temp.right;
                }
            } else {
                // Replace value if same key
                temp.value = value;
                return;
            }
        }
    }

    // Get value by key (iterative)
    public V get(K key) {
        Node temp = root;
        while (temp != null) {
            int cmp = key.compareTo(temp.key);
            if (cmp == 0) return temp.value;
            if (cmp < 0) temp = temp.left; else temp = temp.right;
        }
        return null;
    }

    // In-order traversal - returns values in key order
    public List<V> inOrderValues() {
        List<V> values = new ArrayList<>();
        inorder(root, values);
        return values;
    }

    public void inorder(Node node, List<V> out) {
        if (node == null) return;
        inorder(node.left, out);
        out.add(node.value);
        inorder(node.right, out);
    }

    // Helpers similar to the simple example
    public K minKey() {
        if (root == null) return null;
        Node t = root;
        while (t.left != null) t = t.left;
        return t.key;
    }

    public K maxKey() {
        if (root == null) return null;
        Node t = root;
        while (t.right != null) t = t.right;
        return t.key;
    }
}