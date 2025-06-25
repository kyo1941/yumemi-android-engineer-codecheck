package jp.co.yumemi.android.code_check.data.repository

import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import jp.co.yumemi.android.code_check.exceptions.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class GitHubRepositoryImplTest {

    private fun createRepositoryWithMock(status: HttpStatusCode, body: String = "", headers: Headers = headersOf()): GitHubRepositoryImpl {
        val mockEngine = MockEngine { request ->
            respond(
                content = body,
                status = status,
                headers = headers
            )
        }
        val client = HttpClient(mockEngine)
        return GitHubRepositoryImpl(client)
    }

    @Test
    fun searchRepositories_returnsItems_on200() = runTest {
        val json = """
            {
                "items": [
                    {
                        "full_name": "test/repo",
                        "owner": {"avatar_url": "url"},
                        "language": "Kotlin",
                        "stargazers_count": 1,
                        "watchers_count": 2,
                        "forks_count": 3,
                        "open_issues_count": 4
                    }
                ]
            }
        """.trimIndent()
        val repo = createRepositoryWithMock(HttpStatusCode.OK, json)
        val result = repo.searchRepositories("kotlin")
        assertEquals(1, result.size)
        assertEquals("test/repo", result[0].name)
    }

    @Test
    fun searchRepositories_returnsEmptyList_whenItemsMissing() = runTest {
        val json = "{}"
        val repo = createRepositoryWithMock(HttpStatusCode.OK, json)
        val result = repo.searchRepositories("kotlin")
        assertTrue(result.isEmpty())
    }

    @Test
    fun searchRepositories_returnsEmptyList_whenItemsNull() = runTest {
        val json = """{"items": null}"""
        val repo = createRepositoryWithMock(HttpStatusCode.OK, json)
        val result = repo.searchRepositories("kotlin")
        assertTrue(result.isEmpty())
    }

    @Test(expected = BadRequestException::class)
    fun searchRepositories_throws_BadRequestException_on_400() = runTest {
        val repo = createRepositoryWithMock(HttpStatusCode.BadRequest)
        repo.searchRepositories("kotlin")
    }

    @Test(expected = UnauthorizedException::class)
    fun searchRepositories_throws_UnauthorizedException_on_401() = runTest {
        val repo = createRepositoryWithMock(HttpStatusCode.Unauthorized)
        repo.searchRepositories("kotlin")
    }

    @Test(expected = NotFoundException::class)
    fun searchRepositories_throws_NotFoundException_on_404() = runTest {
        val repo = createRepositoryWithMock(HttpStatusCode.NotFound)
        repo.searchRepositories("kotlin")
    }

    @Test(expected = ClientErrorException::class)
    fun searchRepositories_throws_ClientErrorException_on_418() = runTest {
        val repo = createRepositoryWithMock(HttpStatusCode(418, "I'm a teapot"))
        repo.searchRepositories("kotlin")
    }

    @Test(expected = ServerErrorException::class)
    fun searchRepositories_throws_ServerErrorException_on_500() = runTest {
        val repo = createRepositoryWithMock(HttpStatusCode.InternalServerError)
        repo.searchRepositories("kotlin")
    }

    @Test(expected = RateLimitException::class)
    fun searchRepositories_throws_RateLimitException_on_403() = runTest {
        val headers = headersOf("X-RateLimit-Remaining" to listOf("0"), "X-RateLimit-Reset" to listOf("${1672531200}"))
        val repo = createRepositoryWithMock(HttpStatusCode.Forbidden, headers = headers)
        repo.searchRepositories("kotlin")
    }

    @Test(expected = RateLimitException::class)
    fun searchRepositories_throws_RateLimitException_on_403_with_RetryAfter() = runTest {
        val headers = headersOf("Retry-After" to listOf("2"))
        val repo = createRepositoryWithMock(HttpStatusCode.Forbidden, headers = headers)
        repo.searchRepositories("kotlin")
    }

    @Test(expected = RateLimitException::class)
    fun searchRepositories_throws_RateLimitException_on_403_with_no_headers() = runTest {
        val repo = createRepositoryWithMock(HttpStatusCode.Forbidden)
        repo.searchRepositories("kotlin")
    }

    @Test(expected = Exception::class)
    fun searchRepositories_throws_Exception_on_unexpected_status() = runTest {
        val repo = createRepositoryWithMock(HttpStatusCode(600, "Unknown"))
        repo.searchRepositories("kotlin")
    }
}