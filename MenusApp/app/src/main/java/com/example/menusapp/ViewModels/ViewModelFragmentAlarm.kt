package com.example.menusapp.ViewModels

import android.util.Log
import android.view.ActionMode
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.menusapp.Adapters.AlarmAdapter
import com.example.menusapp.Models.AlarmItem
import com.example.menusapp.R

class ViewModelFragmentAlarm: ViewModel() {
    internal lateinit var alarmAdapter:AlarmAdapter
    internal var isActionModeOn: MutableLiveData<Boolean> = MutableLiveData(false)
    internal var actionMode:ActionMode? = null
    private var dataSource:MutableLiveData<ArrayList< MutableLiveData< AlarmItem> >> = MutableLiveData<ArrayList< MutableLiveData< AlarmItem> >>(
        ArrayList(0)
    )

    var numItemsSelected = MutableLiveData<Int>(0)



    internal var callBackActionMode: ActionMode.Callback = object:ActionMode.Callback{


        override fun onCreateActionMode(actionMode: ActionMode?, menu: Menu?): Boolean {
            val inflater:MenuInflater? = actionMode?.menuInflater
            inflater?.inflate(R.menu.activity_main_action_mode, menu)
            Log.i(TAG,"onCreateActionMode...Start observing")
            Log.i( TAG, "${this@ViewModelFragmentAlarm.numItemsSelected.value}" )


            this@ViewModelFragmentAlarm.numItemsSelected.observeForever  {
                menu?.findItem(R.id.action_bar_itemsSelected)?.title = "${this@ViewModelFragmentAlarm.numItemsSelected.value}"+ " selected"
                Log.i(TAG,"${this@ViewModelFragmentAlarm.numItemsSelected.value} Hi" )
            }

            this@ViewModelFragmentAlarm.numItemsSelected.observeForever  {
                    numItemsSelected ->
                val menuItem = menu?.findItem(R.id.action_bar_selectAll)
                if(numItemsSelected == dataSource.value?.size &&   !menuItem?.isChecked!! ){
                    menuItem.isChecked = true
                }

                Log.i(TAG,"${this@ViewModelFragmentAlarm.numItemsSelected.value} Hi" )
            }


            return true
        }

        override fun onPrepareActionMode(actionMode: ActionMode?, menu: Menu?): Boolean {
            return false
        }

        override fun onActionItemClicked(actionMode: ActionMode?, menuItem: MenuItem?): Boolean {

            when(menuItem?.itemId){
                R.id.action_bar_close -> {
                    this@ViewModelFragmentAlarm.isActionModeOn.value = false
                    actionMode?.finish()
                    return true
                }
                R.id.action_bar_selectAll->{
                    menuItem.isChecked = !menuItem.isChecked

                    when(menuItem.isChecked){
                        true->{
                            selectAllData()
                        }
                        false->{
                            unSelectAllData()
                        }
                    }


                }
                else ->{

                }
            }
            return false
        }

        override fun onDestroyActionMode(actionMode: ActionMode?) {
            this@ViewModelFragmentAlarm.actionMode = null
            this@ViewModelFragmentAlarm.isActionModeOn.value = false
            this@ViewModelFragmentAlarm.numItemsSelected.value = 0
        }

    }


    fun populateDataSource(){
        Log.i(TAG, "populateDataSource")
        if( dataSource.value != null ){
            dataSource.value?.apply {
                add(MutableLiveData<AlarmItem>(AlarmItem("09:10", "Daily| Alarm in 17 hours 3 minutes", false,false,true)))

                add(MutableLiveData<AlarmItem>(AlarmItem("12:35", "Alarm in 20 hours 37 minutes", false,false,true)))
                add(MutableLiveData<AlarmItem>(AlarmItem("12:35", "Alarm in 20 hours 37 minutes", false,false,true)))
                add(MutableLiveData<AlarmItem>(AlarmItem("12:35", "Alarm in 20 hours 37 minutes", false,false,true)))
                add(MutableLiveData<AlarmItem>(AlarmItem("12:35", "Alarm in 20 hours 37 minutes", false,false,true)))
                add(MutableLiveData<AlarmItem>(AlarmItem("12:35", "Alarm in 20 hours 37 minutes", false,false,true)))
                add(MutableLiveData<AlarmItem>(AlarmItem("12:35", "Alarm in 20 hours 37 minutes", false,false,true)))
                add(MutableLiveData<AlarmItem>(AlarmItem("12:35", "Alarm in 20 hours 37 minutes", false,false,true)))
                add(MutableLiveData<AlarmItem>(AlarmItem("12:35", "Alarm in 20 hours 37 minutes", false,false,true)))
            }

        }else{
            Log.i(TAG, "populateDataSource fail")
        }

    }

    fun createAdapter(activity: FragmentActivity?):Boolean {
        Log.i(TAG, "createAdapter")

        populateDataSource()
        if(dataSource.value != null ){
            printDataSource(dataSource.value!!)
            this.alarmAdapter = AlarmAdapter(dataSource,this)
            putObservers()
            return true
        }
        return false
    }

    private fun printDataSource(arrayList: ArrayList<MutableLiveData<AlarmItem>>) {
        arrayList.forEach {
            Log.i(TAG, it.value.toString())
        }

    }


    private fun putObservers() {
        //set all data to unselected
        /*
        this.isActionModeOn.observeForever {
                isActionModeOn ->
            if(!isActionModeOn) unSelectAllData()
        }
        */

        Log.i(TAG,"${dataSource.value?.size} ")
        dataSource.value?.forEach {
                mutableAlarmItem ->
            mutableAlarmItem.observeForever {
                Log.i(TAG, "mutableAlarmItem.observeForever ${mutableAlarmItem.value}")
                if(!mutableAlarmItem.value?.firstTime!!){
                    if(mutableAlarmItem.value?.isSelected!!){
                        this.numItemsSelected.value = this.numItemsSelected.value?.plus(1)
                    }else{
                        this.numItemsSelected.value = this.numItemsSelected.value?.minus(1)
                    }
                    Log.i(TAG, "Items selected ${this.numItemsSelected.value}")

                }
                mutableAlarmItem.value?.firstTime = false
            }
        }
    }

    private fun unSelectAllData() {
        dataSource.value?.forEach {
                alarmItem ->
            if(alarmItem.value?.isSelected == true) alarmItem.value?.isSelected = false
        }
    }

    private fun selectAllData() {
        dataSource.value?.forEach {
                alarmItem ->
            if(alarmItem.value?.isSelected == false) alarmItem.value?.isSelected = true
        }
    }



    companion object{
        const val TAG = "VmFragmentAlarmLogger"
    }
}