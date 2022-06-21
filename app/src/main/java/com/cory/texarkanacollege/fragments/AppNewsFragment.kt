package com.cory.texarkanacollege.fragments

import android.animation.LayoutTransition
import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cory.hourcalculator.adapters.KnownIssuesAdapter
import com.cory.hourcalculator.adapters.RoadmapAdapter
import com.cory.hourcalculator.adapters.UpdateAdapter
import com.cory.texarkanacollege.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class AppNewsFragment : Fragment() {

    private val client = OkHttpClient()
    private val dataList = ArrayList<HashMap<String, String>>()

    private val dataListKnownIssues = ArrayList<HashMap<String, String>>()

    private val dataListRoadMap = ArrayList<HashMap<String, String>>()

    private lateinit var alert : MaterialAlertDialogBuilder

    private var size: Int = 0
    private var sizeKnownIssues = 0
    private var sizeRoadMap = 0

    private var themeSelection = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_app_news, container, false)
    }

    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("CutPasteId")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val appBar = view.findViewById<MaterialToolbar>(R.id.materialToolBarUpdate)

        appBar?.setNavigationOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        val layout = requireView().findViewById<LinearLayout>(R.id.linearLayoutAppNews)
        val layoutTransition = layout.layoutTransition
        layoutTransition.enableTransitionType(LayoutTransition.CHANGING)

        val dialog = MaterialAlertDialogBuilder(
            requireContext(),
            R.style.AlertDialogStyle)
        val progressBar =
            ProgressBar(requireContext(), null, android.R.attr.progressBarStyleLarge)

        dialog.setTitle("Fetching The Latest App News...")
        dialog.setView(progressBar)
        dialog.setNegativeButton("Cancel") { d, _ ->
            d.dismiss()
            activity?.supportFragmentManager?.popBackStack()
        }
        val d = dialog.create()
        d.show()


        val topAppBar = activity?.findViewById<MaterialToolbar>(R.id.materialToolBarUpdate)

        topAppBar?.setNavigationOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        Handler(Looper.getMainLooper()).postDelayed({
            run("https://raw.githubusercontent.com/corylowry12/Texarkana-College/main/app_news.json")
        }, 100)

        Handler(Looper.getMainLooper()).postDelayed({
            d.dismiss()
        }, 1000)

        val updatesConstraint = requireView().findViewById<ConstraintLayout>(R.id.updatesConstraint)

        updatesConstraint.setOnClickListener {
            val updatesChevron = requireView().findViewById<ImageView>(R.id.updatesChevronImage)
            val recyclerViewUpdates = requireView().findViewById<RecyclerView>(R.id.updateRecyclerView)

            if (recyclerViewUpdates.visibility == View.VISIBLE) {
                recyclerViewUpdates.visibility = View.GONE
                updatesChevron.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
            }
            else {
                recyclerViewUpdates.visibility = View.VISIBLE
                updatesChevron.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
            }
        }

        val knownIssuesConstraint = requireView().findViewById<ConstraintLayout>(R.id.knownIssuesConstraint)

        knownIssuesConstraint.setOnClickListener {
            val knownIssuesChevron = requireView().findViewById<ImageView>(R.id.knownIssuesChevronImage)
            val recyclerViewKnownIssues = requireView().findViewById<RecyclerView>(R.id.knownIssuesRecyclerView)

            if (recyclerViewKnownIssues.visibility == View.VISIBLE) {
                recyclerViewKnownIssues.visibility = View.GONE
                knownIssuesChevron.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
            }
            else {
                recyclerViewKnownIssues.visibility = View.VISIBLE
                knownIssuesChevron.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
            }

        }
        val upcomingConstraint = requireView().findViewById<ConstraintLayout>(R.id.upcomingConstraint)

        upcomingConstraint.setOnClickListener {
            val upcomingChevron = requireView().findViewById<ImageView>(R.id.upcomingChevronImage)
            val recyclerViewRoadMap = requireView().findViewById<RecyclerView>(R.id.upcomingRecyclerView)

            if (recyclerViewRoadMap.visibility == View.VISIBLE) {
                recyclerViewRoadMap.visibility = View.GONE
                upcomingChevron.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
            }
            else {
                recyclerViewRoadMap.visibility = View.VISIBLE
                upcomingChevron.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
            }
        }
    }

    @DelicateCoroutinesApi
    fun run(url : String) {
        dataList.clear()
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                GlobalScope.launch(Dispatchers.Main) {
                    alert = MaterialAlertDialogBuilder(
                        requireContext(), R.style.AlertDialogStyle
                    )
                    alert.setTitle("Error")
                    alert.setMessage("There was an error fetching the latest news. Check your data connection.")
                    alert.setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                        activity?.supportFragmentManager?.popBackStack()
                    }
                    if (!alert.create().isShowing) {
                        alert.show()
                    }
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val strResponse = response.body()!!.string()
                val jsonContact = JSONObject(strResponse)
                val jsonArrayInfo: JSONArray = jsonContact.getJSONArray("update")

                size = jsonArrayInfo.length()

                for (i in 0 until size) {
                    val jsonObjectDetail: JSONObject =jsonArrayInfo.getJSONObject(i)

                    val arrayListDetails = HashMap<String, String>()
                    arrayListDetails["date"] = jsonObjectDetail.get("date").toString()
                    arrayListDetails["title"] = (jsonObjectDetail.get("title").toString())
                    arrayListDetails["body"] = (jsonObjectDetail.get("body").toString())
                    dataList.add(arrayListDetails)
                }

                val jsonArrayInfoKnownIssues: JSONArray = jsonContact.getJSONArray("known issues")

                sizeKnownIssues = jsonArrayInfoKnownIssues.length()

                for (i in 0 until sizeKnownIssues) {
                    val jsonObjectDetail: JSONObject =jsonArrayInfoKnownIssues.getJSONObject(i)

                    val arrayListDetails = HashMap<String, String>()
                    arrayListDetails["title"] = (jsonObjectDetail.get("title").toString())
                    dataListKnownIssues.add(arrayListDetails)

                }

                val recyclerViewKnownIssues = requireView().findViewById<RecyclerView>(R.id.knownIssuesRecyclerView)

                GlobalScope.launch(Dispatchers.Main) {
                    recyclerViewKnownIssues?.layoutManager = LinearLayoutManager(requireContext())
                    recyclerViewKnownIssues?.adapter = KnownIssuesAdapter(requireContext(), dataListKnownIssues)

                    val knownIssuesCounter = requireView().findViewById<TextView>(R.id.knownIssuesCounterTextView)
                    knownIssuesCounter.text = sizeKnownIssues.toString()
                }

                val recyclerViewUpdate = requireView().findViewById<RecyclerView>(R.id.updateRecyclerView)

                GlobalScope.launch(Dispatchers.Main) {
                    recyclerViewUpdate?.layoutManager = LinearLayoutManager(requireContext())
                    recyclerViewUpdate?.adapter = UpdateAdapter(requireContext(), dataList)

                    val updatesCounter = requireView().findViewById<TextView>(R.id.updatesCounterTextView)
                    updatesCounter.text = size.toString()
                }

                val jsonArrayInfoRoadMap: JSONArray = jsonContact.getJSONArray("roadmap")

                sizeRoadMap = jsonArrayInfoRoadMap.length()

                for (i in 0 until sizeRoadMap) {
                    val jsonObjectDetail: JSONObject =jsonArrayInfoRoadMap.getJSONObject(i)

                    val arrayListRoadMap = HashMap<String, String>()
                    arrayListRoadMap["title"] = (jsonObjectDetail.get("title").toString())
                    arrayListRoadMap["status"] = (jsonObjectDetail.get("status").toString())
                    dataListRoadMap.add(arrayListRoadMap)
                }

                val recyclerViewRoadMap = requireView().findViewById<RecyclerView>(R.id.upcomingRecyclerView)

                GlobalScope.launch(Dispatchers.Main) {
                    recyclerViewRoadMap?.layoutManager = LinearLayoutManager(requireContext())
                    recyclerViewRoadMap?.adapter = RoadmapAdapter(requireContext(), dataListRoadMap)

                    val upcomingFeatures = requireView().findViewById<TextView>(R.id.upcomingFeaturesCounterTextView)
                    upcomingFeatures.text = sizeRoadMap.toString()

                }

            }
        })
    }

}