package jp.co.yumemi.android.code_check.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import jp.co.yumemi.android.code_check.R
import jp.co.yumemi.android.code_check.domain.model.Item
import coil.compose.AsyncImage
import java.util.Date

@Composable
fun RepositoryScreen(
    navController: NavController,
    item: Item
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val imageSize: Dp = (screenHeight * 0.25f).coerceAtMost(160.dp)
    val imageCornerRadius: Dp = (imageSize * 0.2f).coerceAtLeast(8.dp)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.systemBars.asPaddingValues()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            modifier = Modifier.align(Alignment.Start),
            onClick = {
                navController.popBackStack()
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_keyboard_arrow_left_24),
                contentDescription = "back one screen",
            )
        }
        AsyncImage(
            model = item.ownerIconUrl,
            contentDescription = "owner icon",
            modifier = Modifier
                .size(imageSize)
                .clip(RoundedCornerShape(imageCornerRadius)),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.jetbrains),
        )

        Text(
            text = item.name,
            fontSize = MaterialTheme.typography.headlineMedium.fontSize,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            textAlign = TextAlign.Center,
        )

        Text(
            text = "Information",
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            textAlign = TextAlign.Left,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.weight(0.5f))

            Text(
                text = item.language,
                fontSize = MaterialTheme.typography.bodyLarge.fontSize
            )

            Spacer(modifier = Modifier.weight(1f))

            Column {
                Text(
                    text = stringResource(R.string.stars_count, item.stargazersCount),
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize
                )
                Text(
                    text = stringResource(R.string.watchers_count, item.watchersCount),
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize
                )
                Text(
                    text = stringResource(R.string.forks_count, item.forksCount),
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize
                )
                Text(
                    text = stringResource(R.string.open_issues_count, item.openIssuesCount),
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize
                )
            }

            Spacer(modifier = Modifier.weight(0.5f))
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRepositoryScreen() {
    RepositoryScreen(
        navController = rememberNavController(),
        item = Item(
            name = "JetBrains/kotlin",
            ownerIconUrl = "",
            language = "Kotlin",
            stargazersCount = 38530,
            watchersCount = 38530,
            forksCount = 4675,
            openIssuesCount = 131,
            searchedAt = Date()
        )
    )
}
