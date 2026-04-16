package com.example.weatherapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import com.example.weatherapp.R
import com.example.weatherapp.data.model.WeatherUiModel
import com.example.weatherapp.ui.viewmodel.WeatherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: WeatherViewModel,
    onNavigateToDetails: (String) -> Unit
) {
    val weatherList by viewModel.weatherList.collectAsState()
    val citySuggestions by viewModel.citySuggestions.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessageId by viewModel.errorMessage.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        isDropdownExpanded = true
                        viewModel.onSearchQueryChanged(it)
                    },
                    label = { Text(stringResource(R.string.search_hint)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                DropdownMenu(
                    expanded = isDropdownExpanded && citySuggestions.isNotEmpty(),
                    onDismissRequest = { isDropdownExpanded = false },
                    properties = PopupProperties(focusable = false),
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    citySuggestions.forEach { city ->
                        DropdownMenuItem(
                            text = { Text("${city.name}, ${city.country}") },
                            onClick = {
                                searchQuery = ""
                                isDropdownExpanded = false
                                viewModel.selectCityFromSuggestions(city)
                            }
                        )
                    }
                }
            }

            if (isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
            }

            errorMessageId?.let { stringId ->
                Text(
                    text = stringResource(id = stringId),
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(weatherList) { weather ->
                    WeatherCard(
                        weather = weather,
                        onClick = { onNavigateToDetails(weather.cityName) },
                        onDelete = { viewModel.removeCity(weather.cityName) }
                    )
                }
            }
        }
    }
}

@Composable
fun WeatherCard(
    weather: WeatherUiModel,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "${weather.cityName}, ${weather.country}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.current_temp, weather.currentTemperature.toString()),
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete_desc),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}