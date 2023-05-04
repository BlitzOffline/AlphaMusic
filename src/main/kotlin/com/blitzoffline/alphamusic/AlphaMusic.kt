package com.blitzoffline.alphamusic

import com.blitzoffline.alphamusic.audio.GuildMusicManager
import com.blitzoffline.alphamusic.audio.PlayerManager
import com.blitzoffline.alphamusic.audio.TrackMetadata
import com.blitzoffline.alphamusic.audio.TrackService
import com.blitzoffline.alphamusic.commands.*
import com.blitzoffline.alphamusic.listeners.VoiceListener
import com.blitzoffline.alphamusic.tasks.TaskManager
import com.blitzoffline.alphamusic.utils.terminate
import com.github.ygimenez.model.PaginatorBuilder
import com.github.ygimenez.type.Emote
import dev.triumphteam.cmd.core.message.MessageKey
import dev.triumphteam.cmd.core.message.context.MessageContext
import dev.triumphteam.cmd.core.requirement.RequirementKey
import dev.triumphteam.cmd.jda.SlashCommandManager
import dev.triumphteam.cmd.jda.sender.SlashCommandSender
import dev.triumphteam.cmd.jda.sender.SlashSender
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.requests.RestAction
import net.dv8tion.jda.api.utils.cache.CacheFlag
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class AlphaMusic(private val token: String, youtubeEmail: String?, youtubePass: String?) {
    val logger: Logger = LoggerFactory.getLogger(this::class.java)
    val playerManager = PlayerManager(youtubeEmail, youtubePass)
    val trackService = TrackService(this)
    val taskManager = TaskManager()
    private val musicManagers = HashMap<String, GuildMusicManager>()

    private lateinit var commandManager: SlashCommandManager<SlashSender>
    lateinit var jda: JDA
        private set

    fun run() {
        jda = createJDAInstance()
        jda.awaitReady()
        RestAction.setPassContext(true)
        RestAction.setDefaultFailure(Throwable::printStackTrace)
        commandManager = SlashCommandManager.create(jda)

        registerPagination()

        registerRequirements()
        registerMessages()
        registerCommands()
        jda.getGuildById("1054382015539056690")?.let {
            println("Guild name: ${it.name}")
            commandManager.registerCommand(it, DebugCommand())
        }

        commandManager.pushCommands()
    }

    private fun createJDAInstance() = JDABuilder
        .create(
            token,
            listOf(
                GatewayIntent.GUILD_EMOJIS_AND_STICKERS,
                GatewayIntent.GUILD_VOICE_STATES,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_MESSAGE_REACTIONS,
            )
        )
        .disableCache(
            CacheFlag.ACTIVITY,
            CacheFlag.CLIENT_STATUS,
            CacheFlag.ONLINE_STATUS,
            CacheFlag.SCHEDULED_EVENTS,
        )
        .addEventListeners(
            VoiceListener(this)
        )
        .build()

    @Synchronized fun getMusicManager(guild: Guild): GuildMusicManager {
        var musicManager = musicManagers[guild.id]

        if (musicManager == null) {
            musicManager = GuildMusicManager(this, guild.id)
            musicManagers[guild.id] = musicManager
        }

        guild.audioManager.sendingHandler = musicManager.audioHandler
        return musicManager
    }

    companion object {
        const val EMBED_COLOR = 0x70BB2B
    }

    private fun registerPagination() {
        PaginatorBuilder.createPaginator()
            .setHandler(jda)
            .shouldRemoveOnReact(false)
            .shouldEventLock(true)
            .setEmote(Emote.CANCEL, "✖️")
            .activate()
    }

    private fun registerRequirements() {
        commandManager.registerRequirement(RequirementKey.of("command_in_guild")) { sender, _ ->
            sender.guild != null && sender.member != null
        }

        commandManager.registerRequirement(RequirementKey.of("bot_in_vc")) { sender, _ ->
            sender.guild?.selfMember?.voiceState?.channel != null
        }

        commandManager.registerRequirement(RequirementKey.of("member_in_vc")) { sender, _ ->
            sender.member?.voiceState?.channel != null
        }

        commandManager.registerRequirement(RequirementKey.of("admin")) { sender, _ ->
            sender.member?.permissions?.contains(Permission.ADMINISTRATOR) ?: false
        }

        commandManager.registerRequirement(RequirementKey.of("same_channel_or_admin")) { sender, _ ->
            val member = sender.member

            if (member == null) {
                false
            } else {
                member.permissions.contains(Permission.ADMINISTRATOR) || member.voiceState?.channel == sender.guild?.selfMember?.voiceState?.channel
            }
        }

        commandManager.registerRequirement(RequirementKey.of("paused")) { sender, _ ->
            val guild = sender.guild
            if (guild == null) {
                true
            } else {
                getMusicManager(guild).player.isPaused
            }
        }

        commandManager.registerRequirement(RequirementKey.of("requester_or_admin")) { sender, _ ->
            val guild = sender.guild
            if (guild == null) {
                false
            } else {
                val member = sender.member
                if (member == null) {
                    false
                } else {
                    if (member.hasPermission(Permission.ADMINISTRATOR)) {
                        true
                    } else {
                        val playing = getMusicManager(guild).player.playingTrack
                        if (playing == null) {
                            true
                        } else {
                            val meta = playing.userData as TrackMetadata
                            meta.data.id == member.id
                        }
                    }
                }
            }
        }
    }

    private fun registerMessages() {
        commandManager.registerMessage(MessageKey.of("command_not_in_guild", MessageContext::class.java)) { sender, _ ->
            if (sender !is SlashCommandSender) return@registerMessage
            sender.event.terminate(reason = "This command can only be used in a guild!", ephemeral = true)
        }

        commandManager.registerMessage(MessageKey.of("bot_not_in_vc", MessageContext::class.java)) { sender, _ ->
            if (sender !is SlashCommandSender) return@registerMessage
            sender.event.terminate("The bot is currently not connected to a voice channel!", ephemeral = true)
        }

        commandManager.registerMessage(MessageKey.of("bot_already_in_vc", MessageContext::class.java)) { sender, _ ->
            if (sender !is SlashCommandSender) return@registerMessage
            sender.event.terminate(reason = "The bot is already connected to a voice channel!", ephemeral = true)
        }

        commandManager.registerMessage(MessageKey.of("member_not_in_vc", MessageContext::class.java)) { sender, _ ->
            if (sender !is SlashCommandSender) return@registerMessage
            sender.event.terminate(reason = "You need to be connected to a voice channel!", ephemeral = true)
        }

        commandManager.registerMessage(MessageKey.of("not_same_channel_or_admin", MessageContext::class.java)) { sender, _ ->
            if (sender !is SlashCommandSender) return@registerMessage
            sender.event.terminate(reason = "You need to be in the same Voice Channel as the bot to do this!", ephemeral = true)
        }

        commandManager.registerMessage(MessageKey.of("not_admin", MessageContext::class.java)) { sender, _ ->
            if (sender !is SlashCommandSender) return@registerMessage
            sender.event.terminate(reason = "You can't do this!", ephemeral = true)
        }

        commandManager.registerMessage(MessageKey.of("not_paused", MessageContext::class.java)) { sender, _ ->
            if (sender !is SlashCommandSender) return@registerMessage
            sender.event.terminate(reason = "The audio is not paused. Use \"/pause\" to pause!", ephemeral = true)
        }

        commandManager.registerMessage(MessageKey.of("paused", MessageContext::class.java)) { sender, _ ->
            if (sender !is SlashCommandSender) return@registerMessage
            sender.event.terminate(reason = "The audio is already paused. Use \"/resume\" to resume!", ephemeral = true)
        }

        commandManager.registerMessage(MessageKey.of("not_requester_or_admin", MessageContext::class.java)) { sender, _ ->
            if (sender !is SlashCommandSender) return@registerMessage
            val guild = sender.guild ?: return@registerMessage
            val manager = getMusicManager(guild)
            val playing = manager.player.playingTrack ?: return@registerMessage
            val meta = playing.userData as TrackMetadata

            sender.event.terminate(
                reason = "Only the requester or admins can do this. Requester: ${meta.data.name}#${meta.data.discriminator}",
                ephemeral = true
            )
        }
    }

    private fun registerCommands() {
        commandManager.registerCommand(
            HelpCommand(),
            PlayCommand(this),
            PingCommand(this),
            LoopCommand(this),
            QueueCommand(this),
            NowPlayingCommand(this),
            GrabCommand(this),
            ShuffleCommand(this),
            VolumeCommand(this),
            PauseCommand(this),
            ResumeCommand(this),
            SkipCommand(this),
            JoinCommand(),
            RemoveDupesCommand(this),
            ClearCommand(this),
            ReplayCommand(this),
            StopCommand(this),
            RemoveCommand(this),
            LeaveCommand(this),
            SeekCommand(this),
            ForwardCommand(this),
            RewindCommand(this),
            RadioCommand(this),
        )
    }
}