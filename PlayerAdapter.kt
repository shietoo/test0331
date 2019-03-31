package markxie.game.randomselection.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_player.view.*
import markxie.game.randomselection.Fragment.User
import markxie.game.randomselection.R

class PlayerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var users: ArrayList<User> = ArrayList<User>()

    fun setData(users: ArrayList<User>) {
        this.users = users
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_player, parent, false))
    }

    override fun getItemCount(): Int = users.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, pos: Int) {
        if (holder is ViewHolder) {
            holder.bindView(users[pos])
        }
    }

    fun addUser(user: User) {
        users.add(user)
        notifyDataSetChanged()
    }

    fun clearUser() {
        users.clear()
        notifyDataSetChanged()
    }

    fun getList(): ArrayList<User> = users
}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val userNameTV = view.userNameTV
    private val greedyIV = view.greedyIV!!
    private val demandIV = view.demandIV!!

    fun bindView(user: User) {
        userNameTV.text = user.name

        if (user.isClick) {

            if (user.needed) {
                greedyIV.visibility = View.GONE
                demandIV.visibility = View.VISIBLE
            } else {
                greedyIV.visibility = View.VISIBLE
                demandIV.visibility = View.GONE
            }

        } else {
            greedyIV.visibility = View.VISIBLE
            demandIV.visibility = View.VISIBLE
        }

        greedyIV.setOnClickListener {
            user.isClick = true
            user.needed = false
            demandIV.visibility = View.GONE
        }
        demandIV.setOnClickListener {
            user.isClick = true
            user.needed = true
            greedyIV.visibility = View.GONE
        }

    }
}