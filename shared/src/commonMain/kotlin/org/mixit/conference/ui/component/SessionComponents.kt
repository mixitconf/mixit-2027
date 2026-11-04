package org.mixit.conference.ui.component

import kotlinx.html.ButtonType
import kotlinx.html.FlowContent
import kotlinx.html.FormMethod
import kotlinx.html.InputType
import kotlinx.html.a
import kotlinx.html.article
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.img
import kotlinx.html.input
import kotlinx.html.span
import kotlinx.html.strong
import kotlinx.html.unsafe
import org.mixit.conference.model.link.Link
import org.mixit.conference.model.people.Speaker
import org.mixit.conference.model.shared.Context
import org.mixit.conference.model.talk.Room
import org.mixit.conference.model.talk.Talk
import org.mixit.conference.model.talk.TalkFormat
import org.mixit.conference.ui.formatter.formatTime

/**
 * Building blocks of the programme, the talk page and the speaker pages (2027 design).
 */

private const val HEART_PATH = "M12 20s-7-4.4-7-10a4 4 0 0 1 7-2.6A4 4 0 0 1 19 10c0 5.6-7 10-7 10z"

/** Colour family of a format badge. */
private fun TalkFormat.badgeModifier(): String = when {
    isKeynote() -> "keynote"
    this == TalkFormat.LIGHTNING_TALK -> "lightning"
    this == TalkFormat.WORKSHOP -> "workshop"
    else -> "talk"
}

/** Public URL of a talk. */
fun Context.talkHref(talk: Talk): String = "$uriBasePath/${talk.event}/${talk.slug}"

/** Public URL of a speaker. */
fun Context.speakerHref(speaker: Speaker): String = "$uriBasePath/speakers/${speaker.id}"

fun Speaker.fullName(): String = "${firstname.orEmpty()} ${lastname.orEmpty()}".trim()

fun Speaker.initials(): String =
    listOfNotNull(firstname?.firstOrNull(), lastname?.firstOrNull()).joinToString("").uppercase()

/** Duration of a talk ("50 min", "1h50"), from its slot or, without slot, from its format. */
fun Talk.durationLabel(): String {
    val start = startLocalTime()
    val end = endLocalTime()
    val minutes = if (start != null && end != null) {
        (end.hour * 60 + end.minute) - (start.hour * 60 + start.minute)
    } else {
        format.duration
    }
    return if (minutes >= 60) "${minutes / 60}h${(minutes % 60).toString().padStart(2, '0')}" else "$minutes min"
}

/** Room label, or null when the room is not known. */
fun Context.roomLabel(talk: Talk): String? =
    if (talk.room == Room.UNKNOWN) null else i18n("rooms.${talk.room.name.lowercase()}")

/** Coloured pill with the format of a talk, optionally followed by a detail ("20 min"). */
fun FlowContent.formatBadge(context: Context, talk: Talk, detail: String? = null) {
    span(classes = "mxt-format mxt-format--${talk.format.badgeModifier()}") {
        +context.i18n("talk.format.${talk.format.name}")
        if (detail != null) +" · $detail"
    }
}

/** Topic icon followed by its name. */
fun FlowContent.topicLabel(context: Context, talk: Talk) {
    span(classes = "mxt-topic-label") {
        img(src = talk.topic.iconSrc, alt = "", classes = "mxt-topic-label__icon")
        +context.i18n("topics.${talk.topic.value}.title")
    }
}

/**
 * Heart that adds or removes a talk from the favorites. Signed in: a form posting the toggle and
 * coming back to [redirect]. Anonymous: nothing, unless the talk is already a favorite.
 */
fun FlowContent.favoriteToggle(context: Context, talk: Talk, favorite: Boolean, redirect: String, withLabel: Boolean = false) {
    val label = context.i18n(if (favorite) "favorite.selected" else "favorite.nonselected")
    if (context.email == null) {
        if (withLabel) {
            a(href = "${context.uriBasePath}/login", classes = "mxt-favorite-button") {
                heartIcon(false)
                +label
            }
        }
        return
    }
    form(action = "/talks/${talk.id}/favorite", method = FormMethod.post, classes = "mxt-favorite-form") {
        input(type = InputType.hidden, name = "redirect") { value = redirect }
        button(
            type = ButtonType.submit,
            classes = if (withLabel) "mxt-favorite-button" else "mxt-favorite-heart",
        ) {
            attributes["aria-pressed"] = favorite.toString()
            if (!withLabel) attributes["aria-label"] = "$label : ${talk.title}"
            heartIcon(favorite)
            if (withLabel) +label
        }
    }
}

private fun FlowContent.heartIcon(filled: Boolean) {
    span(classes = "mxt-heart") {
        unsafe {
            raw(
                """<svg width="20" height="20" viewBox="0 0 24 24" fill="${if (filled) "currentColor" else "none"}" stroke="currentColor" stroke-width="2" stroke-linejoin="round" aria-hidden="true"><path d="$HEART_PATH"></path></svg>"""
            )
        }
    }
}

/** Card of a session in the programme: format, duration, favorite, title, speakers, topic and room. */
fun FlowContent.sessionCard(context: Context, talk: Talk, favorite: Boolean, redirect: String) {
    val keynote = talk.format.isKeynote()
    article(classes = if (keynote) "mxt-session mxt-session--keynote" else "mxt-session") {
        div(classes = "mxt-session__head") {
            formatBadge(context, talk)
            span(classes = "mxt-session__duration") { +talk.durationLabel() }
            favoriteToggle(context, talk, favorite, redirect)
        }
        a(href = context.talkHref(talk), classes = "mxt-session__title") { +talk.title }
        val names = talk.speakers.joinToString(" & ") { it.fullName() }
        if (names.isNotBlank()) span(classes = "mxt-session__speakers") { +names }
        div(classes = "mxt-session__meta") {
            topicLabel(context, talk)
            context.roomLabel(talk)?.let { room ->
                span { attributes["aria-hidden"] = "true"; +"·" }
                span { +room }
            }
        }
    }
}

/** Slot time ("10:25") of a talk, or null when it is not scheduled. */
fun Talk.slotLabel(): String? = startLocalTime()?.formatTime()

/** Round portrait of a speaker, or their initials when there is no photo. */
fun FlowContent.speakerPortrait(speaker: Speaker, classes: String) {
    if (speaker.photoUrl.isNullOrBlank()) {
        span(classes = "$classes mxt-portrait--initials") {
            attributes["aria-hidden"] = "true"
            +speaker.initials()
        }
    } else {
        img(src = speaker.photoUrl, alt = "", classes = classes) { attributes["loading"] = "lazy" }
    }
}

/** Speaker in a grid: portrait, name and company, linking to the speaker page. */
fun FlowContent.speakerTile(speaker: Speaker, href: String) {
    a(href = href, classes = "mxt-speaker-tile") {
        attributes["data-search"] = "${speaker.fullName()} ${speaker.company.orEmpty()}".lowercase()
        speakerPortrait(speaker, "mxt-speaker-tile__photo")
        strong(classes = "mxt-speaker-tile__name") { +speaker.fullName() }
        speaker.company?.takeIf { it.isNotBlank() }?.let { span(classes = "mxt-speaker-tile__company") { +it } }
    }
}

/** A talk of any edition: format, topic and year, then its title. Shows a play mark when a video exists. */
fun FlowContent.talkLinkCard(context: Context, talk: Talk) {
    a(href = context.talkHref(talk), classes = "mxt-talk-link") {
        if (talk.videos.isNotEmpty()) {
            span(classes = "mxt-talk-link__play") {
                attributes["aria-hidden"] = "true"
                unsafe { raw("""<svg width="18" height="18" viewBox="0 0 24 24"><path d="M8 5v14l11-7z" fill="currentColor"></path></svg>""") }
            }
        }
        span(classes = "mxt-talk-link__body") {
            span(classes = "mxt-talk-link__meta") {
                formatBadge(context, talk, talk.durationLabel())
                img(src = talk.topic.iconSrc, alt = "", classes = "mxt-topic-label__icon")
                span { +"${context.i18n("topics.${talk.topic.value}.title")} · ${talk.event}" }
            }
            strong(classes = "mxt-talk-link__title") { +talk.title }
        }
    }
}

/** Round buttons with the icon of each link (social networks, web site). */
fun FlowContent.iconLinks(links: List<Link>) {
    if (links.isEmpty()) return
    div(classes = "mxt-icon-links") {
        links.forEach { link ->
            a(href = link.url, classes = "mxt-icon-link") {
                attributes["target"] = "_blank"
                attributes["rel"] = "noopener"
                attributes["aria-label"] = link.name
                img(src = link.type.image ?: "/images/svg/social/mxt-icon--social-link-solid.svg", alt = "", classes = "mxt-icon-link__icon")
            }
        }
    }
}

/** YouTube thumbnail of a talk video, when the player URL gives the video id. */
private fun Talk.videoThumbnail(): String? {
    val url = videos.firstOrNull()?.playerUrl ?: return null
    val id = Regex("youtube\\.com/embed/([A-Za-z0-9_-]{6,})").find(url)?.groupValues?.get(1) ?: return null
    return "https://img.youtube.com/vi/$id/hqdefault.jpg"
}

/** Video of a talk: 16:9 thumbnail with a play mark and its label, then the title. */
fun FlowContent.videoCard(context: Context, talk: Talk, label: String) {
    a(href = context.talkHref(talk), classes = "mxt-video-card") {
        span(classes = "mxt-video-card__thumb") {
            talk.videoThumbnail()?.let { img(src = it, alt = "", classes = "mxt-video-card__img") { attributes["loading"] = "lazy" } }
            span(classes = "mxt-video-card__play") {
                attributes["aria-hidden"] = "true"
                unsafe { raw("""<svg width="20" height="20" viewBox="0 0 24 24"><path d="M8 5v14l11-7z" fill="currentColor"></path></svg>""") }
            }
            span(classes = "mxt-video-card__label") { +label }
        }
        strong(classes = "mxt-video-card__title") { +talk.title }
    }
}
