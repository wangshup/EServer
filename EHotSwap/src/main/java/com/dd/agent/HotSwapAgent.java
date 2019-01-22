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
import java.util.*;

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
     * 参考 1.
     * http://xiaohuishu.net/2015/07/26/%E6%8E%A2%E7%B4%A2Java%E7%83%AD%E9%83%A8
     * %E7%BD%B2/ 2.
     * http://zeroturnaround.com/rebellabs/reloading-objects-classes-
     * classloaders/ 3. http://linmingren.me/blog/2013/02/动态替换目标进程的java类/ 4.
     * http://www.javabeat.net/introduction-to-java-agents/?cm_mc_uid=
     * 69969826849814482528628&cm_mc_sid_50200000=1448263170 5.
     * http://jboss-javassist.github.io/javassist/tutorial/tutorial.html 6.
     * http://download.forge.objectweb.org/asm/asm4-guide.pdf
     *
     * @param args
     * @param inst
     * @throws Exception
     */
    public static void agentmain(String args, Instrumentation inst) throws Exception {
        logger.info(
                "[hot swap] begin, agentmain method invoked with args: {} and inst: {}, RedefineClasses: {} and RetransformClasses: {}",
                args, inst, inst.isRedefineClassesSupported(), inst.isRetransformClassesSupported());
        Map<String, File> classNamePathMap = getFullClassNameFiles();
        if (classNamePathMap.isEmpty()) {
            logger.info("[hot swap] no patch files, finish.");
            return;
        }

        Class<?>[] allLoadedClasses = inst.getAllLoadedClasses();
        List<Class<?>> transformClasses = new ArrayList<>();

        List<ClassLoader> loaders = new ArrayList<>();
        try {
            for (Class<?> clazz : allLoadedClasses) {
                String simpleName = clazz.getName();
                if (simpleName == null || simpleName.isEmpty()) {
                    continue;
                }
                if ("com.dd.game.core.GameExtension".equals(simpleName)) {
                    ClassLoader classLoader = clazz.getClassLoader();
                    loaders.add(classLoader);
                    logger.info("[hot swap] find Game Extension ClassLoader {}", classLoader);
                }
            }
        } catch (Throwable e1) {
        }

        Map<String, String> map = new HashMap<>();
        if (!loaders.isEmpty()) {
            // 保证被替换的class都已经被加载
            for (String className : classNamePathMap.keySet()) {
                try {
                    for (ClassLoader loader : loaders) {
                        Class<?> clazz = loader.loadClass(className);
                        transformClasses.add(clazz);
                        logger.info("[hot swap] {} loaded.", clazz, loader);
                    }
                } catch (ClassNotFoundException e) {
                    map.put(className, className);
                }
            }
            if (map.size() > 0) {// 尝试补漏
                for (Class<?> loadedClass : allLoadedClasses) {
                    String loadedClassName = loadedClass.getName();
                    if (map.containsKey(loadedClassName)) {
                        transformClasses.add(loadedClass);
                        logger.info("[hot swap] {} found.", loadedClass);
                    }
                }
            }
        } else {
            for (Class<?> loadedClass : allLoadedClasses) {
                String loadedClassName = loadedClass.getName();
                if (classNamePathMap.containsKey(loadedClassName)) {
                    transformClasses.add(loadedClass);
                    logger.info("[hot swap] {} found.", loadedClass);
                }
            }
        }

        if (transformClasses.isEmpty()) {
            logger.info("[hot swap] cant find classes in inst, finish.");
            return;
        }


        ClassTransform classTransformer = new ClassTransform(classNamePathMap);
        try {
            inst.addTransformer(classTransformer, true);
            inst.retransformClasses(transformClasses.toArray(new Class[]{}));
        } catch (Throwable t) {
            logger.error("[hot swap] re transform classes error, classes: " + classNamePathMap.keySet().toString(), t);
        } finally {
            inst.removeTransformer(classTransformer);
        }
        logger.info("[hot swap] finish");
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
    public static Map<String, File> getFullClassNameFiles() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String strDate = sdf.format(new Date());
        Map<String, File> map = new HashMap<>();
        Collection<File> patchClassFiles = FileUtils.listFiles(new File(PATCH_DIR), new String[]{"class"}, false);
        for (File file : patchClassFiles) {
            String fullClassName = getFullClassName(file);
            if (fullClassName != null && fullClassName.startsWith(GAME_PACKAGES)) {
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

    public static Instrumentation getInst() {
        return inst;
    }
}
