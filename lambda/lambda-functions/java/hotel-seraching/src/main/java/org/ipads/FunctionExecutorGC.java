package org.ipads;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.gson.*;


import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class FunctionExecutorGC implements RequestStreamHandler {
    private int cnt = 0;


    private Method mainMethod = null;


    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException{
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(output, Charset.forName("US-ASCII"))));
        try {
            JsonParser parser = new JsonParser();
            JsonObject inputObj = parser.parse(reader).getAsJsonObject();
            String mainClassName = inputObj.get("mainClass").getAsString();
            JsonObject realArgs = inputObj.get("args").getAsJsonObject();
            System.out.println(mainClassName);
            System.out.println(realArgs);

            if (mainMethod == null) {
                Class mainClass = this.getClass().getClassLoader().loadClass(mainClassName);
                mainMethod = mainClass.getMethod("main", new Class[] { JsonObject.class });
                mainMethod.setAccessible(true);
            }
            JsonObject ret = (JsonObject)mainMethod.invoke(null, realArgs);

            cnt += 1;
            String pid = ManagementFactory.getRuntimeMXBean().getName();
            pid = pid.substring(0, pid.indexOf('@'));
            System.gc();
            String pmapResult = getPmap(pid);
            writer.write(ret + "\n" + pmapResult+"\t" + String.valueOf(cnt) + "\n");
//        context.getLogger().log("name: " + ManagementFactory.getRuntimeMXBean().getName());
//        context.getLogger().log("cnt: " + String.valueOf(cnt)  + "\n");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            reader.close();
            writer.close();
        }
    }

    static void printExec(String[] commands) {
        ArrayList<String> ret = exec(commands);
        for (int i = 0; i < ret.size(); i++) {
            System.out.println(ret.get(i));
        }
    }

    static String getPmap(String pid) {
        ArrayList<String> ret = exec(new String[] {"cat", "/proc/" + pid + "/smaps"});
        int i = 0;
        String curRange = "";
        int totalPrivateDirty = 0;
        int totalPrivateClean = 0;
        int libPrivateClean = 0;
        boolean hasFileMapping = false;
        while (i < ret.size()) {
            String curLine = ret.get(i);
            if (curLine.indexOf("Size:") == 0) {
                curRange = ret.get(i-1);
                if (curRange.indexOf('/') != -1) {
                    hasFileMapping = true;
                } else {
                    hasFileMapping = false;
                }
            }
            if (curLine.indexOf("Private_Clean:") == 0) {
                int beg = curLine.indexOf(": ") + 2;
                int end = curLine.lastIndexOf(' ');
//                System.out.printf("%s [%d: %d] = %s\n", curLine, beg, end, curLine.substring(beg, end));
                curLine = curLine.substring(beg, end);
                curLine = curLine.substring(curLine.lastIndexOf(' ') + 1);
                int parsedSize = Integer.parseInt(curLine);
                totalPrivateClean += parsedSize;
                if (hasFileMapping) {
                    libPrivateClean += parsedSize;
                }
            }

            if (curLine.indexOf("Private_Dirty:") == 0) {
                int beg = curLine.indexOf(": ") + 2;
                int end = curLine.lastIndexOf(' ');
                curLine = curLine.substring(beg, end);
                curLine = curLine.substring(curLine.lastIndexOf(' ') + 1);
                int parsedSize = Integer.parseInt(curLine);
                totalPrivateDirty += parsedSize;
            }


            i += 1;
        }
        return String.format("[pmap result]totalPD:\t%d\ttotalPC:\t%d\t,libPC:\t%d", totalPrivateDirty, totalPrivateClean, libPrivateClean);
    }

    static ArrayList<String> exec(String[] commands) {
        ArrayList <String> ret = new ArrayList<>();
        try {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(commands);

            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(proc.getInputStream()));

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(proc.getErrorStream()));

            String s = null;
            while ((s = stdInput.readLine()) != null) {
                ret.add(s);
            }

            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }
}

