package net.nekomura.dcbot

object Sort {
    fun bubble(arr: ArrayList<Char>): ArrayList<Char> {
        for (i in 0 until arr.size - 1) {
            for (j in 0 until arr.size - 1 - i) {
                if (arr[j] > arr[j + 1]) {
                    val temp = arr[j + 1]
                    arr[j + 1] = arr[j]
                    arr[j] = temp
                }
            }
        }
        return arr
    }

    @JvmName("bubble1")
    fun bubble(arr: ArrayList<Long>): ArrayList<Long> {
        for (i in 0 until arr.size - 1) {
            for (j in 0 until arr.size - 1 - i) {
                if (arr[j] > arr[j + 1]) {
                    val temp = arr[j + 1]
                    arr[j + 1] = arr[j]
                    arr[j] = temp
                }
            }
        }
        return arr
    }
}