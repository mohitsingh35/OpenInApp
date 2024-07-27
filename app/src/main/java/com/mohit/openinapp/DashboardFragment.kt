package com.mohit.openinapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.mohit.musicplayer.utils.ExtensionsUtil.isNull
import com.mohit.musicplayer.utils.ExtensionsUtil.parseData
import com.mohit.musicplayer.utils.ExtensionsUtil.setOnClickThrottleBounceListener
import com.mohit.openinapp.databinding.FragmentDashboardBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DashboardFragment : Fragment() {

    lateinit var binding: FragmentDashboardBinding
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var linksAdapter: LinksAdapter
    private lateinit var charts : LineChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDefaultViews()
        observeData()
        setUpViews()
    }
    private fun setupChart(chart: LineChart, data: LineData, color: Int, keysList: MutableList<Float>, valuesList: MutableList<Float>) {
        chart.description.isEnabled = false
        chart.setDrawGridBackground(false)
        chart.setTouchEnabled(false)
        chart.isDragEnabled = false
        chart.setScaleEnabled(true)
        chart.setPinchZoom(false)
        chart.isScaleXEnabled = true
        chart.isScaleYEnabled = true
        chart.setBackgroundColor(color)
        chart.data = data
        val l = chart.legend
        l.isEnabled = false
        val xAxis = chart.xAxis
        xAxis.isEnabled = true
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = XAxisValueFormatter(keysList)
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(true)
        xAxis.setDrawGridLines(true)
        xAxis.gridColor = resources.getColor(R.color.grid)
        xAxis.gridLineWidth = 0.5f
        xAxis.setDrawLabels(true)
        xAxis.granularity = 1f
        xAxis.axisMinimum = 0f
        xAxis.axisMaximum = (keysList.size - 1).toFloat()
        val leftAxis = chart.axisLeft
        leftAxis.isEnabled = true
        leftAxis.valueFormatter = YAxisValueFormatter()
        leftAxis.setDrawGridLines(false)
        leftAxis.setDrawGridLines(true)
        leftAxis.gridColor = resources.getColor(R.color.grid)
        leftAxis.gridLineWidth = 0.5f
        leftAxis.setDrawAxisLine(true)
        leftAxis.setDrawLabels(true)
        leftAxis.granularity = 2f
        val minY = valuesList.minOrNull() ?: 0f
        val maxY = valuesList.maxOrNull() ?: 1f
        leftAxis.axisMinimum = minY
        leftAxis.axisMaximum = maxY + 1f
        chart.axisRight.isEnabled = false
        chart.invalidate()
        chart.animateX(1000)
        chart.invalidate()
    }


    private fun setChart() {
        charts=binding.chart1
        viewModel.data.observe(viewLifecycleOwner) { data ->
            val values : MutableList<Entry> = mutableListOf()
            val chartData=parseData(data.data.overall_url_chart.toString())
            val keysList:MutableList<Float> = mutableListOf()
            val valuesList:MutableList<Float> = mutableListOf()

            if (!chartData.isNull) {
                for (i in 0..23) {
                    val key = String.format("%02d:00", i)
                    val value = chartData?.get(key) ?: 0.0
                    keysList.add(i.toFloat())
                    valuesList.add(value.toFloat())
                    values.add(Entry(i.toFloat(), value.toFloat()))
                }
                Log.d("DashboardFragment", "setChart: ${values}")
                val set1 = LineDataSet(values, "DataSet 1")
                set1.lineWidth = 1.75f
                set1.color = resources.getColor(R.color.blue)
                set1.setDrawValues(false)
                set1.setDrawCircles(false)
                set1.setDrawFilled(false)
                val fillDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.gradient_fill)
                set1.fillDrawable = fillDrawable
                setupChart(charts, LineData(set1), resources.getColor(R.color.white), keysList, valuesList )
            }
        }
    }


    private fun observeData(){
        viewModel.data.observe(viewLifecycleOwner) { data ->
            Log.d("DashboardFragment", "onCreate: ${data}")
            setData(data!!)
            setChart()
        }
    }
    private fun setUpViews(){


        viewModel.greeting.observe(viewLifecycleOwner, Observer {
            binding.greeting.text=it
        })

        viewModel.selectedTab.observe(viewLifecycleOwner) { selectedTab ->
            when (selectedTab) {
                TabState.TopLinks -> {
                    setSelectedTab(binding.toplinks,binding.recentLinks)
                }
                TabState.RecentLinks -> {
                    setSelectedTab(binding.recentLinks,binding.toplinks)
                }
            }
        }

        binding.toplinks.setOnClickThrottleBounceListener {
            viewModel.setTabSelected(TabState.TopLinks)
        }

        binding.recentLinks.setOnClickThrottleBounceListener {
            viewModel.setTabSelected(TabState.RecentLinks)
        }

        binding.viewLinks.setOnClickThrottleBounceListener {
            viewModel.toggleViewAllLinks()
        }

    }

    private fun setSelectedTab(selectedTab:TextView,unselectedTab:TextView){
        selectedTab.background=resources.getDrawable(R.drawable.selected_tab)
        selectedTab.setTextColor(resources.getColor(R.color.white))
        unselectedTab.background=null
        unselectedTab.setTextColor(resources.getColor(R.color.text_secondary))
    }

    private fun setData(linksData: LinksData){
        setAnalyticsData(linksData)
        viewModel.selectedTab.observe(viewLifecycleOwner) { selectedTab ->
            when (selectedTab) {
                TabState.TopLinks -> {

                    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                        viewModel.viewAllLinksState.observe(viewLifecycleOwner) { state ->
                            when (state) {
                                ViewAllLinksState.Collapsed -> {
                                    binding.viewLinksTxt.text = "View all Links"
                                    val links = linksData.data?.top_links?.let { topLinks ->
                                        if (topLinks.size > 3) {
                                            topLinks.subList(0, 3)
                                        } else {
                                            topLinks
                                        }
                                    } ?: emptyList()
                                    setRecyclerView(links)
                                }
                                ViewAllLinksState.Expanded -> {
                                    binding.viewLinksTxt.text = "View less"
                                    val links = linksData.data?.top_links ?: emptyList()
                                    setRecyclerView(links)
                                }
                            }
                        }
                    }



                }
                TabState.RecentLinks -> {
                    val recentLinks = linksData.data.recent_links
                    val topLinks:MutableList<TopLink> = mutableListOf()
                    for (recentLink in recentLinks){
                        topLinks.add(TopLink(
                            original_image = recentLink.original_image,
                            title = recentLink.title,
                            created_at = recentLink.created_at,
                            total_clicks = recentLink.total_clicks,
                            web_link = recentLink.web_link,
                            domain_id = recentLink.domain_id,
                            url_id = recentLink.url_id,
                            url_prefix = recentLink.url_prefix,
                            url_suffix = recentLink.url_suffix,
                            smart_link = recentLink.smart_link,
                            times_ago = recentLink.times_ago,
                            is_favourite = recentLink.is_favourite,
                            thumbnail = recentLink.thumbnail,
                            app = recentLink.app,
                        ))
                    }

                    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                        viewModel.viewAllLinksState.observe(viewLifecycleOwner) { state ->
                            when (state) {
                                ViewAllLinksState.Collapsed -> {
                                    binding.viewLinksTxt.text = "View all Links"
                                    val links = topLinks.let { topLinks ->
                                        if (topLinks.size > 3) {
                                            topLinks.subList(0, 3)
                                        } else {
                                            topLinks
                                        }
                                    }
                                    setRecyclerView(links)
                                }
                                ViewAllLinksState.Expanded -> {
                                    binding.viewLinksTxt.text = "View less"
                                    setRecyclerView(topLinks)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setRecyclerView(list: List<TopLink>){
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        linksAdapter = LinksAdapter(list, requireContext())
        recyclerView.adapter = linksAdapter
    }

    private fun setAnalyticsData(linksData: LinksData){
        binding.clicks.count.text=linksData.today_clicks.toString()
        binding.toplocation.count.text=linksData.top_location
        binding.sources.count.text=linksData.top_source
    }

    private fun setDefaultViews(){
        binding.clicks.ic.setImageResource(R.drawable.clicks)
        binding.clicks.count.text="..."
        binding.clicks.text.text="Today's clicks"

        binding.toplocation.ic.setImageResource(R.drawable.toplocations)
        binding.toplocation.count.text="..."
        binding.toplocation.text.text="Top Location"

        binding.sources.ic.setImageResource(R.drawable.search)
        binding.sources.count.text="..."
        binding.sources.text.text="Top sources"
    }



}