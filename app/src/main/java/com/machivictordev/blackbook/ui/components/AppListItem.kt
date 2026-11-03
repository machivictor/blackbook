package com.machivictordev.blackbook.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.machivictordev.blackbook.R

@Composable
fun AppListItem(
    onClick: () -> Unit,
    labelText: String,
    supportingText: String?,
    trailingText : String?,
    showChevron: Boolean = false,
    showLeadingIcon: Boolean = true,
    showTrailingSurface: Boolean = true,
    trailingTextColor: Color? = null,
    trailingSurfaceContainerColor: Color? = null,
) {

    ListItem(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .clickable { onClick() },
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        leadingContent = if (showLeadingIcon) {
            {
                Surface(
                    modifier = Modifier.size(44.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            painter = painterResource(R.drawable.person),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        } else null,
        headlineContent = {
            Column() {

                Text(
                    text = labelText,
                    modifier = Modifier.padding(bottom = 2.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 18.sp
                    )
                )

                if (supportingText != null) {
                    Spacer(modifier = Modifier.size(2.dp))

                    Text(
                        text = supportingText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 15.sp,
                        )
                    )
                }
            }
        },
        trailingContent = if (trailingText != null || showChevron) {
            {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (trailingText != null) {
                        if (showTrailingSurface) {
                            val defaultContainer = MaterialTheme.colorScheme.errorContainer
                            val containerColor = trailingSurfaceContainerColor ?: defaultContainer
                            val defaultTextColor = if (containerColor == MaterialTheme.colorScheme.primaryContainer) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.error
                            }
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = containerColor
                            ) {
                                Text(
                                    text = trailingText,
                                    color = trailingTextColor ?: defaultTextColor,
                                    modifier = Modifier.padding(vertical = 4.dp, horizontal = 10.dp),
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        } else {
                            Text(
                                text = trailingText,
                                color = trailingTextColor ?: MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }

                    if (showChevron) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        } else null
    )
}