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

    private fun <T : Exception> testSearchRepositoriesException(
        statusCode: HttpStatusCode,
        expectedException: Class<T>,
        headers: Headers = headersOf()
    ) = runTest {
        val repo = createRepositoryWithMock(statusCode, headers = headers)
        try {
            repo.searchRepositories("kotlin")
            fail("Expected ${expectedException.simpleName} but no exception was thrown.")
        } catch (e: Exception) {
            assertEquals(
                "Expected ${expectedException.simpleName} but got ${e::class.simpleName}",
                expectedException,
                e::class.java
            )
        }
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
    fun searchRepositories_skipsNullItems() = runTest {
        val json = """
        {
            "items": [
                null,
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
    fun searchRepositories_ownerNull_setsOwnerIconUrlEmpty() = runTest {
        val json = """
        {
            "items": [
                {
                    "full_name": "test/repo",
                    "owner": null,
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
        assertEquals("", result[0].ownerIconUrl)
    }

    @Test
    fun searchRepositories_ownerWithoutAvatar_setsOwnerIconUrlEmpty() = runTest {
        val json = """
        {
            "items": [
                {
                    "full_name": "test/repo",
                    "owner": {},
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
        assertEquals("", result[0].ownerIconUrl)
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

    @Test
    fun searchRepositories_throws_BadRequestException_on_400() = runTest {
        testSearchRepositoriesException(HttpStatusCode.BadRequest, BadRequestException::class.java)
    }

    @Test
    fun searchRepositories_throws_UnauthorizedException_on_401() = runTest {
        testSearchRepositoriesException(HttpStatusCode.Unauthorized, UnauthorizedException::class.java)
    }

    @Test
    fun searchRepositories_throws_NotFoundException_on_404() = runTest {
        testSearchRepositoriesException(HttpStatusCode.NotFound, NotFoundException::class.java)
    }

    @Test
    fun searchRepositories_throws_ClientErrorException_on_418() = runTest {
        testSearchRepositoriesException(HttpStatusCode(418, "I'm a teapot"), ClientErrorException::class.java)
    }

    @Test
    fun searchRepositories_throws_ServerErrorException_on_500() = runTest {
        testSearchRepositoriesException(HttpStatusCode.InternalServerError, ServerErrorException::class.java)
    }

    @Test
    fun searchRepositories_throws_RateLimitException_on_403() = runTest {
        val headers = headersOf("X-RateLimit-Remaining" to listOf("0"), "X-RateLimit-Reset" to listOf("${1672531200}"))
        testSearchRepositoriesException(HttpStatusCode.Forbidden, RateLimitException::class.java, headers)
    }

    @Test
    fun searchRepositories_throws_RateLimitException_on_403_with_RetryAfter() = runTest {
        val headers = headersOf("Retry-After" to listOf("2"))
        testSearchRepositoriesException(HttpStatusCode.Forbidden, RateLimitException::class.java, headers)
    }

    @Test
    fun searchRepositories_throws_RateLimitException_on_403_with_invalid_remaining() = runTest {
        val headers = headersOf("X-RateLimit-Remaining" to listOf("abc"), "X-RateLimit-Reset" to listOf("${1672531200}"))
        testSearchRepositoriesException(HttpStatusCode.Forbidden, RateLimitException::class.java, headers)
    }

    @Test
    fun searchRepositories_throws_RateLimitException_on_403_with_no_headers() = runTest {
        testSearchRepositoriesException(HttpStatusCode.Forbidden, RateLimitException::class.java)
    }

    @Test
    fun searchRepositories_throws_RateLimitException_on_403_with_remaining_zero_but_no_reset() = runTest {
        val headers = headersOf("X-RateLimit-Remaining" to listOf("0"))
        testSearchRepositoriesException(HttpStatusCode.Forbidden, RateLimitException::class.java, headers)
    }

    @Test
    fun searchRepositories_throws_Exception_on_unexpected_status() = runTest {
        testSearchRepositoriesException(HttpStatusCode(600, "Unknown"), Exception::class.java)
    }
}