package org.example.project.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.project.ui.components.StatusBadge
import org.example.project.ui.theme.MiningSafetyColors

/**
 * Template status enum
 */
enum class TemplateStatus(val label: String, val color: androidx.compose.ui.graphics.Color) {
    ACTIVE("Active", MiningSafetyColors.Primary),
    DRAFT("Draft", MiningSafetyColors.Warning),
    ARCHIVED("Archived", MiningSafetyColors.OnSurfaceVariant)
}

/**
 * Data class for inspection template
 */
data class InspectionTemplate(
    val id: String,
    val name: String,
    val description: String,
    val questions: Int,
    val uses: Int,
    val status: TemplateStatus
)

/**
 * Templates screen with template cards
 */
@Composable
fun TemplatesScreen() {
    val templates = rememberTemplatesData()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        TemplatesHeader()
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Actions
        TemplatesActionsRow()
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Templates grid
        TemplatesGrid(templates = templates)
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Create new card
        CreateTemplateCard()
    }
}

@Composable
private fun TemplatesHeader() {
    Text(
        text = "Inspection Templates",
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold
    )
    Text(
        text = "Create and manage inspection form templates",
        style = MaterialTheme.typography.bodyMedium,
        color = MiningSafetyColors.OnSurfaceVariant
    )
}

@Composable
private fun TemplatesActionsRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = { },
            colors = ButtonDefaults.buttonColors(
                containerColor = MiningSafetyColors.Primary
            )
        ) {
            Text("➕ New Template")
        }
    }
}

@Composable
private fun TemplatesGrid(templates: List<InspectionTemplate>) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        templates.chunked(2).forEach { rowTemplates ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowTemplates.forEach { template ->
                    TemplateCard(
                        template = template,
                        modifier = Modifier.weight(1f)
                    )
                }
                // Fill remaining space if odd number
                if (rowTemplates.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun TemplateCard(
    template: InspectionTemplate,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MiningSafetyColors.Surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with icon and status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Document icon
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MiningSafetyColors.Primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "📄",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                
                // Status badge
                StatusBadge(
                    status = template.status.label,
                    color = template.status.color
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Title
            Text(
                text = template.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            // Description
            Text(
                text = template.description,
                style = MaterialTheme.typography.bodySmall,
                color = MiningSafetyColors.OnSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Stats
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TemplateStat("📝", "${template.questions} questions")
                TemplateStat("✓", "${template.uses} uses")
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MiningSafetyColors.SurfaceVariant
                    )
                ) {
                    Text(
                        "Edit",
                        color = MiningSafetyColors.OnSurface,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                
                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MiningSafetyColors.SurfaceVariant)
                ) {
                    Text("📋")
                }
            }
        }
    }
}

@Composable
private fun TemplateStat(icon: String, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = icon,
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = MiningSafetyColors.OnSurfaceVariant
        )
    }
}

@Composable
private fun CreateTemplateCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MiningSafetyColors.SurfaceVariant
        ),
        onClick = { }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "➕",
                style = MaterialTheme.typography.displayMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Create Template",
                style = MaterialTheme.typography.titleMedium,
                color = MiningSafetyColors.OnSurfaceVariant
            )
        }
    }
}

@Composable
private fun rememberTemplatesData(): List<InspectionTemplate> {
    return listOf(
        InspectionTemplate(
            id = "TPL-001",
            name = "Underground Safety Checklist",
            description = "Comprehensive safety inspection for underground mining operations",
            questions = 24,
            uses = 156,
            status = TemplateStatus.ACTIVE
        ),
        InspectionTemplate(
            id = "TPL-002",
            name = "Equipment Pre-Start Check",
            description = "Daily equipment inspection before operations begin",
            questions = 12,
            uses = 89,
            status = TemplateStatus.ACTIVE
        ),
        InspectionTemplate(
            id = "TPL-003",
            name = "Tailings Dam Inspection",
            description = "Weekly inspection of tailings dam facilities",
            questions = 18,
            uses = 34,
            status = TemplateStatus.ACTIVE
        ),
        InspectionTemplate(
            id = "TPL-004",
            name = "Emergency Drill Evaluation",
            description = "Assessment template for emergency response drills",
            questions = 15,
            uses = 12,
            status = TemplateStatus.DRAFT
        ),
        InspectionTemplate(
            id = "TPL-005",
            name = "PPE Compliance Check",
            description = "Personal protective equipment verification checklist",
            questions = 8,
            uses = 210,
            status = TemplateStatus.ACTIVE
        )
    )
}
