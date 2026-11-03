package org.mixit.conference.ui.page

import kotlinx.html.DIV
import kotlinx.html.a
import kotlinx.html.br
import kotlinx.html.div
import kotlinx.html.h3
import kotlinx.html.img
import kotlinx.html.li
import kotlinx.html.ol
import kotlinx.html.p
import kotlinx.html.span
import kotlinx.html.unsafe
import org.mixit.conference.model.event.Event
import org.mixit.conference.model.people.Sponsor
import org.mixit.conference.model.shared.Context
import org.mixit.conference.model.talk.Talk
import org.mixit.conference.ui.component.SectionEffect
import org.mixit.conference.ui.component.SectionStyle
import org.mixit.conference.ui.component.podcastComponent
import org.mixit.conference.ui.component.podcastLinks
import org.mixit.conference.ui.component.sectionComponent
import org.mixit.conference.ui.component.sponsorGroupComponent
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

fun renderHomePage(context: Context, lastPodCastId: String, event: Event, sponsors: List<Sponsor>, keynotes: List<Talk>) =
    renderTemplate(context, event) {
        // Hero: historical tagline + next edition announcement
        sectionComponent(context, effect = SectionEffect.END, style = SectionStyle.DARK) {
            div(classes = "mxt-text__wrapper") {
                +context.i18n("home.wrapper.start")
                span(classes = "mxt-text__third") {
                    +" ${context.i18n("home.wrapper.ethic")} "
                }
                br
                +context.i18n("home.wrapper.crepe")
                span(classes = "mxt-text__secondary") {
                    +" ${context.i18n("home.wrapper.love")} "
                }
            }
            div(classes = "mxt-home__hero") {
                div(classes = "mxt-home__hero-actions") {
                    a(classes = "mxt-home__button mxt-home__button--primary", href = "#home.timeline.title") {
                        +context.i18n("home.hero.dates")
                    }
                    a(classes = "mxt-home__button", href = "/newsletter-subscribe") {
                        target = "newsletter"
                        +context.i18n("home.hero.newsletter")
                    }
                }
            }
        }

        // Why this edition is different
        sectionComponent(context, i18nKey = "home.section.news.title") {
            div(classes = "mxt-home__manifesto") {
                p(classes = "lead") { +context.i18n("home.section.news.intro") }
                p { +context.i18n("home.section.news.focus") }
                p(classes = "mxt-home__quote") { +context.i18n("home.section.news.change") }
            }
        }

        // Formats
        sectionComponent(
            context,
            i18nKey = "home.section.formats.title",
            effect = SectionEffect.BOTH,
            style = SectionStyle.LIGHT,
        ) {
            div(classes = "mxt-home__cards") {
                EDITION_FORMATS.forEachIndexed { index, format ->
                    div(classes = "mxt-home__card") {
                        div(classes = "mxt-home__card-index") { +"0${index + 1}" }
                        h3 { +context.i18n("home.section.formats.$format.title") }
                        div { +context.i18n("home.section.formats.$format.desc") }
                    }
                }
            }
        }

        // Subjects
        sectionComponent(context, i18nKey = "home.section.editorialline.title") {
            div(classes = "mxt-home__cards") {
                EDITION_SUBJECTS.forEach { (subject, icon) ->
                    div(classes = "mxt-home__card mxt-home__card--subject") {
                        img(classes = "mxt-img__topic-home") {
                            src = "/images/svg/topic/mxt-icon--$icon.svg"
                            alt = ""
                        }
                        h3 { +context.i18n("home.section.subjects.$subject.title") }
                        div { +context.i18n("home.section.subjects.$subject.desc") }
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
            ol(classes = "mxt-home__timeline") {
                EDITION_TIMELINE.forEach { step ->
                    li(classes = "mxt-home__timeline-step") {
                        div(classes = "mxt-home__timeline-date") { +context.i18n("home.timeline.$step.date") }
                        div(classes = "mxt-home__timeline-label") { +context.i18n("home.timeline.$step") }
                    }
                }
            }
            div(classes = "mxt-home__closing") {
                +context.i18n("home.section.news.closing")
            }
        }

        sectionComponent(context, i18nKey = "home.section.onair.title") {
            div("lead") { +context.i18n("home.section.onair.subtitle") }
            div("mt-2") { +context.i18n("home.section.onair.description") }
            podcastComponent(lastPodCastId)
            div { +context.i18n("home.section.onair.joinus") }
            podcastLinks()
        }
        sectionComponent(
            context,
            i18nKey = "home.section.values.title",
            effect = SectionEffect.BOTH,
            style = SectionStyle.LIGHT,
        ) {
            renderValueBlock(context, "accessibility")
            renderValueBlock(context, "safe")
            renderValueBlock(context, "eco")
            div(classes = "mxt-no-link mt-4 pb-3") {
                unsafe { raw(context.markdown(context.i18n("home.section.news.newsletter"))) }
            }
        }
        sectionComponent(context) {
            sponsorGroupComponent(context, event, sponsors)
        }
    }

fun DIV.renderValueBlock(context: Context, category: String) {
    div(classes = "d-flex align-items-center pt-3") {
        div {
            img(classes = "mxt-icon__category-$category") {
                alt = context.i18n("home.section.values.$category.title")
            }
        }
        div {
            h3 { +context.i18n("home.section.values.$category.title") }
        }
    }
    div {
        +context.i18n("home.section.values.$category.desc")
    }
}
