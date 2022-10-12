package wasdev.ejb.ui

import kotlin.properties.Delegates

class DiceRequest() {
    lateinit var type: String
    var quantity by Delegates.notNull<Int>()
}