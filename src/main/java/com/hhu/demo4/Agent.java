package com.hhu.demo4;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.AdviceAdapter;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.security.ProtectionDomain;

import static org.objectweb.asm.Opcodes.ASM7;

public class Agent {

	public static void agentmain(String agentArgs, Instrumentation instrumentation) throws UnmodifiableClassException {
		System.out.println("called agentmain");
		instrumentation.addTransformer(new MyClassFileTransformer(), true);
		Class[] classes = instrumentation.getAllLoadedClasses();
		for (Class clazz : classes) {
			if ("com.hhu.demo4.TargetClass".equals(clazz.getName())) {
				System.out.println("reloading: " + clazz.getName());
				instrumentation.retransformClasses(clazz);
				break;
			}
		}
	}

}

class MyClassFileTransformer implements ClassFileTransformer {

	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
		if (!"com/hhu/demo4/TargetClass".equals(className)) {
			return classfileBuffer;
		}
		ClassReader cr = new ClassReader(classfileBuffer);
		ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES);
		ClassVisitor cv = new MyClassVisitor(ASM7, cw);
		cr.accept(cv, ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG);
		return cw.toByteArray();
	}
}

class MyClassVisitor extends ClassVisitor {
	public MyClassVisitor(int api, ClassVisitor classVisitor) {
		super(api, classVisitor);
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
		MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
		if ("add".equals(name)) {
			return new MyMethodVisitor(ASM7, mv, access, name,descriptor);
		}
		return mv;
	}
}

class MyMethodVisitor extends AdviceAdapter {

	protected MyMethodVisitor(int api, MethodVisitor methodVisitor, int access, String name, String descriptor) {
		super(api, methodVisitor, access, name, descriptor);
	}

	@Override
	protected void onMethodEnter() {
		System.out.println("<<< enter method " + this.getName());
		mv.visitIntInsn(ILOAD, 0);
		mv.visitIntInsn(ILOAD, 1);
		mv.visitInsn(ISUB);
		mv.visitInsn(IRETURN);
	}

	@Override
	protected void onMethodExit(int opcode) {
		System.out.println(">>> exit method " + this.getName());
	}
}
