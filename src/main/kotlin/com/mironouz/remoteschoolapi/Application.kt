package com.mironouz.remoteschoolapi

import com.mironouz.remoteschoolapi.config.appConfig
import com.mironouz.remoteschoolapi.config.securityConfig
import de.flapdoodle.embed.mongo.distribution.Version
import org.springframework.boot.WebApplicationType
import org.springframework.fu.kofu.application
import org.springframework.fu.kofu.mongo.reactiveMongodb
import org.springframework.fu.kofu.webflux.webFlux

val app = application(WebApplicationType.REACTIVE) {
    enable(appConfig)
    enable(securityConfig)
    webFlux {
        codecs {
            jackson()
        }
    }
    reactiveMongodb {
        embedded {
            version = Version.Main.PRODUCTION
        }
    }
}

fun main() {
    app.run()
}
