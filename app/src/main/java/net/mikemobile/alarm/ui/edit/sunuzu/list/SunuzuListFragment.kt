package net.mikemobile.alarm.ui.edit.sunuzu.list

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import net.mikemobile.databindinglib.base.BaseFragment
import net.mikemobile.databindinglib.base.BaseNavigator

import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import net.mikemobile.alarm.R
import net.mikemobile.alarm.databinding.FragmentSunuzuListBinding
import net.mikemobile.alarm.ui.edit.sunuzu.edit.SunuzuEditFragment
import org.koin.android.viewmodel.ext.android.viewModel

interface SunuzuListFragmentNavigator: BaseNavigator {
    fun onError(error:String)
    fun moveEditer()
    fun changeSwitch()
    fun onAddItem()
}

class SunuzuListFragment: BaseFragment(),SunuzuListFragmentNavigator {

    private val viewModel: SunuzuListViewModel by viewModel()

    companion object {
        const val TAG = "SunuzuListFragment"
        fun newInstance() = SunuzuListFragment()
    }

    // ---------------------------------------------------------------------------------------------
    //データバインディングを有効にする
    override fun isDataBinding(): Boolean{
        return true
    }

    //
    override fun onCreateViewBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{

        val binding = DataBindingUtil.inflate<FragmentSunuzuListBinding>(inflater, R.layout.fragment_sunuzu_list, container,false)
        val view = binding.root
        viewModel.navigator = this
        binding.viewmodel = viewModel

        binding.lifecycleOwner = this

        /////////////////////////////////////

        viewModel.setRecyclerView(binding.fragmentSunuzuListRecyclerView, context)
        viewModel.initialize()

        var tollbarTitle = binding.includeToolbar.findViewById<TextView>(R.id.toolbar_textview)
        tollbarTitle.setText("スヌーズリスト")


        return view

        return null
    }

    //
    override fun onActivityCreate(savedInstanceState: Bundle?) {

    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onBackStackResume() {
        super.onBackStackResume()
        activity?.let {
            it.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT)
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onResume() {
        super.onResume()
        //Log.w("TEST_LOG34","AlarmListFragment >> onResume()")
        viewModel.resume(this)
        activity?.let {
            it.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT)
        }
    }

    override fun onPause() {
        super.onPause()
        //Log.w("TEST_LOG34","AlarmListFragment >> onPause()")

    }

    override fun onDestroy() {
        super.onDestroy()
        //Log.w("TEST_LOG34","AlarmListFragment >> onDestroy()")
        viewModel.destroy()
    }

    //
    override fun onBack() {

    }

    // ---------------------------------------------------------------------------------------------
    // BaseNavigatorのメソッド
    override fun onCloseFragment() {

    }

    override fun moveEditer() {
    }

    override fun changeSwitch(){

    }

    override fun onError(error:String){
        Toast.makeText(context,"" + error, Toast.LENGTH_SHORT).show()
    }
    override fun onAddItem(){
        activityNavigator?.replaceFragmentToBackStackInSecondContentFrame(SunuzuEditFragment.TAG)
    }
}