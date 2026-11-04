package org.mixit.conference.ui.page

import kotlinx.html.a
import kotlinx.html.li
import kotlinx.html.p
import kotlinx.html.ul
import kotlinx.html.unsafe
import org.mixit.conference.model.event.Event
import org.mixit.conference.model.people.Sponsor
import org.mixit.conference.model.shared.Context
import org.mixit.conference.ui.CONTACT_EMAIL
import org.mixit.conference.ui.component.Badge
import org.mixit.conference.ui.component.Bar
import org.mixit.conference.ui.component.Definition
import org.mixit.conference.ui.component.Rubrique
import org.mixit.conference.ui.component.autoGrid
import org.mixit.conference.ui.component.barList
import org.mixit.conference.ui.component.infoCard
import org.mixit.conference.ui.component.pageContent
import org.mixit.conference.ui.component.pageHeaderComponent
import org.mixit.conference.ui.component.pageSection
import org.mixit.conference.ui.component.sponsorsSection
import org.mixit.conference.ui.component.statRow
import org.mixit.conference.ui.renderTemplate

/** Total budget of the last published year, used to compute the amounts of each line. */
private const val BUDGET_TOTAL = 130_000

/** Budget lines (i18n suffix under budget.line) and their share, from the largest to the smallest. */
private val BUDGET_LINES = listOf(
    "logistics" to 37,
    "venue" to 19,
    "speakers" to 17,
    "media" to 14,
    "accessibility" to 7,
    "admin" to 6,
)

private fun amount(percent: Int): String {
    val value = BUDGET_TOTAL * percent / 100
    return "≈ ${value / 1000} ${(value % 1000).toString().padStart(3, '0')} €"
}

fun renderBudgetPage(context: Context, event: Event, sponsors: List<Sponsor>) =
    renderTemplate(context, event) {
        pageHeaderComponent(
            context,
            rubrique = Rubrique.BUDGET,
            title = context.i18n("budget.v2.title"),
            intro = context.i18n("budget.v2.intro"),
        )
        pageContent {
            statRow(listOf("total", "tickets", "lines").map {
                Definition(context.i18n("budget.stats.$it"), context.i18n("budget.stats.$it.label"))
            })

            pageSection(context.i18n("budget.split.title"), intro = context.i18n("budget.split.intro")) {
                barList(
                    BUDGET_LINES.map { (line, percent) -> Bar(context.i18n("budget.line.$line"), percent, amount(percent)) },
                    label = context.i18n("budget.split.title"),
                )
                p(classes = "mxt-page__note") { +context.i18n("budget.split.note") }
            }

            pageSection(context.i18n("budget.detail.title")) {
                autoGrid("300px") {
                    BUDGET_LINES.forEach { (line, percent) ->
                        infoCard(
                            icon = null,
                            title = context.i18n("budget.line.$line"),
                            html = context.i18n("budget.line.$line.text"),
                            badge = Badge("$percent %", strong = true),
                        )
                    }
                }
            }

            pageSection(context.i18n("budget.why.v2.title")) {
                p { +context.i18n("budget.why.v2.intro") }
                ul(classes = "mxt-page__list") {
                    li { unsafe { raw(context.i18n("budget.why.v2.step1")) } }
                    li { unsafe { raw(context.i18n("budget.why.v2.step2")) } }
                }
                p {
                    +"${context.i18n("budget.why.v2.question")} "
                    a(href = "mailto:$CONTACT_EMAIL") { +CONTACT_EMAIL }
                }
            }
        }
        sponsorsSection(context, event, sponsors)
    }
