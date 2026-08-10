import urllib.request
import json
import os
import sys
# Just use a known URL if possible, or construct it
# Jersey 10 Charted TTF URL:
url = "https://github.com/google/fonts/raw/main/ofl/jersey10charted/Jersey10Charted-Regular.ttf"
os.makedirs("app/src/main/res/font", exist_ok=True)
try:
    urllib.request.urlretrieve(url, "app/src/main/res/font/jersey_10_charted.ttf")
    print("Font downloaded successfully.")
except Exception as e:
    print(f"Error downloading font: {e}")
