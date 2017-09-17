/*****************************************************************************/
/*  math_ld.h        v2.1.5                                                  */
/*                                                                           */
/* Copyright (c) 2014-2017 Texas Instruments Incorporated                    */
/* http://www.ti.com/                                                        */
/*                                                                           */
/*  Redistribution and  use in source  and binary forms, with  or without    */
/*  modification,  are permitted provided  that the  following conditions    */
/*  are met:                                                                 */
/*                                                                           */
/*     Redistributions  of source  code must  retain the  above copyright    */
/*     notice, this list of conditions and the following disclaimer.         */
/*                                                                           */
/*     Redistributions in binary form  must reproduce the above copyright    */
/*     notice, this  list of conditions  and the following  disclaimer in    */
/*     the  documentation  and/or   other  materials  provided  with  the    */
/*     distribution.                                                         */
/*                                                                           */
/*     Neither the  name of Texas Instruments Incorporated  nor the names    */
/*     of its  contributors may  be used to  endorse or  promote products    */
/*     derived  from   this  software  without   specific  prior  written    */
/*     permission.                                                           */
/*                                                                           */
/*  THIS SOFTWARE  IS PROVIDED BY THE COPYRIGHT  HOLDERS AND CONTRIBUTORS    */
/*  "AS IS"  AND ANY  EXPRESS OR IMPLIED  WARRANTIES, INCLUDING,  BUT NOT    */
/*  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR    */
/*  A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT    */
/*  OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,    */
/*  SPECIAL,  EXEMPLARY,  OR CONSEQUENTIAL  DAMAGES  (INCLUDING, BUT  NOT    */
/*  LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,    */
/*  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY    */
/*  THEORY OF  LIABILITY, WHETHER IN CONTRACT, STRICT  LIABILITY, OR TORT    */
/*  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE    */
/*  OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.     */
/*                                                                           */
/*****************************************************************************/

/*****************************************************************************/
/* LONG DOUBLE MATH OPERATIONS - CALL THRUS                                  */
/*   Long double call thru versions of C99 math operations.                  */
/*****************************************************************************/


#if defined(_MATH_LD_DEFINE)
/*****************************************************************************/
/* NON-INLINED DEFINITIONS                                                   */
/*   _MATH_LD_DEFINE is defined by math_ld.c to access these definitions.    */
/*   The macro is required to disambiguate between producing a declaration   */
/*   and definition when _INLINE is undef'ed.                                */
/*****************************************************************************/
#  define _LDBL_CALL_LDBL1(x,y) \
     long double (x)(long double a) {return(y)(a);}
#  define _LDBL_CALL_LDBL2(x,y) \
     long double (x)(long double a, long double b) {return(y)(a, b);}

#elif defined(_INLINE)
/*****************************************************************************/
/* INLINED DEFINITIONS                                                       */
/*****************************************************************************/
#  define _LDBL_CALL_LDBL1(x,y) \
     _IDEFN long double (x)(long double a) {return(y)(a);}
#  define _LDBL_CALL_LDBL2(x,y) \
     _IDEFN long double (x)(long double a, long double b) {return(y)(a, b);}

#else
/*****************************************************************************/
/* NON-INLINED DECLARATIONS                                                  */
/*****************************************************************************/
#  define _LDBL_CALL_LDBL1(x,y) \
     _CODE_ACCESS long double (x)(long double a);
#  define _LDBL_CALL_LDBL2(x,y) \
     _CODE_ACCESS long double (x)(long double a, long double b);
#endif


_LDBL_CALL_LDBL1(log1pl, log1p)
_LDBL_CALL_LDBL2(hypotl, hypot)
_LDBL_CALL_LDBL2(copysignl, copysign)


/*****************************************************************************/
/* Undefine to allow for multiple inclusion as declarations and definitions. */
/*****************************************************************************/
#undef _LDBL_CALL_LDBL1
#undef _LDBL_CALL_LDBL2
