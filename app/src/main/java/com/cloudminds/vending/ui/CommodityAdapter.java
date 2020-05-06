package com.cloudminds.vending.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cloudminds.vending.R;
import com.cloudminds.vending.vo.Commodity;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class CommodityAdapter extends RecyclerView.Adapter<CommodityAdapter.ViewHolder> {

    private Context mContext;
    private List<Commodity> mCommodityList;
    private OnItemClickListener mListener;

    public CommodityAdapter(Context context, List<Commodity> commodityList, OnItemClickListener listener) {
        mContext = context;
        mCommodityList = commodityList;
        mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Commodity commodity);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.commodity_item,
                parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Commodity commodity = mCommodityList.get(position);

        holder.name.setText(commodity.getName());
        holder.price.setText(String.format("¥%.2f", commodity.getPrice() / 100.0));
        holder.image.setImageDrawable(commodity.getImage());
        holder.itemView.setOnClickListener(v -> mListener.onItemClick(commodity));
    }

    @Override
    public int getItemCount() {
        return mCommodityList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView price;
        ImageView image;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price);
            image = itemView.findViewById(R.id.image);
        }
    }
}
