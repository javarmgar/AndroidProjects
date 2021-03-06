package eu.example.userinterfaces

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.widget.SearchView

class ConstraintWidgetsActivity : AppCompatActivity() {
    lateinit var seekBar: SeekBar
    lateinit var textView: TextView
    lateinit var ratingBar: RatingBar
    lateinit var imageView: ImageView
    lateinit var progressBar:ProgressBar
    lateinit var refActivity: ConstraintWidgetsActivity
    lateinit var  calendarView: CalendarView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_constraint_widgets)
        seekBar = findViewById<SeekBar>(R.id.seekbar1)

        /*
        Implementing some behaviour on image profile
        Let's say the image is loaded by some third party service (REST, http library like picasso,...)
        if the resource is not loaded then it will show profile picture from local resources

        * */
        refActivity = this

        imageView = findViewById<ImageView>(R.id.imageView)
        progressBar = findViewById<ProgressBar>(R.id.progressBar2)
        loadImage()

        textView = findViewById<TextView>(R.id.act_const_widg_textView)

        seekBar.setOnSeekBarChangeListener(
            object:SeekBar.OnSeekBarChangeListener{
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    //This method is triggered when a user or some component change the seekbar progress value
                    //that's why there's a  boolean telling if it was the user or not
                    if(fromUser){
                        textView.text = seekBar?.progress.toString()
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    Log.i("seekBar:onStartTrackingTouch", "Someone started dragging")
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    Log.i("seekBar:onStartTrackingTouch", "Someones stopped dragging")
                }

            }
        )

        /*Rating bar is  a subtype of progressbar and seekbar
        but it is measured by stars
        main attributes in xml
        numStars: number of starts showed
        stepSize: measure unit to jump from one measure to another
        isIndicator: Boolean (false by defect)
        rating: the current rating ( can be changed by the user if indicator allows it)
        * */


        ratingBar = findViewById<RatingBar>(R.id.ratingBar2)
        ratingBar.setOnRatingBarChangeListener {
                ratingBar, rating, fromUser ->
            val valueText:String = when(rating.toInt()){
                1 -> "Bad"
                2 -> "Less than average"
                3 -> "Just Average"
                4 -> "Good"
                else -> "Perfect"
            }
            Log.i("ratingBar", valueText)
            Toast.makeText(this, valueText, Toast.LENGTH_SHORT).show()
        }

        /*
        Calendar View:
        view of a calendar that allows setting date within a date's range
        also it implements a setOnDateChangeListener: Every time the
        xml attrs
            -> maxDate: maximum date showed
            -> minDate: minimum date showed
            -> textDateAppereance
            ->weekDayAppereance
        * */
        calendarView = findViewById<CalendarView>(R.id.calendarView)
        val textViewCalendar = findViewById<TextView>(R.id.TextViewCalendar)
        calendarView.setOnDateChangeListener {
                view, year, month, dayOfMonth ->
                textViewCalendar.text = this.resources.getString(R.string.date_label) + "$dayOfMonth/$month/$year"

        }
        setSearchView()

    }


    private fun loadImage() {
        /*
        * here it should go the code from the rest request
        * let's assume it was  unsuccessful
        * */
        object:Thread(){
            override fun run() {
                super.run()
                Thread.sleep(2000) // simulating the time request
                refActivity.runOnUiThread {
                    progressBar.visibility = View.INVISIBLE
                    imageView.visibility = View.VISIBLE
                }

            }
        }.start()




    }

    private fun setSearchView() {

        /***
         * SearchView: It's a view which  receives queries those queries become into request
         * which are handled by a search provider
         *
         * It has three listeners to handle its events which are:
         * onCloseListener: It has method onClose()
         * onQueryTextListener: it has three methods, to execute the query (an enter), onQueryTextChange
         * onSuggestionListener: Every time a query is executed it generates a set of suggestion each suggestion can be click or selected
         *
         */

        val names = arrayOf("Aaran", "Aaren", "Aarez", "Aarman", "Aaron")

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, names )
        val listView = findViewById<ListView>(R.id.ListView)
        listView.adapter = adapter

        val searchView = findViewById<SearchView>(R.id.searchView)

        searchView.setOnQueryTextFocusChangeListener { v, hasFocus ->
            if(!hasFocus){
                searchView.setQuery("",false)
                adapter.filter.filter("")
            }
        }
        searchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    searchView.clearFocus()
                    if(names.contains(query)){
                        Toast.makeText( applicationContext, "Found:$query", Toast.LENGTH_SHORT).show()
                    }else{
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if(names.contains(newText)){
                        adapter.filter.filter(newText)
                    }
                    return true
                }

            }
        )
    }

}
