package net.nekomura.dcbot.Utils

object StringUtils {
    fun String.subString(from: String?, to: String?): String {
        val fromIndex = this.indexOf(from!!)
        val toIndex = this.indexOf(to!!, fromIndex)
        return this.subSequence(fromIndex, toIndex).toString().replace(from, "")
    }
}