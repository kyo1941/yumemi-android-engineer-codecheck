/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.yumemi.android.code_check.domain.model.Item
import jp.co.yumemi.android.code_check.domain.repository.GitHubRepository
import jp.co.yumemi.android.code_check.exceptions.ApiException
import jp.co.yumemi.android.code_check.exceptions.BadRequestException
import jp.co.yumemi.android.code_check.exceptions.ClientErrorException
import jp.co.yumemi.android.code_check.exceptions.NotFoundException
import jp.co.yumemi.android.code_check.exceptions.RateLimitException
import jp.co.yumemi.android.code_check.exceptions.ServerErrorException
import jp.co.yumemi.android.code_check.exceptions.UnauthorizedException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

/**
 * TwoFragment で使う
 */
@HiltViewModel
class OneViewModel @Inject constructor (
    private val repository: GitHubRepository
) : ViewModel() {
    private val _showErrorChannel = Channel<UserMessage>()
    val showErrorFlow = _showErrorChannel.receiveAsFlow()

    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading = _isLoading

    private val _navigateToRepository = Channel<Item>()
    val navigateToRepositoryFlow = _navigateToRepository.receiveAsFlow()

    private val _isEmptyInput = MutableStateFlow<Boolean>(false)
    val isEmptyInput = _isEmptyInput

    private val _searchText = MutableStateFlow<String>("")
    val searchText = _searchText

    private val searchMutex = Mutex()
    private var lastSearchTime: Long = 0
    private val minSearchInterval = 1000L

    suspend fun searchResults(inputText: String): List<Item> {
        searchMutex.withLock {
            _isLoading.value = true
            val currentTime = System.currentTimeMillis()
            val timeSinceLastSearch = currentTime - lastSearchTime

            if (lastSearchTime > 0 && timeSinceLastSearch < minSearchInterval) {
                kotlinx.coroutines.delay(minSearchInterval - timeSinceLastSearch)
            }

            lastSearchTime = System.currentTimeMillis()
        }

        return try {
            repository.searchRepositories(inputText)
        } catch (e: ApiException) {
            val snackbarMessage = when (e) {
                is BadRequestException ->
                    UserMessage.SnackBar(R.string.error_with_code, arrayOf(e.statusCode, R.string.error_bad_request))
                is RateLimitException -> {
                    val waitSeconds = ((e.resetTimeMs - System.currentTimeMillis()) / 1000).coerceAtLeast(1)
                    UserMessage.SnackBar(R.string.error_with_code, arrayOf(e.statusCode, R.string.error_rate_limit, waitSeconds))
                }
                is UnauthorizedException ->
                    UserMessage.SnackBar(R.string.error_with_code, arrayOf(e.statusCode, R.string.error_unauthorized))
                is NotFoundException ->
                    UserMessage.SnackBar(R.string.error_with_code, arrayOf(e.statusCode, R.string.error_not_found))
                is ClientErrorException -> {
                    UserMessage.SnackBar(R.string.error_with_code, arrayOf(e.statusCode, R.string.error_client))
                }
                is ServerErrorException -> {
                    UserMessage.SnackBar(R.string.error_with_code, arrayOf(e.statusCode, R.string.error_server))
                }
            }
            viewModelScope.launch {
                _showErrorChannel.send(snackbarMessage)
            }
            emptyList()
        } catch (e: Exception) {
            viewModelScope.launch {
                _showErrorChannel.send(UserMessage.SnackBar(R.string.error_unknown))
            }
            emptyList()
        } finally {
            _isLoading.value = false
        }
    }

    fun onSearchTextChanged(newText: String) {
        _searchText.value = newText
    }

    fun isValidInput(inputText: String): Boolean {
        return inputText.isNotBlank()
    }

    fun setEmptyInput(isEmpty: Boolean) {
        _isEmptyInput.value = isEmpty
    }

    fun onRepositorySelected(item: Item) {
        viewModelScope.launch {
            _navigateToRepository.send(item)
        }
    }
}
