# CJGE
Compact Java Game Engine - One Abstract Class, All Native Imports.

This is a game engine meant to be writen as one single abstract class in Java using only Java's native libraries. 
This is for a matter of simplicity in the hopes that it will help people jump into game developement more easily.
By not having to worry about messing with importing libraries and such, your life will be stress-free. Instead,
simply download the class and add it to your project source. Extend the class just like you would a JFrame, and 
let your IDE add the unimplemented methods.

# Usage

This is a simple game engine implemented in Java, providing a basic framework for creating games. It handles rendering, input events (keyboard and mouse), and game updates. The engine is designed to be extended by a concrete game implementation.

## Getting Started

To use this game engine, follow these steps:

1. Clone the repository: `git clone https://github.com/your-username/game-engine.git`
2. Open the project in your preferred Java IDE.
3. Extend the class just like you would a JFrame, and let your IDE add the unimplemented methods.
4. Build and run the project.

## Features

- Window creation and management using `JFrame` and `Canvas`.
- Rendering capabilities, including a buffer strategy and pixel manipulation.
- Handling of keyboard and mouse input events, including key press/release, mouse button press/release, mouse movement, and mouse wheel events.
- Game loop implementation for continuous game updates and rendering.
- Utility methods for drawing images on the screen.

## Usage

To create your game using this engine, follow these guidelines:

1. Extend the `GameEngine` class and implement the abstract methods (`preGameOperations()`, `preRenderUpdate()`, `renderUpdate()`, and `stop()`).
2. Add your game-specific logic in the implemented methods.
3. Customize the rendering and input handling as needed.
4. Run the game and test your implementation.

## Contributions

Contributions to this game engine are welcome! If you find any issues or have suggestions for improvements, feel free to open an issue or submit a pull request.
