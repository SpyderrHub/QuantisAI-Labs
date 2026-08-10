import urllib.request
import os

url = "https://github.com/google/fonts/raw/main/ofl/rubikwetpaint/RubikWetPaint-Regular.ttf"
os.makedirs("app/src/main/res/font", exist_ok=True)
try:
    urllib.request.urlretrieve(url, "app/src/main/res/font/rubik_wet_paint.ttf")
    print("Font downloaded successfully.")
except Exception as e:
    print(f"Error downloading font: {e}")
