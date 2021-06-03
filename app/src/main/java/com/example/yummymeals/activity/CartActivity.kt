package com.example.yummymeals.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.yummymeals.R
import com.example.yummymeals.adapter.CartItemAdapter
import com.example.yummymeals.database.DBGetItems
import com.example.yummymeals.model.MenuItem
import com.example.yummymeals.util.ConnectionManager
import com.example.yummymeals.util.ConnectionReportAlertDialog
import org.json.JSONArray
import org.json.JSONObject

class CartActivity : AppCompatActivity() {
    private lateinit var txtRestaurantName: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var btnPlaceOrder: Button
    lateinit var rlProgressBar: RelativeLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        setSupportActionBar(findViewById(R.id.toolbarCart))
        supportActionBar?.title = "My Cart"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        txtRestaurantName = findViewById(R.id.txtCartRestaurantName)
        recyclerView = findViewById(R.id.recyclerCartItems)
        rlProgressBar = findViewById(R.id.rlLoading)
        rlProgressBar.visibility = RelativeLayout.GONE
        linearLayoutManager = LinearLayoutManager(this)
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder)
        if (intent != null) {
            val dataBundle = intent.getBundleExtra("data")
            txtRestaurantName.text = dataBundle.getString("name")
            val restaurantId = dataBundle?.getString("id")
            val dataCartList = DBGetItems(applicationContext, "+$restaurantId").execute().get()
            var totalCost = 0
            val foodObject = JSONArray()
            val userId = getSharedPreferences(
                getString(R.string.preferenceFile),
                Context.MODE_PRIVATE
            ).getString("userId", null)
            if (dataCartList != null) {
                val itemsList = ArrayList<MenuItem>()
                for (i in dataCartList) {
                    foodObject.put(JSONObject().put("food_item_id", i.FoodId))
                    val foodItem = MenuItem(i.FoodId, i.FoodName, i.FoodCost, i.RestaurantId)
                    itemsList.add(foodItem)
                    totalCost += i.FoodCost.toInt()
                }
                val recyclerAdapter = CartItemAdapter(this, itemsList)
                recyclerView.adapter = recyclerAdapter
                recyclerView.layoutManager = linearLayoutManager
                btnPlaceOrder.text = getString(R.string.place_order, totalCost)
                btnPlaceOrder.setOnClickListener {
                    rlProgressBar.visibility = RelativeLayout.VISIBLE
                    if (ConnectionManager().checkConnectivity(this)) {
                        val queue = Volley.newRequestQueue(this)
                        val url = "http://13.235.250.119/v2/place_order/fetch_result/"
                        val jsonParams = JSONObject()
                        jsonParams.put("user_id", userId)
                        jsonParams.put("restaurant_id", restaurantId)
                        jsonParams.put("total_cost", totalCost.toString())
                        jsonParams.put("food", foodObject)
                        val jsonRequest = object :
                            JsonObjectRequest(Method.POST, url, jsonParams, Response.Listener {
                                try {
                                    val objectData = it.getJSONObject("data")
                                    if (objectData.getBoolean("success")) {
                                        val d = Dialog(this, R.style.ActionBar)
                                        d.setContentView(R.layout.layout_order_placed)
                                        d.setCancelable(false)
                                        d.show()
                                        val btnOk: Button = d.findViewById(R.id.btnOkOrderPlaced)
                                        btnOk.setOnClickListener {
                                            finish()
                                            val intent =
                                                Intent(this, HomeDashboardActivity::class.java)
                                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                            startActivity(intent)
                                        }
                                    } else {
                                        Toast.makeText(
                                            this,
                                            "Something went wrong",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(this, "'$e' error occurred", Toast.LENGTH_SHORT)
                                        .show()
                                }
                                rlProgressBar.visibility = RelativeLayout.GONE
                            }, Response.ErrorListener {
                                Toast.makeText(this, "Some $it error occurred", Toast.LENGTH_SHORT)
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
                        ConnectionReportAlertDialog().buildDialog(this)
                    }
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
