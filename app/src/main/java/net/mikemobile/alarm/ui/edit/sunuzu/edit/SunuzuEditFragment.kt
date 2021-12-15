package net.mikemobile.alarm.ui.edit.sunuzu.edit

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import net.mikemobile.databindinglib.base.BaseFragment
import net.mikemobile.databindinglib.base.BaseNavigator

import androidx.databinding.DataBindingUtil
import net.mikemobile.alarm.R
import net.mikemobile.alarm.database.entity.Item
import net.mikemobile.alarm.database.entity.SunuzuItem
import net.mikemobile.alarm.databinding.FragmentSunuzuEditBinding
import net.mikemobile.alarm.ui.dialog.DialogList
import net.mikemobile.alarm.ui.dialog.DialogMusicList
import net.mikemobile.alarm.ui.edit.EditFragment
import net.mikemobile.media.MediaInfo
import net.mikemobile.media.MediaUtilityManager
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.ArrayList

interface SunuzuEditFragmentNavigator: BaseNavigator {
    fun onError(error:String)
    fun moveEditer()
    fun changeSwitch()

    fun onGetMusicList(item: SunuzuItem)
}

class SunuzuEditFragment: BaseFragment(),
    SunuzuEditFragmentNavigator {

    private val viewModel: SunuzuEditViewModel by viewModel()

    companion object {
        const val TAG = "SunuzuEditFragment"
        fun newInstance() = SunuzuEditFragment()
    }

    // ---------------------------------------------------------------------------------------------
    //データバインディングを有効にする
    override fun isDataBinding(): Boolean{
        return true
    }

    //
    override fun onCreateViewBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{

        val binding = DataBindingUtil.inflate<FragmentSunuzuEditBinding>(inflater, R.layout.fragment_sunuzu_edit, container,false)
        val view = binding.root
        viewModel.navigator = this
        binding.viewmodel = viewModel

        binding.lifecycleOwner = this

        /////////////////////////////////////


        viewModel.initialize()
        viewModel.resume(this)

        var tollbarTitle = binding.includeToolbar.findViewById<TextView>(R.id.toolbar_textview)
        tollbarTitle.setText("スヌーズ編集")


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
        activity?.let {
            it.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT)
        }
    }

    override fun onPause() {
        super.onPause()

    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.destroy()
    }

    //
    override fun onBack() {

    }

    // ---------------------------------------------------------------------------------------------
    // BaseNavigatorのメソッド
    override fun onCloseFragment() {
        activityNavigator?.onBack()
    }

    override fun moveEditer() {
    }

    override fun changeSwitch(){

    }

    override fun onError(error:String){
        Toast.makeText(context,"" + error, Toast.LENGTH_SHORT).show()
    }






    override fun onGetMusicList(item: SunuzuItem) {

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE
            ) === PackageManager.PERMISSION_GRANTED
        ) {
            // 許可されている時の処理
        } else {
            //許可されていない時の処理
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                //拒否された時 Permissionが必要な理由を表示して再度許可を求めたり、機能を無効にしたりします。
            } else {
                //まだ許可を求める前の時、許可を求めるダイアログを表示します。
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    0
                )

            }
            return
        }

        var position = -1
        var list = ArrayList<String>()

        // 音楽データ取得
        val mediaManager = MediaUtilityManager.getMediaManager(requireContext())
        val musicList = mediaManager.onReadMusicList()

        var selectItem: MediaInfo? = null

        for (i in 0 until musicList.size) {
            val musicItem = musicList.get(i)

            if (musicItem.path.equals(item.sound_path)) {
                selectItem = musicItem
            }

            list.add(musicItem.title + "\n" + musicItem.artist)
        }

        val dialog = DialogMusicList()
        dialog.setOnItemClickListener(object : DialogMusicList.OnDialogMusicListListener {
            override fun selectMusic(mediaInfo: MediaInfo) {
                dialog.close()
                viewModel.setMusic(mediaInfo.title,mediaInfo.path)
            }
        })
        dialog.show(requireActivity().supportFragmentManager,"DialogMusicList")

    }
    fun openDialog(position:Int, list: ArrayList<String>) {
        Log.i(EditFragment.TAG, "openDialog")
        var dialog = DialogList()
        dialog.setList(list)
        dialog.setPosition(position)

        /**
        dialog.setOnItemClickListener(object : DialogMusicList.OnDialogMusicListListener {
            override fun selectMusic(mediaInfo: MediaInfo) {
                dialog.close()
                viewModel.setMusic(mediaInfo.title,mediaInfo.path)
            }
        })
        */
        dialog.show(requireActivity().supportFragmentManager,"")

    }

}