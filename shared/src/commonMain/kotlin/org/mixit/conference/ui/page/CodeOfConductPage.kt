package org.mixit.conference.ui.page

import kotlinx.html.a
import kotlinx.html.p
import org.mixit.conference.model.event.Event
import org.mixit.conference.model.people.Sponsor
import org.mixit.conference.model.shared.Context
import org.mixit.conference.ui.CONTACT_EMAIL
import org.mixit.conference.ui.component.Rubrique
import org.mixit.conference.ui.component.SideLink
import org.mixit.conference.ui.component.Step
import org.mixit.conference.ui.component.checkList
import org.mixit.conference.ui.component.pageContent
import org.mixit.conference.ui.component.pageHeaderComponent
import org.mixit.conference.ui.component.pageSection
import org.mixit.conference.ui.component.sideNavLayout
import org.mixit.conference.ui.component.sponsorsSection
import org.mixit.conference.ui.component.steps
import org.mixit.conference.ui.renderTemplate

private const val COC_SOURCE = "https://confcodeofconduct.com/index-fr.html"

fun renderCodeOfConduct(context: Context, event: Event, sponsors: List<Sponsor>) =
    renderTemplate(context, event) {
        pageHeaderComponent(
            context,
            rubrique = Rubrique.CODE_OF_CONDUCT,
            title = context.i18n("coc.v2.title"),
            intro = context.i18n("coc.v2.intro"),
        )
        pageContent {
            sideNavLayout(
                title = context.i18n("coc.nav"),
                links = listOf(
                    SideLink(context.i18n("coc.help.nav"), "#coc-help"),
                    SideLink(context.i18n("coc.essential.title"), "#coc-essential"),
                    SideLink(context.i18n("coc.detail.title"), "#coc-detail"),
                ),
            ) {
                pageSection(context.i18n("coc.help.title"), id = "coc-help") {
                    steps((1..3).map {
                        Step(context.i18n("coc.help.step$it.title"), context.i18n("coc.help.step$it.text"))
                    })
                    p {
                        +"${context.i18n("coc.help.contact")} "
                        a(href = "mailto:$CONTACT_EMAIL") { +CONTACT_EMAIL }
                    }
                }
                pageSection(context.i18n("coc.essential.title"), id = "coc-essential") {
                    checkList((1..5).map { context.i18n("coc.essential.$it") })
                }
                pageSection(context.i18n("coc.detail.title"), id = "coc-detail") {
                    (1..6).forEach { p { +context.i18n("coc.detail.$it") } }
                    p(classes = "mxt-page__note") {
                        +"${context.i18n("coc.detail.credits")} "
                        a(href = COC_SOURCE) {
                            attributes["target"] = "_blank"
                            attributes["rel"] = "noopener"
                            +"confcodeofconduct.com"
                        }
                    }
                }
            }
        }
        sponsorsSection(context, event, sponsors)
    }
