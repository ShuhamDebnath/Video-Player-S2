package com.example.videoplayers2.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.videoplayers2.R
import com.example.videoplayers2.adapters.FolderAdapter
import com.example.videoplayers2.databinding.FragmentFolderBinding
import com.example.videoplayers2.viewmodels.ViewModel
import java.util.Locale

class FolderFragment : Fragment(), FolderAdapter.OnCLickListener {

    private lateinit var binding: FragmentFolderBinding
    val viewModel: ViewModel by activityViewModels()
    var folderAdapter: FolderAdapter? = null
    lateinit var search: EditText


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFolderBinding.inflate(inflater, container, false)

        folderAdapter = FolderAdapter(this)
        binding.recyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.recyclerView.adapter = folderAdapter
        search = binding.etSearch

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getFolderNameArrayList().observe(viewLifecycleOwner) {
            Log.d("TAG", "onViewCreated: observer called")
            viewModel.getFolderNameArrayList().value?.let {
                folderAdapter?.updateList(it)
            }
        }



//        search.addTextChangedListener(object : TextWatcher {
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                Log.d("TAG", "onTextChanged: ")
//
//                Log.d("TAG", "onTextChanged: text = ${s}")
////                viewModel.getVideoArrayList().value?.forEach {
////                    Log.d("TAG", "onTextChanged: viewModel.getVideoArrayList() ${it.title} ")
////                }
//
//
//                val searchText = search.text.toString().trim()
//                viewModel.searchVideo(searchText)
////                val filteredList = viewModel.getFilteredVideoList()
//
//                val map = viewModel.getFolderNameArrayList().value
//                val keys = map?.keys
//                Log.d("TAG", "onTextChanged: ${keys}")
//                val filteredList = keys?.filter { folder ->
//                    folder.lowercase(Locale.ROOT).contains(searchText.lowercase(Locale.ROOT))
//                }
//                val tempMap = HashMap<String, Int>()
//                filteredList?.forEach {
//                    tempMap.put(it, map[it]!!)
//                }
//
//                Log.d("TAG", "onTextChanged: filteredList.size ${filteredList?.size} ")
//
//                viewModel.setSearchQuery(searchText)
////                viewModel.setFilteredVideoList(filteredList!!)
//
//
//                if (viewModel.getSearchQuery().isNotEmpty()) {
//                    Log.d(
//                        "TAG",
//                        "onTextChanged: viewModel.getSearchQuery() = ${viewModel.getSearchQuery()} "
//                    )
//                    Log.d("TAG", "onTextChanged: tempMap ${tempMap.size}")
//
//                    folderAdapter?.updateList(tempMap)
//                }
//                else {
//                    Log.d("TAG", "onTextChanged: isEmpty ${viewModel.getSearchQuery()}")
//                    Log.d("TAG", "onTextChanged: tempMap ${tempMap.size}")
//
//                    folderAdapter?.updateList(viewModel.getFolderNameArrayList().value!!)
//                }
//
//            }
//
//            override fun afterTextChanged(s: Editable?) {
//                // do nothing
//            }
//
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                // do nothing
//            }
//        })


    }

    override fun onFolderClick(folname: String) {
        viewModel.folderSending = true
        viewModel.setTempFolderName(folname)
        findNavController().navigate(R.id.action_homeFragment_to_videoFragment)
    }
}