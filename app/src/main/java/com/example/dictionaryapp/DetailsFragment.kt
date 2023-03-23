package com.example.dictionaryapp

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.text.Layout.Alignment
import android.util.TypedValue
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import org.w3c.dom.Text

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DetailsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var word: String? = null
    private var urlSound: String? = null
    private var idx: Int? = 0
    private lateinit var tvWord: TextView
    private lateinit var btnClose: Button
    private lateinit var btnSpeaker: ImageButton
    private lateinit var wordDetailsList: MutableList<WordDetails>
    private lateinit var tvSynonyms: TextView
    private lateinit var tvAntonyms: TextView
    private lateinit var tvDefinition: TextView
    private lateinit var tvExample: TextView
    private lateinit var tvPoS: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            word = it.getString("Word")
            idx = it.getInt("Index")
            urlSound = it.getString("SoundURL")
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ResultsActivity) {
            wordDetailsList = context.wordDetailsList
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_details, container, false)
        tvWord = view.findViewById(R.id.tv_details_word)
        tvWord.text = word?.toUpperCase()
        tvWord.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
        tvWord.gravity = Gravity.CENTER_VERTICAL
        btnClose = view.findViewById(R.id.btn_close)
        btnSpeaker = view.findViewById(R.id.btn_details_speaker)
        tvSynonyms = view.findViewById(R.id.tv_synonyms)
        tvAntonyms = view.findViewById(R.id.tv_antonyms)
        tvDefinition = view.findViewById(R.id.tv_details_definition)
        tvExample = view.findViewById(R.id.tv_details_example)
        tvPoS = view.findViewById(R.id.tv_details_pos)

        tvPoS.text = wordDetailsList[idx!!].partOfSpeech

        if (wordDetailsList[idx!!].synonyms.size > 0) {
            var txt = ""
            val n = wordDetailsList[idx!!].synonyms.size
            for (i in 0 until n) {
                txt += wordDetailsList[idx!!].synonyms[i]
                if (i != n-1) {
                    txt = "$txt, "
                }
            }
            if(txt.isNotEmpty())
                tvSynonyms.text = txt
        }

        if (wordDetailsList[idx!!].antonyms.size > 0) {
            var txt = "Antonyms: "
            val n = wordDetailsList[idx!!].antonyms.size
            for (i in 0 until n) {
                txt += wordDetailsList[idx!!].antonyms[i]
                if (i != n-1) {
                    txt = "$txt, "
                }
            }
            if (txt.isNotEmpty())
                tvAntonyms.text = txt
        }

        if (wordDetailsList[idx!!].definitions[0].example.isNotEmpty())
            tvExample.text = wordDetailsList[idx!!].definitions[0].example
        tvDefinition.text = wordDetailsList[idx!!].definitions[0].definition

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnClose.setOnClickListener {
            val fragmentManager: FragmentManager? = fragmentManager
            val fragmentTransaction: FragmentTransaction? = fragmentManager?.beginTransaction()
            fragmentManager?.popBackStack()
            fragmentTransaction?.commit()
        }

        btnSpeaker.setOnClickListener {
            PlaySound(urlSound, MediaPlayer(), context).execute()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DetailsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}