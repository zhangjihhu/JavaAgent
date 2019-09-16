package com.hhu.demo1;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.AdviceAdapter;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

import static org.objectweb.asm.Opcodes.ASM7;


public class AgentMain {

	public static void premain(String agentArga, Instrumentation instrumentation) {
		System.out.println("premain");
		instrumentation.addTransformer(new MyClassFileTransformer(), true);
	}

	public static class MyClassFileTransformer implements ClassFileTransformer {
		@Override
		public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
								ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
			// 类的全限定名
			if (!"com/hhu/demo1/MyTest".equals(className)) {
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
			if (name.equals("<init>")) {
				return mv;
			}
			return new MyMethodVisitor(mv, access, name, descriptor);
		}
	}

	public static class MyMethodVisitor extends AdviceAdapter {
		protected MyMethodVisitor(MethodVisitor methodVisitor, int access, String name, String descriptor) {
			super(ASM7, methodVisitor, access, name, descriptor);
		}

		@Override
		protected void onMethodEnter() {
			mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
			mv.visitLdcInsn("<<<enter " + this.getName());
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
			super.onMethodEnter();
		}

		@Override
		protected void onMethodExit(int i) {
			super.onMethodExit(i);
			mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
			mv.visitLdcInsn(">>>exit " + this.getName());
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
		}
	}

}
