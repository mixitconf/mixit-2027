package org.mixit.conference.ui.page

import org.mixit.conference.model.event.Event
import org.mixit.conference.model.people.Organization
import org.mixit.conference.model.people.Sponsor
import org.mixit.conference.model.shared.Context
import org.mixit.conference.model.shared.Language
import org.mixit.conference.ui.CONTACT_EMAIL
import org.mixit.conference.ui.DEFAULT_IMG_URL
import org.mixit.conference.ui.MIXETTE_YEARS
import org.mixit.conference.ui.component.Rubrique
import org.mixit.conference.ui.component.Step
import org.mixit.conference.ui.component.autoGrid
import org.mixit.conference.ui.component.callout
import org.mixit.conference.ui.component.editionMenu
import org.mixit.conference.ui.component.logoCard
import org.mixit.conference.ui.component.pageContent
import org.mixit.conference.ui.component.pageHeaderComponent
import org.mixit.conference.ui.component.pageSection
import org.mixit.conference.ui.component.sponsorsSection
import org.mixit.conference.ui.component.steps
import org.mixit.conference.ui.renderTemplate

fun renderMixettePage(
    context: Context,
    event: Event,
    organizations: List<Organization>,
    sponsors: List<Sponsor>,
) = renderTemplate(context, event) {
    pageHeaderComponent(
        context,
        rubrique = Rubrique.MIXETTE,
        title = context.i18n("mixette.v2.title"),
        intro = context.i18n("mixette.v2.intro"),
    ) {
        editionMenu(context.i18n("page.edition"), MIXETTE_YEARS, event.year) { "${context.uriBasePath}/$it/mixette" }
    }
    pageContent {
        pageSection(context.i18n("mixette.how.title")) {
            steps((1..3).map {
                Step(context.i18n("mixette.step$it.title"), context.i18n("mixette.step$it.text"))
            })
        }

        pageSection(context.i18n("mixette.orgas.title"), intro = context.i18n("mixette.orgas.intro")) {
            if (organizations.isEmpty()) {
                callout(
                    icon = Rubrique.MIXETTE.src,
                    title = context.i18n("mixette.orgas.empty.title"),
                    description = context.i18n("mixette.orgas.empty.text"),
                    ctaLabel = context.i18n("page.contact.email"),
                    ctaHref = "mailto:$CONTACT_EMAIL",
                )
            } else {
                autoGrid("320px") {
                    organizations.sortedBy { it.name }.forEach {
                        val description = it.description[context.language]
                            ?: it.description[Language.FRENCH]
                            ?: it.description[Language.ENGLISH].orEmpty()
                        logoCard(
                            image = (it.photoUrl ?: DEFAULT_IMG_URL).replace("https://mixitconf.org", ""),
                            title = it.name,
                            html = context.markdown(description),
                        )
                    }
                }
            }
        }

        callout(
            icon = Rubrique.BUDGET.src,
            title = context.i18n("mixette.budget.title"),
            description = context.i18n("mixette.budget.text"),
            ctaLabel = context.i18n("mixette.budget.cta"),
            ctaHref = "${context.forceUriBAsePath()}/budget",
            soft = true,
        )
    }
    sponsorsSection(context, event, sponsors)
}
