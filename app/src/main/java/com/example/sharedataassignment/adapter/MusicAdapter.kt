package com.example.sharedataassignment.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.sharedataassignment.model.MusicModel
import com.example.sharedataassignment.R

class MusicAdapter(private var ctx: Context, private var musicList:ArrayList<MusicModel>):
    RecyclerView.Adapter<MusicAdapter.MusicViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        val layoutInflater= LayoutInflater.from(ctx)
        return MusicViewHolder(layoutInflater.inflate(R.layout.item_music, parent, false))
    }

    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        val music=musicList[position]
        holder.tvSongName.text=music.songName
        holder.tvSongArtistName.text=music.artistName
    }
    override fun getItemCount(): Int {
        return musicList.size
    }

    class  MusicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvSongName: TextView = itemView.findViewById(R.id.tvSongName)
        var tvSongArtistName: TextView = itemView.findViewById(R.id.tvSongArtistName)
    }
}