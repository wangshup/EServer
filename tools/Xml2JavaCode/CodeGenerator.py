#!/usr/bin/python
# -*- coding:UTF-8 -*-
import os

import CodeTemplate


def getClassName(fileName):
    files = fileName.split("_")
    className = ""
    for name in files:
        className = name[0].upper() + name[1:]
    return className


def getOutputPath(codePath, package):
    paths = package.split(".")
    outputPath = os.path.join(codePath)
    for path in paths:
        outputPath = os.path.join(outputPath, path)
    return outputPath


def outputFile(codeData, codeFileName, codePath, package):
    codePath = getOutputPath(codePath, package)
    if not os.path.exists(codePath):
        os.makedirs(codePath)
    codeFile = os.path.join(codePath, codeFileName)
    with open(codeFile, 'w') as f:
        f.write(codeData)


class CodeGenerator:
    def __init__(self, xmlPath, codePath, package):
        self.__xmlPath = xmlPath
        self.__codePath = codePath
        self.__package = package

    def generateEntityFile(self, xmlFileName, dataTypes):
        name = getClassName(xmlFileName)
        code = ["package ", self.__package, ".entities;\r\rimport java.util.Map;\r\rpublic class ", name,
                "Entity {\r\r"]
        getCode = ["\r\r"]
        for fName, fType in dataTypes.items():
            code.append("    private ")
            getCode.append("    public ")
            if fType == "int":
                code.append("int ")
                getCode.append("int get")
            elif fType == "long":
                code.append("long ")
                getCode.append("long get")
            elif fType == "double":
                code.append("double ")
                getCode.append("double get")
            elif fType == "float":
                code.append("float ")
                getCode.append("float get")
            elif fType == "bool":
                code.append("boolean ")
                getCode.append("boolean get")
            elif fType == "[int]":
                code.append("int[] ")
                getCode.append("int[] get")
            elif fType == "[long]":
                code.append("long[] ")
                getCode.append("long[] get")
            elif fType == "[double]":
                code.append("double[] ")
                getCode.append("double[] get")
            elif fType == "[float]":
                code.append("float[] ")
                getCode.append("float[] get")
            elif fType == "[boolean]":
                code.append("boolean[] ")
                getCode.append("boolean[] get")
            elif fType == "[string]":
                code.append("String[] ")
                getCode.append("String[] get")
            elif fType == "(int,int)":
                code.append("Map<Integer,Integer> ")
                getCode.append("Map<Integer,Integer> get")
            elif fType == "(int,long)":
                code.append("Map<Integer,Long> ")
                getCode.append("Map<Integer,Long> get")
            elif fType == "(int,string)":
                code.append("Map<Integer,String> ")
                getCode.append("Map<Integer,String> get")
            elif fType == "(int,float)":
                code.append("Map<Integer,Float> ")
                getCode.append("Map<Integer,Float> get")
            elif fType == "(int,double)":
                code.append("Map<Integer,Double> ")
                getCode.append("Map<Integer,Double> get")
            elif fType == "(int,boolean)":
                code.append("Map<Integer,Boolean> ")
                getCode.append("Map<Integer,Boolean> get")
            elif fType == "(string,string)":
                code.append("Map<String,String> ")
                getCode.append("Map<String,String> get")
            elif fType == "(string,int)":
                code.append("Map<String,Integer> ")
                getCode.append("Map<String,Integer> get")
            elif fType == "(string,long)":
                code.append("Map<String,Long> ")
                getCode.append("Map<String,Long> get")
            elif fType == "(string,float)":
                code.append("Map<String,Float> ")
                getCode.append("Map<String,Float> get")
            elif fType == "(string,double)":
                code.append("Map<String,Double> ")
                getCode.append("Map<String,Double> get")
            elif fType == "(string,boolean)":
                code.append("Map<String,Boolean> ")
                getCode.append("Map<String,Boolean> get")
            else:
                code.append("String ")
                getCode.append("String get")
            code.append(fName)
            code.append(";\r")
            getCode.append(fName[0].upper() + fName[1:])
            getCode.append("() {\r")
            getCode.append("        return ")
            getCode.append(fName)
            getCode.append(";\r    }\r")
        code.append("".join(getCode))
        code.append("}")
        outputFile("".join(code), name + "Entity.java", self.__codePath, self.__package + ".entities")

    def generateConfigFile(self, xmlFileName):
        uname = getClassName(xmlFileName)
        code = CodeTemplate.getConfigCode(self.__package)
        code = code.replace("@uname", uname).replace("@lname", xmlFileName)
        outputFile(code, uname + "Config.java", self.__codePath, self.__package + ".entities")

    def generateParserFile(self, xmlFileName):
        uname = getClassName(xmlFileName)
        code = CodeTemplate.getParseCode(self.__package)
        code = code.replace("@uname", uname).replace("@lname", xmlFileName)
        outputFile(code, uname + "Parser.java", self.__codePath, self.__package + ".parser")


if __name__ == "__main__":
    pass
