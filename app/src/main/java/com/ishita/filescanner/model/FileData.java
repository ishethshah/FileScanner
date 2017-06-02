package com.ishita.filescanner.model;

import com.ishita.filescanner.utils.AppUtils;

import java.io.Serializable;

/**
 * Created by ishita on 5/29/17.
 */

public class FileData implements Comparable<FileData>, Serializable {
    private String mName;
    private long mSize; // size in Kb

    public FileData(String name, long count) {
        mName = name;
        mSize = count;
    }

    @Override
    public int compareTo(FileData fileInfo) {
        if (mSize > fileInfo.mSize) {
            return 1;
        } else if (mSize < fileInfo.mSize) {
            return -1;
        }
        return 0;
    }

    public String getName() {
        return mName;
    }

    public long getSizeCount() {
        return mSize;
    }

    public String getSize() {
        return AppUtils.getConvertedSize(mSize);
    }

}
