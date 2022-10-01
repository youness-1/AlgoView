# AlgoView
Android app to visualize search algorithms on a canvas grid
The implemented algorithms are Dijkstra and A* with 2 heuristics functions, manhattan distance and euclidian distance.

The application has two sections:
- the home fragment contains the grid with the buttons for carrying out the main operations
- the settings fragment, which is used to set the parameters used for viewing the search algorithms


Under the Grid in the Home fragment there are 3 buttons and a spinner.
spinner element:
- When the user touches a cell of the grid, it takes the value set in the spinner that can be: a wall tile, the start/stop tile and the empty tile
reset button:
- It interrupts the execution of a search algorithm and it resets the Solution, Fringe and Search tiles to Empty
dijkstra button:
- runs the Dijkstra search algorithm, it requires a Start cell and a Stop cell to be present in the grid
A * button:
- runs the search algorithm A *, it requires a Start cell and a Stop cell to be present in the grid

The Settings fragment allows you to set:
The Diagonal Path button: 
- If enabled it gives the possibility of moving diagonally during the execution of a search algorithm. Based on this choice, the type of heuristic used by the algorithm A * is chosen
The Tile Size seekbar: 
- It allows the user to set the length of the side of each cell from 20 to 200 pixels
The Animation Time Radio Button Group: 
- It sets the speed of execution of the search algorithms (Slow, Normal and Fast)
