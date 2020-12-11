package AgentModule;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.UnmodifiableClassException;
import java.security.ProtectionDomain;

import MtraceModule.*;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;


public class setAgent {
    private static Instrumentation INST;
    public static void premain(String agentArgs, Instrumentation inst) {
        INST = inst;
        process();
    }

    private static void process() {
        INST.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader,
                                    String className,
                                    Class<?> clazz,
                                    ProtectionDomain protectionDomain,
                                    byte[] byteCode)
                    throws IllegalClassFormatException {
                ClassReader cr = new ClassReader(byteCode);
                ClassWriter cw = new ClassWriter(cr,ClassWriter.COMPUTE_MAXS);
                ClassVisitor cv = new ASMClassChange(cw);
                cr.accept(cv, 0);
                return cw.toByteArray();
            }
        }
        );
    }

}
