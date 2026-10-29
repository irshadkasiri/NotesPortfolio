package com.irshadkasiri.notesportfolio.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.irshadkasiri.notesportfolio.data.local.NoteEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val cardBackgrounds = listOf(
    Color(0xFFEDE8FF),
    Color(0xFFE4F5ED),
    Color(0xFFFFF0D9),
    Color(0xFFFFE8EC),
    Color(0xFFE3F2FD),
    Color(0xFFF3E5F5)
)

private val accentColors = listOf(
    Color(0xFF6E54C9),
    Color(0xFF2B8965),
    Color(0xFFB57926),
    Color(0xFFC65C75),
    Color(0xFF1E88E5),
    Color(0xFF8E24AA)
)

private val textColor = Color(0xFF17162F)

@Composable
fun NoteCard(
    note: NoteEntity,
    onNoteClick: (NoteEntity) -> Unit,
    onFavoriteClick: (NoteEntity) -> Unit,
    onEditClick: (NoteEntity) -> Unit,
    onDeleteClick: (NoteEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val colorIndex = note.colorIndex.coerceIn(0, cardBackgrounds.size - 1)
    val bgColor = cardBackgrounds[colorIndex]
    val accentColor = accentColors[colorIndex]

    val favoriteStarColor by animateColorAsState(
        targetValue = if (note.isFavorite) Color(0xFFFFB300) else Color.Gray.copy(alpha = 0.6f),
        label = "favoriteColor"
    )

    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(note.createdAt))

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onNoteClick(note) },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row: Category Badge & Favorite Star
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color.White.copy(alpha = 0.85f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = note.category,
                        color = accentColor,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                IconButton(
                    onClick = { onFavoriteClick(note) },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (note.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "Favorite",
                        tint = favoriteStarColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            // Title
            Text(
                text = note.title,
                color = textColor,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(6.dp))

            // Description
            Text(
                text = note.description,
                color = Color(0xFF555268),
                style = MaterialTheme.typography.bodySmall,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(14.dp))

            // Footer: Date and Action Buttons (Edit / Delete)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formattedDate,
                    color = Color(0xFF88859A),
                    style = MaterialTheme.typography.labelSmall
                )

                Row {
                    IconButton(
                        onClick = { onEditClick(note) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Note",
                            tint = accentColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(Modifier.width(4.dp))

                    IconButton(
                        onClick = { onDeleteClick(note) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete Note",
                            tint = accentColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
