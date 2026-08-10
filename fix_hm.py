with open("app/src/main/java/com/example/data/HistoryManager.kt", "r") as f:
    content = f.read()
import re
content = re.sub(r'suspend fun fetchHistorysuspend fun fetchHistory item\.audioUrl\.isNotEmpty\(\)\)', r'&& item.audioUrl.isNotEmpty())', content)
with open("app/src/main/java/com/example/data/HistoryManager.kt", "w") as f:
    f.write(content)
