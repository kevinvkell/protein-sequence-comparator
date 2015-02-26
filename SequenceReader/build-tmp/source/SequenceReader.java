import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class SequenceReader extends PApplet {

String firstSeq;
String secondSeq;
int window = 7;
int[][] grid;
ArrayList<Pair> pairs;
ArrayList<CharPair> charPairs;

public void setup() {
    size(sketchWidth(), sketchHeight());
    initializeAlgorithm();
    matrixFill();
    traceBack();
    pairs = sortPairs(pairs);
    calculateOutput();
    writeOverviewPairs();
}

public void draw() {
    if(mouseY<sketchHeight() && mouseY > sketchHeight()-overviewHeight()) {
        writeMainViewPairs(mouseX);
    }
}

public int sketchWidth() {
    return PApplet.parseInt(displayWidth * 0.8f);
} 

public int sketchHeight() {
    return PApplet.parseInt(displayHeight * 0.8f);
}

public int overviewWidth() {
    return sketchWidth();
}

public int overviewHeight() {
    return PApplet.parseInt(sketchHeight() * 0.2f);
}

public void calculateOutput() {
    charPairs = new ArrayList<CharPair>();
    int textSize = overviewWidth()/(max(firstSeq.length(), secondSeq.length()) + pairs.size());
    fill(150);
    rect(0, sketchHeight() - overviewHeight(), overviewWidth(), overviewHeight());
    textSize(textSize);

    int i = 0;
    int j = 0;
    int k = 0;
    while(i<firstSeq.length() && j<secondSeq.length()) {
        if((i == pairs.get(k).x && j != pairs.get(k).y) || (i >= firstSeq.length())) {
            charPairs.add(new CharPair(' ', secondSeq.charAt(j), false));
            j++;
        } else if((i != pairs.get(k).x && j == pairs.get(k).y) || (j >= secondSeq.length())) {
            charPairs.add(new CharPair(firstSeq.charAt(i), ' ', false));
            i++;
        } else if(i != pairs.get(k).x && j != pairs.get(k).y) {
            charPairs.add(new CharPair(firstSeq.charAt(i), secondSeq.charAt(j), false));
            i++;
            j++;
        } else if(i == pairs.get(k).x && j == pairs.get(k).y) {
            charPairs.add(new CharPair(firstSeq.charAt(i), secondSeq.charAt(j), true));
            i++;
            j++;
            k = k<pairs.size()-1 ? k+1 : k;
        }
    }
}

public void writeOverviewPairs() {
    int textSize = overviewWidth()/charPairs.size();
    textSize(textSize);
    for(int i=0; i<charPairs.size(); i++) {
        CharPair current = charPairs.get(i);

        if(current.isPair) {
            fill(220, 39, 39);
        }
        else {
            fill(0);
        }

        text(current.char1, i*textSize, sketchHeight()-(overviewHeight()/2)+textSize+2);
        text(current.char2, i*textSize, sketchHeight()-(overviewHeight()/2));
    }
}

public void writeMainViewPairs(int x) {
    fill(255);
    rect(0, 0, sketchWidth(), sketchHeight()-overviewHeight());
    int middleIndex = PApplet.parseInt(((float)x/(float)sketchWidth()) * charPairs.size());
    if(middleIndex < window) {
        middleIndex = window;
    }
    if(middleIndex >= charPairs.size() - window) {
        middleIndex = charPairs.size() - 1 - window;
    }

    int textSize = sketchWidth()/(window * 2);
    textSize(textSize);

    for(int i=middleIndex-window; i<middleIndex+window; i++) {
        CharPair current = charPairs.get(i);

        if(current.isPair) {
            fill(220, 39, 39);
        }
        else {
            fill(0);
        }

        int xCoord = (i-(middleIndex-window))*textSize;
        int yCoord = (sketchHeight()-overviewHeight())/2;

        text(current.char1, xCoord, yCoord + textSize + 2);
        text(current.char2, xCoord, yCoord);
    }
    
}

public void initializeAlgorithm() {
    BufferedReader reader1 = createReader("DUT_BUCAI.txt");
    BufferedReader reader2 = createReader("DUT_CANAL.txt");

    try {
        firstSeq = reader1.readLine();
        secondSeq = reader2.readLine();
    } catch (IOException e){
        e.printStackTrace();
    }

    grid = new int[firstSeq.length()][secondSeq.length()];
}

public void matrixFill() {
    for(int i=0; i<firstSeq.length(); i++) {
        for(int j=0; j<secondSeq.length(); j++) {
            int left = i-1<0 ? 0 : grid[i-1][j];
            int up = j-1<0 ? 0 : grid[i][j-1];
            int diag = j-1<0 || i-1<0 ? 0 : grid[i-1][j-1];

            if(firstSeq.charAt(i) == secondSeq.charAt(j)) {
                diag++;
            }

            grid[i][j] = max(left, up, diag);
        }
    }
}

public void traceBack() {
    pairs = new ArrayList<Pair>();

    int i = firstSeq.length() - 1;
    int j = secondSeq.length() - 1;
    while(i>0 && j>0) {
        int left = grid[i-1][j];
        int up = grid[i][j-1];
        int diag = grid[i-1][j-1];
        int max = max(left, up, diag);
        int previous = grid[i][j];
        Pair prevPair = new Pair(i, j);

        boolean leftIsMax = max == left;
        boolean upIsMax = max == up;
        boolean diagIsMax = max == diag;

        if(diagIsMax) {
            i--;
            j--;
        }
        else if(leftIsMax) {
            i--;
        }
        else {
            j--;
        }

        if(grid[i][j] < previous) {
            pairs.add(prevPair);
        }
    }
}

public void printGrid() {
    for(int i=0; i<firstSeq.length(); i++) {
        for(int j=0; j<secondSeq.length(); j++) {
            print(grid[i][j], " ");
        }
        print("\n");
    }
}

public void printPairs() {
    for(Pair p : pairs) {
        print(p.x, ", ", p.y, "\n");
    }
}

public ArrayList<Pair> sortPairs(ArrayList<Pair> unsorted) {
    ArrayList<Pair> tmp = new ArrayList<Pair>(unsorted);
    ArrayList<Pair> sorted = new ArrayList<Pair>();
    while(tmp.size() > 0) {
        Pair next = getFirst(tmp);
        sorted.add(next);
        tmp.remove(next);
    }

    return sorted;
}

public Pair getFirst(ArrayList<Pair> list) {
    Pair result = null;
    for(Pair p : list) {
        if(result == null) {
            result = p;
        }
        else {
            result = p.lessThan(result) ? p : result;
        }
    }

    return result;
}

class Pair {
    int x;
    int y;

    Pair(int newX, int newY) {
        x = newX;
        y = newY;
    }

    public boolean lessThan(Pair that) {
        if(x<that.x || y<that.y) {
            return true;
        }

        return false;
    }
}

class CharPair {
    char char1;
    char char2;
    boolean isPair;

    CharPair(char newChar1, char newChar2, boolean newIsPair) {
        char1 = newChar1;
        char2 = newChar2;
        isPair = newIsPair;
    }
}

  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "SequenceReader" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
