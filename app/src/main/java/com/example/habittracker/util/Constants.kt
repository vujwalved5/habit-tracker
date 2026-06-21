package com.example.habittracker.util

import com.example.habittracker.BuildConfig

object Constants {
    // H3: URL is now sourced from BuildConfig (injected from local.properties)
    // instead of being hardcoded in source code.
    val CLOUDFLARE_BASE_URL: String = BuildConfig.CLOUDFLARE_BASE_URL
    const val STREAK_LOOKBACK_DAYS = 400
}
