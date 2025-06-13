package com.fr0z863xf.FuEmu

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fr0z863xf.FuEmu.ui.theme.FuEmuTheme

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

@Preview(showBackground = true)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    val sharedPreferences = remember { context.getSharedPreferences("FuEmuPrefs", Context.MODE_PRIVATE) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                val packageName = "com.fuulea.venus.g"
                val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
                if (launchIntent != null) {
                    try {
                        context.startActivity(launchIntent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Could not launch $packageName: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(context, "App not found: $packageName", Toast.LENGTH_SHORT).show()
                }
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
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SystemInfoCard(sharedPreferences)
            GovernanceEnvironmentCard(sharedPreferences)
            ProjectInfoCard()
        }
    }
}

@Composable
fun SystemInfoCard(sharedPreferences: SharedPreferences) {
    var systemNameText by remember { mutableStateOf(TextFieldValue(sharedPreferences.getString("system_name", "") ?: "")) }
    var brandText by remember { mutableStateOf(TextFieldValue(sharedPreferences.getString("brand", "") ?: "")) }
    var deviceInfoText by remember { mutableStateOf(TextFieldValue(sharedPreferences.getString("device_info", "") ?: "")) }
    var deviceNameText by remember { mutableStateOf(TextFieldValue(sharedPreferences.getString("device_name", "") ?: "")) }
    val context = LocalContext.current

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("设备特征", style = MaterialTheme.typography.titleLarge)
            OutlinedTextField(
                value = systemNameText,
                onValueChange = { systemNameText = it },
                label = { Text("系统名称") },
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
            Button(
                onClick = {
                    with(sharedPreferences.edit()) {
                        putString("system_name", systemNameText.text)
                        putString("brand", brandText.text)
                        putString("device_info", deviceInfoText.text)
                        putString("device_name", deviceNameText.text)
                        apply()
                    }
                    Toast.makeText(context, "设备特征已保存", Toast.LENGTH_SHORT).show()
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
                    with(sharedPreferences.edit()) {
                        putString("governance_environment", selectedOptionText)
                        apply()
                    }
                    Toast.makeText(context, "管控环境已保存", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("保存")
            }
        }
    }
}

@Composable
fun ProjectInfoCard() {
    val context = LocalContext.current
    val githubUrl = "https://github.com/fR0Z863xF/FuuleaEnvEmu/tree/v2"

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("项目信息", style = MaterialTheme.typography.titleLarge, modifier = Modifier.align(Alignment.Start))
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(githubUrl))
                try {
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, "Could not open URL: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text("跳转到 GitHub")
            }
        }
    }
}
