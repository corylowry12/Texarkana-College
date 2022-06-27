package com.cory.texarkanacollege.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.cory.texarkanacollege.MainActivity
import com.cory.texarkanacollege.R
import com.github.barteksc.pdfviewer.PDFView
import com.google.android.material.appbar.MaterialToolbar

class CampusMapFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_campus_map, container, false)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolBar = activity?.findViewById<MaterialToolbar>(R.id.materialToolbarCampusMap)
        toolBar?.setNavigationOnClickListener {
            activity?.supportFragmentManager?.popBackStack()

            val runnable = Runnable {
                (context as MainActivity).campusMapWidgetExit()
            }
            MainActivity().runOnUiThread(runnable)
        }

        val pdfViewer = view.findViewById<PDFView>(R.id.pdfViewer)
        pdfViewer.enableAntialiasing(true)
        pdfViewer.fromAsset("campusmap.pdf")
            .onPageError { _, _ ->
                Toast.makeText(requireContext(), "There was an error loading file", Toast.LENGTH_SHORT).show()
            }
            .load()
    }
}
