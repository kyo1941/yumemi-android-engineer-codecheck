/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.yumemi.android.code_check.domain.repository.GitHubRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.parcelize.Parcelize
import java.util.Date
import javax.inject.Inject

/**
 * TwoFragment で使う
 */
@HiltViewModel
class OneViewModel @Inject constructor (
    private val repository: GitHubRepository
) : ViewModel() {

    private val searchMutex = Mutex()
    private var lastSearchTime: Long = 0
    private val minSearchInterval = 1000L

    // 検索結果
    suspend fun searchResults(inputText: String): List<Item> {
        searchMutex.withLock {
            val currentTime = System.currentTimeMillis()
            val timeSinceLastSearch = currentTime - lastSearchTime

            if (lastSearchTime > 0 && timeSinceLastSearch < minSearchInterval) {
                kotlinx.coroutines.delay(minSearchInterval - timeSinceLastSearch)
            }

            lastSearchTime = System.currentTimeMillis()
        }

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