package com.example.yummymeals.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.yummymeals.R
import com.example.yummymeals.database.DBRestaurantASyncTask
import com.example.yummymeals.database.RestaurantEntity
import com.example.yummymeals.fragment.RestaurantFragment
import com.example.yummymeals.model.Restaurant
import com.squareup.picasso.Picasso

class RestaurantsRecyclerAdapter(
    val context: Context,
    private var restaurantList: ArrayList<Restaurant>
) : RecyclerView.Adapter<RestaurantsRecyclerAdapter.DashboardViewHolder>() {

    class DashboardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgRestaurant: ImageView = view.findViewById(R.id.imgRestaurantRow)
        val txtRestaurantName: TextView = view.findViewById(R.id.txtRestaurantName)
        val txtRestaurantPrice: TextView = view.findViewById(R.id.txtRestaurantPrice)
        val imgRestaurantFav: ImageView = view.findViewById(R.id.imgRestaurantFav)
        val txtRestaurantRating: TextView = view.findViewById(R.id.txtRestaurantRating)
        val cardViewContent: CardView = view.findViewById(R.id.cardViewContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.custom_row_restaurants, parent, false)
        return DashboardViewHolder(view)
    }

    override fun getItemCount(): Int {
        return restaurantList.size
    }


    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        val getRestaurant = restaurantList[position]
        val id = getRestaurant.restaurantId
        val name = getRestaurant.restaurantName
        val price = getRestaurant.restaurantPrice
        val rating = getRestaurant.restaurantRating
        val url = getRestaurant.imageUrl
        var isFav = getRestaurant.isFav
        holder.txtRestaurantName.text = name
        holder.txtRestaurantPrice.text = ("$price/Person")
        holder.txtRestaurantRating.text = rating
        Picasso.get().load(url).error(R.drawable.res_image).into(holder.imgRestaurant)
        val restaurantEntity = RestaurantEntity(id, name, rating, url, price)
        if (isFav) {
            holder.imgRestaurantFav.setImageResource(R.drawable.ic_fav_2)
        } else {
            holder.imgRestaurantFav.setImageResource(R.drawable.ic_fav_boarder)
        }

        holder.imgRestaurantFav.setOnClickListener {
            if (isFav) {
                if (DBRestaurantASyncTask(
                        context.applicationContext,
                        restaurantEntity,
                        3
                    ).execute().get()
                ) {
                    holder.imgRestaurantFav.setImageResource(R.drawable.ic_fav_boarder)
                    isFav = false
                }
            } else {
                if (DBRestaurantASyncTask(
                        context.applicationContext,
                        restaurantEntity,
                        2
                    ).execute().get()
                ) {
                    holder.imgRestaurantFav.setImageResource(R.drawable.ic_fav_2)
                    isFav = true
                }
            }
        }
        holder.cardViewContent.setOnClickListener {
            val restaurantFragment = RestaurantFragment()
            val bundle = Bundle()
            bundle.putString("id", id)
            bundle.putString("name", name)
            bundle.putSerializable("restaurantEntity", restaurantEntity)
            restaurantFragment.arguments = bundle
            val fragmentTransaction =
                (context as FragmentActivity).supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.frameLayout, restaurantFragment, "resFrag")
                .addToBackStack("restaurantMenu").commit()
        }
    }

    fun updateList(list: ArrayList<Restaurant>) {
        restaurantList = ArrayList()
        restaurantList.addAll(list)
        notifyDataSetChanged()
    }
}
