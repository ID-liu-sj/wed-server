package com.webserver.test;


import java.io.*;
import java.util.Arrays;

/**
 * java.io.ByteArrayOutputStream和ByteArrayInputStream
 * 字节数组输出与输入流
 * 它们是一对低级流，内部维护一个字节数组。
 * <p>
 * ByteArrayOutputStream通过该流写出的数据都会保存在内部维护的字节数组中。
 */
public class BAOSDemo {
    public static void main(String[] args) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        OutputStreamWriter osw = new OutputStreamWriter(baos);
        BufferedWriter bw = new BufferedWriter(osw);
        PrintWriter pw = new PrintWriter(bw, true);
        /*
            ByteArrayOutputStream内部提供的方法:
            Byte[] toByteArray()
            该方法返回的字节数组包含这目前通过这个流写出的所有字节

            int size()
            返回的数字表示已经通过当前流写出了多少字节(在流
         */
        byte[] data = baos.toByteArray();

        System.out.println("内部数组长度是:" + data.length);
        System.out.println("内部数组内容是:" + Arrays.toString(data));
        pw.println("hello world");
        data = baos.toByteArray();
        System.out.println("内部数组长度是:" + data.length);
        System.out.println("内部数组内容是:" + Arrays.toString(data));
        System.out.println("内部缓冲大小:" + baos.size());
        pw.println("i love java");
        data = baos.toByteArray();
        System.out.println("内部数组长度是:" + data.length);
        System.out.println("内部数组内容是:" + Arrays.toString(data));

        pw.close();
    }
}
