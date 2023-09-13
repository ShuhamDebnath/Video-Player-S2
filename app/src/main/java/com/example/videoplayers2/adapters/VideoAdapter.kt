package com.example.videoplayers2.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.videoplayers2.R
import com.example.videoplayers2.models.VideoModel
import java.io.File

class VideoAdapter(
    var context: Context,
    private var onClickListener: VideoAdapter.OnCLickListener
) : RecyclerView.Adapter<VideoAdapter.ViewHolder>() {

    private var videoModelArrayList = ArrayList<VideoModel>()

    fun updateList(videoList: ArrayList<VideoModel>) {
        Log.d("TAG", "updateList: ${videoList.size}")
        videoModelArrayList.clear()
        videoModelArrayList = videoList
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var thumbnails: ImageView
        var more: ImageView
        var fileName: TextView
        var duration: TextView

        init {
            thumbnails = itemView.findViewById(R.id.iv_thumbnail)
            more = itemView.findViewById(R.id.iv_more)
            fileName = itemView.findViewById(R.id.tv_name)
            duration = itemView.findViewById(R.id.tv_duration)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.video_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val video = videoModelArrayList[position]
        holder.duration.text =msToMin(video.duration.toLong())
        holder.fileName.text = video.title
        Glide.with(context).load(File(video.path)).into(holder.thumbnails)
        holder.itemView.setOnClickListener {
            onClickListener.onFileClick(position,videoModelArrayList)
        }
        holder.more.setOnClickListener {
            onClickListener.onMoreClicked(position,videoModelArrayList,video)
        }
    }

    override fun getItemCount(): Int {
        return videoModelArrayList.size
    }



    fun msToMin(ms: Long): String {
        val minutes = ms / (1000 * 60)
        val seconds = ms / 1000 % 60
        return String.format("%d:%02d", minutes, seconds)
    }

    interface OnCLickListener {
        fun onFileClick(
            position1: Int,
            videoModelArrayList: ArrayList<VideoModel>
        )
        fun onMoreClicked(
            position: Int,
            videoModelArrayList: ArrayList<VideoModel>,
            video: VideoModel
        )
    }

}