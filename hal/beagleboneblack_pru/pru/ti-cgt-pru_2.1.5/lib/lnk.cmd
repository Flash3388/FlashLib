/****************************************************************************/
/* LNK.CMD                                                                  */
/*                                                                          */
/*   Usage:  clpru  <src files...> -z -o <out file> -m <map file> lnk.cmd   */
/*                                                                          */
/*   Description: This file is a sample command file that can be used       */
/*                for linking programs built with the PRU C                 */
/*                Compiler.   Use it as a guideline; you may want to change */
/*                the allocation scheme according to the size of your       */
/*                program and the memory layout of your target system.      */
/*                                                                          */
/*   Notes: (1)   You must specify the directory in which rts.lib is        */
/*                located.  Either add a "-i<directory>" line to this       */
/*                file, or use the system environment variable PRU_C_DIR to */
/*                specify a search path for libraries.                      */
/*                                                                          */
/*                                                                          */
/****************************************************************************/
-cr                                        /* LINK USING C CONVENTIONS      */
-stack  0x8000                             /* SOFTWARE STACK SIZE           */
-heap   0x8000                             /* HEAP AREA SIZE                */
/*--args 0x100  */

/* SPECIFY THE SYSTEM MEMORY MAP */

MEMORY
{
    PAGE 0:
       P_MEM    : org = 0x00000008   len = 0x0003FFF8

    PAGE 1:
       NEAR_MEM : org = 0x00000008   len = 0x0000FFF8
       FAR_MEM  : org = 0x00010000   len = 0x80000000
}

/* SPECIFY THE SECTIONS ALLOCATION INTO MEMORY */

SECTIONS
{
    .bss        : {} > NEAR_MEM, PAGE 1
    .data       : {} > NEAR_MEM, PAGE 1 palign=2
    .rodata     : {} > NEAR_MEM, PAGE 1

    .farbss     : {} > FAR_MEM, PAGE 1
    .fardata    : {} > FAR_MEM, PAGE 1
    .rofardata  : {} > FAR_MEM, PAGE 1

    /* In far memory for validation purposes */
    .sysmem     : {} > FAR_MEM, PAGE 1 
    .stack      : {} > FAR_MEM, PAGE 1
    .init_array : {} > FAR_MEM, PAGE 1
    .cinit      : {} > FAR_MEM, PAGE 1

    .args       : {} > NEAR_MEM, PAGE 1     

    .text       : {} > P_MEM, PAGE 0
}

