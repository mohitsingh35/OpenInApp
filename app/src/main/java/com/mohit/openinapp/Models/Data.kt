package com.mohit.openinapp.Models

data class Data(
    val favourite_links: List<Any>,
    val overall_url_chart: Any?,
    val recent_links: List<RecentLink>,
    val top_links: List<TopLink>
)