
import java.io.*;
import java.util.*;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ANKIT
 */
public class BoardState {
    int player_me;
    int player_opp;
    int size;
    int time;//in sec
    HashMap<Integer, ArrayList<BoardUnit>> board = new HashMap<Integer, ArrayList<BoardUnit>>();
    BoardState(int player_me, int player_opp, int size, int time){
        this.player_me = player_me;
        this.player_opp = player_opp;
        this.size = size;
        this.time = time;
        this.board = new HashMap<Integer, ArrayList<BoardUnit>>();
        int board_size = size*size;
        for(int i = 1; i<=board_size; i++){
                board.put(i, new ArrayList<BoardUnit>());
        }
    }
    void addPiece(int posn, BoardUnit bu){
        this.board.get(posn).add(bu);
    }
    BoardUnit removePiece(int posn,int bu_pos){
        return this.board.get(posn).remove(bu_pos);
    }
    void printBoard(){
        int board_size = size*size;
        for(int i =0; i<size; i++){
            String res = ""+(char)(i+97);
            for(int j =1; j<=size; j++){
                ArrayList<BoardUnit> entry = board.get(size*i +j);
                String entry_set = "[";
                for(int k=0; k<entry.size(); k++){
                    entry_set = entry_set + "("+entry.get(k).getColor() + ","+entry.get(k).getKind()+"),";
                }
                entry_set = entry_set + "]";
                res = res +(j)+" : "+ entry_set+"; ";
            }
            System.out.println(res);
        }
    }
    
    int[] getOwnedSquares(){//returns number of squares owned by me and that by opponent in an array [p_me,p_opp]
        int [] result = new int[2];
        int p_me=0;
        int p_opp = 0;
        for(int i =0; i<size; i++){
            for(int j =1; j<=size; j++){
                ArrayList<BoardUnit> entry = board.get(size*i +j);
                if(!entry.isEmpty()){
                    int player = entry.get(entry.size() -1).color;
                    if(player == player_me){
                        p_me++;
                    }
                    if(player == player_opp){
                        p_opp++;
                    }
                }
            }
        }
        result[0]=p_me;
        result[1] = p_opp;
        return result;
    }
    
    int[] getStackHeight(){
        int[] result = new int[2];
        int p_me=0;
        int p_opp = 0;
        for(int i =0; i<size; i++){
            for(int j =1; j<=size; j++){
                ArrayList<BoardUnit> entry = board.get(size*i +j);
                int entry_size = entry.size();
                if(!entry.isEmpty()){
                    int player = entry.get(entry_size -1).color;
                    if(player == player_me){
                        p_me+=entry_size;
                    }
                    if(player == player_opp){
                        p_opp+=entry_size;
                    }
                }
            }
        }
        result[0]=p_me;
        result[1] = p_opp;
        return result;
    }
    
    int[] getMaxChainLength(){//returns the length of the maximum length chain of mine and opp's
        int[] result = new int[2];
        int max_me=0,cur_me=0,max_opp=0,cur_opp=0;
        for(int i =0; i<size; i++){
            for(int j =1; j<=size; j++){
                ArrayList<BoardUnit> entry = board.get(size*i +j);
                int entry_size = entry.size();
                if(entry.isEmpty()){
                    cur_me=0;
                    cur_opp=0;
                }
                else{
                    int player = entry.get(entry_size -1).color;
                    if(player == player_me){
                        cur_me++;
                        cur_opp=0;
                        if(cur_me>max_me){
                            max_me=cur_me;
                        }
                    }
                    if(player == player_opp){
                        cur_opp++;
                        cur_me=0;
                        if(cur_opp>max_opp){
                            max_opp=cur_opp;
                        }
                    }
                }
            }
        }
        for(int i =1; i<=size; i++){
            for(int j =0; j<size; j++){
                ArrayList<BoardUnit> entry = board.get(size*j +i);
                int entry_size = entry.size();
                if(entry.isEmpty()){
                    cur_me=0;
                    cur_opp=0;
                }
                else{
                    int player = entry.get(entry_size -1).color;
                    if(player == player_me){
                        cur_me++;
                        cur_opp=0;
                        if(cur_me>max_me){
                            max_me=cur_me;
                        }
                    }
                    if(player == player_opp){
                        cur_opp++;
                        cur_me=0;
                        if(cur_opp>max_opp){
                            max_opp=cur_opp;
                        }
                    }
                }
            }
        }
        result[0]=max_me;
        result[1] = max_opp;
        return result;
    }
    
    BoardState afterPlacing(int player,int kind, char posn_alphabet, int posn_numeric){
        BoardState result = new BoardState(player_me,player_opp,size,time);
        for(int i=1; i<=size*size; i++){
            result.board.put(i,new ArrayList<BoardUnit>(this.board.get(i)));
        }
        int position = (((int)posn_alphabet)-97)*size + posn_numeric;
        if(!result.board.get(position).isEmpty()){
            return null;
        }
        else{
            result.addPiece(position, new BoardUnit(player,kind));
        }
        return result;
    }
    
    BoardState moveStack(int player,char dirn, char posn_alphabet, int posn_numeric, ArrayList<Integer> moves){//returns the board state after moving the stack
        BoardState result = new BoardState(player_me,player_opp,size,time);
        for(int i=1; i<=size*size; i++){
            //result.board.put(i,new ArrayList<BoardUnit>(this.board.get(i)));
            for(int j =0; j<this.board.get(i).size(); j++){
                int tmp_kind = this.board.get(i).get(j).kind;
                int tmp_color = this.board.get(i).get(j).color;
                result.board.get(i).add(new BoardUnit(tmp_color,tmp_kind));
            }
        }
        int position = (((int)posn_alphabet)-97)*size + posn_numeric;
        int sh = 0;
        int moves_size=moves.size();
        for(int i =0; i<moves_size; i++){
            sh+=moves.get(i);
        }
        if(dirn == '+'){
            int limit = posn_numeric+moves_size;
            if(limit>size){//check that boundary is not crossed
                return null;
            }
            else{
                //scope of optimising here.Instead of checking if there is a wall/capstone here, check it before for all moves size in a separate function and return an array of results
                int removal_index = 0;
                for(int i=0; i<moves_size; i++){
                    ArrayList<BoardUnit> entry = result.board.get(position);
                    int entry_size = entry.size();
                    if(i==0){
                        removal_index=entry_size-sh;
                    }
                    ArrayList<BoardUnit> captured = result.board.get(position+i+1);
                    int captured_size = captured.size();
                    int kind = (captured.isEmpty())?(1):(captured.get(captured_size-1).kind);
                    if(kind==1){//means that the square which is going to be captured by my piece(s) is having a flatstone on top OR is empty; so its completely okay to move there.
                        ArrayList<BoardUnit> moving_unit = new ArrayList<BoardUnit>(entry.subList(removal_index,removal_index+moves.get(i)));
                        for(int j=removal_index;j<removal_index+moves.get(i);j++){
                            entry.remove(removal_index);
                        }
                        captured.addAll(moving_unit);
                    }
                    else if(kind==2){//means that the square which is going to be captured by my piece(s) is a wall
                        if(moves.get(i)!=1){//means that the ith combination of pieces is not only one ==> that can't be a single capstone ==> this move is invalid
                            return null;
                        }
                        else{//means that the ith combination of pieces is only one
                            if(entry.get(removal_index).kind == 3){//means that the piece is capstone; a valid move always FOR kind =2
                                ArrayList<BoardUnit> moving_unit = new ArrayList<BoardUnit>(entry.subList(removal_index,removal_index+moves.get(i)));
                                for(int j=removal_index;j<removal_index+moves.get(i);j++){
                                    entry.remove(removal_index);
                                }
                                BoardUnit removed_piece = captured.remove(captured.size()-1);
                                removed_piece.setKind(1);
                                captured.add(removed_piece);
                                captured.addAll(moving_unit);
                            }
                            else{//means that piece is not a capstone ==> invalid move
                                return null;
                            }
                        }
                    }
                    else if(kind==3){//means that the square which is going to be captured bymy piece(s) is a capstone
                        return null;
                    }
                }
            }
        }
        else if(dirn == '-'){
         int limit = posn_numeric-moves_size;
            if(limit<1){//check that boundary is not crossed
                return null;
            }
            else{
                //scope of optimising here.Instead of checking if there is a wall/capstone here, check it before for all moves size in a separate function and return an array of results
                int removal_index = 0;
                for(int i=0; i<moves_size; i++){
                    ArrayList<BoardUnit> entry = result.board.get(position);
                    int entry_size = entry.size();
                    if(i==0){
                        removal_index=entry_size-sh;
                    }
                    ArrayList<BoardUnit> captured = result.board.get(position-i-1);
                    int captured_size = captured.size();
                    int kind = (captured.isEmpty())?(1):(captured.get(captured_size-1).kind);
                    if(kind==1){//means that the square which is going to be captured by my piece(s) is having a flatstone on top OR is empty; so its completely okay to move there.
                        ArrayList<BoardUnit> moving_unit = new ArrayList<BoardUnit>(entry.subList(removal_index,removal_index+moves.get(i)));
                        for(int j=removal_index;j<removal_index+moves.get(i);j++){
                            entry.remove(removal_index);
                        }
                        captured.addAll(moving_unit);
                    }
                    else if(kind==2){//means that the square which is going to be captured by my piece(s) is a wall
                        if(moves.get(i)!=1){//means that the ith combination of pieces is not only one ==> that can't be a single capstone ==> this move is invalid
                            return null;
                        }
                        else{//means that the ith combination of pieces is only one
                            if(entry.get(removal_index).kind == 3){//means that the piece is capstone; a valid move always FOR kind =2
                                ArrayList<BoardUnit> moving_unit = new ArrayList<BoardUnit>(entry.subList(removal_index,removal_index+moves.get(i)));
                                for(int j=removal_index;j<removal_index+moves.get(i);j++){
                                    entry.remove(removal_index);
                                }
                                BoardUnit removed_piece = captured.remove(captured.size()-1);
                                removed_piece.setKind(1);
                                captured.add(removed_piece);
                                captured.addAll(moving_unit);
                            }
                            else{//means that piece is not a capstone ==> invalid move
                                return null;
                            }
                        }
                    }
                    else if(kind==3){//means that the square which is going to be captured bymy piece(s) is a capstone
                        return null;
                    }
                }
            }
        }
        else if(dirn == '<'){
           int limit = ((int)posn_alphabet-96)-moves_size; //a=1,b=2,c=3....
            if(limit<1){//check that boundary is not crossed
                return null;
            }
            else{
                //scope of optimising here.Instead of checking if there is a wall/capstone here, check it before for all moves size in a separate function and return an array of results
                int removal_index = 0;
                for(int i=0; i<moves_size; i++){
                    ArrayList<BoardUnit> entry = result.board.get(position);
                    int entry_size = entry.size();
                    if(i==0){
                        removal_index=entry_size-sh;
                    }
                    ArrayList<BoardUnit> captured = result.board.get(position-size*(i+1));
                    int captured_size = captured.size();
                    int kind = (captured.isEmpty())?(1):(captured.get(captured_size-1).kind);
                    if(kind==1){//means that the square which is going to be captured by my piece(s) is having a flatstone on top OR is empty; so its completely okay to move there.
                        ArrayList<BoardUnit> moving_unit = new ArrayList<BoardUnit>(entry.subList(removal_index,removal_index+moves.get(i)));
                        for(int j=removal_index;j<removal_index+moves.get(i);j++){
                            entry.remove(removal_index);
                        }
                        captured.addAll(moving_unit);
                    }
                    else if(kind==2){//means that the square which is going to be captured by my piece(s) is a wall
                        if(moves.get(i)!=1){//means that the ith combination of pieces is not only one ==> that can't be a single capstone ==> this move is invalid
                            return null;
                        }
                        else{//means that the ith combination of pieces is only one
                            if(entry.get(removal_index).kind == 3){//means that the piece is capstone; a valid move always FOR kind =2
                                ArrayList<BoardUnit> moving_unit = new ArrayList<BoardUnit>(entry.subList(removal_index,removal_index+moves.get(i)));
                                for(int j=removal_index;j<removal_index+moves.get(i);j++){
                                    entry.remove(removal_index);
                                }
                                BoardUnit removed_piece = captured.remove(captured.size()-1);
                                removed_piece.setKind(1);
                                captured.add(removed_piece);
                                captured.addAll(moving_unit);
                            }
                            else{//means that piece is not a capstone ==> invalid move
                                return null;
                            }
                        }
                    }
                    else if(kind==3){//means that the square which is going to be captured bymy piece(s) is a capstone
                        return null;
                    }
                }
            }
        }
        else if(dirn == '>'){
          int limit = ((int)posn_alphabet-96)+moves_size;
            if(limit>size){//check that boundary is not crossed
                return null;
            }
            else{
                //scope of optimising here.Instead of checking if there is a wall/capstone here, check it before for all moves size in a separate function and return an array of results
                int removal_index = 0;
                for(int i=0; i<moves_size; i++){
                    ArrayList<BoardUnit> entry = result.board.get(position);
                    int entry_size = entry.size();
                    if(i==0){
                        removal_index=entry_size-sh;
                    }
                    ArrayList<BoardUnit> captured = result.board.get(position+size*(i+1));
                    int captured_size = captured.size();
                    int kind = (captured.isEmpty())?(1):(captured.get(captured_size-1).kind);
                    if(kind==1){//means that the square which is going to be captured by my piece(s) is having a flatstone on top OR is empty; so its completely okay to move there.
                        ArrayList<BoardUnit> moving_unit = new ArrayList<BoardUnit>(entry.subList(removal_index,removal_index+moves.get(i)));
                        for(int j=removal_index;j<removal_index+moves.get(i);j++){
                            entry.remove(removal_index);
                        }
                        captured.addAll(moving_unit);
                    }
                    else if(kind==2){//means that the square which is going to be captured by my piece(s) is a wall
                        if(moves.get(i)!=1){//means that the ith combination of pieces is not only one ==> that can't be a single capstone ==> this move is invalid
                            return null;
                        }
                        else{//means that the ith combination of pieces is only one
                            if(entry.get(removal_index).kind == 3){//means that the piece is capstone; a valid move always FOR kind =2
                                ArrayList<BoardUnit> moving_unit = new ArrayList<BoardUnit>(entry.subList(removal_index,removal_index+moves.get(i)));
                                for(int j=removal_index;j<removal_index+moves.get(i);j++){
                                    entry.remove(removal_index);
                                }
                                BoardUnit removed_piece = captured.remove(captured.size()-1);
                                removed_piece.setKind(1);
                                captured.add(removed_piece);
                                captured.addAll(moving_unit);
                            }
                            else{//means that piece is not a capstone ==> invalid move
                                return null;
                            }
                        }
                    }
                    else if(kind==3){//means that the square which is going to be captured bymy piece(s) is a capstone
                        return null;
                    }
                }
            }
        }
        return result;
    }
    
    int[] getEvaluation(){
        int[] result = new int[2];
        result[0]=(this.getStackHeight()[0]+this.getOwnedSquares()[0]+this.getMaxChainLength()[0]);
        result[1]=(this.getStackHeight()[1]+this.getOwnedSquares()[1]+this.getMaxChainLength()[1]);
        return result;
    }
    
    public static void main(String args[]) throws IOException{
        
        InputStreamReader isr = new InputStreamReader(System.in);
        Scanner sc = new Scanner(isr);
        int player_me = sc.nextInt();
        int player_opp = (player_me==1)?2:1;
        int size = sc.nextInt();
        int time = sc.nextInt();
        //sc.close();
        System.out.println("player_me = "+player_me+", player_opp = "+player_opp+", size = "+size+", time = "+time);
        BoardState bs = new BoardState(player_me,player_opp,size,time);
        BufferedReader br = new BufferedReader(isr);
        String read;
        boolean finish_game = false;
        int player_switch = 0;//if its 0 its then opps chance else if its 1 then my cahnce
        while(true){
            if(finish_game){
                break;
            }
            read = br.readLine();
            char first_char = read.charAt(0);
            if(first_char=='F' || first_char=='S' || first_char=='C'){//means that opponent has played a "place a stone"
                int position = ((int)(read.charAt(1))-97)*size + Integer.parseInt(read.substring(2));//position of that piece on board
                System.out.println((int)read.charAt(1)+","+Integer.parseInt(read.substring(2))+","+position);
                BoardUnit bu = new BoardUnit((player_switch==1)?player_me:player_opp,(first_char=='F')?1:((first_char=='S')?2:3));
                player_switch=(player_switch==0)?(1):(0);
                bs.addPiece(position, bu);
                bs.printBoard();
                //System.out.println("aaaaaaaaaaaaaaaaaaaaaaa");
                //bs.afterPlacing(1,2,'d',1).printBoard(); 
                System.out.println("owned:owed square ==> "+bs.getOwnedSquares()[0]+","+bs.getOwnedSquares()[1]);
                System.out.println("owned:owed stack height ==> "+bs.getStackHeight()[0]+","+bs.getStackHeight()[1]);
                System.out.println("owned:owed longest chain length ==> "+bs.getMaxChainLength()[0]+","+bs.getMaxChainLength()[1]);
                System.out.println("evaluation = "+bs.afterPlacing(1,2,'d',1).getEvaluation()[0]+","+bs.afterPlacing(1,2,'d',1).getEvaluation()[1]);
                
            }
            else if(first_char=='M' ){
                //to use this section, type in the console : M<playerid><dirn><posn_alphabet><posn_numeric><Arraylist of moves> ... don't use any space
                //moveStack(int player,char dirn, char posn_alphabet, int posn_numeric, ArrayList<Integer> moves)
                ArrayList<Integer> tmp = new ArrayList<Integer>();
                for(int i =5;i<read.length();i++){
                    tmp.add(Integer.parseInt(read.substring(i,i+1)));
                }
                BoardState bs1 = bs.moveStack(Integer.parseInt(read.substring(1,2)),read.charAt(2),read.charAt(3),Integer.parseInt(read.substring(4,5)),tmp);
                bs1.printBoard();
                System.out.println("kvbdscbsj");
                bs.printBoard();
            }
            else{//means that opp has played "move a stack", assuming the correct format of input here
                int pieces_to_move = Integer.parseInt(read.substring(0,1));
                int posn_alphabet = (int)((read.charAt(1)))-96;
                int posn_numeric = Integer.parseInt(read.substring(2,3));
                int position = (posn_alphabet-1)*size + posn_numeric;//position of that piece on board
                char direction = (read.charAt(3));
                String sequence = read.substring(4);
                int seq_len = sequence.length();
                int seq_arr[] = new int[seq_len];
                for(int i = 0; i < seq_len; i++){
                    seq_arr[i]=Integer.parseInt(sequence.substring(i,i+1));
                }
                if(direction == '-'){
                    int i =0;
                    int next_pos=position-1;
                    int cur_pos_orig_size = bs.board.get(position).size();
                    for(int cnt = 0; cnt<pieces_to_move; cnt++){
                        BoardUnit removed_piece = bs.removePiece(position,cur_pos_orig_size-pieces_to_move);
                        if(removed_piece.kind == 3 && bs.board.get(next_pos).size()-1 >=0){
                            BoardUnit removed_piece2 = bs.removePiece(next_pos, (bs.board.get(next_pos).size()-1));
                            bs.addPiece(next_pos, new BoardUnit(removed_piece2.color,1));
                            //System.out.println(removed_piece2.kind);
                        }
                        bs.addPiece(next_pos,removed_piece );
                        seq_arr[i]--;
                        if(seq_arr[i]==0){
                            i++;
                            next_pos--;
                        }
                    }
                }
                else if(direction == '+'){
                    int i =0;
                    int next_pos=position+1;
                    int cur_pos_orig_size = bs.board.get(position).size();
                    for(int cnt = 0; cnt<pieces_to_move; cnt++){
                        BoardUnit removed_piece = bs.removePiece(position,cur_pos_orig_size-pieces_to_move);
                        if(removed_piece.kind == 3 && bs.board.get(next_pos).size()-1 >=0){
                            BoardUnit removed_piece2 = bs.removePiece(next_pos, (bs.board.get(next_pos).size()-1));
                            bs.addPiece(next_pos, new BoardUnit(removed_piece2.color,1));
                            //System.out.println(removed_piece2.kind);
                        }
                        bs.addPiece(next_pos,removed_piece );
                        seq_arr[i]--;
                        if(seq_arr[i]==0){
                            i++;
                            next_pos++;
                        }
                    }
                }
                else if(direction == '<'){
                    int i =0;
                    int next_pos=position-size;
                    int cur_pos_orig_size = bs.board.get(position).size();
                    for(int cnt = 0; cnt<pieces_to_move; cnt++){
                        BoardUnit removed_piece = bs.removePiece(position,cur_pos_orig_size-pieces_to_move);
                        if(removed_piece.kind == 3 && bs.board.get(next_pos).size()-1 >=0){
                            BoardUnit removed_piece2 = bs.removePiece(next_pos, (bs.board.get(next_pos).size()-1));
                            bs.addPiece(next_pos, new BoardUnit(removed_piece2.color,1));
                            //System.out.println(removed_piece2.kind);
                        }
                        bs.addPiece(next_pos,removed_piece );
                        seq_arr[i]--;
                        if(seq_arr[i]==0){
                            i++;
                            next_pos-=size;
                        }
                    }
                }
                else if(direction == '>'){
                    int i =0;
                    int next_pos=position+size;
                    int cur_pos_orig_size = bs.board.get(position).size();
                    for(int cnt = 0; cnt<pieces_to_move; cnt++){
                        BoardUnit removed_piece = bs.removePiece(position,cur_pos_orig_size-pieces_to_move);
                        if(removed_piece.kind == 3 && bs.board.get(next_pos).size()-1 >=0){
                            BoardUnit removed_piece2 = bs.removePiece(next_pos, (bs.board.get(next_pos).size()-1));
                            bs.addPiece(next_pos, new BoardUnit(removed_piece2.color,1));
                            //System.out.println(removed_piece2.kind);
                        }
                        bs.addPiece(next_pos,removed_piece );
                        seq_arr[i]--;
                        if(seq_arr[i]==0){
                            i++;
                            next_pos+=size;
                        }
                    }
                }
                bs.printBoard();
                System.out.println(bs.getOwnedSquares());
                //System.out.println(pieces_to_move+","+posn_alphabet+","+posn_numeric+","+direction+","+seq_arr);
                //finish_game = true;
            }
        }
    }
}
