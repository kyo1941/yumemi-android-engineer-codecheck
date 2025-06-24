package jp.co.yumemi.android.code_check.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.res.TypedArrayUtils.getString
import jp.co.yumemi.android.code_check.OneViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import jp.co.yumemi.android.code_check.R
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@Composable
fun OneScreen(
    navController: NavController,
    viewModel: OneViewModel = hiltViewModel()
) {
    val searchText by viewModel.searchText.collectAsState()
    val items by viewModel.items.collectAsState()

    val isEmptyInput by viewModel.isEmptyInput.collectAsState()

    val isLoading by viewModel.isLoading.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        viewModel.navigateToRepositoryFlow.collectLatest { item ->
            navController.currentBackStackEntry?.savedStateHandle?.set("item", item)
            navController.navigate("repository")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.systemBars.asPaddingValues())
            .padding(8.dp)
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }
    ) {
        Text(
            text = stringResource(R.string.app_name),
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            textAlign = TextAlign.Center,
        )

        OutlinedTextField(
            value = searchText,
            onValueChange = { viewModel.onSearchTextChanged(it) },
            label = {
                Text(
                    text = stringResource(R.string.searchInputText_hint),
                    style = MaterialTheme.typography.bodySmall
                )
            },
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            maxLines = 1,
            isError = isEmptyInput,
            supportingText = {
                if (isEmptyInput) {
                    Text(stringResource(R.string.error_empty_search), color = Color.Red)
                }
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "search icon",
                    tint = Color.DarkGray
                )
            },
            trailingIcon = {
                if (searchText.isNotEmpty()) {
                    IconButton(onClick = {
                        viewModel.onSearchTextChanged("")
                        viewModel.clearResults()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear Text",
                            tint = Color.DarkGray
                        )
                    }
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    if (!viewModel.isValidInput(searchText)) {
                        viewModel.setEmptyInput(true)
                        return@KeyboardActions
                    }
                    viewModel.setEmptyInput(false)
                    coroutineScope.launch {
                        viewModel.searchResults(searchText)
                        keyboardController?.hide()
                    }
                }
            ),
            colors = TextFieldDefaults.colors(
                unfocusedIndicatorColor = Color.LightGray,
            ),
            modifier = Modifier.fillMaxWidth().shadow(40.dp, RoundedCornerShape(16.dp))
        )

        Spacer(modifier = Modifier.weight(0.1f))

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp),
                color = Color.Blue
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp)
            ) {
                itemsIndexed(items) { index, item ->
                    Text(
                        text = item.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                            .clickable {
                                viewModel.onRepositorySelected(item)
                            }
                    )
                    if (index < items.lastIndex) {
                        HorizontalDivider(
                            color = Color.LightGray,
                            thickness = 1.dp
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewOneScreen() {
    OneScreen(navController = rememberNavController())
}
