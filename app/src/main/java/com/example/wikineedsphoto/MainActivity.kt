package com.example.wikineedsphoto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.wikineedsphoto.ui.theme.WikiNeedsPhotoTheme
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WikiNeedsPhotoTheme {

                MainPage(
                )

            }
        }
    }
}

@Preview
@Composable
fun Preview(){
    MainPage()
}

@Composable
fun MainPage(viewModel: AppSettings = AppSettings()) {
    // ContentPage Background color depending on theme
    val titleColor = if (isSystemInDarkTheme()) Color(0xFFFF7043) else Color(0xFFDE4436)
    val textColor = if (isSystemInDarkTheme()) Color(0xFFBDBDBD) else Color(0xFF757575)
    val buttonColor = if (isSystemInDarkTheme()) Color(0xFFFF7043) else Color(0xFFDE4436)

    // Main content

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Title Label
            Text(
                text = "Wiki needs photos",
                fontSize = 24.sp,
                color = titleColor,
                modifier = Modifier.padding(bottom = 25.dp)
            )

            // Subtitle Label
            Text(
                text = "Generate GPX with Wikidata pages's locations that needs photo around your current location",
                fontSize = 16.sp,
                color = titleColor,
                modifier = Modifier.padding(bottom = 25.dp)
            )

            // Search Radius Label
            Text(
                text = "Search Radius in Degrees (Max 3000 points):",
                fontSize = 18.sp,
                color = textColor,
                modifier = Modifier.align(Alignment.Start)
            )

            // Search Radius Editor
            OutlinedTextField(
                value = viewModel.searchRadiusDegrees,
                onValueChange = { viewModel.searchRadiusDegrees = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            // Search Exclusions Label
            Text(
                text = "Search Exclusions - Exclude points with these words in description. Separate by new line:",
                fontSize = 18.sp,
                color = textColor,
                modifier = Modifier.align(Alignment.Start)
            )

            // Search Exclusions Editor
            OutlinedTextField(
                value = viewModel.descriptionExclusions,
                onValueChange = { viewModel.descriptionExclusions = it },
                keyboardOptions = KeyboardOptions.Default,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp)
                    .padding(vertical = 8.dp)
            )

            // Action Button
            Button(
                onClick = { viewModel.getGpxCommand() },
                enabled = viewModel.isNotBusy,
                colors = ButtonDefaults.buttonColors(buttonColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(top = 10.dp),
            ) {
                Text(
                    text = viewModel.buttonText,
                    color = Color.White,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(3.dp)
                )
            }
        }

}

// Mocked ViewModel for demonstration
class AppSettings {
    var searchRadiusDegrees by mutableStateOf("")
    var descriptionExclusions by mutableStateOf("")
    val isNotBusy = true
    val buttonText = "Get GPX"

    fun getGpxCommand() {
        // Logic to get GPX
    }
}
