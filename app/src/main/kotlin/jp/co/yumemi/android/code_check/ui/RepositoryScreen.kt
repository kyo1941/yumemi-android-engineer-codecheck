package jp.co.yumemi.android.code_check.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import jp.co.yumemi.android.code_check.R

@Composable
fun RepositoryScreen(repositoryName: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.jetbrains),
            contentDescription = null,
            modifier = Modifier
                .size(180.dp)
                .padding(8.dp),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = repositoryName)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Written in Kotlin")
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "hogehoge stars")
        Text(text = "hogehoge watchers")
        Text(text = "hogehoge forks")
        Text(text = "hogehoge open issues")
    }
}