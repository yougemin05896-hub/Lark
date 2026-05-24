package com.example.presentation.player.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class VideoInfoDialog : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_video_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val uri = arguments?.getString("URI") ?: "Unknown"
        val size = arguments?.getString("SIZE") ?: "0 B"
        val res = arguments?.getString("RES") ?: "1080p"
        
        view.findViewById<TextView>(R.id.tv_path).text = "Path: $uri"
        view.findViewById<TextView>(R.id.tv_size).text = "Size: $size"
        view.findViewById<TextView>(R.id.tv_resolution).text = "Resolution: $res"
    }
    
    companion object {
        fun newInstance(uri: String, size: String, resolution: String): VideoInfoDialog {
            val dialog = VideoInfoDialog()
            val args = Bundle().apply {
                putString("URI", uri)
                putString("SIZE", size)
                putString("RES", resolution)
            }
            dialog.arguments = args
            return dialog
        }
    }
}
