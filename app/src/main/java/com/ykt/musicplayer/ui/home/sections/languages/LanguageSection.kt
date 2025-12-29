package com.ykt.musicplayer.ui.home.sections.languages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.ykt.musicplayer.domain.model.Language
import com.ykt.musicplayer.ui.home.components.SectionHeader

@Composable
fun LanguageSection(
    title: String,
    languages: List<Language>,
    onLanguageClick: (Language) -> Unit
) {
    Column {
        SectionHeader(title)
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(languages) { lang ->
                AssistChip(
                    onClick = { onLanguageClick(lang) },
                    label = { Text(lang.name) }
                )
            }
        }
    }
}
