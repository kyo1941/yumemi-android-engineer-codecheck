package jp.co.yumemi.android.code_check.common

import androidx.annotation.StringRes

sealed interface UserMessage {
    data class SnackBar(
        @StringRes val messageResId: Int,
        val formatArgs: Array<Any> = emptyArray()
    ) : UserMessage {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as SnackBar

            if (messageResId != other.messageResId) return false
            if (!formatArgs.contentEquals(other.formatArgs)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = messageResId
            result = 31 * result + formatArgs.contentHashCode()
            return result
        }
    }
}