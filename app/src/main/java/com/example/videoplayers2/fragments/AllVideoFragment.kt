package com.example.videoplayers2.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.videoplayers2.R
import com.example.videoplayers2.adapters.VideoAdapter
import com.example.videoplayers2.databinding.FragmentAllVideoBinding
import com.example.videoplayers2.models.VideoModel
import com.example.videoplayers2.viewmodels.ViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Locale

class AllVideoFragment : Fragment(), VideoAdapter.OnCLickListener {

    lateinit var binding: FragmentAllVideoBinding
    var videoModelList = ArrayList<VideoModel>()
    var videoAdapter: VideoAdapter? = null
    private val viewModel: ViewModel by activityViewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var search: SearchView
    private lateinit var play: FloatingActionButton
    var searchList = ArrayList<VideoModel>()
    var isSearching = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAllVideoBinding.inflate(inflater, container, false)
        recyclerView = binding.recyclerView
        videoAdapter = VideoAdapter(requireContext(), this)
        recyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView.adapter = videoAdapter
        search = binding.svSearch
        play = binding.fabPlayLastVideo


        if (isSearching) {
            videoAdapter?.updateList(searchList)
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getVideoArrayList().observe(viewLifecycleOwner) {
            Log.d("TAG", "onViewCreated: observer called")
            Log.d("TAG", "onViewCreated: it $it")
            videoModelList = ArrayList()
            videoModelList.addAll(it)
            videoAdapter?.updateList(videoModelList)
        }
        searchView()

        play.visibility = View.GONE
    }

    override fun onFileClick(
        position: Int,
        videoModelArrayList: ArrayList<VideoModel>
    ) {
        Log.d("TAG", "onFileClick: ")
        viewModel.fileSending = true
        toVideoActivity(position, videoModelArrayList)
    }

    override fun onMoreClicked(
        position: Int,
        videoModelArrayList: ArrayList<VideoModel>,
        video: VideoModel
    ) {
        bottomSheetDialog(position, videoModelArrayList, video)

    }

    private fun bottomSheetDialog(
        position: Int,
        videoModelArrayList: ArrayList<VideoModel>,
        video: VideoModel
    ) {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.bottom_sheet_more, null)
        val title = view.findViewById<TextView>(R.id.tv_title)
        val play = view.findViewById<LinearLayout>(R.id.ll_pay)
        val rename = view.findViewById<LinearLayout>(R.id.ll_rename)
        val share = view.findViewById<LinearLayout>(R.id.ll_share)
        val delete = view.findViewById<LinearLayout>(R.id.ll_delete)
        val properties = view.findViewById<LinearLayout>(R.id.ll_properties)

        title.text = video.title
        play.setOnClickListener {
            toVideoActivity(position, videoModelArrayList)
            dialog.dismiss()
        }
        rename.setOnClickListener {
            Toast.makeText(requireContext(), "Coming Soon", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        share.setOnClickListener {

            dialog.dismiss()
            val uri = Uri.parse(video.path)
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "video/*"
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            startActivity(Intent.createChooser(intent, "Share video via "))

        }
        delete.setOnClickListener {
            Toast.makeText(requireContext(), "Coming Soon", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        properties.setOnClickListener {
            Toast.makeText(requireContext(), "Coming Soon", Toast.LENGTH_SHORT).show()
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
        findNavController().navigate(R.id.action_allVideoFragment_to_exoPlayerMedia3, bundle)
    }

    fun searchView() {
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    searchList = ArrayList()
                    videoModelList.forEach {
                        if (it.title.lowercase(Locale.ROOT)
                                .contains(newText.lowercase(Locale.ROOT))
                        ) {
                            searchList.add(it)
                        }
                    }
                    isSearching = true
                    videoAdapter?.updateList(searchList)

                } else if (newText == "") {
                    isSearching = false
                    if (videoModelList.size == 0) {
                        viewModel.getVideoArrayList().value?.let { videoModelList.addAll(it) }
                    }
                    videoAdapter?.updateList(videoModelList)

                }
//
                if (videoModelList.size == 0) {
                    viewModel.getVideoArrayList().value?.let { videoModelList.addAll(it) }
                }
//                Log.d("TAG", "onQueryTextChange: newText $newText")
//                Log.d("TAG", "onQueryTextChange: videoModelList ${videoModelList.size}")
//                Log.d(
//                    "TAG",
//                    "onViewCreated: getVideoArrayList() ${viewModel.getVideoArrayList().value?.size}"
//                )
                return true
            }
        })

    }

}