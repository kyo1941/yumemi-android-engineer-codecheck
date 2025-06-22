package jp.co.yumemi.android.code_check.exceptions

class BadRequestException(val statusCode: Int) : Exception()
class RateLimitException (val statusCode: Int, val resetTimeMs: Long): Exception()
class UnauthorizedException(val statusCode: Int) : Exception()
class NotFoundException(val statusCode: Int) : Exception()
class ClientErrorException(val statusCode: Int, val statusDescription: String) : Exception()
class ServerErrorException(val statusCode: Int, val statusDescription: String) : Exception()