package dan277.notifyitemchangedproblem

import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import java.util.ArrayList

object Values {
    var playingPos = -1
}

object Strings {
    val rangeStart: ArrayList<Int> = ArrayList()
    val rangeEnd: ArrayList<Int> = ArrayList()
    val namesText: ArrayList<String> = ArrayList()
    val namesText1: ArrayList<String> = ArrayList()
    val namesArabic: ArrayList<String> = ArrayList()
    fun clear() {
        rangeStart.clear()
        rangeEnd.clear()
        namesText.clear()
        namesText1.clear()
        namesArabic.clear()
    }
}

class MainActivity : AppCompatActivity() {

    private var t1: TextToSpeech? = null
    private var t2: TextToSpeech? = null
    private val TTSID = "tts"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Strings.clear()
        addNames()

        t1 = TextToSpeech(this, TextToSpeech.OnInitListener { status ->
            if (status != TextToSpeech.ERROR) {
                t1?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        play_all.text = "Stop"
                    }

                    override fun onStop(utteranceId: String?, interrupted: Boolean) {
                        super.onStop(utteranceId, interrupted)
                        onDone(utteranceId)
                    }

                    override fun onDone(utteranceId: String?) {
                        play_all.text = "Play All"
                    }

                    override fun onError(utteranceId: String?) {}
//                    override fun onError(utteranceId: String?, errorCode: Int) {
//                        super.onError(utteranceId, errorCode)
//                    }
                })
            }
        })
        play_all.setOnClickListener {
            if (t1 == null) {
                return@setOnClickListener
            }
            val t1 = t1!!
            if (t1.isSpeaking) {
                t1.stop()
                if (play_all.text == "Stop") {
                    return@setOnClickListener
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                for (i in 0 until Strings.namesText.size) {
                    if (i == 0) {
                        t1.speak(Strings.namesText[i], TextToSpeech.QUEUE_FLUSH, null, TTSID)
                    } else {
                        t1.speak(Strings.namesText[i], TextToSpeech.QUEUE_ADD, null, TTSID)
                    }
                }
            } else {
                for (i in 0 until Strings.namesText.size) {
                    if (i == 0) {
                        @Suppress("DEPRECATION")
                        t1.speak(Strings.namesText[i], TextToSpeech.QUEUE_FLUSH, null)
                    } else {
                        @Suppress("DEPRECATION")
                        t1.speak(Strings.namesText[i], TextToSpeech.QUEUE_ADD, null)
                    }
                }
            }
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        t2 = TextToSpeech(this, TextToSpeech.OnInitListener { status ->
            if (status != TextToSpeech.ERROR) {
                t2?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        Log.d("onStart1", Values.playingPos.toString())
                        recyclerView.adapter.notifyItemChanged(Values.playingPos)
                    }

                    override fun onStop(utteranceId: String?, interrupted: Boolean) {
                        super.onStop(utteranceId, interrupted)
                        onDone(utteranceId)
                    }

                    override fun onDone(utteranceId: String?) {
                        Log.d("onDone", Values.playingPos.toString())
                        val temp = Values.playingPos
                        Values.playingPos = -1
                        // Causes problems
                        recyclerView.adapter.notifyItemChanged(temp)
                    }

                    override fun onError(utteranceId: String?) {}
//                    override fun onError(utteranceId: String?, errorCode: Int) {
//                        super.onError(utteranceId, errorCode)
//                    }
                })
//                t1?.setOnUtteranceCompletedListener {
//                    Values.playingPos = -1
////                    recyclerView.adapter.notifyItemChanged(Values.playingPos)
//                }
            }
        })
        recyclerView.adapter = RecyclerAdapter(recyclerView, Strings.namesText, Strings.namesText1, Strings.namesArabic, this, t1, t2)
    }

    private fun addNames() {
        for (str in resources.getStringArray(R.array.text_0)) {
            Strings.namesText.add(str)
        }
        for (str in resources.getStringArray(R.array.text_1)) {
            Strings.namesText1.add(str)
        }
        for (str in resources.getStringArray(R.array.arabic)) {
            Strings.namesArabic.add(str)
        }
    }
}
