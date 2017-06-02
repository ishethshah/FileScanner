package com.ishita.filescanner.adapters;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.ishita.filescanner.R;
import com.ishita.filescanner.databinding.HeaderLayoutBinding;
import com.ishita.filescanner.databinding.ItemListLayoutBinding;
import com.ishita.filescanner.model.ListData;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by ishita on 5/29/17.
 */

public class FileScannerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;

    private static final int TYPE_ITEM = 1;

    private Set<Integer> mHeaderSet;

    private ArrayList<ListData> mListData;

    public FileScannerAdapter() {
        mHeaderSet = new TreeSet<>();
        mListData = new ArrayList<>();
    }

    public void reset() {
        mListData.clear();
        mHeaderSet.clear();
    }

    public void addItem(String item, String count) {
        mListData.add(new ListData(item, count));
    }

    public void addHeader(String title) {
        mListData.add(new ListData(title, null));
        mHeaderSet.add(mListData.size() - 1);
    }

    private boolean isHeader(int position) {
        return mHeaderSet.contains(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            HeaderLayoutBinding headerLayoutBinding = DataBindingUtil.inflate(LayoutInflater.
                    from(parent.getContext()), R.layout.header_layout, parent, false);
            //  View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_layout, parent, false);
            return new FileScannerAdapter.HeaderViewHolder(headerLayoutBinding);
        }

        ItemListLayoutBinding itemListLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_list_layout, parent, false);
        // View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_layout, parent, false);
        return new FileScannerAdapter.ItemViewHolder(itemListLayoutBinding);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ListData data = mListData.get(position);
        Log.v("SECTION_HEADER", "position: " + position);
        if (isHeader(position)) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            headerViewHolder.headerLayoutBinding.title.setText(data.getName());
        } else {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            itemViewHolder.itemListLayoutBinding.name.setText(data.getName());
            itemViewHolder.itemListLayoutBinding.count.setText(data.getCount());
        }
    }

    @Override
    public int getItemCount() {
        return mListData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return isHeader(position) ? TYPE_HEADER : TYPE_ITEM;
    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder {
        ItemListLayoutBinding itemListLayoutBinding;

        ItemViewHolder(ItemListLayoutBinding itemListLayoutBinding) {
            super(itemListLayoutBinding.getRoot());
            this.itemListLayoutBinding = itemListLayoutBinding;
        }
    }

    private static class HeaderViewHolder extends RecyclerView.ViewHolder {
        HeaderLayoutBinding headerLayoutBinding;

        HeaderViewHolder(HeaderLayoutBinding headerLayoutBinding) {
            super(headerLayoutBinding.getRoot());
            this.headerLayoutBinding = headerLayoutBinding;
        }
    }
}
