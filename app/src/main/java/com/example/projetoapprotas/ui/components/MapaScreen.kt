import android.annotation.SuppressLint
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun MapaLeafletWebView(
    onLocationSelected: (Double, Double) -> Unit
) {
    AndroidView(factory = { context ->
        WebView(context).apply {
            settings.javaScriptEnabled = true
            webViewClient = WebViewClient()
            webChromeClient = WebChromeClient()

            // Interface para pegar coordenadas do JS
            addJavascriptInterface(
                object {
                    @android.webkit.JavascriptInterface
                    fun onLocationSelected(lat: Double, lng: Double) {
                        onLocationSelected(lat, lng)
                    }
                },
                "Android"
            )

            loadDataWithBaseURL(
                null,
                """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <style>html, body, #map { height: 100%; margin: 0; padding: 0; }</style>
                    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
                    <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
                </head>
                <body>
                <div id="map"></div>
                <script>
                    var map = L.map('map').setView([-23.55052, -46.633308], 13);
                    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                        attribution: '© OpenStreetMap contributors'
                    }).addTo(map);
                    
                    var marker;
                    
                    function onMapClick(e) {
                        if (marker) map.removeLayer(marker);
                        marker = L.marker(e.latlng).addTo(map);
                        Android.onLocationSelected(e.latlng.lat, e.latlng.lng);
                    }
                    
                    map.on('click', onMapClick);
                </script>
                </body>
                </html>
                """.trimIndent(),
                "text/html",
                "UTF-8",
                null
            )
        }
    })
}
