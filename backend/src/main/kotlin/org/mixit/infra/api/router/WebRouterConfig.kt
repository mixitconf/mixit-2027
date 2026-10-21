package org.mixit.infra.api.router

import org.mixit.conference.model.feedback.Feedback
import org.mixit.conference.model.shared.Context
import org.mixit.conference.ui.CURRENT_MEDIA_YEAR
import org.mixit.conference.ui.CURRENT_TALK_YEAR
import org.mixit.conference.ui.CURRENT_YEAR
import org.mixit.conference.ui.page.renderError
import org.mixit.conference.ui.security.loginStartForm
import org.mixit.domain.api.EventScreen
import org.mixit.infra.api.AuthenticationHandler
import org.mixit.infra.api.EventHandler
import org.mixit.infra.api.FaqHandler
import org.mixit.infra.api.FavoriteHandler
import org.mixit.infra.api.FeedbackHandler
import org.mixit.infra.api.ArchiveHandler
import org.mixit.infra.api.MediaHandler
import org.mixit.infra.api.PeopleHandler
import org.mixit.infra.api.PollHandler
import org.mixit.infra.api.TalkHandler
import org.mixit.infra.api.mapper.toLoginForm
import org.mixit.infra.api.mapper.toLoginStartForm
import org.mixit.infra.api.mapper.toRegistringForm
import org.mixit.infra.api.mapper.toTalkCriteria
import org.mixit.infra.config.WebContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.servlet.function.router

@Configuration
class WebRouterConfig {
    @Bean
    fun webRouter(
        authenticationApi: AuthenticationHandler,
        eventHandler: EventHandler,
        faqHandler: FaqHandler,
        peopleHandler: PeopleHandler,
        talkHandler: TalkHandler,
        mediaHandler: MediaHandler,
        archiveHandler: ArchiveHandler,
        favoriteHandler: FavoriteHandler,
        feedbackHandler: FeedbackHandler,
        pollHandler: PollHandler,
        webContext: WebContext,
    ) = router {
        accept(MediaType.TEXT_HTML).nest {
            arrayOf("/", "/fr", "/en").forEach { path ->
                GET(path) {
                    eventHandler.findByYear(CURRENT_YEAR, EventScreen.HOME)
                }
            }
            GET("/about") {
                eventHandler.findByYear(CURRENT_YEAR, EventScreen.ABOUT)
            }
            GET("/accessibility") {
                eventHandler.findByYear(CURRENT_YEAR, EventScreen.ACCESSIBILITY)
            }
            (GET("/code-of-conduct") or GET("/codeofconduct")) {
                eventHandler.findByYear(CURRENT_YEAR, EventScreen.CODE_OF_CONDUCT)
            }
            (GET("/code-of-conduct") or GET("/codeofconduct")) {
                eventHandler.findByYear(CURRENT_YEAR, EventScreen.CODE_OF_CONDUCT)
            }
            GET("/error") {
                val status = it.attribute("jakarta.servlet.error.status_code").orElse(null)?.toString()
                if (status == "401") {
                    authenticationApi.logout()
                }
                ok()
                    .contentType(MediaType.TEXT_HTML)
                    .body(
                        renderError(
                            context = webContext.context ?: Context.default(),
                            status = status,
                        ),
                    )
            }
            GET("/faq") {
                faqHandler.findAllQuestions()
            }
            GET("/archives") {
                archiveHandler.findThemes()
            }
            GET("/archives/topic/{topic}") {
                archiveHandler.findTopic(it.pathVariable("topic"))
            }
            GET("/archives/year/{year}") {
                archiveHandler.findYear(it.pathVariable("year").toIntOrNull() ?: 0)
            }
            GET("/archives/{theme}") {
                archiveHandler.findTheme(it.pathVariable("theme"))
            }
            GET("/media") {
                mediaHandler.findMediaByYear(CURRENT_MEDIA_YEAR, it.param("search").orElse(null))
            }
            GET("/mixette") {
                peopleHandler.findOrganizationByYear(CURRENT_YEAR)
            }
            GET("/schedule") {
                talkHandler.findTalkByYear(CURRENT_TALK_YEAR, it.toTalkCriteria(webContext.context))
            }
            GET("/speakers") {
                peopleHandler.findSpeakerByYear(CURRENT_TALK_YEAR)
            }
            GET("/speakers/{login}") {
                peopleHandler.findSpeakerByLogin(it.pathVariable("login"))
            }
            GET("/sponsors") {
                peopleHandler.findSponsorByYear(CURRENT_YEAR)
            }
            GET("/talks") {
                talkHandler.findTalkByYear(CURRENT_TALK_YEAR, it.toTalkCriteria(webContext.context))
            }
            GET("/feedbacks-qrcode") {
                talkHandler.findTalkFeedbacks(CURRENT_TALK_YEAR, it.toTalkCriteria(webContext.context))
            }
            GET("/user/{login}") {
                peopleHandler.findSpeakerByLogin(it.pathVariable("login"))
            }
            GET("/venue") {
                eventHandler.findByYear(CURRENT_YEAR, EventScreen.VENUE)
            }
            GET("/budget") {
                eventHandler.findByYear(CURRENT_YEAR, EventScreen.BUDGET)
            }
            GET("/anniversaire") {
                pollHandler.findPoll()
            }
            POST("/anniversaire") { req ->
                pollHandler.savePoll(
                    lotteryParticipation = req.param("lotteryParticipation").orElse("false")!!,
                    keynoteFeeds = req.param("keynoteFeeds"),
                    conferenceFeeds = req.param("conferenceFeeds"),
                )
            }
            (2012..CURRENT_YEAR).forEach { year ->
                GET("/$year") {
                    talkHandler.findTalkByYear(year, it.toTalkCriteria(webContext.context))
                }
                GET("/$year/media") {
                    mediaHandler.findMediaByYear(year, it.param("search").orElse(null))
                }
                GET("/$year/medias/images") {
                    mediaHandler.findMediaByYear(year, it.param("search").orElse(null))
                }
                GET("/$year/medias/images/{album}") {
                    mediaHandler.findMediaByYearAndSection(year, it.pathVariable("album"))
                }
                GET("/$year/medias/images/{album}/{name}") {
                    mediaHandler.findPhoto(year, it.pathVariable("album"), it.pathVariable("name"))
                }
                GET("/$year/medias/images/{album}/{name}") {
                    mediaHandler.findPhoto(year, it.pathVariable("album"), it.pathVariable("name"))
                }
                GET("/$year/about") {
                    eventHandler.findByYear(year, EventScreen.ABOUT)
                }
                GET("/$year/mixette") {
                    peopleHandler.findOrganizationByYear(year)
                }
                GET("/$year/sponsors") {
                    peopleHandler.findSponsorByYear(year)
                }
                GET("/$year/speakers") {
                    peopleHandler.findSpeakerByYear(year)
                }
                GET("/speakers/$year/{login}") {
                    peopleHandler.findSpeakerByLogin(it.pathVariable("login"), year)
                }
                GET("/$year/{slug}") {
                    talkHandler.findTalkBySlug(year, it.pathVariable("slug"))
                }
            }
            POST("/talks/{talkId}/favorite") {
                val email = webContext.context?.email ?: return@POST ServerResponse.status(403).build()
                favoriteHandler.toggleFavorite(email, it.pathVariable("talkId"))
            }
            POST("/api/talks/{talkId}/{feedback}/{value}") { req ->
                val email = webContext.context?.email ?: return@POST ServerResponse.status(403).build()
                feedbackHandler.saveFeedback(
                    email = email,
                    talkId = req.pathVariable("talkId"),
                    feedback = Feedback.valueOf(req.pathVariable("feedback").uppercase()),
                    toAdd = req.pathVariable("value") == "1",
                )
            }
            POST("/talks/{talkId}/feedback") { req ->
                val email = webContext.context?.email ?: return@POST ServerResponse.status(403).build()
                feedbackHandler.saveFeedbacks(
                    email = email,
                    talkId = req.pathVariable("talkId"),
                    feedbacks =
                        Feedback.entries.filter {
                            req.param("feedback-$it").orElse(null) == "1"
                        },
                    comment = req.param("comment").orElse(null),
                )
            }
            // Security
            GET("/login") {
                authenticationApi.login(loginStartForm(dirty = false))
            }
            GET("/logout") {
                authenticationApi.logout()
            }
            POST("/login") {
                authenticationApi.loginStartSend(it.toLoginStartForm())
            }
            POST("/login-finalize") {
                authenticationApi.loginFinalize(it.toLoginForm())
            }
            GET("/signin/{code}/{email}") {
                authenticationApi.loginFinalize(
                    it.toLoginForm(it.pathVariable("email") to it.pathVariable("code"), dirty = false),
                )
            }
            POST("/signup") {
                authenticationApi.signup(
                    it.toRegistringForm(context = webContext.ctx()),
                )
            }
        }
    }
}
