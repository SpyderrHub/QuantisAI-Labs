#!/bin/bash
sed -i '1,2d' app/src/main/java/com/example/ui/screens/AuthScreens.kt
sed -i '/package com.example.ui.screens/a import androidx.compose.foundation.text.InlineTextContent\nimport androidx.compose.foundation.text.appendInlineContent' app/src/main/java/com/example/ui/screens/AuthScreens.kt
