import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import androidx.core.app.ActivityCompat
import android.Manifest
import android.content.pm.PackageManager

class LocationHelper(private val context: Context) {

    private var fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission") // Make sure to handle permissions correctly
    fun getCurrentLocation(onLocationResult: (Location?) -> Unit) {
        // Check if location permissions are granted
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Handle case where permissions are not granted
            onLocationResult(null)
            return
        }

        // Request last known location
        val locationTask: Task<Location> = fusedLocationClient.lastLocation
        locationTask.addOnSuccessListener { location: Location? ->
            // Return the location to the callback
            onLocationResult(location)
        }.addOnFailureListener {
            // Handle any failure in getting location
            onLocationResult(null)
        }
    }
}