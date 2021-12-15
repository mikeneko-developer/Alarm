package net.mikemobile.alarm.ui.edit

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
import androidx.fragment.app.FragmentActivity
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import net.mikemobile.alarm.R
import net.mikemobile.alarm.data.ListItem
import net.mikemobile.alarm.database.entity.Item
import net.mikemobile.alarm.databinding.FragmentEditBinding
import net.mikemobile.alarm.log.LogUtil
import net.mikemobile.alarm.services.TimeReceiver
import net.mikemobile.alarm.ui.dialog.*
import net.mikemobile.alarm.ui.edit.pager.Page1Fragment
import net.mikemobile.alarm.ui.edit.pager.Page2Fragment
import net.mikemobile.alarm.ui.edit.sunuzu.list.SunuzuListFragment
import net.mikemobile.alarm.util.Constant.Companion.VIB_LIST
import net.mikemobile.alarm.util.Constant.Companion.VIB_LIST_TIME
import net.mikemobile.android.music.MyVolume
import net.mikemobile.media.MediaInfo
import net.mikemobile.media.MediaUtilityManager
import net.mikemobile.media.system.MediaReadUtil
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.ArrayList

interface EditFragmentNavigator: BaseNavigator {
    fun onError(error:String)
    fun moveEditer()
    fun openCustomSunuzu()

    fun openTimeDialog(time:Long)
    fun openDateDialog(date:Long)
    fun openMusicDeleteDialog()
    fun onGetMusicList(item: Item)

    fun onChangeVolume(progress: Int)
    fun onChangeVib(type: Int)

    fun saveFinish()

    fun setModePage(page: Int)
    fun movePage(move: Int)
}

class EditFragment: BaseFragment(), EditFragmentNavigator {

    private val viewModel: EditViewModel by viewModel()

    companion object {
        const val TAG = "EditFragment"
        fun newInstance() = EditFragment()
    }

    // ---------------------------------------------------------------------------------------------
    //データバインディングを有効にする
    override fun isDataBinding(): Boolean{
        return true
    }

    //
    override fun onCreateViewBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{

        var item: ListItem? = null
        arguments?.let {
            item = it.getSerializable("item") as ListItem?
        }

        val binding = DataBindingUtil.inflate<FragmentEditBinding>(inflater, R.layout.fragment_edit, container,false)
        val view = binding.root
        viewModel.navigator = this
        binding.viewmodel = viewModel

        binding.lifecycleOwner = this

        setPager(view)
        MyVolume.saveSystemVolume(requireContext())

        val maxVolume = MyVolume.getMaxVolume(requireContext())

        binding.seekBar.max = maxVolume + 1
        binding.seekBar.min = 0

        viewModel.setSeekBar(binding.seekBar)

        /////////////////////////////////////
        viewModel.setObserver(this)
        viewModel.initialize(item)

        val tollbarTitle = binding.includeToolbar.findViewById<TextView>(R.id.toolbar_textview)
        tollbarTitle.setText("アラーム編集")


        return view
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
        viewModel.onResume()
        activity?.let {
            it.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT)
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy")
        stopVib()
        MyVolume.resetSystemVolume(requireContext())
        viewModel.removeObserver()
        viewModel.onDestory()
    }

    //
    override fun onBack() {
        Log.i(TAG, "onBack")
        activityNavigator.onBack()
    }

    // ---------------------------------------------------------------------------------------------
    // BaseNavigatorのメソッド
    override fun onCloseFragment() {
        Log.i(TAG, "onCloseFragment")
        activityNavigator.onBack()
    }

    override fun saveFinish(){
        Log.i("TESTTEST","saveFinish")
        //activityNavigator?.startService()
        activityNavigator.onBack()
    }


    override fun moveEditer() {

    }

    override fun onError(error:String){
        Toast.makeText(context,"" + error, Toast.LENGTH_SHORT).show()
    }

    override fun openCustomSunuzu(){
        activityNavigator?.replaceFragmentToBackStackInSecondContentFrame(SunuzuListFragment.TAG)
    }

    override fun onChangeVolume(progress: Int) {
        context?.let {
            if(progress == -1){
                MyVolume.resetSystemVolume(it)
            }else {
                MyVolume.setVolume(it, progress)
            }
        }
    }

    override fun onChangeVib(type: Int) {
        startVib(type)
    }


    override fun openTimeDialog(time:Long){
        var dialog = CDialogTimePicker.newInstance(time)
        dialog.setOnListener(object: DialogInterface.TimePickerSelect {
            override fun TimePickerSelect(hour: Int, minute: Int) {
                viewModel.setTime(hour,minute)
            }
        })
        dialog.show(requireActivity().supportFragmentManager,"")
    }

    override fun openDateDialog(date:Long){
        var dialog = CDialogDatePicker.newInstance(date)
        dialog.setOnListener(object: DialogInterface.DatePickerSelect {
            override fun DatePickerSelect(year: Int, month: Int, day: Int) {
                viewModel.setDate(year,month,day)
            }
        })
        dialog.show(requireActivity().supportFragmentManager,"")
    }

    override fun openMusicDeleteDialog() {
        var dialog = DialogMessage()
        dialog.setMessage("選択曲を削除してもよろしいですか？")
        dialog.setOnDialogMessageListener(object: DialogMessage.OnDialogMessageListener {
            override fun onClickPositive(position: Int, item: Item?) {
                // 選択曲を削除する
                viewModel.deleteMusic()
            }
            override fun onClickNegative(position: Int, item: Item?) {

            }
        })
        dialog.show(requireActivity().supportFragmentManager,"")
    }


    override fun onGetMusicList(item: Item) {
        // パーミッションチェック
        if (!MediaReadUtil().checkPermission(requireActivity())) {
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

        //openDialog(position,list,mList)

        // ダイアログ表示（専用のダイアログを作成する）
        val dialog = DialogMusicList()
        dialog.selectItem(selectItem)
        dialog.setOnItemClickListener(object : DialogMusicList.OnDialogMusicListListener {
            override fun selectMusic(mediaInfo: MediaInfo) {
                dialog.close()
                viewModel.setMusic(mediaInfo.title,mediaInfo.path)
            }
        })
        dialog.show(requireActivity().supportFragmentManager,"DialogMusicList")

    }

    //////////////////////////////////////
    var vibrator: Vibrator? = null
    val handler = Handler()
    fun startVib(type: Int) {
        if(type == 0){
            return
        }else if (vibrator != null) {
            stopVib()
            return
        }

        vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        val vibType = VIB_LIST[type]

        val power = ArrayList<Int>()
        for(i in 0 until vibType.size) {
            if(i % 2 == 0) {
                power.add(DEFAULT_AMPLITUDE)
            }else {
                power.add(0)
            }
        }

        val powerList = power.toIntArray()

        if (Build.VERSION.SDK_INT >= 26) {
            var vibratorEffect = VibrationEffect.createWaveform(vibType, powerList, 0)
            vibrator?.vibrate(vibratorEffect)
        }else {
            vibrator?.vibrate(vibType, -1)
        }

        handler.postDelayed(object: Runnable {
            override fun run() {
                stopVib()
            }
        },VIB_LIST_TIME[type])
    }

    fun stopVib(){
        if(vibrator != null){
            vibrator!!.cancel()
            vibrator = null
        }
    }



    //////////////////////////////////////
    private lateinit var viewPager2: ViewPager2
    fun setPager(view: View) {
        // インスタンス化
        viewPager2 = view.findViewById(R.id.viewPager2)
        // ページインスタンスを用意
        val pagerAdapter = PagerAdapter(requireActivity())
        // セット
        viewPager2.adapter = pagerAdapter
        viewPager2.isUserInputEnabled = false


        viewPager2.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                // 新しいページが表示された　
                Log.d(TAG, "onPageScrollStateChanged position=$position")

                viewModel.setStopModeSelect(position)
            }
        })
    }

    override fun setModePage(page: Int) {
        // ページを1つ進める
        viewPager2.currentItem += page
    }

    override fun movePage(move: Int) {
        // ページを1つ進める
        viewPager2.currentItem += move
    }

    var NUM_PAGES = 1


    private inner class PagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        // ページ数を取得
        override fun getItemCount(): Int = NUM_PAGES

        // スワイプ位置によって表示するFragmentを変更
        override fun createFragment(position: Int): Fragment =
            when(position) {
                0 -> {
                    Page1Fragment()
                }
                1 -> {
                    Page2Fragment()
                }
                2 -> {
                    Page1Fragment()
                }
                else -> {
                    Page1Fragment()
                }
            }
    }


}