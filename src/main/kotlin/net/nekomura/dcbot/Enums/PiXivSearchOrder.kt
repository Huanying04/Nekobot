package net.nekomura.dcbot.Enums

/**
 * pixiv搜尋排序方法
 */
enum class PiXivSearchOrder {
    /**
     * 按最新排序
     */
    NEW_TO_OLD {
        override fun toString(): String {
            return "date_d"
        }
    },

    /**
     * 按舊排序
     */
    OLD_TO_NEW {
        override fun toString(): String {
            return "date"
        }
    }
}