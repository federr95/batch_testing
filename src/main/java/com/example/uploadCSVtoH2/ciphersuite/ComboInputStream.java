package com.example.uploadCSVtoH2.ciphersuite;

import java.io.IOException;
import java.io.InputStream;

public class ComboInputStream extends InputStream {
   private boolean headDone;
   private InputStream head;
   private InputStream tail;

   public ComboInputStream(InputStream head, InputStream tail) {
       this.head = head != null ? head : tail;
       this.tail = tail != null ? tail : head;
   }

   public int read() throws IOException {
       int c;
       if (this.headDone) {
           c = this.tail.read();
       } else {
           c = this.head.read();
           if (c == -1) {
               this.headDone = true;
               c = this.tail.read();
           }
       }

       return c;
   }

   public int available() throws IOException {
       return this.tail.available() + this.head.available();
   }

   public void close() throws IOException {
       try {
           this.head.close();
       } finally {
           if (this.head != this.tail) {
               this.tail.close();
           }

       }

   }

   public int read(byte[] b, int off, int len) throws IOException {
       int c;
       if (this.headDone) {
           c = this.tail.read(b, off, len);
       } else {
               c = this.head.read(b, off, len);
               if (c == -1) {
                   this.headDone = true;
                   c = this.tail.read(b, off, len);
               }
       }

       return c;
   }
}

