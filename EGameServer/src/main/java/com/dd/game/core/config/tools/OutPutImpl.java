package com.dd.game.core.config.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;

public class OutPutImpl implements Output {

    private String outputPath;

    public OutPutImpl(String outputPath) {
        this.outputPath = outputPath;
    }

    public void outputFile(String data, String fileName) {
        outputFile(data, fileName, null);
    }

    public void outputFile(String data, String fileName, String strPackageName) {
        if (fileName == null)
            return;
        try (FileWriter fw = new FileWriter(getOutputPath(strPackageName) + fileName);
                BufferedWriter bw = new BufferedWriter(fw);) {
            makePackageDir(strPackageName);
            bw.write(data);            
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void makePackageDir(String strPackageName) {
        StringTokenizer st = new StringTokenizer(strPackageName, ".");
        StringBuilder sb = new StringBuilder();
        sb.append(outputPath);
        sb.append("\\");
        while (st.hasMoreElements()) {
            sb.append(st.nextElement());
            sb.append("\\");
            makeDir(new File(sb.toString()));
        }
    }

    private void makeDir(File f) {
        if (!f.exists())
            f.mkdir();       
    }

    private String getOutputPath(String strPackageName) {
        StringTokenizer st = new StringTokenizer(strPackageName, ".");
        StringBuilder sb = new StringBuilder();
        sb.append(outputPath);
        sb.append("\\");
        while (st.hasMoreElements()) {
            sb.append(st.nextElement());
            sb.append("\\");
        }
        return sb.toString();
    }
}
