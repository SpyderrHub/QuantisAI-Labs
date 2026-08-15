with open('app/src/main/java/com/example/ui/screens/AuthScreens.kt', 'r') as f:
    content = f.read()

content = content.replace("androidx.compose.ui.text.withStyle(", "withStyle(")

with open('app/src/main/java/com/example/ui/screens/AuthScreens.kt', 'w') as f:
    f.write(content)
