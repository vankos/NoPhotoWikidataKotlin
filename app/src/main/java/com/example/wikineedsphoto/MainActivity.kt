package com.example.wikineedsphoto

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

private lateinit var fusedLocationClient: FusedLocationProviderClient
class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
                MainPage()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MainPage(viewModel: AppSettings = AppSettings(
        0.05,
        "hotel in\n" +
                "hostel in\n" +
                "guesthouse in\n" +
                "apartment in\n" +
                "neighborhood in"
    )) {
        // ContentPage Background color depending on theme
        val titleColor = if (isSystemInDarkTheme()) Color(0xFFFF7043) else Color(0xFFDE4436)
        val textColor = if (isSystemInDarkTheme()) Color(0xFFBDBDBD) else Color(0xFF757575)
        val editorBackgroundColor = if (isSystemInDarkTheme()) Color(0xFF424242) else Color.White
        val editorTextColor = if (isSystemInDarkTheme()) Color(0xFFE0E0E0) else Color(0xFF212121)
        val buttonColor = if (isSystemInDarkTheme()) Color(0xFFFF7043) else Color(0xFFDE4436)
        val context = LocalContext.current
        val sharedPreferences = remember {
            context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)}
        val savedExclusions = loadText(sharedPreferences)
        if(savedExclusions.isNotEmpty())
            viewModel.descriptionExclusions = savedExclusions

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
                value = viewModel.searchRadiusDegrees.toString(),
                onValueChange = { viewModel.searchRadiusDegrees = it.toDouble() },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = editorBackgroundColor,
                    unfocusedTextColor = editorTextColor
                ),
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
                onValueChange = {
                    viewModel.descriptionExclusions = it
                    saveText(sharedPreferences, it)
                },
                keyboardOptions = KeyboardOptions.Default,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor  = editorBackgroundColor,
                    unfocusedTextColor = editorTextColor
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp)
                    .padding(vertical = 8.dp)
            )

            // Action Button
            Button(
                onClick = { GetGpx(viewModel) },
                enabled = viewModel.isNotBusy,
                colors = ButtonDefaults.buttonColors(containerColor  = buttonColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(top = 10.dp),
            ) {
                Text(
                    text = viewModel.buttonText,
                    color = Color.White,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }

    private fun GetGpx(viewModel: AppSettings) {
//        if (ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return
//        }

        //viewModel.getGpxCommand(fusedLocationClient.lastLocation.result)
        viewModel.getGpxCommand(this)
        return
    }

    // Helper function to load the text from SharedPreferences
    private fun loadText(sharedPreferences: SharedPreferences): String {
        return sharedPreferences.getString("text_key", "") ?: ""
    }

    // Helper function to save the text to SharedPreferences
    private fun saveText(sharedPreferences: SharedPreferences, text: String) {
        sharedPreferences.edit().putString("text_key", text).apply()
    }


    @Preview
    @Composable
    fun Preview(){
        MainPage()
    }
}




