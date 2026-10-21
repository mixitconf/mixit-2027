package org.mixit.conference.ui.component

import kotlinx.html.*
import org.mixit.conference.model.link.Video
import org.mixit.conference.model.link.VideoType
import org.mixit.conference.model.shared.Context
import org.mixit.conference.model.talk.Talk

fun DIV.videoComponent(context: Context, talk: Talk, featured: Boolean = false) =
    talkCardComponent(context, talk, featured)

fun DIV.videoComponent(video: Video) {
    div("mxt-video__player-container") {
        when (video.type) {
            VideoType.VIMEO -> {
                iframe(classes = "mxt-video__player mxt-img__lazyload") {
                    attributes["data-src"] = video.playerUrl!!
                    attributes["allowfullscreen"] = "true"
                }
            }

            VideoType.YOUTUBE -> {
                iframe(classes = "mxt-video__player mxt-img__lazyload") {
                    attributes["data-src"] = video.playerUrl!!
                    attributes["allowfullscreen"] = "true"
                    attributes["accelerometer"] = "true"
                    attributes["autoplay"] = "true"
                    attributes["clipboard-write"] = "true"
                    attributes["encrypted-media"] = "true"
                    attributes["gyroscope"] = "true"
                    attributes["picture-in-picture"] = "true"
                    attributes["web-share"] = "true"
                }
            }

            else -> {
                a(href = video.playerUrl, target = "_blank") {
                    img(classes = "mxt-img__lazyload") {
                        attributes["data-src"] = "/images/svg/mxt-no-player.svg"
                        alt = "No video player"
                    }
                }
            }
        }
    }
}