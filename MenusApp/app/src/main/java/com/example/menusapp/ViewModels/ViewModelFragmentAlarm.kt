package com.example.menusapp.ViewModels

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.ActionMode
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.menusapp.Adapters.AlarmAdapter
import com.example.menusapp.Models.AlarmItem
import com.example.menusapp.NotificationUpdateDB.Notification
import com.example.menusapp.R

class ViewModelFragmentAlarm: ViewModel() {
    var contextFragment: Context? = null
    var fragmentManager:FragmentManager? = null

    internal lateinit var alarmAdapter:AlarmAdapter

    internal var isActionModeOn: MutableLiveData<Boolean> = MutableLiveData(false)
    internal var actionMode:ActionMode? = null
    private var dataSource:MutableLiveData<ArrayList< AlarmItem> > = MutableLiveData<ArrayList<AlarmItem>>(ArrayList())
    internal var selectAll = MutableLiveData<Boolean>(false)
    internal var unSelectAll = MutableLiveData<Boolean>(false)


    init{


        Log.i(TAG, "Registering Notification observer...")
        Notification.LiveDataDelete.observeForever {
            delete ->
            Log.i(TAG, "Receiving notification to delete...")
            if(delete.ready){
                delete.ready = false
                if(delete.deleteChore){
                    deleteDataItem(delete.position)
                }
                delete.ready = true
                delete.deleteChore = false
            }

        }




    }

    private fun deleteDataItem(position: Int) {
        Log.i(TAG, "Deleting item pos: $position")
        val alarmItem = dataSource.value?.removeAt(position)
        Log.i(TAG, "This was removed from the list $alarmItem")
        this.alarmAdapter.notifyDataSetChanged()
        Toast.makeText(this.contextFragment, "This was remove from the list $alarmItem", Toast.LENGTH_SHORT).show()
    }

    val itemCountHandler = ItemCountHandler()
    inner class ItemCountHandler {

        var itemCount = MutableLiveData<Int>(0)
        fun addItemToCount(){
            Log.i(TAG,"${getItemCount()}")
            if(itemCount.value?.compareTo(dataSource.value?.size!!)!! < 0 )itemCount.value = itemCount.value?.plus(1)
        }
        fun removeItemToCount(){
            Log.i(TAG,"${getItemCount()}")
            if(itemCount.value?.compareTo(0)!! > 0 ) itemCount.value = itemCount.value?.minus(1)
        }
        fun getItemCount(): Int? {
            return itemCount.value
        }
    }

    internal var callBackActionMode = object: ActionMode.Callback {


        override fun onCreateActionMode(actionMode: ActionMode?, menu: Menu?): Boolean {
            val inflater:MenuInflater? = actionMode?.menuInflater
            inflater?.inflate(R.menu.activity_main_action_mode, menu)
            Log.i(TAG,"onCreateActionMode...Start observing")
            //Log.i( TAG, "${this@ViewModelFragmentAlarm.numItemsSelected.value}" )


            itemCountHandler.itemCount.observeForever  {
                menu?.findItem(R.id.action_bar_itemsSelected)?.title = "${itemCountHandler.getItemCount()}"+ " items"

                Log.i(TAG, "${itemCountHandler.getItemCount()}"+ " items selected")
            }

            isActionModeOn.observeForever {
                isActionModeOn ->
                if(!isActionModeOn){
                    actionMode?.finish()
                }
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

                    return true
                }
                R.id.action_bar_selectAll->{
                    menuItem.isChecked = !menuItem.isChecked
                    var message:String? = null
                    when(menuItem.isChecked){
                        true -> message = "is checked"
                        false -> message = " is not checked"
                    }

                    Log.i(TAG, "The menu item  $message")

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
            this@ViewModelFragmentAlarm.itemCountHandler.itemCount.value = 0
            unSelectAllData()
        }

    }


    fun populateDataSource(){
        Log.i(TAG, "populateDataSource")
        if( dataSource.value != null ){
            dataSource.value?.apply {

                add(AlarmItem("09:10", "Daily| Alarm in 17 hours 3 minutes", false,false,true))
                add(AlarmItem("12:31", "Alarm in 20 hours 37 minutes", false,false,true))
                add(AlarmItem("12:32", "Alarm in 20 hours 38 minutes", false,false,true))
                add(AlarmItem("12:33", "Alarm in 20 hours 39 minutes", false,false,true))
                add(AlarmItem("12:34", "Alarm in 20 hours 40 minutes", false,false,true))
                add(AlarmItem("12:35", "Alarm in 20 hours 41 minutes", false,false,true))
                add(AlarmItem("12:36", "Alarm in 20 hours 42 minutes", false,false,true))
                add(AlarmItem("12:37", "Alarm in 20 hours 43 minutes", false,false,true))
                add(AlarmItem("12:38", "Alarm in 20 hours 44 minutes", false,false,true))
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

            return true
        }
        return false
    }

    private fun printDataSource(arrayList: ArrayList<AlarmItem>) {
        arrayList.forEach {
            Log.i(TAG, it.toString())
        }

    }


    private fun unSelectAllData() {
        this.unSelectAll.value = true
        dataSource.value?.forEach {
                alarmItem ->
            if(alarmItem.isSelected) {
                this.itemCountHandler.removeItemToCount()
                alarmItem.isSelected = false
            }
        }
        this.unSelectAll.value = false
    }

    private fun selectAllData() {
        this.selectAll.value = true
        dataSource.value?.forEach {
                alarmItem ->
            if(!alarmItem.isSelected){
                this.itemCountHandler.addItemToCount()
                alarmItem.isSelected = true
            }
        }
        this.selectAll.value = true
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun deleteSelectedAlarms():Boolean{
        Log.i(TAG, "Deleting selected items")
        val result = this.dataSource.value?.removeIf {
            alarmItem ->
            alarmItem.isSelected
        }
        this.alarmAdapter.notifyDataSetChanged()
        this.isActionModeOn.value = false

        if(result == null){
            Log.i(TAG, "There was a problem with the data source")
        }
        return result!!
        Log.i(TAG, "Items selected deleted")
    }



    companion object{
        const val TAG = "VmFragmentAlarmLogger"
    }
}