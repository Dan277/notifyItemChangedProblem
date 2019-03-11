package dan277.notifyitemchangedproblem

import android.content.Context
import android.os.Build
import android.speech.tts.TextToSpeech
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import kotlinx.android.synthetic.main.row.view.*
import java.util.*


class RecyclerAdapter(private val recyclerView: RecyclerView, private val itemsText: ArrayList<String>, private val itemsText1: ArrayList<String>, private val itemsArabic: ArrayList<String>, val context: Context, val t1: TextToSpeech?, val t2: TextToSpeech?) : RecyclerView.Adapter<ViewHolder>(), View.OnClickListener {

    private var expanded = Array<Boolean?>(itemsText1.size) { null }
    //    private var playing = Array<Boolean>(itemsText1.size) { false }
//    private lateinit var playingHolder: ViewHolder
//    private lateinit var prevPlayingHolder: ViewHolder
    private val TTSID = "tts1"
//    private var playingPos = -1
//    private var prevPlayingPos = 0

    override fun getItemCount(): Int {
        return itemsText1.size
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.play_layout) {
            val holder = v.tag as ViewHolder

            val position = holder.adapterPosition
            Log.d("-----", "-----")
            var stopped = false
            if (t1?.isSpeaking == true) {
                t1.stop()
            }
            if (t2?.isSpeaking == true) {
                t2.stop()
                stopped = true
            }
            if (!stopped || Values.playingPos != position) {
//                playingHolder = holder
                Values.playingPos = position
                Log.d("onStart", Values.playingPos.toString())

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    t2?.speak(itemsText[position], TextToSpeech.QUEUE_FLUSH, null, TTSID)
                } else {
                    @Suppress("DEPRECATION")
                    t2?.speak(itemsText[position], TextToSpeech.QUEUE_FLUSH, null)
                }
            }
        }
        else if (v?.id == R.id.container_layout) {
            val holder = v.tag as ViewHolder
            expandCollapse(holder, true)
            val position = holder.adapterPosition
            notifyItemChanged(position)
        }
    }

//    init {
//
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row, parent, false)
        val holder = ViewHolder(view)
        holder.layoutPlay.setOnClickListener(this)
        holder.layoutPlay.tag = holder
        holder.layoutContainer.setOnClickListener(this)
        holder.layoutContainer.tag = holder
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (position < 9) {
            val str = " " + (position + 1).toString()
            holder.tvNumber.text = str
        } else {
            holder.tvNumber.text = (position + 1).toString()
        }
        holder.tvText1.text = itemsText[position]
        holder.tvText2.text = itemsText1[position]
        holder.tvArabic.text = itemsArabic[position]
        if (holder.tvText1.layout == null) {
            holder.tvText1.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    holder.tvText1.viewTreeObserver.removeOnPreDrawListener(this)
                    if (expandCollapse(holder)) {
                        notifyItemChanged(holder.adapterPosition)
                    }
                    return true
                }
            })
        } else {
            expandCollapse(holder)
//            notifyItemChanged(position)
        }
        if (Values.playingPos == position) {
            holder.cmdPlay.text = "■"
        }
        else {
            holder.cmdPlay.text = "▶"
        }
        holder.layoutPlay.layoutParams.width = 150

        when {
            expanded[position] == true -> {
                holder.imgExpand.visibility = View.GONE
                holder.imgCollapse.visibility = View.VISIBLE
                holder.tvText1.maxLines = Int.MAX_VALUE
                holder.tvArabic.maxLines = Int.MAX_VALUE
                holder.tvText2.maxLines = Int.MAX_VALUE
            }
            expanded[position] == false -> {
                holder.imgExpand.visibility = View.VISIBLE
                holder.imgCollapse.visibility = View.GONE
                holder.tvText1.maxLines = 1
                holder.tvArabic.maxLines = 1
                holder.tvText2.maxLines = 1
            }
            else -> {
                holder.imgExpand.visibility = View.GONE
                holder.imgCollapse.visibility = View.GONE
                holder.tvText1.maxLines = 1
                holder.tvArabic.maxLines = 1
                holder.tvText2.maxLines = 1
            }
        }
    }

    fun expandCollapse(holder: ViewHolder, toggle: Boolean = false): Boolean {
        val position = holder.adapterPosition
        if (position == -1) {
            return false
        }
        if (expanded[position] != null || holder.tvText1.layout.text.toString() != itemsText[position] || holder.tvArabic.layout.text.toString() != itemsArabic[position] || holder.tvText2.layout.text.toString() != itemsText1[position]) {
            if (expanded[position] == null) {
                expanded[position] = false
                return true
            }
            if (toggle) {
                expanded[position] = expanded[position] == false
                return true
            }
        }
        return false
    }
}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val layoutPlay = view.play_layout!!
    val cmdPlay = view.txt_play!!
    val layoutContainer = view.container_layout!!
    val tvNumber = view.txt_number!!
    val tvText1 = view.txt_1!!
    val tvText2 = view.txt_2!!
    val tvArabic = view.txt_arabic!!
    val imgExpand = view.image_expand_arrow!!
    val imgCollapse = view.image_collapse_arrow!!
}