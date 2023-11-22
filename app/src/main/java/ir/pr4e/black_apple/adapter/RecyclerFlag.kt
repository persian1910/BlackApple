package ir.pr4e.black_apple.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.pr4e.black_apple.databinding.RecyclerChronometerFlagBinding
import org.json.JSONArray

class RecyclerFlag(arr: JSONArray) : RecyclerView.Adapter<RecyclerFlag.ViewHolder>() {
    private val array = arr

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RecyclerChronometerFlagBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val obj = array.get(position)
        holder.binding.lblTimeRecycler.text = obj.toString()
        holder.binding.lblPositionRecycler.text = "${position + 1}"
    }

    override fun getItemCount(): Int {
        return array.length()
    }

    class ViewHolder(bind: RecyclerChronometerFlagBinding) : RecyclerView.ViewHolder(bind.root) {
        val binding = bind
    }
}