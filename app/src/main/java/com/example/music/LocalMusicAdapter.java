package com.example.music;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LocalMusicAdapter extends RecyclerView.Adapter<LocalMusicAdapter.LocalMusicViewHolder> {
    Context context;
    List<LocalMusicBean> mList;

    OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        public void OnItemClick(View view, int position);
    }

    public LocalMusicAdapter(Context context, List<LocalMusicBean> mList) {
        this.context = context;
        this.mList = mList;
    }

    @NonNull
    @Override
    public LocalMusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_local_music, parent, false);

        LocalMusicViewHolder holder = new LocalMusicViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull LocalMusicViewHolder holder, @SuppressLint("RecyclerView") int position) {
        LocalMusicBean musicBean = mList.get(position);
        holder.item_tv_number.setText(musicBean.getNumber());
        holder.item_tv_song.setText(musicBean.getSong());
        holder.item_tv_time.setText(musicBean.getTime());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.OnItemClick(view,position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void refreshData(List<LocalMusicBean> notes){
        this.mList = notes;
        notifyDataSetChanged();
    }

    class LocalMusicViewHolder extends RecyclerView.ViewHolder{

        TextView item_tv_time;
        TextView item_tv_song;
        TextView item_tv_number;

        public LocalMusicViewHolder(@NonNull View itemView) {
            super(itemView);
            item_tv_time = itemView.findViewById(R.id.item_tv_time);
            item_tv_song = itemView.findViewById(R.id.item_tv_song);
            item_tv_number = itemView.findViewById(R.id.item_tv_number);
        }
    }

}
