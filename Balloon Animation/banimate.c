#include <graphics.h>
#include <conio.h>
#include <stdlib.h>
#include <time.h>

int main() {
    int gd = DETECT, gm;
    int x[5], y[5], i;

    clrscr();

    initgraph(&gd, &gm, "C:\\TC\\BGI");

    // Set the initial positions of balloons
    for (i = 0; i < 5; i++) {
        x[i] = rand() % getmaxx();
        y[i] = getmaxy();
    }

    while (!kbhit()) {
        cleardevice();

        // Update the positions of balloons
        for (i = 0; i < 5; i++) {

            y[i] -= 2; // Move balloons upwards

            // Reset balloon when it goes off the screen
            if (y[i] < 0) {
                y[i] = getmaxy();
                x[i] = rand() % getmaxx();
            }

            // Draw balloon
            setcolor(rand() % 15 + 1);
            circle(x[i], y[i], 10);
            floodfill(x[i], y[i], getcolor());
        }

        delay(50);
    }

    closegraph();

    return 0;
}
