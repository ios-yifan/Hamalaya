package com.androidstudy.himalaya.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidstudy.himalaya.R;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.ArrayList;
import java.util.List;

public class AlbumListAdapter extends RecyclerView.Adapter<AlbumListAdapter.InnerHolder> {

    private static final String TAG = "RecommendListAdapter";
    private List<Album> mData = new ArrayList<>();
    private onAlbumItemClickListener mOnAlbumItemClickListener;
    private onAlbumItemLongClickListener albumItemLongClickListener = null;

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommend,parent,false);

        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, final int position) {

        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnAlbumItemClickListener != null) {

                    int clickPos = (int) v.getTag();
                    mOnAlbumItemClickListener.onItemClick(position,mData.get(clickPos));
                }
                Log.d(TAG, "onClick: " + position);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                //true 表示消费掉该事件
                if (albumItemLongClickListener != null) {
                    int clickPos = (int) v.getTag();
                    albumItemLongClickListener.onItemLongClick(mData.get(clickPos));
                }
                return true;
            }
        });
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
            albumDesrcTv.setText(album.getAlbumIntro());
            albumPlayCountTv.setText(album.getPlayCount()+"");
            albumContentTv.setText(album.getIncludeTrackCount()+"");

        }
    }

    public void setAlbumItemClickListener(onAlbumItemClickListener listener){
        this.mOnAlbumItemClickListener = listener;
    }
    public interface onAlbumItemClickListener {
        void onItemClick(int position, Album data);
    }

    /**
     * item 长按的接口
     */
    public interface onAlbumItemLongClickListener{
        void onItemLongClick(Album album);
    }

    public void setAlbumItemLongClickListener(onAlbumItemLongClickListener listener){
        this.albumItemLongClickListener = listener;
    }
}
