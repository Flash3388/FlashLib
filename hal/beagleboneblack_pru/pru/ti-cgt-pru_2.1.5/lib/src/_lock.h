/*****************************************************************************/
/*  _lock.h v2.1.5                                                           */
/*                                                                           */
/* Copyright (c) 2000-2017 Texas Instruments Incorporated                    */
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

#ifndef __LOCK_H
#define __LOCK_H

#include <linkage.h>

#ifdef __cplusplus
extern "C" {
#endif

#pragma diag_push

/* _nop(), _lock(), and _unlock() all accept zero or more optional arguments,
   which must remain as empty rather than (void) parameter lists to avoid
   breaking the API */
#pragma CHECK_MISRA("-16.5")

_CODE_ACCESS void _nop();

extern _DATA_ACCESS void (  *_lock)();
extern _DATA_ACCESS void (*_unlock)();

_CODE_ACCESS void _register_lock  (void (  *lock)());
_CODE_ACCESS void _register_unlock(void (*unlock)());

#pragma diag_pop

#define SYSTEM_WIDE_LOCK_REGISTERED	(_lock != _nop)
#define SYSTEM_WIDE_UNLOCK_REGISTERED	(_unlock != _nop)

#ifdef __cplusplus
} /* extern "C" */
#endif

#endif /* __LOCK_H */
