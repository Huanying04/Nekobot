package net.nekomura.dcbot

import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Listener: ListenerAdapter() {
    private val logger: Logger = LoggerFactory.getLogger(Listener::class.java)

    override fun onReady(event: ReadyEvent) {
        logger.info("{} is ready", event.jda.selfUser.asTag)
    }
}