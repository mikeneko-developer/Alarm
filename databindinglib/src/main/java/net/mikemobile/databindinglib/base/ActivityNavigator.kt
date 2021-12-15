package net.mikemobile.databindinglib.base

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import net.mikemobile.databindinglib.R

open class ActivityNavigator {

    var activity : BaseActivity? = null

    fun onBack() {
        activity?.onBack()
    }

    fun onFinish() {
        activity?.onFinish()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    fun addFragmentInContentFrame(tag:String,layout_id: Int,bundle:Bundle?){
        activity?.addFragmentInContentFrame(tag,layout_id,bundle)
    }
    fun addFragmentToBackStackInContentFrame(tag:String,layout_id: Int,bundle:Bundle?){
        activity?.addFragmentToBackStackInContentFrame(tag,layout_id,bundle)
    }

    fun replaceFragmentInContentFrame(tag : String,layout_id: Int,bundle:Bundle?){
        activity?.replaceFragmentInContentFrame(tag, layout_id,bundle)
    }
    fun replaceFragmentToBackStackInContentFrame(tag : String,layout_id: Int,bundle:Bundle?){
        activity?.replaceFragmentToBackStackInContentFrame(tag, layout_id,bundle)
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    fun addFragmentInMainContentFrame(tag : String){
        android.util.Log.i("TEST_LOG","ActivityNavigator >> replaceFragmentInMainContentFrame( "+tag+" )")
        activity?.addFragmentInContentFrame(tag, BaseActivity.DEFAULT_CONTENT_VIEW_MAIN,null)
    }
    fun replaceFragmentInMainContentFrame(tag : String){
        android.util.Log.i("TEST_LOG","ActivityNavigator >> replaceFragmentInMainContentFrame( "+tag+" )")
        activity?.replaceFragmentInContentFrame(tag, BaseActivity.DEFAULT_CONTENT_VIEW_MAIN,null)
    }
    fun replaceFragmentToBackStackInMainContentFrame(tag : String){
        android.util.Log.i("TEST_LOG","ActivityNavigator >> replaceFragmentInMainContentFrame( "+tag+" )")
        activity?.replaceFragmentToBackStackInContentFrame(tag, BaseActivity.DEFAULT_CONTENT_VIEW_MAIN,null)
    }
    fun replaceFragmentToBackStackInMainContentFrame(tag : String, bundle: Bundle){
        android.util.Log.i("TEST_LOG","ActivityNavigator >> replaceFragmentInMainContentFrame( "+tag+" )")
        activity?.replaceFragmentToBackStackInContentFrame(tag, BaseActivity.DEFAULT_CONTENT_VIEW_MAIN,bundle)
    }

    // ---------------------------------------------------------

    fun addFragmentInSecondContentFrame(tag: String) {
        activity?.addFragmentInContentFrame(tag,BaseActivity.DEFAULT_CONTENT_VIEW_SECOND,null)
    }
    fun addFragmentToBackStackInSecondContentFrame(tag: String) {
        activity?.addFragmentToBackStackInContentFrame(tag,BaseActivity.DEFAULT_CONTENT_VIEW_SECOND,null)
    }
    fun replaceFragmentToBackStackInSecondContentFrame(tag: String) {
        activity?.replaceFragmentToBackStackInContentFrame(tag,BaseActivity.DEFAULT_CONTENT_VIEW_SECOND,null)
    }
    fun replaceFragmentToBackStackInSecondContentFrame(tag: String, bundle: Bundle) {
        activity?.replaceFragmentToBackStackInContentFrame(tag,BaseActivity.DEFAULT_CONTENT_VIEW_SECOND,bundle)
    }

    /**
     * replaceFragmentToBackStackInSecondContentFrame(tag: String)
     *
     * @brief Replase Fragment in second content frame with backStack Entry.
     *         Clear the already entered all Fragments
     * @param tag : Target Fragment ID of adding
     */

    fun popBackStackFragment() {
        activity?.popBackStackFragment()
    }

    fun hideFragment(tag:String) {
        activity?.hideFragment(tag)
    }

    fun showFragment(tag:String) {
        activity?.showFragment(tag)
    }

    fun removeFragment(tag: String) {
        activity?.removeFragment(tag)
    }

    fun showDialogFragmentWithTargetFragment(tag: String, fragment: Fragment, requestCode: Int, bundle: Bundle) {
        activity?.showDialogFragmentWithTargetFragment(tag, fragment, requestCode, bundle)
    }

    fun dismissDialogFragment(tag: String) {
        activity?.dismissDialogFragment(tag)
    }


    fun onActivityResulted(requestCode: Int, resultCode: Int, data: Intent?){
        activity?.onActivityResulted(requestCode,resultCode,data)
    }

}
