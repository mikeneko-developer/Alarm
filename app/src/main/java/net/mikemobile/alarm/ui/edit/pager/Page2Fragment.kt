package net.mikemobile.alarm.ui.edit.pager

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.*
import android.os.VibrationEffect.DEFAULT_AMPLITUDE
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import net.mikemobile.databindinglib.base.BaseFragment
import net.mikemobile.databindinglib.base.BaseNavigator

import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import net.mikemobile.alarm.R
import net.mikemobile.alarm.data.ListItem
import net.mikemobile.alarm.database.entity.Item
import net.mikemobile.alarm.databinding.FragmentEditBinding
import net.mikemobile.alarm.log.LogUtil
import net.mikemobile.alarm.services.TimeReceiver
import net.mikemobile.alarm.ui.dialog.*
import net.mikemobile.alarm.ui.edit.sunuzu.list.SunuzuListFragment
import net.mikemobile.alarm.util.Constant.Companion.VIB_LIST
import net.mikemobile.alarm.util.Constant.Companion.VIB_LIST_TIME
import net.mikemobile.android.music.MyVolume
import net.mikemobile.media.MediaInfo
import net.mikemobile.media.MediaUtilityManager
import net.mikemobile.media.system.MediaReadUtil
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.ArrayList

class Page2Fragment: Fragment() {

    companion object {
        const val TAG = "Page2Fragment"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.pager_fragment_page2, container, false)
    }
    //
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }



}