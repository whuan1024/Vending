package com.cloudminds.vending.ui;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cloudminds.vending.R;
import com.cloudminds.vending.utils.LogUtil;
import com.cloudminds.vending.vo.Commodity;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CommodityListFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private CommodityAdapter mCommodityAdapter;
    private List<Commodity> mCommodityList = new ArrayList<>();

    private CommodityAdapter.OnItemClickListener mOnItemClickListener = new CommodityAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(Commodity commodity) {
            LogUtil.d("[CommodityListFragment] commodity: " + commodity);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_commodity_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = view.findViewById(R.id.commodity_list_view);
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                                       @NonNull RecyclerView parent,
                                       @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.set(0, 0, 20, 40); //分别对应左、上、右、下的间隔
            }
        });
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2,
                RecyclerView.HORIZONTAL, false)); //网格布局，横向滑动，每一列放置2个item

        Commodity a1 = new Commodity("畅意100%乳酸菌饮品原味100ml",1,getContext().getDrawable(R.drawable.rusuanjun));
        Commodity a2 = new Commodity("畅意100%乳酸菌饮品原味100ml",2,getContext().getDrawable(R.drawable.rusuanjun));
        Commodity a3 = new Commodity("脉动维生素运动功能饮料小瓶400ml",3,getContext().getDrawable(R.drawable.rusuanjun));
        Commodity a4 = new Commodity("脉动维生素运动功能饮料小瓶400ml",4,getContext().getDrawable(R.drawable.rusuanjun));
        Commodity a5 = new Commodity("雀巢即饮雀巢咖啡饮料210ml",5,getContext().getDrawable(R.drawable.rusuanjun));
        Commodity a6 = new Commodity("雀巢即饮雀巢咖啡饮料210ml",6,getContext().getDrawable(R.drawable.rusuanjun));
        Commodity a7 = new Commodity("这是第七层",7,getContext().getDrawable(R.drawable.rusuanjun));
        Commodity a8 = new Commodity("这是第八层",8,getContext().getDrawable(R.drawable.rusuanjun));
        Commodity a9 = new Commodity("这是第九层",9,getContext().getDrawable(R.drawable.rusuanjun));

        mCommodityList.add(a1);
        mCommodityList.add(a2);
        mCommodityList.add(a3);
        mCommodityList.add(a4);
        mCommodityList.add(a5);
        mCommodityList.add(a6);
        mCommodityList.add(a7);
        mCommodityList.add(a8);
        mCommodityList.add(a9);

        mCommodityAdapter = new CommodityAdapter(getContext(), mCommodityList, mOnItemClickListener);
        mRecyclerView.setAdapter(mCommodityAdapter);
    }
}
