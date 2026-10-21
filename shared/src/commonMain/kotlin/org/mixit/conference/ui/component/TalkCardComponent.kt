package org.mixit.conference.ui.component

import kotlinx.html.*
import org.mixit.conference.model.shared.Context
import org.mixit.conference.model.talk.Talk

private fun String.excerpt(max: Int): String {
    val clean = replace(Regex("[*_`#>\\[\\]]"), "").replace(Regex("\\s+"), " ").trim()
    if (clean.length <= max) return clean
    return clean.take(max).substringBeforeLast(' ').trimEnd(',', '.', ';', ':', ' ') + "…"
}

fun DIV.talkCardComponent(context: Context, talk: Talk, featured: Boolean = false) {
    val url = "${context.uriBasePath}/${talk.event}/${talk.slug}"
    val text = talk.summary.ifBlank { talk.description }
    a(href = url, classes = "mxt-talk-card" + if (featured) " mxt-talk-card--featured" else "") {
        attributes["aria-label"] = talk.title
        div(classes = "mxt-talk-card__header") {
            topicComponent(context, talk.topic, talk.event.toIntOrNull())
        }
        h3(classes = "mxt-talk-card__title") { +talk.title }
        if (text.isNotBlank()) {
            p(classes = "mxt-talk-card__summary") { +text.excerpt(if (featured) 420 else 220) }
        }
        val names = talk.speakers.joinToString(", ") { "${it.firstname.orEmpty()} ${it.lastname.orEmpty()}".trim() }
        if (names.isNotBlank()) {
            div(classes = "mxt-talk-card__speakers") { +names }
        }

        span(classes = "mxt-talk-card__more") { +context.i18n("archives.more") }
    }
}
