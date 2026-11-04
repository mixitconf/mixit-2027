package org.mixit.conference.ui.page

import kotlinx.html.a
import kotlinx.html.br
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.p
import kotlinx.html.span
import org.mixit.conference.model.event.Event
import org.mixit.conference.model.people.Sponsor
import org.mixit.conference.model.shared.Context
import org.mixit.conference.model.talk.Talk
import org.mixit.conference.ui.HEADER_CTA
import org.mixit.conference.ui.PODCASTS
import org.mixit.conference.ui.component.Definition
import org.mixit.conference.ui.component.SectionEffect
import org.mixit.conference.ui.component.SectionStyle
import org.mixit.conference.ui.component.TimelineStep
import org.mixit.conference.ui.component.blockTitle
import org.mixit.conference.ui.component.callout
import org.mixit.conference.ui.component.cfpButton
import org.mixit.conference.ui.component.cardGrid
import org.mixit.conference.ui.component.definitionList
import org.mixit.conference.ui.component.featureCard
import org.mixit.conference.ui.component.mediaItem
import org.mixit.conference.ui.component.navHref
import org.mixit.conference.ui.component.sectionComponent
import org.mixit.conference.ui.component.sponsorsSection
import org.mixit.conference.ui.component.timeline
import org.mixit.conference.ui.component.topicIconSrc
import org.mixit.conference.ui.renderTemplate

/** Formats announced for the next edition (i18n suffix under home.section.formats). */
private val EDITION_FORMATS = listOf("cfp", "participative", "keynotes", "roundtables")

/** Subjects announced for the next edition, with the topic icon used to illustrate them. */
private val EDITION_SUBJECTS = listOf(
    "tech" to "tech",
    "team" to "team",
    "ethics" to "ethics",
    "social" to "hacktivism",
)

/** Key dates of the next edition (i18n suffix under home.timeline). */
private val EDITION_TIMELINE = listOf("cfp.open", "cfp.close", "program", "tickets.open", "tickets.close")

/** Our values (i18n suffix under home.section.values), illustrated by the values-v2 icons. */
private val VALUES = listOf("accessibility", "safe", "eco")

private val PODCAST_HOME = PODCASTS.first { it.key == "ausha" }.url

fun renderHomePage(
    context: Context,
    @Suppress("UNUSED_PARAMETER") lastPodCastId: String,
    event: Event,
    sponsors: List<Sponsor>,
    @Suppress("UNUSED_PARAMETER") keynotes: List<Talk>,
) = renderTemplate(context, event) {
    // Hero: when and where, the historical tagline and the main call to action
    sectionComponent(context, effect = SectionEffect.END, style = SectionStyle.DARK) {
        div(classes = "mxt-home__hero") {
            p(classes = "mxt-home__when") {
                span(classes = "mxt-home__when-date") { +context.i18n("home.hero.date") }
                span(classes = "mxt-home__when-place") { +context.i18n("home.hero.place") }
            }
            h1(classes = "mxt-home__title") {
                // Properties values lose their leading spaces: the separators are added here.
                +"${context.i18n("home.wrapper.start").trim()} "
                span(classes = "mxt-text__third") { +context.i18n("home.wrapper.ethic").trim() }
                +" ${context.i18n("home.wrapper.crepe").trim()} "
                span(classes = "mxt-text__secondary") { +context.i18n("home.wrapper.love").trim() }
            }
            cfpButton(
                context,
                "mxt-button mxt-button--primary mxt-home__cta",
                openLabel = context.i18n(HEADER_CTA.title),
                closedLabel = context.i18n("home.hero.cfp"),
            )
        }
    }

    // Why this edition is different
    sectionComponent(context) {
        div(classes = "mxt-home__manifesto") {
            div(classes = "mxt-home__manifesto-heading") {
                p(classes = "mxt-eyebrow mxt-eyebrow--muted") { +context.i18n("nav.edition.current") }
                h2(classes = "mxt-home__manifesto-title") { +context.i18n("home.section.news.title") }
                p(classes = "mxt-home__manifesto-lead") { +context.i18n("home.section.news.intro") }
            }
            div(classes = "mxt-home__manifesto-body") {
                p { +context.i18n("home.section.news.focus") }
                p { +context.i18n("home.section.news.change") }
            }
        }
    }

    // Formats and subjects, side by side on large screens
    sectionComponent(context, effect = SectionEffect.BOTH, style = SectionStyle.LIGHT) {
        div(classes = "mxt-home__split") {
            div {
                blockTitle(context.i18n("home.section.formats.title"))
                definitionList(EDITION_FORMATS.map {
                    Definition(
                        context.i18n("home.section.formats.$it.title"),
                        context.i18n("home.section.formats.$it.desc"),
                    )
                })
            }
            div {
                blockTitle(context.i18n("home.section.subjects.title"))
                cardGrid {
                    EDITION_SUBJECTS.forEach { (subject, icon) ->
                        featureCard(
                            icon = topicIconSrc(icon),
                            title = context.i18n("home.section.subjects.$subject.title"),
                            description = context.i18n("home.section.subjects.$subject.desc"),
                        )
                    }
                }
            }
        }
    }

    // Key dates
    sectionComponent(
        context,
        i18nKey = "home.timeline.title",
        effect = SectionEffect.BOTH,
        style = SectionStyle.DARK,
    ) {
        timeline(EDITION_TIMELINE.map {
            TimelineStep(context.i18n("home.timeline.$it.date"), context.i18n("home.timeline.$it"))
        })
    }

    // Podcast
    sectionComponent(context) {
        div(classes = "mxt-home__podcast") {
            callout(
                icon = topicIconSrc("onair"),
                title = context.i18n("home.section.onair.card.title"),
                description = context.i18n("home.section.onair.card.text"),
                ctaLabel = context.i18n("home.section.onair.card.cta"),
                ctaHref = PODCAST_HOME,
                titleId = "home.section.onair.title",
                external = true,
            )
        }
    }

    // Values
    sectionComponent(context) {
        div(classes = "mxt-home__values") {
            blockTitle(context.i18n("home.section.values.title"), id = "home.section.values.title")
            div(classes = "mxt-home__values-list") {
                VALUES.forEach {
                    mediaItem(
                        icon = "/images/svg/value/mxt-value-$it.svg",
                        title = context.i18n("home.section.values.$it.title"),
                        description = context.i18n("home.section.values.$it.short"),
                    )
                }
            }
        }
    }

    // Sponsors, only once the partners of the edition are known
    sponsorsSection(context, event, sponsors)
}
