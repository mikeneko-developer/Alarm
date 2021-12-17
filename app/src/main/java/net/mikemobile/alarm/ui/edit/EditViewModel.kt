package net.mikemobile.alarm.ui.edit

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import net.mikemobile.alarm.data.ListItem
import net.mikemobile.alarm.database.DataBaseManager
import net.mikemobile.alarm.database.DataBaseModel
import net.mikemobile.alarm.database.OnDatabaseListener
import net.mikemobile.alarm.database.entity.Alarm
import net.mikemobile.alarm.database.entity.Item
import net.mikemobile.alarm.database.entity.SunuzuItem
import net.mikemobile.alarm.log.LogUtil
import net.mikemobile.alarm.repository.DataRepository
import net.mikemobile.alarm.repository.DataRepositoryListener
import net.mikemobile.alarm.repository.DataSaveListener
import net.mikemobile.alarm.services.TimeReceiver
import net.mikemobile.alarm.util.Constant
import net.mikemobile.alarm.util.Constant.Companion.SUNUZU_COUNT_MUGEN
import net.mikemobile.alarm.util.Constant.Companion.SUNUZU_TIME_TEXT_LIST
import net.mikemobile.alarm.util.CustomDateTime
import net.mikemobile.android.music.OnMediaListener
import net.mikemobile.databindinglib.base.BaseFragment
import net.mikemobile.media.MediaUtilityManager
import java.util.*


class EditViewModel(
    private val context: Context,
    private val dataBaseModel: DataBaseModel,
    private val dataRepository: DataRepository,
    private val mediaController: OnMediaListener
): ViewModel(){

    companion object {
        const val TAG = "EditViewModel"
    }
    var navigator : EditFragmentNavigator? = null

    val handler = Handler()
    //////////////////////////////////////////////////////////////////////////////////////////////

    var data: MutableLiveData<Item> = MutableLiveData<Item>()

    var sunuzuOnOff: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    var soundPlay: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply{value = false}

    var sunuzuCustom: MutableLiveData<Boolean> = MutableLiveData<Boolean>()

    //////////////////////////////////////////////////////////////////////////////////////////////
    var viewTime: MutableLiveData<String> = MutableLiveData<String>()
    var viewDate: MutableLiveData<String> = MutableLiveData<String>()

    var viewMusicName: MutableLiveData<String> = MutableLiveData<String>()

    var viewSunuzuTime: MutableLiveData<String> = MutableLiveData<String>()
    var viewSunuzuCount: MutableLiveData<String> = MutableLiveData<String>()

    var viewVolume: MutableLiveData<String> = MutableLiveData<String>()
    //////////////////////////////////////////////////////////////////////////////////////////////

    var seekBarListener = object: SeekBar.OnSeekBarChangeListener{
        override fun onStartTrackingTouch(seekBar: SeekBar?) {}
        override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            var volume = progress - 1


            if(volume == -1) {
                viewVolume.postValue("端末設定")
            }else {
                viewVolume.postValue("" + volume)
            }

            setVolume(volume)
        }
    }

    var volumeSeekBar: SeekBar? = null

    fun setSeekBar(seekBar: SeekBar) {
        volumeSeekBar = seekBar
        volumeSeekBar?.setOnSeekBarChangeListener(seekBarListener)
    }


    //////////////////////////////////////////////////////////////////////////////////////////////

    val itemObserver = Observer<Item?> { item ->
        LogUtil.i(TAG, "Observer Response ")
        handler.post(object:Runnable{
            override fun run() {
                // 画面への反映
                if (item == null) {
                    LogUtil.e(TAG, "Observer Response is null")
                    LogUtil.toast(context, "アイテム情報を取得できませんでした")
                } else {
                    LogUtil.i(TAG, "Observer Response is not null")
                    setViewItem(item!!)
                }
            }
        })
    }

    fun setObserver(fragment: BaseFragment){
        LogUtil.i(TAG, "setObserver() ")
        dataRepository.editItem.observe(fragment, itemObserver)
    }

    fun removeObserver(){
        LogUtil.i(TAG, "removeObserver() ")
        dataRepository.editItem.removeObserver(itemObserver)
    }



    fun initialize(_item: ListItem?){
        LogUtil.i(TAG, "initialize() ")
        val item: Item
        if (_item == null) {
            item = Item()
            item.onoff = true
            item.year = CustomDateTime.getYear()
            item.month = CustomDateTime.getMonth()
            item.day = CustomDateTime.getDay()
            item.week = CustomDateTime.getWeek()
            item.hour = CustomDateTime.getHour()
            item.minute = CustomDateTime.getMinute()

            dataRepository.editSunuzuList.postValue(ArrayList<SunuzuItem>())
        } else {
            item = _item.data
            dataRepository.editSunuzuList.postValue(_item.sunuzuList)
        }
        setViewItem(item)

    }

    fun setViewItem(item: Item) {
        LogUtil.i(TAG, "setViewItem() " + item.id)
        data.postValue(item)

        viewDate.postValue(
            CustomDateTime.getYearText(item.year) +
                    "/" + CustomDateTime.getMonthText(item.month) +
                    "/" + CustomDateTime.getDayText(item.day))

        viewTime.postValue(CustomDateTime.getHourText(item.hour) +
                ":" + CustomDateTime.getMinuteText(item.minute))

        viewSunuzuCount.postValue(getSunuzuCountText(item))

        viewSunuzuTime.postValue(getSunuzuTimeText(item))

        sunuzuOnOff.postValue(item.sunuzu)

        val volume = item.sound_volume
        volumeSeekBar?.progress = (volume + 1)

        if(volume == -1) {
            viewVolume.postValue("端末設定")
        }else {
            viewVolume.postValue("" + volume)
        }

        navigator?.onChangeVolume(volume)

        viewMusicName.postValue(item.sound_name)
        sunuzuCustom.postValue(item.sunuzu_custom)


        navigator?.setModePage(item.stopModeSelect)
    }

    fun onResume() {

    }

    fun onPause() {
        //dataBaseModel.setOnDatabaseListener(null)
    }

    fun onDestory() {
        mediaController.onStop()
        data.postValue(Item())
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * スイッチ切り替え
     */
    fun clickChangeSwitch(){
        data.value?.let{item ->
            item.onoff = !item.onoff
            data.postValue(item)
        }

    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    fun clickOpenTimeDialog(){
        data.value?.let{item ->
            val time = CustomDateTime.getTimeInMillisStartHour(item.hour, item.minute)
            navigator?.openTimeDialog(time)
        }
    }


    fun setTime(hour:Int,minute:Int){
        data.value?.let{item ->
            item.hour = hour
            item.minute = minute

            data.postValue(item)
        }

        viewTime.postValue(CustomDateTime.getHourText(hour) +
                ":" + CustomDateTime.getMinuteText(minute))

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    fun clickOpenDateDialog(){
        data.value?.let{item ->
            val date = CustomDateTime.getTimeInMillis(item.year,item.month,item.day)
            navigator?.openDateDialog(date)
        }
    }
    fun setDate(year:Int,month:Int,day:Int) {
        data.value?.let{item ->
            item.year = year
            item.month = month
            item.day = day

            data.postValue(item)
        }

        viewDate.postValue(CustomDateTime.getYearText(year) +
                "/" + CustomDateTime.getMonthText(month) +
                "/" + CustomDateTime.getDayText(day))
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    fun clickLoop(){
        data.value?.let{item ->
            if(item.type == Constant.Companion.AlarmType.LoopWeek.id){
                item.type = Constant.Companion.AlarmType.OneTime.id
            }else {
                item.type = Constant.Companion.AlarmType.LoopWeek.id
            }

            data.postValue(item)
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////

    fun clickWeek(id: Int){
        data.value?.let{item ->
            if(id == 0){
                item.week_sun = if(item.week_sun != 1){1}else{0}

            }else if(id == 1){
                item.week_mon = if(item.week_mon != 1){1}else{0}

            }else if(id == 2){
                item.week_tue = if(item.week_tue != 1){1}else{0}

            }else if(id == 3){
                item.week_wed = if(item.week_wed != 1){1}else{0}

            }else if(id == 4){
                item.week_the = if(item.week_the != 1){1}else{0}

            }else if(id == 5){
                item.week_fri = if(item.week_fri != 1){1}else{0}

            }else if(id == 6){
                item.week_sat = if(item.week_sat != 1){1}else{0}
            }

            data.postValue(item)
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    fun clickOpenMusic(){
        LogUtil.i(TAG,"clickOpenMusic")
        data.value?.let{item ->
            navigator?.onGetMusicList(item)
        }
    }

    fun longClickMusic(): Boolean {
        LogUtil.i(TAG,"longClickMusic")
        navigator?.openMusicDeleteDialog()
        return true
    }

    fun deleteMusic() {
        data.value?.let{item ->
            item.sound_name = ""
            item.sound_path = ""

            data.postValue(item)

            viewMusicName.postValue("")
            soundPlay.postValue(false)
            mediaController.onStop()
        }
    }

    /**
     *
     */
    fun clickPlay(){
        if(mediaController.isPlay()){
            soundPlay.postValue(false)
            mediaController.onStop()

        }else {
            data.value?.let{item ->
                soundPlay.postValue(true)

                val mediaManager = MediaUtilityManager.getMediaManager(context)
                val musicItem = mediaManager.onReadMusicData(item.sound_path)

                musicItem?.let {
                    mediaController.onPlay(it.data!!)
                }

            }
        }
    }

    /**
     *
     */
    fun setMusic(title:String,path:String){
        viewMusicName.postValue(title)
        soundPlay.postValue(true)

        data.value?.let{item ->
            item.sound_name = title
            item.sound_path = path

            data.postValue(item)
            viewMusicName.postValue(item.sound_name)

            val mediaManager = MediaUtilityManager.getMediaManager(context)
            val musicItem = mediaManager.onReadMusicData(item.sound_path)

            musicItem?.let {
                mediaController.onPlay(it.data!!)
            }
        }
    }

    fun setVolume(progress: Int) {
        data.value?.let{item ->
            item.sound_volume = progress
            data.postValue(item)
        }

        navigator?.onChangeVolume(progress)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////



    ////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     *
     */
    fun clickChangeSunuzu(){
        data.value?.let{item ->
            item.sunuzu = if(!item.sunuzu){true} else {false}

            if (!item.sunuzu) {
                item.stopMode = false
            }

            data.postValue(item)
            sunuzuOnOff.postValue(item.sunuzu)
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     *
     */
    fun clickSunuzuTime(calc: Int){
        data.value?.let{item ->
            item.setSunuzuTimeCalc(calc)
            data.postValue(item)

            viewSunuzuTime.postValue(getSunuzuTimeText(item))
            sunuzuCustom.postValue(item.sunuzu_custom)
        }

    }

    /**
     *
     */
    fun getSunuzuTimeText(item: Item = Item()): String{
        var time = item.sunuzu_time
        return SUNUZU_TIME_TEXT_LIST[time]
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     *
     */
    fun clickSunuzuCount(calc: Int){
        data.value?.let{item ->
            item.setSunuzuCountCalc(calc)
            data.postValue(item)
            viewSunuzuCount.postValue(getSunuzuCountText(item))
        }
    }

    /**
     *
     */
    fun getSunuzuCountText(item: Item = Item()): String{
        var count = item.sunuzu_count

        if(count == SUNUZU_COUNT_MUGEN){
            return "無制限"
        }else {
            return "" + count + "回"
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     *
     */
    fun clickSunuzuList(){
        navigator?.openCustomSunuzu()
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * バイブの有効＜＞無効の切り替え
     */
    fun clickChangeVib(){
        data.value?.let{item ->
            if(item.vib == 0){
                item.vib = 1
                navigator?.onChangeVib(item.vib)
            }else {
                item.vib = 0
                navigator?.onStopVib()
            }
            data.postValue(item)
        }
    }

    /**
     * バイブのタイプ切り替え
     */
    fun clickChangeVib(id:Int){
        data.value?.let{item ->
            if(item.vib > 0){
                item.vib = id
                data.postValue(item)
            }
            navigator?.onChangeVib(item.vib)
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    fun clickMode() {
        data.value?.let{item ->
            if(item.sunuzu) {
                item.stopMode = !item.stopMode
            }
            data.postValue(item)
        }
    }

    fun clickModePlus(){
        navigator?.movePage(1)
    }

    fun clickModeMinus() {
        navigator?.movePage(-1)
    }

    fun setStopModeSelect(position: Int) {
        data.value?.let{item ->
            item.stopModeSelect = position
            data.postValue(item)
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     *
     */
    fun clickSaveAlarm(){
        LogUtil.i(TAG,"clickSaveAlarm")

        if (data == null) {
            LogUtil.e(TAG,"data がありません")
        }
        data.value?.let {

            dataRepository.saveItemListener(it, object:  DataRepositoryListener() {
                override fun onSavedItem(item: Item) {
                    Log.i(TAG,"onSavedItem")
                    dataRepository.saveItemListener(it,null)
                    saveEnd()
                }

                override fun onError(type: DataBaseManager.Companion.ERROR_TYPE, error: String) {
                    LogUtil.toast(context, "" + error)
                    dataRepository.saveItemListener(it,null)
                }
            })
        }
    }

    private fun saveEnd() {
        Log.i(TAG,"saveEnd")
        handler.post(object:Runnable{
            override fun run() {
                data.value?.let{item ->
                    if(item.onoff) {
                        TimeReceiver.actionReciever(context, "EditViewModel saveEnd()")
                    }
                }
                navigator?.saveFinish()
            }
        })
    }

}