import re

with open('app/src/main/java/com/example/ui/screens/AuthScreens.kt', 'r') as f:
    content = f.read()

target = """                    Text(
                        text = "I accept the Terms and Policy",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyMedium
                    )"""

replacement = """                    val annotatedString = androidx.compose.ui.text.buildAnnotatedString {
                        append("I accept the ")
                        pushStringAnnotation(tag = "TERMS", annotation = "https://example.com/terms")
                        androidx.compose.ui.text.withStyle(style = androidx.compose.ui.text.SpanStyle(color = Color.Black, textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline)) {
                            append("Terms")
                        }
                        pop()
                        append(" and ")
                        pushStringAnnotation(tag = "POLICY", annotation = "https://example.com/policy")
                        androidx.compose.ui.text.withStyle(style = androidx.compose.ui.text.SpanStyle(color = Color.Black, textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline)) {
                            append("Policy")
                        }
                        pop()
                    }
                    val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
                    androidx.compose.foundation.text.ClickableText(
                        text = annotatedString,
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                        onClick = { offset ->
                            annotatedString.getStringAnnotations(tag = "TERMS", start = offset, end = offset)
                                .firstOrNull()?.let { uriHandler.openUri(it.item) }
                            annotatedString.getStringAnnotations(tag = "POLICY", start = offset, end = offset)
                                .firstOrNull()?.let { uriHandler.openUri(it.item) }
                        }
                    )"""

content = content.replace(target, replacement, 1)

target2 = """                        Text(
                            text = "I accept the Terms and Policy",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodyMedium
                        )"""
                        
replacement2 = """                        val annotatedString = androidx.compose.ui.text.buildAnnotatedString {
                            append("I accept the ")
                            pushStringAnnotation(tag = "TERMS", annotation = "https://example.com/terms")
                            androidx.compose.ui.text.withStyle(style = androidx.compose.ui.text.SpanStyle(color = Color.Black, textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline)) {
                                append("Terms")
                            }
                            pop()
                            append(" and ")
                            pushStringAnnotation(tag = "POLICY", annotation = "https://example.com/policy")
                            androidx.compose.ui.text.withStyle(style = androidx.compose.ui.text.SpanStyle(color = Color.Black, textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline)) {
                                append("Policy")
                            }
                            pop()
                        }
                        val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
                        androidx.compose.foundation.text.ClickableText(
                            text = annotatedString,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                            onClick = { offset ->
                                annotatedString.getStringAnnotations(tag = "TERMS", start = offset, end = offset)
                                    .firstOrNull()?.let { uriHandler.openUri(it.item) }
                                annotatedString.getStringAnnotations(tag = "POLICY", start = offset, end = offset)
                                    .firstOrNull()?.let { uriHandler.openUri(it.item) }
                            }
                        )"""
                        
content = content.replace(target2, replacement2, 1)

with open('app/src/main/java/com/example/ui/screens/AuthScreens.kt', 'w') as f:
    f.write(content)
