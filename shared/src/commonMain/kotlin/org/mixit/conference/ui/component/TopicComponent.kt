package org.mixit.conference.ui.component

import kotlinx.html.A
import kotlinx.html.DIV
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.h4
import kotlinx.html.img
import kotlinx.html.span
import kotlinx.html.unsafe
import org.mixit.conference.model.shared.Context
import org.mixit.conference.model.shared.Language
import org.mixit.conference.shared.model.Topic

/** Topic icon (v2 disc): one file per topic, its colour identifies the topic. */
val Topic.iconSrc: String get() = topicIconSrc(value)

/** Topic icon path from a topic value ("tech", "team"...). */
fun topicIconSrc(value: String): String = "/images/svg/topic-v2/mxt-topic-${value.lowercase()}.svg"

fun DIV.topicComponent(context: Context, topic: Topic, year: Int? = null) {
    div(classes="mxt-topic__container") {
        val t = topic.value.lowercase()
        img {
            src = topic.iconSrc
            alt = context.i18n("topics.$t.title")
        }
        div {
            +context.i18n("topics.$t.title")
            if(year != null) {
                +" $year"
            }
        }
    }
}

fun A.topicPrefixComponent(context: Context, topic: Topic, rawText: String) {
    h4(classes="mxt-talk__title-container") {
        val t = topic.value.lowercase()
        div(classes="mxt-has-tooltip") {
            img {
                src = topic.iconSrc
                alt = context.i18n("topics.$t.title")
            }
            span(classes = "mxt-tooltip small") { +context.i18n("topics.$t.title") }
        }
        div(classes = "mxt-talk__title-container-wrapper") {
            unsafe {
                raw(context.markdown(rawText))
            }
        }
    }
}
