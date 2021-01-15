package com.androidstudy.himalaya.adapters;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.androidstudy.himalaya.R;
import com.androidstudy.himalaya.base.BaseApplication;
import com.androidstudy.himalaya.views.DPopWindow;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.InnerHolder> {

    private List<Track> mData = new ArrayList<>();
    private int playIngIndex = 0;
    private DPopWindow.PlayListItemClickListener mItemClickListener = null;

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_play_list, parent, false);
        return new InnerHolder(inflate);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, final int position) {

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onClickItem(position);
                }
            }
        });
        //设置数据
        Track track = mData.get(position);
        TextView trackTitle = holder.itemView.findViewById(R.id.track_tv);
        trackTitle.setText(track.getTrackTitle());
        trackTitle.setTextColor(playIngIndex == position ? BaseApplication.getAppContext().getColor(R.color.sec_color):BaseApplication.getAppContext().getColor(R.color.three_color));

        View playingView = holder.itemView.findViewById(R.id.track_iv);
        playingView.setVisibility(playIngIndex == position ? View.VISIBLE : View.GONE);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(List<Track> data) {
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public void setCurrentPlayPosition(int playPosition) {
        playIngIndex = playPosition;
        notifyDataSetChanged();
    }

    public void setOnClickItemListener(DPopWindow.PlayListItemClickListener listener){
        this.mItemClickListener = listener;
    }

    public class InnerHolder extends RecyclerView.ViewHolder{
        public InnerHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
