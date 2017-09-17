TI PRU C/C++ CODE GENERATION TOOLS
2.1.5
May 2017

===============================================================================
Contents:
1. New defect history files in 2.1.4
2. Features
3. Changes from previous releases
4. Example usage
5. Common options
6. Linking with an ARM executable
7. Assembly language
8. Calling Convention
9. Language extensions
10. Sections in the linker command file

===============================================================================
New defect history files in 2.1.4

As of the 2.1.4 release, the DefectHistory.txt file has been replaced with the
two files Open_defects.html and Closed_defects.html. These show the open and
closed defects for the 2.1.x compiler branch. For open bugs, a status of Open
or Accepted means that the bug has not been examined yet, whereas a status of
Planned means that an evaluation or fix is in progress.

===============================================================================
Features

1. Full support of C/C++.
2. Generates ELF relocatable object files and executables.        
3. Complete support of the PRU instruction sets (v0, v1, v2, v3).
4. Constant table accesses from C
5. Intrinsics for XFER instructions in C
6. Little and big endian modes

===============================================================================
Changes from 1.1.0B1

1. __delay_cycles intrinsic
2. Boot routine specialization to reduce code size
3. Generation of the LOOP instruction from C
4. Improved performance when accessing cregister symbols
5. Improved support for -o4
6. Improved disassembler output
7. Addition of near and far data qualifiers

===============================================================================
Changes from 1.0.0B1

1. Performance improvements
2. Big endian support
3. Ability to link with an ARM executable
4. New support for accessing the constant table registers from C

===============================================================================
Example Usage

If installed with the default options, the C compiler will get installed at 
below path
 C:\Program Files (x86)\Texas Instruments\PRU Code Generation Tools 1.1.0B1

The first thing you need to do is add the bin folder of the toolchain in your 
path. If working from a Dos command window, type:
set path=%path%;C:\Program Files (x86)\Texas Instruments\PRU Code Generation Tools 1.1.0B1

An example command line for compiling/linking your code for PRU0 of the AM335x:
clpru --silicon_version=3 -o1 main.c tests.c -z AM3359_PRU.cmd -o PRU_tests.out -m PRU_tests.map

If you have CCS 6.0 you should be able to load the executable using the CCS
loader. If the version of CCS you have does not support direct loading, please
follow the steps below.

The next step is to generate the data and program binary files for loading in 
CCS debugger:
hexpru bin.cmd PRU_tests.out
This generates 2 files: data.bin (containing the data sections) and 
text.bin (containing the .text sections).

The example AM3359_PRU.cmd and bin.cmd files are included in this directory
for reference.

In Code Composer Studio, connect to PRU0 of the AM3359.
In the Memory Browser, R-click -> Load memory. Select text.bin then click on 
Next. Select:
- Start address 0
- Memory page: Program_Memory
- Type-size: 32-bits 
Click on Finish. Code sections get loaded in the PRU program memory.
In the Memory Browser, R-click -> Load memory. Select data.bin then click on 
Next. Select: 
- Start address 0
- Memory page: Data_Memory
- Type-size: 32-bits 
Click on Finish. Data sections get loaded in the PRU data memory.
You are ready for code execution. Launch from PC = _c_int00 address 
Note: the address of labels in the linker generated map file are 8-bit 
addresses. So to get the real address to be used/observed in CCS debugger - 
32-bit -, you need to divide this address by 4.

===============================================================================
Common options

A list of all compiler options can be obtained with clpru --help. To get
detailed information about an option run clpru <option> --help.

--silicon_version=0,1,2,3
        Select the silcon version for the core. The default is 3.

-O, --opt_level=off,0,1,2,3
        Select the optimization level to use for compilation. If no option
        is specified, the default is no optimization. Specifying -O with no
        option is the same as specifying -O2.

--keep_asm
        Keeps the generated assembly language (.asm) file.

--c_src_interlist:
        Interlists the C source with the assembly.

===============================================================================
Linking with an ARM executable

The hexpru tool provides the capability for converting a PRU executable into
an ARM object file that can be linked in with an ARM project. The PRU 
code and data are converted to ARM data. The ARM application can reference
symbols in the PRU file. This is useful for bare metal projects that are
not running a high level operating system like Linux.

The files for this example are in the "example" directory.

> clpru -o3 test.c -z AM3359_PRU.cmd -o pru.out

The executable for this will expose the shared_buf and local_data symbols.

Now we need to convert the the executable to an ARM object file. This will
be done using the included PRU_to_ARM.cmd file.

> hexpru pru.out -o pru.obj PRU_to_ARM.cmd

Now we can link the pru.obj into our dummy ARM application:

> armcl arm.c pru0.obj -z dummy_ARM.cmd -o arm.out

===============================================================================
Assembler

-------------------------------------------------------------------------------
Instructions
-------------------------------------------------------------------------------
The syntax for instruction mnemonics is very similiar to the existing PASM
assembler. Exceptions are listed here:

1. MOV instruction
   MOV is only supported for register to register moves. The LDI instruction
   must be used to load a literal into a register. 

2. LDI instruction
   LDI can only be used to load a 16-bit constant into a register. To load a
   32-bit constant, you must use the LDI32 instruction.

3. MVI instruction
   MVI is only supported on core revisions 2 and 3. The existing PASM assembler
   supports the instruction in a limited form for v1 using pseudo operations.

4. ZERO instruction
   ZERO is only supported on v2 and v3 cores. For v1, the user should use
   LDI r0, 0.

5. LFC, SFC, and SCAN
   These instructions are not supported. If support is needed we can add them.

6. Operands with a '&' prefix
   The existing PASM assembler accepts operands with or without the & 
   symbol: LBBO &r0 or LBBO r0. The assembler in this release requires the
   & for these operands. 

-------------------------------------------------------------------------------
Directives
-------------------------------------------------------------------------------
The PRU assembler supports all standard directives in TI SDO assemblers.
Documentation can be found in the manuals for other architectures for now.
These will be completely documented in the PRU manuals once available. 
Here is a discussion of directives in the PASM assembler and how they map
to SDO assemblers.

1. .origin
   Not supported in the assembler. The assembler produces relocatable object
   files which start at address 0.

2. .entrypoint
   Not supported in the assembler. The entrypoint can be specified at linktime
   using the --entrypoint option. 

3. .setcallreg
   Not supported in the assembler. The default register R30.w0 is used by
   the RET and CALL instructions.

4. .macro, .mparam, and .endm
   Supported using the .macro and .endm directives.

5. .enter, .leave, .using
   Not supported in the assembler. The toolset supports separate compilation
   so namespaces are not as important in assembly. 

6. .struct and related directives
   Supported using the .struct directive. An example would be:

tag          .struct
field1       .int
field2       .short
field3       .char
length       .endstruct

   The label defined before the .endstruct directive will evaluate to the 
   size of the struct.

7. .assign
   The .assign functionality is supported through the .sassign directive.

   Syntax:

label   .sassign r0.b1, tag

 ADD   label.field1, label.field1, label.field2
 LBBO  &label, r0, 0, length

   The directive does checking to ensure that all field members can be accessed
   through the register file. The struct must be defined using directives with
   predefined types (.int, .short, .char, ...). The .field directive will cause
   an error. 

===============================================================================
C Calling Convention

This is the description of the calling convention for C callable routines.
It is subject to change in future releases.

Special registers:
   Link register: R3.w2
   Stack pointer: R2

Registers R14-R29 are used for passing by value. Values are packed as tightly
as possible into the registers. For instance:

 void foo(short a, int b, short c)
    a -> R14.w0
    b -> R15
    c -> R14.w2

Return values are returned through R14

Save on call registers are:
R0-R1, R14-R29

Save on entry registers are:
R3.w2-R13

===============================================================================
Data types

Type                            Size (in bits)
----                            --------------
(un)signed char                 8
plain char (unsigned char)      8
(un)signed short                16
(un)signed int                  32
(un)signed long                 32
(un)signed long long            64
float                           32
double                          64
long double                     64
data pointers                   32
code pointers                   16

All data types are byte aligned.

===============================================================================
C/C++ Language Extensions for PRU

-------------------------------------------------------------------------------
Intrinsics
-------------------------------------------------------------------------------
void __xin  (unsigned int device_id, unsigned int base_register, 
            unsigned int use_remapping, void& object)
void __xout (unsigned int device_id, unsigned int base_register, 
             unsigned int use_remapping, void& object)
void __xchg (unsigned int device_id, unsigned int base_register, 
             unsigned int use_remapping, void& object)
void __sxin (unsigned int device_id, unsigned int base_register, 
             unsigned int use_remapping, void& object)
void __sxout(unsigned int device_id, unsigned int base_register, 
             unsigned int use_remapping, void& object)
void __sxchg(unsigned int device_id, unsigned int base_register, 
             unsigned int use_remapping, void& object)

These intrinsics are used to generate the XFER instructions on the PRU. 
The parameters are defined as:

   device_id: Literal 0-255 correpsonding to the first parameter of the 
              assembly instruction.

   base_register: Literal 0-32 corresponding to the register that must be used 
                  as the base register for the transfer. 

   use_remapping: Boolean value (zero is false, non-zero is true) that 
                  specifies whether a register file shift amount is used
                  to move the registers to the appropriate base register.
                  An example of this are the bank shift supported for the 
                  scratchpad memory on ICSS.

   object:        Any object with a size less than 44 bytes. 

-------------------------------------------------------------------------------

unsigned int __lmbd(unsigned int src, unsigned int pattern)

The __lmbd intrinsic can be used to generate the LMBD instruction.
The LMBD instruction scans src for the bit pattern in position 0 of
pattern and returns the first position where it is found.

-------------------------------------------------------------------------------

void __halt()

The __halt intrinsic is used to generate the HALT instruction. This intrinsic
is a barrier.

-------------------------------------------------------------------------------

void __delay_cycles(unsigned int cycles)

The __delay_cycles intrinsic will delay CPU execution for the specified 
number of cycles. The number of cycles must be a constant. 

-------------------------------------------------------------------------------
Constant Table Registers
-------------------------------------------------------------------------------
The cregister mechanism supported in the 1.0.0B1 release is no longer supported.

The new mechanism is described here.

Support for using the constant registers is provided through the new cregister
and peripheral variable attributes. The syntax is:

int x __attribute__((cregister("MEM", [near|far]), peripheral));

The name "MEM" can be any name, although it will need to correspond to a
memory range name in your linker command file (described below). The
near or far parameter tells the compiler if it can use the immediate
addressing mode (near) or register indirect addressing mode (far). If the
data will be completely contained within the first 256 bytes from the top
of the constant register pointer then it should be near, otherwise it should
be far. If the parameter is omitted, near will be chosen. 

The peripheral attribute can only be used with the cregister attribute and has 
two effects. First, it puts the object in a section that will not be loaded
onto the device. This will prevent initialization of the peripheral at runtime.
Second, it allows the same object to be defined in multiple source files 
without a linker error. The intent is that peripherals can be completely 
described in a header file.

The linker command file must be modified for the cregister attribute to work,
otherwise you will get relocation errors. The updated linker command file
syntax is:

MEMORY
{
   MEM: o=xxx l=xxx CREGISTER=4
}

This tells the linker that register C4 points to the top of MEM. Note that the
name MEM in the linker command file must be the same name used in the cregister
attribute. The linker will automatically place all objects declared to
be in MEM into the memory range.

The assmebly code for a near load will look like:
LBCO &R0, __PRU_CREG_MEM, $CSBREL(x), 4

A far load will look like:
LDI R0.w0, $CSBREL(x)
LBCO &R0, __PRU_CREG_MEM, R0.w0, 4

The __PRU_CREG_MEM symbol will be defined at link time with the value of the
register to be used. The $CSBREL modifier will cause a relocation entry 
relative to the memory pointed to by the constant register.

-------------------------------------------------------------------------------
The near and far keywords
-------------------------------------------------------------------------------
PRU can load a 16-bit address into a register with a single instruction,
however pointers are 32 bits and load/store instructions can load from the
full 32-bit address space. In releases prior to 2.0.0B1 all data addresses
were assumed to be 32 bits so two instructions were used to load the address.

In the 2.0.0B1 release the near and far keywords were introduced to allow
more efficient loading of data. The near and far keywords can be applied to
any data symbol. The near keyword asserts that the symbol will be in the 
lower 16 bits of memory. The far keyword asserts that the symbol will not
be in the lower 16 bits of memory. 

By default all symbols are near. This is because the PRU local memory is
always in the lower 16 bits and most accesses to the upper memory range 
will be for peripheral accesses.

The __near and __far keywords are also accepted and are available even
when --strict_ansi is specified. These are guaranteed to not conflict with
user symbols named "near" and "far".

All symbols that will reside in the upper 16 bits of memory must be delcared
using far, even if they have the cregister attribute. A cregister symbol
can have a far qualifier and be a near cregister access. As an example:

__far int peripheral __attribute((cregister("PERIPH", near)));

This means that relative to the cregister access peripheral is a near
access, but if accessed using an absolute address it is far. This is
important because the compiler may choose to not access peripheral using
a cregister access.

-------------------------------------------------------------------------------
Structures in Registers
-------------------------------------------------------------------------------
The compiler has support to allocate aggregate types to registers. Today this
is limited to structs of 44 bytes or smaller. The compiler will automatically
allocate to registers when it is profitable. 

struct s
{
    int a;
    int b;
    char c;
    short d;
};

struct s global_struct;

void foo()
{
    struct s x;
    x = global_struct;
    x.a++;
    x.d++;
    global_struct = x; 
}

===============================================================================
Sections in the linker command file

Executable sections:

     .text       Executable code

User data sections:

     .bss        Uninitialized near data
     .data       Initialized near data
     .rodata     Constant read only near data
     .farbss     Uninitialized far data
     .fardata    Initialized far data
     .rofardata  Constant read only far data

Special data sections:

     .sysmem     Heap for dynamic memory allocation (size is controlled by -heap option)
     .stack      Stack space (size is controlled by -stack option)
     .init_array Table of constructors to be called at startup
     .cinit      Tables for initializing global data at runtime
     .args       Section for passing arguments in the argv array to main
     

All data sections should be allocated on page 1 and all executable sections should be
allocated on page 0
     
