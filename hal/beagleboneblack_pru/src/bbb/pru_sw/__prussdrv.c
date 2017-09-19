/*
 * __prussdrv.c
 *
 *  Created on: Aug 29, 2017
 *      Author: root
 */

#include "__prussdrv.h"

int __pruss_detect_hw_version(unsigned int *pruss_io)
{

    if (pruss_io[(AM18XX_INTC_PHYS_BASE - AM18XX_DATARAM0_PHYS_BASE) >> 2]
        == AM18XX_PRUSS_INTC_REV)
        return PRUSS_V1;
    else {
        if (pruss_io
            [(AM33XX_INTC_PHYS_BASE - AM33XX_DATARAM0_PHYS_BASE) >> 2] ==
            AM33XX_PRUSS_INTC_REV)
            return PRUSS_V2;
        else
            return -1;
    }
}

void __prussintc_set_cmr(volatile unsigned int *pruintc_io,
                         unsigned short sysevt, unsigned short channel)
{
    pruintc_io[(PRU_INTC_CMR1_REG + (sysevt & ~(0x3))) >> 2] |=
        ((channel & 0xF) << ((sysevt & 0x3) << 3));

}


void __prussintc_set_hmr(volatile unsigned int *pruintc_io,
                         unsigned short channel, unsigned short host)
{
    pruintc_io[(PRU_INTC_HMR1_REG + (channel & ~(0x3))) >> 2] =
        pruintc_io[(PRU_INTC_HMR1_REG +
                    (channel & ~(0x3))) >> 2] | (((host) & 0xF) <<
                                                 (((channel) & 0x3) << 3));

}
