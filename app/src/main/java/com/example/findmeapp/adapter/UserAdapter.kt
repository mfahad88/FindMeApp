package com.example.findmeapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.findmeapp.databinding.ItemUserBinding
import com.example.findmeapp.model.User

class UserAdapter() : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {
    var userList: List<User>? = null

    fun addItem(userList: List<User>){
        this.userList = userList
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList?.get(position)
        if (user != null) {
            holder.bind(user)
        }
    }

    override fun getItemCount(): Int {
        return if(userList==null){
            0
        }else{
            userList!!.size
        }
    }

    inner class UserViewHolder(private val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.userNumberTextView.text = user.number
        }
    }
}
