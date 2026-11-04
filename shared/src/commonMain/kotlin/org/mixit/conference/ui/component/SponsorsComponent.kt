package org.mixit.conference.ui.component

import kotlinx.html.DIV
import kotlinx.html.FlowContent
import kotlinx.html.a
import kotlinx.html.div
import kotlinx.html.h2
import kotlinx.html.img
import kotlinx.html.li
import kotlinx.html.span
import kotlinx.html.ul
import org.mixit.conference.model.event.Event
import org.mixit.conference.model.people.Sponsor
import org.mixit.conference.model.people.SponsorshipLevel
import org.mixit.conference.model.shared.Context

/** Packs whose sponsors get the large logos. */
private val MAIN_LEVELS = setOf(SponsorshipLevel.GOLD, SponsorshipLevel.LANYARD, SponsorshipLevel.PARTY)

/** Anchor of a sponsor on the sponsors page. */
fun Sponsor.anchor(): String = name.replace(" ", "-")

/** Local path of a sponsor logo. */
fun Sponsor.logoSrc(): String? = photoUrl?.replace("https://mixitconf.org", "")

/**
 * Sponsors of an edition, as a wall of logos on uniform white tiles (logos are made for light
 * backgrounds): the main sponsors first and larger, then the partners. Each logo opens the sponsor
 * on the sponsors page.
 */
fun DIV.sponsorGroupComponent(context: Context, event: Event, sponsors: List<Sponsor>) {
    if (sponsors.isEmpty()) return
    val ordered = sponsors.sortedBy { it.subscriptionDate }.distinctBy { it.name }
    val main = ordered.filter { it.level in MAIN_LEVELS }
    val partners = ordered.filterNot { it.level in MAIN_LEVELS }
    val pageHref = "${context.uriBasePath}/${event.year}/sponsors"

    div(classes = "mxt-sponsor-wall") {
        div(classes = "mxt-sponsor-wall__head") {
            h2(classes = "mxt-block-title") { +"${context.i18n("sponsor.v2.list.title")} ${event.year}" }
            a(href = pageHref, classes = "mxt-sponsor-wall__more") { +context.i18n("sponsor.v2.all") }
        }
        if (main.isNotEmpty()) sponsorLogos(context.i18n("sponsor.level.main"), main, pageHref, large = true)
        if (partners.isNotEmpty()) sponsorLogos(context.i18n("sponsor.level.partner"), partners, pageHref, large = false)
    }
}

private fun FlowContent.sponsorLogos(label: String, sponsors: List<Sponsor>, pageHref: String, large: Boolean) {
    span(classes = "mxt-eyebrow mxt-eyebrow--muted") { +label }
    ul(classes = if (large) "mxt-sponsor-logos mxt-sponsor-logos--large" else "mxt-sponsor-logos") {
        sponsors.forEach { sponsor ->
            li {
                a(href = "$pageHref#${sponsor.anchor()}", classes = "mxt-sponsor-logo") {
                    attributes["aria-label"] = sponsor.name
                    sponsor.logoSrc()?.let { img(src = it, alt = "") { attributes["loading"] = "lazy" } }
                        ?: span { +sponsor.name }
                }
            }
        }
    }
}

/**
 * Sponsors band at the bottom of a page, rendered only when the edition has sponsors.
 * It is always followed by the footer, whose bevel must start right on the band: hence the inverted end.
 */
fun DIV.sponsorsSection(context: Context, event: Event, sponsors: List<Sponsor>) {
    if (sponsors.isEmpty()) return
    sectionComponent(context, style = SectionStyle.LIGHT, effect = SectionEffect.BOTH_INVERTED) {
        sponsorGroupComponent(context, event, sponsors)
    }
}
