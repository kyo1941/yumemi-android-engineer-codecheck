package jp.co.yumemi.android.code_check.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

private val DarkColorScheme = darkColorScheme(
    primary = Purple200,
    secondary = Teal200,
    background = DarkBackground,
    onPrimary = OnPrimaryDark,
    onSecondary = OnSecondary
)

private val LightColorScheme = lightColorScheme(
    primary = Purple500,
    secondary = Teal200,
    background = LightBackground,
    onPrimary = OnPrimaryLight,
    onSecondary = OnSecondary
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = androidx.compose.material3.Typography()
    ) {
        CompositionLocalProvider(
            LocalTextStyle provides MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onBackground
            )
        ) {
            content()
        }
    }
}