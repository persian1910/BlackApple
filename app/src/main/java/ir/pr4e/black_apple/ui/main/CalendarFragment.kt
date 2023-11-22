package ir.pr4e.black_apple.ui.main

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_CLOCK
import com.google.android.material.timepicker.TimeFormat
import ir.pr4e.black_apple.R
import ir.pr4e.black_apple.adapter.RecyclerReminder
import ir.pr4e.black_apple.databinding.FragmentCalendarBinding
import ir.pr4e.black_apple.databinding.SheetReminderBinding
import ir.pr4e.black_apple.databinding.SheetTimeBinding
import ir.pr4e.black_apple.ui.MainActivity
import ir.pr4e.black_apple.ui.MainActivity.MainActivityHolder.mainActivity
import ir.pr4e.black_apple.utils.RequestApi
import org.json.JSONArray
import org.json.JSONObject
import www.sanju.motiontoast.MotionToastStyle
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class CalendarFragment : Fragment() {
    private lateinit var binding: FragmentCalendarBinding
    private val calendar: Calendar = Calendar.getInstance()
    val sheetReminder: SheetReminder by lazy { SheetReminder(this) }
    private val reminderAdapter: RecyclerReminder by lazy {
        RecyclerReminder(
            activity as MainActivity,
            this
        )
    }
    private var timeObject = JSONObject()
    private var reminderObject = JSONObject()
    private var reminderArray = JSONArray()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalendarBinding.inflate(layoutInflater, container, false)

        segmentOnclick()
        onClick()

        switchAuto()
        setAutoTime()
        calendarOnclick()
        loadTimeFormat()

        reminderLayoutInit()
        return binding.root
    }

    fun loadTimeFormat() {
        if (mainActivity.mainViewModel.roomBase.getSettingByID(1)?.enable == true)
            binding.lblTimeFormat.text = getString(R.string.time_12_hour)
        else
            binding.lblTimeFormat.text = getString(R.string.time_24_hour)
    }

    private fun segmentOnclick() {
        binding.btnSegmentTime.addOnCheckedChangeListener { button, isChecked ->
            button.icon = if (isChecked)
                context?.let { ContextCompat.getDrawable(it, R.drawable.ic_check) }
            else
                null

            binding.timeLayout.visibility = View.VISIBLE
            binding.reminderLayout.visibility = View.GONE
        }

        binding.btnSegmentReminder.addOnCheckedChangeListener { button, isChecked ->
            button.icon = if (isChecked)
                context?.let { ContextCompat.getDrawable(it, R.drawable.ic_check) }
            else
                null

            binding.timeLayout.visibility = View.GONE
            binding.reminderLayout.visibility = View.VISIBLE
        }

        binding.btnSegmentTime.performClick()
    }

    private fun onClick() {
        binding.btnSaveCalendar.setOnClickListener {
            objectCreator()
        }
    }

    private fun objectCreator() {
        timeObject.put("Auto", binding.switchSetAuto.isChecked)
        timeObject.put("Date",
            SimpleDateFormat(
                "MMM dd yyyy",
                Locale.getDefault()
            ).parse(binding.lblDate.text.toString())
                ?.let {
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        .format(it)
                })
        timeObject.put("Time", binding.lblTime.text)

        if (mainActivity.mainViewModel.roomBase.getSettingByID(1)?.enable == true)
            timeObject.put("Format", "12")
        else
            timeObject.put("Format", "24")




        for (i in 0 until (mainActivity.mainViewModel.roomBase.listReminder(false)?.size ?: 0)) {
            val reminder = mainActivity.mainViewModel.roomBase.listReminder(false)?.get(i)
            reminderObject = JSONObject()
            if (reminder != null) {
                reminderObject.put("Time", reminder.time)
                reminderObject.put("Duration", reminder.duration)
                reminderObject.put("Enable", reminder.isEnable)
                reminderArray.put(reminderObject)
            }
        }
        timeObject.put("Reminder", reminderArray)


        // Send to Arduino
        context?.let {
            RequestApi(it).sendRequest(
                "Time", timeObject.toString()
            ) {
                Toast.makeText(context, "Success...", Toast.LENGTH_SHORT).show()
                Log.e("Response Request", it.toString())
            }
        }
        Log.e("Object Json", timeObject.toString())
    }

    //###### Time ##################################################################################
    private fun setAutoTime() {
        binding.lblTime.text = SimpleDateFormat("hh:mm", Locale.getDefault()).format(calendar.time)
        binding.lblDate.text =
            SimpleDateFormat("MMM dd yyyy", Locale.getDefault()).format(calendar.time)
    }

    private fun switchAuto() {
        binding.switchSetAuto.setOnCheckedChangeListener { p0, p1 ->
            if (p1) {
                setAutoTime()
                binding.btnDate.isEnabled = false
                binding.btnTime.isEnabled = false
                binding.btnTimeFormat.isEnabled = false

                binding.btnDate.alpha = 0.5f
                binding.btnTime.alpha = 0.5f
                binding.btnTimeFormat.alpha = 0.5f
            } else {
                binding.btnDate.isEnabled = true
                binding.btnTime.isEnabled = true
                binding.btnTimeFormat.isEnabled = true

                binding.btnDate.alpha = 1f
                binding.btnTime.alpha = 1f
                binding.btnTimeFormat.alpha = 1f
            }
        }
        binding.switchSetAuto.isChecked = true
    }

    private fun calendarOnclick() {
        binding.btnDate.setOnClickListener {
            val materialDatePicker: MaterialDatePicker<Long> =
                MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select Date")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build()
            materialDatePicker.addOnPositiveButtonClickListener {
                binding.lblDate.text =
                    SimpleDateFormat("MMM dd yyyy", Locale.getDefault()).format(it)
            }
            activity?.supportFragmentManager?.let { it1 ->
                materialDatePicker.show(
                    it1,
                    "date_picker"
                )
            }
        }

        binding.btnTime.setOnClickListener {
            val materialTimePicker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(calendar[Calendar.HOUR])
                .setMinute(calendar[Calendar.MINUTE])
                .setTitleText("Select Appointment time")
                .setInputMode(INPUT_MODE_CLOCK)
                .build()
            materialTimePicker.addOnPositiveButtonClickListener {
                binding.lblTime.text =
                    String.format("%02d:%02d", materialTimePicker.hour, materialTimePicker.minute)
            }
            activity?.supportFragmentManager?.let { it1 ->
                materialTimePicker.show(
                    it1,
                    "time_picker"
                )
            }
        }

        binding.btnTimeFormat.setOnClickListener {
            activity?.supportFragmentManager?.let { it1 ->
                SheetTimeFormat(this).show(
                    it1,
                    "time_format"
                )
            }
        }
    }

    //##############################################################################################
    //###### Reminder ##############################################################################
    private fun reminderLayoutInit() {
        binding.btnReminder.setOnClickListener {
            val materialTimePicker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(calendar[Calendar.HOUR_OF_DAY])
                .setMinute(calendar[Calendar.MINUTE])
                .setTitleText("Select Appointment time")
                .setInputMode(INPUT_MODE_CLOCK)
                .build()
            materialTimePicker.addOnPositiveButtonClickListener {
                (activity as MainActivity).mainViewModel.roomBase.insertReminder(
                    String.format(
                        "%02d:%02d",
                        materialTimePicker.hour,
                        materialTimePicker.minute
                    ), 2, true
                )
                reminderAdapter.notifyDataSetChanged()
            }
            activity?.supportFragmentManager?.let { it1 ->
                materialTimePicker.show(
                    it1,
                    "time_picker_reminder"
                )
            }
        }

        binding.recyclerReminder.layoutManager = LinearLayoutManager(context)
        binding.recyclerReminder.adapter = reminderAdapter
    }
    //##############################################################################################

    class SheetTimeFormat(ctx: CalendarFragment) : BottomSheetDialogFragment() {
        private lateinit var binding: SheetTimeBinding
        private val ctFragment = ctx

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            binding = SheetTimeBinding.inflate(layoutInflater, container, false)

            val setting12Hours = mainActivity.mainViewModel.roomBase.getSettingByID(1)
            if (setting12Hours?.enable == true)
                binding.radio12Hour.isChecked = true
            else
                binding.radio24Hour.isChecked = true

            binding.btnOkTimeFormat.setOnClickListener {
                mainActivity.mainViewModel.roomBase.updateSetting(
                    1,
                    binding.radio12Hour.isChecked,
                    ""
                )
                ctFragment.loadTimeFormat()
                dismiss()
            }
            binding.btnCancelTimeFormat.setOnClickListener {
                dismiss()
            }
            return binding.root
        }

        override fun getTheme(): Int {
            return R.style.BottomSheetDialog
        }

        @Deprecated("Deprecated in Java")
        override fun onActivityCreated(savedInstanceState: Bundle?) {
            super.onActivityCreated(savedInstanceState)

            (view?.parent as View).setBackgroundColor(Color.TRANSPARENT)

            val resources = resources
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                val parent = view?.parent as View
                val layoutParams = parent.layoutParams as CoordinatorLayout.LayoutParams
                layoutParams.setMargins(
                    resources.getDimensionPixelSize(R.dimen.margin_bottom_sheet),
                    resources.getDimensionPixelSize(R.dimen.margin_bottom_sheet),
                    resources.getDimensionPixelSize(R.dimen.margin_bottom_sheet),
                    resources.getDimensionPixelSize(R.dimen.margin_bottom_sheet)
                )
                parent.layoutParams = layoutParams
            }
        }
    }

    class SheetReminder(mActivity: CalendarFragment) : BottomSheetDialogFragment() {
        private lateinit var binding: SheetReminderBinding
        private val activity = mActivity

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            binding = SheetReminderBinding.inflate(layoutInflater, container, false)
            try {
                when (tag) {
                    "delete" -> {
                        binding.boxDuration.visibility = View.GONE
                        binding.lblTitleSheetReminder.text = getString(R.string.delete_ask)

                        binding.btnOkReminder.setOnClickListener {
                            (context as MainActivity).mainViewModel.roomBase
                                .deleteReminderByID((context as MainActivity).mainViewModel.reminderID)
                            activity.reminderAdapter.notifyDataSetChanged()
                            dismiss()
                        }
                        binding.btnCancelReminder.setOnClickListener {
                            dismiss()
                        }
                    }

                    "duration" -> {
                        binding.boxDuration.visibility = View.VISIBLE
                        binding.lblTitleSheetReminder.text = getString(R.string.set_duration)
                        val reminder = (context as MainActivity).mainViewModel.roomBase
                            .getReminderByID((context as MainActivity).mainViewModel.reminderID)
                        binding.txtDurationReminderRecycler.hint = "+${reminder?.duration}"
                        binding.btnOkReminder.setOnClickListener {
                            val duration = binding.txtDurationReminderRecycler.text.toString()
                            if (duration.isNotEmpty()) {
                                if (reminder != null && duration.toInt() > 0) {
                                    (context as MainActivity).mainViewModel.roomBase.updateReminder(
                                        reminder.id,
                                        reminder.time,
                                        duration.toInt(),
                                        reminder.isEnable
                                    )
                                    activity.reminderAdapter.notifyDataSetChanged()
                                } else
                                    (context as MainActivity).mainViewModel
                                        .toastMotion(
                                            (context as MainActivity),
                                            MotionToastStyle.ERROR,
                                            getString(R.string.error),
                                            getString(R.string.check_data)
                                        )
                            }
                            dismiss()
                        }
                        binding.btnCancelReminder.setOnClickListener {
                            dismiss()
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("SheetReminder Error", e.toString())
            }

            return binding.root
        }

        override fun getTheme(): Int {
            return R.style.BottomSheetDialog
        }

        @Deprecated("Deprecated in Java")
        override fun onActivityCreated(savedInstanceState: Bundle?) {
            super.onActivityCreated(savedInstanceState)

            (view?.parent as View).setBackgroundColor(Color.TRANSPARENT)

            val resources = resources
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                val parent = view?.parent as View
                val layoutParams = parent.layoutParams as CoordinatorLayout.LayoutParams
                layoutParams.setMargins(
                    resources.getDimensionPixelSize(R.dimen.margin_bottom_sheet),
                    resources.getDimensionPixelSize(R.dimen.margin_bottom_sheet),
                    resources.getDimensionPixelSize(R.dimen.margin_bottom_sheet),
                    resources.getDimensionPixelSize(R.dimen.margin_bottom_sheet)
                )
                parent.layoutParams = layoutParams
            }
        }
    }
}