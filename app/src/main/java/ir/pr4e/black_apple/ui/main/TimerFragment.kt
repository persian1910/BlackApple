package ir.pr4e.black_apple.ui.main

import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import ir.pr4e.black_apple.R
import ir.pr4e.black_apple.adapter.RecyclerFlag
import ir.pr4e.black_apple.databinding.FragmentTimerBinding
import ir.pr4e.black_apple.ui.MainActivity
import ir.pr4e.black_apple.ui.MainActivity.MainActivityHolder.mainActivity
import ir.pr4e.black_apple.utils.NumberPicker
import org.json.JSONArray
import www.sanju.motiontoast.MotionToastStyle
import java.util.Locale

class TimerFragment : Fragment(), OnFocusChangeListener {
    lateinit var binding: FragmentTimerBinding
    private var flagArray = JSONArray()
    private val recyclerFlagAdapter: RecyclerFlag by lazy { RecyclerFlag(flagArray) }
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var counterDownTimer: CountDownTimer

    var counter = 0
    var mainCounter = 0
    private var secCounter = 0
    private var minCounter = 0
    var restCounter = 0
    var laps = 1
    var inputLaps = 0
    private var boxCounterEn = arrayOf(true, false) // time , reset

    private var isChecked = true
    private var isPaused = false
    private var isInTimer = false

    private var timerStarted = false
    private var inChronometer = false

    var seconds = 0
    var minutes = 0
    var milliSeconds = 0
    var millisecondTime: Long = 0
    var startTime: Long = 0
    var timeBuff: Long = 0
    var updateTime: Long = 0L
    var handlerChronometer: Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handlerChronometer = Handler(Looper.getMainLooper())
    }

    private val runnable: Runnable = object : Runnable {
        override fun run() {
            millisecondTime = SystemClock.uptimeMillis() - startTime
            updateTime = timeBuff + millisecondTime
            seconds = (updateTime / 1000).toInt()
            minutes = seconds / 60
            seconds %= 60
            milliSeconds = (updateTime % 1000).toString().take(2).toInt()
            activity?.runOnUiThread {
                binding.lblClock.text =
                    String.format("%02d:%02d.%02d", minutes, seconds, milliSeconds)
            }
            handlerChronometer?.postDelayed(this, 0)
            (updateTime / 1000)
        }
    }

    //##### LIFE CYCLE #############################################################################
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTimerBinding.inflate(layoutInflater, container, false)

        segmentOnclick()

        mainTimer()

        timerSport()

        chronometer()

        onViewListener()

        recyclerFlagInit()

        sportMode()

        return binding.root
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (v?.id == binding.txtLapsSport.id) {
            if (hasFocus)
                (v as EditText).hint = ""
            else
                (v as EditText).hint = getString(R.string.zero)
        }
    }

    //##############################################################################################
    private fun onViewListener() {
        binding.minutesNumberPicker.setOnValueChangedListener { _, _, newVal ->
            minCounter = newVal * 60
        }

        binding.secondNumberPicker.setOnValueChangedListener { _, _, newVal ->
            secCounter = newVal
        }

        binding.restNumberPicker.setOnValueChangedListener { _, _, newVal ->
            restCounter = newVal
        }

        binding.btnPlayTimer.setOnClickListener {
            // In Chronometer
            if (inChronometer) {
                if (!timerStarted) {
                    startTime = SystemClock.uptimeMillis()
                    handlerChronometer?.postDelayed(runnable, 0)
                    binding.btnStopTimer.isEnabled = true
                    context?.let { context ->
                        ContextCompat.getDrawable(context, R.drawable.ic_pause)
                            ?.let { icon -> binding.btnPlayTimer.setIcon(icon) }
                    }
                    timerStarted = true
                } else {
                    timeBuff += millisecondTime
                    handlerChronometer?.removeCallbacks(runnable);
                    context?.let { context ->
                        ContextCompat.getDrawable(context, R.drawable.ic_play)
                            ?.let { icon -> binding.btnPlayTimer.setIcon(icon) }
                    }
                    timerStarted = false
                }
            } else {
                // In Timer
                mainCounter = minCounter + secCounter

                inputLaps = binding.txtLapsSport.text.toString().ifEmpty { "1" }.toInt()

                if (mainCounter > 0) {
                    binding.timerCountLayout.visibility = View.VISIBLE

                    Handler(Looper.getMainLooper()).postDelayed({
                        counterDownTimer.cancel()
                        counterDownTimer.start()
                    }, 700)
                    isInTimer = true
                } else
                    (context as MainActivity).mainViewModel
                        .toastMotion(
                            (context as MainActivity), MotionToastStyle.ERROR,
                            getString(R.string.error), getString(R.string.error_input)
                        )

            }
        }

        binding.btnStopTimer.setOnClickListener {
            if (inChronometer) {
                handlerChronometer?.removeCallbacks(runnable)
                binding.btnStopTimer.isEnabled = false
                millisecondTime = 0L
                startTime = 0L
                timeBuff = 0L
                updateTime = 0L
                seconds = 0
                minutes = 0
                milliSeconds = 0
                binding.lblClock.text = String.format("%02d:%02d.%02d", 0, 0, 0)
                context?.let { context ->
                    ContextCompat.getDrawable(context, R.drawable.ic_play)
                        ?.let { icon -> binding.btnPlayTimer.setIcon(icon) }
                }
                timerStarted = false
                while (flagArray.length() > 0)
                    flagArray.remove(0)
                recyclerFlagAdapter.notifyDataSetChanged()
            }
        }

        binding.btnCloseTimer.setOnClickListener {
            counterDownTimer.cancel()
            reloadInputs()
            binding.timerCountLayout.visibility = View.GONE
            binding.timerLayout.visibility = View.VISIBLE
            isInTimer = false
        }

        binding.btnPauseTimer.setOnClickListener {
            if (isPaused) {
                context?.let { it1 ->
                    ContextCompat.getDrawable(it1, R.drawable.ic_pause)
                        ?.let { icon -> binding.btnPauseTimer.setIcon(icon) }
                }
                counterDownTimer.start()
            } else {
                context?.let { it1 ->
                    ContextCompat.getDrawable(it1, R.drawable.ic_play)
                        ?.let { icon -> binding.btnPauseTimer.setIcon(icon) }
                }
                counterDownTimer.cancel()
            }
            isPaused = !isPaused
        }

        binding.txtLapsSport.onFocusChangeListener = this
    }

    private fun sportMode() {
        binding.switchSportMode.isChecked = mainActivity.mainViewModel.roomBase.getSettingByID(2)?.enable == true

        binding.switchSportMode.setOnCheckedChangeListener { _, isChecked ->
            mainActivity.mainViewModel.roomBase.updateSetting(2,isChecked, "")
        }
    }

    private fun segmentOnclick() {
        binding.btnSegmentTimer.addOnCheckedChangeListener { button, isChecked ->
            button.icon = if (isChecked)
                context?.let { ContextCompat.getDrawable(it, R.drawable.ic_check) }
            else
                null

            if (isInTimer)
                binding.timerCountLayout.visibility = View.VISIBLE
            else
                binding.timerLayout.visibility = View.VISIBLE

            binding.chronometerLayout.visibility = View.GONE
            binding.btnFlagTimer.visibility = View.GONE
            binding.btnStopTimer.visibility = View.GONE

            context?.let { it ->
                ContextCompat.getDrawable(it, R.drawable.ic_pause)
                    ?.let { binding.btnStopTimer.setIcon(it) }
            }
            inChronometer = false
        }

        binding.btnSegmentChronometer.addOnCheckedChangeListener { button, isChecked ->
            button.icon = if (isChecked)
                context?.let { ContextCompat.getDrawable(it, R.drawable.ic_check) }
            else
                null

            binding.timerLayout.visibility = View.GONE
            binding.timerCountLayout.visibility = View.GONE
            binding.chronometerLayout.visibility = View.VISIBLE
            binding.btnFlagTimer.visibility = View.VISIBLE
            binding.btnStopTimer.visibility = View.VISIBLE
            context?.let { it ->
                ContextCompat.getDrawable(it, R.drawable.ic_stop)
                    ?.let { binding.btnStopTimer.setIcon(it) }
            }
            inChronometer = true
        }

        binding.btnSegmentTimer.performClick()
    }

    private fun timerSport() {
        binding.switchSportMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
//                binding.txtRestSport.isEnabled = true
                binding.txtLapsSport.isEnabled = true
                binding.boxConsSport.alpha = 1f
            } else {
//                binding.txtRestSport.isEnabled = false
                binding.txtLapsSport.isEnabled = false
                binding.boxConsSport.alpha = 0.5f
            }
        }
        binding.switchSportMode.isChecked = false
    }

    private fun chronometer() {
        binding.btnFlagTimer.setOnClickListener {
            if (timerStarted) {
                flagArray.put(binding.lblClock.text.toString())
                recyclerFlagAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun recyclerFlagInit() {
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        binding.recyclerFlag.layoutManager = linearLayoutManager
        binding.recyclerFlag.adapter = recyclerFlagAdapter
    }

    private fun mainTimer() {
        counterDownTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(p0: Long) {
                if (boxCounterEn[0] || boxCounterEn[1])
                    counter++

                if (boxCounterEn[0]) {
                    if (counter == mainCounter) {
                        boxCounterEn[0] = false
                        playNotify("round")
                        if (isChecked && laps <= inputLaps - 1) {
                            boxCounterEn[0] = false
                            Handler(Looper.getMainLooper()).postDelayed({
                                boxCounterEn[1] = true
                                counter = -1
                            }, 1000)
                        } else
                            counterDownTimer.cancel()
                    }

                    loadMainTimerViews(mainCounter, R.color.mainColor)
                }

                if (boxCounterEn[1]) {
                    if (counter == restCounter) {
                        boxCounterEn[1] = false
                        laps++
                        playNotify("laps")
                        Handler(Looper.getMainLooper()).postDelayed({
                            boxCounterEn[0] = true
                            counter = -1
                        }, 1000)
                    }

                    loadMainTimerViews(restCounter, R.color.purpleColor)
                }

                binding.lblLaps.text = "${getString(R.string.laps)} $laps / $inputLaps"

            }

            override fun onFinish() {
                start()
            }
        }
    }

    private fun reloadInputs() {
        counter = 0
        mainCounter = 0
        restCounter = 0
        laps = 1
        inputLaps = 0
        boxCounterEn = arrayOf(true, false)
    }

    private fun loadMainTimerViews(ctn: Int, color: Int) {
        binding.progressTimer.textColor =
            context?.let { ContextCompat.getColor(it, color) }!!
        binding.progressTimer.maxValue = ctn.toFloat()
        binding.progressTimer.progress = counter.toFloat()
        binding.progressTimer.strokeColor = ContextCompat.getColor(requireContext(), color)
        binding.progressTimer.text =
            String.format("%02d", (((ctn - counter) / 60)).toLong()) +
                    ":${
                        String.format(
                            "%02d",
                            (((ctn - counter) % 60)).toLong()
                        )
                    }"
    }

    private fun playNotify(type: String) {
        Handler(Looper.getMainLooper()).postDelayed({
            when (type) {
                "round" ->
                    mediaPlayer = MediaPlayer.create(context, R.raw.boxing_bell)

                "laps" ->
                    mediaPlayer = MediaPlayer.create(context, R.raw.boxing_bell_round)

            }
            mediaPlayer?.start()
        }, 100)
    }


}