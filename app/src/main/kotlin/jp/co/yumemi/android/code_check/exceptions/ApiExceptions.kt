package jp.co.yumemi.android.code_check.exceptions

sealed class ApiException(message: String? = null) : Exception(message)

class BadRequestException(val statusCode: Int) : ApiException()
class RateLimitException (val statusCode: Int, val resetTimeMs: Long): ApiException()
class UnauthorizedException(val statusCode: Int) : ApiException()
class NotFoundException(val statusCode: Int) : ApiException()
class ClientErrorException(val statusCode: Int, val statusDescription: String) : ApiException(statusDescription)
class ServerErrorException(val statusCode: Int, val statusDescription: String) : ApiException(statusDescription)