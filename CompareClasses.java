import java.util.*;
import java.util.zip.*;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

class CompareClasses {
    static String a(Object o) {
        if (o instanceof AnnotationNode n) return n.desc + ":" + a(n.values);
        if (o instanceof Object[] xs) return a(Arrays.asList(xs));
        if (o instanceof List<?> xs) return xs.stream().map(CompareClasses::a).toList().toString();
        return String.valueOf(o);
    }
    static TreeMap<String,String> signatures(ClassNode c) {
        var out = new TreeMap<String,String>();
        out.put("class", c.version + " / " + c.access + " / " + c.superName + " / " + c.interfaces + " / " + c.signature + " / " + a(c.visibleAnnotations) + " / " + a(c.invisibleAnnotations));
        for(var f:c.fields) if ((f.access & Opcodes.ACC_SYNTHETIC)==0) out.put("field " + f.name + " " + f.desc, f.access + " / " + f.signature + " / " + f.value + " / " + a(f.visibleAnnotations) + " / " + a(f.invisibleAnnotations));
        for(var m:c.methods) if ((m.access & Opcodes.ACC_SYNTHETIC)==0) out.put("method " + m.name + " " + m.desc, m.access + " / " + m.signature + " / " + m.exceptions + " / " + a(m.visibleAnnotations) + " / " + a(m.invisibleAnnotations));
        return out;
    }
    static ClassNode read(ZipFile z, String name) throws Exception {
        var n = new ClassNode();
        try(var stream = z.getInputStream(z.getEntry(name))) {
            new ClassReader(stream).accept(n, ClassReader.SKIP_DEBUG|ClassReader.SKIP_CODE|ClassReader.SKIP_FRAMES);
        }
        return n;
    }
    public static void main(String[] args) throws Exception {
        int differences=0, classes=0;
        try(var z = new ZipFile(args[0]); var reconstructed = new ZipFile(args[1])) {
            for(var e:z.stream().filter(f->f.getName().endsWith(".class")).toList()) {
                String name=e.getName(); classes++;
                var old=signatures(read(z,name)); var now=signatures(read(reconstructed,name));
                var keys=new TreeSet<>(old.keySet()); keys.addAll(now.keySet());
                for(var k:keys) if(!Objects.equals(old.get(k),now.get(k))) {
                    differences++;
                    System.out.println(name+" "+k+"\n  old: "+old.get(k)+"\n  new: "+now.get(k));
                }
            }
        }
        System.out.println("Classes: " + classes + "; non-synthetic signature differences: " + differences);
        if (differences != 0) System.exit(1);
    }
}
