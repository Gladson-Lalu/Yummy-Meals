package com.example.yummymeals.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.yummymeals.adapter.HistoryAdapter
import com.example.yummymeals.model.MenuItem
import com.example.yummymeals.model.OrderHistory
import com.example.yummymeals.util.ConnectionManager
import com.example.yummymeals.util.ConnectionReportAlertDialog
import org.json.JSONObject

class HistoryFragment : Fragment() {
    lateinit var recyclerViewOrderHistory: RecyclerView
    lateinit var layoutManager: LinearLayoutManager
    lateinit var rlNoOrderPlaced: RelativeLayout
    lateinit var rlProgressBar: RelativeLayout
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.history)
        layoutManager = LinearLayoutManager(activity as Context)
        recyclerViewOrderHistory = view.findViewById(R.id.recyclerOrderHistoryHome)
        rlNoOrderPlaced = view.findViewById(R.id.rlNoHistoryPlaced)
        rlProgressBar = view.findViewById(R.id.rlLoading)
        rlProgressBar.visibility = RelativeLayout.VISIBLE
        if (ConnectionManager().checkConnectivity(activity as Context)) {
            val queue = Volley.newRequestQueue(activity as Context)
            val userId = activity?.getSharedPreferences(
                getString(R.string.preferenceFile),
                Context.MODE_PRIVATE
            )?.getString("userId", null).toString()
            val url = "http://13.235.250.119/v2/orders/fetch_result/$userId"
            val jsonRequest = object : JsonObjectRequest(Method.GET, url, null, Response.Listener {
                try {
                    val objectData = it.getJSONObject("data")
                    if (objectData.getBoolean("success")) {
                        rlNoOrderPlaced.visibility = RelativeLayout.GONE
                        val dataArray = objectData.getJSONArray("data")
                        val orderList = ArrayList<OrderHistory>()
                        for (i in 0 until dataArray.length()) {
                            val foodItemList = ArrayList<MenuItem>()
                            val data = dataArray.getJSONObject(i)
                            val nameData = data.getString("restaurant_name")
                            val idData = data.getString("order_id")
                            val totalCostData = data.getString("total_cost")
                            val dateData = data.getString("order_placed_at")
                            val foodItemData = data.getJSONArray("food_items")
                            for (j in 0 until foodItemData.length()) {
                                val foodItems: JSONObject = foodItemData.getJSONObject(j)
                                val foodName = foodItems.getString("name")
                                val foodId = foodItems.getString("food_item_id")
                                val foodCost = foodItems.getString("cost")
                                val item = MenuItem(foodId, foodName, foodCost, "0")
                                foodItemList.add(item)
                            }
                            val orders = OrderHistory(
                                idData,
                                nameData,
                                totalCostData,
                                dateData,
                                foodItemList
                            )
                            orderList.add(orders)
                        }
                        recyclerViewOrderHistory.adapter =
                            HistoryAdapter(activity as Context, orderList)
                        recyclerViewOrderHistory.layoutManager = layoutManager
                    } else {
                        rlNoOrderPlaced.visibility = RelativeLayout.VISIBLE
                    }
                } catch (e: Exception) {
                    Toast.makeText(activity as Context, "'$e' error occurred", Toast.LENGTH_SHORT)
                        .show()
                }
                rlProgressBar.visibility = RelativeLayout.GONE
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
        return view
    }
}
