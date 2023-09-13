package com.example.videoplayers2.viewmodels

import android.app.Application
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.videoplayers2.models.VideoModel
import com.google.android.exoplayer2.source.MediaSource
import java.io.File

class PlayerVideModel(private val application: Application) : AndroidViewModel(application) {


    private var position = -1;
    private var videoList = ArrayList<VideoModel>()
    private var uriList = ArrayList<Uri>()
    private var mediaSourceList = ArrayList<MediaSource>()


    fun getPosition(): Int {
        return position
    }

    fun setPosition(position: Int) {
        this.position = position
    }

    fun getVideoList(): ArrayList<VideoModel> {
        return videoList
    }

    fun setVideoList(videoList: ArrayList<VideoModel>) {
        this.videoList = videoList
    }

    fun getUriList(): ArrayList<Uri> {
        return uriList
    }

    fun getData(bundle: Bundle) {
        position = bundle.getInt("position")
        val list = bundle.getParcelableArrayList<VideoModel>("videoList")
        if (list != null) {
            Log.d("TAG", "onCreate: list size = ${list.size}")
            videoList = list
        }

    }

    fun generateVideoUriList() {
        videoList.forEach {
            val uri = Uri.fromFile(File(it.path))
            uriList.add(uri)
        }
    }


}