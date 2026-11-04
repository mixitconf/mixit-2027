package org.mixit.conference.model.menu

/**
 * A group of navigation links.
 *
 * @param title i18n key of the group title (footer column, mobile menu section, header dropdown label)
 * @param items links of the group
 * @param inline when true, the desktop header shows the items as top-level links instead of a dropdown
 */
data class Menu(val title: String, val items: List<MenuItem>, val inline: Boolean = false)
