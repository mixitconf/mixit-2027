package org.mixit.conference.ui.page

import kotlinx.html.FlowContent
import kotlinx.html.ScriptType
import kotlinx.html.details
import kotlinx.html.script
import kotlinx.html.summary
import org.mixit.conference.ui.component.anchor
import org.mixit.conference.ui.component.logoSrc
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
import org.mixit.conference.ui.component.banner
import org.mixit.conference.ui.component.editionMenu
import org.mixit.conference.ui.component.iconLinks
import org.mixit.conference.ui.component.pageContent
import org.mixit.conference.ui.component.pageHeaderComponent
import org.mixit.conference.ui.component.pageSection
import org.mixit.conference.ui.renderTemplate

/** Packs offered to the sponsors of the next edition, in the order they are presented. */
private val OFFERED_PACKS = listOf(
    SponsorshipLevel.LANYARD,
    SponsorshipLevel.GOLD,
    SponsorshipLevel.SILVER,
    SponsorshipLevel.PARTY,
    SponsorshipLevel.MIXTEEN,
)

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
                                div(classes = "mxt-sponsor-tiles") {
                                    levelSponsors.sortedBy { it.name.lowercase() }.forEach { sponsorTile(context, it) }
                                }
                            }
                        }
                }
            }

            script(type = ScriptType.textJavaScript) {
                unsafe {
                    raw(
                        """
                        (function () {
                          function openTarget() {
                            var id = decodeURIComponent(location.hash.slice(1));
                            var tile = id && document.getElementById(id);
                            if (tile && tile.tagName === 'DETAILS') { tile.open = true; tile.scrollIntoView({ block: 'center' }); }
                          }
                          openTarget();
                          window.addEventListener('hashchange', openTarget);
                        })();
                        """.trimIndent()
                    )
                }
            }

            if (event.year == CURRENT_YEAR) {
                pageSection(context.i18n("sponsor.v2.packs.title"), context.i18n("sponsor.v2.packs.intro")) {
                    ul(classes = "mxt-tags") {
                        OFFERED_PACKS.forEach { li(classes = "mxt-tag") { +context.i18n("sponsor.level.${it.name}") } }
                    }
                }
            }
        }
    }

/**
 * A sponsor of the sponsors page: a uniform logo tile with the name. A click opens its description
 * and links below, so the grid stays even. Opened directly when the page is reached on its anchor.
 */
private fun FlowContent.sponsorTile(context: Context, sponsor: Sponsor) {
    details(classes = "mxt-sponsor-tile") {
        attributes["id"] = sponsor.anchor()
        summary(classes = "mxt-sponsor-tile__summary") {
            span(classes = "mxt-sponsor-logo mxt-sponsor-logo--tile") {
                sponsor.logoSrc()?.let { img(src = it, alt = "") { attributes["loading"] = "lazy" } }
            }
            strong(classes = "mxt-sponsor-tile__name") { span(classes = "mxt-sponsor-tile__label") { +sponsor.name } }
        }
        div(classes = "mxt-sponsor-tile__body") {
            val description = sponsor.description[context.language] ?: sponsor.description.values.firstOrNull()
            if (!description.isNullOrBlank()) {
                div(classes = "mxt-sponsor-tile__text") { unsafe { raw(context.markdown(description)) } }
            }
            iconLinks(sponsor.links)
        }
    }
}
