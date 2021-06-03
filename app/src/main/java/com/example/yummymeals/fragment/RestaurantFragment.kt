package com.example.yummymeals.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.yummymeals.R
import com.example.yummymeals.activity.CartActivity
import com.example.yummymeals.adapter.RestaurantMenuAdapter
import com.example.yummymeals.database.DBItemASyncTask
import com.example.yummymeals.database.DBRestaurantASyncTask
import com.example.yummymeals.database.RestaurantEntity
import com.example.yummymeals.model.MenuItem
import com.example.yummymeals.util.ConnectionManager
import com.example.yummymeals.util.ConnectionReportAlertDialog
import org.json.JSONObject


class RestaurantFragment : Fragment() {
    lateinit var recyclerViewMenu: RecyclerView
    lateinit var layoutManager: LinearLayoutManager
    lateinit var btnProceed: Button
    private lateinit var progressBar: RelativeLayout
    private lateinit var imgFav: ImageView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_restaurant, container, false)
        recyclerViewMenu = view.findViewById(R.id.recyclerRestaurantItem)
        btnProceed = view.findViewById(R.id.btnProceedToCart)
        imgFav = view.findViewById(R.id.imgFavRestaurantItemMenuLayout)
        progressBar = view.findViewById(R.id.rlLoading)
        progressBar.visibility = RelativeLayout.VISIBLE
        layoutManager = LinearLayoutManager(activity as Context)
        btnProceed.visibility = Button.GONE
        val dataBundle = arguments
        val restaurantId = dataBundle?.getString("id")
        val restaurantName = dataBundle?.getString("name")
        (activity as AppCompatActivity).supportActionBar?.title = restaurantName
        DBItemASyncTask(activity?.applicationContext as Context, null, 4).execute()
        val entity = dataBundle?.getSerializable("restaurantEntity") as RestaurantEntity
        var isRestaurantFav = DBRestaurantASyncTask(
            activity?.applicationContext as Context,
            entity, 1
        ).execute().get()
        if (isRestaurantFav) {
            imgFav.setImageResource(R.drawable.ic_fav_2)
        } else {
            imgFav.setImageResource(R.drawable.ic_fav_boarder)
        }

        imgFav.setOnClickListener {
            if (isRestaurantFav) {
                if (DBRestaurantASyncTask(
                        activity?.applicationContext as Context,
                        entity,
                        3
                    ).execute().get()
                ) {
                    imgFav.setImageResource(R.drawable.ic_fav_boarder)
                    Toast.makeText(
                        activity as Context,
                        "$restaurantName is removed from favourites",
                        Toast.LENGTH_LONG
                    ).show()
                    isRestaurantFav = false
                }
            } else {
                if (DBRestaurantASyncTask(
                        activity?.applicationContext as Context,
                        entity,
                        2
                    ).execute().get()
                ) {
                    imgFav.setImageResource(R.drawable.ic_fav_2)
                    Toast.makeText(
                        activity as Context,
                        "$restaurantName is added to favourites",
                        Toast.LENGTH_LONG
                    ).show()
                    isRestaurantFav = true
                }
            }
        }

        if (ConnectionManager().checkConnectivity(activity as Context)) {

            val queue = Volley.newRequestQueue(activity as Context)
            val url = "http://13.235.250.119/v2/restaurants/fetch_result/+$restaurantId"
            val jsonRequest = object : JsonObjectRequest(Method.GET, url, null, Response.Listener {
                try {
                    btnProceed.setOnClickListener {
                        val intent = Intent(activity as Context, CartActivity::class.java)
                        intent.putExtra("data", dataBundle)
                        (activity as Context).startActivity(intent)
                    }
                    val objectData = it.getJSONObject("data")
                    if (objectData.getBoolean("success")) {
                        val dataArray = objectData.getJSONArray("data")

                        val itemList = ArrayList<MenuItem>()
                        for (i in 0 until dataArray.length()) {
                            val data: JSONObject = dataArray.getJSONObject(i)
                            val nameData = data.getString("name")
                            val idData = data.getString("id")
                            val priceData = data.getString("cost_for_one")
                            val restaurantIdData = data.getString("restaurant_id")
                            val item = MenuItem(idData, nameData, priceData, restaurantIdData)
                            itemList.add(item)
                        }
                        recyclerViewMenu.adapter =
                            RestaurantMenuAdapter(activity as Context, itemList, btnProceed)
                        recyclerViewMenu.layoutManager = layoutManager
                    } else {
                        Toast.makeText(
                            activity as Context, "Something went wrong",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } catch (e: Exception) {
                    Toast.makeText(activity as Context, "'$e' error occurred", Toast.LENGTH_SHORT)
                        .show()
                }
            }, Response.ErrorListener {
                Toast.makeText(activity as Context, "Some $it error occurred", Toast.LENGTH_SHORT)
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
        progressBar.visibility = RelativeLayout.GONE
        return view
    }
}




