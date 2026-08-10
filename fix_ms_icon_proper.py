import re
with open("app/src/main/java/com/example/ui/screens/MainScreens.kt", "r") as f:
    content = f.read()

content = re.sub(r'IconButton\(\s*IconButton\(', r'IconButton(', content)

with open("app/src/main/java/com/example/ui/screens/MainScreens.kt", "w") as f:
    f.write(content)
