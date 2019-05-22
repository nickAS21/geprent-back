package com.georent.s3;

import java.io.File;
import java.io.FileInputStream;
import java.util.Base64;

public class Base64Helper {

    public  String encodePictureToBase64(File file) throws Exception{
        FileInputStream fileInputStreamReader = new FileInputStream(file);
        byte[] bytes = new byte[(int)file.length()];
        fileInputStreamReader.read(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }
}
