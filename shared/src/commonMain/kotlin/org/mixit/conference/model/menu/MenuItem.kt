package org.mixit.conference.model.menu

/**
 * A navigation link.
 *
 * @param title i18n key of the label
 * @param href path relative to the language root, or an anchor
 * @param inHeader false to keep the link out of the header (desktop bar and mobile menu); it stays in the footer
 */
data class MenuItem(
    val title: String,
    val href: String,
    val inHeader: Boolean = true,
)
