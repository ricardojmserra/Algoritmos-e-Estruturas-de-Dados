package aed.search;
import java.util.ArrayList;
import java.util.List;
import aed.collections.StackList;

public class SudokuState {

    private static final int N = 9;
    private int[][] board;

    public SudokuState(int[][] board){
        this.board = board;
    }

    public int[][] getBoard(){
        return this.board;
    }

    public boolean isSolution() {
    	
    	for(int i = 0; i < N; i++)
    		for(int j = 0; j < N; j++) {
    			if(this.board[i][j] == 0)								// zeros nao existem
    				return false;
    			
    			for(int z = j+1; z < N; z++) 							// linhas e colunas
    				if(this.board[i][j] == this.board[i][z] || this.board[j][i] == this.board[z][i]) // Linhas e Colunas
    					return false;
    			
    			if(i%3 == 0 && j%3 == 0)
    				for(int xCheck = 0; xCheck < N; xCheck++)			//quadrados 3*3
    					for(int xCheck2 = xCheck+1; xCheck2 < N; xCheck2++)
    						if(this.board[i + (xCheck%3)][j + (xCheck/3)] == this.board[i + (xCheck2%3)][j + (xCheck2/3)])
    							return false;
    		}
    	return true;
    }

    public boolean isValidAction(int row, int column, int value){
    	if(!(value <= 9 && 0 <= value) && this.board[column][row] != 0) //valor valido e pos valida
    		return false;
    	int GridY = column/3; //qual a grelha 3*3 que se encontra no eixo Y
    	int GridX = row/3;	//qual a grelha 3*3 que se encontra no eixo X
        for(int Temp = 0; Temp < N; Temp++)// ver 9 valores para cima, baixo e no quadrado 3*3
        	if(this.board[row][Temp] == value || this.board[Temp][column] == value || this.board[GridX*3 + (Temp%3)][GridY*3 + (Temp/3)] == value)
        		return false;
        return true;
    }

    public SudokuState generateNextState(int row, int column, int value){
    	SudokuState Output = this.clone();
    	Output.board[row][column] = value;
        return Output;
    }

    public List<SudokuState> generateValidNextStates() {
    	List<SudokuState> OutPut = new ArrayList<SudokuState>();
        for(int row = 0; row < N; row++) {
        	for(int column = 0; column < N; column++) {
        		
        		if(this.board[row][column] == 0) { //Procurar uma casa vazia Esquerda-Diteira, Cima-Baixo
        			for(int value = 1; value <= 9; value++) { //Verificar se algum valor 1-9 é valido
        				if(this.isValidAction(row, column, value)) {
        					OutPut.add(this.generateNextState(row, column, value));
        				}
        			}
        			return OutPut; //return dos estados que existe para a proxima casa vazia
        		}
        	}
        }
        return OutPut; //return vazio
    }

    public static SudokuState backtrackingSearch(SudokuState initialState){
        StackList<SudokuState> OutPut = new StackList<SudokuState>();
        OutPut.push(initialState);
        while(!OutPut.isEmpty() && !OutPut.peek().isSolution()) {
        		for(SudokuState i : OutPut.pop().generateValidNextStates())
        			OutPut.push(i);
        }
        return OutPut.peek();
    }
    
    public SudokuState clone(){
        int[][] newBoard = this.board.clone();
        for(int i = 0; i < N; i++)
            newBoard[i] = this.board[i].clone();
        SudokuState newState = new SudokuState(newBoard);
        return newState;
    }
    
    public String toString(){
        String s = "";
        for(int i = 0 ; i < N ; i++){
            if(i % 3 == 0)
                s+= "----------------------\n";
            for(int j = 0; j < N ; j++){
                if(j % 3 == 0)
                    s+= "|";
                if(this.board[i][j] == 0)
                    s+= "_ ";
                else s+= this.board[i][j]+" ";
            }
            s+="|\n";
        }
        s+= "----------------------\n";
        return s;
    }
}