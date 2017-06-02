package com.ishita.filescanner.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.ishita.filescanner.utils.AppUtils;

import java.util.ArrayList;
import java.util.PriorityQueue;

/**
 * Created by ishita on 5/29/17.
 */

public class ScanResults implements Parcelable {

    public static final Creator<ScanResults> CREATOR = new Creator<ScanResults>() {
        @Override
        public ScanResults createFromParcel(Parcel source) {
            return new ScanResults(source);
        }

        @Override
        public ScanResults[] newArray(int size) {
            return new ScanResults[size];
        }
    };
    private static final int LARGEST_FILES_LIMIT = 10;
    private ArrayList<FileData> mLargestFiles;
    private String mAverageFileSize = "";
    private PriorityQueue<FileData> mTempFileQueue;
    private long mAvgSize;
    private long mtotalFiles = 0;

    public ScanResults() {
        mTempFileQueue = new PriorityQueue<>();
        mLargestFiles = new ArrayList<>();
    }

    protected ScanResults(Parcel in) {
        this();
        this.mAvgSize = in.readLong();
        this.mAverageFileSize = in.readString();
        this.mLargestFiles = (ArrayList<FileData>) in.readSerializable();
        this.mTempFileQueue = (PriorityQueue<FileData>) in.readSerializable();
    }

    /**
     * Add file details to data store.
     *
     * @param name      file title
     * @param size      file size
     * @param extension file extension
     */
    public void addScanResult(String name, long size, String extension) {
        if (mTempFileQueue == null) {
            return;
        }
        mAvgSize += size;

        addFileDetails(mTempFileQueue, name, size);
        mtotalFiles++;
        Log.d("SCAN_RESULT_ADDED", "name: " + name + "\tsize: " + size + "\textension: " + extension);
        Log.d("SCAN_RESULT_ADDED", "size: " + mAvgSize);

    }

    /**
     * Extract results from temp data stores and save them in the appropriate data structures.
     */
    public void extractResults() {
        if (mTempFileQueue.isEmpty())
          return;

        mAvgSize = mAvgSize / mtotalFiles;
        Log.d("SCAN_RESULT_ADDED", "total files: " + mtotalFiles);
        mAverageFileSize = AppUtils.getConvertedSize(mAvgSize);
        saveLargestFilesInfo(mTempFileQueue);
        resetTempStore();
    }

    private void saveLargestFilesInfo(PriorityQueue<FileData> fileList) {
        if (fileList == null) {
            return;
        }
        while (!fileList.isEmpty()) {
            mLargestFiles.add(fileList.poll());
        }
    }

    private void resetTempStore() {
        mAvgSize = 0;
        mTempFileQueue.clear();

    }

    private void addFileDetails(PriorityQueue<FileData> queue, String name, long size) {
        queue.add(new FileData(name, size));
        if (queue.size() > LARGEST_FILES_LIMIT) {
            queue.poll();
        }
    }

    public String getTotalFilesScanned() {
        return String.valueOf(mtotalFiles);
    }

    /**
     * Get Average file size
     *
     * @return average file size
     */
    public String getAvgFileSize() {
        return mAverageFileSize;
    }

    /**
     * Get list of largest files.
     *
     * @return set of Files
     */
    public ArrayList<FileData> getLargestFiles() {
        return mLargestFiles;
    }

    /**
     * Reset global data store.
     */
    public void reset() {
        mAverageFileSize = "";
        mtotalFiles = 0;
        mLargestFiles.clear();
    }

    /**
     * Check to see if files are empty or not.
     *
     * @return flag to know whether results are available or not
     */
    public boolean isEmpty() {
        return (mLargestFiles == null || mLargestFiles.isEmpty());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mAvgSize);
        dest.writeString(this.mAverageFileSize);
        dest.writeSerializable(this.mLargestFiles);
        dest.writeSerializable(this.mTempFileQueue);
    }
}
