sed -i 's/selectedHistoryItemForPreview!!/item/g' app/src/main/java/com/example/ui/screens/MainScreens.kt
sed -i 's/if (selectedHistoryItemForPreview != null) {/val item = selectedHistoryItemForPreview\n    if (item != null) {/g' app/src/main/java/com/example/ui/screens/MainScreens.kt
sed -i 's/allVoices.filter { userProfile!!.savedVoices.contains(it.voiceName) }/allVoices.filter { profile.savedVoices.contains(it.voiceName) }/g' app/src/main/java/com/example/ui/screens/MainScreens.kt
sed -i 's/if (userProfile != null && selectedVoice == null && allVoices.isNotEmpty()) {/val profile = userProfile\n        if (profile != null \&\& selectedVoice == null \&\& allVoices.isNotEmpty()) {/g' app/src/main/java/com/example/ui/screens/MainScreens.kt
sed -i 's/if (user != null && userProfile != null) {/val profile = userProfile\n                        if (user != null \&\& profile != null) {/g' app/src/main/java/com/example/ui/screens/MainScreens.kt
sed -i 's/val currentSaved = userProfile!!.savedVoices.toMutableList()/val currentSaved = profile.savedVoices.toMutableList()/g' app/src/main/java/com/example/ui/screens/MainScreens.kt
sed -i 's/val newProfile = userProfile!!.copy(savedVoices = currentSaved)/val newProfile = profile.copy(savedVoices = currentSaved)/g' app/src/main/java/com/example/ui/screens/MainScreens.kt
sed -i 's/text = sttResultText!!/text = sttResultText ?: ""/g' app/src/main/java/com/example/ui/screens/MainScreens.kt
sed -i 's/audioUrl = generatedAudioUrl!!/audioUrl = generatedAudioUrl ?: ""/g' app/src/main/java/com/example/ui/screens/MainScreens.kt
sed -i 's/val updatedProfile = currentProfile!!.copy(avatarUrl = localFileUri)/val updatedProfile = currentProfile?.copy(avatarUrl = localFileUri)/g' app/src/main/java/com/example/ui/screens/MainScreens.kt
sed -i 's/currentProfile = updatedProfile/if (updatedProfile != null) currentProfile = updatedProfile/g' app/src/main/java/com/example/ui/screens/MainScreens.kt
