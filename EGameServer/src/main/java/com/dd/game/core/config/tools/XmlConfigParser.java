package com.dd.game.core.config.tools;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XmlConfigParser extends DefaultHandler {
    private String rootElement = "ROOT";
    private Map<String, String> dataTypes = new LinkedHashMap<>();
    private String resourcePath;
    private String outputPath;

    public XmlConfigParser(String resourcePath, String outputPath) {
        this.resourcePath = resourcePath;
        this.outputPath = outputPath;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (isValidElement(qName, rootElement)) {
            for (int i = 0; i < attributes.getLength(); ++i) {
                dataTypes.put(attributes.getQName(i), attributes.getValue(i).trim());
            }
        }
    }

    private boolean isValidElement(String qName, String elementName) {
        return qName.equalsIgnoreCase(elementName);
    }

    private boolean onParseCompleted(String name) {
        if (dataTypes.isEmpty())
            return false;
        this.generateEntityFile(name);
        this.generateConfigFile(name);
        this.generateParseFile(name);
        dataTypes.clear();
        return true;
    }

    private void generateConfigFile(String name) {
        String uname = getClassName(name);
        String strCode = CodeTemplet.configFile.replaceAll("@uname", uname);
        strCode = strCode.replaceAll("@lname", name);
        try {
            Output output = new OutPutImpl(outputPath);
            output.outputFile(strCode, uname + "Config.java", CodeTemplet.packageName + ".entitys");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void generateParseFile(String name) {
        String uname = getClassName(name);
        String strCode = CodeTemplet.parseFile.replaceAll("@uname", uname);
        strCode = strCode.replaceAll("@lname", name);
        try {
            Output output = new OutPutImpl(outputPath);
            output.outputFile(strCode, uname + "Parser.java", CodeTemplet.packageName + ".parser");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getClassName(String fileName) {
        String[] strs = fileName.split("_");
        StringBuilder sb = new StringBuilder();
        for (String str : strs) {
            sb.append(str.substring(0, 1).toUpperCase());
            sb.append(str.substring(1));
        }
        return sb.toString();
    }

    private void generateEntityFile(String name) {
        name = getClassName(name);
        StringBuilder sb = new StringBuilder();
        StringBuilder sbGet = new StringBuilder();
        sbGet.append("\r\n");
        sb.append("package " + CodeTemplet.packageName + ".entitys;\r\n\r\n");
        sb.append("import java.util.Map;\r\n\r\n");
        sb.append("public class ");
        sb.append(name);
        sb.append("Entity {\r\n\r\n");
        for (Entry<String, String> e : dataTypes.entrySet()) {
            String fName = e.getKey();
            String fType = e.getValue();
            sb.append("    private ");
            sbGet.append("    public ");
            switch (fType) {
            case "int":
                sb.append("int ");
                sbGet.append("int get");
                break;
            case "long":
                sb.append("long ");
                sbGet.append("long get");
                break;
            case "double":
                sb.append("double ");
                sbGet.append("double get");
                break;
            case "float":
                sb.append("float ");
                sbGet.append("float get");
                break;
            case "bool":
                sb.append("boolean ");
                sbGet.append("boolean get");
                break;
            case "[int]":
                sb.append("int[] ");
                sbGet.append("int[] get");
                break;
            case "[long]":
                sb.append("long[] ");
                sbGet.append("long[] get");
                break;
            case "[double]":
                sb.append("double[] ");
                sbGet.append("double[] get");
                break;
            case "[float]":
                sb.append("float[] ");
                sbGet.append("float[] get");
                break;
            case "[boolean]":
                sb.append("boolean[] ");
                sbGet.append("boolean[] get");
                break;
            case "[string]":
                sb.append("String[] ");
                sbGet.append("String[] get");
                break;
            case "[int,int]":
                sb.append("Map<Integer,Integer> ");
                sbGet.append("Map<Integer,Integer> get");
                break;
            case "[int,long]":
                sb.append("Map<Integer,Long> ");
                sbGet.append("Map<Integer,Long> get");
                break;
            case "[int,string]":
                sb.append("Map<Integer,String> ");
                sbGet.append("Map<Integer,String> get");
                break;
            case "[int,float]":
                sb.append("Map<Integer,Float> ");
                sbGet.append("Map<Integer,Float> get");
                break;
            case "[int,double]":
                sb.append("Map<Integer,Double> ");
                sbGet.append("Map<Integer,Double> get");
                break;
            case "[int,boolean]":
                sb.append("Map<Integer,Boolean> ");
                sbGet.append("Map<Integer,Boolean> get");
                break;
            case "[string,string]":
                sb.append("Map<String,String> ");
                sbGet.append("Map<String,String> get");
                break;
            case "[string,int]":
                sb.append("Map<String,Integer> ");
                sbGet.append("Map<String,Integer> get");
                break;
            case "[string,long]":
                sb.append("Map<String,Long> ");
                sbGet.append("Map<String,Long> get");
                break;
            case "[string,float]":
                sb.append("Map<String,Float> ");
                sbGet.append("Map<String,Float> get");
                break;
            case "[string,double]":
                sb.append("Map<String,Double> ");
                sbGet.append("Map<String,Double> get");
                break;
            case "[string,boolean]":
                sb.append("Map<String,Boolean> ");
                sbGet.append("Map<String,Boolean> get");
                break;
            default: {
                sb.append("String ");
                sbGet.append("String get");
                break;
            }
            }
            sb.append(e.getKey());
            sb.append(";\r\n");
            sbGet.append(fName.substring(0, 1).toUpperCase());
            sbGet.append(fName.substring(1));
            sbGet.append("() {\r\n");
            sbGet.append("        return ");
            sbGet.append(fName);
            sbGet.append(";\r\n    }\r\n\r\n");
        }
        sb.append(sbGet);
        sb.append("}");
        try {
            Output output = new OutPutImpl(outputPath);
            output.outputFile(sb.toString(), name + "Entity.java", CodeTemplet.packageName + ".entitys");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void parse() {
        System.out.println("xml config code generating");
        System.out.println("xml resource path is: " + this.resourcePath);
        System.out.println("classes output path is: " + this.outputPath);
        System.out.println("-------------------------");
        File filePath = new File(resourcePath);
        File[] files = getConfigFiles(filePath);
        for (int i = 0; i < files.length; i++) {
            try (InputStream inputStream = new FileInputStream(files[i])) {
                String fileName = files[i].getName();
                SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
                saxParser.parse(inputStream, this);
                fileName = fileName.substring(0, fileName.lastIndexOf("."));
                if (onParseCompleted(fileName)) {
                    System.out.println(fileName + " classes generated");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static File[] getConfigFiles(File path) {
        File[] files = path.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                String fileName = file.getName();
                if (!fileName.matches("^.+\\.(xml)$")) {
                    return false;
                } else {
                    return true;
                }
            }
        });
        return files;
    }
}
