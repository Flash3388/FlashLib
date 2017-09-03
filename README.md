# FlashLib
FlashLib is a robotics software development library for Java, originaly designed to improve and help FRC teams, but can work on other non-FRC platforms. Although currently available only for Java, we aim to make FlashLib available for C++ as well.

The latest version of FlashLib is 1.0.2

- [Features](#features)
- [Dependencies](#dependencies)
- [Building](#building)
- [Code Of Conduct](#code-of-conduct)
- [Contributing](#contributing)

## Features

The library provides several different tools that can be both used for a robot development and for general softwares:
- Utilities Functions: Including logs, array utilities, constants handling and data structures.
- Mathmetical Structures and Functionalities: Vectors, Matrices, Complex, Interpolations, Integrals, Differentials, etc.
- Communication Managment: Management of communications between two sides, including camera data communications and general communications, Interfacing with any port or bus: Ip sockets, Serial ports, SPI or I2C buses and more.
- Image Processing: A dynamic vision system which require little to no knowledge about image processing. Can interface with any image processing library, but openCV interfacing is already built-in. 
- Human Interface Devices Interfacing: Remote control of robots using Human Interfacing Devices.
- Action Scheduler: An action scheduling system to allow simple managment of robot system functionalities.
- Algorithms: Built-In sensor-based motion algorithms and vision-based motion algorithms.
- Generic Drive Systems: Built-in generic drive systems, including: Tank drive, Mecanum drive and Omni-Directional drive.
- Flashboard: A robot operator's sophisticated dashboard with build-in image processing and several tools for working with linux computer platforms.
- HAL: An Hardware Abstraction Layer with several built-in implementations. (WIP)
- Sensor and Motor Controllers Interfacing: Interfaing with motor controllers and sensors for non-FRC platforms. (WIP)
- Control Station: Pre-Made control software for non-FRC robots. (WIP)

## Dependencies

FlashLib depends on several libraries for its functionalities:
- The openCV implementation of the vision system uses openCV
- The openCV implementation for camera interfacing uses openCV
- FRC robots using FlashLib require WPILib
- Flashboard requires openCV for its vision functionalities

All of those libraries are available in the "libs" folder.

### Versions

The versions used for dependencies by FlashLib are:
- OpenCV: 3.0.0
- WPILib: 2017.3.1

## Building

Building the library requires gradle 4.1. If you have it, run `gradle build`.
If you don't, run the gradle wrapper added to this project `gradlew build`.

You can find the binary files in the `build/libs` folder. Flashboard distribution folder is in `build/flashboard` ready to be
used. To run flashboard, run `run.bat` for windows or `run` for linux which are located in the distribution folder.

## Code Of Conduct

FlashLib is here to help the community in the development of robotics solutions, and thus supports an open and welcoming 
envirnoment for users and developers. We expect everyone to act with respect and according to our code of conduct.

Please see [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md) for information about our code of conduct.

## Contributing

As an open-source project we readily welcome anyone wanting to contribute. But in order to maintain organization we have a few
rules and guidelines for contributions.

Please see [CONTRIBUTING.md](CONTRIBUTING.md) if you are interested in contributing to this project.
