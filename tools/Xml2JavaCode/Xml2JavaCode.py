#!/usr/bin/python
# -*- coding:UTF-8 -*-
import os
import xml
from xml.sax import ContentHandler

import config
import CodeGenerator


class XmlHandler(ContentHandler):
    def __init__(self):
        self.CurrentData = ""
        self.dataTypes = {}

    def startElement(self, tag, attributes):
        self.CurrentData = tag
        if tag == "Root":
            for k, v in attributes.items():
                self.dataTypes[k] = v

    def endElement(self, tag):
        self.CurrentData = ""

    def getDataTypes(self):
        return self.dataTypes


def generate():
    confDict = config.parse("./conf.ini")
    parser = xml.sax.make_parser()
    parser.setFeature(xml.sax.handler.feature_namespaces, 0)
    generator = CodeGenerator.CodeGenerator(confDict["xml_path"], confDict["java_code"], confDict["java_package"])
    for xmlFile in confDict["xml_files"]:
        xmlHandler = XmlHandler()
        parser.setContentHandler(xmlHandler)
        parser.parse(os.path.join(confDict["xml_path"], xmlFile))
        dataTypes = xmlHandler.getDataTypes()
        if dataTypes:
            xmlFileName = os.path.splitext(xmlFile)[0]
            generator.generateEntityFile(xmlFileName, dataTypes)
            generator.generateConfigFile(xmlFileName)
            generator.generateParserFile(xmlFileName)


if __name__ == "__main__":
    generate()
