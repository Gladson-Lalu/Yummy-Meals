package com.example.yummymeals.fragment

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.RelativeLayout
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.yummymeals.R
import com.example.yummymeals.adapter.RestaurantsRecyclerAdapter
import com.example.yummymeals.database.DBGetRestaurants
import com.example.yummymeals.database.RestaurantEntity
import com.example.yummymeals.model.Restaurant
import com.example.yummymeals.util.ConnectionManager
import com.example.yummymeals.util.ConnectionReportAlertDialog
import com.google.android.material.appbar.AppBarLayout
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class HomeFragment : Fragment() {
    private lateinit var recyclerViewDashboard: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var rlProgressBar: RelativeLayout
    private lateinit var searchView: SearchView
    lateinit var recyclerAdapter: RestaurantsRecyclerAdapter
    private lateinit var appBarLayout: AppBarLayout
    var dataArray = JSONArray()
    val restaurantList = ArrayList<Restaurant>()
    private var restaurantsEntityList: List<RestaurantEntity>? = null

    private val priceComparator = Comparator<Restaurant> { res1, res2 ->
        if (res1.restaurantPrice.compareTo(res2.restaurantPrice) == 0) {
            res1.restaurantName.compareTo(res2.restaurantName, true)
        } else {
            res1.restaurantPrice.compareTo(res2.restaurantPrice)
        }
    }

    private val ratingComparator = Comparator<Restaurant> { res1, res2 ->
        if (res1.restaurantRating.compareTo(res2.restaurantRating) == 0) {
            res1.restaurantName.compareTo(res2.restaurantName, true)
        } else {
            res1.restaurantRating.compareTo(res2.restaurantRating)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.home)
        recyclerViewDashboard = view.findViewById(R.id.recyclerHomeDashboard)
        rlProgressBar = view.findViewById(R.id.rlLoading)
        rlProgressBar.visibility = RelativeLayout.VISIBLE
        linearLayoutManager = LinearLayoutManager(activity)
        appBarLayout = view.findViewById(R.id.appBarSearch)
        return view
    }

    override fun onResume() {
        super.onResume()
        if (dataArray.length() == 0) {
            if (ConnectionManager().checkConnectivity(activity as Context)) {
                val queue = Volley.newRequestQueue(activity as Context)
                val url = "http://13.235.250.119/v2/restaurants/fetch_result/"
                val jsonRequest =
                    object : JsonObjectRequest(Method.GET, url, null, Response.Listener {
                        try {
                            val objectData = it.getJSONObject("data")
                            if (objectData.getBoolean("success")) {
                                dataArray = objectData.getJSONArray("data")
                                initLayout()
                            } else {
                                Toast.makeText(
                                    activity as Context, "Something went wrong",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        } catch (e: Exception) {
                            Toast.makeText(
                                activity as Context,
                                "'$e' error occurred",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }, Response.ErrorListener {
                        Toast.makeText(
                            activity as Context,
                            "Some $it error occurred",
                            Toast.LENGTH_SHORT
                        )
                            .show()

                    }) {
                        override fun getHeaders(): MutableMap<String, String> {
                            val header = HashMap<String, String>()
                            header["Content-Type"] = "application/json"
                            header["token"] = "753effaaf3d43e"
                            return header
                        }
                    }
                queue.add(jsonRequest)
            } else {
                ConnectionReportAlertDialog().buildDialog(activity as Context)
            }
        } else {
            initLayout()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_home_dashboard, menu)
    }

    fun initLayout() {
        restaurantList.clear()
        restaurantsEntityList = DBGetRestaurants(
            activity?.applicationContext as Context
        ).execute().get()
        val restaurantsIdList = ArrayList<String>()
        if (restaurantsEntityList != null) {
            for (i in restaurantsEntityList!!) {
                restaurantsIdList.add(i.restaurant_id)
            }
        } else {
            Toast.makeText(
                activity as Context, "Something went wrong",
                Toast.LENGTH_SHORT
            ).show()
        }
        for (i in 0 until dataArray.length()) {
            val data: JSONObject = dataArray.getJSONObject(i)
            val nameData = data.getString("name")
            val ratingData = data.getString("rating")
            val idData = data.getString("id")
            val priceData = data.getString("cost_for_one")
            val urlData = data.getString("image_url")
            var isFav = false
            if (restaurantsIdList.contains(idData)) {
                isFav = true
            }
            val restaurant =
                Restaurant(nameData, priceData, idData, ratingData, urlData, isFav)
            restaurantList.add(restaurant)
        }
        recyclerAdapter =
            RestaurantsRecyclerAdapter(activity as Context, restaurantList)
        recyclerViewDashboard.layoutManager = linearLayoutManager
        recyclerViewDashboard.adapter = recyclerAdapter
        rlProgressBar.visibility = RelativeLayout.GONE
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_sort -> {
                val dialog = AlertDialog.Builder(activity as Context)
                dialog.setTitle("Sort By?")
                val list = resources.getStringArray(R.array.sort_filter)
                dialog.setSingleChoiceItems(list, -1) { _, i ->
                    when (i) {
                        0 -> {
                            Collections.sort(restaurantList, priceComparator)
                        }
                        1 -> {
                            Collections.sort(restaurantList, priceComparator)
                            restaurantList.reverse()
                        }
                        2 -> {
                            Collections.sort(restaurantList, ratingComparator)
                            restaurantList.reverse()
                        }
                    }
                }.setPositiveButton("OK") { dialogInterface, _ ->
                    recyclerAdapter.notifyDataSetChanged()
                    dialogInterface.dismiss()
                }.setNeutralButton("Cancel") { dialogInterface, _ ->
                    dialogInterface.cancel()
                }.create().show()
            }
            R.id.action_search -> {
                searchView = item.actionView as SearchView
                searchView.setIconifiedByDefault(false)
                searchView.queryHint = activity?.getString(R.string.search_restaurants)
                searchView.requestFocus()
                searchView.setIconifiedByDefault(false)
                searchView.isFocusable = true
                searchView.isIconified = false
                searchView.requestFocusFromTouch()
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(p0: String?): Boolean {
                        return false
                    }

                    override fun onQueryTextChange(p0: String?): Boolean {
                        val arrayList = ArrayList<Restaurant>()
                        for (i in restaurantList) {
                            if (i.restaurantName.startsWith(p0.toString(), true)) {
                                arrayList.add(i)
                            }
                        }
                        recyclerAdapter.updateList(arrayList)
                        return true
                    }

                })
                searchView.setOnQueryTextFocusChangeListener { _, b ->
                    if (!b) {
                        recyclerAdapter.updateList(restaurantList)
                        searchView.clearFocus()
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
