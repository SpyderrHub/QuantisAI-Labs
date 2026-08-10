#!/bin/bash
sed -i '1i import androidx.compose.foundation.text.appendInlineContent' app/src/main/java/com/example/ui/screens/AuthScreens.kt
sed -i '1i import androidx.compose.foundation.text.InlineTextContent' app/src/main/java/com/example/ui/screens/AuthScreens.kt

cat << 'REPLACE' > replacement2.txt
                val text = androidx.compose.ui.text.buildAnnotatedString {
                    if (step == 0) {
                        append("Transform text ")
                        appendInlineContent("avatar1", "[avatar1]")
                        append(" into lifelike speech ")
                        appendInlineContent("avatar2", "[avatar2]")
                        append(" instantly.")
                    } else if (step == 1) {
                        append("Customize ")
                        appendInlineContent("avatar3", "[avatar3]")
                        append(" and craft the perfect voice with granular controls.")
                    } else {
                        append("Reach a global ")
                        appendInlineContent("avatar4", "[avatar4]")
                        append(" audience with hundreds of languages.")
                    }
                }
                
                val inlineContent = mapOf(
                    "avatar1" to InlineTextContent(
                        androidx.compose.ui.text.Placeholder(
                            width = 48.sp,
                            height = 48.sp,
                            placeholderVerticalAlign = androidx.compose.ui.text.PlaceholderVerticalAlign.Center
                        )
                    ) {
                        coil.compose.AsyncImage(
                            model = "https://i.pravatar.cc/150?img=1",
                            contentDescription = null,
                            modifier = Modifier.padding(horizontal = 4.dp).fillMaxSize().clip(CircleShape).border(2.dp, Color.White, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    },
                    "avatar2" to InlineTextContent(
                        androidx.compose.ui.text.Placeholder(
                            width = 48.sp,
                            height = 48.sp,
                            placeholderVerticalAlign = androidx.compose.ui.text.PlaceholderVerticalAlign.Center
                        )
                    ) {
                        coil.compose.AsyncImage(
                            model = "https://i.pravatar.cc/150?img=5",
                            contentDescription = null,
                            modifier = Modifier.padding(horizontal = 4.dp).fillMaxSize().clip(CircleShape).border(2.dp, Color.White, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    },
                    "avatar3" to InlineTextContent(
                        androidx.compose.ui.text.Placeholder(
                            width = 48.sp,
                            height = 48.sp,
                            placeholderVerticalAlign = androidx.compose.ui.text.PlaceholderVerticalAlign.Center
                        )
                    ) {
                        coil.compose.AsyncImage(
                            model = "https://i.pravatar.cc/150?img=9",
                            contentDescription = null,
                            modifier = Modifier.padding(horizontal = 4.dp).fillMaxSize().clip(CircleShape).border(2.dp, Color.White, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    },
                    "avatar4" to InlineTextContent(
                        androidx.compose.ui.text.Placeholder(
                            width = 48.sp,
                            height = 48.sp,
                            placeholderVerticalAlign = androidx.compose.ui.text.PlaceholderVerticalAlign.Center
                        )
                    ) {
                        coil.compose.AsyncImage(
                            model = "https://i.pravatar.cc/150?img=12",
                            contentDescription = null,
                            modifier = Modifier.padding(horizontal = 4.dp).fillMaxSize().clip(CircleShape).border(2.dp, Color.White, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                )

                Text(
                    text = text,
                    inlineContent = inlineContent,
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    lineHeight = 52.sp
                )
            }
REPLACE

awk '
BEGIN { skip = 0 }
/val text = androidx.compose.ui.text.buildAnnotatedString/ {
    if (!done) {
        while ((getline line < "replacement2.txt") > 0) {
            print line
        }
        skip = 1
        done = 1
    }
}
/lineHeight = 52.sp/ {
    if (skip) {
        getline
        skip = 0
        next
    }
}
!skip { print }
' app/src/main/java/com/example/ui/screens/AuthScreens.kt > temp2.kt && mv temp2.kt app/src/main/java/com/example/ui/screens/AuthScreens.kt
