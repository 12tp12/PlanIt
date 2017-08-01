package com.planit.planit.utils;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Created by HP on 26-Jun-17.
 */

public class Item {
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Map<String, Pair<String, String>> getQuantities() {
        return quantities;
    }

    public void setQuantities(Map<String, Pair<String, String>> quantities) {
        this.quantities = quantities;
    }

    private String title;
    private Map<String, Pair<String, String>> quantities;

    public Item(String title, String phoneNumber, String amount, String units){
        this.title = title;
        Pair<String, String> p = new Pair<>(amount, units);
        this.quantities = new Map<String, Pair<String, String>>() {
            @Override
            public int size() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean containsKey(Object key) {
                return false;
            }

            @Override
            public boolean containsValue(Object value) {
                return false;
            }

            @Override
            public Pair<String, String> get(Object key) {
                return null;
            }

            @Override
            public Pair<String, String> put(String key, Pair<String, String> value) {
                return null;
            }

            @Override
            public Pair<String, String> remove(Object key) {
                return null;
            }

            @Override
            public void putAll(@NonNull Map<? extends String, ? extends Pair<String, String>> m) {

            }

            @Override
            public void clear() {

            }

            @NonNull
            @Override
            public Set<String> keySet() {
                return null;
            }

            @NonNull
            @Override
            public Collection<Pair<String, String>> values() {
                return null;
            }

            @NonNull
            @Override
            public Set<Entry<String, Pair<String, String>>> entrySet() {
                return null;
            }
        };
        this.quantities.put(phoneNumber, p);
    }

}
