cat << 'REPLACE' > replacement_font.txt
package com.example.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.R

val Jersey10Charted = FontFamily(
    Font(R.font.jersey_10_charted, FontWeight.Normal)
)
REPLACE
cat replacement_font.txt > app/src/main/java/com/example/ui/theme/Typography.kt
