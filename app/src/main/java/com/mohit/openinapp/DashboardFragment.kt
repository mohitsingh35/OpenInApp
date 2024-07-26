package com.mohit.openinapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mohit.musicplayer.utils.ExtensionsUtil.setOnClickSingleTimeBounceListener
import com.mohit.musicplayer.utils.ExtensionsUtil.setOnClickThrottleBounceListener
import com.mohit.musicplayer.utils.ExtensionsUtil.toast
import com.mohit.openinapp.databinding.FragmentDashboardBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DashboardFragment : Fragment() {

    lateinit var binding: FragmentDashboardBinding
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var linksAdapter: LinksAdapter

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
    private fun observeData(){
        viewModel.data.observe(viewLifecycleOwner) { data ->
            Log.d("DashboardFragment", "onCreate: ${data}")
            setData(data!!)
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