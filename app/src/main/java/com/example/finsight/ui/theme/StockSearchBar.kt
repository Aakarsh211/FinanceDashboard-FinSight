package com.example.finsight.ui.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.finsight.model.StockSearchResult

@Composable
fun StockSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    suggestions: List<StockSearchResult>,
    onSuggestionSelected: (StockSearchResult) -> Unit,
    modifier: Modifier = Modifier,
    onSearch: () -> Unit = {} // New parameter for search action
) {
    var expanded by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = {
                onValueChange(it)
                expanded = it.isNotEmpty()
            },
            label = { Text("Search by ticker or company name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearch()
                    focusManager.clearFocus()
                    expanded = false
                }
            ),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            trailingIcon = {
                if (value.isNotEmpty()) {
                    IconButton(onClick = {
                        onValueChange("")
                        expanded = false
                    }) {
                        Icon(Icons.Default.Close, "Clear")
                    }
                }
            }
        )

        if (expanded && suggestions.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
                    .zIndex(1f), // Ensure dropdown appears above other content
                shape = RoundedCornerShape(4.dp)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)
                ) {
                    items(suggestions) { suggestion ->
                        Text(
                            text = "${suggestion.ticker} - ${suggestion.name}",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSuggestionSelected(suggestion)
                                    expanded = false
                                    focusManager.clearFocus()
                                }
                                .padding(16.dp)
                        )
                        Divider()
                    }
                }
            }
        }
    }
}