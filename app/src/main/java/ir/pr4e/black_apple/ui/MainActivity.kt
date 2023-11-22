package ir.pr4e.black_apple.ui

import android.content.res.Configuration
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import com.github.nikartm.button.FitButton
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import ir.pr4e.black_apple.R
import ir.pr4e.black_apple.adapter.MainPagerAdapter
import ir.pr4e.black_apple.databinding.ActivityMainBinding
import ir.pr4e.black_apple.databinding.SheetNavigationBinding
import ir.pr4e.black_apple.databinding.SheetTempBinding
import ir.pr4e.black_apple.models.MainViewModel
import ir.pr4e.black_apple.ui.MainActivity.MainActivityHolder.mainActivity
import ir.pr4e.black_apple.ui.main.TemperatureFragment

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding

    val mainViewModel: MainViewModel by viewModels()

    object MainActivityHolder {
        var mainActivity: MainActivity = MainActivity()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mainActivity = this

        mainViewModel.roomBase.createDB()

        binding.navView.setNavigationItemSelectedListener(this)
        drawerLayout()

        viewPagerInit()

        onClick()
    }

    private fun onClick() {
        binding.btnMenu.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START))
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            else
                binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        binding.navView.getHeaderView(0).findViewById<FitButton>(R.id.btn_close_nav)
            .setOnClickListener {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            }
    }

    private fun drawerLayout() {
        runOnUiThread {
            val actionBarDrawerToggle: ActionBarDrawerToggle = object : ActionBarDrawerToggle(
                this,
                binding.drawerLayout,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
            ) {
                override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                    super.onDrawerSlide(drawerView, slideOffset)
                    val slideX = drawerView.width * slideOffset
                    binding.content.translationX = slideX
                    val scaleFactor = 5F
                    binding.content.scaleX = 1 - slideOffset / scaleFactor
                    binding.content.scaleY = 1 - slideOffset / scaleFactor
                    if (slideOffset == 0f) binding.btnMenu.animate().rotation(0F).start()
                    if (slideOffset == 1f) binding.btnMenu.animate().rotation(90F).start()
                }
            }
            binding.drawerLayout.setScrimColor(Color.TRANSPARENT)
            binding.drawerLayout.drawerElevation = 0f
            binding.drawerLayout.addDrawerListener(actionBarDrawerToggle)
        }

    }

    private fun viewPagerInit() {
        try {
            val array = mainViewModel.tabArray()
            binding.mainPager.adapter = MainPagerAdapter(this, array)
            binding.mainPager.offscreenPageLimit = 3

            TabLayoutMediator(
                binding.tabLayout,
                binding.mainPager
            ) { tab: TabLayout.Tab, position: Int ->
                when (position) {
                    0 -> {
                        tab.text = getString(array.getJSONObject(0).getInt("name"))
                        tab.icon =
                            ContextCompat.getDrawable(this, array.getJSONObject(0).getInt("img"))
                    }

                    1 -> {
                        tab.text = getString(array.getJSONObject(1).getInt("name"))
                        tab.icon =
                            ContextCompat.getDrawable(this, array.getJSONObject(1).getInt("img"))
                    }

                    2 -> {
                        tab.text = getString(array.getJSONObject(2).getInt("name"))
                        tab.icon =
                            ContextCompat.getDrawable(this, array.getJSONObject(2).getInt("img"))
                    }
                }
            }.attach()

            binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    tab?.icon?.setColorFilter(
                        ContextCompat.getColor(
                            this@MainActivity,
                            R.color.mainLightColor
                        ), PorterDuff.Mode.SRC_IN
                    )
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    tab?.icon?.setColorFilter(
                        ContextCompat.getColor(
                            this@MainActivity,
                            R.color.tabUnselectColor
                        ), PorterDuff.Mode.SRC_IN
                    )
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {

                }
            })
        } catch (e: Exception) {
            Log.e("ViewPager Error", e.toString())
//            ToastMotion(MotionToastStyle.WARNING, "Important error # ViewPagerInit", e.toString())
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_on_off -> {
                SheetNavigation().show(supportFragmentManager, "onOff")
            }

            R.id.nav_wifi -> {
                SheetNavigation().show(supportFragmentManager, "wifi")
            }

            R.id.nav_brightness -> {
                SheetNavigation().show(supportFragmentManager, "brightness")
            }

            R.id.nav_display -> {
                SheetNavigation().show(supportFragmentManager, "display")
            }

            R.id.nav_language -> {
//                SheetNavigation().show(supportFragmentManager, "onOff")
            }

            R.id.nav_warranty -> {
                SheetNavigation().show(supportFragmentManager, "warranty")
            }

            R.id.nav_application -> {
                SheetNavigation().show(supportFragmentManager, "help")
            }

            R.id.nav_reset -> {
                SheetNavigation().show(supportFragmentManager, "reset")
            }

            R.id.nav_exit -> {
                SheetNavigation().show(supportFragmentManager, "exit")
            }
        }
        return false
    }


    class SheetNavigation : BottomSheetDialogFragment() {
        private lateinit var binding: SheetNavigationBinding

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            binding = SheetNavigationBinding.inflate(layoutInflater, container, false)

            binding.boxBrightness.visibility = View.GONE
            binding.boxWarranty.visibility = View.GONE
            binding.boxDisplaySetting.visibility = View.GONE

            when (tag) {
                "onOff" -> {
                    binding.lblTitleNavigation.text = getString(R.string.nav_on_off)
                    binding.lineEndNavigation.visibility = View.GONE

                }

                "wifi" -> {
                    binding.lblTitleNavigation.text = getString(R.string.wifi_setting)

                }

                "brightness" -> {
                    binding.lblTitleNavigation.text = getString(R.string.brightness)
                    binding.boxBrightness.visibility = View.VISIBLE

                }

                "display" -> {
                    binding.lblTitleNavigation.text = getString(R.string.display_setting)
                    binding.boxDisplaySetting.visibility = View.VISIBLE

                }

                "warranty" -> {
                    binding.lblTitleNavigation.text = getString(R.string.warranty)
                    binding.boxWarranty.visibility = View.VISIBLE

                }

                "help" -> {
                    binding.lblTitleNavigation.text = getString(R.string.application)

                }

                "reset" -> {
                    binding.lblTitleNavigation.text = getString(R.string.reset_factory)
                    binding.lineEndNavigation.visibility = View.GONE

                }

                "exit" -> {
                    binding.lblTitleNavigation.text = getString(R.string.exit_ask)
                    binding.lineEndNavigation.visibility = View.GONE
                    binding.btnOkNavigation.setOnClickListener {
                        (context as MainActivity).finish()
                    }
                }
            }

            binding.btnCancelNavigation.setOnClickListener {
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