package org.mixit.conference.ui.page

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.html.ButtonType
import kotlinx.html.FlowContent
import kotlinx.html.InputType
import kotlinx.html.form
import kotlinx.html.input
import kotlinx.html.label
import kotlinx.html.nav
import kotlinx.html.p
import kotlinx.html.section
import org.mixit.conference.model.talk.TalkFormat
import org.mixit.conference.ui.CURRENT_TALK_YEAR
import org.mixit.conference.ui.component.Rubrique
import org.mixit.conference.ui.component.chipLink
import org.mixit.conference.ui.component.editionMenu
import org.mixit.conference.ui.component.iconSrc
import org.mixit.conference.ui.component.pageContent
import org.mixit.conference.ui.component.pageHeaderComponent
import org.mixit.conference.ui.component.sessionCard
import org.mixit.conference.ui.component.sponsorsSection
import org.mixit.conference.ui.formatter.formatDayDate
import kotlinx.html.DIV
import kotlinx.html.FormMethod
import kotlinx.html.a
import kotlinx.html.br
import kotlinx.html.button
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.id
import kotlinx.html.img
import kotlinx.html.small
import kotlinx.html.span
import kotlinx.html.title
import kotlinx.html.unsafe
import org.mixit.conference.model.event.Event
import org.mixit.conference.model.people.Sponsor
import org.mixit.conference.model.shared.Context
import org.mixit.conference.model.talk.Talk
import org.mixit.conference.shared.model.Topic
import org.mixit.conference.ui.CURRENT_YEAR
import org.mixit.conference.ui.TALKS_YEARS
import org.mixit.conference.ui.component.languageComponent
import org.mixit.conference.ui.component.roomComponent
import org.mixit.conference.ui.component.sectionComponent
import org.mixit.conference.ui.component.speakersComponentInDiv
import org.mixit.conference.ui.component.sponsorGroupComponent
import org.mixit.conference.ui.component.talkFormatComponent
import org.mixit.conference.ui.component.topicPrefixComponent
import org.mixit.conference.ui.component.yearSelectorComponent
import org.mixit.conference.ui.form.FormDescriptor
import org.mixit.conference.ui.form.FormField
import org.mixit.conference.ui.form.FormFieldType
import org.mixit.conference.ui.formatter.formatDate
import org.mixit.conference.ui.formatter.formatTime
import org.mixit.conference.ui.renderTemplate

data class TalksCriteria(val topic: Topic?, val filter: String?, val favorites: Boolean)

fun talkSearchForm(
    context: Context,
    values: TalksCriteria? = null,
    valuesInRequest: Map<String, String?> = emptyMap(),
    converter: (FormDescriptor<TalksCriteria>) -> TalksCriteria = { throw IllegalStateException() }
) = FormDescriptor(
    fields = listOf(
        FormField(
            "topic",
            type = FormFieldType.Select,
            fieldPlaceholder = context.i18n("talks.topic"),
            options = listOf("" to context.i18n("talks.topic.all")) + Topic.entries.map { it.name to context.i18n("topics.${it.value}.title") },
            defaultValue = values?.topic?.name ?: valuesInRequest["topic"]
        ),
        FormField(
            "filter",
            type = FormFieldType.Text,
            fieldPlaceholder = context.i18n("talks.filter"),
            defaultValue = values?.filter ?: valuesInRequest["filter"],
        ),
        FormField(
            "favorites",
            type = FormFieldType.Checkbox,
            help = context.i18n("talks.favorites"),
            defaultValue = values?.favorites?.toString() ?: valuesInRequest["favorites"],
        )

    ),
    converter = converter
)

fun renderTalks(
    context: Context,
    event: Event,
    sponsors: List<Sponsor>,
    favorites: List<String>,
    talksByDate: Map<LocalDateTime?, List<Talk>>,
    filter: FormDescriptor<TalksCriteria>,
    yearTalks: List<Talk> = talksByDate.values.flatten(),
) =
    renderTemplate(context, event) {
        val criteria = filter.value()
        val base = "${context.uriBasePath}/${event.year}"
        fun href(topic: Topic? = criteria.topic, favoritesOnly: Boolean = criteria.favorites): String {
            val params = listOfNotNull(
                topic?.let { "topic=${it.name}" },
                criteria.filter?.takeIf { it.isNotBlank() }?.let { "filter=${it.urlEncode()}" },
                if (favoritesOnly) "favorites=true" else null,
            )
            return if (params.isEmpty()) base else "$base?${params.joinToString("&")}"
        }
        val current = href()

        pageHeaderComponent(
            context,
            Rubrique.TALK,
            title = "${context.i18n("talks.title")} ${event.year}",
            intro = programmeSummary(context, yearTalks),
        ) {
            editionMenu(context.i18n("page.edition"), TALKS_YEARS, event.year) { "${context.uriBasePath}/$it" }
        }

        pageContent {
            val days = talksByDate.keys.mapNotNull { it?.date }.toSet().sorted()

            div(classes = "mxt-programme__toolbar") {
                if (days.size > 1) {
                    nav(classes = "mxt-programme__days") {
                        attributes["aria-label"] = context.i18n("talks.v2.days")
                        days.forEach { day ->
                            a(href = "#day-$day", classes = "mxt-programme__day") { +day.formatDayDate(context.language) }
                        }
                    }
                }
                div(classes = "mxt-programme__topics") {
                    attributes["role"] = "group"
                    attributes["aria-label"] = context.i18n("talks.topic")
                    chipLink(context.i18n("talks.topic.all"), href(topic = null), active = criteria.topic == null)
                    yearTalks.groupingBy { it.topic }.eachCount().entries
                        .sortedByDescending { it.value }
                        .forEach { (topic, count) ->
                            chipLink(
                                label = context.i18n("topics.${topic.value}.title"),
                                href = href(topic = if (criteria.topic == topic) null else topic),
                                active = criteria.topic == topic,
                                icon = topic.iconSrc,
                                count = count,
                            )
                        }
                    if (context.isAuthenticated) {
                        chipLink(
                            label = context.i18n("talks.v2.favorites"),
                            href = href(favoritesOnly = !criteria.favorites),
                            active = criteria.favorites,
                            icon = "/images/svg/favorites/mxt-favorite.svg",
                        )
                    }
                }
                form(action = base, method = FormMethod.get, classes = "mxt-search") {
                    attributes["role"] = "search"
                    criteria.topic?.let { input(type = InputType.hidden, name = "topic") { value = it.name } }
                    if (criteria.favorites) input(type = InputType.hidden, name = "favorites") { value = "true" }
                    label(classes = "visually-hidden") {
                        htmlFor = "programme-search"
                        +context.i18n("talks.v2.search")
                    }
                    input(type = InputType.search, name = "filter", classes = "mxt-search__input") {
                        id = "programme-search"
                        placeholder = context.i18n("talks.v2.search.placeholder")
                        value = criteria.filter.orEmpty()
                    }
                    button(type = ButtonType.submit, classes = "mxt-search__button") {
                        attributes["aria-label"] = context.i18n("talks.v2.search")
                        unsafe {
                            raw("""<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" aria-hidden="true"><circle cx="11" cy="11" r="7"></circle><line x1="16.5" y1="16.5" x2="21" y2="21"></line></svg>""")
                        }
                    }
                }
            }

            div(classes = "mxt-programme__notes") {
                if (event.year == CURRENT_TALK_YEAR && event.media?.schedulingFileUrl != null) {
                    a(href = event.media.schedulingFileUrl, classes = "mxt-button mxt-button--line") {
                        attributes["target"] = "_blank"
                        attributes["rel"] = "noopener"
                        +context.i18n("talks.pdf")
                    }
                }
                if (!context.isAuthenticated) {
                    p(classes = "mxt-programme__hint") {
                        a(href = "${context.uriBasePath}/login") { +context.i18n("talks.v2.login") }
                        +" ${context.i18n("talks.v2.login.why")}"
                    }
                }
            }

            if (talksByDate.isEmpty()) {
                div(classes = "mxt-empty") { +context.i18n("talks.v2.empty") }
            }

            // Scheduled sessions, day by day and slot by slot
            days.forEach { day ->
                div(classes = "mxt-programme__day-block") {
                    attributes["id"] = "day-$day"
                    if (days.size > 1) h2(classes = "mxt-block-title") { +day.formatDayDate(context.language) }
                    talksByDate.entries
                        .filter { it.key?.date == day }
                        .sortedBy { it.key }
                        .forEach { (time, talks) ->
                            slot(context, time?.formatTime().orEmpty(), talks, favorites, current)
                        }
                }
            }

            // Sessions without slot (older editions): grouped by format
            talksByDate[null]?.groupBy { it.format }?.entries?.sortedBy { it.key.order }?.forEach { (format, talks) ->
                slot(context, context.i18n("talk.format.${format.name}"), talks, favorites, current, byFormat = true)
            }
        }

        sponsorsSection(context, event, sponsors)
    }

/** "60 sessions · 4 keynotes · 32 talks · ..." for the page intro. */
private fun programmeSummary(context: Context, talks: List<Talk>): String {
    if (talks.isEmpty()) return context.i18n("talks.v2.intro.empty")
    val families = listOf(
        "keynote" to talks.count { it.format.isKeynote() },
        "talk" to talks.count { it.format == TalkFormat.TALK },
        "lightning" to talks.count { it.format == TalkFormat.LIGHTNING_TALK },
        "workshop" to talks.count { it.format == TalkFormat.WORKSHOP },
    ).filter { it.second > 0 }
    return (listOf("${talks.size} ${context.i18n("talks.v2.sessions")}") +
        families.map { (family, count) -> "$count ${context.i18n("talks.v2.stat.$family")}" })
        .joinToString(" · ")
}

/** A time slot: the hour (sticky on large screens) and its sessions. */
private fun FlowContent.slot(
    context: Context,
    label: String,
    talks: List<Talk>,
    favorites: List<String>,
    redirect: String,
    byFormat: Boolean = false,
) {
    section(classes = if (byFormat) "mxt-slot mxt-slot--format" else "mxt-slot") {
        div(classes = "mxt-slot__time") {
            if (byFormat) h2(classes = "mxt-slot__label") { +label } else span(classes = "mxt-slot__label") { +label }
            span(classes = "mxt-slot__count") {
                +"${talks.size} ${context.i18n(if (talks.size > 1) "talks.v2.sessions" else "talks.v2.session")}"
            }
        }
        div(classes = "mxt-slot__sessions") {
            talks.sortedWith(compareBy({ !it.format.isKeynote() }, { it.room.order }, { it.title })).forEach { talk ->
                sessionCard(context, talk, favorites.contains(talk.id), redirect)
            }
        }
    }
}

private fun String.urlEncode(): String =
    encodeToByteArray().joinToString("") { byte ->
        val c = (byte.toInt() and 0xff).toChar()
        if (c.isLetterOrDigit() && c.code < 128 || c in "-_.~") c.toString()
        else "%" + (byte.toInt() and 0xff).toString(16).uppercase().padStart(2, '0')
    }

fun DIV.displayTalk(talk: Talk, event: Event, context: Context, favorites: List<String>, svg: String? = null) {
    div(classes = "mxt-talks__container-box-line d-flex align-items-center") {
        if(svg != null) {
            div {
                unsafe {
                    raw(svg)
                }
            }
        }
        div(classes = "mxt-talks__container-line") {
            a(href = "${context.uriBasePath}/${event.year}/${talk.slug}") {
            topicPrefixComponent(context, talk.topic, talk.title)
            div(classes = "d-flex align-items-center") {
                languageComponent(context, talk)
                talkFormatComponent(context, talk)
                if (talk.videos.isNotEmpty()) {
                    img(classes = "mxt-img__header-video ms-2") {
                        attributes["aria-label"] = context.i18n("talks.video.available")
                        title = context.i18n("talks.video.available")
                    }
                }
                roomComponent(context, talk, withTime = true)


                if (context.email != null) {
                    if (favorites.contains(talk.id)) {
                        img(
                            src = "/images/svg/favorites/mxt-favorite.svg",
                            classes = "mxt-favorite"
                        ) {
                            alt = context.i18n("favorite.selected")
                        }
                    } else {
                        img(
                            src = "/images/svg/favorites/mxt-favorite-non.svg",
                            classes = "mxt-favorite"
                        ) {
                            alt = context.i18n("favorite.selected")
                        }
                    }
                }

            }


            div(classes = "mt-3 mxt-talks__detail mxt-no-link") {
                unsafe {
                    raw(context.markdown(talk.summary) + context.markdown(talk.description))
                }
            }
            span { +"..." }
            speakersComponentInDiv(context, talk.speakers)
            div(classes = "mb-2") { +"  " }
        }

        }
    }
}
