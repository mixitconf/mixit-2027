package org.mixit.conference.model.talk

enum class Theme(val id: Int, val slug: String, val label: String, val apiCode: String, val imagePath: String) {
    LANGUAGES(1, "languages", "Langages & paradigmes", "langages", "mixitconf-images-two/main/2025/apres-midi_jour1/mixit-J1_PM-15-DianeSevrin.jpg"),
    WEB(2, "web", "Web & Frontend", "web-frontend", "mixitconf-images/main/2018/mixit18-day1/mixit-2018---day-1---april-19th_41637656732_o.jpg"),
    MOBILE(3, "mobile", "Mobile", "mobile", "mixitconf-images-two/main/2023/mixit23-facilitation/image5.jpg"),
    ARCHITECTURE(4, "architecture", "Architecture & Backend", "architecture", "mixitconf-images-three/main/2026/day2_am/mixit26-J2AM-DianeSevrin-151.jpg"),
    CLOUD(5, "cloud", "Cloud, DevOps & Infra", "cloud-devops", "mixitconf-images/main/2022/mixit22-facilitation/6BB46E4A-6D96-418D-B753-5CC9211F1BE5.jpeg"),
    QUALITY(6, "quality", "Qualité, Tests & Craft", "qualite-tests", "mixitconf-images-two/main/2023/mixit23-facilitation/image82.jpg"),
    DATA(7, "data", "Data & Dataviz", "data", "mixitconf-images/main/2019/mixit19-day1/_jsb4070jpg_48007576826_o.jpg"),
    AI(8, "ai", "Intelligence artificielle", "ia", "mixitconf-images-two/main/2023/mixit23-facilitation/image44.jpg"),
    SECURITY(9, "security", "Sécurité & Cybersécurité", "securite", "mixitconf-images-two/main/2023/mixit23-facilitation/image37.jpg"),
    HARDWARE(10, "hardware", "Hardware, IoT & Makers", "hardware-makers", "mixitconf-images-three/main/2026/mixteen/mixit26_mixteen-DianeSevrin-030.jpg"),
    GAMES(11, "games", "Jeux vidéo & Créa numérique", "jeux-creative-coding", "mixitconf-images-three/main/2026/retrogaming/mixit26_retrogaming-DianeSevrin-009.jpg"),
    OPEN_SOURCE(12, "open-source", "Open source & Communs", "open-source", "mixitconf-images/main/2022/mixit22-day2/mixit-2022-j2-aprem_52098804178_o.jpg"),
    AGILITY(13, "agility", "Agilité & Méthodes", "agilite", "mixitconf-images/main/2019/mixit19-day1/_jsb4120jpg_48007668307_o.jpg"),
    MANAGEMENT(14, "management", "Management & Leadership", "management", "mixitconf-images-two/main/2023/mixit23-facilitation/image71.jpg"),
    COLLABORATION(15, "collaboration", "Collaboration & Communication", "collaboration", "mixitconf-images-three/main/2026/day1_pm/mixit_J1PM-DianeSevrin-101.jpg"),
    CAREER(16, "career", "Carrière & Métier de dev", "carriere", "mixitconf-images/main/2022/mixit22-facilitation/231358AA-1D4E-41DB-A73B-25B6011CE10D.jpeg"),
    LEARNING(17, "learning", "Apprentissage & Transmission", "apprentissage", "mixitconf-images-two/main/2023/mixit23-facilitation/image33.jpg"),
    WELLBEING(18, "wellbeing", "Santé & Bien-être au travail", "sante-bien-etre", "mixitconf-images-two/main/2023/mixit23-facilitation/image26.jpg"),
    FUTURE_OF_WORK(19, "future-of-work", "Futur du travail", "futur-du-travail", "mixitconf-images/main/2022/mixit22-facilitation/1010E811-57CA-414D-9267-19F09CBFAC26.jpeg"),
    PRODUCT(20, "product", "Produit & Entrepreneuriat", "produit-entrepreneuriat", "mixitconf-images-two/main/2023/mixit23-facilitation/image42.jpg"),
    UX(21, "ux", "UX & Design", "ux-design", "mixitconf-images-three/main/2026/facilitation/mixit26_facilitationgraphique-DianeSevrin-005.jpg"),
    ACCESSIBILITY(22, "accessibility", "Accessibilité", "accessibilite", "mixitconf-images-two/main/2023/mixit23-facilitation/image9.jpg"),
    DIVERSITY(23, "diversity", "Diversité, Inclusion & Genre", "diversite-inclusion", "mixitconf-images/main/2022/mixit22-facilitation/E876E787-B722-4D7E-A285-8A70E6861176.jpeg"),
    ECOLOGY(24, "ecology", "Écologie & Numérique responsable", "ecologie", "mixitconf-images/main/2022/mixit22-day1/mixit-2022-j1-matin_52095900688_o.jpg"),
    ETHICS(25, "ethics", "Éthique du numérique", "ethique", "mixitconf-images-two/main/2023/mixit23-facilitation/image75.jpg"),
    PRIVACY(26, "privacy", "Vie privée & Surveillance", "vie-privee-surveillance", "mixitconf-images-two/main/2023/mixit23-facilitation/image51.jpg"),
    GEOPOLITICS(27, "geopolitics", "Géopolitique & Souveraineté", "geopolitique", "mixitconf-images/main/2018/mixit18-day1/mixit-2018---day-1---april-19th_41637656872_o.jpg"),
    DEMOCRACY(28, "democracy", "Démocratie, Citoyenneté & Service public", "democratie-citoyennete", "mixitconf-images-two/main/2023/mixit23-facilitation/image24.jpg"),
    SOCIETY(29, "society", "Économie & Société", "economie-societe", "mixitconf-images-two/main/2023/mixit23-facilitation/image22.jpg"),
    SCIENCE(30, "science", "Sciences & Espace", "sciences-espace", "mixitconf-images-two/main/2024/day2_pm/mixit24-J2-PM-146-DianeSevrin.jpg"),
    HISTORY(31, "history", "Histoire de l’informatique", "histoire-informatique", "mixitconf-images-two/main/2025/mixteen/mixit-mixteen-17-DianeSevrin.jpg"),
    CULTURE(32, "culture", "Culture & Arts", "culture-arts", "mixitconf-images/main/2017/mixit17-event/mixit-2017_33773456704_o.jpg");

    val featured: Boolean get() = slug in FEATURED_SLUGS

    /** Raw GitHub URL of the illustration photo (repo/branch/path). */
    val imageUrl: String get() = "https://raw.githubusercontent.com/mixitconf/$imagePath"

    companion object {
        private val FEATURED_SLUGS = setOf(
            "ai", "open-source", "wellbeing", "product",
            "diversity", "ethics", "privacy", "geopolitics", "society", "science"
        )

        private fun normalize(s: String) =
            s.trim().lowercase()
                .replace('’', '\'')
                .replace(Regex("[àâä]"), "a")
                .replace(Regex("[éèêë]"), "e")
                .replace(Regex("[îï]"), "i")
                .replace(Regex("[ôö]"), "o")
                .replace(Regex("[ùûü]"), "u")
                .replace("ç", "c")

        /** Accepts the API code (e.g. "web-frontend"), the theme number, its enum name, its slug or its french label (or "3. Mobile"). */
        fun of(value: String?): Theme? {
            if (value.isNullOrBlank()) return null
            val raw = value.trim()
            val withoutNumber = raw.replace(Regex("^\\d+\\s*[.)-]\\s*"), "")
            raw.toIntOrNull()?.let { n -> return entries.firstOrNull { it.id == n } }
            val key = normalize(withoutNumber)
            return entries.firstOrNull {
                it.apiCode == key || normalize(it.label) == key || it.slug == key || it.name.equals(withoutNumber.trim(), true)
            } ?: Regex("^(\\d+)").find(raw)?.let { m -> entries.firstOrNull { it.id == m.value.toInt() } }
        }

        fun ofSlug(slug: String?): Theme? = entries.firstOrNull { it.slug == slug }
    }
}
