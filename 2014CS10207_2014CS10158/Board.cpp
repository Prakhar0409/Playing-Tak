#include <bits/stdc++.h>
#include "Board.h"

using namespace std;

Board::Board(){

}

Board::Board(int bsize,int player_me){
    debug = false;
	
    size = bsize;	
    player_color[0] = player_me;
    player_color[1] = 3-player_me;
    if(player_me == 1){
        color_to_player[0] = 0;     //0->white to player 0
        color_to_player[1] = 1;      //1->black to player 1
    }else{
        color_to_player[0] = 1;     
        color_to_player[1] = 0;  
    }

    num_players = 2;
    for (int i = 0; i < num_players; ++i){
        
        if(bsize == 3){
            p_flats[i] = 8;
            p_caps[i] = 1;
        }else if(bsize == 4){
            p_flats[i] = 15;
            p_caps[i] = 1;
        }else if(bsize == 5){
            p_flats[i] = 21;
            p_caps[i] = 1;
        }else if(bsize == 6){
            p_flats[i] = 30;
            p_caps[i] = 1;
        }else if(bsize == 7){
            p_flats[i] = 40;
            p_caps[i] = 1;
        }
        init_flats = p_flats[0];
    }
    
}

void Board::addPiece(int pos, BoardUnit bu){
	b[pos].push_back(bu);
}

void Board::printBoard(){
    if(debug){
        int board_size = size*size;
        for(int i =0; i<size; i++){
            string res = "";
            cerr << ((char) (i + 97));
            // cerr<<"RES: "<<res<<"  ||  c: "<<(char)(i+97)<<endl;
            for(int j = 0; j < size; j++){
                int pos = i*size+j;
                string entry_set = "[";
                for(int k=0; k<b[pos].size(); k++){
                    entry_set = entry_set + "("+ to_string(b[pos][k].color) + ","+ to_string(b[pos][k].kind)+"),";
                }
                entry_set = entry_set + "]";
                res = res + to_string(j+1)+" : "+ entry_set+";\t";
            }
            cerr<< res << endl;
        }
    }
}

Board::~Board(){

}