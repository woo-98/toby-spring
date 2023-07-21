package com.springbook.learningtest.template;

import javax.sound.sampled.Line;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.Buffer;

public class Calculator {
   public Integer fileReadTemplate(String filePath, BufferedReaderCallback callback) throws IOException {
      BufferedReader br = null;
      try {
         br = new BufferedReader(new FileReader(filePath));
         int ret = callback.doSomethingWithReader(br);
         return ret;
      } catch (IOException e) {
         System.out.println(e);
         throw e;
      } finally {
         if (br != null) {
            try {
               br.close();

            } catch (IOException e) {
               System.out.println(e.getMessage());
            }
         }
      }
   }
   public Integer lineReadTemplate(String filepath, LineCallback callback, int intVal) throws IOException {
      BufferedReader br = null;
      try {
         br = new BufferedReader(new FileReader(filepath));
         Integer res = intVal;
         String line = null;
         while ((line = br.readLine()) != null) {
            res = callback.doSomethingWithLine(line, res);
         }
         return res;
      }
      catch (IOException e) {}
      finally {}
      return null;
   }
   public Integer calcSum(String filepath) throws IOException {
      LineCallback sumCallback = new LineCallback() {
         @Override
         public Integer doSomethingWithLine(String line, Integer value) {
            return value + Integer.valueOf(line);
         }
      };
      return lineReadTemplate(filepath, sumCallback, 0);
   }

   public Integer calcMultiply(String filepath) throws IOException {
      LineCallback sumCallback = new LineCallback() {
         @Override
         public Integer doSomethingWithLine(String line, Integer value) {
            return value * Integer.valueOf(line);
         }
      };
      return lineReadTemplate(filepath, sumCallback, 1);
   }
}
