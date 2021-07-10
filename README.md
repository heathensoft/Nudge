
<p>
    <br />
    <img src="https://img.shields.io/badge/Made%20with-Java-red">
    <img src="https://img.shields.io/badge/Made%20with-LWJGL%20-yellow">
    <img src="https://camo.githubusercontent.com/0fa78702c674a5e13004de53a25ae80ed1ce281f92c0e5d6bd5aa7701b3ab483/68747470733a2f2f696d672e736869656c64732e696f2f6769746875622f6c6963656e73652f61746861756e2f454f532e737667">
</p>

<p align="center">
  <h2 align="center">NUDGE</h2>
  <p align="center">
    2D game development framework for Java
    <br />
    <a href=""><strong>Explore the docs »</strong></a><br>
    <a href="https://github.com/fre-dahl/Nudge/issues">Report Bug</a>
  </p>


<!-- TABLE OF CONTENTS -->
## Table of Contents

* [Features](#features)
* [About](#about)
  * [Introduction](#about)
  * [Built With](#built-with)
* [API Overview](#API-Overview)
* [Getting started](#Getting-started)
    * [Prerequisites](#prerequisites)
    * [Project Setup](#project-setup)
    * [Documentation](#documentation)
* [Issues](https://github.com/fre-dahl/Nudge/issues)
* [License](#license)
* [Contact](#contact)

## Features

Modern [OpenGL](https://www.opengl.org/) through the [LWJGL 3](https://www.lwjgl.org/) library for fast GPU rendering.

* [2D graphics](#2d-graphics) ([OpenGL](https://www.opengl.org/))
* [Lifecycle management](#Lifecycle-management)
* [Entity component system](#Entity-component-system)
* [Lightweight physics](#Lightweight-physics)
* [Window and Input](#Window-and-input) ([GLFW](https://www.glfw.org/))
* [Serialization and networking](#Serialization-and-networking)
* [SteamWorks API integration](#SteamWorks-API-integration)

## About

Nudge is a 2D game library meant to be a template for my own
future game projects. I wanted the framework (Nudge),
and it's development to be publicly available for anyone interested in building
a Game / Game Engine from scratch in Java (Hence the name). Feel free to browse and / or
use any part of the framework.

The Nudge framework intends to be completed and Its development will end in some final state.
It will support some predefined core features, and hopefully support them well. I.e. Keeping it simple!


While Nudge can be used as a template for 2D games in general, it is designed with some specific
use-cases in mind:

* Top down
* Low resolution
* Lots of entities
* Calculation heavy
* Procedural content

Think RTS or Simulation games! 

## API Overview

Broad overview of the intended scope of the framework. Further details and documentation will be included
somewhere along the line of development! 

<span style="color:lightgreen">Intended API</span>

#### 2D graphics

  * Orthographic camera
    * Easy to set up
    * culling
  * General sprite batching
  * Particles
    * Instanced rendering
    * Pools
  * Default Shaders
    * Sprites
    * Tile maps
    * UI / Text
    * Normal-mapping
    * Water
    * Lighting
  * Bitmap font rendering
  * Texture packing (atlas)
  * Animation
    * States
  
#### Lifecycle management

* GameStates 

#### Entity component system

#### Lightweight physics

  * Collision handling
    * Soft (Push pull "springs")
    * Hard (AABB)
    * QuadTree impl.
    
  
#### Window and input

* Mouse
* Keyboard
* GamePad
* Listeners
* Window handling


#### Serialization and networking

  * Represent anything as compact binary
  * UDP network transmission protocol
  * Multi-threaded networking
  * Client / Server

<span style="color:darkorange">Not Core</span>

#### SteamWorks API integration

#### Tile maps

  * Low memory footprint
  * Runtime procedural generation
  * Runtime Auto-tiling (8-Bit bit-masking)

<span style="color:darkorange">Outside of scope</span>

* Mobile
* 3D graphics
* 3D perspective
* Multiple monitors
* Network security protocols
* Simulating real world physics


## Prerequisites
* OpenGL capable graphics card (minimum `core 330`)
* OpenGL capable graphics driver
* Java 1.8


### Built With

#### [LWJGL 3](https://www.lwjgl.org/)
Java library that enables cross-platform access to popular native APIs useful in the development of graphics

#### [OpenGL](https://www.opengl.org/)
Widely used and supported 2D and 3D graphics API

#### [GLFW](https://www.glfw.org/)
Open Source, multi-platform library for OpenGL, OpenGL ES and Vulkan development on the desktop. It provides a simple API for creating windows, contexts and surfaces, receiving input and events.



#### [stb_image](https://github.com/nothings/stb)
Image loading library with support for multiple image formats and color channels


#### [JOML](https://joml-ci.github.io/JOML/)
A Java math library for OpenGL rendering calculations


### License
Copyright (c) 2021 MIT License

Consider crediting the author!

### Contact
(add contact info)

Keep on keeping on!
