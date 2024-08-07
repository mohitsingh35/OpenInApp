package com.mohit.openinapp.UI.MainScreen

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.mohit.openinapp.HelperClasses.ExtensionsUtil.showButtonDialog
import com.mohit.openinapp.HelperClasses.ExtensionsUtil.showProgressDialog
import com.mohit.openinapp.HelperClasses.ExtensionsUtil.toast
import com.mohit.openinapp.R
import com.mohit.openinapp.HelperClasses.Status
import com.mohit.openinapp.UI.UIComponents.ViewModels.MainViewModel
import com.mohit.openinapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val viewModel: MainViewModel by viewModels()
    private lateinit var navController: NavController
    lateinit var dialog:Dialog
    lateinit var buttonDialog:Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        if (viewModel.data.value==null) {
            makeApiCall()
        }
        setupBottomNav()
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NavHostFragment
        navController = navHostFragment.navController
    }

    private fun makeApiCall(){
        dialog=showProgressDialog(this@MainActivity,"Fetching Data...")
        if (::buttonDialog.isInitialized){
            buttonDialog.dismiss()
        }
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.getLinksData()
            withContext(Dispatchers.Main){
                viewModel.linksData.observe(this@MainActivity, Observer { data ->
                    when(data.status){
                        Status.SUCCESS -> {
                            dialog.dismiss()
                            Log.d("MainActivity", "onCreate: ${data.data}")
                            viewModel.setData(data.data!!)
                        }
                        Status.ERROR -> {
                            dialog.dismiss()
                            if (::buttonDialog.isInitialized){
                                buttonDialog.show()
                            }
                            else{
                                buttonDialog=showButtonDialog(this@MainActivity,"Something went wrong. Please try again.", onButtonClick = {
                                    makeApiCall()
                                })
                            }
                        }
                        Status.LOADING -> {
                        }
                    }
                })
            }
        }
    }

    private fun setupBottomNav() {
        binding.links.setOnClickListener {
            handleClick(it)
            navController.navigate(R.id.fragment_dashboard)
        }
        binding.courses.setOnClickListener {
            handleClick(it)
            navController.navigate(R.id.fragment_courses)
        }
        binding.campaigns.setOnClickListener {
            handleClick(it)
            navController.navigate(R.id.fragment_campaigns)
        }
        binding.profile.setOnClickListener {
            handleClick(it)
            navController.navigate(R.id.fragment_profile)
        }
        binding.imageView.setOnClickListener {
            handleClick(it)
            toast("Create New Link")
        }
    }

    private fun handleClick(view:View){
        val buttons = listOf(binding.links, binding.courses, binding.campaigns, binding.profile,binding.imageView)
        val imageViews = listOf(binding.linksIc, binding.coursesIc, binding.campaignsIc, binding.profileIc)
        val textViews = listOf(binding.linksTxt, binding.coursesTxt, binding.campaignsTxt, binding.profileTxt)
        val newSelectedIndex = buttons.indexOf(view)
        if (newSelectedIndex != viewModel.getSelectedIndex()) {
            viewModel.setSelectedIndex(newSelectedIndex)
            setSelected(newSelectedIndex, imageViews, textViews)
        }
    }

    private fun setSelected(index: Int, imageViews: List<ImageView>, textViews: List<TextView>) {
        val selectedColor = ContextCompat.getColor(this, R.color.black)
        val unselectedColor = ContextCompat.getColor(this, R.color.text_secondary)
        imageViews.forEachIndexed { i, imageView ->
            imageView.setColorFilter(if (i == index) selectedColor else unselectedColor)
        }
        textViews.forEachIndexed { i, textView ->
            textView.setTextColor(if (i == index) selectedColor else unselectedColor)
        }
    }
}
