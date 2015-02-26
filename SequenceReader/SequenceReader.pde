String firstSeq;
String secondSeq;
int[][] grid;
ArrayList<Pair> pairs;
ArrayList<CharPair> charPairs;

void setup() {
    size(sketchWidth(), sketchHeight());
    initializeAlgorithm();
    matrixFill();
    traceBack();
    pairs = sortPairs(pairs);
    displayOverview();
}

public int sketchWidth() {
    return int(displayWidth * 0.8);
} 

public int sketchHeight() {
    return int(displayHeight * 0.8);
}

public int overviewWidth() {
    return sketchWidth();
}

public int overviewHeight() {
    return int(sketchHeight() * 0.2);
}

public void displayOverview() {
    charPairs = new ArrayList<CharPair>();
    int textSize = overviewWidth()/(max(firstSeq.length(), secondSeq.length()) + pairs.size());
    fill(150);
    rect(0, sketchHeight() - overviewHeight(), overviewWidth(), overviewHeight());
    textSize(textSize);

    int i = 0;
    int j = 0;
    int k = 0;
    int overallIndex = 0;
    while(i<firstSeq.length() && j<secondSeq.length()) {
        print(i, ", ", j, "\n");
        print(pairs.get(k).x, ", ", pairs.get(k).y, "\n");

        if((i == pairs.get(k).x && j != pairs.get(k).y) || (i >= firstSeq.length())) {
            print("skip first\n");
            fill(0);
            charPairs.add(new CharPair(' ', secondSeq.charAt(j), false));
            outputText(' ', secondSeq.charAt(j), overallIndex, textSize);
            j++;
            overallIndex++;
        } else if((i != pairs.get(k).x && j == pairs.get(k).y) || (j >= secondSeq.length())) {
            print("skip second\n");
            fill(0);
            charPairs.add(new CharPair(firstSeq.charAt(i), ' ', false));
            outputText(firstSeq.charAt(i), ' ', overallIndex, textSize);
            i++;
            overallIndex++;
        } else if(i != pairs.get(k).x && j != pairs.get(k).y) {
            print("skip both\n");
            fill(0);
            outputText(firstSeq.charAt(i), secondSeq.charAt(j), overallIndex, textSize);
            i++;
            j++;
            overallIndex++;
        } else if(i == pairs.get(k).x && j == pairs.get(k).y) {
            print("pair\n");
            fill(220, 39, 39);
            outputText(firstSeq.charAt(i), secondSeq.charAt(j), overallIndex, textSize);
            i++;
            j++;
            overallIndex++;
            k = k<pairs.size()-1 ? k+1 : k;
        }
    }
}

public void outputText(char top, char bottom, int location, int size) {
    text(top, location*size, sketchHeight()-(overviewHeight()/2)+size+2);
    text(bottom, location*size, sketchHeight()-(overviewHeight()/2));
}

// public boolean isEnd(int i, int j) {
//     if(i>=firstSeq.length() || j>= secondSeq.length()) {
//         return true;
//     }
//     return false;
// }

// public boolean isPair(int i, int j, int k) {
//     if(i == pairs.get(k).x && j == pairs.get(k).y) {
//         return true;
//     }
//     return false;
// }

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

Pair getFirst(ArrayList<Pair> list) {
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

    boolean lessThan(Pair that) {
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

