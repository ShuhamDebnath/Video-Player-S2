package com.example.videoplayers2.adapters

import android.app.Activity
import android.content.Context
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.videoplayers2.R
import com.example.videoplayers2.models.VideoModel
import java.io.File

class VideoFolderAdapter(
    var context: Context,
    var folderVideoModelArrayList: ArrayList<VideoModel>,
    private var onClickListener: VideoAdapter.OnCLickListener
) : RecyclerView.Adapter<VideoFolderAdapter.ViewHolder>() {



    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var thumbnail: ImageView
        var more: ImageView
        var fileName: TextView
        var Duration: TextView

        init {
            thumbnail = itemView.findViewById(R.id.iv_thumbnail)
            more = itemView.findViewById(R.id.iv_more)
            fileName = itemView.findViewById(R.id.tv_name)
            Duration = itemView.findViewById(R.id.tv_duration)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.video_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val videoModel = folderVideoModelArrayList[position]
        holder.fileName.text = videoModel.title
        Glide.with(context).load(File(videoModel.path)).into(holder.thumbnail)
//        holder.Duration.text = videoModel.duration
        holder.Duration.text = msToMin(videoModel.duration.toLong())
        holder.itemView.setOnClickListener {
            onClickListener.onFileClick(position, folderVideoModelArrayList)
        }
        holder.more.setOnClickListener {
            onClickListener.onMoreClicked(position, folderVideoModelArrayList, videoModel)
        }
    }


    override fun getItemCount(): Int {
        return folderVideoModelArrayList.size
    }

    interface OnCLickListener {
        fun onFileClick(position: VideoModel, position1: Int)
    }

    fun msToMin(ms: Long): String {
        val minutes = ms / (1000 * 60)
        val seconds = ms / 1000 % 60
        return String.format("%d:%02d", minutes, seconds)
    }

}