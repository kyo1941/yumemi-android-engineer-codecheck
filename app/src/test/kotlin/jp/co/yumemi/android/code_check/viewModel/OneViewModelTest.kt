package jp.co.yumemi.android.code_check.viewModel

import app.cash.turbine.test
import jp.co.yumemi.android.code_check.R
import jp.co.yumemi.android.code_check.common.UserMessage
import jp.co.yumemi.android.code_check.domain.model.Item
import jp.co.yumemi.android.code_check.domain.repository.GitHubRepository
import jp.co.yumemi.android.code_check.exceptions.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.rules.TestWatcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.*
import java.util.Date

@ExperimentalCoroutinesApi
class OneViewModelTest {

    private val testScheduler = TestCoroutineScheduler()

    @get:Rule
    val mainDispatcherRule = TestDispatcherRule(StandardTestDispatcher(testScheduler))

    @Mock
    private lateinit var gitHubRepository: GitHubRepository

    private lateinit var viewModel: OneViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        viewModel = OneViewModel(gitHubRepository, clock = { testScheduler.currentTime })
    }

    @Test
    fun searchResults_success_updatesItems() = runTest {
        val testItems = listOf(
            Item(
                name = "repo1",
                ownerIconUrl = "url1",
                language = "Kotlin",
                stargazersCount = 100,
                watchersCount = 50,
                forksCount = 20,
                openIssuesCount = 5,
                searchedAt = Date()
            )
        )
        whenever(gitHubRepository.searchRepositories("kotlin")).thenReturn(testItems)

        viewModel.searchResults("kotlin")
        advanceUntilIdle()

        assertEquals(testItems, viewModel.items.first())
        assertFalse(viewModel.isLoading.first())
    }

    /**
     * エラーハンドリングの共通テストロジック
     * @param query 検索クエリ
     * @param thrownException searchRepositoriesがスローする例外
     * @param assertBlock SnackBarメッセージに対する追加のアサーション
     */
    private fun testSearchResultsErrorHandler(
        query: String,
        thrownException: Exception,
        assertBlock: (snackbar: UserMessage.SnackBar) -> Unit = {}
    ) = runTest {
        whenever(gitHubRepository.searchRepositories(query)).thenAnswer { throw thrownException }

        val job = launch {
            viewModel.showErrorFlow.collect { message ->
                assertTrue(message is UserMessage.SnackBar)
                val snackbar = message as UserMessage.SnackBar
                assertBlock(snackbar)
            }
        }

        viewModel.searchResults(query)
        advanceUntilIdle()

        assertTrue(viewModel.items.first().isEmpty())
        assertFalse(viewModel.isLoading.first())
        job.cancel()
    }

    @Test
    fun searchResults_handlesBadRequestException() = testSearchResultsErrorHandler(
        query = "invalid",
        thrownException = BadRequestException(400)
    ) { snackbar ->
        assertEquals(R.string.error_with_code, snackbar.messageResId)
        assertEquals(400, snackbar.formatArgs[0])
        assertEquals(R.string.error_bad_request, snackbar.formatArgs[1])
    }

    @Test
    fun searchResults_handlesRateLimitException() = testSearchResultsErrorHandler(
        query = "limit",
        thrownException = RateLimitException(403, System.currentTimeMillis() + 5000)
    ) { snackbar ->
        assertEquals(R.string.error_with_code, snackbar.messageResId)
        assertEquals(403, snackbar.formatArgs[0])
        assertEquals(R.string.error_rate_limit, snackbar.formatArgs[1])
        assertTrue((snackbar.formatArgs[2] as Long) >= 1)
    }

    @Test
    fun searchResults_handlesUnauthorizedException() = testSearchResultsErrorHandler(
        query = "unauthorized",
        thrownException = RateLimitException(401, System.currentTimeMillis() + 5000)
    ) { snackbar ->
        assertEquals(R.string.error_with_code, snackbar.messageResId)
        assertEquals(401, snackbar.formatArgs[0])
        assertEquals(R.string.error_rate_limit, snackbar.formatArgs[1])
        assertTrue((snackbar.formatArgs[2] as Long) >= 1)
    }

    @Test
    fun searchResults_handlesNotFoundException() = testSearchResultsErrorHandler(
    query = "notfound",
    thrownException = RateLimitException(404, System.currentTimeMillis() + 5000)
    ) { snackbar ->
        assertEquals(R.string.error_with_code, snackbar.messageResId)
        assertEquals(404, snackbar.formatArgs[0])
        assertEquals(R.string.error_rate_limit, snackbar.formatArgs[1])
        assertTrue((snackbar.formatArgs[2] as Long) >= 1)
    }

    @Test
    fun searchResults_handlesClientErrorException() = testSearchResultsErrorHandler(
        query = "clienterror",
        thrownException = ClientErrorException(422, "Unprocessable Entity")
    ) { snackbar ->
        assertEquals(R.string.error_with_code, snackbar.messageResId)
        assertEquals(422, snackbar.formatArgs[0])
        assertEquals(R.string.error_client, snackbar.formatArgs[1])
    }

    @Test
    fun searchResults_handlesServerErrorException() = testSearchResultsErrorHandler(
        query = "servererror",
        thrownException = ServerErrorException(500, "Internal Server Error")
    ) { snackbar ->
        assertEquals(R.string.error_with_code, snackbar.messageResId)
        assertEquals(500, snackbar.formatArgs[0])
        assertEquals(R.string.error_server, snackbar.formatArgs[1])
    }

    @Test
    fun searchResults_handlesGenericException() = testSearchResultsErrorHandler(
        query = "genericerror",
        thrownException = Exception("Unknown error")
    ) { snackbar ->
        assertEquals(R.string.error_unknown, snackbar.messageResId)
    }

    @Test
    fun searchResults_respectsMinSearchInterval() = runTest {
        val testItems = listOf(
            Item(
                name = "repo1",
                ownerIconUrl = "url1",
                language = "Kotlin",
                stargazersCount = 100,
                watchersCount = 50,
                forksCount = 20,
                openIssuesCount = 5,
                searchedAt = Date()
            )
        )
        whenever(gitHubRepository.searchRepositories(any())).thenReturn(testItems)

        testScheduler.advanceTimeBy(1L)

        viewModel.searchResults("first")
        advanceUntilIdle()
        verify(gitHubRepository, times(1)).searchRepositories("first")

        testScheduler.advanceTimeBy(500)

        viewModel.searchResults("second")
        advanceUntilIdle()

        assertTrue(testScheduler.currentTime >= 1000)
        verify(gitHubRepository, times(1)).searchRepositories("second")
    }

    @Test
    fun onSearchTextChanged_updatesSearchText() = runTest {
        val newText = "new query"
        viewModel.onSearchTextChanged(newText)
        assertEquals(newText, viewModel.searchText.first())
    }

    @Test
    fun onSearchTextChanged_clearsEmptyInputErrorWhenTextIsNotEmpty() = runTest {
        viewModel.setEmptyInput(true) // Set to true initially
        viewModel.onSearchTextChanged("some text")
        assertFalse(viewModel.isEmptyInput.first())
    }

    @Test
    fun isValidInput_returnsTrueForNonBlankText() {
        assertTrue(viewModel.isValidInput("kotlin"))
    }

    @Test
    fun isValidInput_returnsFalseForBlankText() {
        assertFalse(viewModel.isValidInput(""))
        assertFalse(viewModel.isValidInput("   "))
    }

    @Test
    fun setEmptyInput_setsValueCorrectly() = runTest {
        viewModel.setEmptyInput(true)
        assertTrue(viewModel.isEmptyInput.first())
        viewModel.setEmptyInput(false)
        assertFalse(viewModel.isEmptyInput.first())
    }

    @Test
    fun clearResults_clearsItems() = runTest {
        val testItems = listOf(
            Item(
                name = "repo1",
                ownerIconUrl = "url1",
                language = "Kotlin",
                stargazersCount = 100,
                watchersCount = 50,
                forksCount = 20,
                openIssuesCount = 5,
                searchedAt = Date()
            )
        )
        whenever(gitHubRepository.searchRepositories("kotlin")).thenReturn(testItems)
        viewModel.searchResults("kotlin")
        advanceUntilIdle()
        assertEquals(testItems, viewModel.items.first())

        viewModel.clearResults()
        assertTrue(viewModel.items.first().isEmpty())
    }

    @Test
    fun onRepositorySelected_sendsItem() = runTest {
        val testItem = Item(
            name = "repo test",
            ownerIconUrl = "url",
            language = "Java",
            stargazersCount = 10,
            watchersCount = 5,
            forksCount = 2,
            openIssuesCount = 1,
            searchedAt = Date()
        )

        viewModel.navigateToRepositoryFlow.test {
            viewModel.onRepositorySelected(testItem)
            assertEquals(testItem, awaitItem())
            expectNoEvents()
        }
    }
}

// Utility for setting up TestDispatcher
@ExperimentalCoroutinesApi
class TestDispatcherRule(
    val testDispatcher: TestDispatcher = StandardTestDispatcher()
) : TestWatcher() {
    override fun starting(description: org.junit.runner.Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: org.junit.runner.Description) {
        Dispatchers.resetMain()
    }
}