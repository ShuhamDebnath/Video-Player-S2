package com.example.videoplayers2.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.videoplayers2.R
import com.example.videoplayers2.adapters.VideoAdapter
import com.example.videoplayers2.adapters.VideoFolderAdapter
import com.example.videoplayers2.databinding.FragmentVideoBinding
import com.example.videoplayers2.models.VideoModel
import com.example.videoplayers2.viewmodels.ViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog

class VideoFragment : Fragment(), VideoAdapter.OnCLickListener {

    private val viewModel: ViewModel by activityViewModels()
    var videoFolderAdapter: VideoFolderAdapter? = null
    lateinit var binding: FragmentVideoBinding
    var folderName: String? = null
    var videoModelArrayListFolder = ArrayList<VideoModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentVideoBinding.inflate(inflater, container, false)


//        if (folderName != null) {
//            videoModelArrayListFolder = getVideoModelArrayList(requireContext(), folderName!!)
//        }
//        if (videoModelArrayListFolder.size > 0) {
//            Toast.makeText(
//                requireContext(),
//                videoModelArrayListFolder.size.toString() + "e",
//                Toast.LENGTH_SHORT
//            ).show()
//            videoFolderAdapter =
//                VideoFolderAdapter(viewModel, requireContext(), videoModelArrayListFolder)
//            binding.recyclerView.layoutManager = LinearLayoutManager(
//                requireContext(),
//                RecyclerView.VERTICAL,
//                false
//            )
//            binding.recyclerView.adapter = videoFolderAdapter
//        }
        folderName = viewModel.getTempFolderName()
        val list = viewModel.getVideoArrayList().value!!
        folderName?.let { getVideoForFolder(list, it) }

//        videoFolderAdapter?.updateList(videoModelArrayListFolder)
        videoFolderAdapter = VideoFolderAdapter(requireContext(), videoModelArrayListFolder, this)
        binding.recyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.recyclerView.adapter = videoFolderAdapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    fun getVideoForFolder(videoList: ArrayList<VideoModel>, name: String) {
        videoList.forEach {
            val path = it.path
            val lastIndex = path.lastIndexOf('/')
            val folderPath = path.substring(0, lastIndex)
            val firstIndex = folderPath.lastIndexOf('/')
            val folderName = folderPath.substring(firstIndex + 1, lastIndex)
            Log.d("TAG", "getVideoForFolder: folderName ${folderName}")

            if (folderName == name) {
                videoModelArrayListFolder.add(it)
            }

        }

    }

    override fun onFileClick(
        position: Int,
        videoModelArrayList: ArrayList<VideoModel>
    ) {
        Log.d("TAG", "onFileClick: ")
        viewModel.folderSending = true
        toVideoActivity(position,videoModelArrayList)
    }

    override fun onMoreClicked(
        position: Int,
        videoModelArrayList: ArrayList<VideoModel>,
        video: VideoModel
    ) {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.bottom_sheet_more, null)
        val play = view.findViewById<LinearLayout>(R.id.ll_pay)
        play.setOnClickListener{
            toVideoActivity(position,videoModelArrayList)
            dialog.dismiss()
        }
        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()
    }

    private fun toVideoActivity(position: Int, videoModelArrayList: ArrayList<VideoModel>) {
        val bundle = Bundle()
        bundle.putInt("position", position)
        bundle.putParcelableArrayList("videoList", videoModelArrayList)
        findNavController().navigate(R.id.action_videoFragment_to_exoPlayerMedia3, bundle)
    }


}