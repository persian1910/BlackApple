package ir.pr4e.black_apple.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.pr4e.black_apple.ui.MainActivity
import ir.pr4e.black_apple.R
import ir.pr4e.black_apple.databinding.RecyclerReminderBinding
import ir.pr4e.black_apple.ui.main.CalendarFragment
import www.sanju.motiontoast.MotionToastStyle

class RecyclerReminder(mActivity: MainActivity, mFragment: CalendarFragment) :
    RecyclerView.Adapter<RecyclerReminder.ViewHolder>() {
    val activity = mActivity
    val calFragment = mFragment

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RecyclerReminderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reminder = activity.mainViewModel.roomBase.listReminder(false)?.get(position)
        if (reminder != null) {
            holder.binding.lblTimeReminderRecycler.text = reminder.time
            holder.binding.lblTimeReminderRecycler.tag = reminder.id
            holder.binding.switchReminderRecycler.isChecked = reminder.isEnable == true
            holder.binding.lblDurationReminderRecycler.text =
                "${activity.getString(R.string.duration_remin)} (${reminder.duration} sec)"

            holder.binding.switchReminderRecycler.setOnCheckedChangeListener { _, isChecked ->
                activity.mainViewModel.roomBase.updateReminder(
                    reminder.id,
                    reminder.time,
                    reminder.duration,
                    isChecked
                )
                activity.mainViewModel.toastMotion(
                    activity,
                    MotionToastStyle.SUCCESS,
                    "UPDATE",
                    "Reminder updated successfully..."
                )
            }

//            holder.binding.btnReminderRecycler.setOnLongClickListener {
//                activity.mainViewModel.roomBase.deleteReminderByID(reminder.id)
//                activity.mainViewModel.toastMotion(
//                    activity,
//                    MotionToastStyle.SUCCESS,
//                    "DELETE",
//                    "Reminder delete successfully..."
//                )
//                notifyDataSetChanged()
//                false
//            }
        }
    }

    override fun getItemCount(): Int {
        return activity.mainViewModel.roomBase.listReminder(false)?.size ?: 0
    }

    inner class ViewHolder(bind: RecyclerReminderBinding) : RecyclerView.ViewHolder(bind.root) {
        val binding = bind

        init {
            binding.btnReminderRecycler.setOnLongClickListener {
                activity.mainViewModel.reminderID =
                    binding.lblTimeReminderRecycler.tag.toString().toInt()
                if (!calFragment.sheetReminder.isAdded)
                    calFragment.sheetReminder.show(activity.supportFragmentManager, "delete")
                false
            }

            binding.btnReminderRecycler.setOnClickListener {
                activity.mainViewModel.reminderID =
                    binding.lblTimeReminderRecycler.tag.toString().toInt()
                if (!calFragment.sheetReminder.isAdded)
                    calFragment.sheetReminder.show(activity.supportFragmentManager, "duration")
            }
        }
    }
}