package net.nekomura.dcbot.enums

enum class ConfigLongData {
    /**
     * OWNER
     */
    OWNER,

    /**
     * 每次確認pixiv/Minecraft更新的間隔時間
     */
    SCHEDULED_CHECKER_DELAY,

    /**
     * Minecraft更新推播頻道
     */
    MINECRAFT_PUSH_NOTIFICATION_CHANNEL,

    /**
     * pixiv更新推播頻道(全年齡)
     */
    PIXIV_PUSH_NOTIFICATION_CHANNEL,

    /**
     * pixiv更新推播頻道(R-18)
     */
    PIXIV_R18_PUSH_NOTIFICATION_CHANNEL
}