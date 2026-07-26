sed -i '1,2d' app/src/main/java/com/example/ui/screens/MainScreens.kt
sed -i '/package com.example.ui.screens/a \import androidx.compose.material.icons.rounded.Close\nimport androidx.compose.material.icons.filled.KeyboardArrowDown' app/src/main/java/com/example/ui/screens/MainScreens.kt
