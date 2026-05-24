package com.example.presentation.player.dialogs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PlaybackSpeedDialog : BottomSheetDialogFragment() {

    private var listener: SpeedSelectedListener? = null

    interface SpeedSelectedListener {
        fun onSpeedSelected(speed: Float)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is SpeedSelectedListener) {
            listener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_playback_speed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        view.findViewById<TextView>(R.id.btn_speed_0_5).setOnClickListener {
            listener?.onSpeedSelected(0.5f)
            dismiss()
        }
        view.findViewById<TextView>(R.id.btn_speed_1_0).setOnClickListener {
            listener?.onSpeedSelected(1.0f)
            dismiss()
        }
        view.findViewById<TextView>(R.id.btn_speed_1_5).setOnClickListener {
            listener?.onSpeedSelected(1.5f)
            dismiss()
        }
        view.findViewById<TextView>(R.id.btn_speed_2_0).setOnClickListener {
            listener?.onSpeedSelected(2.0f)
            dismiss()
        }
    }
}
