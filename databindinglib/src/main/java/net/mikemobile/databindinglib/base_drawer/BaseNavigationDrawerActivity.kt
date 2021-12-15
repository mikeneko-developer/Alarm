package net.mikemobile.databindinglib.base_drawer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.base_drawer_activity_main.*
import net.mikemobile.databindinglib.*
import net.mikemobile.databindinglib.base.*
import org.koin.android.ext.android.inject

/**
 * Toolberを設定するため、AndroidManifest.xml内で
 * <activity>に「android:theme="@style/AppTheme"」を追加してください
 */

interface OnSelectNavigationListener{
    fun onSelectNavigation(id:Int?, position:Int?, obj: DrawerItem?)
}

abstract class BaseNavigationDrawerActivity : BaseActivity(){

    companion object {
        const val TAG: String = "BaseNavigationActivity"
        val BASE_NAVIGATION_LAYOUT_ID = R.layout.base_drawer_activity_main
    }


    private var bool:Boolean = false

    override fun onActivity():BaseActivity {
        return this
    }

    override fun onFragmentFactory(): BaseFragmentFactory {
        return BaseFragmentFactory().getInstance()
    }

    override fun onContentViewId(base_layout:Int): Int {
        setContentView(BASE_NAVIGATION_LAYOUT_ID)
        return -1
    }

    // ツールバー設定用
    override fun setToolbar(layout_id:Int):Int{
        Log.i(BASE_TAG,"BaseNavigationDrawerActivity >> layout_id:"+ layout_id)
        return -1
    }

    override fun onCreateView(savedInstanceState: Bundle?){

        val toolbar: Toolbar = findViewById(R.id.drawer_toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
            return
        }

        super.onBackPressed()
    }



    /************************************************************
     *
     */

    override fun onActivityResulted(requestCode: Int, resultCode: Int, data: Intent?){

        val drawerFragment:BaseNavigationDrawerFragment? = findFragmentById(R.id.base_navigation_drawer_frame)
        if(drawerFragment != null) {
            Log.i(TAG,"BaseNavigationActivity >> onBackPressed() >> drawerFragment")
            drawerFragment.onActivityResulted(requestCode,resultCode,data)
            return
        }

        super.onActivityResulted(requestCode, resultCode, data)

    }
    /************************************************************
     *
     */
    fun closeDrawer() {
        drawer_layout.closeDrawer(GravityCompat.START)
    }

    fun setDrawerIndicatorEnabled(enable: Boolean) {
        drawer_layout.setDrawerLockMode(if(enable) DrawerLayout.LOCK_MODE_UNLOCKED else DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    /************************************************************
     *  implements Method [MainActivityNavigator]
     */

    fun addFragmentInDrawerFrame(tag: String) {
        android.util.Log.i("TEST_LOG","BaseNavigationActivity >> addFragmentInDrawerFrame()")
        val bundle = Bundle()
        bundle.putString("fragmentTag", tag)
        bundle.putInt("resourceId", R.id.base_navigation_drawer_frame)

        safetyRun(BackStackHandler.ADD_FRAGMENT,bundle)
    }

    fun replaceFragmentInDrawerFrame(tag: String) {
        android.util.Log.i("TEST_LOG","BaseNavigationActivity >> replaceFragmentInDrawerFrame()")
        val bundle = Bundle()
        bundle.putString("fragmentTag", tag)
        bundle.putInt("resourceId", R.id.base_navigation_drawer_frame)

        safetyRun(BackStackHandler.REPLACE_FRAGMENT,bundle)
    }

    /************************************************************
     *
     */


}
