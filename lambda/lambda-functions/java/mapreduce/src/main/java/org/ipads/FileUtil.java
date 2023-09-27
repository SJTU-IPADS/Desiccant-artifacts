package org.ipads;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtil {
    public static void Split(String filePath, int numberOfPieces) throws IOException {
        long time = System.nanoTime();
        BufferedInputStream input;
        input = new BufferedInputStream(new FileInputStream(
                new File("/tmp/" + filePath)));

        try {
            System.out.println("File size: " + input.available() + " bytes");
        } catch (IOException e) {
            e.printStackTrace();
        }
        long fileSize = input.available();
        int splitFileSize = (int) Math.ceil(1.0 * fileSize / numberOfPieces);

        for (int i = 1; i <= numberOfPieces; i++) {
            BufferedOutputStream output = new BufferedOutputStream(
                    new FileOutputStream(new File("/tmp/" + filePath + "-" + i)));
            int value;
            int count = 0;
            // What is wrong if these two conditions are placed in a different order?
            while (count++ < splitFileSize && (value = input.read()) != -1) {
                output.write(value);
            }
            output.close();
        }
        input.close();
        System.out.println("split file cost " + (System.nanoTime() - time));
    }

}
