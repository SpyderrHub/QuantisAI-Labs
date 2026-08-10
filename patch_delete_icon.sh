cat << 'REPLACE' > replacement_delete.txt
                                        IconButton(
                                            onClick = {
                                                scope.launch {
                                                    historyManager.deleteHistoryItem(user?.uid, item)
                                                    recentGenerations = historyManager.getLocalHistory()
                                                    android.widget.Toast.makeText(context, "Audio deleted successfully", android.widget.Toast.LENGTH_SHORT).show()
                                                }
                                            },
                                            modifier = Modifier
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.errorContainer)
                                                .size(36.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.Delete,
                                                contentDescription = "Delete",
                                                tint = MaterialTheme.colorScheme.onErrorContainer,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        IconButton(
REPLACE
awk '
/Spacer\(modifier = Modifier.width\(8.dp\)\)/ {
    if (in_row && !done) {
        while ((getline line < "replacement_delete.txt") > 0) {
            print line
        }
        done = 1
        next
    }
}
{
    if ($0 ~ /Row\(/) {
        in_row = 1
    }
    print
}
' app/src/main/java/com/example/ui/screens/MainScreens.kt > temp.kt && mv temp.kt app/src/main/java/com/example/ui/screens/MainScreens.kt
