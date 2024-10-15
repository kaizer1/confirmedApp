package app.confirmer.viewlistapps

import android.content.Context
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import app.confirmer.R
import okhttp3.internal.notify

class CustomAdapter(contex: Context, retOurData : ArrayList<pInfo>) : BaseAdapter() {

    val cont  = contex
    val appsOur = retOurData
    val inflateMain: LayoutInflater = cont.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val prefs = PreferenceManager.getDefaultSharedPreferences  ( cont )


    override fun getCount(): Int {
        return  appsOur.size
    }

    override fun getItem(p0: Int): pInfo {
       return appsOur[p0]
    }

    override fun getItemId(p0: Int): Long  {
       return p0.toLong()
    }


    private fun addElementToFilter(nameA : String){

        if(prefs.contains("filter")){
           val mainStringFilter = prefs.getString("filter", "")

            if (!mainStringFilter!!.contains(nameA)){
                prefs.edit().putString("filter", mainStringFilter.toString() + nameA).apply()
            }

                //  println("my string filter ${prefs.getString("filter", "")} ")

        }else {
          prefs.edit().putString("filter", nameA).apply()
        }
    }

       private fun removeElementToFilter(nameA : String){

        if(prefs.contains("filter")){

            val mainStringFilter = prefs.getString("filter", "").toString()

            if (mainStringFilter!!.contains(nameA)){
                prefs.edit().putString("filter", mainStringFilter.replace(nameA, "")).apply()
            }

        }
    }




    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {

         var view = p1
        if(view == null){
            view = inflateMain.inflate(R.layout.items_ap, p2, false)
        }

        val app = getApps(p0)

        view!!.findViewById<TextView>(R.id.app_name).text = app.appName
        view.findViewById<ImageView>(R.id.app_icon).setImageDrawable(app.iconD)

        val cjec =  view.findViewById<CheckBox>(R.id.check_box)

            cjec.isChecked = app.checkb


        cjec.setOnClickListener {

            // println("  my setCJ check press = ${getApps(p0).appName} ")
            if(cjec.isChecked){
              //  println(" check add ! ")
                addElementToFilter(getApps(p0).appName)

                appsOur.get(p0).checkb = true
                //cjec.isChecked = false
            }else {
                //println("check removed ")
                removeElementToFilter(getApps(p0).appName)
                //cjec.isChecked = true
                appsOur.get(p0).checkb = false
            }

            //cjec.notify()

            synchronized(this){
                 // notifyDataSetChanged()
            //notify()
              }
            }


        cjec.setOnCheckedChangeListener { p2, p3 ->

        }





            // notifyDataSetChanged()
        return view
    }




     private fun getApps(position : Int) : pInfo{
      return getItem(position)
    }

}