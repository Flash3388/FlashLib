#!/bin/bash
echo "Starting FLASHBoard!"
java -Djava.library.path=flashboard_lib/linux -cp flashboard.jar edu.flash3388.flashlib.dashboard.Dashboard

