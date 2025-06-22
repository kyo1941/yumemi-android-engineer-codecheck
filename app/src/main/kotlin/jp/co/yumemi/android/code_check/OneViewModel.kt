/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import jp.co.yumemi.android.code_check.domain.repository.GitHubRepository
import kotlinx.parcelize.Parcelize
import java.util.Date
import java.util.concurrent.atomic.AtomicLong

/**
 * TwoFragment で使う
 */
class OneViewModel(
    private val repository: GitHubRepository
) : ViewModel() {

    private var lastSearchTime = AtomicLong(0)
    private val minSearchInterval = 1000L

    // 検索結果
    suspend fun searchResults(inputText: String): List<Item> {
        val currentTime = System.currentTimeMillis()
        val timeSinceLastSearch = currentTime - lastSearchTime.get()

        if (lastSearchTime.get() > 0 && timeSinceLastSearch < minSearchInterval) {
            kotlinx.coroutines.delay(minSearchInterval - timeSinceLastSearch)
        }

        lastSearchTime.set(System.currentTimeMillis())

        return repository.searchRepositories(inputText)
    }
}

@Parcelize
data class Item(
    val name: String,
    val ownerIconUrl: String,
    val language: String,
    val stargazersCount: Long,
    val watchersCount: Long,
    val forksCount: Long,
    val openIssuesCount: Long,
    val searchedAt: Date = Date()
) : Parcelable