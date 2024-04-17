package stas.website.Core;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import stas.website.Index.Btree.BTree;
import stas.website.Index.Btree.BTree.Node;
import stas.website.Utils.Utils;

public class IndexManager {

        // Persist to disk
    public static void commit(
        String table_name,
        String index_name,
        BTree tree
    ) {
        String directory_path = "index/" + table_name;
        
        // Create a File object for the directory
        File directory = new File(directory_path);

        
        // Ensure the directory exists
        if (!directory.exists()) {
            directory.mkdirs(); // Make the directory including any necessary but nonexistent parent directories
        }
        String file_path = directory_path + "/" +index_name+".json";

        Gson gson = new Gson();
        String json = gson.toJson(tree.root); // Serialize the root object to JSON
        try (FileWriter writer = new FileWriter(file_path)) {
            writer.write(json); // Write the JSON string to a file
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static BTree read(
        String table_name,
        String index_name
    ) {
        String file_path = "index/"+ table_name + "/" +index_name+".json";
        BTree tree = new BTree(2);
        int t = tree.t;
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(file_path)) {
            java.lang.reflect.Type type = new TypeToken<Node>(){}.getType(); // Define the type of the data
            tree.root = gson.fromJson(reader, type); // Deserialize the JSON string to a Node object
            tree.root.t = tree.t; // Ensure the min degree t is set correctly
            updateTree(tree.root, t); // Update any necessary attributes in the tree
            // Utils.pp(this.root);
        } catch (IOException e) {
            Utils.pp("INDEX FILE NOT FOUND");
            // e.printStackTrace();
        }
        return tree;
    }
    
    private static void updateTree(Node node, int t) {
        // This method recursively ensures that all nodes have the correct min degree t
        node.t = t;
        if (!node.isLeaf) {
            for (Node child : node.children) {
                updateTree(child, t);
            }
        }
    }

    
}

