package com.ishita.filescanner.model;

/**
 * Created by ishita on 5/29/17.
 */

public class ListData {

    private String mName;
    private String mCount;

    public ListData(String name, String count) {
        mName = name;
        mCount = count;
    }

    public String getName() {
        return mName;
    }

    public String getCount() {
        return mCount;
    }
}
