package me.ykrank.s1next.widget.span

import com.github.ykrank.androidtools.util.L.report
import org.xml.sax.Attributes
import org.xml.sax.XMLReader

internal object HtmlTagHandlerCompat {
    fun processAttributes(xmlReader: XMLReader): Attributes? {
        try {
            val elementField = xmlReader.javaClass.getDeclaredField("theNewElement")
            elementField.isAccessible = true
            val element = elementField[xmlReader]
            if (element != null) {
                val attsField = element.javaClass.getDeclaredField("theAtts")
                attsField.isAccessible = true
                val atts = attsField[element]
                return atts as Attributes
            }
        } catch (e: Exception) {
            report(e)
        }
        return null
    }
}
