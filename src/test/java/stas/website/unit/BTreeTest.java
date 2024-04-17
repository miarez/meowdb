package stas.website.unit;

import java.util.ArrayList;

import org.junit.Test;

import stas.website.Core.IndexManager;
import stas.website.Index.Btree.BTree;
import stas.website.Utils.Utils;

public class BTreeTest {
    
    
    @Test
    public void test() {

        BTree tree = new BTree(2);
        tree.insert("apply", 1); 
        tree.insert("apply", 10); 
        tree.insert("click", 50); 

        ArrayList<Integer> offsets = tree.search("apply");
        System.out.println("Offsets for 'apply': " + offsets);
        tree.printTree(tree.root, "", true);
    }

    @Test
    public void read_from_file() {

        BTree tree = IndexManager.read("test_2", "event");
        Utils.pp("FROM READ");
        tree.printTree(tree.root, "", true);
    }


    @Test
    public void write_to_file() {
        BTree tree = new BTree(2);
        tree.insert("apply", 1); 
        tree.insert("apply", 10); 
        tree.insert("click", 50); 
        IndexManager.commit("test_2", "event",tree);
    }

    @Test 
    public void update_disk_index(){

        String index_name = "test_2";
        BTree tree = IndexManager.read("test_2", "event");
        Utils.pp("FROM READ");
        tree.printTree(tree.root, "", true);

        tree.insert("view", 9);                 
        tree.insert("click", 8);

        IndexManager.commit(index_name, "event", tree);
    }

    


}
