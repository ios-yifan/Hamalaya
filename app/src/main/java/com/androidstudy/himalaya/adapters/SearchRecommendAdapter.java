package com.androidstudy.himalaya.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidstudy.himalaya.R;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class SearchRecommendAdapter extends RecyclerView.Adapter<SearchRecommendAdapter.InnerHolder> {


    private List<QueryResult> mData = new ArrayList<>();
    private ItemClickListener itemClickListener;

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_recommend, parent, false);
        return new InnerHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {

        TextView text = holder.itemView.findViewById(R.id.search_recommend_tv);
        final QueryResult queryResult = mData.get(position);
        text.setText(queryResult.getKeyword());
        //设置点击事件
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                itemClickListener.onItemClick(queryResult.getKeyword());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    /**
     * 设置数据
     * @param keyWorkList
     */
    public void setData(List<QueryResult> keyWorkList) {

        mData.clear();
        mData.addAll(keyWorkList);
        notifyDataSetChanged();
    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        public InnerHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public void setItemClickListener(ItemClickListener listener){
        this.itemClickListener = listener;
    }
    public interface ItemClickListener{
        void onItemClick(String keyword);
    }
}
