################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
CPP_SRCS += \
../src/Analog.cpp \
../src/DIO.cpp \
../src/HAL.cpp \
../src/PWM.cpp 

OBJS += \
./src/Analog.o \
./src/DIO.o \
./src/HAL.o \
./src/PWM.o 

CPP_DEPS += \
./src/Analog.d \
./src/DIO.d \
./src/HAL.d \
./src/PWM.d 


# Each subdirectory must supply rules for building sources it contributes
src/%.o: ../src/%.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: GCC C++ Compiler'
	g++ -I"/root/git/FlashLib/hal/shared/include" -O0 -g3 -Wall -c -fmessage-length=0 -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@)" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


