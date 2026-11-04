package org.mixit.conference.ui.component

import kotlinx.html.FlowContent
import kotlinx.html.a
import kotlinx.html.article
import kotlinx.html.details
import kotlinx.html.figcaption
import kotlinx.html.figure
import kotlinx.html.h3
import kotlinx.html.li
import kotlinx.html.nav
import kotlinx.html.p
import kotlinx.html.summary
import kotlinx.html.ul
import kotlinx.html.unsafe
import kotlinx.html.dd
import kotlinx.html.div
import kotlinx.html.dl
import kotlinx.html.dt
import kotlinx.html.h2
import kotlinx.html.img
import kotlinx.html.ol
import kotlinx.html.span
import kotlinx.html.strong

/**
 * Generic content blocks of the 2027 design, shared by the home and the inner pages.
 * They only take already translated strings so they stay independent from the i18n keys.
 */

/** A title and its description, used by [definitionList]. */
data class Definition(val term: String, val description: String)

/** One step of a [timeline]: a highlighted date and what happens. */
data class TimelineStep(val date: String, val label: String)

/** Section title, with an optional id used as an anchor by the navigation. */
fun FlowContent.blockTitle(title: String, id: String? = null) {
    h2(classes = "mxt-block-title") {
        if (id != null) attributes["id"] = id
        +title
    }
}

/** Vertical list of terms separated by thin rules (formats, FAQ-like lists...). */
fun FlowContent.definitionList(items: List<Definition>) {
    dl(classes = "mxt-deflist") {
        items.forEach { item ->
            dt(classes = "mxt-deflist__term") { +item.term }
            dd(classes = "mxt-deflist__desc") { +item.description }
        }
    }
}

/** Grid of small cards. Each card is filled with [featureCard]. */
fun FlowContent.cardGrid(columns: Int = 2, block: FlowContent.() -> Unit) {
    div(classes = "mxt-card-grid mxt-card-grid--$columns") { block() }
}

/** Card with an icon on top, a title and a short text. */
fun FlowContent.featureCard(icon: String, title: String, description: String) {
    div(classes = "mxt-card") {
        img(src = icon, alt = "", classes = "mxt-card__icon")
        strong(classes = "mxt-card__title") { +title }
        span(classes = "mxt-card__desc") { +description }
    }
}

/** Icon on the left, title and text on the right (values, accessibility measures...). */
fun FlowContent.mediaItem(icon: String, title: String, description: String) {
    div(classes = "mxt-media") {
        img(src = icon, alt = "", classes = "mxt-media__icon")
        div(classes = "mxt-media__body") {
            strong(classes = "mxt-media__title") { +title }
            span(classes = "mxt-media__desc") { +description }
        }
    }
}

/** Key dates: horizontal on large screens, vertical on mobile. The first step is highlighted. */
fun FlowContent.timeline(steps: List<TimelineStep>) {
    ol(classes = "mxt-timeline") {
        steps.forEach { step ->
            li(classes = "mxt-timeline__step") {
                span(classes = "mxt-timeline__dot") { attributes["aria-hidden"] = "true" }
                span(classes = "mxt-timeline__date") { +step.date }
                span(classes = "mxt-timeline__label") { +step.label }
            }
        }
    }
}

/**
 * Bordered card with an icon, a title, a text and a call to action
 * (podcast, newsletter, ticketing announcements...).
 */
fun FlowContent.callout(
    icon: String,
    title: String,
    description: String,
    ctaLabel: String,
    ctaHref: String,
    titleId: String? = null,
    external: Boolean = false,
    soft: Boolean = false,
) {
    div(classes = if (soft) "mxt-callout mxt-callout--soft" else "mxt-callout") {
        img(src = icon, alt = "", classes = "mxt-callout__icon")
        div(classes = "mxt-callout__body") {
            h2(classes = "mxt-callout__title") {
                if (titleId != null) attributes["id"] = titleId
                +title
            }
            span(classes = "mxt-callout__desc") { +description }
        }
        a(href = ctaHref, classes = "mxt-button mxt-button--line mxt-callout__cta") {
            if (external) {
                attributes["target"] = "_blank"
                attributes["rel"] = "noopener"
            }
            +ctaLabel
        }
    }
}

/** Column that holds the content of an inner page, under its [pageHeaderComponent]. */
fun FlowContent.pageContent(block: FlowContent.() -> Unit) {
    div(classes = "mxt-page") { block() }
}

/** Titled block of an inner page, with an optional intro and an anchor id. */
fun FlowContent.pageSection(title: String?, intro: String? = null, id: String? = null, block: FlowContent.() -> Unit) {
    div(classes = "mxt-page__section") {
        if (id != null) attributes["id"] = id
        if (title != null || intro != null) {
            div(classes = "mxt-page__section-head") {
                if (title != null) h2(classes = "mxt-block-title") { +title }
                if (intro != null) p(classes = "mxt-page__section-intro") { +intro }
            }
        }
        block()
    }
}

/** Badge shown next to an [infoCard] title. */
data class Badge(val label: String, val strong: Boolean = false)

/**
 * Bordered card: icon, title (and badge), an optional subtitle, a text that may hold links
 * and an optional link. A highlighted card gets an accent top border.
 */
fun FlowContent.infoCard(
    icon: String?,
    title: String,
    html: String,
    subtitle: String? = null,
    badge: Badge? = null,
    link: Pair<String, String>? = null,
    highlighted: Boolean = false,
) {
    article(classes = if (highlighted) "mxt-info-card mxt-info-card--highlight" else "mxt-info-card") {
        div(classes = "mxt-info-card__head") {
            if (icon != null) img(src = icon, alt = "", classes = "mxt-info-card__icon")
            h3(classes = "mxt-info-card__title") { +title }
            if (badge != null) {
                span(classes = if (badge.strong) "mxt-badge mxt-badge--strong" else "mxt-badge") { +badge.label }
            }
        }
        if (subtitle != null) strong(classes = "mxt-info-card__subtitle") { +subtitle }
        div(classes = "mxt-info-card__text") { unsafe { raw(html) } }
        if (link != null) {
            a(href = link.second, classes = "mxt-info-card__link") {
                attributes["target"] = "_blank"
                attributes["rel"] = "noopener"
                +link.first
            }
        }
    }
}

/** Responsive grid of [infoCard]s, [figure]s or people. */
fun FlowContent.autoGrid(minWidth: String = "240px", block: FlowContent.() -> Unit) {
    div(classes = "mxt-auto-grid") {
        attributes["style"] = "--mxt-grid-min: $minWidth"
        block()
    }
}

/** Panel of key facts (label above, value below) followed by actions. */
fun FlowContent.factsPanel(title: String, facts: List<Definition>, actions: FlowContent.() -> Unit = {}) {
    div(classes = "mxt-facts") {
        h2(classes = "mxt-facts__title") { +title }
        dl(classes = "mxt-facts__list") {
            facts.forEach {
                dt { +it.term }
                dd { +it.description }
            }
        }
        div(classes = "mxt-facts__actions") { actions() }
    }
}

/** Big figures in a row (budget, history...). The description is the label under the value. */
fun FlowContent.statRow(stats: List<Definition>) {
    div(classes = "mxt-stats") {
        stats.forEach {
            p(classes = "mxt-stats__item") {
                strong(classes = "mxt-stats__value") { +it.term }
                span(classes = "mxt-stats__label") { +it.description }
            }
        }
    }
}

/** Dark banner with a strong message and a primary call to action. */
fun FlowContent.banner(
    title: String,
    text: String,
    ctaLabel: String,
    ctaHref: String,
    secondary: Pair<String, String>? = null,
    ctaEnabled: Boolean = true,
    footer: (FlowContent.() -> Unit)? = null,
) {
    div(classes = "mxt-banner") {
        div(classes = "mxt-banner__body") {
            strong(classes = "mxt-banner__title") { +title }
            span { +text }
        }
        div(classes = "mxt-banner__actions") {
            if (ctaEnabled) {
                a(href = ctaHref, classes = "mxt-button mxt-button--primary") { +ctaLabel }
            } else {
                span(classes = "mxt-button mxt-button--primary mxt-button--disabled") {
                    attributes["aria-disabled"] = "true"
                    +ctaLabel
                }
            }
            if (secondary != null) {
                a(href = secondary.second, classes = "mxt-button mxt-button--line mxt-banner__secondary") { +secondary.first }
            }
        }
        if (footer != null) div(classes = "mxt-banner__footer") { footer() }
    }
}

/** Numbered steps, with an optional icon per step. */
data class Step(val title: String, val text: String, val icon: String? = null)

fun FlowContent.steps(items: List<Step>) {
    ol(classes = "mxt-steps") {
        items.forEachIndexed { index, step ->
            li(classes = "mxt-steps__item") {
                span(classes = "mxt-steps__number") {
                    attributes["aria-hidden"] = "true"
                    +"${index + 1}"
                }
                div(classes = "mxt-steps__body") {
                    strong(classes = "mxt-steps__title") { +step.title }
                    span(classes = "mxt-steps__text") { +step.text }
                }
            }
        }
    }
}

/** List with a green check in front of each item. */
fun FlowContent.checkList(items: List<String>) {
    ul(classes = "mxt-checklist") {
        items.forEach { li { +it } }
    }
}

/** Image with its caption. */
fun FlowContent.figureImage(src: String, alt: String, caption: String? = null) {
    figure(classes = "mxt-figure") {
        img(src = src, alt = alt, classes = "mxt-figure__img") { attributes["loading"] = "lazy" }
        if (caption != null) figcaption(classes = "mxt-figure__caption") { +caption }
    }
}

/** Horizontal bars, from the largest share to the smallest. */
data class Bar(val label: String, val percent: Int, val detail: String? = null)

fun FlowContent.barList(bars: List<Bar>, label: String) {
    ol(classes = "mxt-bars") {
        attributes["aria-label"] = label
        bars.forEach { bar ->
            li(classes = "mxt-bars__item") {
                span(classes = "mxt-bars__label") { +bar.label }
                span(classes = "mxt-bars__track") {
                    span(classes = "mxt-bars__fill") {
                        attributes["style"] = "width: ${bar.percent}%"
                    }
                    strong(classes = "mxt-bars__value") { +"${bar.percent} %" }
                }
                if (bar.detail != null) span(classes = "mxt-bars__detail") { +bar.detail }
            }
        }
    }
}

/** A link of a [sideNavLayout], with an optional counter. */
data class SideLink(val label: String, val href: String, val count: Int? = null)

/** Sticky navigation on the left (a list on mobile), the content on the right. */
fun FlowContent.sideNavLayout(title: String, links: List<SideLink>, block: FlowContent.() -> Unit) {
    div(classes = "mxt-side-layout") {
        nav(classes = "mxt-side-nav") {
            attributes["aria-label"] = title
            span(classes = "mxt-eyebrow mxt-eyebrow--muted") { +title }
            links.forEach { link ->
                a(href = link.href, classes = "mxt-side-nav__link") {
                    span { +link.label }
                    if (link.count != null) span(classes = "mxt-side-nav__count") { +"${link.count}" }
                }
            }
        }
        div(classes = "mxt-side-layout__content") { block() }
    }
}

/** Question that opens on click, without JavaScript. The answer may hold HTML. */
fun FlowContent.accordionItem(question: String, answerHtml: String) {
    details(classes = "mxt-accordion") {
        summary(classes = "mxt-accordion__question") { +question }
        div(classes = "mxt-accordion__answer") { unsafe { raw(answerHtml) } }
    }
}

/** Round portrait and first name. */
fun FlowContent.personTile(name: String, photo: String, detail: String? = null) {
    div(classes = "mxt-person") {
        img(src = photo, alt = "", classes = "mxt-person__photo") { attributes["loading"] = "lazy" }
        strong(classes = "mxt-person__name") { +name }
        if (detail != null) span(classes = "mxt-person__detail") { +detail }
    }
}

/** Row of edition links ("2027 2026 ..."), the current one marked with aria-current. */
fun FlowContent.editionSwitch(label: String, years: List<Int>, current: Int, href: (Int) -> String) {
    nav(classes = "mxt-edition-switch") {
        attributes["aria-label"] = label
        span(classes = "mxt-edition-switch__label") { +label }
        years.forEach { year ->
            a(href = href(year), classes = "mxt-edition-switch__link") {
                if (year == current) attributes["aria-current"] = "page"
                +year.toString()
            }
        }
    }
}

/** Card with a logo or photo, a name and a description that may hold HTML (organisations, sponsors). */
fun FlowContent.logoCard(image: String, title: String, html: String) {
    article(classes = "mxt-logo-card") {
        img(src = image, alt = "", classes = "mxt-logo-card__img") { attributes["loading"] = "lazy" }
        div(classes = "mxt-logo-card__body") {
            h3(classes = "mxt-logo-card__title") { +title }
            div(classes = "mxt-logo-card__text") { unsafe { raw(html) } }
        }
    }
}

/**
 * Compact edition picker ("Édition 2026 ▾") that opens a list of links, without JavaScript.
 * Meant for the dark page header, where a long [editionSwitch] would not fit.
 */
fun FlowContent.editionMenu(label: String, years: List<Int>, current: Int, href: (Int) -> String) {
    details(classes = "mxt-edition-menu") {
        summary(classes = "mxt-edition-menu__toggle") {
            span(classes = "mxt-edition-menu__label") { +label }
            strong { +current.toString() }
        }
        nav(classes = "mxt-edition-menu__list") {
            attributes["aria-label"] = label
            years.forEach { year ->
                a(href = href(year), classes = "mxt-edition-menu__link") {
                    if (year == current) attributes["aria-current"] = "page"
                    +year.toString()
                }
            }
        }
    }
}

/** Rounded filter link, optionally with an icon and a counter. The active one is filled. */
fun FlowContent.chipLink(label: String, href: String, active: Boolean, icon: String? = null, count: Int? = null) {
    a(href = href, classes = if (active) "mxt-chip mxt-chip--active" else "mxt-chip") {
        if (active) attributes["aria-current"] = "true"
        if (icon != null) img(src = icon, alt = "", classes = "mxt-chip__icon")
        span { +label }
        if (count != null) span(classes = "mxt-chip__count") { +count.toString() }
    }
}
