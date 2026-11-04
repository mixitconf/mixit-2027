package org.mixit.conference.ui.page

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.html.ButtonType
import kotlinx.html.FlowContent
import kotlinx.html.article
import kotlinx.html.aside
import kotlinx.html.br
import kotlinx.html.dd
import kotlinx.html.dl
import kotlinx.html.dt
import kotlinx.html.label
import kotlinx.html.strong
import org.mixit.conference.model.people.Speaker
import org.mixit.conference.ui.CURRENT_TALK_YEAR
import org.mixit.conference.ui.component.Crumb
import org.mixit.conference.ui.component.durationLabel
import org.mixit.conference.ui.component.favoriteToggle
import org.mixit.conference.ui.component.formatBadge
import org.mixit.conference.ui.component.fullName
import org.mixit.conference.ui.component.initials
import org.mixit.conference.ui.component.pageContent
import org.mixit.conference.ui.component.pageHeaderComponent
import org.mixit.conference.ui.component.pageSection
import org.mixit.conference.ui.component.roomLabel
import org.mixit.conference.ui.component.sessionCard
import org.mixit.conference.ui.component.speakerHref
import org.mixit.conference.ui.component.sponsorsSection
import org.mixit.conference.ui.component.talkHref
import org.mixit.conference.ui.component.topicLabel
import org.mixit.conference.ui.formatter.formatDayDate
import org.mixit.conference.ui.formatter.formatTime
import kotlinx.html.DIV
import kotlinx.html.FORM
import kotlinx.html.FormMethod
import kotlinx.html.InputType
import kotlinx.html.ScriptType
import kotlinx.html.a
import kotlinx.html.b
import kotlinx.html.button
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.h4
import kotlinx.html.i
import kotlinx.html.id
import kotlinx.html.img
import kotlinx.html.input
import kotlinx.html.li
import kotlinx.html.onClick
import kotlinx.html.p
import kotlinx.html.script
import kotlinx.html.small
import kotlinx.html.span
import kotlinx.html.textArea
import kotlinx.html.title
import kotlinx.html.ul
import kotlinx.html.unsafe
import org.mixit.conference.model.event.Event
import org.mixit.conference.model.feedback.Feedback
import org.mixit.conference.model.feedback.TalkFeedback
import org.mixit.conference.model.feedback.UserTalkFeedback
import org.mixit.conference.model.people.Sponsor
import org.mixit.conference.model.shared.Context
import org.mixit.conference.model.shared.Language
import org.mixit.conference.model.talk.Talk
import org.mixit.conference.ui.DEFAULT_IMG_URL
import org.mixit.conference.ui.component.SectionEffect
import org.mixit.conference.ui.component.SectionStyle
import org.mixit.conference.ui.component.languageComponent
import org.mixit.conference.ui.component.roomComponent
import org.mixit.conference.ui.component.sectionComponent
import org.mixit.conference.ui.component.sponsorGroupComponent
import org.mixit.conference.ui.component.topicComponent
import org.mixit.conference.ui.component.videoComponent
import org.mixit.conference.ui.renderTemplate

fun renderTalk(
    context: Context,
    event: Event,
    sponsors: List<Sponsor>,
    talk: Talk,
    isFavorite: Boolean,
    isATalkSpeakerOrAdmin: Boolean,
    displayFeedback: Boolean,
    talkFeedback: TalkFeedback?,
    userTalkFeedback: UserTalkFeedback?,
    related: List<Talk> = emptyList(),
) =
    renderTemplate(context, event) {
        val programme = "${context.uriBasePath}/${talk.event}"
        pageHeaderComponent(
            context,
            rubrique = null,
            title = talk.title,
            breadcrumb = listOf(
                Crumb(context.i18n("talks.title"), "${context.uriBasePath}/${CURRENT_TALK_YEAR}"),
                Crumb("MiXiT ${talk.event}", programme),
            ),
            meta = {
                formatBadge(context, talk, talk.durationLabel())
                topicLabel(context, talk)
                val names = talk.speakers.joinToString(" & ") { it.fullName() }
                if (names.isNotBlank()) {
                    span { attributes["aria-hidden"] = "true"; +"·" }
                    span { +names }
                }
            },
        )

        pageContent {
            div(classes = "mxt-talk") {
                article(classes = "mxt-talk__main") {
                    talk.videos.firstOrNull()?.let { videoComponent(it) }
                    div(classes = "mxt-talk__abstract") {
                        h2(classes = "mxt-block-title") { +context.i18n("talk.v2.summary") }
                        div(classes = "mxt-talk__text") {
                            unsafe { raw(context.markdown(talk.summary) + context.markdown(talk.description)) }
                        }
                    }
                }

                aside(classes = "mxt-talk__aside") {
                    div(classes = "mxt-talk__facts") {
                        dl(classes = "mxt-talk__facts-list") {
                            talk.startLocalTime()?.let { start ->
                                dt { +context.i18n("talk.v2.when") }
                                dd {
                                    +start.date.formatDayDate(context.language)
                                    br
                                    +start.formatTime()
                                    talk.endLocalTime()?.let { +" – ${it.formatTime()}" }
                                }
                            }
                            context.roomLabel(talk)?.let { room ->
                                dt { +context.i18n("talk.v2.room") }
                                dd { +room }
                            }
                            talk.level?.let { level ->
                                dt { +context.i18n("talk.v2.level") }
                                dd { +context.i18n("talk.level.${level.name}") }
                            }
                            dt { +context.i18n("talk.v2.language") }
                            dd { +context.i18n("talk.language.${talk.language.name}") }
                        }
                        if (talk.themes.isNotEmpty()) {
                            div(classes = "mxt-talk__themes") {
                                talk.themes.forEach { theme ->
                                    a(href = "${context.uriBasePath}/archives/${theme.slug}", classes = "mxt-tag") {
                                        +context.i18n("themes.${theme.slug}.title")
                                    }
                                }
                            }
                        }
                        favoriteToggle(context, talk, isFavorite, context.talkHref(talk), withLabel = true)
                    }

                    talk.speakers.forEach { speaker ->
                        speakerCard(context, speaker, context.i18n("talk.v2.speaker"))
                    }
                }
            }

            if (displayFeedback) {
                feedbackSection(context, event, talk, isATalkSpeakerOrAdmin, talkFeedback, userTalkFeedback)
            }

            if (related.isNotEmpty()) {
                pageSection(context.i18n("talk.v2.related")) {
                    div(classes = "mxt-slot__sessions") {
                        related.forEach { sessionCard(context, it, favorite = false, redirect = context.talkHref(it)) }
                    }
                }
            }
        }

        sponsorsSection(context, event, sponsors)
    }

/** Speaker of a talk: photo (or initials), name, company, bio and a link to the speaker page. */
fun FlowContent.speakerCard(context: Context, speaker: Speaker, eyebrow: String) {
    div(classes = "mxt-speaker-card") {
        span(classes = "mxt-eyebrow mxt-eyebrow--muted") { +eyebrow }
        div(classes = "mxt-speaker-card__head") {
            if (speaker.photoUrl.isNullOrBlank()) {
                span(classes = "mxt-speaker-card__initials") {
                    attributes["aria-hidden"] = "true"
                    +speaker.initials()
                }
            } else {
                img(src = speaker.photoUrl, alt = "", classes = "mxt-speaker-card__photo") { attributes["loading"] = "lazy" }
            }
            div(classes = "mxt-speaker-card__name") {
                a(href = context.speakerHref(speaker)) { +speaker.fullName() }
                speaker.company?.takeIf { it.isNotBlank() }?.let { span { +it } }
            }
        }
        val bio = speaker.description[context.language] ?: speaker.description.values.firstOrNull()
        if (!bio.isNullOrBlank()) {
            div(classes = "mxt-speaker-card__bio") { unsafe { raw(context.markdown(bio)) } }
        }
        a(href = context.speakerHref(speaker), classes = "mxt-speaker-card__more") { +context.i18n("talk.v2.speaker.more") }
    }
}

private fun FlowContent.feedbackSection(
    context: Context,
    event: Event,
    talk: Talk,
    isATalkSpeakerOrAdmin: Boolean,
    talkFeedback: TalkFeedback?,
    userTalkFeedback: UserTalkFeedback?,
) {
    div(classes = "mxt-page__section mxt-talk__feedback") {
        id = "feedback"
        div(classes = "mxt-page__section-head") {
            h2(classes = "mxt-block-title") { +context.i18n("feedback.title") }
            if (Clock.System.now().toLocalDateTime(TimeZone.of("Europe/Paris")).date < event.start) {
                p(classes = "mxt-page__section-intro") { +context.i18n("feedback.preconf") }
            }
        }
        if (context.isAuthenticated) {
            p {
                +"${context.i18n("feedback.description").trim()} "
                i { +talk.title }
            }
            form(action = "/talks/${talk.id}/feedback", method = FormMethod.post) {
                feedbackTable(context, talk, talkFeedback, userTalkFeedback)
                label(classes = "mxt-talk__feedback-label") {
                    htmlFor = "comment"
                    +context.i18n("feedback.comment")
                }
                textArea(classes = "form-control", rows = "3") {
                    id = "comment"
                    name = "comment"
                    +(userTalkFeedback?.comment ?: "")
                }
                button(classes = "mxt-button mxt-button--primary mt-3") {
                    +context.i18n("feedback.action")
                }
            }
        } else {
            p { +context.i18n("feedback.anonymous") }
            a(href = "${context.uriBasePath}/login", classes = "mxt-button mxt-button--line") {
                +context.i18n("login.action.log")
            }
            div { feedbackTable(context, talk, talkFeedback) }
        }

        if (isATalkSpeakerOrAdmin) {
            div(classes = "mxt-talk__comments") {
                if (talkFeedback == null || talkFeedback.comments.isEmpty()) {
                    strong { +context.i18n("feedback.none.comments") }
                } else {
                    strong { +context.i18n("feedback.your.comments") }
                    ul { talkFeedback.comments.forEach { li { +it } } }
                }
            }
        }
    }
}

fun DIV.feedbackTable(context: Context, talk: Talk, talkFeedback: TalkFeedback?) {
    div(classes = "mxt-feedback__container mb-4") {
        Feedback.forFormat(talk.format).forEach {
            val votes = talkFeedback?.state?.get(it) ?: 0
            div(classes = "mxt-feedback ") {
                id = "feedback-container-${it.name}"
                a(classes = "mxt-feedback-button mxt-feedback-link") {
                    href = "${context.uriBasePath}/login"
                    id = "feedback-text-${it.name}"
                    div(classes = "mxt-feedback-link ") { +context.i18n("feedback.${it.name}") }
                    div {
                        small(classes = "mxt-feedback-link ") {
                            +"$votes vote${if (votes > 1) "s" else ""}"
                        }
                    }
                }
            }
        }

    }
}

fun FORM.feedbackTable(context: Context, talk: Talk, talkFeedback: TalkFeedback?, userTalkFeedback: UserTalkFeedback?) {
    div(classes = "mxt-feedback__container") {
        script(type = ScriptType.textJavaScript) {
            // We try to read the usage mode choosed by the user
            unsafe {
                raw(
                    """   
                                     function voteFeedback(feedback) {
                                        const container = document.getElementById("feedback-container-"+feedback);
                                        const input = document.getElementById("feedback-"+feedback);
                                        const initialValue = parseInt(input.getAttribute("initial-value"));
                                        const userInput = document.getElementById("feedback-user-"+feedback);
                                        if(userInput.value === "1" || parseInt(userInput.value) > 0) {
                                            input.value = (parseInt(input.value) - 1).toString();
                                            userInput.value = "0";
                                            container.classList.remove("mxt-feedback-active");
                                        } else {
                                            input.value = (parseInt(input.value) + 1).toString();
                                            userInput.value = "1";
                                            container.classList.add("mxt-feedback-active");
                                        }
                                        const feedbackText = document.getElementById("feedback-text-"+feedback);
                                        feedbackText.innerHTML = "<small>" + input.value + " vote" + (input.value > 1 ? "s" : "") + "</small>";
                                        // Call backend to save the feedback
                                        fetch(`/api/talks/${talk.id}/${"$"}{feedback}/` + userInput.value, {method: 'post'})
                                            .then(() => console.log("Feedback saved"));
                                     }
                                    """
                )
            }
        }
        Feedback.forFormat(talk.format).forEach {
            val votes = talkFeedback?.state?.get(it) ?: 0
            val userVote = if (userTalkFeedback?.notes?.contains(it) ?: false) 1 else 0

            div(classes = "mxt-feedback " + if (userVote == 1) "mxt-feedback-active" else "") {
                id = "feedback-container-${it.name}"
                input(classes = "mxt-feedback-input") {
                    type = InputType.hidden
                    name = "feedback-${it.name}"
                    id = "feedback-${it.name}"
                    value = votes.toString()
                    attributes["initial-value"] = votes.toString()
                }
                input {
                    readonly = true
                    type = InputType.hidden
                    id = "feedback-user-${it.name}"
                    value = userVote.toString()
                }
                button(classes = "mxt-feedback-button") {
                    type = ButtonType.button
                    onClick = "javascript:voteFeedback('${it.name}')"
                    +context.i18n("feedback.${it.name}")
                    div {
                        id = "feedback-text-${it.name}"
                        small {
                            +"$votes vote${if (votes > 1) "s" else ""}"
                        }
                    }
                }
            }
        }

    }
}
