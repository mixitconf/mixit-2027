package org.mixit.conference.ui.page

import kotlinx.html.a
import kotlinx.html.div
import kotlinx.html.iframe
import org.mixit.conference.model.event.Event
import org.mixit.conference.model.people.Sponsor
import org.mixit.conference.model.shared.Context
import org.mixit.conference.ui.component.Badge
import org.mixit.conference.ui.component.Definition
import org.mixit.conference.ui.component.Rubrique
import org.mixit.conference.ui.component.autoGrid
import org.mixit.conference.ui.component.callout
import org.mixit.conference.ui.component.factsPanel
import org.mixit.conference.ui.component.infoCard
import org.mixit.conference.ui.component.pageContent
import org.mixit.conference.ui.component.pageHeaderComponent
import org.mixit.conference.ui.component.pageSection
import org.mixit.conference.ui.component.sponsorsSection
import org.mixit.conference.ui.renderTemplate

private const val MAP_EMBED =
    "https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d44532.84179104339!2d4.810176969092425!3d45.76512532471853!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x47f4ea977b847259%3A0x9efbb71d73134a76!2sCPE%20Lyon!5e0!3m2!1sfr!2sfr!4v1679595963075!5m2!1sfr!2sfr"
private const val MAP_LINK = "https://www.google.com/maps/place/CPE+Lyon/@45.7841638,4.8675628,17z"
private const val CPE_SITE = "https://www.cpe.fr/coordonnees-acces-contact/"
private const val TCL_SITE = "https://www.tcl.fr/"

/** Transport modes, in display order (i18n suffix under venue.transport). */
private val TRANSPORTS = listOf("tram", "train", "plane", "bike", "car")

fun renderVenuePage(context: Context, event: Event, sponsors: List<Sponsor>) =
    renderTemplate(context, event) {
        pageHeaderComponent(
            context,
            rubrique = Rubrique.VENUE,
            title = context.i18n("venue.v2.title"),
            intro = context.i18n("venue.v2.intro"),
        )
        pageContent {
            div(classes = "mxt-venue__top") {
                div(classes = "mxt-venue__map") {
                    iframe {
                        src = MAP_EMBED
                        attributes["title"] = context.i18n("venue.map.label")
                        attributes["loading"] = "lazy"
                        attributes["allowfullscreen"] = "true"
                        attributes["referrerpolicy"] = "no-referrer-when-downgrade"
                    }
                }
                factsPanel(
                    title = context.i18n("venue.facts.title"),
                    facts = listOf("address", "stop", "walk").map {
                        Definition(context.i18n("venue.facts.$it.label"), context.i18n("venue.facts.$it"))
                    },
                ) {
                    a(href = MAP_LINK, classes = "mxt-button mxt-button--primary") {
                        attributes["target"] = "_blank"
                        attributes["rel"] = "noopener"
                        +context.i18n("venue.facts.maps")
                    }
                    a(href = CPE_SITE, classes = "mxt-button mxt-button--line") {
                        attributes["target"] = "_blank"
                        attributes["rel"] = "noopener"
                        +context.i18n("venue.facts.site")
                    }
                }
            }

            pageSection(context.i18n("venue.transport.title")) {
                autoGrid("320px") {
                    TRANSPORTS.forEach { mode ->
                        infoCard(
                            icon = "/images/svg/transport-v2/mxt-transport-$mode.svg",
                            title = context.i18n("venue.transport.$mode.title"),
                            subtitle = context.i18n("venue.transport.$mode.subtitle"),
                            html = context.i18n("venue.transport.$mode.text"),
                            badge = when (mode) {
                                "tram" -> Badge(context.i18n("venue.badge.recommended"), strong = true)
                                "car" -> Badge(context.i18n("venue.badge.avoid"))
                                else -> null
                            },
                            link = if (mode == "tram") context.i18n("venue.transport.tram.link") to TCL_SITE else null,
                            highlighted = mode == "tram",
                        )
                    }
                }
            }

            callout(
                icon = Rubrique.ACCESSIBILITY.src,
                title = context.i18n("venue.access.title"),
                description = context.i18n("venue.access.text"),
                ctaLabel = context.i18n("venue.access.cta"),
                ctaHref = "${context.forceUriBAsePath()}/accessibility",
                soft = true,
            )
        }
        sponsorsSection(context, event, sponsors)
    }
