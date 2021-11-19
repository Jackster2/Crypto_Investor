package com.example.cryptoinvestor.view.fragments
// fået inspiration fra https://medium.com/@yilmazvolkan/kotlinlinecharts-c2a730226ff1 til hvordan,
// Man sætter en chart op i kotlin.

import CustomMarker
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.cryptoinvestor.databinding.FragmentInfoCryptoBinding
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.android.synthetic.main.fragment_info_crypto.view.*


import com.example.cryptoinvestor.di.ServiceLocator.infoCryptoViewModel
import com.example.cryptoinvestor.utils.FLOAT_FORMATTER
import com.example.cryptoinvestor.utils.PRICE_FORMATTER
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.ArrayList

import androidx.core.content.ContextCompat
import com.example.cryptoinvestor.R


class InfoCryptoFragment : Fragment() {

    private lateinit var binding: FragmentInfoCryptoBinding
    private val viewModel by lazy { infoCryptoViewModel }
    var id : String = ""

    companion object{
        fun newInstance() = InfoCryptoFragment()
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInfoCryptoBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = arguments

        if (bundle != null) {
            id = bundle.getString("id").toString()
            viewModel.refreshAsset(id)
            viewModel.getAssetHistory(id)

            viewModel.asset.observe(viewLifecycleOwner, {asset ->
                if(asset.isSuccessful){
                    asset.body()?.let {
                        var imageUrl = "https://static.coincap.io/assets/icons/"
                        view.info_CurrencyName.text = it.name
                        view.info_CurrencyInitials.text = it.symbol
                        view.info_CurrencyPrice.text = PRICE_FORMATTER.format(it.price).toString()
                        var changeTxt = FLOAT_FORMATTER.format(it.change24Hr).toString()
                        println(asset.body()?.toString())
                        view.info_changePr24Hr.text = changeTxt
                        Picasso.get().load(imageUrl+it.symbol.lowercase()+"@2x.png").into(view.info_CurrencyImage)
                        if (changeTxt.contains("-")){
                            Log.w("Negativ", changeTxt)
                            view.info_changePr24Hr.setTextColor(Color.RED)
                            view.procent.setTextColor(Color.RED)
                        }else{
                            Log.w("Positiv", changeTxt)
                            view.info_changePr24Hr.setTextColor(Color.GREEN)
                            view.procent.setTextColor(Color.GREEN)
                        }

                    }
                }
            })

            viewModel.assetHistory.observe(viewLifecycleOwner, { asset ->
                if (asset.isSuccessful){
                    asset.body().let {
                        if (it != null) {
                            println("Det her er kroppen " + it.map { it.priceUsd })
                            val entries = ArrayList<Entry>()
                            /*
                            mapper assetHistoryDto responsets data til to lister der bruges til at lave entries som senere bliver brugt i setLineChartData()
                             */
                            val assetUsd: List<Float> = it.map { it.priceUsd }
                            val assetTime: List<Float> = it.map { it.time }
                            val assetTimeStr = mutableListOf<String>()
                            println("Det her er formateret tid " + it.map { Date(it.time.toLong()) })
                            //it.forEach(entries.add(Entry(assetUsd,assetTime)))
                            for (asset in it.indices){
                                entries.add(Entry(assetTime[asset],assetUsd[asset]))

                                /*
                                Nedestående er forsøg på at ændre x label med en liste af strings
                                 */
                                assetTimeStr.add("HEJ")
                               // assetTimeStr.add(Date(assetTime[asset].toLong()).toString())
                            }
                            setLineChartData(view.lineChart, entries,assetTime, assetTimeStr)

                        }
                    }
                }else{
                    println("IT IS NULL!!!!")
                }
            })


        }


    }

    override fun onDestroy() {
        super.onDestroy()
    }

    /*
    LineChart Design og opsætning
     */

    fun setLineChartData(
        lineChart: LineChart,
        entries: ArrayList<Entry>,
        assetTime: List<Float>,
        assetTimeStr: MutableList<String>
    ) {


        // x-værdier
        // datasættet for line
        val lineDataSet = LineDataSet(entries, "first")

        // DrawValue sættes false da en corresponding value i en chart vil i længden blive rodet
        // DrawFilled sættes til true for at fylde ud under grafen
        // linewidth kan vi ændre tykkelsen og fylde dens farve

        lineDataSet.setDrawValues(true)
        lineDataSet.setDrawFilled(true)
        lineDataSet.lineWidth = 3f


        val drawable = context?.let { ContextCompat.getDrawable(it,R.drawable.linechart_fill_color) }
        val color = context?.let { ContextCompat.getColor(it, R.color.PrimaryColor) }
        lineDataSet.fillDrawable = drawable

        if (color != null) {
            lineDataSet.color = color
        }

        lineDataSet.setDrawCircles(false)

        //Her sættes en rotation angle til x-aksen (måske udkommenteres)
        lineChart.xAxis.labelRotationAngle = 0f


        // hver sætter vi vores datasæt ind i en line chart til at tegnes

        lineChart.data = LineData(lineDataSet)
        lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(assetTimeStr)
        lineChart.xAxis.setDrawGridLines(false)
        lineChart.axisRight.setDrawGridLines(false)

        //removing extra y-axis
        lineChart.axisLeft.isEnabled = false

        // Dette kan bruges, hvis der ikke kommer noget data ind
        lineChart.description.text = "x-axis"
        lineChart.setNoDataText("No for x-axis yet!")

        // denne burde gerne loade dataen ind, undervejs, det virker kun for API 11
        lineChart.animateX(1800, Easing.EaseInExpo)


        //TODO: dette vil være en dot du kan hive rundt på charten for at se specifik værdi.
        // hvis vi gerne vil se hvordan, kan vi bruge https://medium.com/@yilmazvolkan/kotlinlinecharts-c2a730226ff1
        val markerView = context?.let { CustomMarker(it, R.layout.chart) }
        lineChart.marker = markerView
    }
}

