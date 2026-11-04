package org.mixit.conference.ui.page

import org.mixit.conference.model.event.Event
import org.mixit.conference.model.faq.QuestionSet
import org.mixit.conference.model.people.Sponsor
import org.mixit.conference.model.shared.Context
import org.mixit.conference.model.shared.Language
import org.mixit.conference.ui.CONTACT_EMAIL
import org.mixit.conference.ui.component.Rubrique
import org.mixit.conference.ui.component.SideLink
import org.mixit.conference.ui.component.accordionItem
import org.mixit.conference.ui.component.callout
import org.mixit.conference.ui.component.pageContent
import org.mixit.conference.ui.component.pageHeaderComponent
import org.mixit.conference.ui.component.pageSection
import org.mixit.conference.ui.component.sideNavLayout
import org.mixit.conference.ui.component.sponsorsSection
import org.mixit.conference.ui.renderTemplate

fun renderFaqPage(
    context: Context,
    event: Event,
    questionSets: List<QuestionSet>,
    sponsors: List<Sponsor>,
) = renderTemplate(context, event) {
    fun localized(fr: String, en: String) = if (context.language == Language.ENGLISH) en else fr
    val sets = questionSets.sortedBy { it.order }

    pageHeaderComponent(
        context,
        rubrique = Rubrique.FAQ,
        title = context.i18n("faq.v2.title"),
        intro = context.i18n("faq.v2.intro"),
    )
    pageContent {
        sideNavLayout(
            title = context.i18n("faq.categories"),
            links = sets.map {
                SideLink(localized(it.title.fr, it.title.en), "#faq-${it.id}", it.questions.size)
            },
        ) {
            sets.forEach { set ->
                pageSection(localized(set.title.fr, set.title.en), id = "faq-${set.id}") {
                    set.questions.sortedBy { it.order }.forEach {
                        accordionItem(localized(it.title.fr, it.title.en), context.markdown(localized(it.answer.fr, it.answer.en)))
                    }
                }
            }
            callout(
                icon = Rubrique.FAQ.src,
                title = context.i18n("faq.more.title"),
                description = context.i18n("faq.more.text"),
                ctaLabel = context.i18n("faq.more.cta"),
                ctaHref = "mailto:$CONTACT_EMAIL",
                soft = true,
            )
        }
    }
    sponsorsSection(context, event, sponsors)
}
