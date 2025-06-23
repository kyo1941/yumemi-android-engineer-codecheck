package jp.co.yumemi.android.code_check.domain.repository

import jp.co.yumemi.android.code_check.domain.model.Item

interface GitHubRepository {
    suspend fun searchRepositories(query: String): List<Item>
}