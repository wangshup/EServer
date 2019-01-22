package com.dd.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.ProtectionDomain;
import java.util.Map;

public class ClassTransform implements ClassFileTransformer {
    private static final Logger logger = LoggerFactory.getLogger(ClassTransform.class);
    private Map<String, File> classFiles;

    public ClassTransform(Map<String, File> classFiles) {
        this.classFiles = classFiles;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        logger.info("[hot swap] transform class name: {}, class loader: {}", className, loader);
        String fullClassName = className.replace("/", ".");
        File file = classFiles.get(fullClassName);
        if (file == null) {
            return classfileBuffer;
        }
        try {
            byte[] classContents = Files.readAllBytes(Paths.get(file.getPath()));
            logger.info("[hot swap] successfully redefine class {} md5 {}", fullClassName, MD5.getInstance().getHash(classContents));
            return classContents;
        } catch (IOException e) {
            logger.error("[hot swap] error in reading class file, file path : " + file.getPath(), e);
            return classfileBuffer;
        }
    }
}
