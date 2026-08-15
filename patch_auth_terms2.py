with open('app/src/main/java/com/example/ui/screens/AuthScreens.kt', 'r') as f:
    content = f.read()

content = content.replace("import androidx.compose.ui.text.style.TextAlign", "import androidx.compose.ui.text.style.TextAlign\nimport androidx.compose.ui.text.withStyle")

with open('app/src/main/java/com/example/ui/screens/AuthScreens.kt', 'w') as f:
    f.write(content)
