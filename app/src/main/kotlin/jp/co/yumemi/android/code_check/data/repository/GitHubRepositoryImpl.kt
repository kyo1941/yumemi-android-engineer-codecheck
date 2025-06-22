package jp.co.yumemi.android.code_check.data.repository

import android.content.Context
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import jp.co.yumemi.android.code_check.Item
import jp.co.yumemi.android.code_check.R
import jp.co.yumemi.android.code_check.data.api.GitHubApiClient
import jp.co.yumemi.android.code_check.domain.repository.GitHubRepository
import org.json.JSONObject
import java.util.Date

class GitHubRepositoryImpl(
    private val context: Context
): GitHubRepository {
    override suspend fun searchRepositories(query: String): List<Item> {
        return GitHubApiClient.client.get("https://api.github.com/search/repositories") {
            header("Accept", "application/vnd.github.v3+json")
            parameter("q", query)
        }.let { response ->
            val jsonBody = JSONObject(response.body<String>())

            val jsonItems = jsonBody.optJSONArray("items") ?: org.json.JSONArray()

            val items = mutableListOf<Item>()

            val searchedAt = Date()

            for (i in 0 until jsonItems.length()) {
                val jsonItem = jsonItems.optJSONObject(i) ?: continue
                val name = jsonItem.optString("full_name")
                val ownerIconUrl = jsonItem.optJSONObject("owner")?.optString("avatar_url") ?: ""
                val language = jsonItem.optString("language")
                val stargazersCount = jsonItem.optLong("stargazers_count")
                val watchersCount = jsonItem.optLong("watchers_count")
                val forksCount = jsonItem.optLong("forks_count")
                val openIssuesCount = jsonItem.optLong("open_issues_count")

                items.add(
                    Item(
                        name = name,
                        ownerIconUrl = ownerIconUrl,
                        language = context.getString(R.string.written_language, language),
                        stargazersCount = stargazersCount,
                        watchersCount = watchersCount,
                        forksCount = forksCount,
                        openIssuesCount = openIssuesCount,
                        searchedAt = searchedAt
                    )
                )
            }

            items.toList()
        }
    }
}