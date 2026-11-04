package org.mixit.conference.ui.component

import kotlinx.html.ButtonType
import kotlinx.html.DIV
import kotlinx.html.FlowContent
import kotlinx.html.a
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.header
import kotlinx.html.img
import kotlinx.html.li
import kotlinx.html.nav
import kotlinx.html.onClick
import kotlinx.html.span
import kotlinx.html.ul
import kotlinx.html.unsafe
import org.mixit.conference.model.event.Event
import org.mixit.conference.model.menu.Menu
import org.mixit.conference.model.shared.Context
import org.mixit.conference.model.shared.Language
import org.mixit.conference.ui.HEADER_CTA
import org.mixit.conference.ui.NAVIGATION

private const val MOBILE_MENU_ID = "mxt-mobile-menu"
private const val LOGO_LIGHT = "/images/svg/logo/mxt-icon--logo--light.svg"

private const val ICON_BURGER =
    """<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" aria-hidden="true"><line x1="4" y1="7" x2="20" y2="7"/><line x1="4" y1="12" x2="20" y2="12"/><line x1="4" y1="17" x2="20" y2="17"/></svg>"""
private const val ICON_CLOSE =
    """<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" aria-hidden="true"><line x1="6" y1="6" x2="18" y2="18"/><line x1="18" y1="6" x2="6" y2="18"/></svg>"""
private const val ICON_USER =
    """<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><circle cx="12" cy="8" r="4"/><path d="M4 21a8 8 0 0 1 16 0"/></svg>"""
private const val ICON_CHEVRON =
    """<svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><polyline points="6 9 12 15 18 9"/></svg>"""

/**
 * Site header: desktop navigation bar and mobile off-canvas menu, both built from [NAVIGATION].
 * Always exposes the theme switch and the login/logout link.
 */
fun DIV.headerComponent(ctx: Context, @Suppress("UNUSED_PARAMETER") event: Event?) {
    header(classes = "mxt-header") {
        div(classes = "mxt-header__bar") {
            button(type = ButtonType.button, classes = "mxt-header__icon-button mxt-header__burger") {
                attributes["data-bs-toggle"] = "offcanvas"
                attributes["data-bs-target"] = "#$MOBILE_MENU_ID"
                attributes["aria-controls"] = MOBILE_MENU_ID
                attributes["aria-label"] = ctx.i18n("header.menu.open")
                unsafe { raw(ICON_BURGER) }
            }
            a(href = "${ctx.forceUriBAsePath()}/", classes = "mxt-header__brand") {
                img(src = LOGO_LIGHT, alt = ctx.i18n("header.home"), classes = "mxt-header__logo")
            }
            nav(classes = "mxt-header__nav") {
                attributes["aria-label"] = ctx.i18n("header.nav")
                NAVIGATION.forEach { menu ->
                    if (menu.inline) {
                        menu.items.forEach { item ->
                            a(href = ctx.navHref(item), classes = "mxt-header__link") {
                                if (ctx.isActive(item)) attributes["aria-current"] = "page"
                                +ctx.i18n(item.title)
                            }
                        }
                    } else {
                        headerDropdown(ctx, menu)
                    }
                }
            }
            div(classes = "mxt-header__actions") {
                a(href = ctx.navHref(HEADER_CTA), classes = "mxt-button mxt-button--outline mxt-header__cta") {
                    +ctx.i18n(HEADER_CTA.title)
                }
                languageSwitch(ctx)
                themeSwitch(ctx, "mxt-header__icon-button")
                loginLink(ctx, "mxt-header__icon-button", withLabel = false)
            }
        }
        mobileMenu(ctx)
    }
}

private fun FlowContent.headerDropdown(ctx: Context, menu: Menu) {
    div(classes = "dropdown mxt-header__dropdown") {
        button(type = ButtonType.button, classes = "mxt-header__link mxt-header__link--toggle") {
            attributes["data-bs-toggle"] = "dropdown"
            attributes["aria-expanded"] = "false"
            if (ctx.isActive(menu)) attributes["data-active"] = "true"
            +ctx.i18n(menu.title)
            unsafe { raw(ICON_CHEVRON) }
        }
        ul(classes = "dropdown-menu mxt-header__menu") {
            menu.items.forEach { item ->
                li {
                    a(href = ctx.navHref(item), classes = "dropdown-item mxt-header__menu-item") {
                        if (ctx.isActive(item)) attributes["aria-current"] = "page"
                        +ctx.i18n(item.title)
                    }
                }
            }
        }
    }
}

private fun FlowContent.languageSwitch(ctx: Context) {
    val (target, label, tag) = if (ctx.language == Language.FRENCH) {
        Triple("/en${ctx.path}", "EN", "en")
    } else {
        Triple("/fr${ctx.path}", "FR", "fr")
    }
    a(href = target, classes = "mxt-header__language") {
        attributes["lang"] = tag
        attributes["hreflang"] = tag
        attributes["aria-label"] = ctx.i18n("header.language")
        +label
    }
}

/** Dark/light theme switch (the icon swaps with the theme through the `--mode-img-src` token). */
fun FlowContent.themeSwitch(ctx: Context, classes: String, label: String? = null) {
    button(type = ButtonType.button, classes = classes) {
        onClick = "switchTheme()"
        if (label == null) attributes["aria-label"] = ctx.i18n("header.mode")
        img(classes = "mxt-header__theme-icon", alt = "")
        if (label != null) span { +label }
    }
}

/** Login link, or logout link when the visitor is connected. */
fun FlowContent.loginLink(ctx: Context, classes: String, withLabel: Boolean) {
    val connected = ctx.username != null
    val href = if (connected) "${ctx.uriBasePath}/logout" else "${ctx.uriBasePath}/login"
    val label = ctx.i18n(if (connected) "header.logout" else "header.login")
    a(href = href, classes = classes) {
        if (!withLabel) attributes["aria-label"] = label
        unsafe { raw(ICON_USER) }
        if (withLabel) span { +label }
    }
}

private fun FlowContent.mobileMenu(ctx: Context) {
    div(classes = "offcanvas offcanvas-start mxt-mobile-menu") {
        attributes["id"] = MOBILE_MENU_ID
        attributes["tabindex"] = "-1"
        attributes["aria-label"] = ctx.i18n("header.nav")
        div(classes = "mxt-mobile-menu__header") {
            button(type = ButtonType.button, classes = "mxt-header__icon-button") {
                attributes["data-bs-dismiss"] = "offcanvas"
                attributes["aria-label"] = ctx.i18n("header.menu.close")
                unsafe { raw(ICON_CLOSE) }
            }
            img(src = LOGO_LIGHT, alt = "MiXiT", classes = "mxt-header__logo")
            languageSwitch(ctx)
        }
        div(classes = "mxt-mobile-menu__body") {
            a(href = ctx.navHref(HEADER_CTA), classes = "mxt-button mxt-button--outline mxt-mobile-menu__cta") {
                +ctx.i18n(HEADER_CTA.title)
            }
            navigationGroups(ctx, "mxt-mobile-menu")
        }
        div(classes = "mxt-mobile-menu__footer") {
            themeSwitch(ctx, "mxt-button mxt-button--ghost", label = ctx.i18n("header.mode"))
            loginLink(ctx, "mxt-button mxt-button--ghost", withLabel = true)
        }
    }
}
