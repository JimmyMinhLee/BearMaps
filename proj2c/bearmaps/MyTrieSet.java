package bearmaps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyTrieSet implements TrieSet61B {

    private Node root;

    public MyTrieSet() {
        clear();
    }

    @Override
    public void clear() {
        root = new Node(null, false);
    }

    @Override
    public boolean contains(String key) {
        if (key == null || key.length() < 1) {
            return false;
        }

        Node curr = root;
        for (int i = 0, n = key.length(); i < n; i++) {
            char c = key.charAt(i);
            if (!curr.map.containsKey(c)) {
                return false;
            }
            curr = curr.map.get(c);
        }

        return curr.isKey;
    }

    @Override
    public void add(String key) {
        if (key == null || key.length() < 1) {
            return;
        }
        Node curr = root;
        for (int i = 0, n = key.length(); i < n; i++) {
            char c = key.charAt(i);
            if (!curr.map.containsKey(c)) {
                curr.map.put(c, new Node(c, false));
            }
            curr = curr.map.get(c);
        }
        curr.isKey = true;
    }

    public List<String> keysWithPrefix(String prefix) {
        List<String> result = new ArrayList<>();
        if (prefix == null || prefix.length() < 1) {
            return result;
        }

        Node curr = root;
        for (int i = 0, n = prefix.length(); i < n; i++) {
            char c = prefix.charAt(i);
            if (!curr.map.containsKey(c)) {
                return result;
            }
            curr = curr.map.get(c);
        }

        collect(prefix, result, curr);

        return result;
    }

    private void collect(String prefix, List<String> lst, Node n) {
        if (n.isKey) {
            lst.add(prefix);
        }

        for (Node child : n.map.values()) {
            collect(prefix + child.c, lst, child);
        }
    }

    @Override
    public String longestPrefixOf(String s) {
        throw new UnsupportedOperationException();
    }

    private class Node {
        Character c;
        boolean isKey;
        HashMap<Character, Node> map;

        Node(Character ch, boolean k) {
            c = ch;
            isKey = k;
            map = new HashMap<>();
        }
    }
}
