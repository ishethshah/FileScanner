package com.ishita.filescanner.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;

import com.ishita.filescanner.model.ScanResults;
import com.ishita.filescanner.utils.AppUtils;

import java.io.File;

/**
 * Created by ishita on 5/29/17.
 */

public class FileScannerFragment extends Fragment {

    private OnFileScanListener mListener;

    private FileScannerTask mFileScannerTask;

    private boolean mRunning;

    public FileScannerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        setListener(context);
    }

    private void setListener(Context context) {
        if (context instanceof OnFileScanListener) {
            mListener = (OnFileScanListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void startFileScan() {
        if (AppUtils.isExternalStorageReadable()) {
            mFileScannerTask = new FileScannerTask();
            mFileScannerTask.execute();
            mRunning = true;
        } else {
            if (mListener != null) {
                mListener.showError();
            }
        }
    }

    public void cancelFileScan() {
        if (mFileScannerTask != null) {
            mFileScannerTask.cancel(true);
            mFileScannerTask = null;
            mRunning = false;
        }

    }

    public boolean isRunning() {
        return mRunning;
    }

    public interface OnFileScanListener {
        void onPreScan();

        void onPostScan(ScanResults results);

        void onCancelled();

        void showError();
    }

    private class FileScannerTask extends AsyncTask<Uri, Void, Void> {

        private String SDCARD_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath();

        private ScanResults mScanResults;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mListener != null) {
                mListener.onPreScan();
            }
            mRunning = true;
        }

        @Override
        protected Void doInBackground(Uri... uri) {
            mScanResults = new ScanResults();
            File root = new File(SDCARD_ROOT);
            getFile(root);
            mScanResults.extractResults();
            return null;
        }

        public void onPostExecute(Void none) {
            super.onPostExecute(none);
            if (mListener != null) {
                mListener.onPostScan(mScanResults);
            }
            mRunning = false;
        }

        @Override
        public void onCancelled() {
            if (mListener != null) {
                mListener.onCancelled();
            }
            mRunning = false;
        }

        private void getFile(File dir) {
            if (dir == null) {
                return;
            }

            File listFile[] = dir.listFiles();
            if (listFile != null && listFile.length > 0) {
                for (File file : listFile) {
                    if (file.isDirectory()) {
                        getFile(file);
                    } else {
                        String ext = AppUtils.getExtension(file.getName());
                        mScanResults.addScanResult(file.getName(), file.length(), ext);
                    }
                }
            }
        }
    }
}
