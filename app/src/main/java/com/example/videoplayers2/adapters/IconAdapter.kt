package com.example.videoplayers2.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.videoplayers2.R
import com.example.videoplayers2.models.Icon
import com.example.videoplayers2.models.VideoModel

class IconAdapter(
    var context: Context,
    var iconList: ArrayList<Icon>,
    private var onClickIconListener: OnCLickIconListener
) : RecyclerView.Adapter<IconAdapter.ViewHolder>() {


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var icon: ImageView
        var title: TextView
        var layout : ConstraintLayout
        
        init {
            icon = itemView.findViewById(R.id.iv_icon)
            title = itemView.findViewById(R.id.tv_icon_title)
            layout = itemView.findViewById(R.id.cv_root)

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.each_icon, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val icon = iconList[position]
        holder.title.text = icon.title
        holder.icon.setImageResource(icon.image)
        holder.layout.setOnClickListener {
            onClickIconListener.onIconClick(icon,holder)
        }
    }

    override fun getItemCount(): Int {
        return iconList.size
    }

    interface OnCLickListener {
        fun onFileClick(position: VideoModel, position1: Int)
    }

    fun msToMin(ms: Long): String {
        val minutes = ms / (1000 * 60)
        val seconds = ms / 1000 % 60
        return String.format("%d:%02d", minutes, seconds)
    }

    interface OnCLickIconListener {
        fun onIconClick(icon: Icon, holder: ViewHolder)
    }
}