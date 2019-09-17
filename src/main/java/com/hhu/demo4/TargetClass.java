package com.hhu.demo4;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

public class TargetClass {

	public static void main(String[] args) throws InterruptedException {
		RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
		System.out.println(runtimeMXBean.getName());
		int a = 1;
		int b = 3;
		while (true) {
			System.out.println(add(a, b));
			Thread.sleep(5000);
		}
	}

	private static int add(int a, int b) {
		return a + b;
	}


}
