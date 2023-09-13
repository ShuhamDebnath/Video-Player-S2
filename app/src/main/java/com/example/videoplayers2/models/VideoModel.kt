package com.example.videoplayers2.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class VideoModel(
   var id: String,
   var path: String,
   var title: String,
   var fileName: String,
   var size: String?,
   var dateAdded: String,
   var duration: String
) : Parcelable