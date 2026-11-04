package org.mixit.conference.ui.component

import kotlinx.html.FlowContent
import kotlinx.html.a
import kotlinx.html.li
import kotlinx.html.nav
import kotlinx.html.span
import kotlinx.html.strong
import kotlinx.html.ul
import org.mixit.conference.model.menu.Menu
import org.mixit.conference.model.menu.MenuItem
import org.mixit.conference.model.shared.Context
import org.mixit.conference.ui.NAVIGATION

/** Absolute href of a navigation item, in the current language. */
fun Context.navHref(item: MenuItem): String = "${forceUriBAsePath()}/${item.href}"

/** True when the item points to the page being displayed (anchors are never active). */
fun Context.isActive(item: MenuItem): Boolean {
    if (item.href.startsWith("#")) return false
    val current = path.trim('/')
    return if (item.href.isEmpty()) current.isEmpty() else current == item.href || current.startsWith("${item.href}/")
}

/** True when one of the group's items is the current page. */
fun Context.isActive(menu: Menu): Boolean = menu.items.any { isActive(it) }

/**
 * Navigation groups rendered as titled link lists. Used by the footer columns and the mobile menu,
 * so both always show the same links as the desktop header.
 */
fun FlowContent.navigationGroups(ctx: Context, cssBlock: String, menus: List<Menu> = NAVIGATION) {
    menus.forEach { menu ->
        nav(classes = "${cssBlock}__group") {
            attributes["aria-label"] = ctx.i18n(menu.title)
            strong(classes = "${cssBlock}__group-title") { +ctx.i18n(menu.title) }
            ul(classes = "${cssBlock}__links") {
                menu.items.forEach { item ->
                    li {
                        a(href = ctx.navHref(item), classes = "${cssBlock}__link") {
                            if (ctx.isActive(item)) attributes["aria-current"] = "page"
                            +ctx.i18n(item.title)
                        }
                    }
                }
            }
        }
    }
}

/** Small screen-reader-only label. */
fun FlowContent.srOnly(text: String) {
    span(classes = "visually-hidden") { +text }
}
