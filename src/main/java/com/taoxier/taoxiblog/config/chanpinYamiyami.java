package com.taoxier.taoxiblog.config;

import java.util.Scanner;

/**
 * @Description ：
 * @Author taoxier
 * @Date 2025/5/15
 */
public class chanpinYamiyami {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("请输入姓名：");
        String name = scanner.nextLine();

        System.out.print("请输入食物：");
        String food = scanner.nextLine();

        System.out.printf("我好饿，%s想吃%s！\n", name, food);

    }
}
