# FlashLib
FlashLib is a robotics software development library originaly designed to improve and help FRC teams, but can work on other platforms, specifically Raspberry PI and BeagleBone Black.
FlashLib is currently available for Java users only, but a C++ version is in planning.

FlashLib is still in a beta stage.


The library was moved here from the original repository in GitLab: https://gitlab.com/FLASH3388/FLASHLib

# Features

The library provides several different tools that can be both used for a robot development and for general softwares:
- Utilities Functions: Including a Log, byte array manipulations and data structures.
- Mathmetical Functions: Vectors, Matrices, Complex, Interpolations, Integrals, Differentials.
- Communication Managment: Management of communications between two sides, including camera data communications and general communications, Interfacing with any port or bus: Ip sockets, Serial ports, SPI or I2C busses and more.
- Image Processing: A dynamic vision system which require little to no knowledge about image processing. Can interface with any image processing library, but openCV interfacing is already built-in. 
- Camera interfacing using openCV.
- IO Interfacing: Available for RoboRio, Raspberry PI and BeagleBone Black platforms thanks to WPILib and Bulldog Libraries. (Planned)
- Sensor and Motor Controllers Interfacing: Interfaing with motor controllers and sensors for Raspberry PI and BeagleBone Black platforms.
- Human Interface Devices Interfacing: Remote control of robots using Human Interfacing Devices.
- Control Station: Pre-Made control software for robots using Raspberry PI and BeagleBone Black platforms.
- Action Scheduler: An action scheduling system to allow simple managment of robot system functionalities. Similar to the WPILib scheduling system but more sophisticated.
- Algorithms: Built-In sensor-based motion algorithms and vision-based motion-algorithms.
- Generic Drive Systems: Built-in generic drive systems, including: Tank drive, Mecanum drive and Omni-Directional drive.
- Flashboard: A robot operator's sophisticated dashboard with build-in image processing and several tools for working with linux computer platforms.

# Communication Mangement System
The communications manager takes a network connection through a single port and splits it into mini-networks. The amount of mini-networks possible is not defined, but the more there are the slower the manager.
A Sendable objects defines one end of the network and it has a type which is defined by the user. The type helps identify the type of data the will be sent and received through the mini-network.
A mini-network is created by attaching a Sendable object to the manager, which in turn sends information about that Sendable to the other end causing the creation of a Sendable object on that side to communicate with the attached Sendable. The Sendable object created depends on its type.

The manager can interface and communicate in any way the user wants, be it IP sockets or a Bus of some kind. 

# Action Scheduling System
For easier system and multi-tasking management in your robot code, flashlib introduces a simple scheduling system.
The system divides each robot system into a seperate class which allows for easy system tracking. Programmers can now run 
"Actions" on the robot systems, where only one action can run on a system at any given time. The Scheduler runs and manages the Actions and Systems, avoiding collisions between Actions.

This system is similar to the concept introduced by WPILib for FRC, but improves upon it.

# Dependencies
FlashLib depends on several libraries for its functionalities:
- Image processing and Camera Interfacing requires openCV 3.0.0
- Robots working with the RoboRio platform require WPILib
- Robots working with the Raspberry PI or BeagleBone Black platforms require Bulldog 0.3.0
- Flashboard requires openCV 3.0.0 and Jsch 0.1.54

All of those libraries are available in the "libs" folder of their respective projects.

# Building
Building the library requires gradle. 
To build flashlib, run the batch or bash script (bash for linux, batch for windows) located in the version you wish to use (flashlibj, flashlibc).
The script will run gradle and build the project into build->libs. Required libraries for building the library are located in the libs folder. Those libraries are required for several funcationslities of the library.

To build flashboard, make sure the latest version of flashlibj is located in the libs folder, where the rest of the required libraries are located. Run the build script to build and then run the prep script. The software will now be fully ready in build->flashboard.

To build all the project, run the buildall script in the repository.

# Disclaimer
The FlashLib uses the following libraries, but those libraries do not belong to us:
- WPILib: WPILib is a robotics library for FRC teams created by the Worcester Polytechnic Institute: https://github.com/wpilibsuite/allwpilib
- Bulldog: Bulldog is an IO library for Raspberry PI and BeagleBone Black created by Datenheld: https://github.com/Datenheld/Bulldog
- OpenCV: OpenCV is an open source computer vision library: https://github.com/opencv/opencv
- JSCH: Jsch is a remote protocols library: http://www.jcraft.com/jsch/
