################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
CPP_SRCS += \
../src/hal/Analog.cpp \
../src/hal/DIO.cpp \
../src/hal/HAL.cpp \
../src/hal/PWM.cpp 

OBJS += \
./src/hal/Analog.o \
./src/hal/DIO.o \
./src/hal/HAL.o \
./src/hal/PWM.o 

CPP_DEPS += \
./src/hal/Analog.d \
./src/hal/DIO.d \
./src/hal/HAL.d \
./src/hal/PWM.d 


# Each subdirectory must supply rules for building sources it contributes
src/hal/%.o: ../src/hal/%.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: GCC C++ Compiler'
	g++ -std=c++0x -D__GXX_EXPERIMENTAL_CXX0X__ -I"/root/git/FlashLib/hal/shared/include" -O0 -g3 -Wall -c -fmessage-length=0 -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@)" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


