package jp.co.yumemi.android.code_check.exceptions

class BadRequestException : Exception()
class RateLimitException (val resetTimeMs: Long): Exception()
class UnauthorizedException : Exception()
class NotFoundException : Exception()
class ClientErrorException(val statusCode: Int, val statusDescription: String) : Exception()
class ServerErrorException(val statusCode: Int, val statusDescription: String) : Exception()
class UnknownErrorException(val statusCode: Int, val statusDescription: String) : Exception()