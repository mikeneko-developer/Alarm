package net.mikemobile.databindinglib.base_drawer

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import net.mikemobile.databindinglib.R
import net.mikemobile.databindinglib.base.ActivityNavigator
import net.mikemobile.databindinglib.base.findDialogFragmentByTag
import net.mikemobile.databindinglib.base.hideFragment


/**
 * -----------------------------------------------------------------------------
 *
 */
fun ActivityNavigator.closeDrawer() {
    activity?.let{
        (it as BaseNavigationDrawerActivity).closeDrawer()
    }
}
fun ActivityNavigator.setDrawerIndicatorEnabled(enable: Boolean){
    activity?.let{
        (it as BaseNavigationDrawerActivity).setDrawerIndicatorEnabled(enable)
    }
}

/**
 * -----------------------------------------------------------------------------
 *
 */
fun ActivityNavigator.addFragmentInDrawerFrame(tag: String) {
    activity?.let{
        (it as BaseNavigationDrawerActivity).addFragmentInDrawerFrame(tag)
    }
}


fun ActivityNavigator.replaceFragmentInDrawerFrame(tag : String){
    activity?.let{
        (it as BaseNavigationDrawerActivity).replaceFragmentInDrawerFrame(tag)
    }
}
