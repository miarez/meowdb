package stas.website.Index.Btree;

import java.util.ArrayList;

public class KeyInfo {
    Integer key;
    ArrayList<Integer> offsets;

    public KeyInfo(Integer key, ArrayList<Integer> offsets) {
        this.key = key;
        this.offsets = offsets;
    }
}