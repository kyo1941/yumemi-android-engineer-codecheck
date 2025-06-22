package jp.co.yumemi.android.code_check.domain.repository

import jp.co.yumemi.android.code_check.Item

interface GitHubRepository {
    suspend fun searchRepositories(query: String):List<Item>
}