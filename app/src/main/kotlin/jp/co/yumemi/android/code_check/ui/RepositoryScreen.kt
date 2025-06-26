package jp.co.yumemi.android.code_check.ui

import android.content.res.Configuration
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
import androidx.compose.ui.text.font.FontWeight
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
    val screenWidth = configuration.screenWidthDp.dp
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val imageSize: Dp = (if (isLandscape) (screenWidth * 0.25f) else (screenHeight * 0.25f)).coerceAtMost(160.dp)
    val imageCornerRadius: Dp = (imageSize * 0.2f).coerceAtLeast(8.dp)

    if(!isLandscape) {
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
                    tint = MaterialTheme.colorScheme.onBackground
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
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                textAlign = TextAlign.Center,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier.weight(0.5f))

                Text(
                    text = item.language,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onBackground
                    ),
                )

                Spacer(modifier = Modifier.weight(1f))

                Column {
                    Text(
                        text = stringResource(R.string.stars_count, item.stargazersCount),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onBackground
                        )

                    )
                    Text(
                        text = stringResource(R.string.watchers_count, item.watchersCount),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                    Text(
                        text = stringResource(R.string.forks_count, item.forksCount),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                    Text(
                        text = stringResource(R.string.open_issues_count, item.openIssuesCount),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                }

                Spacer(modifier = Modifier.weight(0.5f))
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    } else {
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(WindowInsets.systemBars.asPaddingValues()),
        ) {
            IconButton(
                onClick = {
                    navController.popBackStack()
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_keyboard_arrow_left_24),
                    contentDescription = "back one screen",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.weight(0.8f))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.weight(1f))

                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    AsyncImage(
                        model = item.ownerIconUrl,
                        contentDescription = "owner icon",
                        modifier = Modifier
                            .size(imageSize)
                            .clip(RoundedCornerShape(imageCornerRadius)),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(id = R.drawable.jetbrains)
                    )

                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(vertical = 16.dp),
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Column {
                    Text(
                        text = item.language,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onBackground
                        ),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Column {
                        Text(
                            text = stringResource(R.string.stars_count, item.stargazersCount),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        )
                        Text(
                            text = stringResource(R.string.watchers_count, item.watchersCount),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        )
                        Text(
                            text = stringResource(R.string.forks_count, item.forksCount),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        )
                        Text(
                            text = stringResource(R.string.open_issues_count, item.openIssuesCount),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.weight(1f))
        }
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
