
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
    //int player;
    int size;
    int time;//in sec
    HashMap<Integer, ArrayList<BoardUnit>> board = new HashMap<Integer, ArrayList<BoardUnit>>();
    BoardState(int size, int time){
        //this.player = player;
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
    public static void main(String args[]) throws IOException{
        InputStreamReader isr = new InputStreamReader(System.in);
        Scanner sc = new Scanner(isr);
        int player_me = sc.nextInt();
        int player_opp = (player_me==1)?2:1;
        int size = sc.nextInt();
        int time = sc.nextInt();
        //sc.close();
        System.out.println("player_me = "+player_me+", player_opp = "+player_opp+", size = "+size+", time = "+time);
        BoardState bs = new BoardState(size,time);
        BufferedReader br = new BufferedReader(isr);
        String read;
        boolean finish_game = false;
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
                //System.out.println(pieces_to_move+","+posn_alphabet+","+posn_numeric+","+direction+","+seq_arr);
                //finish_game = true;
            }
        }
    }
}
