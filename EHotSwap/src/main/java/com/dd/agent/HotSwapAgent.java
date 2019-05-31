package com.dd.agent;

import org.apache.commons.io.FileUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HotSwapAgent {
    private static final Logger logger = LoggerFactory.getLogger(HotSwapAgent.class);
    private static final String PATCH_DIR = "patches";
    private static final String GAME_PACKAGES = "com.dd";

    private static Instrumentation inst;

    public static void premain(String args, Instrumentation inst) {
        HotSwapAgent.inst = inst;
    }

    public static String reload(Class<?> cls, File file, int serverId) throws IOException, ClassNotFoundException, UnmodifiableClassException {
        if (inst == null) {
            return "该应用没有添加此特性, 请检查启动参数 javaagent";
        }
        byte[] code = loadBytesFromClassFile(file);
        ClassDefinition def = new ClassDefinition(cls, code);
        inst.redefineClasses(new ClassDefinition[]{def});
        return "<br>[hot swap v2] " + cls.getName() + " reloaded server " + serverId;
    }

    private static byte[] loadBytesFromClassFile(File classFile) throws IOException {
        byte[] buffer = new byte[(int) classFile.length()];
        try (FileInputStream fis = new FileInputStream(classFile); BufferedInputStream bis = new BufferedInputStream(fis)) {
            bis.read(buffer);
        } catch (IOException e) {
            throw e;
        }
        return buffer;
    }

    /**
     * this class loaded by Application Class Loader, can not load extension
     * classes, need extension class loader extends URLClassLoader public static
     * void agentmain(String agentArguments, Instrumentation instrumentation)
     * throws Exception { logger.info(
     * "[hot swap] agentmain method invoked with args: {} and inst: {}",
     * agentArguments, instrumentation); logger.info(
     * "[hot swap] RedefineClasses flag {}, RetransformClasses flag {}",
     * instrumentation.isRedefineClassesSupported(),
     * instrumentation.isRetransformClassesSupported()); Collection
     * <File> patchClassFiles = FileUtils.listFiles(new File(PATCH_DIR), new
     * String[]{"class"}, true); if (patchClassFiles.isEmpty()) { logger.info(
     * "[hot swap] no patch files"); return; } for (File patchFile :
     * patchClassFiles) { String path = patchFile.getPath(); String
     * fullClassName = getFullClassName(path); if
     * (!fullClassName.startsWith(GAME_PACKAGES)) continue; logger.info(
     * "[hot swap] ready redefine file {}, full class name {}", path,
     * fullClassName); byte[] classContents =
     * Files.readAllBytes(Paths.get(path)); ClassDefinition classDefinition =
     * new ClassDefinition(Class.forName(fullClassName), classContents);
     * instrumentation.redefineClasses(classDefinition); logger.info(
     * "[hot swap] finish redefine class {}", fullClassName); } }
     **/
    public static Map<String, File> getFullClassNameFiles(String packageName) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String strDate = sdf.format(new Date());
        Map<String, File> map = new HashMap<>();
        Collection<File> patchClassFiles = FileUtils.listFiles(new File(PATCH_DIR), new String[]{"class"}, false);
        for (File file : patchClassFiles) {
            String fullClassName = getFullClassName(file);
            if (fullClassName != null && fullClassName.startsWith(packageName)) {
                String dirPath = file.getParent() + File.separator + strDate;
                File dir = new File(dirPath);
                if (!dir.exists()) {
                    dir.mkdir();
                }
                File newFile = new File(dirPath + File.separator + file.getName());
                file.renameTo(newFile);
                map.put(fullClassName, newFile);
                logger.info("[hot swap] find class file {} in {}.", fullClassName, PATCH_DIR);
            }
        }
        return map;
    }

    private static String getFullClassName(File file) {
        try (InputStream is = new FileInputStream(file)) {
            ClassReader reader = new ClassReader(is);
            ClassNode node = new ClassNode();
            reader.accept(node, 0);
            String packageName = node.name.replace("/", ".");
            return packageName;
        } catch (IOException e) {
            logger.error("[hot swap] load file {} package name error", file.getPath());
            return null;
        }
    }
}
