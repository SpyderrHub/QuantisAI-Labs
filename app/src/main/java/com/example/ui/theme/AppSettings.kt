package com.example.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

data class AppSettings(
    val language: String = "English",
    val theme: String = "default"
)

val LocalAppSettings = compositionLocalOf { AppSettings() }
val LocalAppSettingsUpdater = compositionLocalOf<(AppSettings) -> Unit> { {} }

fun getThemePrimaryColor(themeName: String): Color {
    return when (themeName.lowercase()) {
        "white" -> Color(0xFFF8FAFC)
        "blue" -> Color(0xFF3B82F6)
        "red" -> Color(0xFFEF4444)
        "orange" -> Color(0xFFF97316)
        "gray" -> Color(0xFF94A3B8)
        "yellow" -> Color(0xFFEAB308)
        "purple" -> Color(0xFFA855F7)
        "green" -> Color(0xFF10B981)
        "pink" -> Color(0xFFEC4899)
        else -> Color(0xFFA855F7) // Default
    }
}

fun getThemeSecondaryColor(themeName: String): Color {
    return when (themeName.lowercase()) {
        "white" -> Color(0xFF94A3B8)
        "blue" -> Color(0xFF06B6D4)
        "red" -> Color(0xFFF43F5E)
        "orange" -> Color(0xFFF59E0B)
        "gray" -> Color(0xFF64748B)
        "yellow" -> Color(0xFFFACC15)
        "purple" -> Color(0xFF3B82F6)
        "green" -> Color(0xFF059669)
        "pink" -> Color(0xFFF43F5E)
        else -> Color(0xFF3B82F6) // Default
    }
}

fun translate(text: String, language: String): String {
    if (language == "English") return text
    
    val dict = mapOf(
        "Home" to mapOf("Spanish" to "Inicio", "French" to "Accueil", "German" to "Startseite", "Italian" to "Home", "Portuguese" to "Início", "Russian" to "Главная", "Chinese" to "首页", "Japanese" to "ホーム", "Korean" to "홈"),
        "Generate" to mapOf("Spanish" to "Generar", "French" to "Générer", "German" to "Generieren", "Italian" to "Genera", "Portuguese" to "Gerar", "Russian" to "Генерировать", "Chinese" to "生成", "Japanese" to "生成", "Korean" to "생성"),
        "Design" to mapOf("Spanish" to "Diseño", "French" to "Design", "German" to "Design", "Italian" to "Design", "Portuguese" to "Design", "Russian" to "Дизайн", "Chinese" to "设计", "Japanese" to "デザイン", "Korean" to "디자인"),
        "Tag" to mapOf("Spanish" to "Etiqueta", "French" to "Étiquette", "German" to "Tag", "Italian" to "Tag", "Portuguese" to "Etiqueta", "Russian" to "Тег", "Chinese" to "标签", "Japanese" to "タグ", "Korean" to "태그"),
        "Chat" to mapOf("Spanish" to "Chat", "French" to "Chat", "German" to "Chat", "Italian" to "Chat", "Portuguese" to "Chat", "Russian" to "Чат", "Chinese" to "聊天", "Japanese" to "チャット", "Korean" to "채팅"),
        "Library" to mapOf("Spanish" to "Biblioteca", "French" to "Bibliothèque", "German" to "Bibliothek", "Italian" to "Libreria", "Portuguese" to "Biblioteca", "Russian" to "Библиотека", "Chinese" to "图书馆", "Japanese" to "ライブラリ", "Korean" to "라이브러리"),
        "Settings" to mapOf("Spanish" to "Ajustes", "French" to "Paramètres", "German" to "Einstellungen", "Italian" to "Impostazioni", "Portuguese" to "Configurações", "Russian" to "Настройки", "Chinese" to "设置", "Japanese" to "設定", "Korean" to "설정"),
        "Account" to mapOf("Spanish" to "Cuenta", "French" to "Compte", "German" to "Konto", "Italian" to "Account", "Portuguese" to "Conta", "Russian" to "Аккаунт", "Chinese" to "账户", "Japanese" to "アカウント", "Korean" to "계정"),
        "Profile" to mapOf("Spanish" to "Perfil", "French" to "Profil", "German" to "Profil", "Italian" to "Profilo", "Portuguese" to "Perfil", "Russian" to "Профиль", "Chinese" to "个人资料", "Japanese" to "プロフィール", "Korean" to "프로필"),
        "Notification" to mapOf("Spanish" to "Notificación", "French" to "Notification", "German" to "Benachrichtigung", "Italian" to "Notifica", "Portuguese" to "Notificação", "Russian" to "Уведомление", "Chinese" to "通知", "Japanese" to "通知", "Korean" to "알림"),
        "Preferences" to mapOf("Spanish" to "Preferencias", "French" to "Préférences", "German" to "Einstellungen", "Italian" to "Preferenze", "Portuguese" to "Preferências", "Russian" to "Предпочтения", "Chinese" to "偏好", "Japanese" to "環境設定", "Korean" to "환경설정"),
        "Theme" to mapOf("Spanish" to "Tema", "French" to "Thème", "German" to "Thema", "Italian" to "Tema", "Portuguese" to "Tema", "Russian" to "Тема", "Chinese" to "主题", "Japanese" to "テーマ", "Korean" to "테마"),
        "Language" to mapOf("Spanish" to "Idioma", "French" to "Langue", "German" to "Sprache", "Italian" to "Lingua", "Portuguese" to "Idioma", "Russian" to "Язык", "Chinese" to "语言", "Japanese" to "言語", "Korean" to "언어"),
        "More" to mapOf("Spanish" to "Más", "French" to "Plus", "German" to "Mehr", "Italian" to "Altro", "Portuguese" to "Mais", "Russian" to "Еще", "Chinese" to "更多", "Japanese" to "もっと", "Korean" to "더보기"),
        "Help & Support" to mapOf("Spanish" to "Ayuda y soporte", "French" to "Aide et support", "German" to "Hilfe & Support", "Italian" to "Assistenza e supporto", "Portuguese" to "Ajuda e Suporte", "Russian" to "Помощь и поддержка", "Chinese" to "帮助与支持", "Japanese" to "ヘルプとサポート", "Korean" to "도움말 및 지원"),
        "Privacy Policy" to mapOf("Spanish" to "Política de privacidad", "French" to "Politique de confidentialité", "German" to "Datenschutz", "Italian" to "Privacy Policy", "Portuguese" to "Política de Privacidade", "Russian" to "Политика конфиденциальности", "Chinese" to "隐私政策", "Japanese" to "プライバシーポリシー", "Korean" to "개인정보 보호정책"),
        "Terms of Service" to mapOf("Spanish" to "Términos de servicio", "French" to "Conditions d'utilisation", "German" to "Nutzungsbedingungen", "Italian" to "Termini di servizio", "Portuguese" to "Termos de Serviço", "Russian" to "Условия обслуживания", "Chinese" to "服务条款", "Japanese" to "利用規約", "Korean" to "서비스 약관"),
        "Version" to mapOf("Spanish" to "Versión", "French" to "Version", "German" to "Version", "Italian" to "Versione", "Portuguese" to "Versão", "Russian" to "Версия", "Chinese" to "版本", "Japanese" to "バージョン", "Korean" to "버전"),
        "English" to mapOf("Spanish" to "Inglés", "French" to "Anglais", "German" to "Englisch", "Italian" to "Inglese", "Portuguese" to "Inglês", "Russian" to "Английский", "Chinese" to "英语", "Japanese" to "英語", "Korean" to "영어"),
        "Spanish" to mapOf("Spanish" to "Español", "French" to "Espagnol", "German" to "Spanisch", "Italian" to "Spagnolo", "Portuguese" to "Espanhol", "Russian" to "Испанский", "Chinese" to "西班牙语", "Japanese" to "スペイン語", "Korean" to "스페인어"),
        "French" to mapOf("Spanish" to "Francés", "French" to "Français", "German" to "Französisch", "Italian" to "Francese", "Portuguese" to "Francês", "Russian" to "Французский", "Chinese" to "法语", "Japanese" to "フランス語", "Korean" to "프랑스어"),
        "German" to mapOf("Spanish" to "Alemán", "French" to "Allemand", "German" to "Deutsch", "Italian" to "Tedesco", "Portuguese" to "Alemão", "Russian" to "Немецкий", "Chinese" to "德语", "Japanese" to "ドイツ語", "Korean" to "독일어"),
        "Italian" to mapOf("Spanish" to "Italiano", "French" to "Italien", "German" to "Italienisch", "Italian" to "Italiano", "Portuguese" to "Italiano", "Russian" to "Итальянский", "Chinese" to "意大利语", "Japanese" to "イタリア語", "Korean" to "이탈리아어"),
        "Portuguese" to mapOf("Spanish" to "Portugués", "French" to "Portugais", "German" to "Portugiesisch", "Italian" to "Portoghese", "Portuguese" to "Português", "Russian" to "Португальский", "Chinese" to "葡萄牙语", "Japanese" to "ポルトガル語", "Korean" to "포르투갈어"),
        "Russian" to mapOf("Spanish" to "Ruso", "French" to "Russe", "German" to "Russisch", "Italian" to "Russo", "Portuguese" to "Russo", "Russian" to "Русский", "Chinese" to "俄语", "Japanese" to "ロシア語", "Korean" to "러시아어"),
        "Chinese" to mapOf("Spanish" to "Chino", "French" to "Chinois", "German" to "Chinesisch", "Italian" to "Cinese", "Portuguese" to "Chinês", "Russian" to "Китайский", "Chinese" to "中文", "Japanese" to "中国語", "Korean" to "중국어"),
        "Japanese" to mapOf("Spanish" to "Japonés", "French" to "Japonais", "German" to "Japanisch", "Italian" to "Giapponese", "Portuguese" to "Japonês", "Russian" to "Японский", "Chinese" to "日语", "Japanese" to "日本語", "Korean" to "일본어"),
        "Korean" to mapOf("Spanish" to "Coreano", "French" to "Coréen", "German" to "Koreanisch", "Italian" to "Coreano", "Portuguese" to "Coreano", "Russian" to "Корейский", "Chinese" to "韩语", "Japanese" to "韓国語", "Korean" to "한국어"),
        "Arabic" to mapOf("Spanish" to "Árabe", "French" to "Arabe", "German" to "Arabisch", "Italian" to "Arabo", "Portuguese" to "Árabe", "Russian" to "Арабский", "Chinese" to "阿拉伯语", "Japanese" to "アラビア語", "Korean" to "아랍어"),
        "Hindi" to mapOf("Spanish" to "Hindi", "French" to "Hindi", "German" to "Hindi", "Italian" to "Hindi", "Portuguese" to "Hindi", "Russian" to "Хинди", "Chinese" to "印地语", "Japanese" to "ヒンディー語", "Korean" to "힌디어"),
        "Indonesian" to mapOf("Spanish" to "Indonesio", "French" to "Indonésien", "German" to "Indonesisch", "Italian" to "Indonesiano", "Portuguese" to "Indonésio", "Russian" to "Индонезийский", "Chinese" to "印尼语", "Japanese" to "インドネシア語", "Korean" to "인도네시아어"),
        "Logout" to mapOf("Spanish" to "Cerrar sesión", "French" to "Se déconnecter", "German" to "Abmelden", "Italian" to "Esci", "Portuguese" to "Sair", "Russian" to "Выйти", "Chinese" to "登出", "Japanese" to "ログアウト", "Korean" to "로그아웃")
    )
    
    return dict[text]?.get(language) ?: text
}
