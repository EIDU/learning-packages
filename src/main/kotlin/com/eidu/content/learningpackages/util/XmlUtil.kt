package com.eidu.content.learningpackages.util

import org.w3c.dom.Document
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

private val documentBuilderFactory by lazy { DocumentBuilderFactory.newInstance() }
private val xpath by lazy { XPathFactory.newInstance().newXPath() }

fun parseXml(xml: String): Document =
    xml.byteInputStream().use { documentBuilderFactory.newDocumentBuilder().parse(it) }

fun Document.getStrings(expression: String): Sequence<String> {
    val nodes = xpath.evaluate(expression, this, XPathConstants.NODESET) as NodeList
    return nodes.asSequence().map { it.textContent }
}

fun NodeList.asSequence(): Sequence<Node> = sequence {
    for (i in 0 until length)
        yield(item(i))
}
