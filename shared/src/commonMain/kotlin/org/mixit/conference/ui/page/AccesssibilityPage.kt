package org.mixit.conference.ui.page

import org.mixit.conference.model.event.Event
import org.mixit.conference.model.people.Sponsor
import org.mixit.conference.model.shared.Context
import org.mixit.conference.ui.CONTACT_EMAIL
import org.mixit.conference.ui.component.Rubrique
import org.mixit.conference.ui.component.autoGrid
import org.mixit.conference.ui.component.banner
import org.mixit.conference.ui.component.callout
import org.mixit.conference.ui.component.figureImage
import org.mixit.conference.ui.component.infoCard
import org.mixit.conference.ui.component.pageContent
import org.mixit.conference.ui.component.pageHeaderComponent
import org.mixit.conference.ui.component.pageSection
import org.mixit.conference.ui.component.sponsorsSection
import org.mixit.conference.ui.renderTemplate

/** On-site measures, in display order (i18n prefix and icon name). */
private val MEASURES = listOf("parents", "captioning", "elevator", "seat")

fun renderAccessibilityPage(context: Context, event: Event, sponsors: List<Sponsor>) =
    renderTemplate(context, event) {
        pageHeaderComponent(
            context,
            rubrique = Rubrique.ACCESSIBILITY,
            title = context.i18n("accessibility.title"),
            intro = context.i18n("accessibility.v2.intro"),
        )
        pageContent {
            banner(
                title = context.i18n("accessibility.banner.title"),
                text = context.i18n("accessibility.banner.text"),
                ctaLabel = context.i18n("page.contact.email"),
                ctaHref = "mailto:$CONTACT_EMAIL",
            )

            pageSection(context.i18n("accessibility.onsite.title")) {
                autoGrid("240px") {
                    MEASURES.forEach {
                        infoCard(
                            icon = "/images/svg/access-v2/mxt-access-$it.svg",
                            title = context.i18n("accessibility.$it.title"),
                            html = context.i18n("accessibility.$it.text"),
                        )
                    }
                }
            }

            autoGrid("300px") {
                (1..2).forEach {
                    figureImage(
                        src = "/images/jpg/mxt-accessibility$it.jpg",
                        alt = context.i18n("accessibility.photo$it.alt"),
                        caption = context.i18n("accessibility.photo$it.caption"),
                    )
                }
            }

            callout(
                icon = Rubrique.VENUE.src,
                title = context.i18n("accessibility.venue.title"),
                description = context.i18n("accessibility.venue.text"),
                ctaLabel = context.i18n("accessibility.venue.cta"),
                ctaHref = "${context.forceUriBAsePath()}/venue",
                soft = true,
            )
        }
        sponsorsSection(context, event, sponsors)
    }
