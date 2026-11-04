package com.prashantlatta.samfili

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prashantlatta.samfili.data.ThemePreferences
import com.prashantlatta.samfili.ui.components.AppBackground
import com.prashantlatta.samfili.ui.settings.ThemeSettingsDialog
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : ComponentActivity() {

    private lateinit var themePreferences: ThemePreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        themePreferences = ThemePreferences(this)
        checkStoragePermission()

        setContent {
            val scope = rememberCoroutineScope()
            val bgImageUri by themePreferences.bgImageUri.collectAsState(initial = null)
            val bgOpacity by themePreferences.bgOpacity.collectAsState(initial = 0.25f)

            var isInFileManager by remember { mutableStateOf(false) }
            var currentFolder by remember { mutableStateOf(Environment.getExternalStorageDirectory()) }
            var showSettingsDialog by remember { mutableStateOf(false) }

            AppBackground(
                imageUri = bgImageUri,
                opacity = bgOpacity,
                isBlurred = isInFileManager
            ) {
                Scaffold(
                    containerColor = Color.Transparent,
                    topBar = {
                        ClassicSamsungToolbar(
                            title = if (isInFileManager) currentFolder.name else "SAMFILI",
                            showBackButton = isInFileManager,
                            onBackClick = {
                                val parent = currentFolder.parentFile
                                if (parent != null && parent.canRead() && currentFolder != Environment.getExternalStorageDirectory()) {
                                    currentFolder = parent
                                } else {
                                    isInFileManager = false
                                }
                            },
                            onSettingsClick = { showSettingsDialog = true }
                        )
                    }
                ) { paddingValues ->
                    Box(modifier = Modifier.padding(paddingValues)) {
                        if (!isInFileManager) {
                            HomeScreen(
                                onOpenInternalStorage = {
                                    currentFolder = Environment.getExternalStorageDirectory()
                                    isInFileManager = true
                                }
                            )
                        } else {
                            FileManagerScreen(
                                currentDirectory = currentFolder,
                                onFolderClick = { folder -> currentFolder = folder }
                            )
                        }

                        if (showSettingsDialog) {
                            ThemeSettingsDialog(
                                currentUri = bgImageUri,
                                currentOpacity = bgOpacity,
                                onSaveImage = { uri ->
                                    scope.launch { themePreferences.saveBackgroundImage(uri) }
                                },
                                onSaveOpacity = { opacity ->
                                    scope.launch { themePreferences.saveOpacity(opacity) }
                                },
                                onDismiss = { showSettingsDialog = false }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                    data = Uri.parse("package:$packageName")
                }
                startActivity(intent)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassicSamsungToolbar(
    title: String,
    showBackButton: Boolean,
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    TopAppBar(
        title = { Text(title, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.SemiBold) },
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
            }
        },
        actions = {
            IconButton(onClick = onSettingsClick) {
                Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1E5BB8))
    )
}

@Composable
fun HomeScreen(onOpenInternalStorage: () -> Unit) {
    val categories = listOf(
        CategoryItem("Images", Icons.Default.Image, Color(0xFFFF5252)),
        CategoryItem("Videos", Icons.Default.VideoLibrary, Color(0xFFFF7A00)),
        CategoryItem("Audio", Icons.Default.AudioFile, Color(0xFF4CAF50)),
        CategoryItem("Documents", Icons.Default.Description, Color(0xFF29B6F6)),
        CategoryItem("Downloads", Icons.Default.Download, Color(0xFFAB47BC)),
        CategoryItem("APKs", Icons.Default.Android, Color(0xFF00E676))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("श्रेणियाँ", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(categories) { item ->
                CategoryCard(item)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("डिवाइस स्टोरेज", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onOpenInternalStorage() },
            colors = CardDefaults.cardColors(containerColor = Color(0xCC050505)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(36.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("आंतरिक मेमोरी (Internal Storage)", color = Color.White, fontWeight = FontWeight.Medium)
                    Text("फ़ाइलें और फ़ोल्डर ब्राउज़ करें", color = Color(0xFF8E8E93), fontSize = 13.sp)
                }
            }
        }
    }
}

data class CategoryItem(val title: String, val icon: ImageVector, val iconColor: Color)

@Composable
fun CategoryCard(item: CategoryItem) {
    Card(
        modifier = Modifier
            .height(90.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0x99101010)),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(item.icon, contentDescription = item.title, tint = item.iconColor, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(item.title, color = Color.White, fontSize = 12.sp)
        }
    }
}

@Composable
fun FileManagerScreen(currentDirectory: File, onFolderClick: (File) -> Unit) {
    val files = remember(currentDirectory) {
        currentDirectory.listFiles()?.sortedWith(compareBy({ !it.isDirectory }, { it.name.lowercase() })) ?: emptyList()
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(files) { file ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (file.isDirectory) {
                            onFolderClick(file)
                        }
                    }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (file.isDirectory) Icons.Default.Folder else Icons.Default.InsertDriveFile,
                    contentDescription = null,
                    tint = if (file.isDirectory) Color(0xFFFFB300) else Color(0xFF90CAF9),
                    modifier = Modifier.size(30.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(file.name, color = Color.White, fontSize = 15.sp, maxLines = 1)
                    Text(
                        if (file.isDirectory) "फ़ोल्डर" else "${file.length() / 1024} KB",
                        color = Color(0xFF8E8E93),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
