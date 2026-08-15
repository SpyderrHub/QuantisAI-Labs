package com.example.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

data class AppSettings(
    val language: String = "English",
    val theme: String = "white"
)

val LocalAppSettings = compositionLocalOf { AppSettings() }
val LocalAppSettingsUpdater = compositionLocalOf<(AppSettings) -> Unit> { {} }

@Composable
fun String.tr(): String {
    val lang = LocalAppSettings.current.language
    return translate(this, lang)
}

fun getOnPrimaryColor(themeName: String): Color {
    return when (themeName.lowercase()) {
        "white", "yellow" -> Color(0xFF000000) // Opposite text/icon color for light themes
        else -> Color(0xFFFFFFFF)
    }
}

fun getThemePrimaryColor(themeName: String): Color {
    return when (themeName.lowercase()) {
        "white" -> Color(0xFFFFFFFF)
        "blue" -> Color(0xFF3B82F6)
        "red" -> Color(0xFFEF4444)
        "orange" -> Color(0xFFF97316)
        "gray" -> Color(0xFF94A3B8)
        "yellow" -> Color(0xFFEAB308)
        "purple" -> Color(0xFFA855F7)
        "green" -> Color(0xFF10B981)
        "pink" -> Color(0xFFEC4899)
        else -> Color(0xFFFFFFFF) // White Default
    }
}

fun getThemeSecondaryColor(themeName: String): Color {
    return when (themeName.lowercase()) {
        "white" -> Color(0xFFCBD5E1)
        "blue" -> Color(0xFF06B6D4)
        "red" -> Color(0xFFF43F5E)
        "orange" -> Color(0xFFF59E0B)
        "gray" -> Color(0xFF64748B)
        "yellow" -> Color(0xFFFACC15)
        "purple" -> Color(0xFF3B82F6)
        "green" -> Color(0xFF059669)
        "pink" -> Color(0xFFF43F5E)
        else -> Color(0xFFCBD5E1) // White Default
    }
}

fun translate(text: String, language: String): String {
    if (language == "English") return text
    
    val dict = mapOf(
        "Home" to mapOf("Spanish" to "Inicio", "French" to "Accueil", "German" to "Startseite", "Italian" to "Home", "Portuguese" to "Início", "Russian" to "Главная", "Chinese" to "首页", "Japanese" to "ホーム", "Korean" to "홈", "Arabic" to "الرئيسية", "Hindi" to "होम", "Indonesian" to "Beranda"),
        "Generate" to mapOf("Spanish" to "Generar", "French" to "Générer", "German" to "Generieren", "Italian" to "Genera", "Portuguese" to "Gerar", "Russian" to "Генерировать", "Chinese" to "生成", "Japanese" to "生成", "Korean" to "생성", "Arabic" to "توليد", "Hindi" to "जनरेट करें", "Indonesian" to "Hasilkan"),
        "Design" to mapOf("Spanish" to "Diseño", "French" to "Design", "German" to "Design", "Italian" to "Design", "Portuguese" to "Design", "Russian" to "Дизайн", "Chinese" to "设计", "Japanese" to "デザイン", "Korean" to "디자인", "Arabic" to "تصميم", "Hindi" to "डिजाइन", "Indonesian" to "Desain"),
        "Tag" to mapOf("Spanish" to "Etiqueta", "French" to "Étiquette", "German" to "Tag", "Italian" to "Tag", "Portuguese" to "Etiqueta", "Russian" to "Тег", "Chinese" to "标签", "Japanese" to "タグ", "Korean" to "태그", "Arabic" to "وسم", "Hindi" to "टैग", "Indonesian" to "Tag"),
        "Chat" to mapOf("Spanish" to "Chat", "French" to "Chat", "German" to "Chat", "Italian" to "Chat", "Portuguese" to "Chat", "Russian" to "Чат", "Chinese" to "聊天", "Japanese" to "チャット", "Korean" to "채팅", "Arabic" to "دردشة", "Hindi" to "चैट", "Indonesian" to "Obrolan"),
        "Library" to mapOf("Spanish" to "Biblioteca", "French" to "Bibliothèque", "German" to "Bibliothek", "Italian" to "Libreria", "Portuguese" to "Biblioteca", "Russian" to "Библиотека", "Chinese" to "图书馆", "Japanese" to "ライブラリ", "Korean" to "라이브러리", "Arabic" to "المكتبة", "Hindi" to "लाइब्रेरी", "Indonesian" to "Perpustakaan"),
        "Settings" to mapOf("Spanish" to "Ajustes", "French" to "Paramètres", "German" to "Einstellungen", "Italian" to "Impostazioni", "Portuguese" to "Configurações", "Russian" to "Настройки", "Chinese" to "设置", "Japanese" to "設定", "Korean" to "설정", "Arabic" to "الإعدادات", "Hindi" to "सेटिंग्स", "Indonesian" to "Pengaturan"),
        "Account" to mapOf("Spanish" to "Cuenta", "French" to "Compte", "German" to "Konto", "Italian" to "Account", "Portuguese" to "Conta", "Russian" to "Аккаунт", "Chinese" to "账户", "Japanese" to "アカウント", "Korean" to "계정", "Arabic" to "الحساب", "Hindi" to "खाता", "Indonesian" to "Akun"),
        "Profile" to mapOf("Spanish" to "Perfil", "French" to "Profil", "German" to "Profil", "Italian" to "Profilo", "Portuguese" to "Perfil", "Russian" to "Профиль", "Chinese" to "个人资料", "Japanese" to "プロフィール", "Korean" to "프로필", "Arabic" to "الملف الشخصي", "Hindi" to "प्रोफ़ाइल", "Indonesian" to "Profil"),
        "Notification" to mapOf("Spanish" to "Notificación", "French" to "Notification", "German" to "Benachrichtigung", "Italian" to "Notifica", "Portuguese" to "Notificação", "Russian" to "Уведомление", "Chinese" to "通知", "Japanese" to "通知", "Korean" to "알림", "Arabic" to "الإشعارات", "Hindi" to "अधिसूचना", "Indonesian" to "Notifikasi"),
        "Preferences" to mapOf("Spanish" to "Preferencias", "French" to "Préférences", "German" to "Einstellungen", "Italian" to "Preferenze", "Portuguese" to "Preferências", "Russian" to "Предпочтения", "Chinese" to "偏好", "Japanese" to "環境設定", "Korean" to "환경설정", "Arabic" to "التفضيلات", "Hindi" to "प्राथमिकताएं", "Indonesian" to "Preferensi"),
        "Theme" to mapOf("Spanish" to "Tema", "French" to "Thème", "German" to "Thema", "Italian" to "Tema", "Portuguese" to "Tema", "Russian" to "Тема", "Chinese" to "主题", "Japanese" to "テーマ", "Korean" to "테마", "Arabic" to "المظهر", "Hindi" to "थीम", "Indonesian" to "Tema"),
        "Language" to mapOf("Spanish" to "Idioma", "French" to "Langue", "German" to "Sprache", "Italian" to "Lingua", "Portuguese" to "Idioma", "Russian" to "Язык", "Chinese" to "语言", "Japanese" to "言語", "Korean" to "언어", "Arabic" to "اللغة", "Hindi" to "भाषा", "Indonesian" to "Bahasa"),
        "More" to mapOf("Spanish" to "Más", "French" to "Plus", "German" to "Mehr", "Italian" to "Altro", "Portuguese" to "Mais", "Russian" to "Еще", "Chinese" to "更多", "Japanese" to "もっと", "Korean" to "더보기", "Arabic" to "المزيد", "Hindi" to "अधिक", "Indonesian" to "Lainnya"),
        "Help & Support" to mapOf("Spanish" to "Ayuda y soporte", "French" to "Aide et support", "German" to "Hilfe & Support", "Italian" to "Assistenza e supporto", "Portuguese" to "Ajuda e Suporte", "Russian" to "Помощь и поддержка", "Chinese" to "帮助与支持", "Japanese" to "ヘルプとサポート", "Korean" to "도움말 및 지원", "Arabic" to "المساعدة والدعم", "Hindi" to "सहायता और समर्थन", "Indonesian" to "Bantuan & Dukungan"),
        "Privacy Policy" to mapOf("Spanish" to "Política de privacidad", "French" to "Politique de confidentialité", "German" to "Datenschutz", "Italian" to "Privacy Policy", "Portuguese" to "Política de Privacidade", "Russian" to "Политика конфиденциальности", "Chinese" to "隐私政策", "Japanese" to "プライバシーポリシー", "Korean" to "개인정보 보호정책", "Arabic" to "سياسة الخصوصية", "Hindi" to "गोपनीयता नीति", "Indonesian" to "Kebijakan Privasi"),
        "Terms of Service" to mapOf("Spanish" to "Términos de servicio", "French" to "Conditions d'utilisation", "German" to "Nutzungsbedingungen", "Italian" to "Termini di servicio", "Portuguese" to "Termos de Serviço", "Russian" to "Условия обслуживания", "Chinese" to "服务条款", "Japanese" to "利用規約", "Korean" to "서비스 약관", "Arabic" to "شروط الخدمة", "Hindi" to "सेवा की शर्तें", "Indonesian" to "Ketentuan Layanan"),
        "Version" to mapOf("Spanish" to "Versión", "French" to "Version", "German" to "Version", "Italian" to "Versione", "Portuguese" to "Versão", "Russian" to "Версия", "Chinese" to "版本", "Japanese" to "バージョン", "Korean" to "버전", "Arabic" to "الإصدار", "Hindi" to "संस्करण", "Indonesian" to "Versi"),
        "English" to mapOf("Spanish" to "Inglés", "French" to "Anglais", "German" to "Englisch", "Italian" to "Inglese", "Portuguese" to "Inglês", "Russian" to "Английский", "Chinese" to "英语", "Japanese" to "英語", "Korean" to "영어", "Arabic" to "الإنجليزية", "Hindi" to "अंग्रेज़ी", "Indonesian" to "Inggris"),
        "Spanish" to mapOf("Spanish" to "Español", "French" to "Espagnol", "German" to "Spanisch", "Italian" to "Spagnolo", "Portuguese" to "Espanhol", "Russian" to "Испанский", "Chinese" to "西班牙语", "Japanese" to "スペイン語", "Korean" to "스페인어", "Arabic" to "الإسبانية", "Hindi" to "स्पैनिश", "Indonesian" to "Spanyol"),
        "French" to mapOf("Spanish" to "Francés", "French" to "Français", "German" to "Französisch", "Italian" to "Francese", "Portuguese" to "Francês", "Russian" to "Французский", "Chinese" to "法语", "Japanese" to "フランス語", "Korean" to "프랑스어", "Arabic" to "الفرنسية", "Hindi" to "फ़्रेंच", "Indonesian" to "Prancis"),
        "German" to mapOf("Spanish" to "Alemán", "French" to "Allemand", "German" to "Deutsch", "Italian" to "Tedesco", "Portuguese" to "Alemão", "Russian" to "Немецкий", "Chinese" to "德语", "Japanese" to "ドイツ語", "Korean" to "독일어", "Arabic" to "الألمانية", "Hindi" to "जर्मन", "Indonesian" to "Jerman"),
        "Italian" to mapOf("Spanish" to "Italiano", "French" to "Italien", "German" to "Italienisch", "Italian" to "Italiano", "Portuguese" to "Italiano", "Russian" to "Итальянский", "Chinese" to "意大利语", "Japanese" to "イタリア語", "Korean" to "Coreano", "Arabic" to "الإيطالية", "Hindi" to "इतालवी", "Indonesian" to "Italia"),
        "Portuguese" to mapOf("Spanish" to "Portugués", "French" to "Portugais", "German" to "Portugiesisch", "Italian" to "Portoghese", "Portuguese" to "Português", "Russian" to "Португальский", "Chinese" to "葡萄牙语", "Japanese" to "ポルトガル語", "Korean" to "포르투갈어", "Arabic" to "البرتغالية", "Hindi" to "पुर्तगाली", "Indonesian" to "Portugis"),
        "Russian" to mapOf("Spanish" to "Ruso", "French" to "Russe", "German" to "Russisch", "Italian" to "Russo", "Portuguese" to "Russo", "Russian" to "Русский", "Chinese" to "俄语", "Japanese" to "ロシア語", "Korean" to "러시아어", "Arabic" to "الروسية", "Hindi" to "रूसी", "Indonesian" to "Rusia"),
        "Chinese" to mapOf("Spanish" to "Chino", "French" to "Chinois", "German" to "Chinesisch", "Italian" to "Cinese", "Portuguese" to "Chinês", "Russian" to "Китайский", "Chinese" to "中文", "Japanese" to "中国語", "Korean" to "중국어", "Arabic" to "الصينية", "Hindi" to "चीनी", "Indonesian" to "Cina"),
        "Japanese" to mapOf("Spanish" to "Japonés", "French" to "Japonais", "German" to "Japanisch", "Italian" to "Giapponese", "Portuguese" to "Japonês", "Russian" to "Японский", "Chinese" to "日语", "Japanese" to "日本語", "Korean" to "일본어", "Arabic" to "اليابانية", "Hindi" to "जापानी", "Indonesian" to "Jepang"),
        "Korean" to mapOf("Spanish" to "Coreano", "French" to "Coréen", "German" to "Koreanisch", "Italian" to "Coreano", "Portuguese" to "Coreano", "Russian" to "Корейский", "Chinese" to "韩语", "Japanese" to "韓国語", "Korean" to "한국어", "Arabic" to "الكورية", "Hindi" to "कोरियाई", "Indonesian" to "Korea"),
        "Arabic" to mapOf("Spanish" to "Árabe", "French" to "Arabe", "German" to "Arabisch", "Italian" to "Arabo", "Portuguese" to "Árabe", "Russian" to "Арабский", "Chinese" to "阿拉伯语", "Japanese" to "アラビア語", "Korean" to "아랍어", "Arabic" to "العربية", "Hindi" to "अरबी", "Indonesian" to "Arab"),
        "Hindi" to mapOf("Spanish" to "Hindi", "French" to "Hindi", "German" to "Hindi", "Italian" to "Hindi", "Portuguese" to "Hindi", "Russian" to "Хинди", "Chinese" to "印地语", "Japanese" to "ヒンディー語", "Korean" to "힌디어", "Arabic" to "الهندية", "Hindi" to "हिंदी", "Indonesian" to "Hindi"),
        "Indonesian" to mapOf("Spanish" to "Indonesio", "French" to "Indonésien", "German" to "Indonesisch", "Italian" to "Indonesiano", "Portuguese" to "Indonésio", "Russian" to "Индонезийский", "Chinese" to "印尼语", "Japanese" to "インドネシア語", "Korean" to "인도네시아어", "Arabic" to "الإندونيسية", "Hindi" to "इंडोनेशियाई", "Indonesian" to "Bahasa Indonesia"),
        "Logout" to mapOf("Spanish" to "Cerrar sesión", "French" to "Se déconnecter", "German" to "Abmelden", "Italian" to "Esci", "Portuguese" to "Sair", "Russian" to "Выйти", "Chinese" to "登出", "Japanese" to "ログアウト", "Korean" to "로그아웃", "Arabic" to "تسجيل الخروج", "Hindi" to "लॉग आउट", "Indonesian" to "Keluar"),
        "AI Voice Generator" to mapOf("Spanish" to "Generador de voz IA", "French" to "Générateur de voix IA", "German" to "KI-Sprachgenerator", "Italian" to "Generatore di voce IA", "Portuguese" to "Gerador de Voz IA", "Russian" to "ИИ Генератор голоса", "Chinese" to "AI 语音生成器", "Japanese" to "AI 音声ジェネレーター", "Korean" to "AI 음성 생성기", "Arabic" to "مولد الصوت بالذكاء الاصطناعي", "Hindi" to "एआई वॉयस जनरेटर", "Indonesian" to "Generator Suara AI"),
        "Search voices..." to mapOf("Spanish" to "Buscar voces...", "French" to "Rechercher des voix...", "German" to "Stimmen suchen...", "Italian" to "Cerca voci...", "Portuguese" to "Pesquisar vozes...", "Russian" to "Поиск голосов...", "Chinese" to "搜索声音...", "Japanese" to "声を検索...", "Korean" to "음성 검색...", "Arabic" to "البحث عن الأصوات...", "Hindi" to "आवाज़ें खोजें...", "Indonesian" to "Cari suara..."),
        "All" to mapOf("Spanish" to "Todos", "French" to "Tous", "German" to "Alle", "Italian" to "Tutti", "Portuguese" to "Todos", "Russian" to "Все", "Chinese" to "全部", "Japanese" to "すべて", "Korean" to "전체", "Arabic" to "الكل", "Hindi" to "सभी", "Indonesian" to "Semua"),
        "Male" to mapOf("Spanish" to "Masculino", "French" to "Masculin", "German" to "Männlich", "Italian" to "Maschile", "Portuguese" to "Masculino", "Russian" to "Мужской", "Chinese" to "男", "Japanese" to "男性", "Korean" to "남성", "Arabic" to "ذكور", "Hindi" to "पुरुष", "Indonesian" to "Pria"),
        "Female" to mapOf("Spanish" to "Femenino", "French" to "Féminin", "German" to "Weiblich", "Italian" to "Femminile", "Portuguese" to "Feminino", "Russian" to "Женский", "Chinese" to "女", "Japanese" to "女性", "Korean" to "여성", "Arabic" to "إناث", "Hindi" to "महिला", "Indonesian" to "Wanita"),
        "Saved" to mapOf("Spanish" to "Guardado", "French" to "Enregistré", "German" to "Gespeichert", "Italian" to "Salvati", "Portuguese" to "Salvo", "Russian" to "Сохраненные", "Chinese" to "已保存", "Japanese" to "保存済み", "Korean" to "저장됨", "Arabic" to "محفوظات", "Hindi" to "सेव्ड", "Indonesian" to "Tersimpan"),
        "Trending" to mapOf("Spanish" to "Tendencias", "French" to "Tendances", "German" to "Beliebt", "Italian" to "Tendenze", "Portuguese" to "Tendências", "Russian" to "В тренде", "Chinese" to "热门", "Japanese" to "トレンド", "Korean" to "트렌딩", "Arabic" to "الشائع", "Hindi" to "ट्रेंडिंग", "Indonesian" to "Trending"),
        "Text to Speech" to mapOf("Spanish" to "Texto a voz", "French" to "Texte en parole", "German" to "Text-in-Sprache", "Italian" to "Sintesi vocale", "Portuguese" to "Texto para Fala", "Russian" to "Текст в речь", "Chinese" to "文本转语音", "Japanese" to "テキスト読み上げ", "Korean" to "음성 합성", "Arabic" to "تحويل النص إلى كلام", "Hindi" to "टेक्स्ट से भाषण", "Indonesian" to "Teks ke Suara"),
        "Enter your text here..." to mapOf("Spanish" to "Escribe tu texto aquí...", "French" to "Entrez votre texte ici...", "German" to "Geben Sie Ihren Text hier ein...", "Italian" to "Inserisci il tuo testo qui...", "Portuguese" to "Digite seu texto aqui...", "Russian" to "Введите текст...", "Chinese" to "在此输入文本...", "Japanese" to "テキストを入力...", "Korean" to "텍스트를 입력하세요...", "Arabic" to "أدخل نصك هنا...", "Hindi" to "यहाँ अपना पाठ दर्ज करें...", "Indonesian" to "Masukkan teks Anda di sini..."),
        "Generate Voice" to mapOf("Spanish" to "Generar voz", "French" to "Générer la voix", "German" to "Stimme generieren", "Italian" to "Genera voce", "Portuguese" to "Gerar Voz", "Russian" to "Сгенерировать голос", "Chinese" to "生成语音", "Japanese" to "音声生成", "Korean" to "음성 생성", "Arabic" to "توليد الصوت", "Hindi" to "आवाज़ जनरेट करें", "Indonesian" to "Hasilkan Suara"),
        "Speed" to mapOf("Spanish" to "Velocidad", "French" to "Vitesse", "German" to "Geschwindigkeit", "Italian" to "Velocità", "Portuguese" to "Velocidade", "Russian" to "Скорость", "Chinese" to "语速", "Japanese" to "速度", "Korean" to "속도", "Arabic" to "السرعة", "Hindi" to "गति", "Indonesian" to "Kecepatan"),
        "Pitch" to mapOf("Spanish" to "Tono", "French" to "Hauteur", "German" to "Tonhöhe", "Italian" to "Tono", "Portuguese" to "Tom", "Russian" to "Высота тона", "Chinese" to "音调", "Japanese" to "ピッチ", "Korean" to "피치", "Arabic" to "طبقة الصوت", "Hindi" to "पिच", "Indonesian" to "Nada"),
        "Emotion" to mapOf("Spanish" to "Emoción", "French" to "Émotion", "German" to "Emotion", "Italian" to "Emozione", "Portuguese" to "Emoção", "Russian" to "Эмоция", "Chinese" to "情感", "Japanese" to "感情", "Korean" to "감정", "Arabic" to "العاطفة", "Hindi" to "भावना", "Indonesian" to "Emosi"),
        "Voice Design" to mapOf("Spanish" to "Diseño de voz", "French" to "Design vocal", "German" to "Stimmdesign", "Italian" to "Design della voce", "Portuguese" to "Design de Voz", "Russian" to "Дизайн голоса", "Chinese" to "声音设计", "Japanese" to "ボイスデザイン", "Korean" to "보이스 디자인", "Arabic" to "تصميم الصوت", "Hindi" to "वॉयस डिजाइन", "Indonesian" to "Desain Suara"),
        "Voice Name" to mapOf("Spanish" to "Nombre de voz", "French" to "Nom de la voix", "German" to "Stimmenname", "Italian" to "Nome della voce", "Portuguese" to "Nome da Voz", "Russian" to "Имя голоса", "Chinese" to "声音名称", "Japanese" to "音声名", "Korean" to "음성 이름", "Arabic" to "اسم الصوت", "Hindi" to "आवाज़ का नाम", "Indonesian" to "Nama Suara"),
        "Gender" to mapOf("Spanish" to "Género", "French" to "Genre", "German" to "Geschlecht", "Italian" to "Genere", "Portuguese" to "Gênero", "Russian" to "Пол", "Chinese" to "性别", "Japanese" to "性別", "Korean" to "성별", "Arabic" to "الجنس", "Hindi" to "लिंग", "Indonesian" to "Jenis Kelamin"),
        "Age" to mapOf("Spanish" to "Edad", "French" to "Âge", "German" to "Alter", "Italian" to "Età", "Portuguese" to "Idade", "Russian" to "Возраст", "Chinese" to "年龄", "Japanese" to "年齢", "Korean" to "연령", "Arabic" to "العمر", "Hindi" to "आयु", "Indonesian" to "Usia"),
        "Accent" to mapOf("Spanish" to "Acento", "French" to "Accent", "German" to "Akzent", "Italian" to "Accento", "Portuguese" to "Sotaque", "Russian" to "Акцент", "Chinese" to "口音", "Japanese" to "アクセント", "Korean" to "억양", "Arabic" to "اللكنة", "Hindi" to "उच्चारण", "Indonesian" to "Aksen"),
        "Welcome Back" to mapOf("Spanish" to "Bienvenido de nuevo", "French" to "Bon retour", "German" to "Willkommen zurück", "Italian" to "Bentornato", "Portuguese" to "Bem-vindo de volta", "Russian" to "С возвращением", "Chinese" to "欢迎回来", "Japanese" to "おかえりなさい", "Korean" to "다시 오신 것을 환영합니다", "Arabic" to "مرحباً بعودتك", "Hindi" to "वापसी पर स्वागत है", "Indonesian" to "Selamat Datang Kembali"),
        "Create Account" to mapOf("Spanish" to "Crear cuenta", "French" to "Créer un compte", "German" to "Konto erstellen", "Italian" to "Crea account", "Portuguese" to "Criar Conta", "Russian" to "Создать аккаунт", "Chinese" to "创建账户", "Japanese" to "アカウント作成", "Korean" to "계정 만들기", "Arabic" to "إنشاء حساب", "Hindi" to "खाता बनाएं", "Indonesian" to "Buat Akun"),
        "Full Name" to mapOf("Spanish" to "Nombre completo", "French" to "Nom complet", "German" to "Vollständiger Name", "Italian" to "Nome completo", "Portuguese" to "Nome Completo", "Russian" to "Полное имя", "Chinese" to "全名", "Japanese" to "氏名", "Korean" to "성명", "Arabic" to "الاسم الكامل", "Hindi" to "पूरा नाम", "Indonesian" to "Nama Lengkap"),
        "Email address" to mapOf("Spanish" to "Correo electrónico", "French" to "Adresse e-mail", "German" to "E-Mail-Adresse", "Italian" to "Indirizzo e-mail", "Portuguese" to "E-mail", "Russian" to "Эл. почта", "Chinese" to "电子邮件", "Japanese" to "メールアドレス", "Korean" to "이메일 주소", "Arabic" to "البريد الإلكتروني", "Hindi" to "ईमेल पता", "Indonesian" to "Alamat Email"),
        "Password" to mapOf("Spanish" to "Contraseña", "French" to "Mot de passe", "German" to "Passwort", "Italian" to "Password", "Portuguese" to "Senha", "Russian" to "Пароль", "Chinese" to "密码", "Japanese" to "パスワード", "Korean" to "비밀번호", "Arabic" to "كلمة المرور", "Hindi" to "पासवर्ड", "Indonesian" to "Kata Sandi"),
        "Login" to mapOf("Spanish" to "Iniciar sesión", "French" to "Connexion", "German" to "Anmelden", "Italian" to "Accedi", "Portuguese" to "Entrar", "Russian" to "Войти", "Chinese" to "登录", "Japanese" to "ログイン", "Korean" to "로그인", "Arabic" to "تسجيل الدخول", "Hindi" to "लॉगिन", "Indonesian" to "Masuk"),
        "Sign Up" to mapOf("Spanish" to "Registrarse", "French" to "S'inscrire", "German" to "Registrieren", "Italian" to "Registrati", "Portuguese" to "Cadastrar", "Russian" to "Регистрация", "Chinese" to "注册", "Japanese" to "サインアップ", "Korean" to "회원가입", "Arabic" to "الاشتراك", "Hindi" to "साइन अप", "Indonesian" to "Daftar"),
        "Allow Notifications" to mapOf("Spanish" to "Permitir notificaciones", "French" to "Autoriser les notifications", "German" to "Benachrichtigungen zulassen", "Italian" to "Consenti notifiche", "Portuguese" to "Permitir Notificações", "Russian" to "Разрешить уведомления", "Chinese" to "允许通知", "Japanese" to "通知を許可", "Korean" to "알림 허용", "Arabic" to "السماح بالإشعارات", "Hindi" to "अधिसूचनाओं की अनुमति दें", "Indonesian" to "Izinkan Notifikasi"),
        "Voice Library" to mapOf("Spanish" to "Biblioteca de voces", "French" to "Bibliothèque de voix", "German" to "Stimmenbibliothek", "Italian" to "Libreria vocale", "Portuguese" to "Biblioteca de Vozes", "Russian" to "Библиотека голосов", "Chinese" to "声音库", "Japanese" to "ボイスライブラリ", "Korean" to "음성 라이브러리", "Arabic" to "مكتبة الأصوات", "Hindi" to "वॉयस लाइब्रेरी", "Indonesian" to "Perpustakaan Suara"),
        "Speech to Text" to mapOf("Spanish" to "Voz a texto", "French" to "Parole en texte", "German" to "Sprache-in-Text", "Italian" to "Sintesi vocale", "Portuguese" to "Fala para Texto", "Russian" to "Речь в текст", "Chinese" to "语音转文本", "Japanese" to "音声テキスト変換", "Korean" to "음성 텍스트 변환", "Arabic" to "تحويل الكلام إلى نص", "Hindi" to "भाषण से टेक्स्ट", "Indonesian" to "Suara ke Teks"),
        "Saved Voices" to mapOf("Spanish" to "Voces guardadas", "French" to "Voix enregistrées", "German" to "Gespeichert", "Italian" to "Voci salvate", "Portuguese" to "Vozes Salvas", "Russian" to "Сохраненные голоса", "Chinese" to "已保存的声音", "Japanese" to "保存した音声", "Korean" to "저장된 음성", "Arabic" to "الأصوات المحفوظة", "Hindi" to "सहेजी गई आवाज़ें", "Indonesian" to "Suara Tersimpan"),
        "Watch Ad" to mapOf("Spanish" to "Ver anuncio", "French" to "Regarder pub", "German" to "Werbung ansehen", "Italian" to "Guarda annuncio", "Portuguese" to "Assistir Anúncio", "Russian" to "Смотреть рекламу", "Chinese" to "观看广告", "Japanese" to "広告を見る", "Korean" to "광고 보기", "Arabic" to "مشاهدة الإعلان", "Hindi" to "विज्ञापन देखें", "Indonesian" to "Tonton Iklan"),
        "Ad Credits" to mapOf("Spanish" to "Créditos de anuncios", "French" to "Crédits pub", "German" to "Werbeguthaben", "Italian" to "Crediti pubblicitari", "Portuguese" to "Créditos de Anúncio", "Russian" to "Кредиты за рекламу", "Chinese" to "广告积分", "Japanese" to "広告クレジット", "Korean" to "광고 크레딧", "Arabic" to "رصيد الإعلانات", "Hindi" to "विज्ञापन क्रेडिट", "Indonesian" to "Kredit Iklan"),
        "Upgrade to Pro" to mapOf("Spanish" to "Mejorar a Pro", "French" to "Passer à Pro", "German" to "Auf Pro aufrüsten", "Italian" to "Passa a Pro", "Portuguese" to "Mudar para Pro", "Russian" to "Перейти на Pro", "Chinese" to "升级到 Pro", "Japanese" to "Proにアップグレード", "Korean" to "Pro로 업그레이드", "Arabic" to "الترقية إلى برو", "Hindi" to "प्रो में अपग्रेड करें", "Indonesian" to "Tingkatkan ke Pro"),
        "Credits Available" to mapOf("Spanish" to "Créditos disponibles", "French" to "Crédits disponibles", "German" to "Verfügbare Guthaben", "Italian" to "Crediti disponibili", "Portuguese" to "Créditos Disponíveis", "Russian" to "Доступные кредиты", "Chinese" to "可用积分", "Japanese" to "利用可能なクレジット", "Korean" to "사용 가능한 크레딧", "Arabic" to "الرصيد المتاح", "Hindi" to "उपलब्ध क्रेडिट", "Indonesian" to "Kredit Tersedia"),
        "Recent Generations" to mapOf("Spanish" to "Generaciones recientes", "French" to "Générations récentes", "German" to "Neueste Generierungen", "Italian" to "Generazioni recenti", "Portuguese" to "Gerações Recentes", "Russian" to "Недавние генерации", "Chinese" to "最近的生成", "Japanese" to "最近の生成", "Korean" to "최근 생성", "Arabic" to "التوليدات الأخيرة", "Hindi" to "हाल के जनरेशन", "Indonesian" to "Generasi Terbaru")
    )
    
    val exact = dict[text]?.get(language)
    if (exact != null) return exact

    return dict[text.trim()]?.get(language) ?: text
}
