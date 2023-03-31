package de.lightplugins.lighteconomyv5.utils;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Sorter {

    private HashMap<String, Double> list;

    public Sorter(HashMap<String, Double> list) {
        this.list = list;
    }

    public TreeMap<String, Double> get() {
        ValueComparator bvc = new ValueComparator(this.list);
        TreeMap<String, Double> sorted_map = new TreeMap<>(bvc);
        sorted_map.putAll(this.list);
        return sorted_map;
    }

    class ValueComparator implements Comparator<String> {
        Map<String, Double> base;

        public ValueComparator(HashMap<String, Double> map) {
            this.base = map;
        }

        public int compare(String a, String b) {
            if ((Double) this.base.get(a) >= (Double) this.base.get(b))
                return -1;
            return 1;
        }
    }
}
