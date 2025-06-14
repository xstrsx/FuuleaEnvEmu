package com.fr0z863xf.FuEmu

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import com.fr0z863xf.FuEmu.ui.theme.FuEmuTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import androidx.core.net.toUri
import androidx.core.content.edit
import org.json.JSONException
import java.io.File
import java.io.FileWriter
import kotlin.concurrent.write
import kotlin.io.path.exists

data class GitHubAsset(val name: String, val browserDownloadUrl: String)
data class GitHubReleaseInfo(
    val tagName: String,
    val htmlUrl: String,
    val body: String,
    val assets: List<GitHubAsset>
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FuEmuTheme {
                MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    val sharedPreferences = remember { context.getSharedPreferences("FuEmuPrefs", Context.MODE_PRIVATE) }
    val coroutineScope = rememberCoroutineScope()

    var showUpdateDialog by remember { mutableStateOf(false) }
    var latestReleaseInfo by remember { mutableStateOf<GitHubReleaseInfo?>(null) }
    var isCheckingForUpdate by remember { mutableStateOf(false) }


    fun isNewerVersion(newVersion: String, currentVersion: String): Boolean {
        val newParts = newVersion.split(".").mapNotNull { it.toIntOrNull() }
        val currentParts = currentVersion.split(".").mapNotNull { it.toIntOrNull() }

        for (i in 0 until maxOf(newParts.size, currentParts.size)) {
            val newPart = newParts.getOrElse(i) { 0 }
            val currentPart = currentParts.getOrElse(i) { 0 }
            if (newPart > currentPart) return true
            if (newPart < currentPart) return false
        }
        return false
    }

    fun checkForUpdates() {
        if (isCheckingForUpdate) return
        isCheckingForUpdate = true
        Toast.makeText(context, "正在检查更新...", Toast.LENGTH_SHORT).show()

        coroutineScope.launch(Dispatchers.IO) {
            try {
                val url = URL("https://api.github.com/repos/fR0Z863xF/FuuleaEnvEmu/releases/latest")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 10000 // 10 seconds
                connection.readTimeout = 10000   // 10 seconds

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        response.append(line)
                    }
                    reader.close()

                    val jsonResponse = JSONObject(response.toString())
                    val tagName = jsonResponse.getString("tag_name").removePrefix("v") // Remove 'v' prefix if present
                    val htmlUrl = jsonResponse.getString("html_url")
                    val body = jsonResponse.getString("body")
                    val assetsArray = jsonResponse.getJSONArray("assets")
                    val assetsList = mutableListOf<GitHubAsset>()
                    for (i in 0 until assetsArray.length()) {
                        val assetObject = assetsArray.getJSONObject(i)
                        assetsList.add(
                            GitHubAsset(
                                name = assetObject.getString("name"),
                                browserDownloadUrl = assetObject.getString("browser_download_url")
                            )
                        )
                    }

                    val currentVersion = (context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "0.0.0").removePrefix("v")
                    if (tagName.isNotEmpty() && tagName != currentVersion && isNewerVersion(tagName, currentVersion)) {
                        withContext(Dispatchers.Main) {
                            latestReleaseInfo = GitHubReleaseInfo(jsonResponse.getString("tag_name"), htmlUrl, body, assetsList)
                            showUpdateDialog = true
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "已是最新版本", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Log.e("MainScreen", "GitHub API Err: ${connection.responseCode} ${connection.responseMessage}")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "检查更新失败: ${connection.responseMessage}", Toast.LENGTH_LONG).show()
                    }
                }
                connection.disconnect()
            } catch (e: Exception) {
                Log.e("MainScreen", "Update check failed", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "检查更新失败: ${e.message}", Toast.LENGTH_LONG).show()
                }
            } finally {
                isCheckingForUpdate = false
            }
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("FuEmu") },
                actions = {
                    Text(if (isCheckingForUpdate) "正在检查更新..." else "检查更新->")
                    IconButton(onClick = { checkForUpdates() }, enabled = !isCheckingForUpdate) {
                        Icon(Icons.Filled.Refresh, contentDescription = "检查更新")

                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                val launchIntent = Intent(Intent.ACTION_MAIN)
                launchIntent.addCategory(Intent.CATEGORY_LAUNCHER)
                launchIntent.setComponent(ComponentName("com.fuulea.venus.g", "com.fuulea.venus.MainActivity"))
                launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(context, launchIntent,null)
            }) {
                Row(modifier = Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.PlayArrow, contentDescription = "启动")
                    Spacer(Modifier.width(8.dp))
                    Text("启动")
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SystemInfoCard(sharedPreferences)
            GovernanceEnvironmentCard(sharedPreferences)
            ProjectInfoCard()
        }
    }

    if (showUpdateDialog && latestReleaseInfo != null) {
        UpdateInfoDialog(
            releaseInfo = latestReleaseInfo!!,
            onDismiss = { showUpdateDialog = false },
            context = context
        )
    }
}

fun savePreferencesToJson(context: Context, sharedPreferences: SharedPreferences) {
    val allPreferences = sharedPreferences.all
    val json = JSONObject()
    for ((key, value) in allPreferences) {
        try {
            json.put(key, value)
        } catch (e: JSONException) {
            Log.e("MainScreen", "Error putting preference into JSON", e)
        }
    }

    if (json.length() == 0) {
        Log.w("MainScreen", "No preferences to save to JSON.")
        return
    }

    try {
        val fuEmuDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "FuEmu"
        )
        if (!fuEmuDir.exists()) {
            if (!fuEmuDir.mkdirs()) {
                Log.e("MainScreen", "Failed to create directory: ${fuEmuDir.absolutePath}")
                Toast.makeText(context, "创建目录失败", Toast.LENGTH_LONG).show()
                return
            }
        }
        val file = File(fuEmuDir, "FuEmuPrefs.json")
        val fileWriter = FileWriter(file)
        fileWriter.write(json.toString(4)) // Use 4 for pretty print JSON
        fileWriter.flush()
        fileWriter.close()
        Toast.makeText(context, "数据已保存", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        Log.e("MainScreen", "Error saving preferences to JSON", e)
        Toast.makeText(context, "保存数据失败: ${e.message}", Toast.LENGTH_LONG).show()
    }
}


@Composable
fun UpdateInfoDialog(releaseInfo: GitHubReleaseInfo, onDismiss: () -> Unit, context: Context) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("发现新版本: ${releaseInfo.tagName}") },
        text = {
            SelectionContainer {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(releaseInfo.body.ifEmpty { "暂无更新日志" })
                }
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, releaseInfo.htmlUrl.toUri())
                    try {
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "打开失败: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                    onDismiss()
                }) {
                    Text("前往项目页面")
                }
                val downloadUrl = releaseInfo.assets.firstOrNull()?.browserDownloadUrl
                if (downloadUrl != null) {
                    Button(onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, downloadUrl.toUri())
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "下载链接打开失败: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                        onDismiss()
                    }) {
                        Text("立刻下载")
                    }
                }
            }
        }//,
        //dismissButton = {
        //    Button(onClick = onDismiss) {
        //        Text("暂不更新")
        //    }
        //}
    )
}


@Composable
fun SystemInfoCard(sharedPreferences: SharedPreferences) {
    var androidVersionText by remember { mutableStateOf(TextFieldValue(sharedPreferences.getString("android_version", "") ?: "")) }
    var brandText by remember { mutableStateOf(TextFieldValue(sharedPreferences.getString("brand", "") ?: "")) }
    var deviceInfoText by remember { mutableStateOf(TextFieldValue(sharedPreferences.getString("device_info", "") ?: "")) }
    var deviceNameText by remember { mutableStateOf(TextFieldValue(sharedPreferences.getString("device_name", "") ?: "")) }
    var serialNumberText by remember { mutableStateOf(TextFieldValue(sharedPreferences.getString("serial_number", "") ?: "")) }
    val context = LocalContext.current

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("设备特征", style = MaterialTheme.typography.titleLarge)
            OutlinedTextField(
                value = androidVersionText,
                onValueChange = { androidVersionText = it },
                label = { Text("Android版本") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = brandText,
                onValueChange = { brandText = it },
                label = { Text("品牌") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = deviceInfoText,
                onValueChange = { deviceInfoText = it },
                label = { Text("设备信息") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = deviceNameText,
                onValueChange = { deviceNameText = it },
                label = { Text("设备名称") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = serialNumberText,
                onValueChange = { serialNumberText = it },
                label = { Text("序列号") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    sharedPreferences.edit {
                    putString("android_version", androidVersionText.text)
                    putString("brand", brandText.text)
                    putString("device_info", deviceInfoText.text)
                    putString("device_name", deviceNameText.text)
                    putString("serial_number", serialNumberText.text)
                }
                    savePreferencesToJson(context, sharedPreferences)
                    //Toast.makeText(context, "设备特征已保存", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("保存")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GovernanceEnvironmentCard(sharedPreferences: SharedPreferences) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("Linspirer", "HEM", "Hangzhi")
    var selectedOptionText by remember { mutableStateOf(sharedPreferences.getString("governance_environment", options[0]) ?: options[0]) }
    val context = LocalContext.current

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("管控环境", style = MaterialTheme.typography.titleLarge)
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedOptionText,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("选择环境") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    options.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption) },
                            onClick = {
                                selectedOptionText = selectionOption
                                expanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                }
            }
            Button(
                onClick = {
                    sharedPreferences.edit {
                    putString("governance_environment", selectedOptionText)
                }
                    savePreferencesToJson(context, sharedPreferences)
                    //Toast.makeText(context, "管控环境已保存", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("保存")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectInfoCard() {
    val context = LocalContext.current
    val githubUrl = "https://github.com/fR0Z863xF/FuuleaEnvEmu/tree/v2"
    val projectDescription = "FuEmu是一款用于模拟特定环境的工具，旨在帮助开发者进行相关测试和开发。\n\n此项目仅为此目的提供此工具，请在遵守相关规定的情况下使用。\n\n点击卡片前往Github项目页面。"

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            val intent = Intent(Intent.ACTION_VIEW, githubUrl.toUri())
            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(context, "无法打开链接: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
//            Icon(
//                imageVector = Icons.Filled.Info,
//                contentDescription = "项目信息图标",
//                modifier = Modifier.size(40.dp)
//            )
//            Spacer(modifier = Modifier.width(16.dp))
            Column {
                //Text("项目信息", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(4.dp))
                Text(projectDescription, style = MaterialTheme.typography.bodyMedium)
            }

        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FuEmuTheme {
        MainScreen()
    }
}
