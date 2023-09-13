package com.example.videoplayers2.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.videoplayers2.R
import com.example.videoplayers2.databinding.ActivityMainBinding
import com.example.videoplayers2.viewmodels.ViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: ViewModel by viewModels()

    companion object {
        private const val REQUESTED_CODE = 120
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        permission()

        val bottomNavigationView = binding.bottomNavigationView
        val navController = findNavController(R.id.fragmentContainerView)
        bottomNavigationView.setupWithNavController(navController)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun permission() {
        Log.d("TAG", "permission: ")
        if (
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.READ_MEDIA_VIDEO
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            Toast.makeText(this, "permission granted", Toast.LENGTH_SHORT).show()
            viewModel.getAllVideoArrayList()

        } else if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.READ_MEDIA_VIDEO
            )
        ) {

            val alertDialog = AlertDialog.Builder(this)
                .setMessage("This app require read video permission to run")
                .setTitle("Permission Required")
                .setCancelable(false)
                .setPositiveButton("OK") { dialog, which ->
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(
                            Manifest.permission.READ_MEDIA_VIDEO
                        ), REQUESTED_CODE
                    )
                }
                .setNegativeButton("cancel") { dialog, which ->
                    dialog.dismiss()
                }
                .create()
            alertDialog.show()

        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_MEDIA_VIDEO
                ), REQUESTED_CODE
            )

        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d("TAG", "onRequestPermissionsResult: ")
        if (requestCode == REQUESTED_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(
                    this,
                    "permission granted, you can stream video ",
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.getAllVideoArrayList()
            } else if (!ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_MEDIA_VIDEO
                )
            ) {
                val alertDialog = AlertDialog.Builder(this)
                    .setMessage("This app require read video permission to run. please allow to run the app from setting")
                    .setTitle("Permission Required")
                    .setCancelable(false)
                    .setPositiveButton("Setting") { dialog, which ->

                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        startActivity(intent)
                        dialog.dismiss()
                    }
                    .setNegativeButton("cancel") { dialog, which ->
                        dialog.dismiss()
                    }
                    .create()
                alertDialog.show()
            } else {
                permission()
            }
        }
    }

}