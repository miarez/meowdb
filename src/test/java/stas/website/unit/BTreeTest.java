package stas.website.unit;

import java.util.ArrayList;

import org.junit.Test;

import stas.website.Index.Btree.BTree;
import stas.website.Utils.Utils;

public class BTreeTest {
    
    @Test
    public void test() {
        BTree tree = new BTree(2); // Create a B-Tree with minimum degree 2
        tree.insert("apply", 19);
        tree.insert("apply", 200);

        tree.insert("serp", 200);
        tree.insert("meow", 200);
        tree.insert("woof", 200);
        tree.insert("quack", 200);
        tree.insert("bill", 20);
        tree.insert("click", 200);
        tree.insert("apply", 200);
        tree.insert("sero", 200);
        tree.insert("stonks", 200);
        tree.insert("nostonks", 200);
        tree.insert("sad", 200);
        tree.insert("sam", 200);
        tree.insert("sappy", 200);
        tree.insert("sal", 200);        
        tree.insert("sally", 200);                
        tree.insert("saul", 200);                        
        tree.insert("happy", 200);
        tree.insert("click", 200);
        tree.insert("click", 200);
        tree.insert("bill", 200);
        tree.insert("weoo", 200);

        
        // Test search functionality
        ArrayList<Integer> offsets = tree.search("event1");
        System.out.println("Offsets for 'event1': " + offsets);
        offsets = tree.search("event2");
        System.out.println("Offsets for 'event2': " + offsets);


        tree.printTree(tree.root, "", true);


        
    }


}
