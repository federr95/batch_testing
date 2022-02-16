package com.example.uploadCSVtoH2.charset;

import org.mozilla.universalchardet.UniversalDetector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.charset.UnsupportedCharsetException;


@Configuration
public class CheckCharset {

    @Bean
    public String getCheckCharset(String fileName) throws IOException, UnsupportedCharsetException {
        String encoding;

        byte[] buf = new byte[4096];
        java.io.InputStream fis = java.nio.file.Files.newInputStream(java.nio.file.Paths
                .get(fileName));
        UniversalDetector detector = new UniversalDetector();
        int nread;
        while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
            detector.handleData(buf, 0, nread);
        }
        detector.dataEnd();
        encoding = detector.getDetectedCharset();
        if(!encoding.equals("US-ASCII")) throw
            new UnsupportedCharsetException("charset " + encoding + " is not supported");
        System.out.println("charset detected      - " + encoding);
        return  encoding;
    }
}
