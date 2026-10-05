# 🎈 Balloon Animation — Quick README

## 1. What does it do?
A simple **C graphics animation** where 5 balloons continuously move **upward** on the screen.

## 2. Work Flow
**Create 5 balloons → Give random X positions → Draw them → Move upward → If they leave screen, bring them back to bottom → Repeat until a key is pressed.**

## 3. Core Concepts

| Code | Purpose |
|---|---|
| `graphics.h` | Graphics functions |
| `initgraph()` | Starts graphics mode |
| `x[5], y[5]` | Store positions of 5 balloons |
| `rand()` | Generates random positions/colors |
| `circle()` | Draws balloon |
| `floodfill()` | Fills balloon |
| `y[i] -= 2` | Moves balloon upward |
| `cleardevice()` | Clears previous frame |
| `delay(50)` | Controls animation speed |
| `kbhit()` | Checks whether a key is pressed |
| `closegraph()` | Closes graphics mode |

## 4. Important Logic

### Initial position
```c
x[i] = rand() % getmaxx();
y[i] = getmaxy();
```
- `x` = random horizontal position
- `y` = bottom of screen

### Movement
```c
y[i] -= 2;
```
In graphics coordinates, decreasing `Y` moves the object **up**.

### Reset
```c
if (y[i] < 0) {
    y[i] = getmaxy();
    x[i] = rand() % getmaxx();
}
```
When a balloon leaves the top, it comes back from the bottom at a new random X position.

## 5. Main Loop
```text
while key is NOT pressed
      ↓
clear screen
      ↓
move all balloons
      ↓
draw all balloons
      ↓
wait 50 ms
      ↓
repeat
```

## 6. 30-Second  review
> “This is a C graphics animation using the BGI/Turbo C graphics library. I store the X and Y coordinates of 5 balloons in arrays. In every loop, I decrease their Y coordinate to move them upward. When a balloon crosses the top boundary, I reset it to the bottom with a new random X position. `cleardevice()` refreshes the frame and `delay()` controls the animation speed.”

## 7. Quick Questions

**Q: Why use arrays?**  
To store positions of multiple balloons efficiently.

**Q: Why `y[i] -= 2`?**  
Because decreasing Y moves upward in screen coordinates.

**Q: Why `cleardevice()`?**  
To remove the previous frame before drawing the new positions.

**Q: Why `kbhit()`?**  
To stop the animation when the user presses a key.

**Q: Why `rand()`?**  
To create random balloon positions and colors.

## 8. Important Limitation
- Uses **legacy Turbo C/BGI (`graphics.h`)**, so it may not run directly in modern C compilers.
- `time.h` is included but **not actually used** in the code.
