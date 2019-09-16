package com.hhu.demo2;

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

public class AgentMain {

	public static void agentmain(String agentArgs, Instrumentation instrumentation) throws UnmodifiableClassException {
		System.out.println("agentmain called");
		instrumentation.addTransformer(new MyClassFileTransformer(), true);
		Class[] classes = instrumentation.getAllLoadedClasses();
		for (int i = 0; i < classes.length; i++) {
			if (classes[i].getName().equals("MyTestMain")) {
				System.out.println("Reloading: " + classes[i].getName());
				instrumentation.retransformClasses(classes[i]);
				break;
			}
		}
	}

	public static class MyClassFileTransformer implements ClassFileTransformer {
		@Override
		public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
			if (!"MyTestMain".equals(className)) {
				return classfileBuffer;
			}
			ClassReader cr = new ClassReader(classfileBuffer);
			ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES);
			ClassVisitor cv = new MyClassVisitor(ASM7, cw);
			cr.accept(cv, ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG);
			return cw.toByteArray();
		}
	}

	public static class MyClassVisitor extends ClassVisitor {
		public MyClassVisitor(int api, ClassVisitor classVisitor) {
			super(api, classVisitor);
		}

		@Override
		public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
			MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
			if ("foo".equals(name)) {
				return new MyMethodVisitor(ASM7, mv, access, name, descriptor);
			}

			return mv;
		}
	}

	public static class MyMethodVisitor extends AdviceAdapter {
		protected MyMethodVisitor(int api, MethodVisitor methodVisitor, int access, String name, String descriptor) {
			super(api, methodVisitor, access, name, descriptor);
		}

		@Override
		protected void onMethodEnter() {
			mv.visitIntInsn(BIPUSH, 50);
			mv.visitInsn(IRETURN);
		}
	}


}
