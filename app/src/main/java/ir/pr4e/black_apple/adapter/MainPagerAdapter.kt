package ir.pr4e.black_apple.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import ir.pr4e.black_apple.ui.main.TemperatureFragment
import ir.pr4e.black_apple.ui.main.CalendarFragment
import ir.pr4e.black_apple.ui.main.TimerFragment
import org.json.JSONArray

internal class MainPagerAdapter(fragmentActivity: FragmentActivity, arr: JSONArray): FragmentStateAdapter(fragmentActivity) {
    private val array = arr
    override fun getItemCount(): Int {
        return array.length()
    }

    override fun createFragment(position: Int): Fragment {
        return when (position){
            0 -> CalendarFragment()
            1 -> TimerFragment()
            2 -> TemperatureFragment()
            else -> CalendarFragment()
        }
    }
}