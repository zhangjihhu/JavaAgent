package com.hhu.demo4;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

import java.io.IOException;
import java.util.Scanner;

public class AttachMain {

	public static void main(String[] args) throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {
		Scanner sc = new Scanner(System.in);
		System.out.println("请输入目标jvm pid: ");
		String pid = sc.nextLine();

		VirtualMachine vm = VirtualMachine.attach(pid);
		String agentPath = "F:\\Project\\java\\javaagent\\target\\my-javaagent.jar";
		vm.loadAgent(agentPath);
		vm.detach();

	}

}
