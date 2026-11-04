package com.prashantlatta.samfili.ui.settings

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun ThemeSettingsDialog(
    currentUri: String?,
    currentOpacity: Float,
    onSaveImage: (String?) -> Unit,
    onSaveOpacity: (Float) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var sliderValue by remember { mutableStateOf(currentOpacity) }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            try {
                context.contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: Exception) {}
            onSaveImage(it.toString())
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF121212),
        titleContentColor = Color.White,
        textContentColor = Color(0xFFCCCCCC),
        title = { Text("थीम और बैकग्राउंड कस्टमाइज़ेशन") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = "मेन पेज बैकग्राउंड इमेज:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { imagePicker.launch(arrayOf("image/*")) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E5BB8)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Image, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("छवि चुनें")
                    }

                    if (!currentUri.isNullOrEmpty()) {
                        OutlinedButton(
                            onClick = { onSaveImage(null) },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                            modifier = Modifier.wrapContentWidth()
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "हटाएं")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "बैकग्राउंड अपारदर्शिता (Opacity): ${(sliderValue * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFAAAAAA)
                )

                Slider(
                    value = sliderValue,
                    onValueChange = {
                        sliderValue = it
                        onSaveOpacity(it)
                    },
                    valueRange = 0.05f..0.85f,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFFFFB300),
                        activeTrackColor = Color(0xFF1E5BB8)
                    )
                )

                Text(
                    text = "* फ़ाइल मैनेजर के अंदर जाने पर यह बैकग्राउंड अपने आप ब्लर (Blur) हो जाएगा।",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF888888)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("पूर्ण", color = Color(0xFF42A5F5))
            }
        }
    )
}
