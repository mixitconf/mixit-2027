package org.mixit.conference.ui.page

import kotlinx.html.FlowContent
import kotlinx.html.a
import kotlinx.html.details
import kotlinx.html.div
import kotlinx.html.h2
import kotlinx.html.img
import kotlinx.html.li
import kotlinx.html.ol
import kotlinx.html.p
import kotlinx.html.span
import kotlinx.html.strong
import kotlinx.html.summary
import org.mixit.conference.model.event.Event
import org.mixit.conference.model.people.Sponsor
import org.mixit.conference.model.shared.Context
import org.mixit.conference.model.talk.Talk
import org.mixit.conference.model.talk.TalkFormat
import org.mixit.conference.model.talk.Theme
import org.mixit.conference.shared.model.Topic
import org.mixit.conference.ui.component.Crumb
import org.mixit.conference.ui.component.Rubrique
import org.mixit.conference.ui.component.iconSrc
import org.mixit.conference.ui.component.pageContent
import org.mixit.conference.ui.component.pageHeaderComponent
import org.mixit.conference.ui.component.pageSection
import org.mixit.conference.ui.component.sponsorsSection
import org.mixit.conference.ui.component.videoCard
import org.mixit.conference.ui.renderTemplate

private const val SPOTLIGHT_TALKS = 3

fun renderArchives(
    context: Context,
    event: Event,
    @Suppress("UNUSED_PARAMETER") sponsors: List<Sponsor>,
    talksByTheme: Map<Theme, List<Talk>>,
    talksByTopic: Map<Topic, List<Talk>>,
) =
    renderTemplate(context, event) {
        val allTalks = (talksByTheme.values.flatten() + talksByTopic.values.flatten()).distinctBy { it.id }
        val themes = Theme.entries.filter { talksByTheme[it].orEmpty().isNotEmpty() }
            .sortedByDescending { talksByTheme.getValue(it).size }
        val years = allTalks.mapNotNull { it.event.toIntOrNull() }.distinct().sortedDescending()

        pageHeaderComponent(
            context,
            Rubrique.VIDEO,
            title = context.i18n("archives.v2.title"),
            intro = context.i18n("archives.v2.intro"),
            meta = {
                stat(allTalks.size, context.i18n("archives.topic.count"))
                stat(years.size, context.i18n("archives.v2.editions"))
                stat(themes.size, context.i18n("archives.v2.themes"))
            },
        )

        pageContent {
            // A featured theme, picked at random
            themes.filter { it.featured }.randomOrNull()?.let { spotlight ->
                val spotTalks = talksByTheme.getValue(spotlight)
                div(classes = "mxt-spotlight") {
                    div(classes = "mxt-spotlight__text") {
                        span(classes = "mxt-eyebrow") { +context.i18n("archives.spotlight.theme") }
                        h2(classes = "mxt-spotlight__title") { +context.i18n("themes.${spotlight.slug}.title") }
                        p { +context.i18n("themes.${spotlight.slug}.description") }
                        a(href = "${context.uriBasePath}/archives/${spotlight.slug}", classes = "mxt-button mxt-button--primary") {
                            +"${context.i18n("archives.theme.more")} · ${spotTalks.size}"
                        }
                    }
                    div(classes = "mxt-spotlight__videos") {
                        spotTalks.shuffled().take(SPOTLIGHT_TALKS).forEach { videoCard(context, it, talkLabel(context, it)) }
                    }
                }
            }

            pageSection(context.i18n("archives.bytheme.title"), context.i18n("archives.bytheme.intro")) {
                div(classes = "mxt-theme-grid") {
                    themes.filter { it.featured }.forEach { themeCard(context, it, talksByTheme.getValue(it).size) }
                }
                val others = themes.filterNot { it.featured }
                if (others.isNotEmpty()) {
                    details(classes = "mxt-more") {
                        summary(classes = "mxt-button mxt-button--line mxt-more__toggle") {
                            +"${context.i18n("archives.themes.more")} (${others.size})"
                        }
                        div(classes = "mxt-theme-grid") {
                            others.forEach { themeCard(context, it, talksByTheme.getValue(it).size) }
                        }
                    }
                }
            }

            pageSection(context.i18n("archives.byedition.title"), context.i18n("archives.byedition.intro")) {
                ol(classes = "mxt-year-list") {
                    years.forEach { year ->
                        val count = allTalks.count { it.event == year.toString() }
                        li {
                            a(href = "${context.uriBasePath}/archives/year/$year", classes = "mxt-year-list__link") {
                                span(classes = "mxt-year-list__year") { +"$year" }
                                span(classes = "mxt-year-list__count") { +"$count ${context.i18n("archives.year.videos")}" }
                            }
                        }
                    }
                }
            }

            pageSection(context.i18n("archives.bytopic.title"), context.i18n("archives.bytopic.intro")) {
                div(classes = "mxt-topic-grid") {
                    Topic.entries
                        .filter { it != Topic.OTHER && talksByTopic[it].orEmpty().isNotEmpty() }
                        .sortedByDescending { talksByTopic.getValue(it).size }
                        .forEach { topic ->
                            a(href = "${context.uriBasePath}/archives/topic/${topic.value}", classes = "mxt-topic-card") {
                                img(src = topic.iconSrc, alt = "", classes = "mxt-topic-card__icon")
                                strong { +context.i18n("topics.${topic.value}.title") }
                                span(classes = "mxt-topic-card__count") { +"${talksByTopic.getValue(topic).size}" }
                            }
                        }
                }
            }
        }
    }

fun renderArchiveTheme(
    context: Context,
    event: Event,
    @Suppress("UNUSED_PARAMETER") sponsors: List<Sponsor>,
    theme: Theme,
    talks: List<Talk>,
) =
    renderTemplate(context, event) {
        archiveHeader(context, context.i18n("themes.${theme.slug}.title"), context.i18n("themes.${theme.slug}.description"), talks.size)
        pageContent { archiveTalkSections(context, talks) }
    }

fun renderArchiveTopic(
    context: Context,
    event: Event,
    @Suppress("UNUSED_PARAMETER") sponsors: List<Sponsor>,
    topic: Topic,
    talks: List<Talk>,
) =
    renderTemplate(context, event) {
        archiveHeader(
            context,
            context.i18n("topics.${topic.value}.title"),
            context.i18n("topics.${topic.value}.description"),
            talks.size,
            icon = topic.iconSrc,
        )
        pageContent { archiveTalkSections(context, talks) }
    }

fun renderArchiveYear(
    context: Context,
    event: Event,
    sponsors: List<Sponsor>,
    year: Int,
    talks: List<Talk>,
    sponsorEvent: Event? = null,
) =
    renderTemplate(context, event) {
        archiveHeader(context, "MiXiT $year", null, talks.size) {
            a(href = "${context.uriBasePath}/$year/media", classes = "mxt-button mxt-button--line mxt-page-header__button") {
                +context.i18n("archives.year.media")
            }
        }
        pageContent { archiveTalkSections(context, talks) }
        // Sponsors of that edition, at the bottom of the page
        sponsorsSection(context, sponsorEvent ?: event, sponsors)
    }

/** Header of an archive listing: breadcrumb back to the archives, title, description and count. */
private fun kotlinx.html.DIV.archiveHeader(
    context: Context,
    title: String,
    intro: String?,
    count: Int,
    icon: String? = null,
    aside: (kotlinx.html.DIV.() -> Unit)? = null,
) {
    pageHeaderComponent(
        context,
        rubrique = if (icon == null) Rubrique.VIDEO else null,
        title = title,
        intro = intro,
        breadcrumb = listOf(Crumb(context.i18n("media.v2.crumb"), "${context.uriBasePath}/archives")),
        meta = { span { +"$count ${context.i18n("archives.topic.count")}" } },
        leading = icon?.let { src -> { img(src = src, alt = "", classes = "mxt-page-header__icon") } },
        aside = aside,
    )
}

private fun FlowContent.stat(value: Int, label: String) {
    span(classes = "mxt-header-stat") {
        strong { +"$value" }
        +" $label"
    }
}

private fun talkLabel(context: Context, talk: Talk): String =
    if (talk.format.isKeynote()) "${context.i18n("talk.format.KEYNOTE")} · ${talk.event}"
    else "${context.i18n("topics.${talk.topic.value}.title")} · ${talk.event}"

private fun FlowContent.archiveTalkSections(context: Context, talks: List<Talk>) {
    val sorted = talks.sortedWith(compareByDescending<Talk> { it.event }.thenBy { it.title })
    listOf(
        "archives.keynotes" to sorted.filter { it.format.isKeynote() },
        "archives.talks" to sorted.filter { !it.format.isKeynote() && it.format != TalkFormat.ON_AIR },
        "archives.podcasts" to sorted.filter { it.format == TalkFormat.ON_AIR },
    ).filter { it.second.isNotEmpty() }.forEach { (key, list) ->
        pageSection("${context.i18n(key)} (${list.size})") {
            div(classes = "mxt-video-grid") {
                list.forEach { videoCard(context, it, talkLabel(context, it)) }
            }
        }
    }
}

private fun FlowContent.themeCard(context: Context, theme: Theme, count: Int) {
    a(href = "${context.uriBasePath}/archives/${theme.slug}", classes = "mxt-theme-card") {
        img(src = theme.imageUrl, alt = "", classes = "mxt-theme-card__img") { attributes["loading"] = "lazy" }
        span(classes = "mxt-theme-card__body") {
            strong { +context.i18n("themes.${theme.slug}.title") }
            span { +"$count ${context.i18n("archives.topic.count")}" }
        }
    }
}
