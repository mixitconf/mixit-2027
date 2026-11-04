package org.mixit.conference.ui.page

import kotlinx.html.FlowContent
import kotlinx.html.a
import kotlinx.html.div
import kotlinx.html.h3
import kotlinx.html.img
import kotlinx.html.li
import kotlinx.html.span
import kotlinx.html.strong
import kotlinx.html.ul
import kotlinx.html.unsafe
import org.mixit.conference.model.event.Event
import org.mixit.conference.model.people.Sponsor
import org.mixit.conference.model.people.SponsorshipLevel
import org.mixit.conference.model.shared.Context
import org.mixit.conference.ui.CONTACT_EMAIL
import org.mixit.conference.ui.CURRENT_YEAR
import org.mixit.conference.ui.YEARS
import org.mixit.conference.ui.component.Rubrique
import org.mixit.conference.ui.component.autoGrid
import org.mixit.conference.ui.component.banner
import org.mixit.conference.ui.component.editionMenu
import org.mixit.conference.ui.component.iconLinks
import org.mixit.conference.ui.component.pageContent
import org.mixit.conference.ui.component.pageHeaderComponent
import org.mixit.conference.ui.component.pageSection
import org.mixit.conference.ui.renderTemplate

fun renderSponsor(context: Context, event: Event, sponsors: List<Sponsor>) =
    renderTemplate(context, event) {
        pageHeaderComponent(
            context,
            Rubrique.SPONSOR,
            title = context.i18n("sponsor.title"),
            intro = context.i18n("sponsor.intro"),
        ) {
            editionMenu(context.i18n("page.edition"), YEARS, event.year) { "${context.uriBasePath}/$it/sponsors" }
        }

        pageContent {
            if (event.year == CURRENT_YEAR) {
                banner(
                    title = context.i18n("sponsor.v2.cta.title"),
                    text = context.i18n("sponsor.v2.cta.text"),
                    ctaLabel = context.i18n("sponsor.v2.cta.contact"),
                    ctaHref = "mailto:$CONTACT_EMAIL",
                    secondary = context.i18n("sponsor.v2.cta.leaflet") to context.i18n("sponsor.leaflet.url"),
                ) {
                    span { +context.i18n("sponsor.v2.forms") }
                    a(href = context.i18n("sponsor.mixit.form.url")) { +context.i18n("sponsor.mixit.form.name") }
                    a(href = context.i18n("sponsor.mixteen.form.url")) { +context.i18n("sponsor.mixteen.form.name") }
                }
            }

            pageSection("${context.i18n("sponsor.v2.list.title")} ${event.year}") {
                if (sponsors.isEmpty()) {
                    div(classes = "mxt-empty mxt-empty--rich") {
                        img(src = Rubrique.SPONSOR.src, alt = "", classes = "mxt-empty__icon")
                        strong { +context.i18n("sponsor.v2.empty.title") }
                        span { +context.i18n("sponsor.v2.empty.text") }
                    }
                } else {
                    sponsors
                        .groupBy { it.level }
                        .entries
                        .sortedByDescending { it.key.priority }
                        .forEach { (level, levelSponsors) ->
                            div(classes = "mxt-sponsor-level") {
                                h3(classes = "mxt-sponsor-level__title") { +context.i18n("sponsor.level.${level.name}") }
                                autoGrid("300px") {
                                    levelSponsors.sortedBy { it.name.lowercase() }.forEach { sponsorCard(context, it) }
                                }
                            }
                        }
                }
            }

            if (event.year == CURRENT_YEAR) {
                pageSection(context.i18n("sponsor.v2.packs.title"), context.i18n("sponsor.v2.packs.intro")) {
                    ul(classes = "mxt-tags") {
                        SponsorshipLevel.entries.forEach { li(classes = "mxt-tag") { +context.i18n("sponsor.level.${it.name}") } }
                    }
                }
            }
        }
    }

/** A sponsor: logo on a white plate (logos are made for light backgrounds), name, description and links. */
private fun FlowContent.sponsorCard(context: Context, sponsor: Sponsor) {
    div(classes = "mxt-sponsor-card") {
        attributes["id"] = sponsor.name.replace(" ", "-")
        div(classes = "mxt-sponsor-card__logo") {
            sponsor.photoUrl?.let { img(src = it.replace("https://mixitconf.org", ""), alt = sponsor.name) { attributes["loading"] = "lazy" } }
        }
        strong(classes = "mxt-sponsor-card__name") { +sponsor.name }
        val description = sponsor.description[context.language] ?: sponsor.description.values.firstOrNull()
        if (!description.isNullOrBlank()) {
            div(classes = "mxt-sponsor-card__text") { unsafe { raw(context.markdown(description)) } }
        }
        iconLinks(sponsor.links)
    }
}
