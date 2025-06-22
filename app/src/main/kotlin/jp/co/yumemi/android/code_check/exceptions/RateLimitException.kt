package jp.co.yumemi.android.code_check.exceptions

class RateLimitException (val resetTimeMs: Long): Exception()