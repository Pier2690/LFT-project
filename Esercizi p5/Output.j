.class public Output 
.super java/lang/Object

.method public <init>()V
 aload_0
 invokenonvirtual java/lang/Object/<init>()V
 return
.end method

.method public static print(I)V
 .limit stack 2
 getstatic java/lang/System/out Ljava/io/PrintStream;
 iload_0 
 invokestatic java/lang/Integer/toString(I)Ljava/lang/String;
 invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
 return
.end method

.method public static read()I
 .limit stack 3
 new java/util/Scanner
 dup
 getstatic java/lang/System/in Ljava/io/InputStream;
 invokespecial java/util/Scanner/<init>(Ljava/io/InputStream;)V
 invokevirtual java/util/Scanner/next()Ljava/lang/String;
 invokestatic java/lang/Integer.parseInt(Ljava/lang/String;)I
 ireturn
.end method

.method public static run()V
 .limit stack 1024
 .limit locals 256
 ldc 10
 dup
 istore 0
 istore 1
 goto L1
L1:
 iload 0
 invokestatic Output/print(I)V
 iload 1
 invokestatic Output/print(I)V
 goto L2
L2:
 invokestatic Output/read()I
 istore 2
 invokestatic Output/read()I
 istore 3
 goto L3
L3:
 ldc 1
 invokestatic Output/print(I)V
 ldc 2
 ldc 3
 ldc 4
 iadd 
 iadd 
 invokestatic Output/print(I)V
 goto L4
L4:
 iload 2
 iload 3
 if_icmpgt L6
 goto L7
L6:
 iload 2
 invokestatic Output/print(I)V
 goto L5
L7:
 iload 2
 iload 3
 if_icmpeq L8
 goto L9
L8:
 iload 2
 invokestatic Output/print(I)V
 iload 3
 invokestatic Output/print(I)V
 goto L5
L9:
 iload 3
 invokestatic Output/print(I)V
 goto L5
L5:
 goto L10
L10:
 iload 2
 ldc 0
 if_icmpgt L11
 goto L12
L11:
 iload 2
 ldc 1
 isub 
 istore 2
 goto L13
L13:
 iload 2
 invokestatic Output/print(I)V
 goto L10
L12:
 goto L14
L14:
 goto L0
L0:
 return
.end method

.method public static main([Ljava/lang/String;)V
 invokestatic Output/run()V
 return
.end method

