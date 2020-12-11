package MtraceModule;


import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class ASMMethodChange extends MethodVisitor{
    private MethodVisitor mv;

    public ASMMethodChange(MethodVisitor mv){
        super(Opcodes.ASM9,mv);
        this.mv = mv;

    }
    // need to record: getstatic/putstatic/getfield/putfield/*aload/*astore


    private boolean STATIC_STATE = false;
    private String current_owner;
    private String arrayType;

    private static final String logOrder="(IILjava/lang/String;Ljava/lang/Object;Ljava/lang/String;)V";
    @Override
    public void visitInsn(int opcode){
        // need to accomplish : *aload *astore
        switch(opcode){
            case Opcodes.AALOAD:
            case Opcodes.BALOAD:
            case Opcodes.CALOAD:
            case Opcodes.DALOAD:
            case Opcodes.FALOAD:
            case Opcodes.IALOAD:
            case Opcodes.LALOAD:
            case Opcodes.SALOAD:
                visitTypeALOAD(opcode); break;
            case Opcodes.AASTORE:
            case Opcodes.BASTORE:
            case Opcodes.CASTORE:
            case Opcodes.FASTORE:
            case Opcodes.IASTORE:
            case Opcodes.SASTORE:
                visitTypeASTORE(opcode);break;
            case Opcodes.LASTORE:
            case Opcodes.DASTORE:
                visitLongbyteTypeASTORE(opcode);break;
            default: break;
        }

        mv.visitInsn(opcode);
    }
    /*
    @Override
    public void visitFieldInsn(int opcode,String owner,String name,String desc){
        // need to accomplish : getstatic putstatic getfield putfield getfield
        if (name.startsWith("java")||name.startsWith("jdk")) {
            mv.visitFieldInsn(opcode, owner, name, desc);
            return;
        }
        switch (opcode){
            case Opcodes.GETSTATIC:
            case Opcodes.PUTSTATIC:
                visitTypeSTATIC(opcode,owner,name,desc); break;
            case Opcodes.GETFIELD:
            case Opcodes.PUTFIELD:
                visitTypeFIELD(opcode,owner,name,desc); break;
            default: break;

        }
        mv.visitFieldInsn(opcode, owner, name, desc);
    }*/

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
        if (owner.startsWith("java") || owner.startsWith("sun")) {
            mv.visitFieldInsn(opcode, owner, name, desc);
            return;
        }
        // System.out.println("opcode: " + opcode + "\towner: " + owner + "\tname: " + name + "\tdesc: " + desc);
        switch (opcode) {

            case Opcodes.GETSTATIC:
            case Opcodes.PUTSTATIC:
            case Opcodes.GETFIELD:
            case Opcodes.PUTFIELD:
                if (desc.startsWith("[")) {
                    STATIC_STATE = opcode != Opcodes.GETFIELD && opcode != Opcodes.PUTFIELD;
                    current_owner = owner;
                    arrayType = desc.substring(1).replace("/", ".");
                }
                mv.visitInsn(Opcodes.ICONST_M1);
                if (opcode == Opcodes.GETFIELD || opcode == Opcodes.GETSTATIC)
                    mv.visitInsn(Opcodes.ICONST_0);
                else
                    mv.visitInsn(Opcodes.ICONST_1);
                mv.visitLdcInsn(owner + "." + name);
                if (opcode == Opcodes.GETFIELD || opcode == Opcodes.PUTFIELD)
                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                else
                    mv.visitLdcInsn(owner);


                mv.visitLdcInsn("arrayType");
                mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                        Type.getInternalName(printTrace.class),
                        "printLog",
                        logOrder,
                        false);
                break;
        }
        mv.visitFieldInsn(opcode, owner, name, desc);
    }

    private void visitTypeALOAD(int opcode){
        // ..., arrayref, index -> ..., value
        mv.visitInsn(Opcodes.DUP);   // index
        mv.visitInsn(Opcodes.ICONST_0); // rw = read
        mv.visitLdcInsn("name"); // name
        if(STATIC_STATE)
            mv.visitLdcInsn(current_owner); // owner
        else
            mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitLdcInsn(arrayType);  // arrayType
        // ..., index, index, rw, name, owner, arrayType
        mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                printTrace.getInternalName,
                "printLog",
                logOrder,
                false
        );
    }

    private void visitTypeASTORE(int opcode){
        // ..., arrayref, index, value -> ...
        mv.visitInsn(Opcodes.DUP2);
        mv.visitInsn(Opcodes.POP);     // index
        // index, value , index
        mv.visitInsn(Opcodes.ICONST_1); // rw = write
        mv.visitLdcInsn("name"); // name
        if(STATIC_STATE)
            mv.visitLdcInsn(current_owner);    // owner
        else
            mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitLdcInsn(arrayType);

        mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                printTrace.getInternalName,
                "printLog",
                logOrder,
                false);
    }

    private void visitLongbyteTypeASTORE(int opcode){
        mv.visitInsn(Opcodes.DUP2_X2);
        mv.visitInsn(Opcodes.POP2);
        mv.visitInsn(Opcodes.DUP);
        mv.visitInsn(Opcodes.ICONST_1); // rw = write
        mv.visitLdcInsn("name"); // name
        if(STATIC_STATE)
            mv.visitLdcInsn(current_owner);    // owner
        else
            mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitLdcInsn(arrayType);

        mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                printTrace.getInternalName,
                "printLog",
                logOrder,
                false);
        mv.visitInsn(Opcodes.DUP2_X2);
        mv.visitInsn(Opcodes.POP2);
    }

    private void visitTypeFIELD(int opcode,String owner,String name,String descriptor){
        if(descriptor.startsWith("[")){
            STATIC_STATE = false;
            current_owner = owner;
            arrayType = descriptor.substring(1).replace("/", ".");
        }
        mv.visitInsn(Opcodes.ICONST_M1); // index
        if(opcode == Opcodes.GETFIELD)
            mv.visitInsn(Opcodes.ICONST_0);  // rw = read
        else
            mv.visitInsn(Opcodes.ICONST_1);  // rw = write
        mv.visitLdcInsn(owner+"."+name);  // name
        mv.visitVarInsn(Opcodes.ALOAD, 0); //  owner
        mv.visitLdcInsn("arrayType");   // arrayType

        mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                printTrace.getInternalName,
                "printLog",
                logOrder,
                false);
    }

    private void visitTypeSTATIC(int opcode,String owner,String name,String descriptor){
        if(descriptor.startsWith("[")){
            STATIC_STATE = true;
            current_owner = owner;
            arrayType = descriptor.substring(1).replace("/", ".");
        }
        mv.visitInsn(Opcodes.ICONST_M1); // index
        if(opcode == Opcodes.GETSTATIC)
            mv.visitInsn(Opcodes.ICONST_0);  // rw = read
        else
            mv.visitInsn(Opcodes.ICONST_1);  // rw = write
        mv.visitLdcInsn(owner+"."+name);  // name
        mv.visitLdcInsn(owner); // owner
        mv.visitLdcInsn("arrayType");   // arrayType

        mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                printTrace.getInternalName,
                "printLog",
                logOrder,
                false);
    }
}
