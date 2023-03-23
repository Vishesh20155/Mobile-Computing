package com.example.dictionaryapp

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.AsyncTask
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.IOException
import java.net.URL

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
    private lateinit var btnSpeaker: ImageButton
    private var urlSound: String? = null
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
            urlSound = it.getString("SoundURL")
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
        tvWord.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
        tvWord.gravity = Gravity.CENTER_VERTICAL
        recyclerView = view.findViewById(R.id.rv_pos_list)
        btnSpeaker = view.findViewById(R.id.btn_speaker)
        btnSpeaker.setOnClickListener {
//            CallForSound(MediaPlayer()).execute()
//            PlaySound(urlSound, MediaPlayer(), context).execute()
            PlaySound(context, urlSound).execute()
        }
        for (w in wordDetailsList) {
            posList.add(w.partOfSpeech)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = PoSListAdapter(wordDetailsList)
//        println("Number of elements inside fragment: ${wordDetailsList.size}")
        adapter.setOnBtnClickListener(object : PoSListAdapter.OnBtnClickListener {
            override fun onBtnClick(item: WordDetails) {
                val detailsFragment = DetailsFragment()
                val bundle = Bundle()
                bundle.putString("Word", word)
                bundle.putString("SoundURL", urlSound)
                bundle.putInt("Index", wordDetailsList.indexOf(item))
                detailsFragment.arguments = bundle
                val fragmentManager: FragmentManager? = fragmentManager
                val fragmentTransaction: FragmentTransaction? = fragmentManager?.beginTransaction()
                fragmentTransaction?.replace(R.id.list_frag_container, detailsFragment)
                fragmentTransaction?.addToBackStack(null)
                fragmentTransaction?.commit()
//                detailsFragment.show(childFragmentManager, "popup")
                Toast.makeText(context, "Clicked ${item.partOfSpeech}", Toast.LENGTH_SHORT).show()
            }
        })

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

//    private inner class CallForSound(val mediaPlayer: MediaPlayer) : AsyncTask<Void, Void, Boolean>() {
//        override fun doInBackground(vararg p0: Void?): Boolean {
//            println("Inside do in BG. URL: $urlSound")
//            if ((urlSound == null) || (urlSound?.length==0)) return false
//            try {
//                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
//                mediaPlayer.setDataSource(urlSound)
//                mediaPlayer.prepare()
//                return true
//            }
//            catch (e: IOException) {
//                e.printStackTrace()
//            }
//            return false
//        }
//
//        override fun onPostExecute(result: Boolean?) {
//            super.onPostExecute(result)
//            if (result == true){
//                mediaPlayer.start()
//            }
//            else{
//                Toast.makeText(context, "Audio Link unavailable", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }

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