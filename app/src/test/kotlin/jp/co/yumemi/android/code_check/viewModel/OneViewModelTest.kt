package jp.co.yumemi.android.code_check.viewModel

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

    @Test
    fun searchResults_handlesBadRequestException() = runTest {
        whenever(gitHubRepository.searchRepositories("invalid")).thenAnswer { throw BadRequestException(400) }

        val job = launch {
            viewModel.showErrorFlow.collect { message ->
                assertTrue(message is UserMessage.SnackBar)
                val snackbar = message as UserMessage.SnackBar
                assertEquals(R.string.error_with_code, snackbar.messageResId)
                assertEquals(400, snackbar.formatArgs[0])
                assertEquals(R.string.error_bad_request, snackbar.formatArgs[1])
            }
        }

        viewModel.searchResults("invalid")
        advanceUntilIdle()

        assertTrue(viewModel.items.first().isEmpty())
        assertFalse(viewModel.isLoading.first())
        job.cancel()
    }

    @Test
    fun searchResults_handlesRateLimitException() = runTest {
        val resetTime = System.currentTimeMillis() + 5000 // 5 seconds from now
        whenever(gitHubRepository.searchRepositories("limit")).thenAnswer { throw RateLimitException(403, resetTime) }

        val job = launch {
            viewModel.showErrorFlow.collect { message ->
                assertTrue(message is UserMessage.SnackBar)
                val snackbar = message as UserMessage.SnackBar
                assertEquals(R.string.error_with_code, snackbar.messageResId)
                assertEquals(403, snackbar.formatArgs[0])
                assertEquals(R.string.error_rate_limit, snackbar.formatArgs[1])
                // We expect waitSeconds to be at least 1, due to `coerceAtLeast(1)`
                assertTrue((snackbar.formatArgs[2] as Long) >= 1)
            }
        }

        viewModel.searchResults("limit")
        advanceUntilIdle()

        assertTrue(viewModel.items.first().isEmpty())
        assertFalse(viewModel.isLoading.first())
        job.cancel()
    }

    @Test
    fun searchResults_handlesUnauthorizedException() = runTest {
        whenever(gitHubRepository.searchRepositories("unauthorized")).thenAnswer { throw UnauthorizedException(401) }

        val job = launch {
            viewModel.showErrorFlow.collect { message ->
                assertTrue(message is UserMessage.SnackBar)
                val snackbar = message as UserMessage.SnackBar
                assertEquals(R.string.error_with_code, snackbar.messageResId)
                assertEquals(401, snackbar.formatArgs[0])
                assertEquals(R.string.error_unauthorized, snackbar.formatArgs[1])
            }
        }

        viewModel.searchResults("unauthorized")
        advanceUntilIdle()

        assertTrue(viewModel.items.first().isEmpty())
        assertFalse(viewModel.isLoading.first())
        job.cancel()
    }

    @Test
    fun searchResults_handlesNotFoundException() = runTest {
        whenever(gitHubRepository.searchRepositories("notfound")).thenAnswer { throw NotFoundException(404) }

        val job = launch {
            viewModel.showErrorFlow.collect { message ->
                assertTrue(message is UserMessage.SnackBar)
                val snackbar = message as UserMessage.SnackBar
                assertEquals(R.string.error_with_code, snackbar.messageResId)
                assertEquals(404, snackbar.formatArgs[0])
                assertEquals(R.string.error_not_found, snackbar.formatArgs[1])
            }
        }

        viewModel.searchResults("notfound")
        advanceUntilIdle()

        assertTrue(viewModel.items.first().isEmpty())
        assertFalse(viewModel.isLoading.first())
        job.cancel()
    }

    @Test
    fun searchResults_handlesClientErrorException() = runTest {
        whenever(gitHubRepository.searchRepositories("clienterror")).thenAnswer { throw ClientErrorException(422, "Unprocessable Entity") }

        val job = launch {
            viewModel.showErrorFlow.collect { message ->
                assertTrue(message is UserMessage.SnackBar)
                val snackbar = message as UserMessage.SnackBar
                assertEquals(R.string.error_with_code, snackbar.messageResId)
                assertEquals(422, snackbar.formatArgs[0])
                assertEquals(R.string.error_client, snackbar.formatArgs[1])
            }
        }

        viewModel.searchResults("clienterror")
        advanceUntilIdle()

        assertTrue(viewModel.items.first().isEmpty())
        assertFalse(viewModel.isLoading.first())
        job.cancel()
    }

    @Test
    fun searchResults_handlesServerErrorException() = runTest {
        whenever(gitHubRepository.searchRepositories("servererror")).thenAnswer { throw ServerErrorException(500, "Internal Server Error") }

        val job = launch {
            viewModel.showErrorFlow.collect { message ->
                assertTrue(message is UserMessage.SnackBar)
                val snackbar = message as UserMessage.SnackBar
                assertEquals(R.string.error_with_code, snackbar.messageResId)
                assertEquals(500, snackbar.formatArgs[0])
                assertEquals(R.string.error_server, snackbar.formatArgs[1])
            }
        }

        viewModel.searchResults("servererror")
        advanceUntilIdle()

        assertTrue(viewModel.items.first().isEmpty())
        assertFalse(viewModel.isLoading.first())
        job.cancel()
    }

    @Test
    fun searchResults_handlesGenericException() = runTest {
        whenever(gitHubRepository.searchRepositories("genericerror")).thenAnswer { throw Exception("Unknown error") }

        val job = launch {
            viewModel.showErrorFlow.collect { message ->
                assertTrue(message is UserMessage.SnackBar)
                val snackbar = message as UserMessage.SnackBar
                assertEquals(R.string.error_unknown, snackbar.messageResId)
            }
        }

        viewModel.searchResults("genericerror")
        advanceUntilIdle()

        assertTrue(viewModel.items.first().isEmpty())
        assertFalse(viewModel.isLoading.first())
        job.cancel()
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

        var fakeTime = 0L
        val fakeClock: () -> Long = { fakeTime }

        // 仮想時間を返すクロックを注入
        viewModel = OneViewModel(gitHubRepository, clock = fakeClock)

        viewModel.searchResults("first")
        advanceUntilIdle()

        val startTime = fakeTime
        fakeTime += 1000L

        viewModel.searchResults("second")
        advanceUntilIdle()
        val endTime = fakeTime

        assertTrue(endTime - startTime >= 1000L)
        verify(gitHubRepository, times(2)).searchRepositories(any())
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

        val job = launch {
            viewModel.navigateToRepositoryFlow.collect { item ->
                assertEquals(testItem, item)
            }
        }

        viewModel.onRepositorySelected(testItem)
        advanceUntilIdle() // Ensure the send operation completes

        job.cancel()
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