package org.mixit.conference.ui.page

import kotlinx.html.*
import org.mixit.conference.model.event.Event
import org.mixit.conference.model.people.Sponsor
import org.mixit.conference.model.shared.Context
import org.mixit.conference.model.talk.Talk
import org.mixit.conference.model.talk.TalkFormat
import org.mixit.conference.model.talk.Theme
import org.mixit.conference.shared.model.Topic
import org.mixit.conference.ui.MEDIA_YEARS
import org.mixit.conference.ui.component.sectionComponent
import org.mixit.conference.ui.component.sponsorGroupComponent
import org.mixit.conference.ui.component.talkCardComponent
import org.mixit.conference.ui.renderTemplate
import kotlin.random.Random

private const val TALKS_PER_THEME = 1
private const val EXPLORE_TALKS = 3

fun renderArchives(
    context: Context,
    event: Event,
    sponsors: List<Sponsor>,
    talksByTheme: Map<Theme, List<Talk>>,
    talksByTopic: Map<Topic, List<Talk>>,
) =
    renderTemplate(context, event) {
        sectionComponent(context) {
            val pageTitle = context.i18n("archives.title")
            h1 {
                img(classes = "mxt-img__header-video me-2") {
                    alt = pageTitle
                }
                +pageTitle
            }
            p(classes = "lead") { +context.i18n("archives.intro") }

            val allTalks = (talksByTheme.values.flatten() + talksByTopic.values.flatten()).distinctBy { it.id }
            val themes = Theme.entries.filter { talksByTheme[it].orEmpty().isNotEmpty() }
            h2(classes = "mt-4") { +context.i18n("archives.bytheme.title") }
            p { +context.i18n("archives.bytheme.intro") }
            div(classes = "mxt-archives__topics mxt-archives__topics--themes") {
                Theme.entries.filter { it.featured }.forEach { themeCard(context, it, talksByTheme[it].orEmpty().size) }
            }
            details(classes = "mxt-archives__more-themes") {
                summary(classes = "mxt-btn-secondary") { +context.i18n("archives.themes.more") }
                div(classes = "mxt-archives__topics mxt-archives__topics--themes") {
                    Theme.entries.filterNot { it.featured }.forEach { themeCard(context, it, talksByTheme[it].orEmpty().size) }
                }
            }

            val spotlight = themes.filter { it.featured }.randomOrNull()
            if (spotlight != null) {
                val spotTalks = talksByTheme.getValue(spotlight)
                p(classes = "mt-4 lead fw-bold") {
                    +context.i18n("archives.spotlight.theme")
                    +" : ${context.i18n("themes.${spotlight.slug}.title")}"
                }
                div(classes = "mxt-talk-cards mt-3") {
                    spotTalks.shuffled().take(EXPLORE_TALKS).forEach { talkCardComponent(context, it) }
                }
                p(classes = "mt-3") {
                    a(href = "${context.uriBasePath}/archives/${spotlight.slug}", classes = "btn btn-custom") {
                        +context.i18n("archives.theme.more")
                        +" (${spotTalks.size})"
                    }
                }
            }


            h2(classes = "mt-5") { +context.i18n("archives.byedition.title") }
            p { +context.i18n("archives.byedition.intro") }
            div(classes = "mxt-archives__topics") {
                MEDIA_YEARS.forEach { year ->
                    val yearTalks = allTalks.filter { it.event == year.toString() }
                    a(href = "${context.uriBasePath}/$year/media", classes = "mxt-archives__topic") {
                        span(classes = "mxt-archives__year") { +"$year" }
                        span(classes = "mxt-archives__topic-body") {
                            span(classes = "mxt-archives__topic-title") { +"MiXiT $year" }
                            span(classes = "mxt-archives__topic-count") {
                                +("${yearTalks.size} ${context.i18n("archives.topic.count")} · " +
                                    "${yearTalks.map { it.videos.size }.sum()} ${context.i18n("archives.year.videos")} · " +
                                    "${yearTalks.map { it.photos.size }.sum()} ${context.i18n("archives.year.photos")}")
                            }
                        }
                    }
                }
            }

            val pastYears = allTalks.mapNotNull { it.event.toIntOrNull() }.distinct().sorted()
            val lastEdition = pastYears.filter { it < event.year }.lastOrNull() ?: pastYears.lastOrNull()
            if (lastEdition != null) {
                p(classes = "mt-4 lead fw-bold") { +"${context.i18n("archives.explore")} : MiXiT $lastEdition" }
                div(classes = "mxt-talk-cards mt-3") {
                    allTalks.filter { it.event == lastEdition.toString() }.shuffled(Random)
                        .take(EXPLORE_TALKS).forEach { talkCardComponent(context, it) }
                }
                p(classes = "mt-3") {
                    a(href = "${context.uriBasePath}/archives/year/$lastEdition", classes = "btn btn-custom") {
                        +context.i18n("archives.theme.more")
                    }
                }
            }


            val topics = Topic.entries.filter { it != Topic.OTHER && talksByTopic[it].orEmpty().isNotEmpty() }
            h2(classes = "mt-5") { +context.i18n("archives.bytopic.title") }
            p { +context.i18n("archives.bytopic.intro") }
            div(classes = "mxt-archives__topics") {
                topics.forEach { topic ->
                    val label = context.i18n("topics.${topic.value}.title")
                    a(href = "${context.uriBasePath}/archives/topic/${topic.value}", classes = "mxt-archives__topic") {
                        img(classes = "mxt-archives__topic-icon") {
                            src = "/images/svg/topic/mxt-icon--${topic.value}.svg"
                            alt = ""
                        }
                        span(classes = "mxt-archives__topic-body") {
                            span(classes = "mxt-archives__topic-title") { +label }
                            span(classes = "mxt-archives__topic-count") {
                                +"${talksByTopic.getValue(topic).size} ${context.i18n("archives.topic.count")}"
                            }
                        }
                    }
                }
            }

            val topicSpot = topics.randomOrNull()
            if (topicSpot != null) {
                val topicTalks = talksByTopic.getValue(topicSpot)
                h3(classes = "mt-4") { +"${context.i18n("archives.explore")} : ${context.i18n("topics.${topicSpot.value}.title")}" }
                div(classes = "mxt-talk-cards mt-3") {
                    topicTalks.shuffled().take(EXPLORE_TALKS).forEach { talkCardComponent(context, it) }
                }
                p(classes = "mt-3") {
                    a(href = "${context.uriBasePath}/archives/topic/${topicSpot.value}", classes = "btn btn-custom") {
                        +context.i18n("archives.theme.more")
                        +" (${topicTalks.size})"
                    }
                }
            }
        }
        sectionComponent(context) {
            sponsorGroupComponent(context, event, sponsors)
        }
    }

fun renderArchiveTheme(
    context: Context,
    event: Event,
    sponsors: List<Sponsor>,
    theme: Theme,
    talks: List<Talk>,
) =
    renderTemplate(context, event) {
        sectionComponent(context) {
            val title = context.i18n("themes.${theme.slug}.title")
            archiveBackButton(context)
            div(classes = "d-flex mt-3") {
                img(classes = "mxt-archives__thumb me-3") {
                    src = theme.imageUrl
                    alt = ""
                }
                h1(classes = "mb-0") { +title }
            }
                    div(classes = "mxt-archives__theme-intro") {
                        p(classes = "lead mb-1") { +context.i18n("themes.${theme.slug}.description") }
                        p(classes = "text-muted mb-0") { +"${talks.size} talks" }
                    }


            archiveTalkSections(context, talks)
        }
        sectionComponent(context) {
            sponsorGroupComponent(context, event, sponsors)
        }
    }

fun renderArchiveTopic(
    context: Context,
    event: Event,
    sponsors: List<Sponsor>,
    topic: Topic,
    talks: List<Talk>,
) =
    renderTemplate(context, event) {
        sectionComponent(context) {
            val title = context.i18n("topics.${topic.value}.title")
            archiveBackButton(context)
            div(classes = "mxt-archives__topic-header mt-3") {
                img(classes = "mxt-archives__topic-icon") {
                    src = "/images/svg/topic/mxt-icon--${topic.value}.svg"
                    alt = ""
                }
                div {
                    h1 { +title }
                    p(classes = "lead mb-0") { +context.i18n("topics.${topic.value}.description") }
                }
            }
            archiveTalkSections(context, talks)
        }
        sectionComponent(context) {
            sponsorGroupComponent(context, event, sponsors)
        }
    }

fun FlowContent.archiveBackButton(context: Context) {
    a(href = "${context.uriBasePath}/archives", classes = "btn btn-custom mt-3") { +context.i18n("archives.back") }
}

fun renderArchiveYear(
    context: Context,
    event: Event,
    sponsors: List<Sponsor>,
    year: Int,
    talks: List<Talk>,
) =
    renderTemplate(context, event) {
        sectionComponent(context) {
            archiveBackButton(context)
            div(classes = "mxt-archives__topic-header mt-3") {
                div {
                    h1 { +"MiXiT $year" }
                    p(classes = "lead mb-0") {
                        a(href = "${context.uriBasePath}/$year/media") { +context.i18n("archives.year.media") }
                    }
                }
            }
            archiveTalkSections(context, talks)
        }
        sectionComponent(context) {
            sponsorGroupComponent(context, event, sponsors)
        }
    }

private fun FlowContent.archiveTalkSections(context: Context, talks: List<Talk>) {
    val sorted = talks.sortedWith(compareByDescending<Talk> { it.event }.thenBy { it.title })
    val sections = listOf(
        "archives.keynotes" to sorted.filter { it.format == TalkFormat.KEYNOTE },
        "archives.talks" to sorted.filter { it.format != TalkFormat.KEYNOTE && it.format != TalkFormat.ON_AIR },
        "archives.podcasts" to sorted.filter { it.format == TalkFormat.ON_AIR },
    )
    sections.filter { it.second.isNotEmpty() }.forEach { (key, list) ->
        val featured = key == "archives.keynotes"
        h2(classes = "mt-5") { +context.i18n(key) }
        div(classes = "mxt-talk-cards" + if (featured) " mxt-talk-cards--featured" else "") {
            list.forEach { talkCardComponent(context, it, featured = featured) }
        }
    }
}

private fun FlowContent.themeCard(context: Context, theme: Theme, count: Int) {
    a(href = "${context.uriBasePath}/archives/${theme.slug}", classes = "mxt-archives__topic") {
        img(classes = "mxt-archives__topic-icon mxt-archives__thumb") {
            src = theme.imageUrl
            alt = ""
            attributes["loading"] = "lazy"
        }
        span(classes = "mxt-archives__topic-body") {
            span(classes = "mxt-archives__topic-title") { +context.i18n("themes.${theme.slug}.title") }
            span(classes = "mxt-archives__topic-count") { +"$count ${context.i18n("archives.topic.count")}" }
        }
    }
}
