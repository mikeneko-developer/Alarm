package net.mikemobile.databindinglib.base

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.os.Message
import android.util.Log
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.base_activity_main.*
import net.mikemobile.databindinglib.*
import org.koin.android.ext.android.inject

abstract class BaseActivity : AppCompatActivity() {

    abstract fun onCreateView(savedInstanceState: Bundle?)

    companion object {
        const val BASE_TAG: String = "BaseActivity"
        val DEFAULT_LAYOUT_ID = R.layout.base_activity_main
        val DEFAULT_TOOLBAR_ID = R.id.toolbar

        val DEFAULT_CONTENT_VIEW_BACKGROUND = R.id.background_content_view
        val DEFAULT_CONTENT_VIEW_MAIN = R.id.main_content_view
        val DEFAULT_CONTENT_VIEW_TAB = R.id.tab_buttons_view
        val DEFAULT_CONTENT_VIEW_SECOND = R.id.second_content_view
        val DEFAULT_CONTENT_VIEW_POPUP = R.id.popup_content_view
        val DEFAULT_CONTENT_VIEW_ALART = R.id.alert_content_view
    }
    private val navigator: ActivityNavigator by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sampleApplication = setActivityApplication()
        sampleApplication.initialize()

        Log.i(BASE_TAG,"BaseActivity >> onCreate")
        var layout_id:Int = onContentViewId(DEFAULT_LAYOUT_ID)

        if (true && layout_id != 0 && layout_id != -1) {

        }else if(layout_id != 0 && layout_id != -1){
            Log.i(BASE_TAG,"BaseActivity >> layout_id:"+ layout_id + " <> default:" + DEFAULT_LAYOUT_ID)
            setContentView(layout_id)
        }

        navigator.activity = onActivity()

        /**
        var toolbar_id = setToolbar(DEFAULT_TOOLBAR_ID)

        Log.i(BASE_TAG,"BaseActivity >> toolbar_id:"+ toolbar_id)

        if(toolbar_id != -1 && toolbar_id != 0) {
            val toolbar: Toolbar = findViewById(toolbar_id)
            setSupportActionBar(toolbar)
        }
        */

        onCreateView(savedInstanceState)

    }
    /*************************************************************/
    // Override出来るように置いているメソッド

    open fun onActivity():BaseActivity{
        return this
    }

    open fun onFragmentFactory(): BaseFragmentFactory {
        return BaseFragmentFactory().getInstance()
    }

    open fun setActivityApplication(): BaseActivityApplication {
        return application as BaseActivityApplication
    }

    open fun onContentViewId(base_layout:Int): Int {
        return base_layout
    }

    // ツールバー設定用
    open fun setToolbar(layout_id:Int):Int{
        return layout_id
    }

    open fun setToolbarEnabled(bool: Boolean) {

    }

    /*************************************************************/
    override fun onDestroy(){
        super.onDestroy()

        android.util.Log.i("TEST_LOG","BaseActivity >> onDestroy()")

        val sampleApplication = setActivityApplication()
        sampleApplication.finishActicities()
        sampleApplication.terminate()

        BaseFragmentFactory().destroyInstance()
    }


    override fun onBackPressed() {
        onBack()
    }

    open fun onBack() {
        android.util.Log.i("TEST_LOG3000","BaseActivity >> onBack()")
        var prevTag: String? = null
        if(true) {
            val cnt = supportFragmentManager.backStackEntryCount
            android.util.Log.i("TEST_LOG3000","BaseActivity >> onBack() cnt:"+cnt)
            if(cnt > 0) {
                var backstack = supportFragmentManager.getBackStackEntryAt(cnt - 1)
                backstack.name?.let{tag ->
                    prevTag = tag
                }
                android.util.Log.i("TEST_LOG3000","BaseActivity >> onBack() tag:"+prevTag)
            }
        }

        if (!popBackStackImmediate()) {
            onFinish()
        }else {
            prevTag?.let{tag ->
                var fragment = findFragmentByTag(tag)
                fragment?.let{
                    it.onBackStackResume()
                }
            }
        }
    }

    open fun onFinish() {
        super.finish()
    }


    override fun onResume() {
        super.onResume()
        android.util.Log.i("TEST_LOG3","BaseActivity >> onResume()")

        // onResumeが呼ばれた時、格納された遷移イベントが呼ばれるリスナーを登録
        onBackStackResume()
    }
    override fun onPause() {
        super.onPause()
        android.util.Log.i("TEST_LOG3","BaseActivity >> onPause()")
        onBackStackPause()
    }

    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){}

    /*************************************************************/
    //Override用メソッド

    open fun onActivityResulted(requestCode: Int, resultCode: Int, data: Intent?){}


    /************************************************************
     *  implements Method [MainActivityNavigator]
     */
    fun checkBackPressToFragment(content_view_id:Int): Boolean{
        val fragment:BaseFragment? = findFragmentById(content_view_id)
        if(fragment != null) {
            Log.i("","BaseNavigationActivity >> onBackPressed() >> alartFragment")
            fragment.onBack()
            return true
        }
        return false
    }

    /************************************************************
     *  implements Method [MainActivityNavigator]
     */

    fun addFragmentInContentFrame(tag: String,layout_id:Int,bundle: Bundle?) {
        val bundleItem = Bundle()
        bundleItem.putString("fragmentTag", tag)
        bundleItem.putInt("resourceId", layout_id)

        bundle?.let{ bundleItem.putAll(it) }

        safetyRun(BackStackHandler.ADD_FRAGMENT,bundleItem)
    }
    fun addFragmentToBackStackInContentFrame(tag: String,layout_id:Int,bundle: Bundle?) {
        val bundleItem = Bundle()
        bundleItem.putString("fragmentTag", tag)
        bundleItem.putInt("resourceId", layout_id)

        bundle?.let{ bundleItem.putAll(it) }

        safetyRun(BackStackHandler.ADD_FRAGMENT_IN_BACKSTACK,bundleItem)
    }

    fun replaceFragmentInContentFrame(tag: String,layout_id:Int, bundle: Bundle?) {
        android.util.Log.i("TEST_LOG","BaseActivity >> replaceFragmentInMainContentFrame() bundle = null = " + (bundle == null))
        val bundleItem = Bundle()
        bundleItem.putString("fragmentTag", tag)
        bundleItem.putInt("resourceId", layout_id)

        bundle?.let{ bundleItem.putAll(it) }

        safetyRun(BackStackHandler.REPLACE_FRAGMENT,bundleItem)
    }
    fun replaceFragmentToBackStackInContentFrame(tag: String,layout_id:Int, bundle: Bundle?) {
        android.util.Log.i("TEST_LOG","BaseActivity >> replaceFragmentInMainContentFrame()")
        val bundleItem = Bundle()
        bundleItem.putString("fragmentTag", tag)
        bundleItem.putInt("resourceId", layout_id)

        bundle?.let{ bundleItem.putAll(it) }

        safetyRun(BackStackHandler.REPLACE_FRAGMENT_IN_BACKSTACK,bundleItem)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    fun popBackStackFragment() {
        popBackStack()
    }


    fun hideFragment(tag: String) {
        hideFragment(findOrCreateViewFragment(onFragmentFactory(), tag, null))
    }

    fun showFragment(tag: String) {
        showFragment(findOrCreateViewFragment(onFragmentFactory(), tag, null))
    }

    fun removeFragment(tag: String) {
        val bundle = Bundle()
        bundle.putString("fragmentTag", tag)

        safetyRun(BackStackHandler.REMOVE_FRAGMENT,bundle)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    fun showDialogFragmentWithTargetFragment(tag: String, fragment: Fragment, requestCode: Int, bundle: Bundle?) {
        val bundleItem = Bundle()
        bundleItem.putString("fragmentTag", tag)
        bundleItem.putInt("requestCode", requestCode)

        bundle?.let{ bundleItem.putAll(it) }

        safetyRun(BackStackHandler.SHOW_DIALOG, bundleItem, fragment)
    }


    fun dismissDialogFragment(tag: String) {
        val bundle = Bundle()
        bundle.putString("fragmentTag", tag)

        safetyRun(BackStackHandler.DISMISS_DIALOG,bundle)
    }


    /************************************************************
     *  implements Method [MainActivityNavigator]
     */

    /**
     * onResume()が呼ばれた時、実行するメソッド
     */
    fun onBackStackResume(){
        onBackStackResume(object: BackStackHandlerListener {
            override fun onProcess(msg: Message, fragment: Fragment?){

                // Bundleから必要な情報を取得する
                val bundle = msg.getData()
                val tag = bundle.getString("fragmentTag")
                val resourceId = bundle.getInt("resourceId")

                // what変数から各遷移先画面への遷移処理を実行する

                when (msg.what) {
                    BackStackHandler.ADD_FRAGMENT -> addFragmentToActivity(findOrCreateViewFragment(onFragmentFactory(), tag, bundle), resourceId, tag)
                    BackStackHandler.REPLACE_FRAGMENT -> replaceFragmentToActivity(findOrCreateViewFragment(onFragmentFactory(), tag, bundle), resourceId, tag)
                    BackStackHandler.ADD_FRAGMENT_IN_BACKSTACK -> addToBackStackFragmentToActivity(findOrCreateViewFragment(onFragmentFactory(), tag, bundle), resourceId, tag)
                    BackStackHandler.REPLACE_FRAGMENT_IN_BACKSTACK -> replaceToBackStackFragmentToActivity(findOrCreateViewFragment(onFragmentFactory(), tag, bundle), resourceId, tag)
                    BackStackHandler.REMOVE_FRAGMENT -> removeFragment(findFragmentByTag(tag))
                    BackStackHandler.DISMISS_DIALOG -> removeFragment(findDialogFragmentByTag(tag))
                    BackStackHandler.SHOW_DIALOG -> {
                        // ダイアログ生成のみrequestCodeとfragmentを使用して実行する
                        val requestCode = bundle.getInt("requestCode")
                        val dialog = findOrCreateDialogFragment(onFragmentFactory(), tag, bundle)

                        fragment?.let{ showDialogFragmentWithTargetFragmentShow(dialog, tag, fragment, requestCode)}
                    }
                }
            }
        })
    }


}
