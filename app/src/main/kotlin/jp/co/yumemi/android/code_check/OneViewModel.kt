/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import jp.co.yumemi.android.code_check.domain.repository.GitHubRepository
import kotlinx.parcelize.Parcelize
import java.util.Date

/**
 * TwoFragment で使う
 */
class OneViewModel(
    private val repository: GitHubRepository
) : ViewModel() {

    // 検索結果
    suspend fun searchResults(inputText: String): List<Item> {
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