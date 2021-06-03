package com.example.yummymeals.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.yummymeals.R
import com.example.yummymeals.database.DBItemASyncTask
import com.example.yummymeals.database.ItemsEntity
import com.example.yummymeals.model.MenuItem

class RestaurantMenuAdapter(
    val context: Context,
    private val itemList: ArrayList<MenuItem>,
    val button: Button
) : RecyclerView.Adapter<RestaurantMenuAdapter.MenuItemViewHolder>() {


    class MenuItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtItemName: TextView = view.findViewById(R.id.txtItemName)
        val txtIemPrice: TextView = view.findViewById(R.id.txtItemPrice)
        val txtItemSNo: TextView = view.findViewById(R.id.txtSerialNo)
        val btnItemAdd: Button = view.findViewById(R.id.btnAddCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuItemViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.custom_row_restaurant_menu, parent, false)
        return MenuItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }


    override fun onBindViewHolder(holder: MenuItemViewHolder, position: Int) {
        val getItem = itemList[position]
        holder.txtItemName.text = getItem.itemName
        holder.txtIemPrice.text = context.getString(R.string.itemRupees, getItem.itemPrice)
        holder.txtItemSNo.text = (position + 1).toString()
        val itemEntity =
            ItemsEntity(getItem.itemId, getItem.itemName, getItem.itemPrice, getItem.restaurantId)
        var buttonClickFlag = false
        holder.btnItemAdd.setOnClickListener {

            if (!buttonClickFlag) {
                if (DBItemASyncTask(context.applicationContext, itemEntity, 2).execute().get()) {
                    buttonClickFlag = true
                    holder.btnItemAdd.text = context.getString(R.string.remove)
                    holder.btnItemAdd.setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.colorToolbar
                        )
                    )

                } else {
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
                }

            } else {
                if (DBItemASyncTask(context.applicationContext, itemEntity, 3).execute().get()) {
                    buttonClickFlag = false
                    holder.btnItemAdd.text = context.getString(R.string.add)
                    holder.btnItemAdd.setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.btnColor1
                        )
                    )
                    holder.btnItemAdd.setTextColor(Color.WHITE)
                } else {
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
                }
            }
            if (DBItemASyncTask(context.applicationContext, null, 1, getItem.restaurantId).execute()
                    .get()
            ) {
                button.visibility = Button.VISIBLE
            } else {
                button.visibility = Button.GONE
            }
        }
    }
}
