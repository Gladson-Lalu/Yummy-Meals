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
import com.example.yummymeals.R
import com.example.yummymeals.adapter.RestaurantsRecyclerAdapter
import com.example.yummymeals.database.DBGetRestaurants
import com.example.yummymeals.model.Restaurant

class FavouritesFragment : Fragment() {

    private lateinit var recyclerViewDashboard: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var rlNoFavRestaurant: RelativeLayout
    private lateinit var rlProgressBar: RelativeLayout
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favourites, container, false)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.favourites)
        recyclerViewDashboard = view.findViewById(R.id.recyclerFavourites)
        rlNoFavRestaurant = view.findViewById(R.id.rlNoFavouriteRes)
        rlProgressBar = view.findViewById(R.id.rlLoading)
        rlProgressBar.visibility = RelativeLayout.VISIBLE
        linearLayoutManager = LinearLayoutManager(activity)
        val restaurantsList = DBGetRestaurants(
            activity?.applicationContext as Context
        ).execute().get()
        val recyclerRestaurantList = ArrayList<Restaurant>()

        if (restaurantsList != null) {
            if (restaurantsList.isNotEmpty()) {
                rlNoFavRestaurant.visibility = RelativeLayout.GONE
                for (i in restaurantsList) {
                    val restaurant = Restaurant(
                        i.restaurant_name,
                        i.restaurant_Price,
                        i.restaurant_id,
                        i.restaurant_rating,
                        i.restaurant_imgUrl,
                        true
                    )
                    recyclerRestaurantList.add(restaurant)
                }
                recyclerViewDashboard.adapter =
                    RestaurantsRecyclerAdapter(activity as Context, recyclerRestaurantList)
                recyclerViewDashboard.layoutManager = linearLayoutManager
            } else {
                rlNoFavRestaurant.visibility = RelativeLayout.VISIBLE
            }
            rlProgressBar.visibility = RelativeLayout.GONE
        } else {
            Toast.makeText(
                activity as Context, "Something went wrong",
                Toast.LENGTH_SHORT
            ).show()
        }
        return view
    }
}
