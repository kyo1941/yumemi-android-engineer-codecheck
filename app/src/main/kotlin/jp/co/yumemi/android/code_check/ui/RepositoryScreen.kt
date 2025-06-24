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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
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
                .size(160.dp)
                .clip(RoundedCornerShape(32.dp)),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.jetbrains),
        )

        Text(
            text = item.name,
            fontWeight = MaterialTheme.typography.headlineMedium.fontWeight,
            fontSize = MaterialTheme.typography.headlineMedium.fontSize,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            textAlign = TextAlign.Center,
        )

        Text(
            text = "Information",
            fontWeight = MaterialTheme.typography.titleLarge.fontWeight,
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Left,
        )

        Spacer(modifier = Modifier.weight(0.1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Written in ${item.language}")

            Spacer(modifier = Modifier.weight(1f))

            Column {
                Text(text = "${item.stargazersCount} stars")
                Text(text = "${item.watchersCount} watchers")
                Text(text = "${item.forksCount} forks")
                Text(text = "${item.openIssuesCount} open issues")
            }
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
