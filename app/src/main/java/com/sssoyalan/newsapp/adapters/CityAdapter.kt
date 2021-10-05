package com.sssoyalan.newsapp.adapters

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.sssoyalan.newsapp.models.city.CityResponseItem
import com.sssoyalan.newsapp.databinding.ItemCityBinding
import com.sssoyalan.newsapp.ui.activities.MainActivity
import com.sssoyalan.newsapp.ui.fragments.WeatherFragment
import java.util.*
import kotlin.collections.ArrayList

class CityAdapter(private var cityList: List<CityResponseItem>, private val weatherFragment: WeatherFragment, private val activity: MainActivity): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var cityFilterList : List<CityResponseItem> = mutableListOf()

    lateinit var mContext: Context

    class CityHolder(var viewBinding: ItemCityBinding) :
        RecyclerView.ViewHolder(viewBinding.root)

    init {
        cityFilterList = cityList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ItemCityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val sch = CityHolder(binding)
        mContext = parent.context
        return sch
    }

    override fun getItemCount(): Int {
        return cityFilterList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val cityHolder = holder as CityHolder
        cityHolder.viewBinding.selectCountryContainer.setBackgroundColor(Color.TRANSPARENT)

        cityHolder.viewBinding.selectCountryText.setTextColor(Color.WHITE)
        cityHolder.viewBinding.selectCountryText.text = cityFilterList[position].name

        holder.itemView.setOnClickListener {
            weatherFragment.showProgressBar()
            val sharedPreferences = mContext.getSharedPreferences(mContext.packageName,android.content.Context.MODE_PRIVATE)
            sharedPreferences.edit().putString("city", cityFilterList[position].name).apply()
            activity.goResfreshWeather(null,null,cityFilterList[position].name)
            weatherFragment.hideKeyboard(holder.itemView)
            weatherFragment.hideCitydialog()
            weatherFragment.goResfresh(null,null,cityFilterList[position].name)
        }
    }


    @OptIn(ExperimentalStdlibApi::class)
     fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    cityFilterList = cityList
                } else {
                    val resultList = ArrayList<CityResponseItem>()
                    for (row in cityList) {
                        if (row.name.toString().lowercase(Locale.ROOT).contains(charSearch.lowercase(Locale.ROOT))) {
                            resultList.add(row)
                        }
                    }
                    cityFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = cityFilterList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                cityFilterList = results?.values as ArrayList<CityResponseItem>
                notifyDataSetChanged()
            }

        }
    }

}