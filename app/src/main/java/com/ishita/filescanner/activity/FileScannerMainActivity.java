package com.ishita.filescanner.activity;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import com.ishita.filescanner.R;
import com.ishita.filescanner.databinding.ActivityFileScannerMainBinding;
import com.ishita.filescanner.adapters.FileScannerAdapter;
import com.ishita.filescanner.fragments.FileScannerFragment;
import com.ishita.filescanner.model.FileData;
import com.ishita.filescanner.model.ScanResults;
import com.ishita.filescanner.utils.AppUtils;
import com.ishita.filescanner.utils.PermissionsUtil;

import java.util.ArrayList;

/**
 * Created by ishita on 5/29/17.
 */

public class FileScannerMainActivity extends AppCompatActivity implements FileScannerFragment.OnFileScanListener {

    private static final int REQUEST_PERMISSIONS_CODE = 100;

    private static final String SCAN_RESULTS_KEY = "scan-results-key";

    private static final String SCAN_RESULTS_FRAGMENT_KEY = "scan-results-fragment";

    private ActivityFileScannerMainBinding mFileScannerBinding;

    private FileScannerFragment mFileScannerFragment;

    private ScanResults mScanResults;

    private boolean isPermissionDisplayed;


    private void initScanResults() {
        mFileScannerBinding.emptyMessage.setVisibility(View.GONE);
        mFileScannerBinding.scanResults.setLayoutManager(new LinearLayoutManager(this));
        mFileScannerBinding.scanResults.setAdapter(new FileScannerAdapter());

        mFileScannerFragment = (FileScannerFragment) getSupportFragmentManager().findFragmentByTag(SCAN_RESULTS_FRAGMENT_KEY);
        if (mFileScannerFragment == null) {
            mFileScannerFragment = new FileScannerFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(mFileScannerFragment, SCAN_RESULTS_FRAGMENT_KEY).commit();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFileScannerBinding = DataBindingUtil.setContentView(this, R.layout.activity_file_scanner_main);
        mFileScannerBinding.setHandlers(this);
        initScanResults();
    }

    @Override
    public void onResume() {
        super.onResume();
        showProgress(mFileScannerFragment.isRunning());
        if (PermissionsUtil.isPermissionRequired(this)) {
            if (!isPermissionDisplayed) {
                showPermissionDialog();
            }
        } else {
            mFileScannerBinding.emptyMessage.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null
                && !mFileScannerFragment.isRunning()
                && !PermissionsUtil.isPermissionRequired(this)) {
            mScanResults = savedInstanceState.getParcelable(SCAN_RESULTS_KEY);
            updateScanResults();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mScanResults != null) {
            outState.putParcelable(SCAN_RESULTS_KEY, mScanResults);
        }
    }

    @Override
    public void onBackPressed() {
        mFileScannerFragment.cancelFileScan();
        super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSIONS_CODE:
                isPermissionDisplayed = true;
                if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    showOpenPermissionMessage();
                }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void onStartButtonClicked() {
        mFileScannerBinding.emptyMessage.setVisibility(View.GONE);
        if (PermissionsUtil.isPermissionRequired(this)) {
            showOpenPermissionMessage();
        } else {
            startFileScan();
        }
    }

    public void onStopButtonClicked() {
        mFileScannerFragment.cancelFileScan();
        mFileScannerBinding.actionStop.setEnabled(false);
        mFileScannerBinding.progressTitle.setText(getString(R.string.preparing_to_cancel));
    }

    @Override
    public void onPreScan() {
        showProgress(true);
    }

    @Override
    public void onPostScan(ScanResults results) {
        mScanResults = results;
        updateScanResults();
        showProgress(false);
    }

    @Override
    public void onCancelled() {
        showProgress(false);
        Log.v("cancelled", "cancelled");
        showMessage(getString(R.string.scan_cancelled));
        mFileScannerBinding.progressTitle.setText(getString(R.string.file_scan_in_progress));
    }

    @Override
    public void showError() {
        showProgress(false);
        showMessage(getString(R.string.unable_to_read_external_storage));
    }

    private void startFileScan() {
        if (mScanResults != null) {
            mScanResults.reset();
        }
        mFileScannerFragment.startFileScan();
    }

    private void showPermissionDialog() {
        if (PermissionsUtil.isRationaleRequired(this)) { // user already denied
            showExplanationDialog();
        } else {
            displaySystemPermissionDialog();
        }
    }

    private void displaySystemPermissionDialog() {
        ActivityCompat.requestPermissions(this, PermissionsUtil.PERMISSIONS, REQUEST_PERMISSIONS_CODE);
    }

    private void updateScanResults() {
        if (mScanResults != null && !mScanResults.isEmpty()) {
            updateResultsAdapter();
            mFileScannerBinding.scanResults.setVisibility(View.VISIBLE);
        } else {
            showMessage(getString(R.string.empty_sdcard));
        }
        invalidateOptionsMenu();
    }

    private void updateResultsAdapter() {
        FileScannerAdapter adapter = (FileScannerAdapter) mFileScannerBinding.scanResults.getAdapter();
        adapter.reset();
        adapter.addHeader(getString(R.string.scan_summary));
        adapter.addItem(getString(R.string.total_files_scanned), mScanResults.getTotalFilesScanned());
        adapter.addItem(getString(R.string.avg_file_size), mScanResults.getAvgFileSize());
        addLargestFilesToAdapter(adapter);
        mFileScannerBinding.scanResults.setVisibility(View.VISIBLE);
    }

    private void addLargestFilesToAdapter(FileScannerAdapter adapter) {
        adapter.addHeader(getString(R.string.top_10_largest_files));
        ArrayList<FileData> largestFiles = mScanResults.getLargestFiles();
        for (int i = largestFiles.size()-1 ; i >=0 ; i--) {
            FileData file = largestFiles.get(i);
            adapter.addItem(file.getName(), file.getSize());
        }
    }

    private void showExplanationDialog() {
        AlertDialog dlg = (new AlertDialog.Builder(this))
                .setCancelable(false)
                .setMessage(R.string.enable_permission)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        displaySystemPermissionDialog();
                    }
                }).create();
        dlg.show();
    }

    private void showProgress(boolean showProgress) {
        mFileScannerBinding.progressContainer.setVisibility(showProgress ? View.VISIBLE : View.GONE);
        mFileScannerBinding.actionStart.setEnabled(!showProgress);
        mFileScannerBinding.actionStop.setEnabled(showProgress);
    }

    private void showEmptyMessage() {
        mFileScannerBinding.scanResults.setVisibility(View.GONE);
        mFileScannerBinding.emptyMessage.setVisibility(View.VISIBLE);
    }

    private void showMessage(String message) {
        Snackbar.make(mFileScannerBinding.mainContainer, message, Snackbar.LENGTH_LONG).show();
    }

    private void showOpenPermissionMessage() {
        showEmptyMessage();
        Snackbar snackbar = Snackbar.make(mFileScannerBinding.mainContainer, R.string.storage_permission_required, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.open, new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                AppUtils.launchApplicationSettings(FileScannerMainActivity.this);
            }
        });
        snackbar.setActionTextColor(ContextCompat.getColor(this, R.color.orange));
        snackbar.show();
    }


}


