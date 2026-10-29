package com.irshadkasiri.notesportfolio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.irshadkasiri.notesportfolio.data.local.NoteEntity
import com.irshadkasiri.notesportfolio.ui.NoteViewModel
import com.irshadkasiri.notesportfolio.ui.components.AddEditNoteDialog
import com.irshadkasiri.notesportfolio.ui.components.NoteCard
import com.irshadkasiri.notesportfolio.ui.theme.NotesPortfolioTheme

private val Navy = Color(0xFF17162F)
private val Purple = Color(0xFF7965E8)
private val Page = Color(0xFFF7F7FB)
private val Muted = Color(0xFF88879A)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NotesPortfolioTheme {
                val viewModel: NoteViewModel = viewModel(
                    factory = NoteViewModel.Factory(application)
                )
                NotesHomeScreen(viewModel)
            }
        }
    }
}

@Composable
fun NotesHomeScreen(viewModel: NoteViewModel) {
    val notes by viewModel.notes.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val uiMessage by viewModel.uiMessage.collectAsState()
    val editingNote by viewModel.editingNote.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Show toast / snackbar when UI message updates
    LaunchedEffect(uiMessage) {
        uiMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        containerColor = Page,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                contentColor = Purple
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { viewModel.setTab(0) },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") }
                )

                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { viewModel.setTab(1) },
                    icon = { Icon(Icons.Default.StarBorder, contentDescription = "Favorites") },
                    label = { Text("Favorites") }
                )

                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { viewModel.setTab(2) },
                    icon = { Icon(Icons.Default.Info, contentDescription = "Stats & Info") },
                    label = { Text("Info") }
                )
            }
        },
        floatingActionButton = {
            if (selectedTab != 2) {
                FloatingActionButton(
                    onClick = {
                        viewModel.startEditingNote(null)
                        showAddDialog = true
                    },
                    containerColor = Purple,
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Note")
                }
            }
        }
    ) { padding ->

        if (showAddDialog || editingNote != null) {
            AddEditNoteDialog(
                noteToEdit = editingNote,
                onDismiss = {
                    showAddDialog = false
                    viewModel.startEditingNote(null)
                },
                onSaveNote = { title, description, category, colorIndex ->
                    viewModel.saveNote(title, description, category, colorIndex)
                    showAddDialog = false
                    viewModel.startEditingNote(null)
                }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {

            Spacer(Modifier.height(16.dp))

            // Header banner
            HeaderBanner(
                totalNotes = notes.size,
                selectedTab = selectedTab
            )

            Spacer(Modifier.height(24.dp))

            when (selectedTab) {
                0, 1 -> {
                    // Search bar
                    Text(
                        text = if (selectedTab == 1) "Search Favorites" else "Find your ideas",
                        color = Navy,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Search your notes...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(18.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White,
                            unfocusedBorderColor = Color(0xFFE8E6F0),
                            focusedBorderColor = Purple
                        )
                    )

                    Spacer(Modifier.height(20.dp))

                    if (selectedTab == 0) {
                        // Category Filters
                        Text(
                            text = "Categories",
                            color = Navy,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(10.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("All Notes", "Personal", "Work", "Ideas", "Journal").forEach { category ->
                                FilterChip(
                                    selected = selectedCategory == category,
                                    onClick = { viewModel.setCategory(category) },
                                    label = { Text(category) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Purple,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }

                        Spacer(Modifier.height(20.dp))
                    }

                    // Section Title
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (selectedTab == 1) "Favorite Notes" else "Your Notes",
                            color = Navy,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "${notes.size} notes",
                            color = Muted,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(Modifier.height(14.dp))

                    if (notes.isEmpty()) {
                        EmptyStateView(
                            tab = selectedTab,
                            searchQuery = searchQuery,
                            selectedCategory = selectedCategory
                        )
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 2000.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            userScrollEnabled = false
                        ) {
                            items(notes, key = { it.id }) { note ->
                                NoteCard(
                                    note = note,
                                    onNoteClick = { viewModel.startEditingNote(it) },
                                    onFavoriteClick = { viewModel.toggleFavorite(it) },
                                    onEditClick = { viewModel.startEditingNote(it) },
                                    onDeleteClick = { viewModel.deleteNote(it) }
                                )
                            }
                        }
                    }
                }

                2 -> {
                    // App Stats & Info Tab
                    val favoriteNotesCount = notes.count { it.isFavorite }
                    InfoAndStatsSection(
                        totalNotes = notes.size,
                        favoriteNotesCount = favoriteNotesCount
                    )
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun HeaderBanner(totalNotes: Int, selectedTab: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(
                    listOf(Navy, Color(0xFF38316B))
                ),
                RoundedCornerShape(28.dp)
            )
            .padding(24.dp)
    ) {
        Text(
            text = "NOTESPORTFOLIO",
            color = Color(0xFFBDB4FF),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = when (selectedTab) {
                1 -> "Your Favorites ⭐"
                2 -> "Portfolio Analytics 📊"
                else -> "Welcome Back 👋"
            },
            color = Color(0xFFD7D4E8),
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = when (selectedTab) {
                1 -> "Starred thoughts,\nalways accessible."
                2 -> "Clean architecture,\npowered by Room & MVVM."
                else -> "Your thoughts,\nstored securely on-device."
            },
            color = Color.White,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = "$totalNotes notes in view",
            color = Color(0xFFD7D4E8),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun EmptyStateView(
    tab: Int,
    searchQuery: String,
    selectedCategory: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "📝",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = when {
                    searchQuery.isNotBlank() -> "No notes found matching '$searchQuery'"
                    tab == 1 -> "No favorite notes added yet"
                    selectedCategory != "All Notes" -> "No notes in '$selectedCategory'"
                    else -> "No notes yet. Tap + to add one!"
                },
                color = Navy,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "All notes are saved locally in Room database.",
                color = Muted,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun InfoAndStatsSection(totalNotes: Int, favoriteNotesCount: Int) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Developer Profile & Portfolio Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            Brush.linearGradient(
                                listOf(Purple, Color(0xFF5B45C6))
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "IK",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.width(16.dp))

                Column {
                    Text(
                        text = "Irshad Kasiri",
                        color = Navy,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(2.dp))

                    Text(
                        text = "Android Developer Portfolio",
                        color = Muted,
                        style = MaterialTheme.typography.bodySmall
                    )

                    Spacer(Modifier.height(6.dp))

                    Box(
                        modifier = Modifier
                            .background(Color(0xFFEDE8FF), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "v1.0.0 • Room & MVVM",
                            color = Purple,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Analytics 2x2 Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                icon = "📝",
                title = "Total Notes",
                value = "$totalNotes",
                bgColor = Color(0xFFEDE8FF),
                accentColor = Color(0xFF6E54C9)
            )

            StatCard(
                modifier = Modifier.weight(1f),
                icon = "⭐",
                title = "Favorites",
                value = "$favoriteNotesCount",
                bgColor = Color(0xFFFFF0D9),
                accentColor = Color(0xFFB57926)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                icon = "🗄️",
                title = "Database",
                value = "Room DB",
                bgColor = Color(0xFFE4F5ED),
                accentColor = Color(0xFF2B8965)
            )

            StatCard(
                modifier = Modifier.weight(1f),
                icon = "⚡",
                title = "Architecture",
                value = "MVVM Flow",
                bgColor = Color(0xFFE3F2FD),
                accentColor = Color(0xFF1E88E5)
            )
        }

        // Technical Architecture Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Technical Stack",
                    color = Navy,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(14.dp))

                TechItemRow(
                    icon = "🛡️",
                    title = "Room Database",
                    subtitle = "Local persistent storage with Coroutine Flow"
                )

                Spacer(Modifier.height(12.dp))

                TechItemRow(
                    icon = "🏗️",
                    title = "MVVM Architecture",
                    subtitle = "Clean Repository pattern & ViewModel state"
                )

                Spacer(Modifier.height(12.dp))

                TechItemRow(
                    icon = "🎨",
                    title = "Jetpack Compose",
                    subtitle = "100% declarative UI with Material 3 design"
                )

                Spacer(Modifier.height(12.dp))

                TechItemRow(
                    icon = "🔄",
                    title = "Kotlin StateFlow",
                    subtitle = "Reactive real-time asynchronous data streams"
                )
            }
        }

        // App Capabilities Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "App Features",
                    color = Navy,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(12.dp))

                FeatureCheckRow("Live Instant Search across Titles & Descriptions")
                FeatureCheckRow("Category-based Filtering (Personal, Work, Ideas, Journal)")
                FeatureCheckRow("Favorite Bookmark & Quick Favorites Tab")
                FeatureCheckRow("Custom Color-coded Note Cards & Picker")
                FeatureCheckRow("Offline-First Auto Save & Edit Support")
            }
        }

        // Footer
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Crafted with ❤️ by Irshad Kasiri",
                color = Muted,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "NotesPortfolio App • Android Jetpack",
                color = Purple,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    icon: String,
    title: String,
    value: String,
    bgColor: Color,
    accentColor: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = icon, style = MaterialTheme.typography.titleLarge)

            Spacer(Modifier.height(8.dp))

            Text(
                text = value,
                color = accentColor,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(2.dp))

            Text(
                text = title,
                color = Navy,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun TechItemRow(icon: String, title: String, subtitle: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color(0xFFF0EFF8), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = icon, style = MaterialTheme.typography.titleMedium)
        }

        Spacer(Modifier.width(14.dp))

        Column {
            Text(
                text = title,
                color = Navy,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = subtitle,
                color = Muted,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun FeatureCheckRow(featureText: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "✓",
            color = Purple,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.width(10.dp))

        Text(
            text = featureText,
            color = Navy,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
