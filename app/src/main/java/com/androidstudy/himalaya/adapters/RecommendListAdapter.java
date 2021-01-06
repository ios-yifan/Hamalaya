package com.androidstudy.himalaya.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidstudy.himalaya.R;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.ArrayList;
import java.util.List;

public class RecommendListAdapter extends RecyclerView.Adapter<RecommendListAdapter.InnerHolder> {

    private List<Album> mData = new ArrayList<>();

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommend,parent,false);

        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {

        holder.itemView.setTag(position);
        holder.setData(mData.get(position));

    }

    @Override
    public int getItemCount() {
        //返回要显示的个数
        if (mData != null) {
            return mData.size();
        }
        return 0;
    }


    public void setData(List<Album> albumList) {


        if (albumList != null) {
            mData.clear();
            mData.addAll(albumList);
        }
        notifyDataSetChanged();
    }

    public class InnerHolder extends RecyclerView.ViewHolder{
        public InnerHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void setData(Album album) {

            ImageView albumCoverIv = itemView.findViewById(R.id.album_cover);
            TextView albumTitleTv = itemView.findViewById(R.id.album_title_tv);
            TextView albumDesrcTv = itemView.findViewById(R.id.album_subtitle_tv);
            TextView albumPlayCountTv = itemView.findViewById(R.id.album_play_count);
            TextView albumContentTv = itemView.findViewById(R.id.album_content_size);


            Picasso.with(itemView.getContext()).load(album.getCoverUrlLarge()).into(albumCoverIv);
            albumTitleTv.setText(album.getAlbumTitle());
            albumDesrcTv.setText(album.getPriceTypeDetails());
            albumPlayCountTv.setText(album.getPlayCount()+"");
            albumContentTv.setText(album.getIncludeTrackCount()+"");

        }
    }
}
