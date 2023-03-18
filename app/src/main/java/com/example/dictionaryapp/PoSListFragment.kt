package com.example.dictionaryapp

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PoSListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PoSListFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var tvWord: TextView
    private var word: String? = null
    private lateinit var wordDetailsList: MutableList<WordDetails>
    private lateinit var recyclerView: RecyclerView
    val posList: MutableList<String> = mutableListOf()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ResultsActivity) {
            wordDetailsList = context.wordDetailsList
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            word = it.getString("Word")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_pos_list, container, false)
        tvWord = view.findViewById(R.id.tv_word)
        tvWord.text = word?.toUpperCase()
        recyclerView = view.findViewById(R.id.rv_pos_list)
        for (w in wordDetailsList) {
            posList.add(w.partOfSpeech)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = PoSListAdapter(wordDetailsList)
        println("Number of elements inside fragment: ${wordDetailsList.size}")
        adapter.setOnBtnClickListener(object : PoSListAdapter.OnBtnClickListener {
            override fun onBtnClick(item: WordDetails) {
                Toast.makeText(context, "Clicked ${item.partOfSpeech}", Toast.LENGTH_SHORT).show()
            }
        })

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PoSListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PoSListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}