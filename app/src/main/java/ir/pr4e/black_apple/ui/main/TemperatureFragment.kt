package ir.pr4e.black_apple.ui.main

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ir.pr4e.black_apple.R
import ir.pr4e.black_apple.databinding.FragmentTemperatureBinding
import ir.pr4e.black_apple.databinding.SheetTempBinding
import ir.pr4e.black_apple.databinding.SheetTimeBinding
import ir.pr4e.black_apple.ui.MainActivity.MainActivityHolder.mainActivity
import org.json.JSONObject

class TemperatureFragment : Fragment() {
    lateinit var binding: FragmentTemperatureBinding
    private var degreeType = "Celsius"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTemperatureBinding.inflate(layoutInflater, container, false)

        segmentOnclick()

        onClick()


        return binding.root
    }

    private fun segmentOnclick() {
        binding.btnSegmentDegree.addOnCheckedChangeListener { button, isChecked ->
            button.icon = if (isChecked)
                context?.let { ContextCompat.getDrawable(it, R.drawable.ic_check) }
            else
                null

            binding.progressTemperature.text = "25${getString(R.string.degree_symbol)}C"
            degreeType = "Celsius"
        }

        binding.btnSegmentFahrenheit.addOnCheckedChangeListener { button, isChecked ->
            button.icon = if (isChecked)
                context?.let { ContextCompat.getDrawable(it, R.drawable.ic_check) }
            else
                context?.let { ContextCompat.getDrawable(it, R.drawable.ic_null) }

            binding.progressTemperature.text = "77${getString(R.string.degree_symbol)}F"
            degreeType = "Fahrenheit"
        }

        binding.btnSegmentDegree.performClick()
    }

    private fun onClick() {
        binding.btnCalibration.setOnClickListener {
            activity?.supportFragmentManager?.let { it1 ->
                SheetTempDialog(degreeType).show(
                    it1,
                    "calibration"
                )
            }
            Log.e("Click", "Calibration")
        }

        binding.btnSetPoint.setOnClickListener {
            activity?.supportFragmentManager?.let { it1 ->
                SheetTempDialog(degreeType).show(
                    it1,
                    "humidity"
                )
            }
            Log.e("Click", "Set Point")
        }

        binding.btnSaveTemp.setOnClickListener {
            Log.e("Click", "Save")
        }
    }


    //###### Time ##################################################################################
    //##############################################################################################
    //###### Reminder ##############################################################################

    class SheetTempDialog(degree: String) : BottomSheetDialogFragment() {
        private lateinit var binding: SheetTempBinding
        private val degreeType = degree

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            binding = SheetTempBinding.inflate(layoutInflater, container, false)

            binding.lblTempTitle.text = (if (degreeType == "Celsius")
                getString(R.string.tempC)
            else
                getString(R.string.tempF))

            when (tag) {
                "calibration" -> {
                    binding.lblLabel.text = getString(R.string.calibration)
                    binding.boxPointTop.visibility = View.GONE
                    binding.boxPointDown.visibility = View.GONE
                    val cal = mainActivity.mainViewModel.roomBase.getSettingByID(3)
                    binding.txtToTop.setText(cal?.temp)
                    binding.checkBoxTemp.isChecked = cal?.enable == true

                    val cal2 = mainActivity.mainViewModel.roomBase.getSettingByID(4)
                    binding.txtToDown.setText(cal2?.temp)
                    binding.checkBoxHumidity.isChecked = cal2?.enable == true

                    binding.btnOkCalibration.setOnClickListener {
                        mainActivity.mainViewModel.roomBase.updateSetting(
                            3,
                            binding.checkBoxTemp.isChecked,
                            binding.txtToTop.text.toString()
                        )
                        mainActivity.mainViewModel.roomBase.updateSetting(
                            4,
                            binding.checkBoxHumidity.isChecked,
                            binding.txtToDown.text.toString()
                        )
                        dismiss()
                    }
                }

                "humidity" -> {
                    binding.lblLabel.text = getString(R.string.setPoint)
                    binding.boxPointTop.visibility = View.VISIBLE
                    binding.boxPointDown.visibility = View.VISIBLE
                    val hu = mainActivity.mainViewModel.roomBase.getSettingByID(5)
                    val obj1 = JSONObject(hu?.temp.toString())
                    binding.txtFromTop.setText(obj1.getString("from").toString())
                    binding.txtToTop.setText(obj1.getString("to").toString())
                    binding.checkBoxTemp.isChecked = hu?.enable == true

                    val hu2 = mainActivity.mainViewModel.roomBase.getSettingByID(6)
                    val obj2 = JSONObject(hu2?.temp.toString())
                    binding.txtFromDown.setText(obj2.getString("from").toString())
                    binding.txtToDown.setText(obj2.getString("to").toString())
                    binding.checkBoxHumidity.isChecked = hu2?.enable == true


                    binding.btnOkCalibration.setOnClickListener {
                        val obj = JSONObject()
                        obj.put("from", binding.txtFromTop.text)
                        obj.put("to", binding.txtToTop.text)

                        val obj2 = JSONObject()
                        obj2.put("from", binding.txtFromDown.text)
                        obj2.put("to", binding.txtToDown.text)

                        mainActivity.mainViewModel.roomBase.updateSetting(
                            5,
                            binding.checkBoxTemp.isChecked,
                            obj.toString()
                        )
                        mainActivity.mainViewModel.roomBase.updateSetting(
                            6,
                            binding.checkBoxHumidity.isChecked,
                            obj2.toString()
                        )
                        dismiss()
                    }
                }
            }

            binding.btnCancelCalibration.setOnClickListener {
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
}