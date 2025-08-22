package ds;

//  long BST with insert and reverse-inorder (RVL) traversal
public class IntBST {

    public static class Node {
        public long data;
        public Node left, right;
        public Node(long data) {
            this.data = data;
            this.left = null;
            this.right = null;
        }
    }

    public Node root = null;

    // Insert long; duplicates go to the right subtree
    public void insert(long data) {
        Node n = new Node(data);
        if (root == null) {
            root = n;
            return;
        }
        Node cur = root;
        while (true) {
            if (data < cur.data) {
                if (cur.left == null) { cur.left = n; return; }
                cur = cur.left;
            } else { // data >= cur.data goes right
                if (cur.right == null) { cur.right = n; return; }
                cur = cur.right;
            }
        }
    }

    // Inorder (ascending)
    public void printInorder() {
        inorder(root);
        System.out.println();
    }

    private void inorder(Node node) {
        if (node == null) return;
        inorder(node.left);
        System.out.print(node.data + " ");
        inorder(node.right);
    }

    // Reverse inorder (descending) -> RVL
    public void printReverseInorder() {
        reverseInorder(root);
        System.out.println();
    }

    private void reverseInorder(Node node) {
        if (node == null) return;
        reverseInorder(node.right);
        System.out.print(node.data + " ");
        reverseInorder(node.left);
    }

    public Long min() {
        if (root == null) return null;
        Node t = root;
        while (t.left != null) t = t.left;
        return t.data;
    }

    public Long max() {
        if (root == null) return null;
        Node t = root;
        while (t.right != null) t = t.right;
        return t.data;
    }
}