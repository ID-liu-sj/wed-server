package com.webserver.core;

public class C1 {
        public static void live(String x) {
            System.out.println("您好" + x);
        }

        public void lives(String y){
            System.out.println("您好"+y);
        }
        public static void main(String[] args) {
            C1 z =null; //类引用指向null
            z.live("1"); //用null调用方法（仅仅限于静态方法）
            ((C1)null).live("1"); //或者使用（向上自动转换）
        }


}
