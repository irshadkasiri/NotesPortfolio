package com.irshadkasiri.notesportfolio.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.irshadkasiri.notesportfolio.data.local.NoteEntity

private val Purple = Color(0xFF7965E8)
private val Navy = Color(0xFF17162F)

private val cardColorOptions = listOf(
    Color(0xFFEDE8FF),
    Color(0xFFE4F5ED),
    Color(0xFFFFF0D9),
    Color(0xFFFFE8EC),
    Color(0xFFE3F2FD),
    Color(0xFFF3E5F5)
)

private val categories = listOf("Personal", "Work", "Ideas", "Journal")

@Composable
fun AddEditNoteDialog(
    noteToEdit: NoteEntity? = null,
    onDismiss: () -> Unit,
    onSaveNote: (title: String, description: String, category: String, colorIndex: Int) -> Unit
) {
    var titleState by remember(noteToEdit) { mutableStateOf(noteToEdit?.title ?: "") }
    var descriptionState by remember(noteToEdit) { mutableStateOf(noteToEdit?.description ?: "") }
    var selectedCategory by remember(noteToEdit) { mutableStateOf(noteToEdit?.category ?: "Personal") }
    var selectedColorIndex by remember(noteToEdit) { mutableIntStateOf(noteToEdit?.colorIndex ?: 0) }

    val dialogShape = remember { RoundedCornerShape(28.dp) }
    val scrollState = rememberScrollState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = remember { DialogProperties(usePlatformDefaultWidth = false) }
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 24.dp),
            shape = dialogShape,
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(scrollState)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (noteToEdit != null) "Edit Note" else "Create New Note",
                        color = Navy,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.Gray
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Title Input Field (Isolated State for Instant Typing)
                IsolatedTitleInput(
                    initialTitle = noteToEdit?.title ?: "",
                    onTitleChange = { titleState = it }
                )

                Spacer(Modifier.height(16.dp))

                // Category Selection
                Text(
                    text = "Category",
                    color = Navy,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = { Text(category) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Purple,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Color Selection
                Text(
                    text = "Theme Color",
                    color = Navy,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    cardColorOptions.forEachIndexed { index, color ->
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(color, CircleShape)
                                .border(
                                    width = if (selectedColorIndex == index) 3.dp else 0.dp,
                                    color = if (selectedColorIndex == index) Purple else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { selectedColorIndex = index }
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Description Input Field (Isolated State for Instant Typing)
                IsolatedDescriptionInput(
                    initialDescription = noteToEdit?.description ?: "",
                    onDescriptionChange = { descriptionState = it }
                )

                Spacer(Modifier.height(24.dp))

                // Actions: Save & Cancel
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = Color.Gray)
                    }

                    Spacer(Modifier.width(12.dp))

                    Button(
                        onClick = {
                            if (titleState.isNotBlank()) {
                                onSaveNote(
                                    titleState,
                                    descriptionState,
                                    selectedCategory,
                                    selectedColorIndex
                                )
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Purple)
                    ) {
                        Text(
                            text = if (noteToEdit != null) "Update Note" else "Save Note",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun IsolatedTitleInput(
    initialTitle: String,
    onTitleChange: (String) -> Unit
) {
    var text by remember(initialTitle) { mutableStateOf(initialTitle) }
    val shape = remember { RoundedCornerShape(16.dp) }
    val colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Purple,
        unfocusedBorderColor = Color(0xFFE0E0E0)
    )

    OutlinedTextField(
        value = text,
        onValueChange = {
            text = it
            onTitleChange(it)
        },
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Title") },
        placeholder = { Text("e.g. Portfolio Ideas") },
        singleLine = true,
        shape = shape,
        colors = colors
    )
}

@Composable
private fun IsolatedDescriptionInput(
    initialDescription: String,
    onDescriptionChange: (String) -> Unit
) {
    var text by remember(initialDescription) { mutableStateOf(initialDescription) }
    val shape = remember { RoundedCornerShape(16.dp) }
    val colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Purple,
        unfocusedBorderColor = Color(0xFFE0E0E0)
    )

    OutlinedTextField(
        value = text,
        onValueChange = {
            text = it
            onDescriptionChange(it)
        },
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Description") },
        placeholder = { Text("Write your thoughts...") },
        minLines = 4,
        maxLines = 8,
        shape = shape,
        colors = colors
    )
}
