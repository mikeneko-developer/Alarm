package net.mikemobile.alarm.ui.list

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import net.mikemobile.alarm.R
import net.mikemobile.alarm.data.ListItem
import net.mikemobile.alarm.database.entity.Item
import net.mikemobile.alarm.databinding.FragmentListBinding
import net.mikemobile.alarm.setup.onStopAlarm
import net.mikemobile.alarm.setup.onUpdateAlarm
import net.mikemobile.alarm.ui.clock.ClockFragment
import net.mikemobile.alarm.ui.debug.DebugFragment
import net.mikemobile.alarm.ui.dialog.DialogMessage
import net.mikemobile.alarm.ui.edit.EditFragment
import net.mikemobile.alarm.util.Constant
import net.mikemobile.databindinglib.base.BaseActivity
import net.mikemobile.databindinglib.base.BaseFragment
import net.mikemobile.databindinglib.base.BaseNavigator
import org.koin.android.viewmodel.ext.android.viewModel

interface ListFragmentNavigator: BaseNavigator {
    fun onError(error:String)
    fun moveEditer()
    fun onAddItem(item: ListItem?)
    fun onListItemDelete(position: Int, item: Item)
    fun onOpenClock()
    fun onUpdateAlarm()
    fun startService()
    fun onStopAlarm()
    fun onDebug()
}

class ListFragment: BaseFragment(),ListFragmentNavigator {

    private val viewModel: ListViewModel by viewModel()

    companion object {
        const val TAG = "ListFragment"
        fun newInstance() = ListFragment()
    }

    // ---------------------------------------------------------------------------------------------
    //データバインディングを有効にする
    override fun isDataBinding(): Boolean{
        return true
    }

    //
    override fun onCreateViewBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{

        val binding = DataBindingUtil.inflate<FragmentListBinding>(inflater, R.layout.fragment_list, container,false)
        val view = binding.root
        viewModel.navigator = this
        binding.viewmodel = viewModel

        binding.lifecycleOwner = this

        /////////////////////////////////////

        //val adapter = GroupRecyclerAdapter(this)
        viewModel.setRecyclerView(binding.fragmentAlarmListRecyclerView, context)
        binding.fragmentAlarmListRecyclerView.setLayoutManager(LinearLayoutManager(context))
        binding.fragmentAlarmListRecyclerView.setAdapter(viewModel.adapter)

        // ドラッグアンドドロップ用設定
        binding.fragmentAlarmListRecyclerView.setHasFixedSize(true)
        viewModel.mIth.attachToRecyclerView(binding.fragmentAlarmListRecyclerView)
        viewModel.initialize()

        var tollbarTitle = binding.includeToolbar.findViewById<TextView>(R.id.toolbar_textview)
        tollbarTitle.setText("アラームリスト")


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
            //it.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT)
        }
        viewModel.resume(this)
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onResume() {
        super.onResume()
        activity?.let {
            //it.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT)
        }

        viewModel.resume(this)
    }

    override fun onPause() {
        super.onPause()
        viewModel.pause(this)

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

    }

    override fun moveEditer() {
    }

    override fun onError(error:String){
        Toast.makeText(context,"" + error, Toast.LENGTH_SHORT).show()
    }
    override fun onAddItem(item: ListItem?){
        val bundle = Bundle()
        bundle.putSerializable("item", item)

        activityNavigator.replaceFragmentToBackStackInMainContentFrame(EditFragment.TAG, bundle)
    }

    override fun onListItemDelete(position: Int, item: Item) {
        openDialog(position, item)
    }

    override fun onOpenClock() {
        activityNavigator.replaceFragmentToBackStackInMainContentFrame(ClockFragment.TAG)
    }

    override fun onUpdateAlarm() {
        activityNavigator.onUpdateAlarm()
    }

    override fun startService() {
        //activityNavigator?.startService()
    }

    override fun onStopAlarm() {
        activityNavigator.onStopAlarm()
    }

    override fun onDebug() {
        activityNavigator.replaceFragmentToBackStackInMainContentFrame(DebugFragment.TAG)
    }

    // ---------------------------------------------------------------------------------------------
    @SuppressLint("UseRequireInsteadOfGet")
    fun openDialog(position: Int, item: Item) {
        var dialog = DialogMessage()
        dialog.setListsItem(position, item)
        dialog.setMessage("削除してもよろしいですか？")
        dialog.setOnDialogMessageListener(object: DialogMessage.OnDialogMessageListener {
            override fun onClickPositive(position: Int, item: Item?) {
                viewModel.deleteListItem(item!!)
            }
            override fun onClickNegative(position: Int, item: Item?) {
                viewModel.updateList()
            }
        })
        dialog.show(this.activity?.supportFragmentManager!!,"")
    }
}