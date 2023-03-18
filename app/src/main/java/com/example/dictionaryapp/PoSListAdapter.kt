package com.example.dictionaryapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PoSListAdapter (private val wordDetailsList: List<WordDetails>):
    RecyclerView.Adapter<PoSListAdapter.ViewHolder>() {

    private lateinit var tvPoS: TextView
    private lateinit var btnShowDetail: Button

    interface OnBtnClickListener {
        fun onBtnClick(item: WordDetails)
    }
    private var listener: OnBtnClickListener? = null

    fun setOnBtnClickListener(listener: OnBtnClickListener){
        this.listener = listener
    }

    inner class ViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(wordDetail: WordDetails, listener: OnBtnClickListener?) {
            tvPoS.text = wordDetail.partOfSpeech
            btnShowDetail.setOnClickListener {
                listener?.onBtnClick(wordDetail)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.pos_list_item, parent, false)
        tvPoS = view.findViewById(R.id.tv_pos)
        btnShowDetail = view.findViewById(R.id.btn_show_detail)
        println("Number of elements inside adapter: ${wordDetailsList.size}")
        return ViewHolder(view)
    }

    override fun getItemCount() = wordDetailsList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(wordDetailsList[position], listener)
    }
}