
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
        /*ArrayList<Integer> a1 = new ArrayList<Integer>();
        a1.add(1);
        a1.add(2);
        a1.add(3);
        ArrayList<Integer> a2 = new ArrayList<Integer>(a1);
        ArrayList<Integer> a3 = a1;
        System.out.println(a2);
        System.out.println(a3);
        a1.add(4);
        System.out.println(a2);
        System.out.println(a3);
        HashMap<Integer,Integer> h1 = new HashMap<Integer,Integer>();
        h1.put(0,0);
        h1.put(1,1);
        System.out.println(h1);
        HashMap<Integer,Integer> h2 = new HashMap<Integer,Integer>(h1);
        HashMap<Integer,Integer> h3 = h1;
        h1.put(2,2);
        System.out.println(h2);
        System.out.println(h3);*/
        while(true){
            if(finish_game){
                break;
            }
            read = br.readLine();
            char first_char = read.charAt(0);
            if(first_char=='F' || first_char=='S' || first_char=='C'){//means that opponent has played a "place a stone"
                int position = ((int)(read.charAt(1))-97)*size + Integer.parseInt(read.substring(2));//position of that piece on board
                System.out.println((int)read.charAt(1)+","+Integer.parseInt(read.substring(2))+","+position);
                BoardUnit bu = new BoardUnit(player_opp,(first_char=='F')?1:((first_char=='S')?2:3));
                bs.addPiece(position, bu);
                bs.printBoard();
                //System.out.println("aaaaaaaaaaaaaaaaaaaaaaa");
                //bs.afterPlacing(1,2,'d',1).printBoard(); 
                System.out.println("owned:owed square ==> "+bs.getOwnedSquares()[0]+","+bs.getOwnedSquares()[1]);
                System.out.println("owned:owed stack height ==> "+bs.getStackHeight()[0]+","+bs.getStackHeight()[1]);
                System.out.println("owned:owed longest chain length ==> "+bs.getMaxChainLength()[0]+","+bs.getMaxChainLength()[1]);
                System.out.println("evaluation = "+bs.afterPlacing(1,2,'d',1).getEvaluation()[0]+","+bs.afterPlacing(1,2,'d',1).getEvaluation()[1]);
                
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
