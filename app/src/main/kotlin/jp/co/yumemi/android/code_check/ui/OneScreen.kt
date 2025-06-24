package jp.co.yumemi.android.code_check.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import jp.co.yumemi.android.code_check.OneViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import jp.co.yumemi.android.code_check.R
import jp.co.yumemi.android.code_check.domain.model.Item
import kotlinx.coroutines.launch


@Composable
fun OneScreen(
    viewModel: OneViewModel = hiltViewModel()
) {
    var searchText by remember { mutableStateOf("") }
    var items by remember { mutableStateOf<List<Item>>(emptyList()) }

    var isEmptyInput by remember { mutableStateOf(false) }

    val isLoading by viewModel.isLoading.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        OutlinedTextField(
            value = searchText,
            onValueChange = {
                searchText = it
                if (isEmptyInput && viewModel.isValidInput(it)) {
                    isEmptyInput = false
                }
            },
            label = { Text(stringResource(R.string.searchInputText_hint)) },
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
                    IconButton(onClick = { searchText = "" }) {
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
                        isEmptyInput = true
                        return@KeyboardActions
                    }
                    isEmptyInput = false
                    coroutineScope.launch {
                        items = viewModel.searchResults(searchText)
                        keyboardController?.hide()
                    }
                }
            ),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            modifier = Modifier.fillMaxWidth()
        )

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
                items(items.size) { index ->
                    val item = items[index]
                    Text(text = item.name)
                }
            }
        }

    }
}