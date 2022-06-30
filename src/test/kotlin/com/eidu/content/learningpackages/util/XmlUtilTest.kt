package com.eidu.content.learningpackages.util

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.matchesPredicate
import org.junit.jupiter.api.Test

class XmlUtilTest {
    @Test
    fun `parseXml parses XML`() {
        assertThat(parseXml("<x/>")).matchesPredicate {
            it.childNodes.item(0).nodeName == "x"
        }
    }

    @Test
    fun `getStrings gets list of text contents of selected nodes`() {
        val doc = parseXml(
            """
                <root>
                    <item>1</item>
                    <item>2<leaf>3</leaf></item>
                    <item/>
                </root>
            """
        )
        assertThat(doc.getStrings("/root/item").toList()).isEqualTo(
            listOf(
                "1", "23", ""
            )
        )
    }
}
