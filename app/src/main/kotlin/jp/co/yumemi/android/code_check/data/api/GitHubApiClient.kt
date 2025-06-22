package jp.co.yumemi.android.code_check.data.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android

object GitHubApiClient {
    val client = HttpClient(Android) {
        expectSuccess = false
    }
}