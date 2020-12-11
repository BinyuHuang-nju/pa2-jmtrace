package MtraceModule;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ASMClassChange extends ClassVisitor{

    private ClassVisitor cv;

    public ASMClassChange(ClassVisitor cv){
        super(Opcodes.ASM9, cv);
        this.cv = cv;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor,String signature,String[] exceptions){
        MethodVisitor mv = cv.visitMethod(access,name,descriptor,signature,exceptions);
        if(name.startsWith("sun/launcher")||name.startsWith("java")||name.startsWith("jdk"))
            return mv;
        mv = new ASMMethodChange(mv);
        return mv;
    }
    /*
    @Override
    public void visit(int version, int access, String name,String signature,String superName,String[] exceptions){

    }*/
}
