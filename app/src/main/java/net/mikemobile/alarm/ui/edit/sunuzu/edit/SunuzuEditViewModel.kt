package net.mikemobile.alarm.ui.edit.sunuzu.edit

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.mikemobile.alarm.database.DataBaseModel
import net.mikemobile.alarm.database.entity.Item
import net.mikemobile.alarm.database.entity.SunuzuItem
import net.mikemobile.alarm.repository.DataRepository
import net.mikemobile.alarm.util.CustomDateTime
import net.mikemobile.android.music.OnMediaListener
import net.mikemobile.databindinglib.base.BaseFragment
import net.mikemobile.media.MediaUtilityManager


class SunuzuEditViewModel(
    private val context: Context,
    private val databaseModel: DataBaseModel,
    private val dataRepository: DataRepository,
    private val mediaController: OnMediaListener): ViewModel(){

    var navigator : SunuzuEditFragmentNavigator? = null

    var inputOwnerYear: MutableLiveData<String> = MutableLiveData<String>().apply{value = "" + CustomDateTime.getYearText()}
    var inputOwnerMonth: MutableLiveData<String> = MutableLiveData<String>().apply{value = "" + CustomDateTime.getMonthText()}
    var inputOwnerDay: MutableLiveData<String> = MutableLiveData<String>().apply{value = "" + CustomDateTime.getDayText()}
    var inputOwnerHour: MutableLiveData<String> = MutableLiveData<String>().apply{value = "" + CustomDateTime.getHourText()}
    var inputOwnerMinute: MutableLiveData<String> = MutableLiveData<String>().apply{value = "" + CustomDateTime.getMinuteText()}


    var input100: MutableLiveData<String> = MutableLiveData<String>().apply{value = "" + 0}
    var input10: MutableLiveData<String> = MutableLiveData<String>().apply{value = "" + 0}
    var input1: MutableLiveData<String> = MutableLiveData<String>().apply{value = "" + 0}


    var sunuzuItem: MutableLiveData<SunuzuItem> = MutableLiveData<SunuzuItem>().apply{value = SunuzuItem()}

    var soundPlay: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply{value = false}
    var viewMusicName: MutableLiveData<String> = MutableLiveData<String>().apply{value = ""}

    fun initialize(){

        var position = dataRepository.editSunuzuPosition


        var owner_year = CustomDateTime.getYear()
        var owner_month = CustomDateTime.getMonth()
        var owner_day = CustomDateTime.getDay()
        var owner_hour = CustomDateTime.getHour()
        var owner_minute = CustomDateTime.getMinute()

        dataRepository.editItem.value?.let{
            owner_year = it.year
            owner_month = it.month
            owner_day = it.day
            owner_hour = it.hour
            owner_minute =it.minute
        }


        inputOwnerYear.postValue("" + owner_year)
        inputOwnerMonth.postValue("" + owner_month)
        inputOwnerDay.postValue("" + owner_day)
        inputOwnerHour.postValue("" + owner_hour)
        inputOwnerMinute.postValue("" + owner_minute)

        var list = ArrayList<SunuzuItem>()
        dataRepository.editSunuzuList.value?.let {
            list = it
        }

        var item = SunuzuItem()
        var new = false

        if(list.size == 0 || position == -1) {
            new = true
        }else {
            item = list[position]
        }


        sunuzuItem.postValue(item)

        var date_text = "" + item.plusMinute
        if(new){
            date_text = "" + dataRepository.editSunuzuNextMinute
        }

        var i100 = "0"
        var i10 = "0"
        var i1 = "0"
       if(date_text.length == 3){
            i100 = date_text.substring(0,1)
            i10 = date_text.substring(1,2)
            i1 = date_text.substring(2)
        }else if(date_text.length == 2){
            i10 = date_text.substring(0,1)
            i1 = date_text.substring(1)
        }else {
            i1 = date_text
        }

        input100.postValue(i100)
        input10.postValue(i10)
        input1.postValue(i1)

    }

    fun resume(fragment: BaseFragment){
        // ガイド終了時の処理


    }

    fun destroy(){
        if(soundPlay.value!!){
            mediaController.onStop()
        }
    }

    fun clickSaveAlarm(){
        Log.i("TEST_LOG100","click_button ViewModel")
        try{
            var finish = false

            var owner_year = CustomDateTime.getYear()
            var owner_month = CustomDateTime.getMonth()
            var owner_day = CustomDateTime.getDay()
            var owner_hour = CustomDateTime.getHour()
            var owner_minute = CustomDateTime.getMinute()
            /**
            dataRepository.editItem?.let{
                owner_year = it.year
                owner_month = it.month
                owner_day = it.day
                owner_hour = it.hour
                owner_minute =it.minute
            }
*/
            var owner_date = CustomDateTime.getTimeInMillis(owner_year,owner_month,owner_day,owner_hour,owner_minute)
            Log.i("TEST_LOG100","click_button owner_date:" + CustomDateTime.getDateTimeText(owner_date))

            var newItem : SunuzuItem? = null

            sunuzuItem.value?.let{
                newItem = it
            }

            if(newItem != null){

                val day = input100.value?.toInt()!!

                val hour = input10.value?.toInt()!!
                val minute = input1.value?.toInt()!!

                val plus_minute = ("" + day + "" + hour + "" + minute).toInt()
                Log.i("TEST_LOG100","click_button plus_minute:" + plus_minute)

                val next_date = CustomDateTime.getNextMinute(owner_date,plus_minute)
                Log.i("TEST_LOG100","click_button next_date:" + CustomDateTime.getDateTimeText(next_date))

                newItem!!.plusMinute = plus_minute
                val position = dataRepository.editSunuzuPosition

                if(position == -1) {
                    dataRepository.addSunuzuItem(newItem!!)
                }else {
                    dataRepository.setSunuzuItem(position, newItem!!)
                }

                finish = true
            }


            if(finish) {
                navigator?.onCloseFragment()
            }

        }catch(e:Exception){
            navigator?.onError("時間を入力してください")
        }


    }

    fun clickOpenMusic(){
        val item = sunuzuItem.value
        item?.let{item ->
            navigator?.onGetMusicList(item)
        }
    }

    fun clickChangeSwitch(){

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////



    /**
     *
     */
    fun clickPlay(){
        if(mediaController.isPlay()){
            soundPlay.postValue(false)
            mediaController.onStop()

        }else {
            val item = sunuzuItem.value
            item?.let{item ->
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
        val item = sunuzuItem.value
        item?.let{item ->
            item.sound_name = title
            item.sound_path = path

            sunuzuItem.postValue(item)

            val mediaManager = MediaUtilityManager.getMediaManager(context)
            val musicItem = mediaManager.onReadMusicData(item.sound_path)

            musicItem?.let {
                mediaController.onPlay(it.data!!)
            }
        }
        viewMusicName.postValue(title)
        soundPlay.postValue(true)

        //navigator?.playMusic(path)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    fun clickDay(calc: Int){
        var num = 0
        input100.value?.let{
            if(!it.equals("")) {
                num = it.toInt()
            }
        }
        num = num + calc
        if(num > 9){ num = 0 }
        else if(num < 0){num = 9}

        input100.postValue("" + num)
    }
    fun clickHour(calc: Int){
        var num = 0

        input10.value?.let{
            if(!it.equals("")) {
                num = it.toInt()
            }
        }
        num = num + calc
        if(num > 9){ num = 0 }
        else if(num < 0){num = 9}

        input10.postValue("" + num)
    }
    fun clickMinute(calc: Int){
        var num = 0

        input1.value?.let{
            if(!it.equals("")) {
                num = it.toInt()
            }
        }
        num = num + calc
        if(num > 9){ num = 0 }
        else if(num < 0){num = 9}

        input1.postValue("" + num)

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////


}