# TakBot

The tak bot competes against other tak players or bots. It uses minimax search with alpha beta pruning. Read more about it [here](https://www.cs.cornell.edu/courses/cs312/2002sp/lectures/rec21.htm). Minimax search is a popular game play strategy.

## How to run it?

Compile and run the file player.cpp

## How to use it?

If you are placing a stone, first you need to specify what kind of stone you are placing. This is specified by the letter F, S or C, indicating flat stone, wall (standing  stone), or capstone respectively. This is followed by the square on which stone is being placed. Eg:
(a) Place a flat stone on the square a1: Fa1
(b) Place a wall at d3: Sd3
(c) Place a capstone at b4: Cb

If you are moving a stack on the board, first you need to specify the number of stones being moved from a stack (eg. 4). Then you specify the square from which the stack is being moved (eg. c3). Then you need to specify the direction in which the stones picked will move. This is specified by one of the following symbols: < (moving towards the letter a on board),> (the opposite direction), - (moving towards the number 1 on board) or + (the opposite direction). Finally you specify the number of stones to drop on each square in the given direction.


## Issues?

Mail us or open an issue here on github

