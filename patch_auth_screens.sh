#!/bin/bash
cat << 'REPLACE' > replacement.txt
                val text = androidx.compose.ui.text.buildAnnotatedString {
                    if (step == 0) {
                        append("Transform text ")
                        androidx.compose.foundation.text.appendInlineContent("avatar1", "[avatar1]")
                        append(" into lifelike speech ")
                        androidx.compose.foundation.text.appendInlineContent("avatar2", "[avatar2]")
                        append(" instantly.")
                    } else if (step == 1) {
                        append("Customize ")
                        androidx.compose.foundation.text.appendInlineContent("avatar3", "[avatar3]")
                        append(" and craft the perfect voice with granular controls.")
                    } else {
                        append("Reach a global ")
                        androidx.compose.foundation.text.appendInlineContent("avatar4", "[avatar4]")
                        append(" audience with hundreds of languages.")
                    }
                }
                
                val inlineContent = mapOf(
                    "avatar1" to androidx.compose.foundation.text.InlineTextContent(
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
                    "avatar2" to androidx.compose.foundation.text.InlineTextContent(
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
                    "avatar3" to androidx.compose.foundation.text.InlineTextContent(
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
                    "avatar4" to androidx.compose.foundation.text.InlineTextContent(
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
REPLACE

awk '
NR==198 {
    while ((getline line < "replacement.txt") > 0) {
        print line
    }
    skip = 1
}
NR==210 {
    skip = 0
    next
}
!skip { print }
' app/src/main/java/com/example/ui/screens/AuthScreens.kt > temp.kt && mv temp.kt app/src/main/java/com/example/ui/screens/AuthScreens.kt
