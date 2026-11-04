package org.mixit.conference.ui.page

import kotlinx.html.div
import kotlinx.html.p
import org.mixit.conference.model.event.Event
import org.mixit.conference.model.people.Sponsor
import org.mixit.conference.model.people.Staff
import org.mixit.conference.model.people.Volunteer
import org.mixit.conference.model.shared.Context
import org.mixit.conference.ui.DEFAULT_IMG_URL
import org.mixit.conference.ui.YEARS
import org.mixit.conference.ui.component.Definition
import org.mixit.conference.ui.component.Rubrique
import org.mixit.conference.ui.component.autoGrid
import org.mixit.conference.ui.component.editionSwitch
import org.mixit.conference.ui.component.figureImage
import org.mixit.conference.ui.component.pageContent
import org.mixit.conference.ui.component.pageHeaderComponent
import org.mixit.conference.ui.component.pageSection
import org.mixit.conference.ui.component.personTile
import org.mixit.conference.ui.component.sponsorsSection
import org.mixit.conference.ui.component.statRow
import org.mixit.conference.ui.renderTemplate

fun renderAboutPage(
    context: Context,
    event: Event,
    staff: List<Staff>,
    volunteers: List<Volunteer>,
    sponsors: List<Sponsor>,
) = renderTemplate(context, event) {
    pageHeaderComponent(
        context,
        rubrique = Rubrique.ABOUT,
        title = context.i18n("about.v2.title"),
        intro = context.i18n("about.v2.intro"),
    )
    pageContent {
        statRow(listOf("start", "edition", "team").map {
            Definition(context.i18n("about.stats.$it"), context.i18n("about.stats.$it.label"))
        })

        div(classes = "mxt-about__story") {
            pageSection(context.i18n("about.story.title")) {
                (1..3).forEach { p { +context.i18n("about.story.$it") } }
            }
            figureImage("/images/jpg/mixit_amphi.jpg", context.i18n("about.story.photo"))
        }

        pageSection(context.i18n("about.team.title"), intro = context.i18n("about.team.v2.intro")) {
            editionSwitch(context.i18n("page.edition"), YEARS, event.year) { "${context.uriBasePath}/$it/about" }
            autoGrid("140px") {
                staff.sortedBy { it.firstname }.forEach {
                    personTile(it.firstname, it.photoUrl ?: DEFAULT_IMG_URL, it.lastname)
                }
            }
        }

        pageSection(context.i18n("about.volunteers.v2.title"), intro = context.i18n("about.volunteers.v2.intro")) {
            if (volunteers.isNotEmpty()) {
                autoGrid("140px") {
                    volunteers.sortedBy { it.firstname }.forEach {
                        personTile(it.firstname, it.photoUrl ?: DEFAULT_IMG_URL, it.lastname)
                    }
                }
            }
        }
    }
    sponsorsSection(context, event, sponsors)
}
