package jp.co.yumemi.android.code_check.data.repository

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import jp.co.yumemi.android.code_check.Item
import jp.co.yumemi.android.code_check.data.api.GitHubApiClient
import jp.co.yumemi.android.code_check.domain.repository.GitHubRepository
import jp.co.yumemi.android.code_check.exceptions.BadRequestException
import jp.co.yumemi.android.code_check.exceptions.ClientErrorException
import jp.co.yumemi.android.code_check.exceptions.NotFoundException
import jp.co.yumemi.android.code_check.exceptions.RateLimitException
import jp.co.yumemi.android.code_check.exceptions.ServerErrorException
import jp.co.yumemi.android.code_check.exceptions.UnauthorizedException
import org.json.JSONObject
import java.util.Date
import kotlin.text.toIntOrNull
import kotlin.text.toLongOrNull

class GitHubRepositoryImpl(): GitHubRepository {
    companion object {
        private val DEFAULT_WAIT_TIME_MS = 60 * 1000L
        private val MIN_WAIT_TIME_MS = 1000L
    }

    override suspend fun searchRepositories(query: String): List<Item> {
        val response = GitHubApiClient.client.get("https://api.github.com/search/repositories") {
            header("Accept", "application/vnd.github.v3+json")
            parameter("q", query)
        }


        when (response.status.value) {
            200 -> {
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
                            language = language,
                            stargazersCount = stargazersCount,
                            watchersCount = watchersCount,
                            forksCount = forksCount,
                            openIssuesCount = openIssuesCount,
                            searchedAt = searchedAt
                        )
                    )
                }

                return items.toList()
            }

            400 -> throw BadRequestException()

            401 -> throw UnauthorizedException()

            403 -> handleRateLimitError(response)

            404 -> throw NotFoundException()

            in 400..499 -> throw ClientErrorException(response.status.value, response.status.description)

            in 500..599 -> throw ServerErrorException(response.status.value, response.status.description)

            else -> throw Exception()
        }

        return emptyList()
    }

    private fun handleRateLimitError(response: HttpResponse) {
        val retryAfter = response.headers["Retry-After"]?.toLongOrNull()
        val rateReset = response.headers["X-RateLimit-Reset"]?.toLongOrNull()
        val remaining = response.headers["X-RateLimit-Remaining"]?.toIntOrNull() ?: 0

        val waitTimeMs = when {
            retryAfter != null -> retryAfter * 1000

            remaining == 0 && rateReset != null -> {
                val currentTimeSeconds = System.currentTimeMillis() / 1000
                (rateReset - currentTimeSeconds) * 1000
            }

            else -> DEFAULT_WAIT_TIME_MS
        }.coerceAtLeast(MIN_WAIT_TIME_MS)

        val resetTime = System.currentTimeMillis() + waitTimeMs

        throw RateLimitException(resetTimeMs = resetTime)
    }
}