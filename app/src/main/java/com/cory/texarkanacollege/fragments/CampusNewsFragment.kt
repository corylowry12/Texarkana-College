package com.cory.texarkanacollege.fragments

import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.os.PersistableBundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cory.texarkanacollege.MainActivity
import com.cory.texarkanacollege.adapters.CampusNewsAdapter
import com.cory.texarkanacollege.R
import com.cory.texarkanacollege.adapters.ClassesAdapter
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import java.lang.NullPointerException

@OptIn(DelicateCoroutinesApi::class)
class CampusNewsFragment : Fragment() {

    private lateinit var gridLayoutManager: GridLayoutManager
    private val dataList = ArrayList<HashMap<String, String>>()
    private val selectedItems = ArrayList<HashMap<String, String>>()
    private lateinit var campusNewsAdapter: CampusNewsAdapter
    private lateinit var materialDialog : MaterialAlertDialogBuilder

    var state = Bundle()

    private lateinit var loadAllMaterialDialog : MaterialAlertDialogBuilder
    private lateinit var d : AlertDialog
    private lateinit var loadAllD : AlertDialog
    var loadPosition = 1
    private lateinit var recyclerViewState: Parcelable
    private lateinit var progressBar: ProgressBar
    var pageNumber = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_campus_news, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        campusNewsAdapter = CampusNewsAdapter(requireContext(), dataList)
        val topAppBar = view.findViewById<MaterialToolbar>(R.id.materialToolBarCampusNews)

        topAppBar.setNavigationOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        topAppBar?.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.loadAll -> {
                    if (loadPosition != pageNumber) {
                        loadAll()
                    }
                    else {
                        Toast.makeText(requireContext(), "Everything is already loaded", Toast.LENGTH_SHORT).show()
                    }
                    true
                }
                else -> false
            }
        }

        val recyclerView = requireActivity().findViewById<RecyclerView>(R.id.campusNewsRecyclerView)

        gridLayoutManager = if (resources.getBoolean(R.bool.isTablet)) {
            GridLayoutManager(requireContext(), 2)
        } else {
            GridLayoutManager(requireContext(), 1)
        }

        campusNewsAdapter = CampusNewsAdapter(requireContext(), dataList)

        if (isOnline(requireContext())) {
            loadIntoList()
        }
        else {
            Toast.makeText(requireContext(), getString(R.string.there_was_an_error_check_connection), Toast.LENGTH_SHORT).show()
            activity?.supportFragmentManager?.popBackStack()
        }

        search()

        val search = view.findViewById<TextInputEditText>(R.id.search)

        recyclerView?.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (search.text.toString() == "") {
                if (!recyclerView.canScrollVertically(1)) {
                    if (loadPosition != pageNumber) {
                        loadPosition++
                        recyclerViewState = recyclerView.layoutManager?.onSaveInstanceState()!!
                        loadMore()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "There is no more news",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                }
                else {
                    if (!recyclerView.canScrollVertically(1)) {
                        Toast.makeText(requireContext(), "Cant load more while searching", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    fun search() {
        val search = requireView().findViewById<TextInputEditText>(R.id.search)
        val recyclerView = requireActivity().findViewById<RecyclerView>(R.id.campusNewsRecyclerView)

        search?.setOnKeyListener(View.OnKeyListener { _, i, keyEvent ->
            if (i == KeyEvent.KEYCODE_BACK && keyEvent.action == KeyEvent.ACTION_DOWN) {
                search.clearFocus()
                hideKeyboard()
                return@OnKeyListener true
            }
            if (i == KeyEvent.KEYCODE_ENTER && keyEvent.action == KeyEvent.ACTION_UP) {
                hideKeyboard()
                search.clearFocus()
                return@OnKeyListener true
            }
            false
        })

        search?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                selectedItems.clear()
                try {
                    if (s.toString() != "") {
                        for (i in 0 until dataList.count()) {
                            if (dataList[i]["name"]!!.lowercase().contains(s.toString().lowercase())) {
                                selectedItems.add(dataList[i])
                            }
                            else {
                                recyclerView.adapter?.notifyItemRemoved(i)

                                campusNewsAdapter = CampusNewsAdapter(requireContext(), selectedItems)

                                recyclerView.adapter = campusNewsAdapter
                                recyclerView.invalidate()
                            }
                        }
                    }
                    else {
                        campusNewsAdapter =
                            CampusNewsAdapter(requireContext(), dataList)

                        recyclerView.adapter = campusNewsAdapter
                        recyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                selectedItems.clear()
                try {
                    if (s.toString() != "") {
                        for (i in 0 until dataList.count()) {
                            if (dataList[i]["name"]!!.lowercase().contains(s.toString().lowercase())) {
                                selectedItems.add(dataList[i])
                            }
                            else {
                                recyclerView.adapter?.notifyItemRemoved(i)
                                campusNewsAdapter = CampusNewsAdapter(requireContext(), selectedItems)

                                recyclerView.adapter = campusNewsAdapter
                                recyclerView.invalidate()
                            }
                        }
                    }
                    else {
                        campusNewsAdapter =
                            CampusNewsAdapter(requireContext(), dataList)

                        recyclerView.adapter = campusNewsAdapter
                        recyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                selectedItems.clear()
                try {
                    if (s.toString() != "") {
                        for (i in 0 until dataList.count()) {
                            if (dataList[i]["name"]!!.lowercase().contains(s.toString().lowercase())) {
                                selectedItems.add(dataList[i])
                            }
                            else {
                                recyclerView.adapter?.notifyItemRemoved(i)
                                campusNewsAdapter = CampusNewsAdapter(requireContext(), selectedItems)

                                recyclerView.adapter = campusNewsAdapter
                                recyclerView.invalidate()
                            }
                        }
                    }
                    else {
                        campusNewsAdapter =
                            CampusNewsAdapter(requireContext(), dataList)

                        recyclerView.adapter = campusNewsAdapter
                        recyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    private fun loadAll() {
        var stop = false
        val layout = layoutInflater.inflate(R.layout.fetching_all_dialog_layout, null)
        GlobalScope.launch(Dispatchers.Main) {
            loadAllMaterialDialog = MaterialAlertDialogBuilder(
                requireContext(), R.style.AlertDialogStyle)
            layout.findViewById<TextView>(R.id.body).text = "Fetching page ${loadPosition + 1} of $pageNumber"
            loadAllMaterialDialog.setCancelable(false)

            loadAllMaterialDialog.setView(layout)
            loadAllMaterialDialog.setNegativeButton(getString(R.string.cancel)) { d, _ ->
                d.dismiss()
                stop = true
            }
            loadAllD = loadAllMaterialDialog.create()
            loadAllD.show()

        }

        GlobalScope.launch(Dispatchers.IO) {

            for (i in (loadPosition+1)..pageNumber) {
                if (stop) {
                    loadPosition = i
                    break
                }
                val url = "https://www.texarkanacollege.edu/news/page/${i}/"

                val document = Jsoup.connect(url).get()
                val name = document.select("p.text-xl")
                val linkClass = document.select("div.flex-1")
                val link = linkClass.select("a.block.mt-2")
                val img = document.select("img")
                var imgLink = img.attr("src")
                val hidden = document.select("div.hidden")
                pageNumber = hidden.select("a.-mt-px").last().text().trim().toInt()

                for (z in 1 until name.count()) {
                    val map = HashMap<String, String>()
                    map["name"] = name[z].text()
                    try {
                        map["link"] = link[z - 1].attr("href").toString()
                        map["imgLink"] = img[z - 1].attr("src").toString()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    dataList.add(map)
                }
                GlobalScope.launch(Dispatchers.Main) {
                    val recyclerView =
                        activity?.findViewById<RecyclerView>(R.id.campusNewsRecyclerView)
                    recyclerView?.layoutManager = gridLayoutManager
                    recyclerView?.adapter = campusNewsAdapter
                    layout.findViewById<TextView>(R.id.body).text = "Fetching page ${i+1} of $pageNumber"
                }
                loadPosition = pageNumber
            }
            loadAllD.dismiss()
            search()
        }
    }

    private fun loadMore() {

        GlobalScope.launch(Dispatchers.Main) {
            progressBar = requireActivity().findViewById<ProgressBar>(R.id.progress_circular)
            progressBar.visibility = View.VISIBLE

        }

        GlobalScope.launch(Dispatchers.IO) {

            val url = "https://www.texarkanacollege.edu/news/page/${loadPosition}/"

            val document = Jsoup.connect(url).get()
            val name = document.select("p.text-xl")
            val linkClass = document.select("div.flex-1")
            val link = linkClass.select("a.block.mt-2")
            val img = document.select("img")
            var imgLink = img.attr("src")
            val hidden = document.select("div.hidden")
            pageNumber = hidden.select("a.-mt-px").last().text().trim().toInt()

            for (i in 1 until name.count()) {
                val map = HashMap<String, String>()
                map["name"] = name[i].text()
                try {
                    map["link"] = link[i - 1].attr("href").toString()
                    map["imgLink"] = img[i - 1].attr("src").toString()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                dataList.add(map)
            }
            GlobalScope.launch(Dispatchers.Main) {
                val recyclerView = activity?.findViewById<RecyclerView>(R.id.campusNewsRecyclerView)

                recyclerView?.adapter = campusNewsAdapter
                recyclerView?.layoutManager = gridLayoutManager
                recyclerView?.layoutManager?.onRestoreInstanceState(recyclerViewState)
                progressBar.visibility = View.GONE
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }

    private fun loadIntoList() {

        GlobalScope.launch(Dispatchers.Main) {
            materialDialog = MaterialAlertDialogBuilder(
                requireContext(), R.style.AlertDialogStyle)
            val layout = layoutInflater.inflate(R.layout.fetching_dialog_layout, null)
            materialDialog.setCancelable(false)
            materialDialog.setView(layout)
            materialDialog.setNegativeButton(getString(R.string.cancel)) { d, _ ->
                d.dismiss()
                activity?.supportFragmentManager?.popBackStack()
            }
            d = materialDialog.create()
            if (activity != null && !requireActivity().isFinishing) {
                d.show()
            }
        }

        GlobalScope.launch(Dispatchers.IO) {

            val url = "https://www.texarkanacollege.edu/news/page/${loadPosition}/"

            val document = Jsoup.connect(url).get()
            val name = document.select("p.text-xl")
            val linkClass = document.select("div.flex-1")
            val link = linkClass.select("a.block.mt-2")
            val img = document.select("img")
            var imgLink = img.attr("src")
            val hidden = document.select("div.hidden")
            pageNumber = hidden.select("a.-mt-px").last().text().trim().toInt()

                for (i in 1 until name.count()) {
                    val map = HashMap<String, String>()
                    map["name"] = name[i].text()
                    try {
                        map["link"] = link[i - 1].attr("href").toString()
                        map["imgLink"] = img[i - 1].attr("src").toString()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    dataList.add(map)
                }
            GlobalScope.launch(Dispatchers.Main) {
                val recyclerView = activity?.findViewById<RecyclerView>(R.id.campusNewsRecyclerView)
                recyclerView?.layoutManager = gridLayoutManager
                recyclerView?.adapter = campusNewsAdapter

                try {
                    d.dismiss()
                }
                catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun hideKeyboard() {
        try {
            val inputManager: InputMethodManager =
                activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            val focusedView = activity?.currentFocus

            if (focusedView != null) {
                inputManager.hideSoftInputFromWindow(
                    focusedView.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
    }

}