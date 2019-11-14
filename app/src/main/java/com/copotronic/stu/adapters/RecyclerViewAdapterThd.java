package com.copotronic.stu.adapters;


import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by touhidroid on 8/11/16.
 *
 * @author touhidroid
 */

public abstract class RecyclerViewAdapterThd<MODEL>
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                return null;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

            }
        };
    }

    private static final String TAG = RecyclerViewAdapterThd.class.getSimpleName();
    private ArrayList<MODEL> itemList;
    public RecyclerViewAdapterThd(ArrayList<MODEL> itemList) {
        this.itemList = itemList;
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    /**
     * Adds to the last of the list
     */
    public void add(MODEL object) {
        itemList.add(object);
        notifyItemInserted(getItemCount() - 1);
    }

    public void add(int position, MODEL object) {
        itemList.add(position, object);
        notifyItemInserted(position);
    }

    public void add(MODEL object, int position) {
        add(position, object);
    }

    // Data has been added to recyclerview from database
    // data has been brought from server
    // Need to check whether data has been added twice or not
    // Data has to be unique

    /**
     * Recommended to OVER-RIDE <br/>
     * This method compares objects using the toString() method, which is not likely to be dependable.
     * Over-ride this method to write your own comparison code.
     */
    public void addUniquely(ArrayList<MODEL> newList) {
//        int nsz = newList.size();
//        for (int i = 0; i < nsz; i++) {
//            boolean exist = false;
//            MODEL p = newList.get(i);
//            int curSz = itemList.size();
//            for (int j = 0; j < curSz; j++)
//                if (p.toString().equals(itemList.get(j).toString())) {
//                    exist = true;
//                    break;
//                }
//            if (!exist) {
//                itemList.add(p);
//                notifyItemInserted(curSz);
//            }
//        }

        List<MODEL> itemList = this.itemList;
        for (MODEL nm : newList) {
            boolean exists = false;
            for (int j = 0; j < itemList.size(); j++) {
                MODEL om = itemList.get(j);
                if (om.toString().equals(nm.toString())) {
                    exists = true;
                    break;
                }
            }
            if (!exists)
                add(nm);
        }
    }


    /*public void addUniquely(ArrayList<ParentModel> newList) {
        int nsz = newList.size();
        for (int i = 0; i < nsz; i++) {
            boolean exist = false;
            ParentModel p = newList.get(i);
            int curSz = itemList.size();
            for (int j = 0; j < curSz; j++)
                if (p.getId() == ((ParentModel) itemList.get(j)).getId()) {
                    exist = true;
                    break;
                }
            if (!exist) {
                itemList.add((MODEL) p);
                notifyItemInserted(curSz);
            }
        }
    }*/

    public void addAll(ArrayList<MODEL> newList) {
        int c = getItemCount();
        // Lg.i(TAG, "Adding all: Current count=" + c + ", \nNew count=" + newList.size());
        itemList.addAll(newList);
        int size = newList.size();
        if (c > 0)
            notifyItemRangeInserted(c - 1, size);
        else
            notifyItemRangeInserted(0, size);
    }

    public void remove(int position) {
        if (position > getItemCount())
            return;
        itemList.remove(position);
        notifyItemRemoved(position);
    }

    public void remove(MODEL object) {
        int pos = itemList.indexOf(object);
        itemList.remove(pos);
        notifyItemRemoved(pos);
    }

    public MODEL getItem(int position) {
        return itemList.get(position);
    }

    public void clear() {
        int itemCount = getItemCount();
        itemList.clear();
        notifyItemRangeRemoved(0, itemCount);
    }

    public ArrayList<MODEL> getAllItems() {
        return itemList;
    }

    public void refreshWith( ArrayList<MODEL> freshList) {
        itemList.clear();
        itemList.addAll(freshList);
        notifyDataSetChanged();
    }
}