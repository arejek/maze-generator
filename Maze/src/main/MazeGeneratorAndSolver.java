package main;

import java.util.Random;
import java.util.Stack;
import java.util.Vector;

public class MazeGeneratorAndSolver {
	
	int size;
	
	public String[][] gridOfPluses, whiteSpace, walls, upsNDowns, markedVisited;
	//pierwsze trzy odpowiadaja za 'narysowanie' labiryntu
	//whiteSpace to pola, a walls i upsNDowns to sciany
	//markedVisited nie jest drukowana, odpowiada za przechowywanie informacji gdzie juz bylismy
	
	Vector<int[]> starredCells = new Vector<>(); //pula komorek 'z ktorych mozemy isc'
	Vector<int[]> currentAdjacent = new Vector<>(); //komorki przylegle do obecnie wybranej komorki
	
	int currentI, currentJ; //wybrana komorka (obecna komorka)
	
	int start, end; //lokalizacja wejscia w wyjscia
	
	MazeGeneratorAndSolver(int size){
		this.size = size;
		gridOfPluses = new String[size][size];
		whiteSpace = new String[size][size];
		walls  = new String[size][size];
		upsNDowns = new String[size][size];	
		markedVisited = new String[size][size];
	}
	
	public void initializeGrid() {
		for(int i=0; i<size; i++) {
			for(int j=0; j<size; j++) {
				gridOfPluses[i][j] = "+";
				walls[i][j] = "|";
				upsNDowns[i][j] = "---";
				if(j==0) {
					upsNDowns[j][i] = "===";	//gorna sciana labiryntu
					walls[i][j] = "!";			//boczna lewa
				}
			}		
		}

		for(int j=0; j<size; j++) {
			upsNDowns[size-1][j] = "===";	//dolna sciana labiryntu
			walls[j][size-1] = "!"; 		//boczna prawa
		}
		
		for(int i=0; i<size; i++) {
			walls[size-1][i] = " ";
			markedVisited[size-1][i] = "x";
		}
		for(int j=0; j<size; j++) {
			upsNDowns[j][size-1] = " ";
			markedVisited[j][size-1] = "x";
		}
		
	}
	
	public void printGrid() {
		for(int i=0; i<size; i++) {
			//drukujemy linie gridu (+===+ lub +   +):
			for(int j=0; j<size; j++) {
				System.out.print(gridOfPluses[i][j] + upsNDowns[i][j]);
			}
			System.out.println();
			//drukujemy linie scian i puste pola labiryntu:
			for(int j=0; j<size; j++) {
				System.out.print(walls[i][j] + whiteSpace[i][j]);
			}
			System.out.println();
		}
	}

	
	public void setCurrent(int i, int j) {
		
		//ustawiamy zmienne reprezentujace indeks obecnej komorki
		currentI = i;
		currentJ = j;
		
		//zaznaczamy
		whiteSpace[currentI][currentJ] = " * ";
		markedVisited[currentI][currentJ] = "x";
		
		//dodajemy komorke do puli:
		int[] current = {currentI, currentJ};
		starredCells.add(current);
	}
	
	//wybieramy komorki z ktorych mozemy losowac droge 
	public boolean selectAdjacent() {
		
		boolean hasAdjacent = false;
		currentAdjacent.clear();
		
		if((walls[currentI][currentJ] != "!") && (markedVisited[currentI][currentJ-1] != "x")) {
			int cell[] = {currentI, currentJ-1};
			currentAdjacent.add(cell);
		}
		if((walls[currentI][currentJ+1] != "!") && (markedVisited[currentI][currentJ+1] != "x")) {
			int cell[] = {currentI, currentJ+1};
			currentAdjacent.add(cell);
		}
		if((currentI-1 < 0) == false) {
			if((upsNDowns[currentI-1][currentJ] != "===") && (markedVisited[currentI-1][currentJ] != "x")) {
				int cell[] = {currentI-1, currentJ};
				currentAdjacent.add(cell);
			}
		}
		if((upsNDowns[currentI+1][currentJ] != "===") && (markedVisited[currentI+1][currentJ] != "x")) {
			int cell[] = {currentI+1, currentJ};
			currentAdjacent.add(cell);
		}
		if(currentAdjacent.isEmpty() == false) hasAdjacent = true;
		
	 return hasAdjacent;
	}	
	
	//otwiera sciane do komorki do ktorej przeszlismy,
	public void openWall(int previousI, int previousJ) {
		
		boolean wentUpDown = false;
		boolean wentLeftRight = false;
		
		int changeInWalls = previousJ - currentJ;
		int changeInUpDowns = previousI - currentI;
		
		if((changeInWalls) != 0) {
			wentLeftRight = true;
			if(changeInWalls < 0) { //poszedl w lewo (nizszy obecny indeks)
				walls[previousI][previousJ+1] = " ";
			} else { //poszedl w prawo
				walls[previousI][previousJ] = " ";
			}	
		}
		if((changeInUpDowns) != 0) {
			wentUpDown = true;
			if(changeInUpDowns < 0) { //poszedl w gore (nizszy obecny indeks)
				upsNDowns[previousI+1][previousJ] = "   ";
			} else { //poszedl w dol
				upsNDowns[previousI][previousJ] = "   ";
			}
		}
		if((wentLeftRight == true) && (wentUpDown == true) ) {
			System.out.println("There was a mistake in location change!");
		}
		
	}
	
	//dla wylosowanej z puli komorki losuje gdzie idziemy dalej
	public void chooseWay() {
		Random rand = new Random();
		int choice = rand.nextInt(currentAdjacent.size());
		int[] temp = {currentI, currentJ};
		int[] current = currentAdjacent.get(choice);
		setCurrent(current[0],current[1]);
		openWall(temp[0],temp[1]);
	}
	
	//losuje z puli komorke z ktorej bedziemy sie poruszac
	public void chooseNext() {
		Random rand = new Random();
		boolean nextChosen = false;
		
		while(nextChosen == false) {
			int choice = rand.nextInt(starredCells.size());
			int[] current = starredCells.get(choice);
			setCurrent(current[0],current[1]);
			if(selectAdjacent() == false) {
				starredCells.remove(choice);
			} else { nextChosen = true; }
		}
		chooseWay();
		
	}
	
    //-------------------------------------------------------------------
	public void goThrough() {
		
		Random rand = new Random();
		
		start = rand.nextInt(size-1); //losujemy lokalizacje wejscia 
		
        upsNDowns[0][start] = " S ";
        setCurrent(0,start);
        selectAdjacent();
        
        for(int i=0; i<((size-1)*(size-1)-1); i++) { 
        	chooseNext();
        }
        
        end = rand.nextInt(size-1); //losujemy lokalizacje wyjscia
        
        upsNDowns[size-1][end] = " E ";
         
        
	} //-----------------------------------------------------------------
	
	//oczyszcza ze znakow pomocniczych przy tworzeniu labiryntu
	public void clearGrid() {
		for(int i=0; i<size; i++) {
			for(int j=0; j<size; j++) {
				whiteSpace[i][j] = "   ";
			}
		}
	}
	
	public void generate() {
		this.initializeGrid();
		this.goThrough();
		this.clearGrid();
		this.printGrid();
	}
	
	//===================================================================
	//                ZNAJDOWANIE WYJSCIA Z LABIRYNTU 
	
	int goingI;
	int goingJ;
	
	int prevI;
	int prevJ;
	
	boolean stuck = false;
	boolean exitFound = false;
	
	Stack<int[]> cellsStack = new Stack<>();
	//stos przechowujacy komorki sciezki
	
	Stack<int[]> mainRoadStack = new Stack<>();
	//stos przechowujacy komorki wyznaczonej krotkiej drogi przez labirynt
	
	//ustawia wspolrzedne komorki w ktorej jestesmy, zapisuje wspolrzedne poprzedniej
	public void setGoingCell(int i, int j) {
		prevI = goingI;
		prevJ = goingJ;
		goingI = i;
		goingJ = j;
		whiteSpace[i][j] = " * ";
	}
	
	public void addToStack(int i, int j) {
		int[] cell = {0,0};
		cell[0] = i;
		cell[1] = j;
		cellsStack.push(cell);
	}
	
	public void addToRoad(int i, int j) {
		int[] cell = {0,0};
		cell[0] = i;
		cell[1] = j;
		mainRoadStack.push(cell);
	}
	
	public void coordinateMoving() {
		//----------------------------------
		while(exitFound == false) {
			//MA GDZIE ISC:
			while((stuck == false) && (exitFound == false)) {
				
				addToStack(goingI,goingJ);
				
				if((goingI != 0) && (goingJ != 0)) { 
				//na krancach planszy (i=0 lub j=0) nie wystepuja skrzyzowania
					if(isCrossRoads(goingI,goingJ)==true) {
						addToStack(goingI,goingJ);
						addToStack(goingI,goingJ);
					}
				}
				move();
			}
			//NIE MA GDZIE ISC:
			if(stuck == true) {
				int[] cell = {0,start};
				
				if(cellsStack.empty() == false) {
					cell = cellsStack.pop();
				}
				whiteSpace[goingI][goingJ] = " . ";

				setGoingCell(cell[0],cell[1]);
				stuck = false;
				if(exitFound == false) move();
			}	
		}
		//----------------------------------
	}
	
	//Sprawdza czy komorka jest skrzyzowaniem o ksztalcie + (wymaga specjalnej obslugi)
	public boolean isCrossRoads(int i, int j) {
		return ( (upsNDowns[goingI][goingJ] == "   ") && (walls[goingI][goingJ+1] == " ")
			&& (upsNDowns[goingI+1][goingJ] == "   ") && (walls[goingI][goingJ] == " "));
	}
	
	public void move() {
		boolean moved = false;
		
		boolean goingRightException = false;
		goingRightException = ((goingJ == (prevJ-1)) && (upsNDowns[goingI][goingJ] == "   "));
		
		if(goingRightException == true) {
			
			//idzie w dol
			if((upsNDowns[goingI+1][goingJ] != " E ") && (upsNDowns[goingI+1][goingJ] != "===") 
			&& (upsNDowns[goingI+1][goingJ] != "---") && (whiteSpace[goingI+1][goingJ] != " * ") 
			&& (whiteSpace[goingI+1][goingJ] != " . ")) 
			{
				setGoingCell(goingI+1,goingJ);
				moved = true;
								
			//sprawdza dol - znalazl wyjscie:	
			} else if((upsNDowns[goingI+1][goingJ] == " E ")) {
				exitFound = true;
				moved = true;
			
			//idzie w lewo
			} else if((walls[goingI][goingJ] != "|") && (walls[goingI][goingJ] != "!")
			&& (whiteSpace[goingI][goingJ-1] != " * ") && (whiteSpace[goingI][goingJ-1] != " . "))
			{
				setGoingCell(goingI,goingJ-1);
				moved = true;
				
			//idzie w gore
			} else if((upsNDowns[goingI][goingJ] != " S ") && (upsNDowns[goingI][goingJ] != "===") && 
			(upsNDowns[goingI][goingJ] != "---") && (whiteSpace[goingI-1][goingJ] != " * ") && 
			(whiteSpace[goingI-1][goingJ] != " . ")) 
			{
				setGoingCell(goingI-1,goingJ);
				moved = true;
	
			//utkniety
			} else if(moved == false){
				stuck = true;
			}
			
		}
		else {
		
			//idzie w gore:
			if((upsNDowns[goingI][goingJ] != " S ") && (upsNDowns[goingI][goingJ] != "===") && 
			(upsNDowns[goingI][goingJ] != "---") && (whiteSpace[goingI-1][goingJ] != " * ") && 
			(whiteSpace[goingI-1][goingJ] != " . ")) 
			{
				setGoingCell(goingI-1,goingJ);
				moved = true;

			//idzie w prawo:
			} else if((walls[goingI][goingJ+1] != "|") && (walls[goingI][goingJ+1] != "!") 
			&& (whiteSpace[goingI][goingJ+1] != " * ") && (whiteSpace[goingI][goingJ+1] != " . ")) 
			{
				setGoingCell(goingI,goingJ+1);
				moved = true;
						
			//idzie w dol:
			} else if((upsNDowns[goingI+1][goingJ] != " E ") && (upsNDowns[goingI+1][goingJ] != "===") 
			&& (upsNDowns[goingI+1][goingJ] != "---") && (whiteSpace[goingI+1][goingJ] != " * ") 
			&& (whiteSpace[goingI+1][goingJ] != " . ")) 
			{
				setGoingCell(goingI+1,goingJ);
				moved = true;
						
			//sprawdza dol - znalazl wyjscie:	
			} else if((upsNDowns[goingI+1][goingJ] == " E ")) {
				moved = true;
				exitFound = true;
						
			//idzie w lewo
			} else if((walls[goingI][goingJ] != "|") && (walls[goingI][goingJ] != "!")
			&& (whiteSpace[goingI][goingJ-1] != " * ") && (whiteSpace[goingI][goingJ-1] != " . "))
			{
				setGoingCell(goingI,goingJ-1);
				moved = true;
			
			//kiedy jest utkniety, otoczony ze wszystkich stron scianami i gwiazdkami itd
			} else if(moved == false){
				stuck = true;
			}
		}
		
	}
	
	public void findWayOut() {
		setGoingCell(0,start);
		coordinateMoving();
	}
	
	public void moveThroughPath() {
		
		boolean roadEnds = false;
		
		//-------------------------
		while(roadEnds == false) {
			
			boolean moved = false;
			
			//idzie w lewo
			if((whiteSpace[goingI][goingJ+1] == " * ") && (walls[goingI][goingJ+1] != "|") && (walls[goingI][goingJ+1] != "!")) {
				setGoingCell(goingI,goingJ+1);
				whiteSpace[goingI][goingJ] = " # ";
				addToRoad(goingI,goingJ);
				moved = true;
			
			//idzie w dol
			} else if((whiteSpace[goingI+1][goingJ] == " * ") && (upsNDowns[goingI+1][goingJ] != "---") && (upsNDowns[goingI+1][goingJ] != "===") 
					&& (upsNDowns[goingI+1][goingJ] != " E ")){
				setGoingCell(goingI+1,goingJ);
				whiteSpace[goingI][goingJ] = " # ";
				addToRoad(goingI,goingJ);
				moved = true;
				
			} else if(upsNDowns[goingI+1][goingJ] == " E ") {
				roadEnds = true;
				moved = true;
			
			//idzie w prawo
			} else 
				if((whiteSpace[goingI][goingJ-1] == " * ") && (walls[goingI][goingJ] != "|") && (walls[goingI][goingJ] != "!")){
					setGoingCell(goingI,goingJ-1);
					whiteSpace[goingI][goingJ] = " # ";
					addToRoad(goingI,goingJ);
					moved = true;
				
			} else if(moved == false) {
				mainRoadStack.pop();
				int[] cell = {0,0};
				if(mainRoadStack.empty() == false) {
					cell = mainRoadStack.pop();
				}
				setGoingCell(cell[0],cell[1]);
				addToRoad(goingI,goingJ);
				whiteSpace[goingI][goingJ] = " # ";	
			}
		}
		//-------------------------
	}
	
	public void clearWay() {
		setGoingCell(0,start);
		addToRoad(goingI,goingJ);
		moveThroughPath();
		clearGrid();
		while(mainRoadStack.empty() == false) {
			int[] cell = mainRoadStack.pop();
			whiteSpace[cell[0]][cell[1]] = " # ";
		}
	}
	
	//===================================================================
	
	public static void main(String args[]) {
		
		MazeGeneratorAndSolver mg = new MazeGeneratorAndSolver(51);
		
		mg.initializeGrid();
		mg.goThrough();
		mg.clearGrid();
		
		System.out.println();
		System.out.println("** GENERATED MAZE **");
		mg.printGrid();
		
		mg.findWayOut();

		System.out.println();
		System.out.println("Maze after going through it first time (touching the wall)");
		mg.printGrid();
		mg.clearWay();
		System.out.println();
		System.out.println("** WAY OUT OF THE MAZE **");
		mg.printGrid();
		
	}
}
