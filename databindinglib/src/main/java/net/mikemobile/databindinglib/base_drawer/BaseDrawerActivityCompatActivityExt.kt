package net.mikemobile.databindinglib.base_drawer

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import net.mikemobile.databindinglib.base.BaseActivity

/************************************************************
 *  Extention Method
 */

/**
 * Runs a FragmentTransaction, then calls commit().
 */
private inline fun FragmentManager.transact(action: FragmentTransaction.() -> Unit) {
    beginTransaction().apply {
        action()
    }.commit()
}




/************************************************************
 *  各ビューを新規生成もしくは既存のFragmentを呼び出すための処理
 */


/**
 * Find fragment from supportFragmentManager
 * @return BaseFragment?
 */
fun AppCompatActivity.findDrawerFragmentByTag(tag: String?) : BaseNavigationDrawerFragment?{
    val fragment = supportFragmentManager.findFragmentByTag(tag)
    return fragment as? BaseNavigationDrawerFragment
}





/**
 * Find fragment from supportFragmentManager
 * @return BaseFragment?
 */
fun AppCompatActivity.findFragmentById(id : Int) : BaseNavigationDrawerFragment?{
    val fragment = supportFragmentManager.findFragmentById(id)
    return fragment as? BaseNavigationDrawerFragment
}

fun BaseActivity.closeDrawer(){}
fun BaseActivity.setDrawerIndicatorEnabled(enable: Boolean){}
fun BaseActivity.addFragmentInDrawerFrame(tag: String){}
fun BaseActivity.replaceFragmentInDrawerFrame(tag: String){}