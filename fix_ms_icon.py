with open("app/src/main/java/com/example/ui/screens/MainScreens.kt", "r") as f:
    lines = f.readlines()
new_lines = []
for i in range(len(lines)):
    if lines[i].strip() == "IconButton(" and i+1 < len(lines) and lines[i+1].strip() == "IconButton(":
        continue
    new_lines.append(lines[i])
with open("app/src/main/java/com/example/ui/screens/MainScreens.kt", "w") as f:
    f.writelines(new_lines)
