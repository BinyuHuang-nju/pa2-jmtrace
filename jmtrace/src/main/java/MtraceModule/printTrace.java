package MtraceModule;

public class printTrace {
    public static int READ = 0;
    public static int WRITE = 1;

    public static final String getInternalName
            = printTrace.class.getCanonicalName().replace(".","/");

    public static void printLog(int index,int rw,String name,Object owner,String arrayType){
        printRW(rw);
        printThread();
        printIdentity(owner);
        printVal(index,name,arrayType);
    }

    private static void printRW(int rw){
        if(rw == printTrace.READ)
            System.out.print("R ");
        else
            System.out.print("W ");
    }
    private static void printThread(){
        System.out.printf("%d ",Thread.currentThread().getId());
    }
    private static void printIdentity(Object object){
        System.out.printf("%016x ",System.identityHashCode(object));
    }
    private static void printVal(int index,String name,String arrayType){
        if(index==-1){
            System.out.println(name.replace("/", "."));
            return;
        }
        String arr = "long";
        if(arrayType=="I")
            arr = "int";
        else if(arrayType=="B")
            arr = "byte";
        else if(arrayType=="C")
            arr = "char";
        else if(arrayType=="D")
            arr = "double";
        else if(arrayType=="F")
            arr = "float";
        else if(arrayType=="J")
            arr = "long";
        else if(arrayType=="S")
            arr = "short";
        else if(arrayType=="Z")
            arr = "boolean";
        else if(arrayType=="V")
            arr = "void";
        System.out.println(arr+"["+index+"]");
    }
}
