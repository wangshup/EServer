#!/usr/bin/python
# -*- coding:UTF-8 -*-
import configparser


def parse(filename):
    confDict = {}

    conf = configparser.ConfigParser()
    conf.read(filename)

    for section in conf.sections():
        if section == "xml2java":
            for (key, value) in conf.items(section):
                if key == "xml_path":   confDict['xml_path'] = str(value)
                if key == "xml_files": confDict['xml_files'] = str(value).split(',')
                if key == "java_code":   confDict['java_code'] = str(value)
                if key == "java_package":   confDict['java_package'] = str(value)

    return confDict


if __name__ == "__main__":
    print(parse("D:/work/CLionProjects/C11Test/conf.ini"))
