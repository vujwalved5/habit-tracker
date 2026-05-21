package com.example.habittracker.presentation.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.em
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.habittracker.presentation.settings.SettingsViewModel
import com.example.habittracker.ui.theme.*

private val profileColors = listOf(
    Color(0xFFD97706), // AmberOchre
    Color(0xFFEF4444), // DangerRed
    Color(0xFF3B82F6), // Blue
    Color(0xFF10B981), // Emerald
    Color(0xFF8B5CF6), // Violet
    Color(0xFFF59E0B), // Amber
    Color(0xFF6366F1), // Indigo
    Color(0xFFEC4899), // Pink
    Color(0xFF06B6D4), // Cyan
    Color(0xFF84CC16), // Lime
)

private val profileGradients = listOf(
    listOf(Color(0xFFD97706), Color(0xFFEF4444)),
    listOf(Color(0xFF3B82F6), Color(0xFF10B981)),
    listOf(Color(0xFF8B5CF6), Color(0xFFEC4899)),
    listOf(Color(0xFFF59E0B), Color(0xFF6366F1)),
    listOf(Color(0xFF00C6FF), Color(0xFF0072FF)),
    listOf(Color(0xFFF2994A), Color(0xFFF2C94C)),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val totalHabits by viewModel.totalHabits.collectAsState()
    val totalCompletions by viewModel.totalCompletions.collectAsState()
    val longestStreak by viewModel.longestStreak.collectAsState()
    
    val userName by viewModel.userName.collectAsState()
    val profilePictureType by viewModel.profilePictureType.collectAsState()
    val profilePictureValue by viewModel.profilePictureValue.collectAsState()

    var showResetDialog by remember { mutableStateOf(false) }
    var showNameDialog by remember { mutableStateOf(false) }
    var showAvatarDialog by remember { mutableStateOf(false) }

    val photoPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.updateProfilePicture(ProfilePictureType.Image, it.toString()) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "PREFERENCES", 
                        fontSize = 9.sp, 
                        color = TextDim, 
                        letterSpacing = 0.1.em,
                        fontWeight = FontWeight.Normal
                    ) 
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = CanvasBlack
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(TileDeep)
                            .border(1.dp, AmberOchre, CircleShape)
                            .clickable { showAvatarDialog = true },
                        contentAlignment = Alignment.Center
                    ) {
                        when (profilePictureType) {
                            ProfilePictureType.Color -> {
                                val color = try { Color(profilePictureValue.toLong(16).toInt()) } catch(_: Exception) { AmberOchre }
                                Box(modifier = Modifier.fillMaxSize().background(color))
                            }
                            ProfilePictureType.Gradient -> {
                                val colors = profilePictureValue.split(",").map { 
                                    try { Color(it.toLong(16).toInt()) } catch(_: Exception) { AmberOchre }
                                }
                                Box(modifier = Modifier.fillMaxSize().background(Brush.linearGradient(colors)))
                            }
                            ProfilePictureType.Image -> {
                                AsyncImage(
                                    model = profilePictureValue,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                        
                        // Overlay edit icon
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Outlined.Edit, 
                                contentDescription = null, 
                                tint = SilverWhite.copy(alpha = 0.7f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { showNameDialog = true }
                    ) {
                        Text(
                            userName.uppercase(), 
                            fontSize = 9.sp, 
                            color = TextDim, 
                            letterSpacing = 0.1.em
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            Icons.Outlined.Edit, 
                            contentDescription = null, 
                            tint = TextDim, 
                            modifier = Modifier.size(10.dp)
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ProfileStatCard(modifier = Modifier.weight(1f), label = "HABITS", value = totalHabits.toString())
                    ProfileStatCard(modifier = Modifier.weight(1f), label = "STREAK", value = "$longestStreak")
                    ProfileStatCard(modifier = Modifier.weight(1f), label = "DONE", value = totalCompletions.toString())
                }
            }

            item {
                SectionLabel("PREFERENCES")
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    shape = SharpCard,
                    colors = CardDefaults.cardColors(containerColor = TileDeep),
                    modifier = Modifier.fillMaxWidth().border(0.5.dp, BorderSubtle, SharpCard)
                ) {
                    Column {
                        SettingsRow(
                            icon = Icons.Outlined.Person,
                            title = "Customize Profile Picture",
                            onClick = { showAvatarDialog = true }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = BorderSubtle)
                        
                        SettingsRow(
                            icon = Icons.Outlined.Badge,
                            title = "Change Name",
                            onClick = { showNameDialog = true }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = BorderSubtle)

                        SettingsRow(
                            icon = Icons.Outlined.Notifications,
                            title = "Notifications",
                            trailing = {
                                var checked by remember { mutableStateOf(true) }
                                Switch(
                                    checked = checked, 
                                    onCheckedChange = { checked = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = SilverWhite,
                                        checkedTrackColor = AmberOchre,
                                        uncheckedThumbColor = TextDim,
                                        uncheckedTrackColor = SurfaceSlate,
                                        uncheckedBorderColor = Color.Transparent
                                    )
                                )
                            }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = BorderSubtle)
                        
                        // Background row
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Background", color = SilverWhite, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            val selectedStyle by settingsViewModel.selectedBackgroundStyle.collectAsState()
                            val customUri by settingsViewModel.customBackgroundUri.collectAsState()
                            
                            val photoPicker = rememberLauncherForActivityResult(
                                ActivityResultContracts.GetContent()
                            ) { uri ->
                                uri?.let { settingsViewModel.setCustomBackgroundUri(it.toString()) }
                            }

                            val styles = listOf(
                                BackgroundStyle.Pure,
                                BackgroundStyle.GeometricGrid,
                                BackgroundStyle.DiagonalLines,
                                BackgroundStyle.Topographic,
                                BackgroundStyle.Custom
                            )

                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(styles) { style ->
                                    BackgroundTile(
                                        style = style,
                                        isSelected = selectedStyle == style,
                                        customUri = if (style == BackgroundStyle.Custom) customUri else null,
                                        onClick = {
                                            if (style == BackgroundStyle.Custom) {
                                                photoPicker.launch("image/*")
                                            } else {
                                                settingsViewModel.setBackgroundStyle(style)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Card(
                    shape = SharpCard,
                    colors = CardDefaults.cardColors(containerColor = TileWarn),
                    modifier = Modifier.fillMaxWidth().border(0.5.dp, DangerRed.copy(alpha = 0.5f), SharpCard)
                ) {
                    SettingsRow(
                        icon = Icons.Outlined.RestartAlt,
                        title = "Reset all habits",
                        titleColor = DangerRed,
                        onClick = { showResetDialog = true }
                    )
                }
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            containerColor = TileDeep,
            titleContentColor = SilverWhite,
            textContentColor = TextMuted,
            title = { Text("Reset All Habits?") },
            text = { Text("This will permanently delete all your habits and progress.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.resetAllHabits()
                    showResetDialog = false
                }) {
                    Text("Reset", color = DangerRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel", color = SilverWhite)
                }
            }
        )
    }

    if (showNameDialog) {
        var tempName by remember { mutableStateOf(userName) }
        AlertDialog(
            onDismissRequest = { showNameDialog = false },
            containerColor = TileDeep,
            titleContentColor = SilverWhite,
            title = { Text("Edit Name") },
            text = {
                OutlinedTextField(
                    value = tempName,
                    onValueChange = { tempName = it },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = SilverWhite,
                        unfocusedTextColor = SilverWhite,
                        cursorColor = AmberOchre,
                        focusedBorderColor = AmberOchre,
                        unfocusedBorderColor = BorderSubtle
                    ),
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.updateUserName(tempName)
                    showNameDialog = false
                }) {
                    Text("Save", color = AmberOchre)
                }
            },
            dismissButton = {
                TextButton(onClick = { showNameDialog = false }) {
                    Text("Cancel", color = SilverWhite)
                }
            }
        )
    }

    if (showAvatarDialog) {
        AlertDialog(
            onDismissRequest = { showAvatarDialog = false },
            containerColor = TileDeep,
            titleContentColor = SilverWhite,
            title = { Text("Customize Profile Picture") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Colors", color = TextDim, fontSize = 12.sp)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(profileColors) { color ->
                            val colorHex = color.toArgb().toUInt().toString(16).uppercase()
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        if (profilePictureType == ProfilePictureType.Color && profilePictureValue == colorHex) 
                                            2.dp else 0.dp, 
                                        SilverWhite, 
                                        CircleShape
                                    )
                                    .clickable {
                                        viewModel.updateProfilePicture(ProfilePictureType.Color, colorHex)
                                    }
                            )
                        }
                    }
                    
                    Text("Gradients", color = TextDim, fontSize = 12.sp)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(profileGradients) { colors ->
                            val gradientValue = colors.joinToString(",") { it.toArgb().toUInt().toString(16).uppercase() }
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Brush.linearGradient(colors))
                                    .border(
                                        if (profilePictureType == ProfilePictureType.Gradient && profilePictureValue == gradientValue) 
                                            2.dp else 0.dp, 
                                        SilverWhite, 
                                        CircleShape
                                    )
                                    .clickable {
                                        viewModel.updateProfilePicture(ProfilePictureType.Gradient, gradientValue)
                                    }
                            )
                        }
                    }

                    Text("Other", color = TextDim, fontSize = 12.sp)
                    Button(
                        onClick = { 
                            photoPicker.launch("image/*")
                            showAvatarDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceSlate),
                        shape = SharpCard,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Upload Image", color = SilverWhite)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAvatarDialog = false }) {
                    Text("Done", color = AmberOchre)
                }
            }
        )
    }
}

@Composable
fun SectionLabel(text: String) {
    Text(
        text = text,
        fontSize = 9.sp,
        color = TextDim,
        letterSpacing = 0.1.em,
        fontWeight = FontWeight.Normal
    )
}

@Composable
fun ProfileStatCard(modifier: Modifier, label: String, value: String) {
    Card(
        modifier = modifier.border(0.5.dp, BorderSubtle, SharpCard),
        shape = SharpCard,
        colors = CardDefaults.cardColors(containerColor = TileDeep)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AmberOchre)
            Text(label, fontSize = 8.sp, color = TextDim, letterSpacing = 0.05.em)
        }
    }
}

@Composable
fun SettingsRow(
    icon: ImageVector,
    title: String,
    titleColor: Color = SilverWhite,
    trailing: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = if (titleColor == SilverWhite) TextMuted else titleColor)
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, modifier = Modifier.weight(1f), color = titleColor, fontWeight = FontWeight.Medium)
        trailing?.invoke()
    }
}

@Composable
fun BackgroundTile(
    style: BackgroundStyle,
    isSelected: Boolean,
    customUri: String?,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(width = 60.dp, height = 80.dp)
            .clip(SharpCard)
            .background(CanvasBlack)
            .border(
                width = if (isSelected) 2.dp else 0.5.dp,
                color = if (isSelected) AmberOchre else BorderSubtle,
                shape = SharpCard
            )
            .clickable { onClick() }
    ) {
        when (style) {
            BackgroundStyle.Pure -> { }
            BackgroundStyle.GeometricGrid -> {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val step = 10.dp.toPx()
                    for (x in 0..(size.width / step).toInt()) {
                        drawLine(BorderSubtle, Offset(x * step, 0f), Offset(x * step, size.height), 0.5.dp.toPx())
                    }
                    for (y in 0..(size.height / step).toInt()) {
                        drawLine(BorderSubtle, Offset(0f, y * step), Offset(size.width, y * step), 0.5.dp.toPx())
                    }
                }
            }
            BackgroundStyle.DiagonalLines -> {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val step = 8.dp.toPx()
                    var x = -size.height
                    while (x < size.width) {
                        drawLine(BorderSubtle, Offset(x, 0f), Offset(x + size.height, size.height), 0.5.dp.toPx())
                        x += step
                    }
                }
            }
            BackgroundStyle.Topographic -> {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(BorderSubtle, radius = 20.dp.toPx(), center = Offset(size.width, size.height), style = androidx.compose.ui.graphics.drawscope.Stroke(0.5.dp.toPx()))
                    drawCircle(BorderSubtle, radius = 30.dp.toPx(), center = Offset(size.width, size.height), style = androidx.compose.ui.graphics.drawscope.Stroke(0.5.dp.toPx()))
                    drawCircle(BorderSubtle, radius = 40.dp.toPx(), center = Offset(size.width, size.height), style = androidx.compose.ui.graphics.drawscope.Stroke(0.5.dp.toPx()))
                }
            }
            BackgroundStyle.Custom -> {
                if (customUri != null) {
                    AsyncImage(
                        model = customUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        alpha = 0.5f
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("+", color = TextDim, fontSize = 24.sp)
                    }
                }
            }
        }
    }
}
