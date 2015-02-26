# protein-sequence-comparator
Finds and visualizes the best alignment for two sequences. 

##Use
Run SequenceReader.pde like any other processing project.
The input files must be named input1.txt and input2.txt. 
The contents of the sequence must be all on one line with no spaces or delimiters. 

##Visualization
Matches are displayed in red, and breaks are displayed as a space. 
The entire alignment is displayed in the overview panel at the bottom.
As the size of the alignment gets bigger, the text gets smaller.
Eventually the text will be too small to read, and the user will only be able to see the color red where matches are.
To view more detail, the user must mouse over the area in the overview that they would like to see more closely. 
The zoomed in view will be displayed in the main display area at the top middle. 

##Significance
This visualization allows biologists to see how closely related two sequences are. 
They can see a general idea of how related the sequences are by looking at the overview. 
They can see more specifically how the sequences are aligned by looking at the main view. 

When the mouse is in the overview panel, the draw function calculates the part of the sequence to display based on the x position of the mouse. 
It is easy to display this section of the data because the entire alignment has been calculated and stored, so displaying a section of it is a matter of pulling a few indexes in an array. 